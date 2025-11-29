package com.example.todolistev.presentation

import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistev.MainActivity
import com.example.todolistev.R
import com.example.todolistev.Utils
import com.example.todolistev.data.model.TaskEntity
import com.example.todolistev.databinding.CustomEditDialogLayoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class TaskViewHolder(
    itemView: View,
    private val onTaskComplete: (TaskEntity) -> Unit,
    private val onTaskDelete: (TaskEntity) -> Unit,
    private val onTaskEdit: (TaskEntity) -> Unit,
) : RecyclerView.ViewHolder(itemView), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkbox_complete)
    private val textViewTitle: TextView = itemView.findViewById(R.id.tv_title)
    val textViewDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val buttonDelete: ImageButton = itemView.findViewById(R.id.button_delete)
    private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
    private val dueDate: TextView = itemView.findViewById(R.id.tv_date)
    private lateinit var editDialog: CustomEditDialogLayoutBinding

    private val formatter = SimpleDateFormat("MMM. dd, yyyy HH:mm")
    private val calendar = Calendar.getInstance()


    private var _currentTask: TaskEntity? = null
    private val currentTask: TaskEntity
        get() = _currentTask ?: throw IllegalStateException("current task must not be null")

    init {
        setupClickListeners()
    }

    fun bind(task: TaskEntity) {
        _currentTask = task
        textViewTitle.text = task.taskTitle
        textViewDescription.text = task.taskDescription
        checkBoxComplete.isChecked = task.isCompleted
        dueDate.text = "До: " + formatter.format(task.taskDueDate)

        checkBoxComplete.setOnCheckedChangeListener(null)
        checkBoxComplete.isChecked = task.isCompleted
        setupClickListeners()
    }

    private fun setupClickListeners() {
        checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            currentTask.let { task ->
                if (isChecked != task.isCompleted) {
                    onTaskComplete(task.copy(isCompleted = isChecked))
                    updateTextAppearance(task)
                }
            }
        }

        buttonDelete.setOnClickListener {
            onTaskDelete(currentTask)
        }

        editButton.setOnClickListener {
            showEditTaskDialog(currentTask)

        }

        textViewDescription.setOnClickListener {
            showEditTaskDialog(currentTask)
        }
    }

    private fun updateTextAppearance(task: TaskEntity) {

        if (task.isCompleted) {
            // Создаем перечеркнутый текст
            textViewDescription.paintFlags =
                textViewDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textViewDescription.setTextColor(Color.GRAY)
        } else {
            // Убираем перечеркивание
            textViewDescription.paintFlags =
                textViewDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textViewDescription.setTextColor(Color.WHITE)
        }
    }

    fun showEditTaskDialog(task: TaskEntity) {
        val dialogView =
            LayoutInflater.from(itemView.context).inflate(R.layout.custom_edit_dialog_layout, null)
        editDialog = CustomEditDialogLayoutBinding.bind(dialogView)

        val alertDialog = AlertDialog.Builder(itemView.context)
            .setView(dialogView)
            .create()

        alertDialog.show()

        editDialog.inputTaskText.setText(task.taskDescription)
        editDialog.tvDueDate.text = dueDate.text


        editDialog.tvDueDate.setOnClickListener {
            showDatePickerDialog(task)
        }
        editDialog.dialogAddBtn.setOnClickListener {
            val taskText = editDialog.inputTaskText.text.toString().trim()
            if (taskText.isNotEmpty() && calendar.timeInMillis > System.currentTimeMillis()) {
                onTaskEdit(
                    task.copy(
                        taskDescription = taskText,
                        taskDueDate = calendar.timeInMillis
                    )
                )
                alertDialog.cancel()
            } else {
                if (taskText.isEmpty()) {
                    Toast.makeText(itemView.context, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
                }
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    Toast.makeText(itemView.context, "Нельзя запланировать прошлое", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun showDatePickerDialog(task: TaskEntity) {
        if (task.taskDueDate > 0) {
            calendar.timeInMillis = task.taskDueDate;
        }

        DatePickerDialog(
            editDialog.root.context,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    private fun displayFormatDate(timeInMillis: Long): String {
        return formatter.format(timeInMillis)
    }

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int,
    ) {
        calendar.set(year, month, dayOfMonth)

        TimePickerDialog(
            editDialog.root.context,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
            .show()
    }

    override fun onTimeSet(
        view: TimePicker?,
        hourOfDay: Int,
        minute: Int
    ) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

        editDialog.tvDueDate.text = displayFormatDate(calendar.timeInMillis)

    }
}

