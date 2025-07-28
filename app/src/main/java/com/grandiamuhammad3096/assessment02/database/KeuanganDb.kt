package com.grandiamuhammad3096.assessment02.database

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.grandiamuhammad3096.assessment02.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [Category::class, Transaction::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class KeuanganDb : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        // Volatile memastikan perubahan pada INSTANCE langsung terlihat ke semua thread
        @Volatile
        private var INSTANCE: KeuanganDb? = null

        /**
         * Mendapatkan instance AppDatabase tunggal.
         * synchronized untuk memastikan hanya satu thread yang membuat instance.
         */
        fun getInstance(context: Context): KeuanganDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeuanganDb::class.java,
                    "keuangan.db"             // nama file database
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate di background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).categoryDao().apply {
                                    // Expense kategori
                                    insert(Category(name = context.getString(R.string.kategori_makanan), type = CategoryType.EXPENSE))
                                    insert(Category(name = context.getString(R.string.kategori_transportasi), type = CategoryType.EXPENSE))
                                    insert(Category(name = context.getString(R.string.kategori_kesehatan), type = CategoryType.EXPENSE))
                                    insert(Category(name = context.getString(R.string.kategori_belanja), type = CategoryType.EXPENSE))
                                    insert(Category(name = context.getString(R.string.kategori_hiburan), type = CategoryType.EXPENSE))
                                    // Income Kategori
                                    insert(Category(name = context.getString(R.string.kategori_gaji), type = CategoryType.INCOME))
                                    insert(Category(name = context.getString(R.string.kategori_bonus), type = CategoryType.INCOME))
                                    insert(Category(name = context.getString(R.string.kategori_hadiah), type = CategoryType.INCOME))
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter
    fun fromDate(date: LocalDate): String = date.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toDate(dateString: String): LocalDate = LocalDate.parse(dateString)
}