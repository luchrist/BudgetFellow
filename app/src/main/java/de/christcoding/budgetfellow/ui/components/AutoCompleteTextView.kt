import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.viewmodels.AddOrEditTransactionViewModel

@Composable
fun AutoCompleteTextView(vm: AddOrEditTransactionViewModel) {

    val categories by vm.categories.collectAsState()

    val heightTextFields by remember {
        mutableStateOf(55.dp)
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var expanded by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val state = vm.state

    // Category Field
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vm.selectedCategoryName,
                    onValueChange =
                    {
                        vm.selectedCategoryName = it
                        vm.onEvent(AddTransactionEvent.OnCategoryChanged(it))
                        expanded = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightTextFields)
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    shape = RoundedCornerShape(16.dp),
                    label = { Text(text = "Category") },
                    isError = state.catError != null,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "arrow",
                                tint = Color.White
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                    ) {

                        if (vm.selectedCategoryName.isNotEmpty()) {
                            items(
                                categories.filter {
                                    it.name.lowercase()
                                        .contains(vm.selectedCategoryName.lowercase())
                                }
                                    .sorted()
                            ) {
                                Category(title = it.name) { title ->
                                    vm.selectedCategoryName = title
                                    vm.onEvent(AddTransactionEvent.OnCategoryChanged(title))
                                    expanded = false
                                }
                            }
                        } else {
                            items(
                                categories.sorted()
                            ) {
                                Category(title = it.name) { title ->
                                    vm.selectedCategoryName = title
                                    vm.onEvent(AddTransactionEvent.OnCategoryChanged(title))
                                    expanded = false
                                }
                            }
                        }

                    }

                }
            }

            if(state.catError != null) {
                Text(text = state.catError, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            }
        }

    }
}

@Composable
fun Category(
    title: String,
    onSelect: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
            }
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }

}