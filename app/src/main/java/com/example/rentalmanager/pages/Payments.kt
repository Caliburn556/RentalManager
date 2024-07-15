package com.example.rentalmanager.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.rentalmanager.AuthViewModel
import com.example.rentalmanager.Payment
import com.example.rentalmanager.Property
import com.example.rentalmanager.Tenant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun Payments(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val properties by authViewModel.propertyList.observeAsState(emptyList())
    val tenants by authViewModel.tenantList.observeAsState(emptyList())
    val payments by authViewModel.paymentList.observeAsState(emptyList())

    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var selectedTenant by remember { mutableStateOf<Tenant?>(null) }
    var amount by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showPropertyDialog by remember { mutableStateOf(false) }
    var showTenantDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF312D35))
            .padding(16.dp)
    ) {
        Text(
            text = "Record Payments",
            fontSize = 25.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(onClick = { showPropertyDialog = true }) {
            Text(text = selectedProperty?.houseNumber ?: "Select Property")
        }

        Button(onClick = { showTenantDialog = true }) {
            Text(text = selectedTenant?.fullName ?: "Select Tenant")
        }

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = month,
            onValueChange = { month = it },
            label = { Text("Month") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = year.toString(),
            onValueChange = { year = it.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR) },
            label = { Text("Year") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                val payment = Payment(
                    tenantId = selectedTenant?.tenantId ?: "",
                    propertyId = selectedProperty?.propertyId ?: "",
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = getCurrentDate(),
                    month = month,
                    year = year
                )
                authViewModel.addPayment(payment)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Record Payment")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Payment Records",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(payments) { payment ->
                PaymentItem(payment = payment, tenants = tenants, properties = properties, payments = payments)
            }
        }

        if (showPropertyDialog) {
            SelectPropertyDialog(properties = properties, onPropertySelected = {
                selectedProperty = it
                showPropertyDialog = false
            }, onDismiss = { showPropertyDialog = false })
        }

        if (showTenantDialog) {
            SelectTenantDialog(tenants = tenants, onTenantSelected = {
                selectedTenant = it
                showTenantDialog = false
            }, onDismiss = { showTenantDialog = false })
        }
    }
}

@Composable
fun SelectPropertyDialog(properties: List<Property>, onPropertySelected: (Property) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Property",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                LazyColumn {
                    items(properties) { property ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPropertySelected(property) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = property.houseNumber,
                                color = Color.Black
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
fun SelectTenantDialog(tenants: List<Tenant>, onTenantSelected: (Tenant) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Tenant",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                LazyColumn {
                    items(tenants) { tenant ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTenantSelected(tenant) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = tenant.fullName,
                                color = Color.Black
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
fun PaymentItem(payment: Payment, tenants: List<Tenant>, properties: List<Property>, payments: List<Payment>) {
    val tenant = tenants.find { it.tenantId == payment.tenantId }
    val property = properties.find { it.propertyId == payment.propertyId }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Tenant: ${tenant?.fullName ?: "Unknown"}", color = Color.White, fontSize = 20.sp)
        Text(text = "Property: ${property?.houseNumber ?: "Unknown"}", color = Color.White, fontSize = 16.sp)
        Text(text = "Amount: \$${payment.amount}", color = Color.White, fontSize = 16.sp)
        Text(text = "Date: ${payment.date}", color = Color.White, fontSize = 16.sp)
        Text(text = "Month: ${payment.month}", color = Color.White, fontSize = 16.sp)
        Text(text = "Year: ${payment.year}", color = Color.White, fontSize = 16.sp)
    }
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}





