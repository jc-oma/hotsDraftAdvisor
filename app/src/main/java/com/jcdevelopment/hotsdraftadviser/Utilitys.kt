package com.jcdevelopment.hotsdraftadviser

object Utilitys {
    fun mapDifficultyForChamp(champName: String): Difficulty? {
        when (champName) {
            "Abathur" -> return Difficulty.EXTREME
            "Alarak" -> return Difficulty.EXTREME
            "Alexstrasza" -> return Difficulty.MEDIUM
            "Ana" -> return Difficulty.EXTREME
            "Anduin" -> return Difficulty.MEDIUM
            "Anubarak" -> return Difficulty.HARD
            "Artanis" -> return Difficulty.HARD
            "Arthas" -> return Difficulty.MEDIUM
            "Auriel" -> return Difficulty.HARD
            "Azmodan" -> return Difficulty.MEDIUM
            "Blaze" -> return Difficulty.MEDIUM
            "Brightwing" -> return Difficulty.EASY
            "Cassia" -> return Difficulty.MEDIUM
            "Chen" -> return Difficulty.MEDIUM
            "Cho" -> return Difficulty.MEDIUM
            "Chogall" -> return Difficulty.MEDIUM
            "Chromie" -> return Difficulty.HARD
            "Deathwing" -> return Difficulty.HARD
            "Deckard" -> return Difficulty.MEDIUM
            "Dehaka" -> return Difficulty.MEDIUM
            "Diablo" -> return Difficulty.HARD
            "DVA" -> return Difficulty.MEDIUM
            "ETC" -> return Difficulty.HARD
            "Falstad" -> return Difficulty.MEDIUM
            "Fenix" -> return Difficulty.MEDIUM
            "Gall" -> return Difficulty.EASY
            "Garrosh" -> return Difficulty.HARD
            "Gazlowe" -> return Difficulty.MEDIUM
            "Genji" -> return Difficulty.EXTREME
            "Greymane" -> return Difficulty.HARD
            "Guldan" -> return Difficulty.MEDIUM
            "Hanzo" -> return Difficulty.HARD
            "Hogger" -> return Difficulty.HARD
            "Illidan" -> return Difficulty.EXTREME
            "Imperius" -> return Difficulty.MEDIUM
            "Jaina" -> return Difficulty.MEDIUM
            "Johanna" -> return Difficulty.EASY
            "Junkrat" -> return Difficulty.HARD
            "Kaelthas" -> return Difficulty.HARD
            "Kelthuzad" -> return Difficulty.EXTREME
            "Kerrigan" -> return Difficulty.EXTREME
            "Kharazim" -> return Difficulty.HARD
            "Leoric" -> return Difficulty.MEDIUM
            "Lili" -> return Difficulty.EASY
            "Li-Ming" -> return Difficulty.MEDIUM
            "LtMorales" -> return Difficulty.EXTREME
            "Lucio" -> return Difficulty.MEDIUM
            "Lunara" -> return Difficulty.MEDIUM
            "Maiev" -> return Difficulty.EXTREME
            "Malfurion" -> return Difficulty.HARD
            "Malganis" -> return Difficulty.HARD
            "Malthael" -> return Difficulty.HARD
            "Medivh" -> return Difficulty.EXTREME
            "Mei" -> return Difficulty.HARD
            "Mephisto" -> return Difficulty.HARD
            "Muradin" -> return Difficulty.EASY
            "Murky" -> return Difficulty.HARD
            "Nazeebo" -> return Difficulty.MEDIUM
            "Nova" -> return Difficulty.HARD
            "Orphea" -> return Difficulty.HARD
            "Probius" -> return Difficulty.HARD
            "Qhira" -> return Difficulty.HARD
            "Ragnaros" -> return Difficulty.MEDIUM
            "Raynor" -> return Difficulty.EASY
            "Rehgar" -> return Difficulty.MEDIUM
            "Rexxar" -> return Difficulty.EXTREME
            "Samuro" -> return Difficulty.EXTREME
            "SgtHammer" -> return Difficulty.HARD
            "Sonya" -> return Difficulty.MEDIUM
            "Stitches" -> return Difficulty.HARD
            "Stukov" -> return Difficulty.HARD
            "Sylvanas" -> return Difficulty.HARD
            "Tassadar" -> return Difficulty.HARD
            "Butcher" -> return Difficulty.HARD
            "LostVikings" -> return Difficulty.EXTREME
            "Thrall" -> return Difficulty.MEDIUM
            "Tracer" -> return Difficulty.HARD
            "Tychus" -> return Difficulty.HARD
            "Tyrael" -> return Difficulty.HARD
            "Tyrande" -> return Difficulty.HARD
            "Uther" -> return Difficulty.HARD
            "Valeera" -> return Difficulty.EXTREME
            "Valla" -> return Difficulty.MEDIUM
            "Varian" -> return Difficulty.MEDIUM
            "Whitemane" -> return Difficulty.EXTREME
            "Xul" -> return Difficulty.MEDIUM
            "Yrel" -> return Difficulty.HARD
            "Zagara" -> return Difficulty.MEDIUM
            "Zeratul" -> return Difficulty.EXTREME
            "Zarya" -> return Difficulty.MEDIUM
            "Zuljin" -> return Difficulty.MEDIUM


            else -> return null
        }
    }

