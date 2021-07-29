package com.todo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.todo.alarm.Alarm
import com.todo.model.ToDoItem
import com.todo.repository.ToDoDuo
import com.todo.repository.ToDoRepository
import com.todo.retrofit.UserService
import com.todo.utils.CommonUtils
import com.todo.utils.Constants
import kotlinx.coroutines.*

class HomeViewModel(val context: Context, toDoDuo: ToDoDuo) : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main
    )

    val toDoRepository = ToDoRepository(context, toDoDuo)

    private var _toDoList = MutableLiveData<List<ToDoItem>?>()
    val toDoList: LiveData<List<ToDoItem>?> = _toDoList

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _addToDo = MutableLiveData<Boolean>()
    val addToDo: LiveData<Boolean> = _addToDo

    fun setLoading(res: Boolean) {
        _loading.value = res
    }

    fun setMessage(res: String?) {
        _message.value = res
    }

    fun setAddToDo(res: Boolean) {
        _addToDo.value = res
    }

    fun listToDo() {
        setLoading(true)
        coroutineScope.launch {
            val toDoList = withContext(Dispatchers.IO) {
                CommonUtils.getPreference(context, Constants.EMAIL)?.let {
                    toDoRepository.getAllToDos(
                        it
                    )
                }
            }
            withContext(Dispatchers.Main) {
                _toDoList.value = toDoList
            }
        }
    }

    fun deleteToDo(toDoItem: ToDoItem) {
        setLoading(true)
        coroutineScope.launch {
            val toDoList = withContext(Dispatchers.IO) {

                toDoRepository.deleteToDo(toDoItem)
                Alarm.cancelAlarmById(context, toDoItem.id)
                CommonUtils.getPreference(context, Constants.EMAIL)?.let {
                    toDoRepository.getAllToDos(
                        it
                    )
                }
            }
            withContext(Dispatchers.Main) {
                _toDoList.value = toDoList
            }
        }
    }

}