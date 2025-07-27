package com.grandiamuhammad3096.assessment02.util

import android.content.Context
import com.grandiamuhammad3096.assessment02.database.KeuanganDb
import com.grandiamuhammad3096.assessment02.repository.FinancialRepository
import com.grandiamuhammad3096.assessment02.repository.FinancialRepositoryImpl

object ServiceLocator {
    fun provideRepository(context: Context): FinancialRepository {
        val db = KeuanganDb.getInstance(context)
        return FinancialRepositoryImpl(db.categoryDao(), db.transactionDao())
    }
}
