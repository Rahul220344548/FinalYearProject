package com.example.gym_application.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gym_application.model.MembershipInfo
import com.example.gym_application.model.UserClassBooking
import com.example.gym_application.model.UserDetails
import com.example.gym_application.newModel.Instructor
import com.example.gym_application.newModel.newUserClassBooking
import com.example.gym_application.utils.formatDateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class UserFirebaseDatabaseHelper {

    fun createAccountInAuthentication(email: String, password: String, callback: (Boolean, String?) -> Unit) {

        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    callback(true, userId)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun addAccountInUserDatabase(userId: String, userDetails: UserDetails, callback: (Boolean, String?) -> Unit) {

        val userDatabase = FirebaseDatabase.getInstance().getReference("users")

        userDatabase.child(userId).setValue(userDetails)
            .addOnFailureListener {
                callback(true,null)
            }
            .addOnFailureListener { exception ->
                callback(false,exception.message)
            }

    }

    fun fetchAuthentication(userId: String, onComplete: (Boolean, String) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
        val auth = FirebaseAuth.getInstance()

        databaseRef.child(userId).get().addOnSuccessListener { snapshot ->
            val status = snapshot.child("status").value?.toString() ?: "active"
            val role = snapshot.child("role").value?.toString() ?: "Member"

            if (status == "inactive") {

                auth.signOut()
                onComplete(false, "inactive")
            } else {
                onComplete(true, role)
            }
        }.addOnFailureListener {
            onComplete(false, "error")
        }

    }

    fun addUserBookedClassToCurrentBookings(
        userId: String,
        classId: String,
        scheduleId: String,
        callback: (Boolean) -> Unit
    ) {

        val userDatabaseRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)
            .child("bookings")
            .child("current")
            .push()

        val booking = UserClassBooking(classId, scheduleId)

        userDatabaseRef.setValue(booking)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false)
            }

    }

    fun newAddUserBookedClassToCurrentBookings(
    userId: String,
    scheduleId: String,
    callback: (Boolean) -> Unit
    ) {
        val userDatabaseRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)
            .child("bookings")
            .child("current")
            .push()
        val booking = newUserClassBooking(scheduleId)
        userDatabaseRef.setValue(booking)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false)
            }

    }

    fun fetchUserCurrentBookings(userId : String, callback: (List<String>?) -> Unit) {

        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)
            .child("bookings")
            .child("current")

        userDatabase.get().addOnSuccessListener { snapshot ->

            val classIdList = mutableListOf<String>()
            if (snapshot.exists()) {
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(UserClassBooking::class.java)
                    val fetchedclassId = booking?.classId

                    if (fetchedclassId != null) {
                        classIdList.add(fetchedclassId)
                    }
                }
                callback(classIdList)
            }


        }.addOnFailureListener { exception ->
            callback(null)
        }

    }

    private var currentBookingsListener: ValueEventListener? = null

    fun listenToUserCurrentBookingsLive(
        userId: String,
        onUpdate: (List<String>) -> Unit
    ) {
        val userDatabase = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(userId)
            .child("bookings")
            .child("current")

        currentBookingsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scheduleIdList = mutableListOf<String>()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(UserClassBooking::class.java)
                    val fetchedScheduleId = booking?.scheduleId
                    if (fetchedScheduleId != null) {
                        scheduleIdList.add(fetchedScheduleId)
                    }
                }
                onUpdate(scheduleIdList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserBookings", "Error: ${error.message}")
                onUpdate(emptyList())
            }
        }

        userDatabase.addValueEventListener(currentBookingsListener!!)
    }

    fun removeUserCurrentBookingsListener(userId: String) {
        val userDatabase = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(userId)
            .child("bookings")
            .child("current")

        currentBookingsListener?.let {
            userDatabase.removeEventListener(it)
        }
        currentBookingsListener = null
    }

    fun fetchUserCurrentBookingsAsPairs(userId: String, callback: (List<Pair<String, String>>?) -> Unit) {

        val userBookingsRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(userId)
            .child("bookings")
            .child("current")

        userBookingsRef.get()
            .addOnSuccessListener { snapshot ->
                val bookings = snapshot.children.mapNotNull { bookingSnapshot ->
                    val booking = bookingSnapshot.getValue(UserClassBooking::class.java)
                    val classId = booking?.classId
                    val scheduleId = booking?.scheduleId

                    if (!classId.isNullOrEmpty() && !scheduleId.isNullOrEmpty()) {
                        Pair(classId, scheduleId)
                    } else null
                }

                callback(bookings.takeIf { it.isNotEmpty() })
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun fetchInstructors (callback: (List<Instructor>) -> Unit) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val instructors = mutableListOf<Instructor>()

        databaseReference.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    val role = userSnapshot.child("role").getValue(String::class.java)
                    val status = userSnapshot.child("status").getValue(String::class.java)?: ""
                    if (role == "Instructor" && status == "active") {
                        val userId = userSnapshot.key ?: continue
                        val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                        val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                        val fullName = "$firstName $lastName"
                        instructors.add(Instructor(name = fullName, userId = userId))
                    }
                }
                callback(instructors)
            }else {
                callback(emptyList())
            }
        }.addOnFailureListener {
            callback(emptyList())
        }

    }

    fun listenForUserUpdates(onDataChanged: (Map<UserDetails, String>) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMap = mutableMapOf<UserDetails, String>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserDetails::class.java)
                    val userId = userSnapshot.key
                    if (user != null && userId != null && user.role != "Admin") {
                        Log.d("User Fetch", "Fetched user name: ${user.firstName} ${user.lastName}")
                        userMap[user] = userId
                    }
                }
                onDataChanged(userMap)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error Fetching Users: ${error.message}")
            }
        })
    }

    fun adminUpdateUserInfo(userId:String, userUpdate: Map<String,Any>, callback: (Boolean) -> Unit) {

        val userDatabase = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)

        userDatabase.updateChildren(userUpdate)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun fetchUserMembershipInfo(userId: String, callback: (MembershipInfo?) -> Unit) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val membershipRef = databaseReference.child(userId).child("membershipDetails")

        membershipRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val membershipTitle = snapshot.child("membershipTitle").value?.toString() ?: "No Plan"
                val membershipDuration = snapshot.child("membershipDuration").value?.toString() ?: "Unknown"
                val membershipStatus = snapshot.child("membershipStatus").value?.toString()?.lowercase() ?: "inactive"
                val expirationDate = snapshot.child("endDate").value?.toString() ?: "Unknown"

                val membershipInfo = MembershipInfo(
                    membershipTitle,
                    membershipDuration,
                    membershipStatus,
                    expirationDate
                )
                callback(membershipInfo)
            }else {
                callback(null)
            }
        }.addOnFailureListener { error ->
            callback(null)
        }

    }

    fun softDeleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)

        userDatabase.child("status").setValue("inactive")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }

    }

    fun fetchUserStatus(userId: String, onComplete: (Boolean, String) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val status = snapshot.child("status").value?.toString() ?: "active"
                onComplete(true, status)
            } else {
                onComplete(false, "User not found")
            }
        }.addOnFailureListener {
            onComplete(false, "Error fetching user status")
        }
    }

    fun updateUserStatus(userId: String, onComplete: (Boolean) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)

        userDatabase.child("status").setValue("active")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e->
                onComplete(false)
            }

    }


    fun getUserStatsForAdmin(
        onResult: (activeCount: Int, staffCount: Int,
                   inactiveCount:Int, totalUsers: Int) -> Unit
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var activeCount = 0
                var inactiveCount = 0
                var staffCount = 0
                var totalUSers = 0

                for (userSnapshot in snapshot.children) {
                    totalUSers++
                    val status = userSnapshot.child("status").getValue(String::class.java)
                    val role = userSnapshot.child("role").getValue(String::class.java)
                    if (status == "active" && role == "Member" ) {
                        activeCount++
                    } else if (status == "inactive") {
                        inactiveCount++
                    }
                    if (role.equals("Instructor", ignoreCase = true)) {
                        staffCount++
                    }
                }
                onResult(activeCount,staffCount,inactiveCount,totalUSers)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(0,0,0,0)
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createScheduleEntryToInstructor(
        context: Context,
        instructorId: String,
        classStartDate : String,
        scheduleId: String
    ) {

        val classStartDate = formatDateUtils.formatDateForFirebase(classStartDate)

        val userDatabaseRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(instructorId)
            .child("mySchedules")
            .child(classStartDate)
            .child(scheduleId)


        userDatabaseRef.setValue(true)
            .addOnSuccessListener {
                Toast.makeText(context, "Schedule linked to instructor!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error linking schedule: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }




}