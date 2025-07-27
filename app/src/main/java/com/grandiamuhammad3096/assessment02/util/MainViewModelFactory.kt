package com.grandiamuhammad3096.assessment02.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grandiamuhammad3096.assessment02.repository.FinancialRepository
import com.grandiamuhammad3096.assessment02.ui.screen.MainViewModel

class MainViewModelFactory(
    private val repo: FinancialRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}