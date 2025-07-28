package com.grandiamuhammad3096.assessment02.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.grandiamuhammad3096.assessment02.R
import com.grandiamuhammad3096.assessment02.database.CategoryType
import com.grandiamuhammad3096.assessment02.database.Transaction
import com.grandiamuhammad3096.assessment02.navigasi.Screens
import com.grandiamuhammad3096.assessment02.ui.theme.Assessment02Theme
import com.grandiamuhammad3096.assessment02.util.MainViewModelFactory
import com.grandiamuhammad3096.assessment02.util.ServiceLocator
import com.grandiamuhammad3096.assessment02.util.ThemeViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val isDark by themeViewModel.isDark.collectAsState()
    val repo = ServiceLocator.provideRepository(context)
    val vm: MainViewModel = viewModel(
        factory = MainViewModelFactory(repo)
    )

    val income by vm.totalIncome.collectAsState()
    val expense by vm.totalExpense.collectAsState()
    val transactions by vm.recentTransactions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        if (isDark) {
                            Icon(Icons.Filled.Brightness7, contentDescription = "Light Mode")
                        } else {
                            Icon(Icons.Filled.Brightness4, contentDescription = "Dark Mode")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.Transaction.route)
                },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.tambah_transaction)
                )
            }
        }
    ) { innerPadding ->
        ScreenContent(
            Modifier.padding(innerPadding),
            income = income?.toInt() ?: 0,
            expense = expense?.toInt() ?: 0,
            transactions = transactions,
            onTransactionClick = { id ->
                navController.navigate(Screens.TransactionEdit.routeWithId(id))
            }
        )
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    income: Int,
    expense: Int,
    transactions: List<Transaction>,
    onTransactionClick: (Long) -> Unit
) {
    val balance = income - expense

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.ringkasan_keuangan),
            style = MaterialTheme.typography.titleLarge)

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.saldo))
                    Spacer(Modifier.padding(end = 68.dp))
                    Text(text = stringResource(R.string.rupiah, formatRupiah(balance)))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.pemasukan_satuan))
                    Spacer(Modifier.padding(end = 22.dp))
                    Text(text = stringResource(R.string.rupiah, formatRupiah(income)))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.pengeluaran_satuan))
                    Spacer(Modifier.padding(end = 16.dp))
                    Text(text = stringResource(R.string.rupiah, formatRupiah(expense)))
                }
            }
        }

        Text(text = stringResource(R.string.catatan_terbaru),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp))

        if (transactions.isEmpty()) {
            Column (
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.list_kosong))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(transactions) { tx ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = "${tx.date}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                        )},
                        supportingContent = {
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (tx.type == CategoryType.EXPENSE)
                                    stringResource(R.string.pengeluaran) else stringResource(R.string.pemasukan),
                                    modifier = Modifier.weight(2.7f), maxLines = 1
                                )
                                Text(text = tx.note ?: stringResource(R.string.tanpa_catatan),
                                    modifier = Modifier.weight(2.5f).padding(start = 16.dp),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "Rp. ",
                                    style = MaterialTheme.typography.bodyMedium)
                                Text(text = formatRupiah(tx.amount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(2.5f)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTransactionClick(tx.id)
                            },
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

fun formatRupiah(amount: Number): String {
    val format = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return format.format(amount)
}

// Khusus Untuk Privew Content!
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContentPreview(navController: NavController) {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(context))
    val isDark by themeViewModel.isDark.collectAsState()
    val repo = ServiceLocator.provideRepository(context)
    val vm: MainViewModel = viewModel(
        factory = MainViewModelFactory(repo)
    )

    val income by vm.totalIncome.collectAsState()
    val expense by vm.totalExpense.collectAsState()
    val transactions by vm.recentTransactions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {  }) {
                        if (isDark) {
                            Icon(Icons.Filled.Brightness7, contentDescription = "Light Mode")
                        } else {
                            Icon(Icons.Filled.Brightness4, contentDescription = "Dark Mode")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.Transaction.route)
                },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.tambah_transaction)
                )
            }
        }
    ) { innerPadding ->
        ScreenContent(
            Modifier.padding(innerPadding),
            income = income?.toInt() ?: 0,
            expense = expense?.toInt() ?: 0,
            transactions = transactions,
            onTransactionClick = { id ->
                navController.navigate(Screens.TransactionEdit.routeWithId(id))
            }
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assessment02Theme {
        MainScreenContentPreview(rememberNavController())
    }
}