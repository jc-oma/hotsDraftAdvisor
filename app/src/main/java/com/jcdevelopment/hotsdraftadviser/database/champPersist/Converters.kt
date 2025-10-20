package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.TypeConverter
import com.jcdevelopment.hotsdraftadviser.RoleEnum
import com.jcdevelopment.hotsdraftadviser.dataclasses.GoodTeamWith
import com.jcdevelopment.hotsdraftadviser.dataclasses.MapScoreData
import com.jcdevelopment.hotsdraftadviser.dataclasses.StrongAgainstData
import com.jcdevelopment.hotsdraftadviser.dataclasses.WeakAgainstData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    // Konverter für einfache Listen von Strings
    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)

    // Konverter für Listen von Enums
    @TypeConverter
    fun fromRoleEnumList(value: List<RoleEnum>): String = Json.encodeToString(value)

    @TypeConverter
    fun toRoleEnumList(value: String): List<RoleEnum> = Json.decodeFromString(value)

    // Konverter für Listen von StrongAgainstData
    @TypeConverter
    fun fromStrongAgainstList(value: List<StrongAgainstData>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStrongAgainstList(value: String): List<StrongAgainstData> = Json.decodeFromString(value)

    // Konverter für Listen von WeakAgainstData
    @TypeConverter
    fun fromWeakAgainstList(value: List<WeakAgainstData>): String = Json.encodeToString(value)

    @TypeConverter
    fun toWeakAgainstList(value: String): List<WeakAgainstData> = Json.decodeFromString(value)

    // Konverter für Listen von GoodTeamWith
    @TypeConverter
    fun fromGoodTeamWithList(value: List<GoodTeamWith>): String = Json.encodeToString(value)

    @TypeConverter
    fun toGoodTeamWithList(value: String): List<GoodTeamWith> = Json.decodeFromString(value)

    // Konverter für Listen von MapScoreData
    @TypeConverter
    fun fromMapScoreList(value: List<MapScoreData>): String = Json.encodeToString(value)

    @TypeConverter
    fun toMapScoreList(value: String): List<MapScoreData> = Json.decodeFromString(value)
}