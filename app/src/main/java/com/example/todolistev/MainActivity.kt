package com.example.todolistev

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistev.databinding.ActivityMainBinding
import com.example.todolistev.presentation.TaskAdapter
import com.example.todolistev.presentation.TaskViewModel
import com.example.todolistev.presentation.TaskViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainBinding.root)


        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        mainBinding.fabAdd.setOnClickListener { view ->
            showAddTaskDialog()
        }
    }

    fun showAddTaskDialog() {
        val inputEditTitle = EditText(this)
        inputEditTitle.hint = "Введите название задачи"

//        Todo mb
//        val inputEditDescription = EditText(this)
//        inputEditTitle.hint = "Введите текст задачи"

        AlertDialog.Builder(this)
            .setTitle("Новая задача")
            .setMessage("Пожалуйста, введите текст для добавления в БД")
            .setView(inputEditTitle)
            .setPositiveButton("Добавить") { dialog, which ->
                val taskText = inputEditTitle.text.toString().trim()
                if (taskText.isNotEmpty()) {
                    addNewTask(taskText)
                } else {
                    Toast.makeText(this, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun addNewTask(taskText: String) {
        viewModel.addTask(taskDescription = taskText)
    }

    private fun setupObservers() {
        // Наблюдаем за задачами и обновляем RecyclerView
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.submitList(tasks)
                    updateEmptyState(tasks.isEmpty())
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if(isEmpty) {
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
            }
        )

        mainBinding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupViewModel() {
        // Получаем доступ к контейнеру приложения
        val applicationContainer = (application as TaskListApplication)

        // Создаем ViewModel, передавая ей нужную зависимость (интерфейс)
        val factory = TaskViewModelFactory(applicationContainer.taskRepository)

        viewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

    }
}