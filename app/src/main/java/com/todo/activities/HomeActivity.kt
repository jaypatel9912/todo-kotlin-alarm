package com.todo.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.todo.R
import com.todo.adapters.ItemClickListener
import com.todo.adapters.ToDosAdapter
import com.todo.databinding.ActivityHomeBinding
import com.todo.model.ToDoItem
import com.todo.repository.TodoDatabase
import com.todo.utils.CommonUtils
import com.todo.utils.CommonUtils.toast
import com.todo.utils.Constants
import com.todo.viewmodel.HomeViewModel
import com.todo.viewmodelfactory.HomeViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    lateinit var activity: HomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@HomeActivity

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        supportActionBar?.title = getString(R.string.home)

        homeViewModel = ViewModelProvider(
            activity,
            HomeViewModelFactory(activity, TodoDatabase.getInstance(application).toDoDuo)
        ).get(HomeViewModel::class.java)

        binding.viewModel = homeViewModel

        val adapter = ToDosAdapter(ItemClickListener { delete, item ->
            if (delete) {
                deleteItem(item)
            } else {
                navigateToAddUpdateToDo(item)
            }
        })

        binding.listToDo.adapter = adapter

        homeViewModel.toDoList.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.listToDo.visibility = View.GONE
                binding.empty.visibility = View.VISIBLE
            } else {
                binding.listToDo.visibility = View.VISIBLE
                binding.empty.visibility = View.GONE
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
            homeViewModel.setLoading(false)
        })

        homeViewModel.loading.observe(this, {
            if (it) {
                CommonUtils.showProgressBar(activity)
            } else {
                CommonUtils.dismissProgressBar()
            }
        })

        homeViewModel.addToDo.observe(this, {
            if (it) {
                navigateToAddUpdateToDo(null)
                homeViewModel.setAddToDo(false)
            }
        })

        homeViewModel.message.observe(this, {
            if (!it.isNullOrEmpty()) {
                toast(it)
                homeViewModel.setMessage("")
            }
        })
    }

    private fun deleteItem(todoItem: ToDoItem) {
        val parentLayout = findViewById<View>(android.R.id.content)
        Snackbar.make(
            parentLayout,
            String.format(getString(R.string.ask_delete), todoItem.title),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(android.R.string.yes)) { homeViewModel.deleteToDo(todoItem) }
            .setDuration(5000)
            .show()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.listToDo()
    }

    private fun navigateToAddUpdateToDo(todoItem: ToDoItem?) {
        val intentToDo = Intent(activity, AddUpdateToDoActivity::class.java)
        if (todoItem != null)
            intentToDo.putExtra(Constants.TODO_ITEM, Gson().toJson(todoItem))
        startActivity(intentToDo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(
            Menu.FLAG_ALWAYS_PERFORM_CLOSE,
            10,
            Menu.NONE,
            getString(R.string.logout)
        )

        menu.findItem(10)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            10 -> {
                CommonUtils.setPreference(activity, Constants.EMAIL, "")
                val intent = Intent(activity, LoginActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(intent)
                finish()
            }
        }
        return true
    }
}