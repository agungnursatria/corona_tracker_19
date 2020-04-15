package tech.awesome.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
data class Country(
    @PrimaryKey
    var name: String = "",
    var countryName: String = "",
    var flag: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var provinceName: String? = null
)