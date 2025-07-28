package com.grandiamuhammad3096.assessment02.repository

import com.grandiamuhammad3096.assessment02.database.Category
import com.grandiamuhammad3096.assessment02.database.CategoryType
import com.grandiamuhammad3096.assessment02.database.Transaction
import com.grandiamuhammad3096.assessment02.database.CategoryDao
import com.grandiamuhammad3096.assessment02.database.TransactionDao
import kotlinx.coroutines.flow.Flow

/**
 * FinancialRepository defines abstract operations for managing
 * categories and transactions in the application.
 */
interface FinancialRepository {
    // Category operations
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    fun getAllCategories(): Flow<List<Category>>

    // Transaction operations
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByType(type: CategoryType): Flow<List<Transaction>>
    fun getTotalAmountByType(type: CategoryType): Flow<Double?>
}

/**
 * Default implementation of FinancialRepository, using Room DAOs
 * to perform all database operations.
 */
class FinancialRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) : FinancialRepository {

    // Category implementations
    override suspend fun insertCategory(category: Category) =
        categoryDao.insert(category)

    override suspend fun deleteCategory(category: Category) =
        categoryDao.delete(category)

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> =
        categoryDao.getByType(type)

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories()

    // Transaction implementations
    override suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insert(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()

    override fun getTransactionsByType(type: CategoryType): Flow<List<Transaction>> =
        transactionDao.getByType(type)

    override fun getTotalAmountByType(type: CategoryType): Flow<Double?> =
        transactionDao.getTotalAmountByType(type)
}