    fun mapMapNameToDrawable(champName: String): Int? {
        when (champName) {
            "Alterac Pass" -> return R.drawable.map_alteracpass_card
            "Battlefield of Eternity" -> return R.drawable.map_battlefield_of_eternity_card
            "Black Hearts Bay" -> return R.drawable.map_black_hearts_bay_card
            "Braxis Holdout" -> return R.drawable.map_braxis_holdout_card
            "Cursed Hollow" -> return R.drawable.map_cursed_hollow_card
            "Dragonshire" -> return R.drawable.map_dragon_shire_card
            "Garden of Terror" -> return R.drawable.map_garden_of_terror_card
            "Hanamura" -> return R.drawable.map_hanamura_card
            "Haunted Mines" -> return R.drawable.map_haunted_mines_card
            "Infernal Shrines" -> return R.drawable.map_infernal_shrines_card
            "Lost Caverns" -> return R.drawable.map_lost_caverns_card
            "Sky Temple" -> return R.drawable.map_sky_temple_card
            "Tomb of the Spider Queen" -> return R.drawable.map_tomb_of_the_spider_queen_card
            "Towers of Doom" -> return R.drawable.map_towers_of_doom_card
            "Volskaya Foundry" -> return R.drawable.map_volskaya_foundry_card
            "Warhead Junction" -> return R.drawable.map_warhead_junction_card
            //TODO Fix
            else -> return R.drawable.map_empty
        }
    }

    fun mapMapNameToStringRessource(mapName: String): Int? {
        when (mapName) {
            "Alterac Pass" -> return R.string.map_name_Alterac_Pass
            "Battlefield of Eternity" -> return R.string.map_name_Battlefield_of_Eternity
            "Black Hearts Bay" -> return R.string.map_name_Black_Hearts_Bay
            "Braxis Holdout" -> return R.string.map_name_Braxis_Holdout
            "Cursed Hollow" -> return R.string.map_name_Cursed_Hollow
            "Dragonshire" -> return R.string.map_name_Dragonshire
            "Garden of Terror" -> return R.string.map_name_Garden_of_Terror
            "Hanamura" -> return R.string.map_name_Hanamura
            "Haunted Mines" -> return R.string.map_name_Haunted_Mines
            "Infernal Shrines" -> return R.string.map_name_Infernal_Shrines
            "Lost Caverns" -> return R.string.map_name_Lost_Caverns
            "Sky Temple" -> return R.string.map_name_Sky_Temple
            "Tomb of the Spider Queen" -> return R.string.map_name_Tomb_of_the_Spider_Queen
            "Towers of Doom" -> return R.string.map_name_Towers_of_Doom
            "Volskaya Foundry" -> return R.string.map_name_Volskaya_Foundry
            "Warhead Junction" -> return R.string.map_name_Warhead_Junction

            //TODO FIX
            else -> return R.string.empty_String
        }
    }

