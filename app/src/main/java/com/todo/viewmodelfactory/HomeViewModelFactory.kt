package com.todo.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todo.repository.ToDoDuo
import com.todo.viewmodel.HomeViewModel

class HomeViewModelFactory (private val context: Context, private val  toDoDuo: ToDoDuo) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(context, toDoDuo) as T
        }
        throw IllegalArgumentException("Unknown HomeViewModel class")
    }
}