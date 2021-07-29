package com.todo.repository

import android.content.Context
import com.todo.model.ToDoItem

class ToDoRepository(val context: Context?, val database: ToDoDuo) {

    fun insertToDo(toDoItem: ToDoItem){
        database.insertToDo(toDoItem)
    }

    fun updateToDo(toDoItem: ToDoItem){
        database.updateToDo(toDoItem)
    }

    fun getAllToDos(email : String) : List<ToDoItem>{
        return database.getAllToDos(email)
    }

    fun deleteToDo(toDoItem: ToDoItem){
         database.deleteToDo(toDoItem)
    }

}