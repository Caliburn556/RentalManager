package com.example.rentalmanager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rentalmanager.pages.HomePage
import com.example.rentalmanager.pages.LogInPage
import com.example.rentalmanager.pages.OccupancyScreen
import com.example.rentalmanager.pages.Payments
import com.example.rentalmanager.pages.Profile
import com.example.rentalmanager.pages.PropertiesScreen
import com.example.rentalmanager.pages.SignUpPage
import com.example.rentalmanager.pages.TenantsScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel){
 val navController = rememberNavController()
 NavHost(navController =navController , startDestination ="Login" , builder ={
       composable(route = "Login"){
        LogInPage(modifier,navController,authViewModel)
       }
     composable(route = "signup"){
         SignUpPage(modifier,navController,authViewModel)
     }
     composable(route = "Home"){
         HomePage(modifier,navController,authViewModel)
     }
     composable(route = "payments"){
         Payments(modifier,navController,authViewModel)}
     composable(route = "Tenants"){
         TenantsScreen(navController,authViewModel)
     }
     composable(route = "Properties"){
         PropertiesScreen(navController,authViewModel)
     }
     composable(route = "Occupancy"){
         OccupancyScreen(modifier,navController,authViewModel)
     }
     composable(route = "profile"){
         Profile(modifier,navController,authViewModel)
     }
 })
}