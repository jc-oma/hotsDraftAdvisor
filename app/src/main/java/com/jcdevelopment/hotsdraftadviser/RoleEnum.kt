package com.jcdevelopment.hotsdraftadviser

import kotlinx.serialization.Serializable

@Serializable
enum class RoleEnum {
    ranged,
    support,
    melee,
    heal,
    tank,
    bruiser
}