    fun mapChampNameToPortraitDrawable(champName: String): Int? {
        when (champName) {
            "Abathur" -> return R.drawable.card_portrait_abathur
            "Alarak" -> return R.drawable.card_portrait_alarak
            "Alexstrasza" -> return R.drawable.card_portrait_alexstrasza
            "Ana" -> return R.drawable.card_portrait_ana_card
            "Anduin" -> return R.drawable.card_portrait_anduin
            "Anubarak" -> return R.drawable.card_portrait_anubarak
            "Artanis" -> return R.drawable.card_portrait_artanis
            "Arthas" -> return R.drawable.card_portrait_arthas
            "Auriel" -> return R.drawable.card_portrait_auriel
            "Azmodan" -> return R.drawable.card_portrait_azmodan
            "Blaze" -> return R.drawable.card_portrait_blaze
            "Brightwing" -> return R.drawable.card_portrait_brightwing
            "Cassia" -> return R.drawable.card_portrait_cassia
            "Chen" -> return R.drawable.card_portrait_chen
            "Cho" -> return R.drawable.card_portrait_cho
            "Chogall" -> return R.drawable.card_portrait_cho
            "Chromie" -> return R.drawable.card_portrait_chromie
            "Deathwing" -> return R.drawable.card_portrait_deathwing
            "Deckard" -> return R.drawable.card_portrait_deckard
            "Dehaka" -> return R.drawable.card_portrait_dehaka
            "Diablo" -> return R.drawable.card_portrait_diablo
            "DVA" -> return R.drawable.card_portrait_dva
            "ETC" -> return R.drawable.card_portrait_etc
            "Falstad" -> return R.drawable.card_portrait_falstad
            "Fenix" -> return R.drawable.card_portrait_fenix
            "Gall" -> return R.drawable.card_portrait_gall
            "Garrosh" -> return R.drawable.card_portrait_garrosh
            "Gazlowe" -> return R.drawable.card_portrait_gazlowe
            "Genji" -> return R.drawable.card_portrait_genji
            "Greymane" -> return R.drawable.card_portrait_greymane
            "Guldan" -> return R.drawable.card_portrait_guldan
            "Hanzo" -> return R.drawable.card_portrait_hanzo
            "Hogger" -> return R.drawable.card_portrait_hogger
            "Illidan" -> return R.drawable.card_portrait_illidan
            "Imperius" -> return R.drawable.card_portrait_imperius
            "Jaina" -> return R.drawable.card_portrait_jaina
            "Johanna" -> return R.drawable.card_portrait_johanna
            "Junkrat" -> return R.drawable.card_portrait_junkrat
            "Kaelthas" -> return R.drawable.card_portrait_kaelthas
            "Kelthuzad" -> return R.drawable.card_portrait_kelthuzad
            "Kerrigan" -> return R.drawable.card_portrait_kerrigan
            "Kharazim" -> return R.drawable.card_portrait_kharazim
            "Leoric" -> return R.drawable.card_portrait_leoric
            "Lili" -> return R.drawable.card_portrait_lili
            "Li-Ming" -> return R.drawable.card_portrait_liming
            "LtMorales" -> return R.drawable.card_portrait_ltmorales
            "Lucio" -> return R.drawable.card_portrait_lucio
            "Lunara" -> return R.drawable.card_portrait_lunara
            "Maiev" -> return R.drawable.card_portrait_maiev
            "Malfurion" -> return R.drawable.card_portrait_malfurion
            "Malganis" -> return R.drawable.card_portrait_malganis
            "Malthael" -> return R.drawable.card_portrait_malthael
            "Medivh" -> return R.drawable.card_portrait_medivh
            "Mei" -> return R.drawable.card_portrait_mei
            "Mephisto" -> return R.drawable.card_portrait_mephisto
            "Muradin" -> return R.drawable.card_portrait_muradin
            "Murky" -> return R.drawable.card_portrait_murky
            "Nazeebo" -> return R.drawable.card_portrait_nazeebo
            "Nova" -> return R.drawable.card_portrait_nova
            "Orphea" -> return R.drawable.card_portrait_orphea
            "Probius" -> return R.drawable.card_portrait_probius
            "Qhira" -> return R.drawable.card_portrait_qhira
            "Ragnaros" -> return R.drawable.card_portrait_ragnaros
            "Raynor" -> return R.drawable.card_portrait_raynor
            "Rehgar" -> return R.drawable.card_portrait_rehgar
            "Rexxar" -> return R.drawable.card_portrait_rexxar
            "Samuro" -> return R.drawable.card_portrait_samuro
            "SgtHammer" -> return R.drawable.card_portrait_sgthammer
            "Sonya" -> return R.drawable.card_portrait_sonya
            "Stitches" -> return R.drawable.card_portrait_stitches
            "Stukov" -> return R.drawable.card_portrait_stukov
            "Sylvanas" -> return R.drawable.card_portrait_sylvanas
            "Tassadar" -> return R.drawable.card_portrait_tassadar
            "Butcher" -> return R.drawable.card_portrait_thebutcher
            "LostVikings" -> return R.drawable.card_portrait_thelostvikings
            "Thrall" -> return R.drawable.card_portrait_thrall
            "Tracer" -> return R.drawable.card_portrait_tracer
            "Tychus" -> return R.drawable.card_portrait_tychus
            "Tyrael" -> return R.drawable.card_portrait_tyrael
            "Tyrande" -> return R.drawable.card_portrait_tyrande
            "Uther" -> return R.drawable.card_portrait_uther
            "Valeera" -> return R.drawable.card_portrait_valeera
            "Valla" -> return R.drawable.card_portrait_valla
            "Varian" -> return R.drawable.card_portrait_varian
            "Whitemane" -> return R.drawable.card_portrait_whitemane
            "Xul" -> return R.drawable.card_portrait_xul
            "Yrel" -> return R.drawable.card_portrait_yrel
            "Zagara" -> return R.drawable.card_portrait_zagara
            "Zeratul" -> return R.drawable.card_portrait_zeratul
            "Zarya" -> return R.drawable.card_portrait_zarya
            "Zuljin" -> return R.drawable.card_portrait_zuljin


            else -> return null
        }
    }

