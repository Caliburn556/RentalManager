package com.example.rentalmanager.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.rentalmanager.AuthViewModel
import com.example.rentalmanager.Property
import com.example.rentalmanager.R


@Composable
fun PropertiesScreen(navController: NavController, authViewModel: AuthViewModel) {
    val propertyList by authViewModel.propertyList.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF312D35))
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "List of Properties",
                fontSize = 25.sp,
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            IconButton(
                onClick = { showDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        if (propertyList.isEmpty()) {
            Text(
                text = " Your Property list is empty",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumnList(propertyList = propertyList, authViewModel = authViewModel)
        }
        if (showDialog) {
            AddPropertyDialog(
                onDismiss = { showDialog = false },
                onAdd = { property ->
                    authViewModel.addProperty(property)
                    showDialog = false
                }
            )
        }
    }
}


@Composable
fun LazyColumnList(propertyList: List<Property>, authViewModel: AuthViewModel) {
    LazyColumn(
        modifier = Modifier
            .padding(start = 14.dp)
            .clip(RoundedCornerShape(32.dp)),
        content = {
            itemsIndexed(propertyList) { index, item ->
                PropertyListItem(property = item, authViewModel = authViewModel)
            }
        }
    )
}


@Composable
fun PropertyListItem(property: Property, authViewModel: AuthViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PropertyIcon(property.houseType)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "House Number: ${property.houseNumber}",
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    tint = Color.White
                )
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                PropertyDetailItem(label = "Size: ", value = property.houseType)
                PropertyDetailItem(label = "Rent Amount: ", value = property.rentAmount.toString())
                PropertyDetailItem(label = "Meter Number: ", value = property.meterNumber.toString())
                IconButton(onClick = { authViewModel.deleteProperty(property.propertyId) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Icon",
                        modifier = Modifier.size(25.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyDetailItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.padding(end = 4.dp),
            color = Color.White
        )
        Text(
            text = value,
            color = Color.Gray
        )
    }
}

@Composable
fun AddPropertyDialog(onDismiss: () -> Unit, onAdd: (Property) -> Unit) {
    var houseNumber by remember { mutableStateOf("") }
    var houseType by remember { mutableStateOf("") }
    var rentAmount by remember { mutableStateOf("") }
    var meterNumber by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        val imePadding = WindowInsets.ime.asPaddingValues()

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(32.dp)
                .padding(imePadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HouseTypeSelection(selectedType = houseType, onHouseTypeSelected = { houseType = it })
                TextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it },
                    label = { Text("House Number") }
                )
                TextField(
                    value = rentAmount,
                    onValueChange = { rentAmount = it },
                    label = { Text("Rent Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = meterNumber,
                    onValueChange = { meterNumber = it },
                    label = { Text("Meter Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val property = Property(
                            houseNumber = houseNumber,
                            houseType = houseType,
                            rentAmount = rentAmount.toIntOrNull() ?: 0,
                            meterNumber = meterNumber.toIntOrNull() ?: 0
                        )
                        onAdd(property)
                    }) { Text("Add") }
                }
            }
        }
    }
}






@Composable
fun HouseTypeSelection(selectedType: String, onHouseTypeSelected: (String) -> Unit) {
    val houseTypes = listOf("Bedsitter", "One bedroom", "Two bedroom", "Three bedroom")

    Column {
        Text(
            text = "Select House Type",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        houseTypes.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHouseTypeSelected(type) }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedType == type,
                    onClick = { onHouseTypeSelected(type) }
                )
                Text(
                    text = type,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}



@Composable
fun PropertyIcon(houseType: String) {
    val icon = when (houseType) {
        "Bedsitter" -> R.drawable.hostel
        "One bedroom" -> R.drawable.onebed
        "Two bedroom" -> R.drawable.twobeds
        "Three bedroom" -> R.drawable.beds
        else -> R.drawable.hostel
    }

    Image(
        painter = painterResource(id = icon),
        contentDescription = houseType,
        modifier = Modifier.size(40.dp),
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center
    )
}
