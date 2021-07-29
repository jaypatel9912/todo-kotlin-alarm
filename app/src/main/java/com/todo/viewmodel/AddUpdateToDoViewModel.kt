package com.todo.viewmodel

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.todo.R
import com.todo.alarm.Alarm
import com.todo.model.ToDoItem
import com.todo.repository.ToDoDuo
import com.todo.repository.ToDoRepository
import com.todo.retrofit.UserService
import com.todo.utils.CommonUtils
import com.todo.utils.Constants
import kotlinx.coroutines.*
import java.util.*

class AddUpdateToDoViewModel(val context: Context, toDoDuo: ToDoDuo) : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main
    )

    val calendar = Calendar.getInstance()

    val toDoRepository = ToDoRepository(context, toDoDuo)

    private var _toDoList = MutableLiveData<List<ToDoItem>?>()
    val toDoList: LiveData<List<ToDoItem>?> = _toDoList

    private val apiService = UserService().getApiService()

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _todoType = MutableLiveData(0)
    val todoType: LiveData<Int> = _todoType

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _finish = MutableLiveData<Boolean>()
    val finish: LiveData<Boolean> = _finish

    private val _updateToDo = MutableLiveData<ToDoItem>()
    val updateToDo: LiveData<ToDoItem> = _updateToDo

    private var _dateString = MutableLiveData("")
    val dateString: LiveData<String> = _dateString


    private var _timeString = MutableLiveData("")
    val timeString: LiveData<String> = _timeString

    private var _title = MutableLiveData("")
    val title: LiveData<String> = _title

    private var _desc = MutableLiveData("")
    val desc: LiveData<String> = _desc


    private val _addToDo = MutableLiveData<Boolean>()
    val addToDo: LiveData<Boolean> = _addToDo

    private var _showDatePicker = MutableLiveData(false)
    val showDatePicker: LiveData<Boolean> = _showDatePicker

    private var _showTimePicker = MutableLiveData(false)
    val showTimePicker: LiveData<Boolean> = _showTimePicker

    init {
        calendar.time = Date()
    }

    fun setToDoType(res: Int) {
        _todoType.value = res
    }

    private fun setLoading(res: Boolean) {
        _loading.value = res
    }

    fun setMessage(res: String?) {
        _message.value = res
    }

    fun setFinish(res: Boolean) {
        _finish.value = res
    }

    fun setUpdateData(toDoItem: ToDoItem) {
        _updateToDo.value = toDoItem
        _title.value = toDoItem.title
        _desc.value = toDoItem.desc
        _dateString.value = toDoItem.date
        _timeString.value = toDoItem.time
        _todoType.value = toDoItem.type
    }

    fun setShowDatePicker(showDatePicker: Boolean) {
        _showDatePicker.value = showDatePicker
    }

    fun setShowTimePicker(showDatePicker: Boolean) {
        _showTimePicker.value = showDatePicker
    }

    val textWatcherTitle = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _title.value = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    }

    val textWatcherDesc = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _desc.value = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    }


    fun setCalenderDate(year: Int, month: Int, day: Int) {
        calendar.set(
            year,
            month,
            day,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
        val cal = Calendar.getInstance()
        cal.time = Date()
        _dateString.value = CommonUtils.formatDate(calendar.time, "MMM dd, yyyy", false)
    }

    fun setCalenderTime(hour: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        _timeString.value = CommonUtils.formatDate(calendar.time, "hh:mm aa", false)
    }

    fun saveToDo() {

        if (TextUtils.isEmpty(_title.value)) {
            setMessage(context.getString(R.string.err_title))
            return
        }

        if (TextUtils.isEmpty(_desc.value)) {
            setMessage(context.getString(R.string.err_desc))
            return
        }

        if (_todoType.value == 0) {
            setMessage(context.getString(R.string.err_type))
            return
        }

        if (TextUtils.isEmpty(_dateString.value)) {
            setMessage(context.getString(R.string.err_date))
            return
        }

        if (TextUtils.isEmpty(_timeString.value)) {
            setMessage(context.getString(R.string.err_time))
            return
        }

        val item = ToDoItem(
            _updateToDo.value?.id ?: 0,
            title = _title.value,
            desc = _desc.value,
            date = _dateString.value,
            time = _timeString.value,
            email = CommonUtils.getPreference(context, Constants.EMAIL),
            type = _todoType.value
        )
        setLoading(true)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                if (_updateToDo.value == null)
                    toDoRepository.insertToDo(item)
                else
                    toDoRepository.updateToDo(item)
                Alarm.addAlarm(context, item)
                withContext(Dispatchers.Main) {
                    setMessage(context.getString(R.string.todo_save))
                    setFinish(true)
                }
            }
        }
    }
}