    fun mapChampNameToRoundPortraitDrawable(champName: String): Int? {
        when (champName) {
            "Abathur" -> return R.drawable.round_portrait_abathur
            "Alarak" -> return R.drawable.round_portrait_alarak
            "Alexstrasza" -> return R.drawable.round_portrait_alexstrasza
            "Ana" -> return R.drawable.round_portrait_ana
            "Anduin" -> return R.drawable.round_portrait_anduin
            "Anubarak" -> return R.drawable.round_portrait_anubarak
            "Artanis" -> return R.drawable.round_portrait_artanis
            "Arthas" -> return R.drawable.round_portrait_arthas
            "Auriel" -> return R.drawable.round_portrait_auriel
            "Azmodan" -> return R.drawable.round_portrait_azmodan
            "Blaze" -> return R.drawable.round_portrait_blaze
            "Brightwing" -> return R.drawable.round_portrait_brightwing
            "Cassia" -> return R.drawable.round_portrait_cassia
            "Chen" -> return R.drawable.round_portrait_chen
            "Cho" -> return R.drawable.round_portrait_chogall
            "Chogall" -> return R.drawable.round_portrait_chogall
            "Chromie" -> return R.drawable.round_portrait_chromie
            "Deathwing" -> return R.drawable.round_portrait_deathwing
            "Deckard" -> return R.drawable.round_portrait_deckard
            "Dehaka" -> return R.drawable.round_portrait_dehaka
            "Diablo" -> return R.drawable.round_portrait_diablo
            "DVA" -> return R.drawable.round_portrait_dva
            "ETC" -> return R.drawable.round_portrait_etc
            "Falstad" -> return R.drawable.round_portrait_falstad
            "Fenix" -> return R.drawable.round_portrait_fenix
            "Gall" -> return R.drawable.round_portrait_chogall
            "Garrosh" -> return R.drawable.round_portrait_garrosh
            "Gazlowe" -> return R.drawable.round_portrait_gazlowe
            "Genji" -> return R.drawable.round_portrait_genji
            "Greymane" -> return R.drawable.round_portrait_greymane
            "Guldan" -> return R.drawable.round_portrait_guldan
            "Hanzo" -> return R.drawable.round_portrait_hanzo
            "Hogger" -> return R.drawable.round_portrait_hogger
            "Illidan" -> return R.drawable.round_portrait_illidan
            "Imperius" -> return R.drawable.round_portrait_imperius
            "Jaina" -> return R.drawable.round_portrait_jaina
            "Johanna" -> return R.drawable.round_portrait_johanna
            "Junkrat" -> return R.drawable.round_portrait_junkrat
            "Kaelthas" -> return R.drawable.round_portrait_kaelthas
            "Kelthuzad" -> return R.drawable.round_portrait_kelthuzad
            "Kerrigan" -> return R.drawable.round_portrait_kerrigan
            "Kharazim" -> return R.drawable.round_portrait_kharazim
            "Leoric" -> return R.drawable.round_portrait_leoric
            "Lili" -> return R.drawable.round_portrait_lili
            "Li-Ming" -> return R.drawable.round_portrait_liming
            "LtMorales" -> return R.drawable.round_portrait_ltmorales
            "Lucio" -> return R.drawable.round_portrait_lucio
            "Lunara" -> return R.drawable.round_portrait_lunara
            "Maiev" -> return R.drawable.round_portrait_maiev
            "Malfurion" -> return R.drawable.round_portrait_malfurion
            "Malganis" -> return R.drawable.round_portrait_malganis
            "Malthael" -> return R.drawable.round_portrait_malthael
            "Medivh" -> return R.drawable.round_portrait_medivh
            "Mei" -> return R.drawable.round_portrait_mei
            "Mephisto" -> return R.drawable.round_portrait_mephisto
            "Muradin" -> return R.drawable.round_portrait_muradin
            "Murky" -> return R.drawable.round_portrait_murky
            "Nazeebo" -> return R.drawable.round_portrait_nazeebo
            "Nova" -> return R.drawable.round_portrait_nova
            "Orphea" -> return R.drawable.round_portrait_orphea
            "Probius" -> return R.drawable.round_portrait_probius
            "Qhira" -> return R.drawable.round_portrait_qhira
            "Ragnaros" -> return R.drawable.round_portrait_ragnaros
            "Raynor" -> return R.drawable.round_portrait_raynor
            "Rehgar" -> return R.drawable.round_portrait_rehgar
            "Rexxar" -> return R.drawable.round_portrait_rexxar
            "Samuro" -> return R.drawable.round_portrait_samuro
            "SgtHammer" -> return R.drawable.round_portrait_sgthammer
            "Sonya" -> return R.drawable.round_portrait_sonya
            "Stitches" -> return R.drawable.round_portrait_stitches
            "Stukov" -> return R.drawable.round_portrait_stukov
            "Sylvanas" -> return R.drawable.round_portrait_sylvanas
            "Tassadar" -> return R.drawable.round_portrait_tassadar
            "Butcher" -> return R.drawable.round_portrait_the_butcher
            "LostVikings" -> return R.drawable.round_portrait_the_lost_vikings
            "Thrall" -> return R.drawable.round_portrait_thrall
            "Tracer" -> return R.drawable.round_portrait_tracer
            "Tychus" -> return R.drawable.round_portrait_tychus
            "Tyrael" -> return R.drawable.round_portrait_tyrael
            "Tyrande" -> return R.drawable.round_portrait_tyrande
            "Uther" -> return R.drawable.round_portrait_uther
            "Valeera" -> return R.drawable.round_portrait_valeera
            "Valla" -> return R.drawable.round_portrait_valla
            "Varian" -> return R.drawable.round_portrait_varian
            "Whitemane" -> return R.drawable.round_portrait_whitemane
            "Xul" -> return R.drawable.round_portrait_xul
            "Yrel" -> return R.drawable.round_portrait_yrel
            "Zagara" -> return R.drawable.round_portrait_zagara
            "Zeratul" -> return R.drawable.round_portrait_zeratul
            "Zarya" -> return R.drawable.round_portrait_zarya
            "Zuljin" -> return R.drawable.round_portrait_zuljin


            else -> return null
        }
    }

