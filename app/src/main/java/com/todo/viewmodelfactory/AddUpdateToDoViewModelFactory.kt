package com.todo.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todo.repository.ToDoDuo
import com.todo.viewmodel.AddUpdateToDoViewModel

class AddUpdateToDoViewModelFactory(private val context: Context, private val  toDoDuo: ToDoDuo) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddUpdateToDoViewModel::class.java)) {
            return AddUpdateToDoViewModel(context, toDoDuo) as T
        }
        throw IllegalArgumentException("Unknown AddUpdateToDoViewModel class")
    }
}