package com.grandiamuhammad3096.assessment02.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grandiamuhammad3096.assessment02.repository.FinancialRepository
import com.grandiamuhammad3096.assessment02.ui.screen.TransactionViewModel

class TransactionViewModelFactory(
    private val repo: FinancialRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}