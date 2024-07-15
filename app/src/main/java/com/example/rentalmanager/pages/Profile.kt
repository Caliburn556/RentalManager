package com.example.rentalmanager.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.rentalmanager.R

import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.database



import androidx.compose.material.icons.Icons
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rentalmanager.AuthState
import com.example.rentalmanager.AuthViewModel
import com.google.firebase.auth.FirebaseAuth



@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val database = com.google.firebase.Firebase.database.reference
    val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var profileSaved by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            database.child("users").child(userId).get().addOnSuccessListener {
                val userData = it.getValue(UserData::class.java)
                if (userData != null) {
                    name = userData.name
                    email = userData.email
                    mobile = userData.mobile
                    location = userData.location
                    address = userData.address
                    profileSaved = true
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveProfile(updatedName: String, updatedEmail: String, updatedMobile: String, updatedLocation: String, updatedAddress: String) {
        database.child("users").child(userId).setValue(
            UserData(updatedName, updatedEmail, updatedMobile, updatedLocation, updatedAddress, true)
        ).addOnSuccessListener {
            name = updatedName
            email = updatedEmail
            mobile = updatedMobile
            location = updatedLocation
            address = updatedAddress
            profileSaved = true
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    if (!profileSaved) {
        EditProfileDialog(
            name = name,
            email = email,
            mobile = mobile,
            location = location,
            address = address,
            onDismiss = { /* Dialog dismissed */ },
            onSave = { updatedName, updatedEmail, updatedMobile, updatedLocation, updatedAddress ->
                saveProfile(updatedName, updatedEmail, updatedMobile, updatedLocation, updatedAddress)
            }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp)
            .background(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                onClick = { navController.navigate("Home") }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Profile content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.property),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                modifier = Modifier.padding(vertical = 4.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Landlord",
                modifier = Modifier.padding(vertical = 4.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Email: $email",
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Mobile: $mobile",
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Location: $location",
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Address: $address",
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun EditProfileDialog(
    name: String,
    email: String,
    mobile: String,
    location: String,
    address: String,
    onSave: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var newName by remember { mutableStateOf(name) }
    var newEmail by remember { mutableStateOf(email) }
    var newMobile by remember { mutableStateOf(mobile) }
    var newLocation by remember { mutableStateOf(location) }
    var newAddress by remember { mutableStateOf(address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") }
                )
                TextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Email") }
                )
                TextField(
                    value = newMobile,
                    onValueChange = { newMobile = it },
                    label = { Text("Mobile") }
                )
                TextField(
                    value = newLocation,
                    onValueChange = { newLocation = it },
                    label = { Text("Location") }
                )
                TextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("Address") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(newName, newEmail, newMobile, newLocation, newAddress)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}





data class UserData(
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val location: String = "",
    val address: String = "",
    val profileSaved: Boolean = false
)








