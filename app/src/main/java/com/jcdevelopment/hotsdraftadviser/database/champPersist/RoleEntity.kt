package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jcdevelopment.hotsdraftadviser.RoleEnum

@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey
    val roleName: RoleEnum
)