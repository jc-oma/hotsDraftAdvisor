package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jcdevelopment.hotsdraftadviser.RoleEnum
import com.jcdevelopment.hotsdraftadviser.dataclsasses.GoodTeamWith
import com.jcdevelopment.hotsdraftadviser.dataclsasses.MapScoreData
import com.jcdevelopment.hotsdraftadviser.dataclsasses.StrongAgainstData
import com.jcdevelopment.hotsdraftadviser.dataclsasses.WeakAgainstData

@Entity(tableName = "champions")
data class ChampEntity(
    @PrimaryKey var key: Int = 0,
    val ChampName: String,
    val ChampRole: List<String>,
    val ChampRoleAlt: List<RoleEnum>,
    val StrongAgainst: List<StrongAgainstData>,
    val WeakAgainst: List<WeakAgainstData>,
    val GoodTeamWith: List<GoodTeamWith>,
    val MapScore: List<MapScoreData>,
)