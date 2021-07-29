package com.todo.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.todo.R
import com.todo.activities.HomeActivity
import com.todo.utils.Constants
import java.util.*

class LocalNotificationService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        if (intent.extras == null) return
        try {
            generateNotification(intent.extras!!, context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateNotification(bundle: Bundle, context: Context?) {
        var resultIntent= Intent(context, HomeActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert(notificationManager != null)
            val mChannel = notificationManager.getNotificationChannel(Constants.CHANNEL_ID)
            if (mChannel == null) {
                createChannel(context)
            }
        }
        val notificationBuilder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
        val descString = bundle.getString(Constants.ALARM_DESC)
        notificationBuilder.setContentTitle(bundle.getString(Constants.ALARM_TITLE) + " - " + bundle.getString(Constants.ALARM_DATE_TIME))
        notificationBuilder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
            )
        )
        notificationBuilder.setSmallIcon(
            context.resources.getIdentifier(
                "ic_launcher",
                "mipmap",
                context.packageName
            )
        )
        notificationBuilder.setChannelId(Constants.CHANNEL_ID)
        notificationBuilder.setContentText(descString)
        notificationBuilder.setContentIntent(resultPendingIntent)
        notificationBuilder.setStyle(
            NotificationCompat.BigTextStyle().bigText(descString)
        )
        notificationBuilder.setAutoCancel(true)
        val notification = notificationBuilder.build()
        notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        notification.defaults = notification.defaults or Notification.DEFAULT_VIBRATE
        val random = Random()
        notificationManager.notify(random.nextInt(1000), notification)
//        Alarm.start(context)
    }


    private fun createChannel(context: Context?) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationManager =
                    context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val importance = NotificationManager.IMPORTANCE_HIGH
                var mChannel: NotificationChannel? = null
                mChannel = NotificationChannel(
                    Constants.CHANNEL_ID,
                    "ToDo",
                    importance
                )
                mChannel.description = "ToDo"
                mChannel.enableLights(true)
                mChannel.lightColor = Color.RED
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                assert(mNotificationManager != null)
                mNotificationManager.createNotificationChannel(mChannel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        var context: Context? = null
        fun enqueueWork(mcontext: Context?, intent1: Intent) {
            val intent = Intent(mcontext, LocalNotificationService::class.java)
            intent1.extras?.let { intent.putExtras(it) }
            context = mcontext
            enqueueWork(
                mcontext!!,
                LocalNotificationService::class.java, 1000, intent
            )
        }
    }
}
