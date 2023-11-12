package com.example.verochallenge.features.tasks

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.verochallenge.R
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.databinding.TaskItemRowBinding

class TasksAdapter(private val context: Context) :
    ListAdapter<Task, TasksAdapter.ViewHolder>(TaskDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TaskItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class ViewHolder(private val binding: TaskItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                taskTitle.text = task.tasks.title
                taskDescription.text = task.tasks.description
                taskString.text = task.tasks.task
                if (task.tasks.colorCode?.isNotEmpty() == true) {
                    binding.taskColor.setBackgroundColor(Color.parseColor(task.tasks.colorCode))
                } else {
                    binding.taskColor.setBackgroundColor(ContextCompat.getColor(context, R.color.default_color))
                }
            }

        }
    }

}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}