package com.example.todolistev.presentation

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistev.R
import com.example.todolistev.data.model.TaskEntity

class TaskAdapter(
    private val onTaskComplete: (TaskEntity) -> Unit,
    private val onTaskDelete: (TaskEntity) -> Unit,
    private val onTaskEdit: (TaskEntity) -> Unit
) : RecyclerView.Adapter<TaskViewHolder>() {

    private var tasks: List<TaskEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, onTaskComplete, onTaskDelete, onTaskEdit)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])

        if (tasks[position].isCompleted) {
            // Создаем перечеркнутый текст
            holder.textViewDescription.paintFlags = holder.textViewDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textViewDescription.setTextColor(Color.GRAY)
        } else {
            // Убираем перечеркивание
            holder.textViewDescription.paintFlags = holder.textViewDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textViewDescription.setTextColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun submitList(newTasks: List<TaskEntity>) {
        val diffResult = DiffUtil.calculateDiff(TaskDiffCallback(tasks, newTasks))
        tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
    }

    class TaskDiffCallback(
        private val oldList: List<TaskEntity>,
        private val newList: List<TaskEntity>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}