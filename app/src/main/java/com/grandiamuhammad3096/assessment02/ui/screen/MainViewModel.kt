package com.grandiamuhammad3096.assessment02.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grandiamuhammad3096.assessment02.database.CategoryType
import com.grandiamuhammad3096.assessment02.database.Transaction
import com.grandiamuhammad3096.assessment02.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * MainViewModel provides consolidated data for the main screen,
 * including total income, total expense, balance, and recent transactions.
 */

class MainViewModel(
    repository: FinancialRepository
) : ViewModel() {
    // Total income
    val totalIncome: StateFlow<Double?> = repository
        .getTotalAmountByType(CategoryType.INCOME)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Total expense
    val totalExpense: StateFlow<Double?> = repository
        .getTotalAmountByType(CategoryType.EXPENSE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Recent 5 transactions
    val recentTransactions: StateFlow<List<Transaction>> = repository
        .getAllTransactions()
        .map { list -> list.sortedByDescending { it.date }.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}