    fun mapChampNameToPickSlottDrawable(champName: String): Int? {
        when (champName) {
            "Abathur" -> return R.drawable.pick_slot_abathur
            "Alarak" -> return R.drawable.pick_slot_alarak
            "Alexstrasza" -> return R.drawable.pick_slot_alexstrasza
            "Ana" -> return R.drawable.pick_slot_ana
            "Anduin" -> return R.drawable.pick_slot_anduin
            "Anubarak" -> return R.drawable.pick_slot_anubarak
            "Artanis" -> return R.drawable.pick_slot_artanis
            "Arthas" -> return R.drawable.pick_slot_arthas
            "Auriel" -> return R.drawable.pick_slot_auriel
            "Azmodan" -> return R.drawable.pick_slot_azmodan
            "Blaze" -> return R.drawable.pick_slot_blaze
            "Brightwing" -> return R.drawable.pick_slot_brightwing
            "Cassia" -> return R.drawable.pick_slot_cassia
            "Chen" -> return R.drawable.pick_slot_chen
            "Cho" -> return R.drawable.pick_slot_cho
            "Chogall" -> return R.drawable.pick_slot_chogall
            "Chromie" -> return R.drawable.pick_slot_chromie
            "Deathwing" -> return R.drawable.pick_slot_deathwing
            "Deckard" -> return R.drawable.pick_slot_deckard
            "Dehaka" -> return R.drawable.pick_slot_dehaka
            "Diablo" -> return R.drawable.pick_slot_diablo
            "DVA" -> return R.drawable.pick_slot_dva
            "ETC" -> return R.drawable.pick_slot_etc
            "Falstad" -> return R.drawable.pick_slot_falstad
            "Fenix" -> return R.drawable.pick_slot_fenix
            "Gall" -> return R.drawable.pick_slot_gall
            "Garrosh" -> return R.drawable.pick_slot_garrosh
            "Gazlowe" -> return R.drawable.pick_slot_gazlowe
            "Genji" -> return R.drawable.pick_slot_genji
            "Greymane" -> return R.drawable.pick_slot_greymane
            "Guldan" -> return R.drawable.pick_slot_guldan
            "Hanzo" -> return R.drawable.pick_slot_hanzo
            "Hogger" -> return R.drawable.pick_slot_hogger
            "Illidan" -> return R.drawable.pick_slot_illidan
            "Imperius" -> return R.drawable.pick_slot_imperius
            "Jaina" -> return R.drawable.pick_slot_jaina
            "Johanna" -> return R.drawable.pick_slot_johanna
            "Junkrat" -> return R.drawable.pick_slot_junkrat
            "Kaelthas" -> return R.drawable.pick_slot_kaelthas
            "Kelthuzad" -> return R.drawable.pick_slot_kelthuzad
            "Kerrigan" -> return R.drawable.pick_slot_kerrigan
            "Kharazim" -> return R.drawable.pick_slot_kharazim
            "Leoric" -> return R.drawable.pick_slot_leoric
            "Lili" -> return R.drawable.pick_slot_lili
            "Li-Ming" -> return R.drawable.pick_slot_liming
            "LtMorales" -> return R.drawable.pick_slot_ltmorales
            "Lucio" -> return R.drawable.pick_slot_lucio
            "Lunara" -> return R.drawable.pick_slot_lunara
            "Maiev" -> return R.drawable.pick_slot_maiev
            "Malfurion" -> return R.drawable.pick_slot_malfurion
            "Malganis" -> return R.drawable.pick_slot_malghanis
            "Malthael" -> return R.drawable.pick_slot_malthael
            "Medivh" -> return R.drawable.pick_slot_medhiv
            "Mei" -> return R.drawable.pick_slot_mei
            "Mephisto" -> return R.drawable.pick_slot_mephisto
            "Muradin" -> return R.drawable.pick_slot_muradin
            "Murky" -> return R.drawable.pick_slot_murky
            "Nazeebo" -> return R.drawable.pick_slot_nazeebo
            "Nova" -> return R.drawable.pick_slot_nova
            "Orphea" -> return R.drawable.pick_slot_orphea
            "Probius" -> return R.drawable.pick_slot_probius
            "Qhira" -> return R.drawable.pick_slot_qhira
            "Ragnaros" -> return R.drawable.pick_slot_ragnaros
            "Raynor" -> return R.drawable.pick_slot_raynor
            "Rehgar" -> return R.drawable.pick_slot_rehgar
            "Rexxar" -> return R.drawable.pick_slot_rexxar
            "Samuro" -> return R.drawable.pick_slot_samuro
            "SgtHammer" -> return R.drawable.pick_slot_sgthammer
            "Sonya" -> return R.drawable.pick_slot_sonya
            "Stitches" -> return R.drawable.pick_slot_stitches
            "Stukov" -> return R.drawable.pick_slot_stukov
            "Sylvanas" -> return R.drawable.pick_slot_sylvanas
            "Tassadar" -> return R.drawable.pick_slot_tassadar
            "Butcher" -> return R.drawable.pick_slot_the_butcher
            "LostVikings" -> return R.drawable.pick_slot_the_vikings
            "Thrall" -> return R.drawable.pick_slot_thrall
            "Tracer" -> return R.drawable.pick_slot_tracer
            "Tychus" -> return R.drawable.pick_slot_tychus
            "Tyrael" -> return R.drawable.pick_slot_tyrael
            "Tyrande" -> return R.drawable.pick_slot_tyrande
            "Uther" -> return R.drawable.pick_slot_uther
            "Valeera" -> return R.drawable.pick_slot_valeera
            "Valla" -> return R.drawable.pick_slot_valla
            "Varian" -> return R.drawable.pick_slot_varian
            "Whitemane" -> return R.drawable.pick_slot_whitemane
            "Xul" -> return R.drawable.pick_slot_xul
            "Yrel" -> return R.drawable.pick_slot_yrel
            "Zagara" -> return R.drawable.pick_slot_zagara
            "Zeratul" -> return R.drawable.pick_slot_zeratul
            "Zarya" -> return R.drawable.pick_slot_zarya
            "Zuljin" -> return R.drawable.pick_slot_zuljin


            else -> return null
        }
    }

