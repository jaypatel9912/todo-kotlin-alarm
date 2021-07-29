package com.todo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.todo.databinding.ItemTodoBinding
import com.todo.model.ToDoItem

class ToDosAdapter(
    private val clickListListener: ItemClickListener,
) :
    ListAdapter<ToDoItem, ToDosAdapter.ViewHolder>(RequestItemDiffCallback()) {
    class ViewHolder private constructor(val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTodoBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            item: ToDoItem,
            clickListListener: ItemClickListener,
        ) {
            binding.clickListener = clickListListener
            binding.item = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListListener)
    }
}

class RequestItemDiffCallback : DiffUtil.ItemCallback<ToDoItem>() {
    override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
        return oldItem == newItem
    }
}

class ItemClickListener(val clickListener: (delete: Boolean, item: ToDoItem) -> Unit) {
    fun onClick(delete: Boolean, item: ToDoItem) = clickListener(delete, item)
}
