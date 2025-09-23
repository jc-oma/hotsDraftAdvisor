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
}