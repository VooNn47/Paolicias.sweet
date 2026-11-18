package com.ban.paoliciassweet.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ban.paoliciassweet.data.local.dao.ProductoDao
import com.ban.paoliciassweet.data.local.dao.ReservaDao
import com.ban.paoliciassweet.data.local.dao.UserDao
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.local.entity.ReservaEntity
import com.ban.paoliciassweet.data.local.entity.UserEntity

// SQLite local:
// - users:     login y registro de los dos roles (requisito de la rúbrica)
// - productos: copia del catálogo del backend para usar sin conexión
// - reservas:  tabla antigua; las reservas ahora viven en reservas-service (backend)
@Database(
    entities = [
        UserEntity::class,
        ReservaEntity::class,
        ProductoEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reservaDao(): ReservaDao
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

                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
