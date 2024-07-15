package com.example.rentalmanager.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rentalmanager.AuthViewModel



@Composable
fun OccupancyScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val occupancies by viewModel.occupancies.observeAsState(emptyList())
    val occupantName = remember { mutableStateOf("") }
    val houseNumber = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchOccupancies()
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Assign Tenant to House")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = occupantName.value,
            onValueChange = { occupantName.value = it },
            label = { Text("Occupant Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = houseNumber.value,
            onValueChange = { houseNumber.value = it },
            label = { Text("House Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (occupantName.value.isNotEmpty() && houseNumber.value.isNotEmpty()) {
                    viewModel.addOccupancy(occupantName.value, houseNumber.value)
                    occupantName.value = ""
                    houseNumber.value = ""
                    Toast.makeText(context, "Occupancy assigned", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Assign")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Assigned Occupancies")

        LazyColumn {
            items(occupancies) { occupancy ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Occupant Name: ${occupancy.occupantName}")
                            Text("House Number: ${occupancy.houseNumber}")
                            Text("Date: ${occupancy.date}")
                        }
                        IconButton(onClick = {
                            viewModel.deleteOccupancy(occupancy.id)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}












