package com.buntlit.pictureoftheday.ui.favorite

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


@androidx.room.Database(
    entities = [RoomFavoritesPictures::class],
    version = 1,
    exportSchema = false
)
abstract class Database: RoomDatabase() {

    abstract val favoritesDAO: FavoritesDAO

    companion object {

        @Volatile
        private var INSTANCE: Database? = null

        private const val DB_NAME = "favorites.db"

        fun getDatabase(context: Context): Database{

            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = Database::class.java,
                    name = DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}