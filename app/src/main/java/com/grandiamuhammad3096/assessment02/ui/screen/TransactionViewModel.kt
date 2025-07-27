package com.grandiamuhammad3096.assessment02.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grandiamuhammad3096.assessment02.database.CategoryType
import com.grandiamuhammad3096.assessment02.database.Transaction
import com.grandiamuhammad3096.assessment02.repository.FinancialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class TransactionViewModel (
    private val repository: FinancialRepository
) : ViewModel() {
    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction

    private val _categories = MutableStateFlow<List<com.grandiamuhammad3096.assessment02.database.Category>>(emptyList())
    val categories: StateFlow<List<com.grandiamuhammad3096.assessment02.database.Category>> = _categories
    fun loadCategories(type: CategoryType) {
        viewModelScope.launch {
            repository.getCategoriesByType(type).collect {
                _categories.value = it
            }
        }
    }

    fun loadTransactionById(id: Long) {
        viewModelScope.launch {
            val all = repository.getAllTransactions().firstOrNull() ?: emptyList()
            _transaction.value = all.find { it.id == id }
        }
    }
    fun addTransaction(
        amount: Double,
        date: LocalDate,
        type: CategoryType,
        categoryId: Int,
        note: String?
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    date = date,
                    type = type,
                    category = categoryId,
                    note = note
                )
            )
        }
    }

    fun updateTransaction(
        id: Long,
        amount: Double,
        date: LocalDate,
        type: CategoryType,
        categoryId: Int,
        note: String?
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    id = id,
                    amount = amount,
                    date = date,
                    type = type,
                    category = categoryId,
                    note = note
                )
            )
        }
    }

    fun removeTransactionById(id: Long) {
        viewModelScope.launch {
            val all = repository.getAllTransactions().firstOrNull() ?: emptyList()
            val trx = all.find { it.id == id }
            trx?.let { repository.deleteTransaction(it) }
        }
    }
}