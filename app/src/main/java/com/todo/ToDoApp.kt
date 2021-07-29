package com.todo

import android.app.Application
import com.todo.alarm.Alarm

class ToDoApp : Application() {

    override fun onCreate() {
        super.onCreate()
//        Thread {
//            Alarm.start(this@ToDoApp)
//        }.start()
    }
}