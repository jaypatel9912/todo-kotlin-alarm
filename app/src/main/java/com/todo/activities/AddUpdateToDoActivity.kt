package com.todo.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.todo.R
import com.todo.databinding.ActivityAddUpdateToDoBinding
import com.todo.model.ToDoItem
import com.todo.repository.TodoDatabase
import com.todo.utils.CommonUtils.toast
import com.todo.utils.Constants
import com.todo.viewmodel.AddUpdateToDoViewModel
import com.todo.viewmodelfactory.AddUpdateToDoViewModelFactory
import java.util.*


class AddUpdateToDoActivity : AppCompatActivity() {

    private lateinit var addUpdateToDoViewModel: AddUpdateToDoViewModel
    private lateinit var binding: ActivityAddUpdateToDoBinding
    lateinit var activity: AddUpdateToDoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@AddUpdateToDoActivity

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_update_to_do)

        supportActionBar?.title = getString(R.string.add_todo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addUpdateToDoViewModel = ViewModelProvider(
            activity,
            AddUpdateToDoViewModelFactory(activity, TodoDatabase.getInstance(application).toDoDuo)
        ).get(AddUpdateToDoViewModel::class.java)

        binding.viewModel = addUpdateToDoViewModel


        addUpdateToDoViewModel.updateToDo.observe(this, {
            if (it != null) {
                binding.editTitle.setText(it.title)
                binding.editDesc.setText(it.desc)
                if (it.type == 1)
                    binding.daily.isChecked = true
                else if (it.type == 2)
                    binding.weekly.isChecked = true
            }
        })

        if (intent != null && intent.extras != null && intent.extras!!.containsKey(Constants.TODO_ITEM)) {
            supportActionBar?.title = getString(R.string.update_todo)
            val todoItem =
                Gson().fromJson(intent.getStringExtra(Constants.TODO_ITEM), ToDoItem::class.java)
            addUpdateToDoViewModel.setUpdateData(todoItem)
        }

        binding.rdGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.daily.id)
                addUpdateToDoViewModel.setToDoType(1)
            else if (checkedId == binding.weekly.id)
                addUpdateToDoViewModel.setToDoType(2)
        }

        addUpdateToDoViewModel.showDatePicker.observe(this, {
            if (it) {
                showDatePicker()
                addUpdateToDoViewModel.setShowDatePicker(false)
            }
        })

        addUpdateToDoViewModel.message.observe(this, {
            if (!it.isNullOrEmpty()) {
                toast(it)
                addUpdateToDoViewModel.setMessage("")
            }
        })

        addUpdateToDoViewModel.showTimePicker.observe(this, {
            if (it) {
                showTimePicker()
                addUpdateToDoViewModel.setShowTimePicker(false)
            }
        })

        addUpdateToDoViewModel.finish.observe(this, {
            if (it) {
                finish()
                addUpdateToDoViewModel.setFinish(false)
            }
        })

        addUpdateToDoViewModel.dateString.observe(this, {
            if (!it.isNullOrEmpty()) {
                binding.tvDate.text = it
            }
        })

        addUpdateToDoViewModel.timeString.observe(this, {
            if (!it.isNullOrEmpty()) {
                binding.tvTime.text = it
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(
            Menu.FLAG_ALWAYS_PERFORM_CLOSE,
            7,
            Menu.NONE,
            getString(R.string.save)
        )

        menu.findItem(7)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            7 -> {
                addUpdateToDoViewModel.saveToDo()
            }
        }
        return true
    }


    private fun showDatePicker() {
        val year = addUpdateToDoViewModel.calendar.get(Calendar.YEAR)
        val month = addUpdateToDoViewModel.calendar.get(Calendar.MONTH)
        val day = addUpdateToDoViewModel.calendar.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(activity, { view, years, monthOfYear, dayOfMonth ->
            addUpdateToDoViewModel.setCalenderDate(years, monthOfYear, dayOfMonth)
        }, year, month, day)

        var cal = Calendar.getInstance()
        cal.time = Date()
        dpd.datePicker.minDate = cal.time.time
        dpd.show()
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            addUpdateToDoViewModel.setCalenderTime(hour, minute)
        }

        var picker = TimePickerDialog(
            activity,
            timeSetListener,
            addUpdateToDoViewModel.calendar.get(Calendar.HOUR_OF_DAY),
            addUpdateToDoViewModel.calendar.get(Calendar.MINUTE),
            false
        )
        picker.show()
    }
}