package com.example.todolistev.presentation

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistev.R
import com.example.todolistev.data.model.TaskEntity

class TaskViewHolder(
    itemView: View,
    private val onTaskComplete: (TaskEntity) -> Unit,
    private val onTaskDelete: (TaskEntity) -> Unit,
) : RecyclerView.ViewHolder(itemView) {
    private val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkbox_complete)
    private val textViewTitle: TextView = itemView.findViewById(R.id.tv_title)
    private val textViewDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val buttonDelete: ImageButton = itemView.findViewById(R.id.button_delete)

    private var currentTask: TaskEntity? = null

    init {
        setupClickListeners();
    }

    fun bind(task: TaskEntity) {
        currentTask = task
        textViewTitle.text = task.taskTitle
        textViewDescription.text = task.taskDescription
        checkBoxComplete.isChecked = task.isCompleted

        checkBoxComplete.setOnCheckedChangeListener(null)
        checkBoxComplete.isChecked = task.isCompleted
        setupClickListeners()
    }

    private fun setupClickListeners() {
        checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            currentTask?.let { task ->
                if (isChecked != task.isCompleted) {
                    onTaskComplete(task.copy(isCompleted = isChecked))
                }
            }
        }

        buttonDelete.setOnClickListener {
            currentTask?.let { task ->
                onTaskDelete(task)
            }
        }
    }

    private fun updateTextAppearance(isComplete: Boolean) {
        if (isComplete) {
            textViewTitle.paintFlags = textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textViewTitle.setTextColor(Color.GRAY)
        } else {
            textViewTitle.paintFlags = textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textViewTitle.setTextColor(Color.BLACK)
        }
    }
}

