package com.grandiamuhammad3096.assessment02.navigasi

sealed class Screens(val route: String) {
    data object Main : Screens("main")
    data object Transaction : Screens("transaction")
    data object TransactionEdit : Screens("transaction/{transactionId}") {
        fun routeWithId(id: Long): String = "transaction/$id"
    }
}