    fun mapChampNameToBannedPortraitDrawable(champName: String): Int? {
        when (champName) {
            "Abathur" -> return R.drawable.banned_abathur
            "Alarak" -> return R.drawable.banned_alarak
            "Alexstrasza" -> return R.drawable.banned_alexstrasza
            "Ana" -> return R.drawable.banned_ana
            "Anduin" -> return R.drawable.banned_anduin
            "Anubarak" -> return R.drawable.banned_anubarak
            "Artanis" -> return R.drawable.banned_artanis
            "Arthas" -> return R.drawable.banned_arthas
            "Auriel" -> return R.drawable.banned_auriel
            "Azmodan" -> return R.drawable.banned_azmodan
            "Blaze" -> return R.drawable.banned_blaze
            "Brightwing" -> return R.drawable.banned_brightwing
            "Cassia" -> return R.drawable.banned_cassia
            "Chen" -> return R.drawable.banned_chen
            "Cho" -> return R.drawable.banned_cho
            //TODO separate chogall?
            "Chogall" -> return R.drawable.banned_gall
            "Chromie" -> return R.drawable.banned_chromie
            "Deathwing" -> return R.drawable.banned_deathwing
            "Deckard" -> return R.drawable.banned_deckard
            "Dehaka" -> return R.drawable.banned_dehaka
            "Diablo" -> return R.drawable.banned_diablo
            "DVA" -> return R.drawable.banned_dva
            "ETC" -> return R.drawable.banned_etc
            "Falstad" -> return R.drawable.banned_falstad
            "Fenix" -> return R.drawable.banned_fenix
            "Gall" -> return R.drawable.banned_gall
            "Garrosh" -> return R.drawable.banned_garrosh
            "Gazlowe" -> return R.drawable.banned_gazlowe
            "Genji" -> return R.drawable.banned_genji
            "Greymane" -> return R.drawable.banned_greymane
            "Guldan" -> return R.drawable.banned_guldan
            "Hanzo" -> return R.drawable.banned_hanzo
            "Hogger" -> return R.drawable.banned_hogger
            "Illidan" -> return R.drawable.banned_illidan
            "Imperius" -> return R.drawable.banned_imperius
            "Jaina" -> return R.drawable.banned_jaina
            "Johanna" -> return R.drawable.banned_johanna
            "Junkrat" -> return R.drawable.banned_junkrat
            "Kaelthas" -> return R.drawable.banned_kaelthas
            "Kelthuzad" -> return R.drawable.banned_kelthuzad
            "Kerrigan" -> return R.drawable.banned_kerrigan
            "Kharazim" -> return R.drawable.banned_khrazim
            "Leoric" -> return R.drawable.banned_leoric
            "Lili" -> return R.drawable.banned_lili
            "Li-Ming" -> return R.drawable.banned_liming
            "LtMorales" -> return R.drawable.banned_ltmorales
            "Lucio" -> return R.drawable.banned_lucio
            "Lunara" -> return R.drawable.banned_lunara
            "Maiev" -> return R.drawable.banned_maiev
            "Malfurion" -> return R.drawable.banned_malfurion
            "Malganis" -> return R.drawable.banned_malganis
            "Malthael" -> return R.drawable.banned_malthael
            "Medivh" -> return R.drawable.banned_medivh
            "Mei" -> return R.drawable.banned_mei
            "Mephisto" -> return R.drawable.banned_mephisto
            "Muradin" -> return R.drawable.banned_muradin
            "Murky" -> return R.drawable.banned_murky
            "Nazeebo" -> return R.drawable.banned_nazeebo
            "Nova" -> return R.drawable.banned_nova
            "Orphea" -> return R.drawable.banned_orphea
            "Probius" -> return R.drawable.banned_probius
            "Qhira" -> return R.drawable.banned_qhira
            "Ragnaros" -> return R.drawable.banned_ragnaros
            "Raynor" -> return R.drawable.banned_raynor
            "Rehgar" -> return R.drawable.banned_rehgar
            "Rexxar" -> return R.drawable.banned_rexxar
            "Samuro" -> return R.drawable.banned_samuro
            "SgtHammer" -> return R.drawable.banned_sgthammer
            "Sonya" -> return R.drawable.banned_sonya
            "Stitches" -> return R.drawable.banned_stitches
            "Stukov" -> return R.drawable.banned_stukov
            "Sylvanas" -> return R.drawable.banned_sylvanas
            "Tassadar" -> return R.drawable.banned_tassadar
            "Butcher" -> return R.drawable.banned_thebutcher
            "LostVikings" -> return R.drawable.banned_thelostvikings
            "Thrall" -> return R.drawable.banned_thrall
            "Tracer" -> return R.drawable.banned_tracer
            "Tychus" -> return R.drawable.banned_tychus
            "Tyrael" -> return R.drawable.banned_tyrael
            "Tyrande" -> return R.drawable.banned_tyrande
            "Uther" -> return R.drawable.banned_uther
            "Valeera" -> return R.drawable.banned_valeera
            "Valla" -> return R.drawable.banned_valla
            "Varian" -> return R.drawable.banned_varian
            "Whitemane" -> return R.drawable.banned_whitemane
            "Xul" -> return R.drawable.banned_xul
            "Yrel" -> return R.drawable.banned_yrel
            "Zagara" -> return R.drawable.banned_zagara
            "Zeratul" -> return R.drawable.banned_zeratul
            "Zarya" -> return R.drawable.banned_zarya
            "Zuljin" -> return R.drawable.banned_zuljin


            else -> return null
        }
    }

