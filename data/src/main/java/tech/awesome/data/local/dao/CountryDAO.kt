package tech.awesome.data.local.dao

import androidx.room.*
import tech.awesome.data.local.entity.Country

@Dao
interface CountryDAO {

    @Query("select * from country where name like :name")
    suspend fun get(name: String): List<Country>

    @Query("select * from country")
    suspend fun get(): List<Country>

    @Query("DELETE FROM country")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg country: Country)

    @Query("delete from country where name like :name")
    suspend fun delete(name: String)

    @Update
    suspend fun update(vararg country: Country)

}