package com.jcdevelopment.hotsdraftadviser

import android.app.Application
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.jcdevelopment.hotsdraftadviser.ApiService.hotsApi
import com.jcdevelopment.hotsdraftadviser.database.AppDatabase
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampRepository
import com.jcdevelopment.hotsdraftadviser.database.champPersist.champString.ChampStringCodeEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.champString.ChampStringCodeRepository
import com.jcdevelopment.hotsdraftadviser.database.favoritChamps.FavoriteChampionsRepository
import com.jcdevelopment.hotsdraftadviser.database.isFirstStart.FirstStartRepository
import com.jcdevelopment.hotsdraftadviser.database.isListShown.IsListModeRepository
import com.jcdevelopment.hotsdraftadviser.database.isStarRating.IsStarRatingRepository
import com.jcdevelopment.hotsdraftadviser.database.isStreamingEnabled.StreamingSettingsRepository
import com.jcdevelopment.hotsdraftadviser.database.resetCounter.ResetCounterRepository
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.MinVerionCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import kotlin.collections.List

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)

    private val streamingSettingsRepository: StreamingSettingsRepository =
        StreamingSettingsRepository(db.streamingSettingDao())
    private val isListModeRepository: IsListModeRepository =
        IsListModeRepository(db.isListShownSettingDao())
    private val favoriteChampionsRepository: FavoriteChampionsRepository =
        FavoriteChampionsRepository(db.favoriteChampionDao())
    private val starRateRepository: IsStarRatingRepository =
        IsStarRatingRepository(db.isStarRatingSettingDao())
    private val isFirstStartRepository: FirstStartRepository =
        FirstStartRepository(db.firstStartSettingDao())
    private val champRepository: ChampRepository = ChampRepository(db.champDao())
    private val champStringRepository: ChampStringCodeRepository =
        ChampStringCodeRepository(db.champStringCodeDao())

    private val resetCounterRepository: ResetCounterRepository =
        ResetCounterRepository(db.resetCounterDao())

    // Dein isStreamingEnabled als StateFlow, das von der Datenbank gespeist wird
    val isStreamingEnabled: StateFlow<Boolean> = streamingSettingsRepository.isStreamingEnabled
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

    val champDBStringCode: StateFlow<ChampStringCodeEntity?> = champStringRepository.champStringCode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ChampStringCodeEntity(jsonString = "")
        )

    init {
        viewModelScope.launch {
            champDBStringCode.collect { string ->
                Log.d("StringCodeViewModel", "stringcode from DB (Flow): $string")
            }
        }
    }

    val resetCounter: StateFlow<Int> = resetCounterRepository.resetCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    private val _isDisclaymerShown = MutableStateFlow(false)
    private val _targetState = MutableStateFlow(true)
    private val _isTutorialShown = MutableStateFlow(false)
    private val _isListMode = MutableStateFlow(false)
    private val _isStreamingEnabled = MutableStateFlow(true)

    private val _allChampsData = MutableStateFlow<List<ChampData>>(emptyList())

    private val _filterMapsString = MutableStateFlow<String>("")
    private val _filterChampString = MutableStateFlow<String>("")
    private val _choosenMap = MutableStateFlow<String>("")
    private val _sortState = MutableStateFlow<SortState>(SortState.OWNPOINTS)
    private val _roleFilter = MutableStateFlow<List<RoleEnum>>(emptyList())
    private val _favFilter = MutableStateFlow<Boolean>(false)
    private val _minVersionCode = MutableStateFlow<MinVerionCode>(MinVerionCode(0))
    private val maxPicks = 5
    private val maxBans = 3
    private var pickcounter: MutableMap<TeamSide, Int> =
        mutableMapOf(
            TeamSide.OWN to 0,
            TeamSide.THEIR to 0,
            TeamSide.BANNEDOWN to 0,
            TeamSide.BANNEDTHEIR to 0
        )

    val isDisclaymerShown: StateFlow<Boolean> = _isDisclaymerShown.asStateFlow()
    val isTutorialShown: StateFlow<Boolean> = _isTutorialShown.asStateFlow()
    val isListMode: StateFlow<Boolean> = isListModeRepository.isListModeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    val isFirstStart: StateFlow<Boolean> = isFirstStartRepository.isFirstStartFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    val isStarRatingMode: StateFlow<Boolean> = starRateRepository.isStarRatingEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )
    val favFilter: StateFlow<Boolean> = _favFilter.asStateFlow()

    val targetState: StateFlow<Boolean> = _targetState

    val allChampsData = _allChampsData.asStateFlow()
    val mapList: StateFlow<List<String>> = getSortedUniqueMaps()
    val filterMapsString: StateFlow<String> = _filterMapsString.asStateFlow()
    val filteredMaps: StateFlow<List<String>> = filterMapsByString(mapList, filterMapsString)

    val filterOwnChampString: StateFlow<String> = _filterChampString.asStateFlow()
    val sortState: StateFlow<SortState> = _sortState.asStateFlow()

    val pickedTheirTeamChamps: StateFlow<List<ChampData>> = getPickedTheirTeamChamps(TeamSide.THEIR)
    val pickedOwnTeamChamps: StateFlow<List<ChampData>> = getPickedTheirTeamChamps(TeamSide.OWN)

    val minVersionCode: StateFlow<MinVerionCode> = _minVersionCode.asStateFlow()


    private fun getPickedTheirTeamChamps(team: TeamSide): StateFlow<List<ChampData>> =
        _allChampsData.map { champs -> champs.filter { it.pickedBy == team } }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val unfilteredChosableChampList: StateFlow<List<ChampData>> =
        dataFlowForChampListWithScores(false, false, false)

    private val _choosableChampList = dataFlowForChampListWithScores(true, false, true)

    private val _distinctchoosableChampList = dataFlowForChampListWithScores(true, true, true)
    private val _bannedChamps = MutableStateFlow<List<ChampData>>(emptyList())

    val chosableChampList: StateFlow<List<ChampData>> = _choosableChampList
    val distinctChosableChampList: StateFlow<List<ChampData>> = _distinctchoosableChampList
    val bannedChamps: StateFlow<List<ChampData>> = _bannedChamps
    val theirsBannedChamps: StateFlow<List<ChampData>> =
        _bannedChamps.map { it.filter { it.pickedBy == TeamSide.BANNEDTHEIR } }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList<ChampData>()
        )
    val ownBannedChamps: StateFlow<List<ChampData>> =
        _bannedChamps.map { it.filter { it.pickedBy == TeamSide.BANNEDOWN } }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList<ChampData>()
        )
    val allChampsDistinct = dataFlowForChampListWithScores(false, true, false)

    val distinctfilteredChosableChampList: StateFlow<List<ChampData>> = combine(
        allChampsDistinct,
        distinctChosableChampList
    ) { unfilteredList, distinctList ->
        val distinctChampNames = distinctList.map { it.ChampName }.toSet()
        unfilteredList.filter { it.ChampName in distinctChampNames }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val ownScoreMax = allChampsDistinct.map { list -> list.maxOfOrNull { it.scoreOwn } ?: 1 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 1
        )

    val theirScoreMax = allChampsDistinct.map { list -> list.maxOfOrNull { it.scoreTheir } ?: 1 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 1
        )

    val fitTeamMax: StateFlow<Int> = allChampsDistinct.map { list ->
        list.maxOfOrNull { it.fitTeam } ?: 1
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 1
    )

    val goodAgainstTeamMax: StateFlow<Int> = combine(
        allChampsDistinct, // Assuming this contains champs with their StrongAgainst properties
        pickedTheirTeamChamps
    ) { champs, pickedTheirChamps ->
        champs.maxOfOrNull { champ ->
            champ.StrongAgainst.sumOf { strongAgainstEntry ->
                if (pickedTheirChamps.any { pickedChamp -> pickedChamp.ChampName == strongAgainstEntry.ChampName }) {
                    strongAgainstEntry.ScoreValue
                } else 0
            }
        } ?: 1
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 1
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

    fun toggleDisclaymer() {
        viewModelScope.launch {
            _isDisclaymerShown.value = !_isDisclaymerShown.value
        }
    }

    fun toggleTutorial() {
        viewModelScope.launch {
            _isTutorialShown.value = !_isTutorialShown.value
            isFirstStartRepository.setIsFirstStart(false)
        }
    }

    fun toggleListMode() {
        viewModelScope.launch {
            isListModeRepository.updateListModeStatus(!isListMode.value)
            checkIfChampIsFavorite()
        }
    }

    fun toggleStarRateMode() {
        viewModelScope.launch {
            starRateRepository.updateStarRatingStatus(!isStarRatingMode.value)
        }
    }

    fun setChosenMapByName(name: String) {
        viewModelScope.launch {
            delay(550)
            _choosenMap.value = name
        }

        _targetState.value = false
    }


    fun pickChampForTeam(index: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentChampList = _distinctchoosableChampList.first()
            val alreadyPicked = currentChampList.filter { it.isPicked && it.pickedBy == teamSide }
            val isChogall = currentChampList[index].ChampName == "Chogall"
            var teamCounter = pickcounter[teamSide] ?: 0

            if (isChogall) {
                if (teamCounter < maxPicks - 1) {
                    pickcounter[teamSide] = teamCounter + 2
                    val pickedChamp =
                        currentChampList.find { it.ChampName == currentChampList[index].ChampName }
                            ?.copy(isPicked = true, pickedBy = teamSide)
                            ?: return@launch // Frühzeitiger Ausstieg, falls der Champ nicht gefunden wird
                    _allChampsData.value =
                        _allChampsData.value.map { if (it.ChampName == pickedChamp.ChampName) pickedChamp else it }
                }
            } else if (teamCounter < maxPicks) {
                pickcounter[teamSide] = teamCounter + 1
                val pickedChamp =
                    currentChampList.find { it.ChampName == currentChampList[index].ChampName }
                        ?.copy(isPicked = true, pickedBy = teamSide)
                        ?: return@launch // Frühzeitiger Ausstieg, falls der Champ nicht gefunden wird
                _allChampsData.value =
                    _allChampsData.value.map { if (it.ChampName == pickedChamp.ChampName) pickedChamp else it }
            }
        }
    }

    fun setBansPerTeam(i: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentChampList = _distinctchoosableChampList.first()
            var teamCounter = pickcounter[teamSide] ?: 0
            if (teamCounter < maxBans) {
                val bannedChamp = currentChampList[i].copy(isPicked = true, pickedBy = teamSide)
                _bannedChamps.value = _bannedChamps.value + bannedChamp
                updateChampDataWithPickStatus(bannedChamp)
                pickcounter[teamSide] = teamCounter + 1
            }
        }
    }

    fun removeBan(index: Int, teamSide: TeamSide) {
        viewModelScope.launch {
            val currentBannedList = if (teamSide == TeamSide.BANNEDOWN) {
                ownBannedChamps.value
            } else {
                theirsBannedChamps.value
            }

            if (index < currentBannedList.size) {
                val champToUnban =
                    currentBannedList[index].copy(isPicked = false, pickedBy = TeamSide.NONE)
                _bannedChamps.value = _bannedChamps.value - currentBannedList[index]
                _allChampsData.value =
                    _allChampsData.value.map { if (it.ChampName == champToUnban.ChampName) champToUnban else it }
            }
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

                if (teamPicks[index].ChampName == "Chogall") {
                    pickcounter[teamSide] = pickcounter[teamSide]!! - 2
                } else {
                    pickcounter[teamSide] = pickcounter[teamSide]!! - 1
                }

            } else {
                Log.w(
                    "ViewModel",
                    "Ungültiger Index zum Entfernen des Picks: $index für Team: $teamSide"
                )
            }
        }
    }

    private fun updateChampDataWithPickStatus(
        champ: ChampData
    ) {
        val currentChampData = _allChampsData.value.toMutableList()
        val indexInAllChamps = currentChampData.indexOfFirst { it.ChampName == champ.ChampName }
        val indexIfChogal = currentChampData.indexOfLast { it.ChampName == champ.ChampName }
        if (indexInAllChamps != -1) {
            currentChampData[indexInAllChamps] =
                currentChampData[indexInAllChamps].copy(isPicked = true)
            currentChampData[indexIfChogal] =
                currentChampData[indexInAllChamps].copy(isPicked = true)
            _allChampsData.value = currentChampData.toList()
        }
    }

    private fun dataFlowForChampListWithScores(
        isFiltered: Boolean,
        isDistincted: Boolean,
        isFilterPicks: Boolean
    ): StateFlow<List<ChampData>> {
        var copy = calculateChampsPerPicks()
        var list = combine(
            copy,
            favoriteChampionsRepository.getAllFavoriteChampionNamesFlow(),
            _choosenMap,
            _sortState,
            _filterChampString
        ) { champs, allFavChamps, mapSearchString, doSortByOwn, filter ->

            val filteredByNameChamps = if (filter.isBlank()) {
                champs
            } else {
                if (isFiltered) {
                    champs.filter { champ ->
                        (champ.ChampName.contains(
                            filter,
                            ignoreCase = true
                        ) || champ.localName!!.contains(
                            filter,
                            ignoreCase = true
                        )) && !champ.isPicked
                    }
                } else {
                    champs
                }
            }

            val lowerCaseSearchString = mapSearchString.lowercase()

            val filteredByPickedChamps = if (isFilterPicks) {
                filteredByNameChamps.filter { champ ->
                    !champ.isPicked
                }
            } else {
                filteredByNameChamps
            }

            val calculatedChamps = filteredByPickedChamps.map { champ ->
                val updatedChamp = champ.copy()

                if (!mapSearchString.isEmpty()) {
                    champ.MapScore.forEach { mapScore ->
                        if (mapScore.MapName.lowercase().contains(lowerCaseSearchString)) {
                            updatedChamp.scoreOwn += mapScore.ScoreValue
                            updatedChamp.scoreTheir += mapScore.ScoreValue
                        }
                    }
                }
                updatedChamp
            }


            val sortedChamps = if (doSortByOwn == SortState.OWNPOINTS) {
                calculatedChamps.sortedByDescending { it.scoreOwn } // Höchster ScoreOwn zuerst
            } else if (doSortByOwn == SortState.THEIRPOINTS) {
                calculatedChamps.sortedByDescending { it.scoreTheir } // Höchster ScoreTheir zuerst
            } else {
                calculatedChamps.sortedBy { it.ChampName }
            }

            sortedChamps
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

        list = if (isDistincted) {
            list.map { champs ->
                champs.distinctBy { it.ChampName }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        } else {
            list
        }

        // Normalize scores based on the maximum mapScore in the list
        list = list.map { champs ->

            val maxMapScoreOverall = champs.maxOfOrNull { champ ->
                val scoreOfCurrentMap = champ.MapScore.find { it.MapName == _choosenMap.value }
                scoreOfCurrentMap?.ScoreValue ?: 0
            } ?: 1

            val champsWithMapFloat = champs.map { champ ->
                val mapScoreForChosenMap =
                    champ.MapScore.find { it.MapName == _choosenMap.value }?.ScoreValue ?: 0
                val mapFloatValue =
                    if (maxMapScoreOverall != 0) (mapScoreForChosenMap.toFloat() / maxMapScoreOverall.toFloat()) else 0f
                champ.copy(
                    mapFloat = mapFloatValue
                )
            }

            champsWithMapFloat
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        if (isFiltered) {
            list = filterChampsByFav(list)
        }

        list = addMapScoreToDistinctMaps(list)

        return list
    }

    private fun filterChampsByFav(list: StateFlow<List<ChampData>>): StateFlow<List<ChampData>> {
        return combine(list, _favFilter) { champs, favFilter ->
            if (favFilter) {
                champs.filter { it.isAFavoriteChamp }
            } else {
                champs
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    private fun addMapScoreToDistinctMaps(list: StateFlow<List<ChampData>>): StateFlow<List<ChampData>> {
        return list.map { champs ->
            champs.map { champ ->
                val distinctMapScores = champ.MapScore
                    .groupBy { it.MapName }
                    .map { (mapName, scores) ->
                        scores.first().copy(ScoreValue = scores.sumOf { it.ScoreValue })
                    }
                champ.copy(MapScore = distinctMapScores)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    private fun filterChampsByRole(flow: StateFlow<List<ChampData>>): StateFlow<List<ChampData>> {
        return combine(flow, _roleFilter) { champs, selectedRoles ->
            if (selectedRoles.isEmpty()) {
                champs
            } else {
                champs.filter { champ ->
                    selectedRoles.any { it ->
                        val ccrole = champ.ChampRoleAlt
                        ccrole.contains(it)
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
                //--------------------------//
                var strongAgainstScoreOwn = 0
                var strongAgainstScoreTheir = 0

                var weakAgainstScoreOwn = 0
                var weakAgainstScoreTheir = 0

                var goodTeamWithScoreOwn = 0
                var goodTeamWithScoreTheir = 0

                champ.StrongAgainst.forEach { strongAgainstEntry ->
                    if (theirPickNames.contains(strongAgainstEntry.ChampName)) {
                        currentScoreOwn += strongAgainstEntry.ScoreValue
                        strongAgainstScoreOwn += strongAgainstEntry.ScoreValue
                    }

                    if (ownPickNames.contains(strongAgainstEntry.ChampName)) {
                        currentScoreTheir += strongAgainstEntry.ScoreValue
                        strongAgainstScoreTheir += strongAgainstEntry.ScoreValue
                    }
                }

                //TODO überprüfen ob das passt - eventuell doppelung mit dadrüber
                champ.WeakAgainst.forEach { weakAgainstEntry ->
                    if (theirPickNames.contains(weakAgainstEntry.ChampName)) {
                        currentScoreOwn -= weakAgainstEntry.ScoreValue
                        weakAgainstScoreOwn += weakAgainstEntry.ScoreValue
                    }
                    if (ownPickNames.contains(weakAgainstEntry.ChampName)) {
                        currentScoreTheir -= weakAgainstEntry.ScoreValue
                        weakAgainstScoreTheir += weakAgainstEntry.ScoreValue
                    }
                }

                champ.GoodTeamWith.forEach { goodTeamEntry ->
                    if (ownPickNames.contains(goodTeamEntry.ChampName)) {
                        currentScoreOwn += goodTeamEntry.ScoreValue
                        goodTeamWithScoreOwn += goodTeamEntry.ScoreValue
                    }
                    if (theirPickNames.contains(goodTeamEntry.ChampName)) {
                        currentScoreTheir += goodTeamEntry.ScoreValue
                        goodTeamWithScoreTheir += goodTeamEntry.ScoreValue
                    }
                }

                champ.copy(
                    scoreOwn = currentScoreOwn,
                    scoreTheir = currentScoreTheir,
                    fitTeam = goodTeamWithScoreOwn,
                    goodAgainstTeam = strongAgainstScoreOwn - weakAgainstScoreOwn
                )
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

    private suspend fun checkIfChampIsFavorite() {
        _allChampsData.value = _allChampsData.value.map { champ ->
            champ.copy(
                isAFavoriteChamp = favoriteChampionsRepository.isChampionFavorite(champ.ChampName)
            )
        }
    }

    private fun setUniqueMapsInMapScore() {
        _allChampsData.value = _allChampsData.value.map { champ ->
            val uniqueMapScores = champ.MapScore
                .groupBy { it.MapName }
                .map { (mapName, scores) ->
                    scores.first().copy(ScoreValue = scores.sumOf { it.ScoreValue })
                }
            champ.copy(MapScore = uniqueMapScores)
        }
    }

    private fun setUniqueCahmpsInChampScores() {
        _allChampsData.value = _allChampsData.value.map { champ ->
            val uniqueChampScore = champ.StrongAgainst
                .groupBy { it.ChampName }
                .map { (mapName, scores) ->
                    scores.first().copy(ScoreValue = scores.sumOf { it.ScoreValue })
                }
            champ.copy(StrongAgainst = uniqueChampScore)
        }

        _allChampsData.value = _allChampsData.value.map { champ ->
            val uniqueChampScore = champ.WeakAgainst
                .groupBy { it.ChampName }
                .map { (mapName, scores) ->
                    scores.first().copy(ScoreValue = scores.sumOf { it.ScoreValue })
                }
            champ.copy(WeakAgainst = uniqueChampScore)
        }

        _allChampsData.value = _allChampsData.value.map { champ ->
            val uniqueChampScore = champ.GoodTeamWith
                .groupBy { it.ChampName }
                .map { (mapName, scores) ->
                    scores.first().copy(ScoreValue = scores.sumOf { it.ScoreValue })
                }
            champ.copy(GoodTeamWith = uniqueChampScore)
        }
    }

    fun updateMapsSearchQuery(query: String) {
        _filterMapsString.value = query
    }

    fun updateChampSearchQuery(query: String) {
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
            val application = getApplication<Application>()

            currentMaps.filter { item ->
                item.lowercase().contains(lowerCaseSearchString) ||
                        application.getString(Utilitys.mapMapNameToStringRessource(item)!!)
                            .lowercase().contains(lowerCaseSearchString)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadJson() {
        var dbChampJson: String?
        var champData: List<ChampData> = listOf()

        viewModelScope.launch(Dispatchers.IO) {
            dbChampJson = champStringRepository.champStringCode.first()?.jsonString
            val jsonString = try {
                application.assets.open("output.json")
            } catch (ioException: IOException) {
                Log.e(
                    "JsonAssetViewModel",
                    "Fehler beim Lesen der Asset-Datei 'outputjson': ${ioException.message}"
                )
                null
            }

            if (jsonString != null || dbChampJson != null) {
                try {
                    champData = if (jsonString != null) {
                        Json.decodeFromStream<List<ChampData>>(jsonString)
                    } else if (dbChampJson != null) {
                        Json.decodeFromString<List<ChampData>>(dbChampJson!!)
                    } else {
                        listOf()
                    }

                    Log.d("TAG", "ChampData erfolgreich gemappt: ${champData}")

                    _allChampsData.value = champData
                    val champDataInJson = Json.encodeToString(champData)

                    champStringRepository.saveOrUpdate(
                        ChampStringCodeEntity(
                            version = 0,
                            jsonString = champDataInJson
                        )
                    )

                    checkIfChampIsFavorite()
                    setUniqueMapsInMapScore()
                    setUniqueCahmpsInChampScores()
                    _allChampsData.value = _allChampsData.value.map { champ ->
                        val application = getApplication<Application>()
                        champ.copy(
                            difficulty = Utilitys.mapDifficultyForChamp(champ.ChampName)!!,
                            origin = Utilitys.mapChampToOrigin(champ.ChampName)!!,
                            localName = application.getString(
                                Utilitys.mapChampNameToStringRessource(
                                    champ.ChampName
                                )!!
                            )
                        )
                    }

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
        _targetState.value = true
    }

    fun setRoleFilter(role: RoleEnum?) {
        if (role == null) {
            _roleFilter.value = emptyList()
        } else {
            if (_roleFilter.value.contains(role)) {
                _roleFilter.value = _roleFilter.value.filter { it -> it != role }
            } else {
                _roleFilter.value = _roleFilter.value + role
            }
        }
    }

    fun toggleFavFilter() {
        _favFilter.value = !_favFilter.value
    }

    fun resetAll() {
        viewModelScope.launch {
            pickcounter[TeamSide.OWN] = 0
            pickcounter[TeamSide.THEIR] = 0
            pickcounter[TeamSide.BANNEDOWN] = 0
            pickcounter[TeamSide.BANNEDTHEIR] = 0
            _targetState.value = true
            _filterMapsString.value = ""
            _filterChampString.value = ""
            _choosenMap.value = ""
            _sortState.value = SortState.OWNPOINTS
            _roleFilter.value = emptyList()
            _bannedChamps.value = emptyList()
            _allChampsData.value = _allChampsData.value.map {
                it.copy(
                    isPicked = false,
                    pickedBy = TeamSide.NONE,
                    scoreOwn = 0,
                    scoreTheir = 0
                )
            }
        }

    }

    private fun getTeamAgregatedPoints(teamSide: TeamSide): StateFlow<Int> {
        return combine(unfilteredChosableChampList) { champsWithScores ->
            var totalScore = 0
            val pickedChamp = champsWithScores.first().filter { it -> it.pickedBy == teamSide }
            pickedChamp.forEach { pickedChamp ->
                champsWithScores.first().find { it.ChampName == pickedChamp.ChampName }
                    ?.let { matchedChamp ->
                        totalScore += matchedChamp.scoreOwn
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
            streamingSettingsRepository.updateStreamingEnabled(newValue)
            // Der StateFlow `isStreamingEnabled` wird automatisch durch den Flow aus dem Repo aktualisiert.
            Log.d("VideoStreamViewModel", "Toggled isStreamingEnabled to: $newValue (saved to DB)")
        }
    }

    fun toggleFavoriteStatus(championName: String) {
        viewModelScope.launch {
            favoriteChampionsRepository.toggleFavoriteStatus(championName)
            checkIfChampIsFavorite()
        }
    }

    fun scrollList(listState: LazyListState, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
    }

    fun incrementResetCounter() {
        viewModelScope.launch {
            resetCounterRepository.incrementClickCount()
        }
    }

    fun fetchMinVersionCode() {
        viewModelScope.launch {
            try {
                _minVersionCode.value = hotsApi.getMinVersionCode()
                Log.d("ApiServiceDebug", "Erfolgreich gemappt: $_minVersionCode")
            } catch (e: Exception) {
                Log.e("ApiServiceDebug", "Fehler beim Mappen des JSON", e)
            }
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

enum class Difficulty {
    EASY, MEDIUM, HARD, EXTREME
}