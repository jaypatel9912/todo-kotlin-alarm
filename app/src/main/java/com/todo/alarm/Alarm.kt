package com.todo.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.todo.model.ToDoItem
import com.todo.repository.ToDoRepository
import com.todo.repository.TodoDatabase
import com.todo.utils.CommonUtils
import com.todo.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

object Alarm {
    fun start(context: Context) {

        val toDoRepository = ToDoRepository(context, TodoDatabase.getInstance(context).toDoDuo)
        var shouldAllowRunComponene = false
        CommonUtils.getPreference(context, Constants.EMAIL)?.let {
            toDoRepository.getAllToDos(
                it
            )
        }?.forEach {
            shouldAllowRunComponene = true
            if (alarmExists(context, it.id))
                cancelAlarmById(context, it.id)

            addAlarm(context, it)
        }

        if (shouldAllowRunComponene) {
            val componentName = ComponentName(context, AlarmBootReceiver::class.java)
            val packageManager = context.packageManager
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun alarmExists(context: Context, id: Int): Boolean {
        try {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(Constants.ALARM_ID, id)
            return PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_NO_CREATE
            ) != null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun addAlarm(context: Context, todoItem: ToDoItem) {
        if (alarmExists(context, todoItem.id))
            cancelAlarmById(context, todoItem.id)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        var calAlarmDateTime = Calendar.getInstance()
        calAlarmDateTime.timeInMillis =
            CommonUtils.getDateFromString(todoItem.date + " " + todoItem.time).time

        if (todoItem.type == 1)
            calendar.timeInMillis = System.currentTimeMillis()
        else {
            //  for setting alarm on specific day of week
            calendar.timeInMillis = calAlarmDateTime.timeInMillis
            calendar[Calendar.DAY_OF_WEEK] = calAlarmDateTime.get(Calendar.DAY_OF_WEEK)
        }

        calendar[Calendar.HOUR_OF_DAY] = calAlarmDateTime.get(Calendar.HOUR_OF_DAY)
        calendar[Calendar.MINUTE] = calAlarmDateTime.get(Calendar.MINUTE)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)


        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.ALARM_ID, todoItem.id)
        intent.putExtra(Constants.ALARM_TITLE, todoItem.title)
        intent.putExtra(Constants.ALARM_DESC, todoItem.desc)
        intent.putExtra(Constants.ALARM_DATE_TIME, todoItem.date + " " + todoItem.time)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                todoItem.id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val test = sdf.format(calendar.time)
        Log.d("Added alarm id ", "" + todoItem.id)
        Log.e("Reminder time : ", test)
        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelAlarmById(context: Context, id: Int) {
        Log.d("Cancel alarm id ", "" + id)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.ALARM_ID, id)
        val pendingIntent = PendingIntent
            .getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
