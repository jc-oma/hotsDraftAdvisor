package com.example.hotsdraftadviser

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.hotsdraftadviser.database.AppDatabase
import com.example.hotsdraftadviser.database.StreamingSettingsRepository
import com.example.hotsdraftadviser.dataclsasses.ChampData
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
    private val repository: StreamingSettingsRepository by lazy {
        Log.d("ViewModelInit", "Initializing repository...") // Deine Logs sind hier hilfreich!
        try {
            Log.d("ViewModelInit", "Getting database instance...")
            val db = AppDatabase.getDatabase(application)
            Log.d("ViewModelInit", "Database instance obtained: $db")
            Log.d("ViewModelInit", "Getting DAO...")
            val dao = db.streamingSettingDao() // <<--- MÖGLICHER ABSTURZPUNKT HIER
            Log.d("ViewModelInit", "DAO obtained: $dao")
            val repoInstance = StreamingSettingsRepository(dao)
            Log.d("ViewModelInit", "Repository instance created: $repoInstance")
            repoInstance
        } catch (e: Exception) {
            Log.e("ViewModelInit", "Error initializing repository", e)
            throw e // Diese Exception wird den lazy-Block abbrechen
        }
    }


    // Dein isStreamingEnabled als StateFlow, das von der Datenbank gespeist wird
    val isStreamingEnabled: StateFlow<Boolean> = repository.isStreamingEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    init {
        // Logge den initialen Wert oder wenn er sich ändert
        viewModelScope.launch {
            isStreamingEnabled.collect { enabled ->
                Log.d("VideoStreamViewModel", "isStreamingEnabled from DB (Flow): $enabled")
            }
        }
    }

    private val _isStreamingEnabled = MutableStateFlow(true)

    private val _allChampsData = MutableStateFlow<List<ChampData>>(emptyList())

    private val _filterMapsString = MutableStateFlow<String>("")
    private val _filterChampString = MutableStateFlow<String>("")
    private val _choosenMap = MutableStateFlow<String>("")
    private val _sortState = MutableStateFlow<SortState>(SortState.OWNPOINTS)
    private val _roleFilter = MutableStateFlow<List<RoleEnum>>(emptyList())

    private val maxPicks = 5

    val allChampsData = _allChampsData.asStateFlow()
    val mapList: StateFlow<List<String>> = getSortedUniqueMaps()
    val filterMapsString: StateFlow<String> = _filterMapsString.asStateFlow()
    val filteredMaps: StateFlow<List<String>> = filterMapsByString(mapList, filterMapsString)

    val filterOwnChampString: StateFlow<String> = _filterChampString.asStateFlow()
    val sortState: StateFlow<SortState> = _sortState.asStateFlow()

    val pickedTheirTeamChamps: StateFlow<List<ChampData>> = getPickedTheirTeamChamps(TeamSide.THEIR)
    val pickedOwnTeamChamps: StateFlow<List<ChampData>> = getPickedTheirTeamChamps(TeamSide.OWN)

    private fun getPickedTheirTeamChamps(team: TeamSide): StateFlow<List<ChampData>> =
        _allChampsData.map { champs -> champs.filter { it.pickedBy == team } }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val unfilteredChosableChampList: StateFlow<List<ChampData>> = dataFlowForChampListWithScores(false)
    val chosableChampList: StateFlow<List<ChampData>> = dataFlowForChampListWithScores(true)

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
            val currentChampList = chosableChampList.first()
            val alreadyPicked = currentChampList.filter { it.isPicked && it.pickedBy == teamSide }

            if (alreadyPicked.size < maxPicks) {
                if (!(alreadyPicked.size > maxPicks - 1 && currentChampList[index].ChampName == "Chogall")) {
                    val pickedChamp =
                        currentChampList.find { it.ChampName == currentChampList[index].ChampName }
                            ?.copy(isPicked = true, pickedBy = teamSide)
                            ?: return@launch // Frühzeitiger Ausstieg, falls der Champ nicht gefunden wird
                    _allChampsData.value =
                        _allChampsData.value.map { if (it.ChampName == pickedChamp.ChampName) pickedChamp else it }
                }
            }
        }
    }

    //TODO: Implement bans Teamside
    fun setBansPerTeam(i: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentChampList = chosableChampList.first()
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
                Log.w(
                    "ViewModel",
                    "Ungültiger Index zum Entfernen des Picks: $index für Team: $teamSide"
                )
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

    private fun dataFlowForChampListWithScores(isFiltered: Boolean): StateFlow<List<ChampData>> {
        var copy = calculateChampsPerPicks()
        var list = combine(
            copy,
            _choosenMap,
            _sortState,
            _filterChampString
        ) { champs, mapSearchString, doSortByOwn, filter ->


            val filteredByNameChamps = if (filter.isBlank()) {
                champs
            } else {
                if (isFiltered) {
                    champs.filter { champ ->
                        champ.ChampName.contains(filter, ignoreCase = true) && !champ.isPicked
                    }
                } else {
                    champs
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

        // Filter 2x
        list = if (isFiltered) {
            filterChampsByRole(list)
        } else {
            list
        }
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

    private fun calculateChampsPerPicks(): StateFlow<List<ChampData>> {
        val calculatedChampsPerPick = combine(
            _allChampsData
        ) { allChamps ->
            val ownPickNames = pickedOwnTeamChamps.first().map { it.ChampName }
            val theirPickNames = pickedTheirTeamChamps.first().map { it.ChampName }

            allChamps.first().map { champ ->

                var currentScoreOwn = 0
                var currentScoreTheir = 0

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
        val list = _allChampsData.map { champs ->
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
                    pickedBy = TeamSide.NONE,
                    ScoreOwn = 0,
                    ScoreTheir = 0
                )
            } // Setze isPicked für alle Champs zurück
        }

    }

    private fun getTeamAgregatedPoints(teamSide: TeamSide): StateFlow<Int> {
        return combine(unfilteredChosableChampList) { champsWithScores ->
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

    fun toggleStreaming() {
        val currentValue = isStreamingEnabled.value // Hole den aktuellen Wert vom StateFlow
        val newValue = !currentValue
        viewModelScope.launch {
            repository.updateStreamingEnabled(newValue)
            // Der StateFlow `isStreamingEnabled` wird automatisch durch den Flow aus dem Repo aktualisiert.
            Log.d("VideoStreamViewModel", "Toggled isStreamingEnabled to: $newValue (saved to DB)")
        }
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