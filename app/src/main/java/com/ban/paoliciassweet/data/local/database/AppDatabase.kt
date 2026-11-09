package com.ban.paoliciassweet.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ban.paoliciassweet.data.local.dao.ReservaDao
import com.ban.paoliciassweet.data.local.dao.SaldoDao
import com.ban.paoliciassweet.data.local.dao.UserDao
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.local.entity.ReservaEntity
import com.ban.paoliciassweet.data.local.entity.SaldoEntity
import com.ban.paoliciassweet.data.local.entity.UserEntity
import com.ban.paoliciassweet.data.local.dao.ProductoDao

@Database(
    entities = [
        UserEntity::class,
        ReservaEntity::class,
        SaldoEntity::class,
        ProductoEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reservaDao(): ReservaDao
    abstract fun saldoDao(): SaldoDao
    abstract fun productoDao(): ProductoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Myapp_databse.db"
                )
                    // Al subir de versión se recrea la base (solo para desarrollo)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}