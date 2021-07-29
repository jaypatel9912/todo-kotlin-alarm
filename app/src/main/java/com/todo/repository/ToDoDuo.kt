package com.todo.repository

import androidx.room.*
import com.todo.model.ToDoItem

@Dao
interface ToDoDuo {
    @Insert
    fun insertToDo(note: ToDoItem): Long?

    @Query("SELECT * FROM table_todo WHERE email =:email ORDER BY id DESC")
    fun getAllToDos(email: String): List<ToDoItem>

    @Query("SELECT * FROM table_todo WHERE id =:taskId")
    fun getToDo(taskId: Int): ToDoItem

    @Update
    fun updateToDo(note: ToDoItem)

    @Delete
    fun deleteToDo(note: ToDoItem)
}
