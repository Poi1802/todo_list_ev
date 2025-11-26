package com.example.todolistev

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.style.TtsSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistev.databinding.ActivityMainBinding
import com.example.todolistev.databinding.CustomDialogLayoutBinding
import com.example.todolistev.presentation.TaskAdapter
import com.example.todolistev.presentation.TaskViewModel
import com.example.todolistev.presentation.TaskViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.SimpleFormatter

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Toast.makeText(this, "Разрешение \"Точные будильники\" предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var dialogBinding: CustomDialogLayoutBinding
    private lateinit var alarmManager: AlarmManager
    private val calendar: Calendar = Calendar.getInstance()


    @SuppressLint("SimpleDateFormat")
    private val formatter = SimpleDateFormat("MMM. dd, yyyy HH:mm")

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if(!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            requestPermissionLauncher.launch(intent)
        }

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    // Устанавливаем клик события
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun setupClickListeners() {
        mainBinding.fabAdd.setOnClickListener { view ->
            showAddTaskDialog()
        }

        mainBinding.arrowBack.setOnClickListener { view ->
            val intent = Intent(this, MainNavActivity::class.java)

            startActivity(intent)

            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }
    }

    // Показывает диалог окно добаления задачи
    fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)
        dialogBinding = CustomDialogLayoutBinding.bind(dialogView)

//        Todo mb
//        val inputEditDescription = EditText(this)
//        inputEditTitle.hint = "Введите текст задачи"

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        alertDialog.show()

        dialogBinding.tvDueDate.setOnClickListener {
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()


        }
        dialogBinding.dialogAddBtn.setOnClickListener {
            val taskText = dialogBinding.inputTaskText.text.toString().trim()
            if (taskText.isNotEmpty() && calendar.timeInMillis > System.currentTimeMillis()) {
                addNewTask(taskText, calendar.timeInMillis)
                alertDialog.cancel()
            } else {
                if (taskText.isEmpty()) {
                    Toast.makeText(this, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Нельзя запланировать прошлое", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun displayFormatDate(timeInMillis: Long): String {
        return formatter.format(timeInMillis)
    }

    private fun addNewTask(taskText: String, dueDateInMilles: Long) {
        viewModel.addTask(
            this@MainActivity,
            taskDescription = taskText, dueDateInMilles = dueDateInMilles,
        )
    }

    // Наблюдаем за задачами и обновляем RecyclerView
    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.submitList(tasks)
                    updateEmptyState(tasks.isEmpty())
                }
            }
        }
    }

    // Обновление пустого состояния списка задач
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            mainBinding.textEmptyTasks.visibility = View.VISIBLE
            mainBinding.recyclerViewTasks.visibility = View.GONE
        } else {
            mainBinding.textEmptyTasks.visibility = View.GONE
            mainBinding.recyclerViewTasks.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskComplete = { task ->
                viewModel.toggleCompletionTask(task)
            },
            onTaskDelete = { task ->
                viewModel.deleteTask(task)
            },
            onTaskEdit = { task ->
                viewModel.updateTask(task)
            }
        )

        mainBinding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setupViewModel() {
        // Получаем доступ к контейнеру приложения
        val applicationContainer = (application as TaskListApplication)

        // Создаем ViewModel, передавая ей нужную зависимость (интерфейс)
        val factory = TaskViewModelFactory(applicationContainer.taskRepository)

        viewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        viewModel.alarmManager = alarmManager
    }

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int,
    ) {
        calendar.set(year, month, dayOfMonth)

        TimePickerDialog(
            this,
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

        dialogBinding.tvDueDate.text = displayFormatDate(calendar.timeInMillis)

    }
}