    fun mapChampNameToStringRessource(champName: String): Int? {
        when (champName) {
            "Abathur" -> return R.string.champ_name_abathur
            "Alarak" -> return R.string.champ_name_Alarak
            "Alexstrasza" -> return R.string.champ_name_Alexstrasza
            "Ana" -> return R.string.champ_name_Ana
            "Anduin" -> return R.string.champ_name_Anduin
            "Anubarak" -> return R.string.champ_name_Anubarak
            "Artanis" -> return R.string.champ_name_Artanis
            "Arthas" -> return R.string.champ_name_Arthas
            "Auriel" -> return R.string.champ_name_Auriel
            "Azmodan" -> return R.string.champ_name_Azmodan
            "Blaze" -> return R.string.champ_name_Blaze
            "Brightwing" -> return R.string.champ_name_Brightwing
            "Cassia" -> return R.string.champ_name_Cassia
            "Chen" -> return R.string.champ_name_Chen
            "Cho" -> return R.string.champ_name_Cho
            "Chogall" -> return R.string.champ_name_Chogall
            "Chromie" -> return R.string.champ_name_Chromie
            "Deathwing" -> return R.string.champ_name_Deathwing
            "Deckard" -> return R.string.champ_name_Deckard
            "Dehaka" -> return R.string.champ_name_Dehaka
            "Diablo" -> return R.string.champ_name_Diablo
            "DVA" -> return R.string.champ_name_DVA
            "ETC" -> return R.string.champ_name_ETC
            "Falstad" -> return R.string.champ_name_Falstad
            "Fenix" -> return R.string.champ_name_Fenix
            "Gall" -> return R.string.champ_name_Gall
            "Garrosh" -> return R.string.champ_name_Garrosh
            "Gazlowe" -> return R.string.champ_name_Gazlowe
            "Genji" -> return R.string.champ_name_Genji
            "Greymane" -> return R.string.champ_name_Greymane
            "Guldan" -> return R.string.champ_name_Guldan
            "Hanzo" -> return R.string.champ_name_Hanzo
            "Hogger" -> return R.string.champ_name_Hogger
            "Illidan" -> return R.string.champ_name_Illidan
            "Imperius" -> return R.string.champ_name_Imperius
            "Jaina" -> return R.string.champ_name_Jaina
            "Johanna" -> return R.string.champ_name_Johanna
            "Junkrat" -> return R.string.champ_name_Junkrat
            "Kaelthas" -> return R.string.champ_name_Kaelthas
            "Kelthuzad" -> return R.string.champ_name_Kelthuzad
            "Kerrigan" -> return R.string.champ_name_Kerrigan
            "Kharazim" -> return R.string.champ_name_Kharazim
            "Leoric" -> return R.string.champ_name_Leoric
            "Lili" -> return R.string.champ_name_Lili
            "Li-Ming" -> return R.string.champ_name_Li_Ming
            "LtMorales" -> return R.string.champ_name_LtMorales
            "Lucio" -> return R.string.champ_name_Lucio
            "Lunara" -> return R.string.champ_name_Lunara
            "Maiev" -> return R.string.champ_name_Maiev
            "Malfurion" -> return R.string.champ_name_Malfurion
            "Malganis" -> return R.string.champ_name_Malganis
            "Malthael" -> return R.string.champ_name_Malthael
            "Medivh" -> return R.string.champ_name_Medivh
            "Mei" -> return R.string.champ_name_Mei
            "Mephisto" -> return R.string.champ_name_Mephisto
            "Muradin" -> return R.string.champ_name_Muradin
            "Murky" -> return R.string.champ_name_Murky
            "Nazeebo" -> return R.string.champ_name_Nazeebo
            "Nova" -> return R.string.champ_name_Nova
            "Orphea" -> return R.string.champ_name_Orphea
            "Probius" -> return R.string.champ_name_Probius
            "Qhira" -> return R.string.champ_name_Qhira
            "Ragnaros" -> return R.string.champ_name_Ragnaros
            "Raynor" -> return R.string.champ_name_Raynor
            "Rehgar" -> return R.string.champ_name_Rehgar
            "Rexxar" -> return R.string.champ_name_Rexxar
            "Samuro" -> return R.string.champ_name_Samuro
            "SgtHammer" -> return R.string.champ_name_SgtHammer
            "Sonya" -> return R.string.champ_name_Sonya
            "Stitches" -> return R.string.champ_name_Stitches
            "Stukov" -> return R.string.champ_name_Stukov
            "Sylvanas" -> return R.string.champ_name_Sylvanas
            "Tassadar" -> return R.string.champ_name_Tassadar
            "Butcher" -> return R.string.champ_name_Butcher
            "LostVikings" -> return R.string.champ_name_LostVikings
            "Thrall" -> return R.string.champ_name_Thrall
            "Tracer" -> return R.string.champ_name_Tracer
            "Tychus" -> return R.string.champ_name_Tychus
            "Tyrael" -> return R.string.champ_name_Tyrael
            "Tyrande" -> return R.string.champ_name_Tyrande
            "Uther" -> return R.string.champ_name_Uther
            "Valeera" -> return R.string.champ_name_Valeera
            "Valla" -> return R.string.champ_name_Valla
            "Varian" -> return R.string.champ_name_Varian
            "Whitemane" -> return R.string.champ_name_Whitemane
            "Xul" -> return R.string.champ_name_Xul
            "Yrel" -> return R.string.champ_name_Yrel
            "Zagara" -> return R.string.champ_name_Zagara
            "Zeratul" -> return R.string.champ_name_Zeratul
            "Zarya" -> return R.string.champ_name_Zarya
            "Zuljin" -> return R.string.champ_name_Zuljin


            else -> return null
        }
    }

