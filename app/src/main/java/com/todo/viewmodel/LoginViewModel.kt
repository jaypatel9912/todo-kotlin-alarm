package com.todo.viewmodel

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.todo.R
import com.todo.retrofit.UserService
import com.todo.utils.CommonUtils
import com.todo.utils.Constants
import kotlinx.coroutines.*

class LoginViewModel(val context: Context) : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main
    )

    private val apiService = UserService().getApiService()

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private var _navigateToNext = MutableLiveData(0)
    val navigateToNext: LiveData<Int> = _navigateToNext

    private var _doLogin = MutableLiveData(false)
    val doLogin: LiveData<Boolean> = _doLogin

    private fun setLoading(res: Boolean) {
        _loading.value = res
    }

    fun setDoLogin(res: Boolean) {
        _doLogin.value = res
    }

    fun setMessage(res: String?) {
        _message.value = res
    }

    fun setNavigateToNext(res: Int) {
        _navigateToNext.value = res
    }

    fun login(email: String, password: String) {

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setMessage(context.getString(R.string.error_email))
            return
        }

        if (TextUtils.isEmpty(password)) {
            setMessage(context.getString(R.string.error_password))
            return
        }

        setLoading(true)
        setMessage(null)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = apiService.loginAsync(email, password).await()
                    withContext(Dispatchers.Main) {
                        if (!TextUtils.isEmpty(result.token)) {
                            CommonUtils.setPreference(context, Constants.EMAIL, email)
                            setMessage(context.getString(R.string.login_done))
                            setNavigateToNext(1)
                        } else {
                            setMessage(context.getString(R.string.login_failed))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("", e.printStackTrace().toString())
                    withContext(Dispatchers.Main) {
                        setMessage(context.getString(R.string.login_failed) + " : " + e.localizedMessage)
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        setLoading(false)
                    }
                }
            }
        }
    }

}