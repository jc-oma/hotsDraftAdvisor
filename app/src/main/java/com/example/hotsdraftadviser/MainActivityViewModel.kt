package com.example.hotsdraftadviser

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.IOException

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _champData = MutableStateFlow<List<ChampData>>(emptyList())
    private val _remainingChamps = MutableStateFlow<List<ChampData>>(emptyList())
    private val _filterMapsString = MutableStateFlow<String>("")
    private val _filterChampString = MutableStateFlow<String>("")
    private val _choosenMap = MutableStateFlow<String>("")
    private val _sortState = MutableStateFlow<SortState>(SortState.OWNPOINTS)

    private val _picksOwnTeam = MutableStateFlow<List<ChampData>>(emptyList())
    private val _picksTheirTeam = MutableStateFlow<List<ChampData>>(emptyList())
    private val _bansOwnTeam = MutableStateFlow<List<ChampData>>(emptyList())
    private val _bansTheirTeam = MutableStateFlow<List<ChampData>>(emptyList())

    private val _roleFilter = MutableStateFlow<List<RoleEnum>>(emptyList())

    private val _theirPickScore = MutableStateFlow<Int>(0)
    private val _ownPickScore = MutableStateFlow<Int>(0)

    private val maxPicks = 5

    //MAPS
    val allChamps: StateFlow<List<ChampData>> = _champData.asStateFlow()
    val mapList: StateFlow<List<String>> = getSortedUniqueMaps(allChamps)
    val filterMapsString: StateFlow<String> = _filterMapsString.asStateFlow()
    val filteredMaps: StateFlow<List<String>> = filterMapsByString(mapList, filterMapsString)

    val remainingChamps: StateFlow<List<ChampData>> = _remainingChamps.asStateFlow()
    val filterOwnChampString: StateFlow<String> = _filterChampString.asStateFlow()
    val sortState: StateFlow<SortState> = _sortState.asStateFlow()

    val champsWithCalculatedScores: StateFlow<List<ChampData>> =
        dataFlowForChampListWithScores(
            champDataFlow = remainingChamps,
            searchMapNameFlow = _choosenMap,
            sortedByScoreOwn = _sortState,
            filterString = _filterChampString,
            ownPicks = _picksOwnTeam,
            theirPicks = _picksTheirTeam
        )

    val choosenMap: StateFlow<String> = _choosenMap
    val picksOwnTeam: StateFlow<List<ChampData>> = _picksOwnTeam
    val picksTheirTeam: StateFlow<List<ChampData>> = _picksTheirTeam
    val bansOwnTeam: StateFlow<List<ChampData>> = _bansOwnTeam
    val bansTheirTeam: StateFlow<List<ChampData>> = _bansTheirTeam

    val ownPickScore: StateFlow<Int> = getOwnTeamAgregatedPointsAsStateFlow()
    val theirPickScore: StateFlow<Int> = getTheirTeamAgregatedPoints()

    val roleFilter: StateFlow<List<RoleEnum>> = _roleFilter

    fun setSortState(sortState: SortState) {
        viewModelScope.launch {
            _sortState.value = sortState
        }
    }

    fun setChosenMapByIndex(index: Int) {
        viewModelScope.launch {
            val currentMapList = mapList.first()

            if (index >= 0 && index < currentMapList.size) {
                _choosenMap.value = currentMapList[index]
            } else {
                _choosenMap.value = ""
                println("Warnung: Ungültiger Index ($index) für mapList. choosenMap wurde auf einen leeren String gesetzt.")
            }
        }
    }

    fun setPickedOwnTeam(index: Int) {
        if (_picksOwnTeam.value.size < maxPicks) {
            viewModelScope.launch {
                val currentChampList = champsWithCalculatedScores.first()
                val champs = _picksOwnTeam.value
                val pickedChamp = currentChampList[index].copy(isPicked = true, pickedBy = TeamSide.OWN)
                _picksOwnTeam.value = champs + pickedChamp
                updateChampDataWithPickStatus(pickedChamp, true)
            }
        }
    }


    fun setPickedTheirTeam(index: Int) {
        if (_picksTheirTeam.value.size < maxPicks) {
            viewModelScope.launch {
                val currentChampList = champsWithCalculatedScores.first()
                val champs = _picksTheirTeam.value
                val pickedChamp = currentChampList[index].copy(isPicked = true, pickedBy = TeamSide.THEIR)
                _picksTheirTeam.value = champs + pickedChamp
                updateChampDataWithPickStatus(pickedChamp, true)
            }
        }
    }

    fun setBansOwnTeam(i: Int) {
        viewModelScope.launch {
            val currentChampList = champsWithCalculatedScores.first()
            val champs = _bansOwnTeam.value
            val bannedChamp = currentChampList[i].copy(isPicked = true)
            _bansOwnTeam.value = champs + bannedChamp
            updateChampDataWithPickStatus(bannedChamp, true)
        }
    }

    fun setBansTheirTeam(i: Int) {
        viewModelScope.launch {
            val currentChampList = champsWithCalculatedScores.first()
            val champs = _bansTheirTeam.value
            val bannedChamp = currentChampList[i].copy(isPicked = true)
            _bansTheirTeam.value = champs + bannedChamp
            updateChampDataWithPickStatus(bannedChamp, true)
        }
    }

    fun removePick(index: Int, teamSide: TeamSide) {
        when (teamSide) {
            TeamSide.OWN -> {
                val currentPicks = _picksOwnTeam.value.toMutableList()
                if (index >= 0 && index < currentPicks.size) {
                    val removedChamp = currentPicks.removeAt(index)
                    _picksOwnTeam.value = currentPicks.toList()
                    updateChampDataWithPickStatus(removedChamp, false)
                } else {
                    println("Error: Invalid index $index for removing pick from own team.")
                }
            }

            TeamSide.THEIR -> {
                val currentPicks = _picksTheirTeam.value.toMutableList()
                if (index >= 0 && index < currentPicks.size) {
                    val removedChamp = currentPicks.removeAt(index)
                    _picksTheirTeam.value = currentPicks.toList()
                    updateChampDataWithPickStatus(removedChamp, false)
                } else {
                    println("Error: Invalid index $index for removing pick from their team.")
                }
            }

            TeamSide.NONE -> {
                println("Error: Invalid Team side.")
            }
        }
    }

    private fun updateChampDataWithPickStatus(champ: ChampData, isPicked: Boolean) {
        val currentChampData = _champData.value.toMutableList()
        val indexInAllChamps = currentChampData.indexOfFirst { it.ChampName == champ.ChampName }
        if (indexInAllChamps != -1) {
            currentChampData[indexInAllChamps] = currentChampData[indexInAllChamps].copy(isPicked = isPicked)
            _champData.value = currentChampData.toList()
        }
    }

    private fun dataFlowForChampListWithScores(
        champDataFlow: StateFlow<List<ChampData>>,
        searchMapNameFlow: StateFlow<String>,
        sortedByScoreOwn: MutableStateFlow<SortState>,
        filterString: MutableStateFlow<String>,
        ownPicks: MutableStateFlow<List<ChampData>>,
        theirPicks: MutableStateFlow<List<ChampData>>
    ): StateFlow<List<ChampData>> {

        var copy = calculateChampsPerPicks(_champData, ownPicks, theirPicks)
        copy = filterChampsByRole(copy)

        val list = combine(
            copy,
            searchMapNameFlow,
            sortedByScoreOwn,
            filterString
        ) { champs, mapSearchString, doSortByOwn, filter ->
            val availableChamps = champs

            val filteredByNameChamps = if (filter.isBlank()) {
                availableChamps
            } else {
                availableChamps.filter { champ ->
                    champ.ChampName.contains(filter, ignoreCase = true) && !champ.isPicked
                }
            }

            val lowerCaseSearchString = mapSearchString.lowercase()

            val calculatedChamps = filteredByNameChamps.map { champ ->
                val updatedChamp = champ.copy()

                if (!mapSearchString.isEmpty()) {
                    champ.MapScore.forEach { mapScore ->
                        if (mapScore.MapName.lowercase().contains(lowerCaseSearchString)) {
                            updatedChamp.ScoreOwn += mapScore.ScoreValue
                            updatedChamp.ScoreTheir += mapScore.ScoreValue
                        }
                    }
                }
                updatedChamp
            }

            if (doSortByOwn == SortState.OWNPOINTS) {
                calculatedChamps.sortedByDescending { it.ScoreOwn } // Höchster ScoreOwn zuerst
            } else if (doSortByOwn == SortState.THEIRPOINTS) {
                calculatedChamps.sortedByDescending { it.ScoreTheir } // Höchster ScoreTheir zuerst
            } else {
                calculatedChamps.sortedBy { it.ChampName }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

        return list
    }

    private fun filterChampsByRole(flow: StateFlow<List<ChampData>>): StateFlow<List<ChampData>> {
        return combine(flow, _roleFilter) { champs, selectedRoles ->
            if (selectedRoles.isEmpty()) {
                champs
            } else {
                champs.filter { champ ->
                    selectedRoles.any { selectedRole ->
                        champ.ChampRoleAlt.equals(selectedRole.name, ignoreCase = true)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    private fun filterChampsfromList(
        list: StateFlow<List<ChampData>>,
        pick1: MutableStateFlow<List<ChampData>>,
        pick2: MutableStateFlow<List<ChampData>>
    ): StateFlow<List<ChampData>> {
        return combine(list, pick1, pick2) { allChamps, currentOwnPicks, currentTheirPicks ->
            // Diese Funktion wird nicht mehr zum direkten Entfernen verwendet, sondern setzt isPicked
            val allPickedChampNames = (currentOwnPicks.map { it.ChampName } +
                    currentTheirPicks.map { it.ChampName }).toSet()

            allChamps.filter { champ -> !allPickedChampNames.contains(champ.ChampName) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    private fun calculateChampsPerPicks(
        champs: StateFlow<List<ChampData>>,
        ownPicks: MutableStateFlow<List<ChampData>>,
        theirPicks: MutableStateFlow<List<ChampData>>
    ): StateFlow<List<ChampData>> {
        return combine(
            champs,
            ownPicks,
            theirPicks
        ) { allChamps, currentOwnPicks, currentTheirPicks ->
            val ownPickNames = currentOwnPicks.map { it.ChampName }
            val theirPickNames = currentTheirPicks.map { it.ChampName }
            val allPickedNames = ownPickNames + theirPickNames // Champs, die bereits gewählt wurden

            allChamps.map { champ ->
                if (allPickedNames.contains(champ.ChampName)) {
                    return@map champ
                }

                var currentScoreOwn = champ.ScoreOwn
                var currentScoreTheir = champ.ScoreTheir

                champ.StrongAgainst.forEach { strongAgainstEntry ->
                    if (theirPickNames.contains(strongAgainstEntry.ChampName)) {
                        currentScoreOwn += strongAgainstEntry.ScoreValue
                    }

                    if (ownPickNames.contains(strongAgainstEntry.ChampName)) {
                        currentScoreTheir += strongAgainstEntry.ScoreValue
                    }
                }

                //TODO überprüfen ob das passt - eventuell doppelung mit dadrüber
                champ.WeakAgainst.forEach { weakAgainstEntry ->
                    if (theirPickNames.contains(weakAgainstEntry.ChampName)) {
                        currentScoreOwn -= weakAgainstEntry.ScoreValue
                    }
                    if (ownPickNames.contains(weakAgainstEntry.ChampName)) {
                        currentScoreTheir -= weakAgainstEntry.ScoreValue
                    }
                }

                champ.GoodTeamWith.forEach { goodTeamEntry ->
                    if (ownPickNames.contains(goodTeamEntry.ChampName)) {
                        currentScoreOwn += goodTeamEntry.ScoreValue
                    }
                    if (theirPickNames.contains(goodTeamEntry.ChampName)) {
                        currentScoreTheir += goodTeamEntry.ScoreValue
                    }
                }

                champ.copy(ScoreOwn = currentScoreOwn, ScoreTheir = currentScoreTheir)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    init {
        loadJson()
    }

    fun updateMapsSearchQuery(query: String) {
        _filterMapsString.value = query
    }

    fun updateOwnChampSearchQuery(query: String) {
        _filterChampString.value = query
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSortedUniqueMaps(champFlow: StateFlow<List<ChampData>>): StateFlow<List<String>> {
        val list = champFlow.map { champs ->
            champs.map { champ ->
                champ.MapScore.map { map ->
                    map.MapName
                }.distinct()
                    .sorted()
            }
        }
            .map { nestedList ->
                nestedList.flatten()
                    .distinct()
            }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )
        return list
    }

    private fun filterMapsByString(
        maps: StateFlow<List<String>>,
        searchString: StateFlow<String>
    ): StateFlow<List<String>> {
        return combine(maps, searchString) { currentMaps, currentSearchString ->
            val lowerCaseSearchString = currentSearchString.lowercase()

            currentMaps.filter { item ->
                item.lowercase().contains(lowerCaseSearchString)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadJson() {
        var champData: List<ChampData> = listOf()
        viewModelScope.launch {
            val jsonString = try {
                application.assets.open("output.json")
            } catch (ioException: IOException) {
                Log.e(
                    "JsonAssetViewModel",
                    "Fehler beim Lesen der Asset-Datei 'outputjson': ${ioException.message}"
                )
                null
            }

            if (jsonString != null) {
                try {
                    champData = Json.decodeFromStream<List<ChampData>>(jsonString)
                    Log.d("TAG", "ChampData erfolgreich gemappt: ${champData}")

                    _champData.value = champData
                    _remainingChamps.value = champData
                } catch (e: Exception) {
                    Log.e("TAG", "Fehler beim Mappen der ChampData JSON-Daten: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.e("TAG", "JSON-String konnte nicht aus Assets geladen werden.")
            }
        }
    }

    fun clearChoosenMap() {
        _choosenMap.value = ""
    }

    fun setRoleFilter(role: RoleEnum) {
        if (_roleFilter.value.contains(role)) {
            _roleFilter.value = _roleFilter.value.filter { it -> it != role }
        } else {
            _roleFilter.value = _roleFilter.value + role
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            _filterMapsString.value = ""
            _filterChampString.value = ""
            _choosenMap.value = ""
            _sortState.value = SortState.OWNPOINTS
            _picksOwnTeam.value = emptyList()
            _picksTheirTeam.value = emptyList()
            _bansOwnTeam.value = emptyList()
            _bansTheirTeam.value = emptyList()
            _roleFilter.value = emptyList()
            _champData.value = _champData.value.map { it.copy(isPicked = false) } // Setze isPicked für alle Champs zurück
            _remainingChamps.value = _champData.value // Setze remainingChamps auf alle Champs
        }

    }

    private fun getOwnTeamAgregatedPointsAsStateFlow(): StateFlow<Int> {
        return combine(picksOwnTeam, champsWithCalculatedScores) { ownPicks, champsWithScores ->
            var totalScore = 0
            ownPicks.forEach { pickedChamp ->
                champsWithScores.find { it.ChampName == pickedChamp.ChampName }?.let { matchedChamp ->
                    totalScore += matchedChamp.ScoreOwn
                }
            }
            Log.d("ViewModel", "Aggregated Own Team Score: $totalScore")
            totalScore
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    }

    fun getTheirTeamAgregatedPoints(): StateFlow<Int> {
        return combine(picksTheirTeam, champsWithCalculatedScores) { theirPicks, champsWithScores ->
            var totalScore = 0
            theirPicks.forEach { pickedChamp ->
                champsWithScores.find { it.ChampName == pickedChamp.ChampName }?.let { matchedChamp ->
                    totalScore += matchedChamp.ScoreOwn
                }
            }
            Log.d("ViewModel", "Aggregated Their Team Score: $totalScore")
            totalScore
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    }
}

class MainActivityViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(application) as T
        }
        throw IllegalArgumentException("Unbekannte ViewModel-Klasse")
    }
}

enum class TeamSide {
    OWN, THEIR, NONE
}

enum class SortState {
    OWNPOINTS, THEIRPOINTS, CHAMPNAME
}