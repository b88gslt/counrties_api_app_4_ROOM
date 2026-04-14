package com.example.hm_third_count.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val countryCode: String,
    /** Serialized `Country` JSON so favorites are visible without network. */
    val countrySnapshotJson: String? = null
)