    fun mapChampToOrigin(champName: String): GameOrigin? {
        when (champName) {
            "Abathur" -> return GameOrigin.STARCRAFT
            "Alarak" -> return GameOrigin.STARCRAFT
            "Alexstrasza" -> return GameOrigin.WARCRAFT
            "Ana" -> return GameOrigin.OVERWATCH
            "Anduin" -> return GameOrigin.WARCRAFT
            "Anubarak" -> return GameOrigin.WARCRAFT
            "Artanis" -> return GameOrigin.STARCRAFT
            "Arthas" -> return GameOrigin.WARCRAFT
            "Auriel" -> return GameOrigin.DIABLO
            "Azmodan" -> return GameOrigin.DIABLO
            "Blaze" -> return GameOrigin.STARCRAFT
            "Brightwing" -> return GameOrigin.WARCRAFT
            "Cassia" -> return GameOrigin.DIABLO
            "Chen" -> return GameOrigin.WARCRAFT
            "Cho" -> return GameOrigin.WARCRAFT
            "Chogall" -> return GameOrigin.WARCRAFT
            "Chromie" -> return GameOrigin.WARCRAFT
            "Deathwing" -> return GameOrigin.WARCRAFT
            "Deckard" -> return GameOrigin.DIABLO
            "Dehaka" -> return GameOrigin.STARCRAFT
            "Diablo" -> return GameOrigin.DIABLO
            "DVA" -> return GameOrigin.OVERWATCH
            "ETC" -> return GameOrigin.WARCRAFT
            "Falstad" -> return GameOrigin.WARCRAFT
            "Fenix" -> return GameOrigin.STARCRAFT
            "Gall" -> return GameOrigin.WARCRAFT
            "Garrosh" -> return GameOrigin.WARCRAFT
            "Gazlowe" -> return GameOrigin.WARCRAFT
            "Genji" -> return GameOrigin.OVERWATCH
            "Greymane" -> return GameOrigin.WARCRAFT
            "Guldan" -> return GameOrigin.WARCRAFT
            "Hanzo" -> return GameOrigin.OVERWATCH
            "Hogger" -> return GameOrigin.WARCRAFT
            "Illidan" -> return GameOrigin.WARCRAFT
            "Imperius" -> return GameOrigin.DIABLO
            "Jaina" -> return GameOrigin.WARCRAFT
            "Johanna" -> return GameOrigin.DIABLO
            "Junkrat" -> return GameOrigin.OVERWATCH
            "Kaelthas" -> return GameOrigin.WARCRAFT
            "Kelthuzad" -> return GameOrigin.WARCRAFT
            "Kerrigan" -> return GameOrigin.STARCRAFT
            "Kharazim" -> return GameOrigin.DIABLO
            "Leoric" -> return GameOrigin.DIABLO
            "Lili" -> return GameOrigin.WARCRAFT
            "Li-Ming" -> return GameOrigin.DIABLO
            "LtMorales" -> return GameOrigin.STARCRAFT
            "Lucio" -> return GameOrigin.OVERWATCH
            "Lunara" -> return GameOrigin.WARCRAFT
            "Maiev" -> return GameOrigin.WARCRAFT
            "Malfurion" -> return GameOrigin.WARCRAFT
            "Malganis" -> return GameOrigin.WARCRAFT
            "Malthael" -> return GameOrigin.DIABLO
            "Medivh" -> return GameOrigin.WARCRAFT
            "Mei" -> return GameOrigin.OVERWATCH
            "Mephisto" -> return GameOrigin.DIABLO
            "Muradin" -> return GameOrigin.WARCRAFT
            "Murky" -> return GameOrigin.WARCRAFT
            "Nazeebo" -> return GameOrigin.DIABLO
            "Nova" -> return GameOrigin.STARCRAFT
            "Orphea" -> return GameOrigin.NEXUS
            "Probius" -> return GameOrigin.STARCRAFT
            "Qhira" -> return GameOrigin.NEXUS
            "Ragnaros" -> return GameOrigin.WARCRAFT
            "Raynor" -> return GameOrigin.STARCRAFT
            "Rehgar" -> return GameOrigin.WARCRAFT
            "Rexxar" -> return GameOrigin.WARCRAFT
            "Samuro" -> return GameOrigin.WARCRAFT
            "SgtHammer" -> return GameOrigin.STARCRAFT
            "Sonya" -> return GameOrigin.DIABLO
            "Stitches" -> return GameOrigin.WARCRAFT
            "Stukov" -> return GameOrigin.STARCRAFT
            "Sylvanas" -> return GameOrigin.WARCRAFT
            "Tassadar" -> return GameOrigin.STARCRAFT
            "Butcher" -> return GameOrigin.DIABLO
            "LostVikings" -> return GameOrigin.NEXUS
            "Thrall" -> return GameOrigin.WARCRAFT
            "Tracer" -> return GameOrigin.OVERWATCH
            "Tychus" -> return GameOrigin.STARCRAFT
            "Tyrael" -> return GameOrigin.DIABLO
            "Tyrande" -> return GameOrigin.WARCRAFT
            "Uther" -> return GameOrigin.WARCRAFT
            "Valeera" -> return GameOrigin.WARCRAFT
            "Valla" -> return GameOrigin.DIABLO
            "Varian" -> return GameOrigin.WARCRAFT
            "Whitemane" -> return GameOrigin.WARCRAFT
            "Xul" -> return GameOrigin.DIABLO
            "Yrel" -> return GameOrigin.WARCRAFT
            "Zagara" -> return GameOrigin.STARCRAFT
            "Zeratul" -> return GameOrigin.STARCRAFT
            "Zarya" -> return GameOrigin.OVERWATCH
            "Zuljin" -> return GameOrigin.WARCRAFT


            else -> return null
        }
    }

    fun mapRoleToImageRessource(role: RoleEnum): Int? {
        when (role) {
            RoleEnum.ranged -> return R.drawable.icon_ranged
            RoleEnum.support -> return R.drawable.support
            RoleEnum.melee -> return R.drawable.icon_melee
            RoleEnum.heal -> return R.drawable.icon_heiler
            RoleEnum.tank -> return R.drawable.tank
            RoleEnum.bruiser -> return R.drawable.icon_bruiser
        }
    }

    fun mapDifficultyToDrawable(difficulty: Difficulty): Int {
        return when (difficulty) {
            Difficulty.EASY -> R.drawable.difficulty_easy
            Difficulty.MEDIUM -> R.drawable.difficulty_medium
            Difficulty.HARD -> R.drawable.difficulty_hard
            Difficulty.EXTREME -> R.drawable.difficulty_extreme
        }
    }
}