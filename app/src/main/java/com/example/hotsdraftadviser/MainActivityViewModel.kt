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

    private val _allChampsData = MutableStateFlow<List<ChampData>>(emptyList())
    private val _filterMapsString = MutableStateFlow<String>("")
    private val _filterChampString = MutableStateFlow<String>("")
    private val _choosenMap = MutableStateFlow<String>("")
    private val _sortState = MutableStateFlow<SortState>(SortState.OWNPOINTS)

    private val _roleFilter = MutableStateFlow<List<RoleEnum>>(emptyList())

    private val maxPicks = 5
    private val allChamps: StateFlow<List<ChampData>> = _allChampsData.asStateFlow()


    val mapList: StateFlow<List<String>> = getSortedUniqueMaps()
    val filterMapsString: StateFlow<String> = _filterMapsString.asStateFlow()
    val filteredMaps: StateFlow<List<String>> = filterMapsByString(mapList, filterMapsString)

    val filterOwnChampString: StateFlow<String> = _filterChampString.asStateFlow()
    val sortState: StateFlow<SortState> = _sortState.asStateFlow()

    val champsWithCalculatedScores: StateFlow<List<ChampData>> =
        dataFlowForChampListWithScores(
            searchMapNameFlow = _choosenMap,
            sortedByScoreOwn = _sortState,
            filterString = _filterChampString
        )

    val choosenMap: StateFlow<String> = _choosenMap

    val ownPickScore: StateFlow<Int> = getTeamAgregatedPoints(TeamSide.OWN)
    val theirPickScore: StateFlow<Int> = getTeamAgregatedPoints(TeamSide.THEIR)

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

    fun pickChampForTeam(index: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentChampList = champsWithCalculatedScores.first()
            val alreadyPicked = currentChampList.filter { it.isPicked && it.pickedBy == teamSide }

            if (alreadyPicked.size < maxPicks) {
                val pickedChamp =
                    currentChampList.find { it.ChampName == currentChampList[index].ChampName }
                        ?.copy(isPicked = true, pickedBy = teamSide)
                        ?: return@launch // Frühzeitiger Ausstieg, falls der Champ nicht gefunden wird
                _allChampsData.value =
                    _allChampsData.value.map { if (it.ChampName == pickedChamp.ChampName) pickedChamp else it }
            }
        }
    }

    //TODO: Implement bans Teamside
    fun setBansPerTeam(i: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentChampList = champsWithCalculatedScores.first()
            val bannedChamp = currentChampList[i].copy(isPicked = true)
            updateChampDataWithPickStatus(bannedChamp, true, teamSide)
        }
    }

    fun removePick(index: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentChampList = _allChampsData.value
            val teamPicks = currentChampList.filter { it.isPicked && it.pickedBy == teamSide }

            if (index >= 0 && index < teamPicks.size) {
                val champToRemove = teamPicks[index]
                val updatedChamp = champToRemove.copy(isPicked = false, pickedBy = TeamSide.NONE)

                _allChampsData.value =
                    _allChampsData.value.map { if (it.ChampName == updatedChamp.ChampName) updatedChamp else it }
            } else {
                Log.w("ViewModel", "Ungültiger Index zum Entfernen des Picks: $index für Team: $teamSide")
            }
        }
    }

    private fun updateChampDataWithPickStatus(
        champ: ChampData,
        isPicked: Boolean,
        teamSide: TeamSide
    ) {
        val currentChampData = _allChampsData.value.toMutableList()
        val indexInAllChamps = currentChampData.indexOfFirst { it.ChampName == champ.ChampName }
        if (indexInAllChamps != -1) {
            currentChampData[indexInAllChamps] =
                currentChampData[indexInAllChamps].copy(isPicked = isPicked)
            _allChampsData.value = currentChampData.toList()
        }
    }

    private fun dataFlowForChampListWithScores(
        searchMapNameFlow: StateFlow<String>,
        sortedByScoreOwn: MutableStateFlow<SortState>,
        filterString: MutableStateFlow<String>
    ): StateFlow<List<ChampData>> {
        var copy = calculateChampsPerPicks()
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
    ): StateFlow<List<ChampData>> {
        val calculatedChampsPerPick = combine(
            _allChampsData
        ) { allChamps ->
            val ownPickNames =
                allChamps.first().filter { it.pickedBy == TeamSide.OWN }.map { it.ChampName }
            val theirPickNames =
                allChamps.first().filter { it.pickedBy == TeamSide.THEIR }.map { it.ChampName }
            val allPickedNames = ownPickNames + theirPickNames

            allChamps.first().map { champ ->
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
        return calculatedChampsPerPick
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
    fun getSortedUniqueMaps(): StateFlow<List<String>> {
        val list = allChamps.map { champs ->
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

                    _allChampsData.value = champData
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
            _roleFilter.value = emptyList()
            _allChampsData.value = _allChampsData.value.map {
                it.copy(
                    isPicked = false,
                    pickedBy = TeamSide.NONE
                )
            } // Setze isPicked für alle Champs zurück
        }

    }

    private fun getTeamAgregatedPoints(teamSide: TeamSide): StateFlow<Int> {
        return combine(champsWithCalculatedScores) { champsWithScores ->
            var totalScore = 0
            val pickedChamp = champsWithScores.first().filter { it -> it.pickedBy == teamSide }
            pickedChamp.forEach { pickedChamp ->
                champsWithScores.first().find { it.ChampName == pickedChamp.ChampName }
                    ?.let { matchedChamp ->
                        totalScore += matchedChamp.ScoreOwn
                    }
            }
            Log.d("ViewModel", "Aggregated Own Team Score: $totalScore")
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
    OWN, THEIR, NONE, BANNEDOWN, BANNEDTHEIR
}

enum class SortState {
    OWNPOINTS, THEIRPOINTS, CHAMPNAME
}