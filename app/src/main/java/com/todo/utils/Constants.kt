package com.todo.utils

abstract class Constants {
    companion object {
        const val preferenceFileKey = "com.todo.android.token_preferences"
        private const val DOMAIN = " https://reqres.in/"
        const val BASE_URL = DOMAIN + "api/"
        const val EMAIL = "email"
        const val TODO_DB = "todo_database"
        const val TODO_ITEM = "todo_item"

        const val CHANNEL_ID = "todo_channel"

        const val ALARM_ID = "alarm_id"
        const val ALARM_TITLE  = "alarm_title"
        const val ALARM_DESC = "alarm_desc"
        const val ALARM_DATE_TIME = "alarm_date_time"

    }
}