package com.example.todolistev.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistev.MainActivity
import com.example.todolistev.R
import com.example.todolistev.data.model.TaskEntity
import com.example.todolistev.databinding.CustomDialogLayoutBinding
import com.example.todolistev.databinding.CustomEditDialogLayoutBinding

class TaskViewHolder(
    itemView: View,
    private val onTaskComplete: (TaskEntity) -> Unit,
    private val onTaskDelete: (TaskEntity) -> Unit,
    private val onTaskEdit: (TaskEntity) -> Unit,
) : RecyclerView.ViewHolder(itemView) {
    private val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkbox_complete)
    private val textViewTitle: TextView = itemView.findViewById(R.id.tv_title)
    private val textViewDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val buttonDelete: ImageButton = itemView.findViewById(R.id.button_delete)
    private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
    private lateinit var editDialog: CustomEditDialogLayoutBinding

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
                    updateTextAppearance(task.isCompleted)
                }
            }
        }

        buttonDelete.setOnClickListener {
            currentTask?.let { task ->
                onTaskDelete(task)
            }
        }

        editButton.setOnClickListener {
            currentTask?.let { task ->
//                onTaskEdit(task)
                showEditTaskDialog(task)
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

    fun showEditTaskDialog(task: TaskEntity) {
        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.custom_edit_dialog_layout, null)
        editDialog = CustomEditDialogLayoutBinding.bind(dialogView)

        val alertDialog = AlertDialog.Builder(itemView.context)
            .setView(dialogView)
            .create()

        alertDialog.show()

        editDialog.inputTaskText.setText(task.taskDescription)

        editDialog.dialogAddBtn.setOnClickListener { view ->
            val taskText = editDialog.inputTaskText.text.toString().trim()
            if (taskText.isNotEmpty()) {
                onTaskEdit(task.copy(taskDescription = taskText))
                alertDialog.cancel()
            } else {
                Toast.makeText(itemView.context, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

