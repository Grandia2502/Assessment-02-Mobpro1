package com.grandiamuhammad3096.assessment02.ui.screen

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.grandiamuhammad3096.assessment02.R
import com.grandiamuhammad3096.assessment02.database.Category
import com.grandiamuhammad3096.assessment02.database.CategoryType
import com.grandiamuhammad3096.assessment02.ui.theme.Assessment02Theme
import com.grandiamuhammad3096.assessment02.util.ServiceLocator
import com.grandiamuhammad3096.assessment02.util.TransactionViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(
    navController: NavController,
    transactionId: Long? = null
) {
    val context = LocalContext.current
    val repo = ServiceLocator.provideRepository(context)
    val vm: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(repo)
    )

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var categoryType by remember { mutableStateOf(CategoryType.EXPENSE) }
    var categoryId by remember { mutableIntStateOf(-1) } // Default, nanti bisa dropdown
    var date by remember { mutableStateOf(LocalDate.now()) }

    val categories by vm.categories.collectAsState()
    LaunchedEffect(categoryType) {
        vm.loadCategories(categoryType)
    }

    // Jika edit, load data
    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            vm.loadTransactionById(transactionId)
        }
    }
    val trx by vm.transaction.collectAsState()
    LaunchedEffect(trx) {
        trx?.let {
            amount = it.amount.toString()
            note = it.note.orEmpty()
            categoryType = it.type
            categoryId = it.category
            date = it.date
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toEpochDay() * 24L * 60 * 60 * 1000
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        date = LocalDate.ofEpochDay(millis / (24L * 60 * 60 * 1000))
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.batal))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) { Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.kembali),
                        tint = MaterialTheme.colorScheme.primary
                    ) }
                },
                title =  {
                    Text(if (transactionId == null) stringResource(R.string.tambah_transaction)
                        else stringResource(R.string.edit_transaction)
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    if (transactionId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.hapus),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        TransactionForm(
            amount = amount,
            onAmountChange = { amount = it },
            note = note,
            onNoteChange = { note = it },
            categoryType = categoryType,
            onCategoryTypeChange = { categoryType = it },
            categoryId = categoryId,
            onCategoryIdChange = { categoryId = it },
            categories = categories,
            date = date,
            onShowDatePicker = { showDatePicker = true },
            onSave = {
                if (amount.isBlank() || categoryId == -1 || note.isBlank()) {
                    Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                    return@TransactionForm
                }
                if (transactionId == null) {
                    vm.addTransaction(
                        amount.toDoubleOrNull() ?: 0.0,
                        date,
                        categoryType,
                        categoryId,
                        note
                    )
                } else {
                    vm.updateTransaction(
                        transactionId,
                        amount.toDoubleOrNull() ?: 0.0,
                        date,
                        categoryType,
                        categoryId,
                        note
                    )
                }
                navController.popBackStack()
            },
            modifier = Modifier.padding(padding)
        )

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        transactionId?.let {
                            vm.removeTransactionById(it)
                            navController.popBackStack()
                        }
                        showDeleteDialog = false
                    }) {
                        Text(stringResource(R.string.hapus))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.batal))
                    }
                },
                title = { Text(stringResource(R.string.konfirmasi_hapus)) },
                text = { Text(stringResource(R.string.pesan_hapus)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionForm(
    amount: String,
    onAmountChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    categoryType: CategoryType,
    onCategoryTypeChange: (CategoryType) -> Unit,
    categoryId: Int,
    onCategoryIdChange: (Int) -> Unit,
    categories: List<Category>,
    date: LocalDate,
    onShowDatePicker: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    var expanded by remember { mutableStateOf(false) }
    LocalContext.current

    Column(
        modifier = modifier
        .padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(selectedTabIndex = if (categoryType == CategoryType.EXPENSE) 0 else 1) {
            Tab(selected = categoryType == CategoryType.EXPENSE,
                onClick = { onCategoryTypeChange(CategoryType.EXPENSE) },
                text = { Text(stringResource(R.string.pengeluaran)) })
            Tab(selected = categoryType == CategoryType.INCOME,
                onClick = { onCategoryTypeChange(CategoryType.INCOME) },
                text = { Text(stringResource(R.string.pemasukan)) })
        }
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date.format(formatter),
                onValueChange = {},
                label = { Text(stringResource(R.string.tanggal)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { onShowDatePicker() }) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.pilih_tanggal))
                    }
                }
            )
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { onShowDatePicker() }
            )
        }
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.jumlah)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text(stringResource(R.string.catatan)) },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = categories.firstOrNull { it.id == categoryId }?.name ?: stringResource(R.string.pilih_kategori),
                onValueChange = {},
                label = { Text(stringResource(R.string.kategori)) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            onCategoryIdChange(cat.id)
                            expanded = false
                        }
                    )
                }
            }
        }
        Button(
            onClick = onSave,

            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.simpan))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TransactionScreenPreview() {
    Assessment02Theme {
        TransactionScreen(
            rememberNavController()
        )
    }
}