package com.buntlit.pictureoftheday.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface FavoritesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(response: RoomFavoritesPictures)

    @Delete
    suspend fun delete(response: RoomFavoritesPictures)

    @Query("SELECT * FROM RoomFavoritesPictures")
    fun getLiveData() : LiveData<List<RoomFavoritesPictures>>

    @Query("SELECT * FROM RoomFavoritesPictures")
    suspend fun getAllSortDefault() : List<RoomFavoritesPictures>

    @Query("SELECT * FROM RoomFavoritesPictures ORDER BY date DESC")
    suspend fun getAllSortDate(): List<RoomFavoritesPictures>

    @Query("SELECT * FROM RoomFavoritesPictures ORDER BY title")
    suspend fun getAllSortName(): List<RoomFavoritesPictures>

    @Query("SELECT * FROM RoomFavoritesPictures ORDER BY mediaType DESC")
    suspend fun getAllSortType(): List<RoomFavoritesPictures>

    @Query("SELECT * FROM RoomFavoritesPictures WHERE date = :date LIMIT 1")
    fun getIfLiked(date: String): RoomFavoritesPictures

}