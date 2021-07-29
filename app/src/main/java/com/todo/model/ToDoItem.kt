package com.todo.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "table_todo")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val desc: String?,
    val date: String?,
    val time: String?,
    val email: String?, // differentiate for users
    val type: Int?, //1 - daily, 2 - weekly
) : Parcelable