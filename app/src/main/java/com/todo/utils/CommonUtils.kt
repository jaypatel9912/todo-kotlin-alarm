package com.todo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.material.snackbar.Snackbar
import com.todo.R
import java.text.SimpleDateFormat
import java.util.*


object CommonUtils {
    lateinit var pBarDialog: ProgressBarDialog

    fun getPreference(context: Context?, key: String, defValue: String? = ""): String? {
        var sharedPrefs: SharedPreferences? =
            context?.getSharedPreferences(Constants.preferenceFileKey, Context.MODE_PRIVATE)
        return sharedPrefs?.getString(key, defValue)
    }

    fun setPreference(context: Context?, key: String, value: String) {
        var sharedPrefs: SharedPreferences? =
            context?.getSharedPreferences(Constants.preferenceFileKey, Context.MODE_PRIVATE)
        sharedPrefs?.let {
            with(it.edit()) {
                putString(key, value)
                apply()
            }
        }
    }

    fun showProgressBar(ctx: Context) {
        try {
            pBarDialog = ProgressBarDialog(ctx)
            pBarDialog.setCancelable(false)
            pBarDialog.setCanceledOnTouchOutside(false)
            pBarDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isOnline(context: Context?): Boolean {
        val connectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        context.toast(context.getString(R.string.no_internet))
        return false
    }


    fun dismissProgressBar() {
        try {
            if (this::pBarDialog.isInitialized) {
                if (pBarDialog.isShowing)
                    pBarDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun formatDate(date: Date, format: String = "yyyy-MM-dd", addZeroTime: Boolean = true): String {
        var dateString = SimpleDateFormat(format, Locale.ENGLISH).format(date)
        if (addZeroTime) {
            dateString += "T00:00:00"
        }
        return dateString
    }

    fun getDateFromString(date: String, format: String = "MMM dd, yyyy hh:mm aa"): Date {
        var dateString = SimpleDateFormat(format, Locale.ENGLISH).parse(date)
        return dateString
    }

    inline fun Context.toast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun hideSoftKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @JvmStatic
    @BindingAdapter("textChangedListener")
    fun bindTextWatcher(editText: EditText, textWatcher: TextWatcher) {
        editText.addTextChangedListener(textWatcher)
    }
}