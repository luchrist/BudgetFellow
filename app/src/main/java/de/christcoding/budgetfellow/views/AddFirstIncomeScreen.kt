package de.christcoding.budgetfellow.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFirstIncomeScreen() {
    val context = LocalContext.current
    var datePickerVisibility by remember {
        mutableStateOf(false)
    }
    val dateLabel = stringResource(R.string.date)
    var datePicked by remember {
        mutableStateOf(dateLabel)
    }
    var recurring by remember {
        mutableStateOf(false)
    }
    var recurringPeriod by remember {
        mutableStateOf("1")
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var periodUnit by remember {
        mutableStateOf("Day")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.hi_i_am_your_budget_fellow_i_will_help_you_reach_your_financial_goals),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.let_s_start_by_adding_your_first_income),
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            label = { Text(text = stringResource(R.string.name_of_income)) })

        OutlinedTextField(
            value = "",
            shape = RoundedCornerShape(16.dp),
            onValueChange = {},
            minLines = 3,
            label = { Text(text = stringResource(R.string.description)) })

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = "",
                shape = RoundedCornerShape(16.dp),
                onValueChange = {},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                label = { Text(text = stringResource(R.string.value)) })
            OutlinedButton(modifier = Modifier.padding(top = 10.dp), onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.euro),
                    contentDescription = "currency"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { datePickerVisibility = true }) {
            Text(text = datePicked, fontSize = 16.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.recurring)
            )
            Switch(checked = recurring, onCheckedChange = {
                recurring = it
            })
        }
        if (recurring) {
            Row() {
                OutlinedTextField(
                    value = recurringPeriod, onValueChange = { recurringPeriod = it },
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
                            periodUnit,
                            recurringPeriod
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
                                        if (recurringPeriod.toInt() == 1)
                                            stringResource(R.string.day)
                                        else
                                            stringResource(R.string.days)
                                    )
                                },
                                onClick = {
                                    periodUnit = context.resources.getString(R.string.day)
                                    isExpanded = false
                                })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (recurringPeriod.toInt() == 1) stringResource(R.string.week) else stringResource(
                                            R.string.weeks
                                        )
                                    )
                                },
                                onClick = {
                                    periodUnit = context.resources.getString(R.string.week)
                                    isExpanded = false
                                })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (recurringPeriod.toInt() == 1) stringResource(R.string.month) else stringResource(
                                            R.string.months
                                        )
                                    )
                                },
                                onClick = {
                                    periodUnit = context.resources.getString(R.string.month)
                                    isExpanded = false
                                })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (recurringPeriod.toInt() == 1) stringResource(R.string.year) else stringResource(
                                            R.string.years
                                        )
                                    )
                                },
                                onClick = {
                                    periodUnit = context.resources.getString(R.string.year)
                                    isExpanded = false
                                })
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.Add, contentDescription = "add income")
            Text(text = stringResource(R.string.add), fontSize = 18.sp)
        }
    }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedButton(
            onClick = { /*TODO*/ },
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "next")
            Text(text = stringResource(R.string.skip))
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
                datePicked = date
            }, enabled = confirmEnabled.value) {
                Text(text = stringResource(R.string.okay))
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }
}
