package com.example.firebasesample

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Task(
    @DocumentId
    val id : String = "",
    val title : String = "",
    val createdAt: Date = Date(System.currentTimeMillis())
)
