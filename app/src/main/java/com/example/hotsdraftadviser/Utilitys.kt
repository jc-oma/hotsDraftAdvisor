package com.example.hotsdraftadviser

class Utilitys {
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
            else -> return null
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

            else -> return null
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
            "Orphea" -> return R.drawable.orphea_card_portrait
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
            "Butcher" -> return R.drawable.thebutcher_card_portrait
            "LostVikings" -> return R.drawable.thelostvikings_card_portrait
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
            "Zeratul" -> return R.drawable.zeratul_card_portrait
            "Zarya" -> return R.drawable.zarya_card_portrait
            "Zuljin" -> return R.drawable.zuljin_card_portrait


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
}