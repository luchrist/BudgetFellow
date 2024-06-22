package de.christcoding.budgetfellow.views

import AutoCompleteTextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.ui.theme.Destructive
import de.christcoding.budgetfellow.utils.DateUtils
import de.christcoding.budgetfellow.viewmodels.AddOrEditTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditIncomeOrExpense(
    mode: TransactionMode,
    transaction: TransactionDetails? = null,
    specificViewModel: AddOrEditTransactionViewModel
) {
    //val specificViewModel: AddOrEditTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    specificViewModel.setEditableTransaction(transaction, mode)
    val context = LocalContext.current
    val state = specificViewModel.state
    var datePickerVisibility by remember {
        mutableStateOf(false)
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val addOrSave =
        if (transaction != null) stringResource(R.string.save) else stringResource(R.string.add)

    LaunchedEffect(key1 = context) {
        specificViewModel.validationEvents.collect { event ->
            when (event) {
                is ValidationEvent.Success -> {
                    specificViewModel.handleSubmit(mode)
                }
                is ValidationEvent.AllSuccess -> {
                    specificViewModel.handleAllSubmit(mode)
                }
            }
        }
    }
    OutlinedTextField(
        value = specificViewModel.transactionName,
        onValueChange = { specificViewModel.transactionName = it },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        label = { Text(text = stringResource(R.string.name)) })
    AutoCompleteTextView(
        elements = specificViewModel.categoryNames.collectAsState().value,
        currentElement = specificViewModel.selectedCategoryName,
        onElementChanged = {
            specificViewModel.selectedCategoryName = it
            specificViewModel.onEvent(AddTransactionEvent.OnCategoryChanged(it))
        },
        title = stringResource(R.string.category),
        error = specificViewModel.state.catError
    )
    OutlinedTextField(
        value = specificViewModel.transactionDescription,
        shape = RoundedCornerShape(16.dp),
        onValueChange = { specificViewModel.transactionDescription = it },
        minLines = 3,
        label = { Text(text = stringResource(R.string.description)) })

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Column {
            OutlinedTextField(
                value = specificViewModel.amount,
                shape = RoundedCornerShape(16.dp),
                onValueChange = {
                    specificViewModel.amount = it
                    specificViewModel.onEvent(AddTransactionEvent.OnAmountChanged(it))
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                isError = state.amountError != null,
                label = { Text(text = stringResource(R.string.amount)) })
            if (state.amountError != null) {
                Text(text = state.amountError, color = MaterialTheme.colorScheme.error)
            }
        }
        OutlinedButton(modifier = Modifier.padding(top = 10.dp), onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.euro),
                contentDescription = "currency"
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(onClick = { datePickerVisibility = true }) {
            Text(text = specificViewModel.datePicked.toString(), fontSize = 16.sp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.recurring)
            )
            Switch(checked = specificViewModel.recurring, onCheckedChange = {
                specificViewModel.recurring = it
            })
        }
        if (specificViewModel.recurring) {
            Row() {
                OutlinedTextField(
                    value = specificViewModel.recurringPeriod,
                    onValueChange = {
                        specificViewModel.recurringPeriod = it
                        specificViewModel.onEvent(AddTransactionEvent.OnPeriodChanged(it))
                    },
                    isError = state.periodError != null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(0.2f)
                )
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }) {
                    OutlinedTextField(
                        value = DateUtils.getPluralUnit(
                            context,
                            specificViewModel.periodUnit,
                            specificViewModel.recurringPeriod
                        ),
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.date_range),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = isExpanded
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.menuAnchor()
                    )
                    Box {
                        ExposedDropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text =
                                        if (specificViewModel.recurringPeriod.toInt() == 1)
                                            stringResource(R.string.day)
                                        else
                                            stringResource(R.string.days)
                                    )
                                },
                                onClick = {
                                    specificViewModel.periodUnit =
                                        context.resources.getString(R.string.day)
                                    isExpanded = false
                                })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (specificViewModel.recurringPeriod.toInt() == 1) stringResource(
                                            R.string.week
                                        ) else stringResource(
                                            R.string.weeks
                                        )
                                    )
                                },
                                onClick = {
                                    specificViewModel.periodUnit =
                                        context.resources.getString(R.string.week)
                                    isExpanded = false
                                })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (specificViewModel.recurringPeriod.toInt() == 1) stringResource(
                                            R.string.month
                                        ) else stringResource(
                                            R.string.months
                                        )
                                    )
                                },
                                onClick = {
                                    specificViewModel.periodUnit =
                                        context.resources.getString(R.string.month)
                                    isExpanded = false
                                })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (specificViewModel.recurringPeriod.toInt() == 1) stringResource(
                                            R.string.year
                                        ) else stringResource(
                                            R.string.years
                                        )
                                    )
                                },
                                onClick = {
                                    specificViewModel.periodUnit =
                                        context.resources.getString(R.string.year)
                                    isExpanded = false
                                })
                        }
                    }
                }
            }
            if (state.periodError != null) {
                Text(
                    text = state.periodError,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row (horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { specificViewModel.onEvent(AddTransactionEvent.OnAddClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "add income")
                Text(text = addOrSave, fontSize = 18.sp)
            }
            if (transaction != null && transaction.recurring) {
                OutlinedButton(onClick = { specificViewModel.onEvent(AddTransactionEvent.OnSaveAllClicked) }) {
                    Icon(Icons.Default.Add, contentDescription = "save all recurring transactions")
                    Text(text = "Save for all ongoing", fontSize = 18.sp)
                }
            }
        }
        if (transaction != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { specificViewModel.onEvent(AddTransactionEvent.OnDeleteClicked) },
                    colors = ButtonColors(
                        containerColor = Destructive,
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "delete")
                    Text(text = "This", fontSize = 18.sp)
                }
                if (transaction.recurring) {
                    Button(
                        onClick = {
                            specificViewModel.onEvent(AddTransactionEvent.OnDeleteRecurringClicked)
                        },
                        colors = ButtonColors(
                            containerColor = Destructive,
                            contentColor = Color.White,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "delete")
                        Text(text = "All Recurring", fontSize = 18.sp)
                    }
                }
            }
        }
    }

    if (datePickerVisibility) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }

        DatePickerDialog(onDismissRequest = { datePickerVisibility = false }, confirmButton = {
            TextButton(onClick = {
                datePickerVisibility = false
                var date = "No Selection"
                if (datePickerState.selectedDateMillis != null) {
                    date = DateUtils.convertMillisToDate(datePickerState.selectedDateMillis!!)
                }
                specificViewModel.datePicked = DateUtils.convertStringToDate(date)
            }, enabled = confirmEnabled.value) {
                Text(text = stringResource(R.string.okay))
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }
    if (specificViewModel.rowsUpdated > 0) {
        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
        specificViewModel.rowsUpdated = -1
    } else if (specificViewModel.rowsUpdated == 0) {
        Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show()
        specificViewModel.rowsUpdated = -1
    }
    if (specificViewModel.id > -1L) {
        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
        specificViewModel.id = -2L
    } else if (specificViewModel.id == -1L) {
        Toast.makeText(context, "Adding Failed", Toast.LENGTH_SHORT).show()
        specificViewModel.id = -2L
    }
}