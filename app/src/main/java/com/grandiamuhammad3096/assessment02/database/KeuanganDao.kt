package com.grandiamuhammad3096.assessment02.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM `Transaction` ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE type = :type ORDER BY date DESC")
    fun getByType(type: CategoryType): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM `Transaction` WHERE type = :type")
    fun getTotalAmountByType(type: CategoryType): Flow<Double?>

    @Delete
    suspend fun delete(transaction: Transaction)
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Query("SELECT * FROM Category ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE type = :type ORDER BY name ASC")
    fun getByType(type: CategoryType): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE id = :id")
    suspend fun getById(id: Int): Category?

    @Delete
    suspend fun delete(category: Category)
}