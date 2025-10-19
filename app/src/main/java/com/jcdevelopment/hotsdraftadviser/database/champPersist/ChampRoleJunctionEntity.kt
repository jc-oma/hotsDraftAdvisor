package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "champ_role_junction",
    // Primärschlüssel, um doppelte Rollenzuweisungen zu verhindern
    primaryKeys = ["champName", "roleName"],
    foreignKeys = [
        ForeignKey(
            entity = ChampEntity::class,
            parentColumns = ["ChampName"],
            childColumns = ["champName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoleEntity::class,
            parentColumns = ["roleName"],
            childColumns = ["roleName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("champName"), Index("roleName")]
)
data class ChampRoleJunctionEntity(
    val champName: String,
    val roleName: String // Hier speichern wir den Namen des Enums als String
)