package com.example.hotsdraftadviser

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.hotsdraftadviser.database.AppDatabase
import com.example.hotsdraftadviser.database.isStreamingEnabled.StreamingSettingsRepository
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
        Log.d("ViewModelInit", "Initializing repository...")
        try {
            Log.d("ViewModelInit", "Getting database instance...")
            val db = AppDatabase.getDatabase(application)
            Log.d("ViewModelInit", "Database instance obtained: $db")
            Log.d("ViewModelInit", "Getting DAO...")
            val dao = db.streamingSettingDao()
            Log.d("ViewModelInit", "DAO obtained: $dao")
            val repoInstance = StreamingSettingsRepository(dao)
            Log.d("ViewModelInit", "Repository instance created: $repoInstance")
            repoInstance
        } catch (e: Exception) {
            Log.e("ViewModelInit", "Error initializing repository", e)
            throw e
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

    fun mapChampNameToDrawable(champName: String): Int? {
        when (champName) {
            "Abathur" -> return R.drawable.abathur_card_portrait
            "Alarak" -> return R.drawable.alarak_card_portrait
            "Alexstrasza" -> return R.drawable.alexstrasza_card_portrait
            "Ana" -> return R.drawable.ana_card_portrait
            "Anduin" -> return R.drawable.anduin_card_portrait
            "Anubarak" -> return R.drawable.anubarak_card_portrait
            "Artanis" -> return R.drawable.artanis_card_portrait
            "Arthas" -> return R.drawable.arthas_card_portrait
            "Auriel" -> return R.drawable.auriel_card_portrait
            "Azmodan" -> return R.drawable.azmodan_card_portrait
            "Blaze" -> return R.drawable.blaze_card_portrait
            "Brightwing" -> return R.drawable.brightwing_card_portrait
            "Cassia" -> return R.drawable.cassia_card_portrait
            "Chen" -> return R.drawable.chen_card_portrait
            "Cho" -> return R.drawable.cho_card_portrait
            "Chogall" -> return R.drawable.cho_card_portrait
            "Chromie" -> return R.drawable.chromie_card_portrait
            "Deathwing" -> return R.drawable.deathwing_card_portrait
            "Deckard" -> return R.drawable.deckard_card_portrait
            "Dehaka" -> return R.drawable.dehaka_card_portrait
            "Diablo" -> return R.drawable.diablo_card_portrait
            "DVA" -> return R.drawable.dva_card_portrait
            "ETC" -> return R.drawable.etc_card_portrait
            "Falstad" -> return R.drawable.falstad_card_portrait
            "Fenix" -> return R.drawable.fenix_card_portrait
            "Gall" -> return R.drawable.gall_card_portrait
            "Garrosh" -> return R.drawable.garrosh_card_portrait
            "Gazlowe" -> return R.drawable.gazlowe_card_portrait
            "Genji" -> return R.drawable.genji_card_portrait
            "Greymane" -> return R.drawable.greymane_card_portrait
            "Guldan" -> return R.drawable.guldan_card_portrait
            "Hanzo" -> return R.drawable.hanzo_card_portrait
            "Hogger" -> return R.drawable.hogger_card_portrait
            "Illidan" -> return R.drawable.zillidan_card_portrait
            "Imperius" -> return R.drawable.zimperius_card_portrait
            "Jaina" -> return R.drawable.jaina_card_portrait
            "Johanna" -> return R.drawable.johanna_card_portrait
            "Junkrat" -> return R.drawable.junkrat_card_portrait
            "Kaelthas" -> return R.drawable.kaelthas_card_portrait
            "Kelthuzad" -> return R.drawable.kelthuzad_card_portrait
            "Kerrigan" -> return R.drawable.kerrigan_card_portrait
            "Kharazim" -> return R.drawable.kharazim_card_portrait
            "Leoric" -> return R.drawable.leoric_card_portrait
            "Lili" -> return R.drawable.lili_card_portrait
            "Li-Ming" -> return R.drawable.liming_card_portrait
            "LtMorales" -> return R.drawable.ltmorales_card_portrait
            "Lucio" -> return R.drawable.lucio_card_portrait
            "Lunara" -> return R.drawable.lunara_card_portrait
            "Maiev" -> return R.drawable.maiev_card_portrait
            "Malfurion" -> return R.drawable.malfurion_card_portrait
            "Malganis" -> return R.drawable.malganis_card_portrait
            "Malthael" -> return R.drawable.malthael_card_portrait
            "Medivh" -> return R.drawable.medivh_card_portrait
            "Mei" -> return R.drawable.mei_card_portrait
            "Mephisto" -> return R.drawable.mephisto_card_portrait
            "Muradin" -> return R.drawable.muradin_card_portrait
            "Murky" -> return R.drawable.murky_card_portrait
            "Nazeebo" -> return R.drawable.nazeebo_card_portrait
            "Nova" -> return R.drawable.nova_card_portrait
            "Orphea"  -> return R.drawable.orphea_card_portrait
            "Probius" -> return R.drawable.probius_card_portrait
            "Qhira" -> return R.drawable.qhira_card_portrait
            "Ragnaros" -> return R.drawable.ragnaros_card_portrait
            "Raynor" -> return R.drawable.raynor_card_portrait
            "Rehgar" -> return R.drawable.rehgar_card_portrait
            "Rexxar" -> return R.drawable.rexxar_card_portrait
            "Samuro" -> return R.drawable.samuro_card_portrait
            "SgtHammer" -> return R.drawable.sgthammer_card_portrait
            "Sonya" -> return R.drawable.sonya_card_portrait
            "Stitches" -> return R.drawable.stitches_card_portrait
            "Stukov" -> return R.drawable.stukov_card_portrait
            "Sylvanas" -> return R.drawable.sylvanas_card_portrait
            "Tassadar" -> return R.drawable.tassadar_card_portrait
            "TheButcher" -> return R.drawable.thebutcher_card_portrait
            "TheLostVikings" -> return R.drawable.thelostvikings_card_portrait
            "Thrall" -> return R.drawable.thrall_card_portrait
            "Tracer" -> return R.drawable.tracer_card_portrait
            "Tychus" -> return R.drawable.tychus_card_portrait
            "Tyrael" -> return R.drawable.tyrael_card_portrait
            "Tyrande" -> return R.drawable.tyrande_card_portrait
            "Uther" -> return R.drawable.uther_card_portrait
            "Valeera" -> return R.drawable.valeera_card_portrait
            "Valla" -> return R.drawable.valla_card_portrait
            "Varian" -> return R.drawable.varian_card_portrait
            "Whitemane" -> return R.drawable.whitemane_card_portrait
            "Xul" -> return R.drawable.xul_card_portrait
            "Yrel" -> return R.drawable.yrel_card_portrait
            "Zagara" -> return R.drawable.zagara_card_portrait
            "Zeratul" -> return R.drawable.zaratul_card_portrait
            "Zarya" -> return R.drawable.zarya_card_portrait
            "Zuljin" -> return R.drawable.zuljin_card_portrait


            else -> return null
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