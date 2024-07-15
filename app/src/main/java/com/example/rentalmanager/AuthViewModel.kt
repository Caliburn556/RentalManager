package com.example.rentalmanager


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _tenantList = MutableLiveData<List<Tenant>>()
    val tenantList: LiveData<List<Tenant>> = _tenantList

    private val _propertyList = MutableLiveData<List<Property>>()
    val propertyList: LiveData<List<Property>> = _propertyList

    private val _paymentList = MutableLiveData<List<Payment>>()
    val paymentList: LiveData<List<Payment>> = _paymentList

    private val _occupancies = MutableLiveData<List<Occupancy>>()
    val occupancies: LiveData<List<Occupancy>> = _occupancies




    init {
        checkAuthStatus()
        fetchTenants()
        fetchProperties()
        fetchPayments()
    }

    fun checkAuthStatus() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
            fetchUserData()
        } else {
            _authState.value = AuthState.Unauthenticated
            clearData()
        }
    }
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    fetchTenants()
                    fetchProperties()
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Authentication failed")
                }
            }
    }
    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    fetchTenants()
                    fetchProperties()
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }
    private fun fetchUserData() {
        fetchTenants()
        fetchProperties()
        fetchPayments()
    }
    private fun clearData() {
        _tenantList.value = emptyList()
        _propertyList.value = emptyList()
        _paymentList.value = emptyList()
    }
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
    private fun fetchTenants() {
        auth.currentUser?.let { user ->
            database.child("users").child(user.uid).child("tenants").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tenants = snapshot.children.mapNotNull { it.getValue(Tenant::class.java) }
                    _tenantList.value = tenants
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    fun addTenant(tenant: Tenant) {
        auth.currentUser?.let { user ->
            val newTenantRef = database.child("users").child(user.uid).child("tenants").push()
            tenant.tenantId = newTenantRef.key.toString()
            newTenantRef.setValue(tenant)
        }
    }
    fun deleteTenant(tenantId: String) {
        auth.currentUser?.let { user ->
            database.child("users").child(user.uid).child("tenants").child(tenantId).removeValue()
        }
    }
    private fun fetchProperties() {
        auth.currentUser?.let { user ->
            database.child("users").child(user.uid).child("properties").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val properties = snapshot.children.mapNotNull { it.getValue(Property::class.java) }
                    _propertyList.value = properties
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }


    fun addProperty(property: Property) {
        auth.currentUser?.let { user ->
            val newPropertyRef = database.child("users").child(user.uid).child("properties").push()
            newPropertyRef.setValue(property)
        }
    }

    fun deleteProperty(propertyId: String) {
        auth.currentUser?.let { user ->
            database.child("users").child(user.uid).child("properties").child(propertyId).removeValue()
        }
    }





    private fun fetchPayments() {
        auth.currentUser?.let { user ->
            database.child("users").child(user.uid).child("payments").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val payments = snapshot.children.mapNotNull { it.getValue(Payment::class.java) }
                    _paymentList.value = payments
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun addPayment(payment: Payment) {
        auth.currentUser?.let { user ->
            val newPaymentRef = database.child("users").child(user.uid).child("payments").push()
            newPaymentRef.setValue(payment)
        }
    }
    fun fetchOccupancies() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child("users").child(userId).child("occupancies").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val occupancyList = mutableListOf<Occupancy>()
                for (occupancySnapshot in snapshot.children) {
                    val occupancy = occupancySnapshot.getValue(Occupancy::class.java)
                    occupancy?.let { occupancyList.add(it) }
                }
                _occupancies.value = occupancyList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun addOccupancy(occupantName: String, houseNumber: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val id = database.child("users").child(userId).child("occupancies").push().key ?: return
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val occupancy = Occupancy(id, occupantName, houseNumber, date)

        database.child("users").child(userId).child("occupancies").child(id).setValue(occupancy)
    }
    fun deleteOccupancy(occupancyId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child("users").child(userId).child("occupancies").child(occupancyId).removeValue()
    }
}








sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

data class Tenant(
    var tenantId: String = "",
    val photoUrl: String = "",
    val fullName: String = "",
    val gender: String = "",
    val age: Int = 0,
    val idNumber: String = "",
    val occupation: String = "",
    val mobile: String = ""
)

data class Property(
    var propertyId: String = "",
    val houseNumber: String = "",
    val houseType: String = "",
    val rentAmount: Int = 0,
    val meterNumber: Int = 0,
    var userId: String = "",
)

data class Payment(
    val paymentId: String = "",
    val tenantId: String = "",
    val propertyId: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val month: String = "",
    val year: Int = 0
)

data class Occupancy(
    val id: String = "",
    val occupantName: String = "",
    val houseNumber: String = "",
    val date: String = ""
)





