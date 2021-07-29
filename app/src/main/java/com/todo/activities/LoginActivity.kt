package com.todo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.todo.R
import com.todo.databinding.ActivityLoginBinding
import com.todo.utils.CommonUtils
import com.todo.utils.CommonUtils.toast
import com.todo.viewmodel.LoginViewModel
import com.todo.viewmodelfactory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    lateinit var activity: LoginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@LoginActivity

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        loginViewModel = ViewModelProvider(
            activity,
            LoginViewModelFactory(activity)
        ).get(LoginViewModel::class.java)

        binding.viewModel = loginViewModel

        loginViewModel.doLogin.observe(this, {
            if (it) {
                CommonUtils.hideSoftKeyboard(activity)
                if (CommonUtils.isOnline(activity)) {
                    loginViewModel.login(
                        binding.email.text.toString().trim(),
                        binding.password.text.toString().trim()
                    )
                }
            }
        })

        loginViewModel.navigateToNext.observe(this, {
            if (it == 1) {
                var intent = Intent(activity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                loginViewModel.setNavigateToNext(0)
            }
        })

        loginViewModel.loading.observe(this, {
            if (it) {
                CommonUtils.showProgressBar(activity)
            } else {
                CommonUtils.dismissProgressBar()
            }
        })

        loginViewModel.message.observe(this, {
            if (!it.isNullOrEmpty()) {
                toast(it)
                loginViewModel.setMessage("")
            }
        })
    }
}
