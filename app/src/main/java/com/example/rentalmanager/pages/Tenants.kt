package com.example.rentalmanager.pages

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.rentalmanager.AuthViewModel
import com.example.rentalmanager.R
import com.example.rentalmanager.Tenant
import java.util.UUID

@Composable
fun TenantsScreen(navController: NavController, authViewModel: AuthViewModel) {
    val tenantList by authViewModel.tenantList.observeAsState(emptyList())
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
                text = "List of Tenants",
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
        if (tenantList.isEmpty()) {
            Text(
                text = "You have no tenants currently",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumnList(tenantList = tenantList, authViewModel = authViewModel)
        }
        if (showDialog) {
            AddTenantDialog(
                onDismiss = { showDialog = false },
                onAdd = { tenant ->
                    authViewModel.addTenant(tenant)
                    showDialog = false
                }
            )
        }
    }
}
@Composable
fun LazyColumnList(tenantList: List<Tenant>, authViewModel: AuthViewModel) {
    LazyColumn(
        modifier = Modifier
            .padding(start = 14.dp)
            .clip(RoundedCornerShape(32.dp)),
        content = {
            itemsIndexed(tenantList) { index, item ->
                ListOfTenants(item = item, authViewModel = authViewModel)
            }
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
fun ListOfTenants(item: Tenant, authViewModel: AuthViewModel) {
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
            val imageRes = if (item.gender == "Male") R.drawable.malepic else R.drawable.female
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.fullName,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
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
                RowItem(label = "Gender: ", value = item.gender)
                RowItem(label = "Age: ", value = item.age.toString())
                RowItem(label = "Phone: ", value = item.mobile)
                RowItem(label = "ID Number: ", value = item.idNumber)
                RowItem(label = "Occupation: ", value = item.occupation)
                IconButton(onClick = {
                    authViewModel.deleteTenant(item.tenantId)
                }) {
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
fun RowItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )
    }
}

@Composable
fun AddTenantDialog(onDismiss: () -> Unit, onAdd: (Tenant) -> Unit) {
    var gender by remember { mutableStateOf("Male") }
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Gender", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Male",
                        onClick = { gender = "Male" }
                    )
                    Text("Male", modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = gender == "Female",
                        onClick = { gender = "Female" }
                    )
                    Text("Female", modifier = Modifier.padding(start = 8.dp))
                }
                TextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
                TextField(value = age, onValueChange = { age = it }, label = { Text("Age") })
                TextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("ID Number") })
                TextField(value = occupation, onValueChange = { occupation = it }, label = { Text("Occupation") })
                TextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val photoUrl = if (gender == "Male") "ic_male" else "ic_female"
                        val tenant = Tenant(
                            tenantId = UUID.randomUUID().toString(),
                            photoUrl = photoUrl,
                            fullName = fullName,
                            gender = gender,
                            age = age.toIntOrNull() ?: 0,
                            idNumber = idNumber,
                            occupation = occupation,
                            mobile = mobile
                        )
                        onAdd(tenant)
                    }) { Text("Add") }
                }
            }
        }
    }
}





