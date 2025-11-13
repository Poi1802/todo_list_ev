package com.example.todolistev

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var dialogBinding: CustomDialogLayoutBinding

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

            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right, R.anim.slide_out_left)
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

        dialogBinding.dialogAddBtn.setOnClickListener { view ->
            val taskText = dialogBinding.inputTaskText.text.toString().trim()
            if (taskText.isNotEmpty()) {
                addNewTask(taskText)
                alertDialog.cancel()
            } else {
                Toast.makeText(this, "Текст не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNewTask(taskText: String) {
        viewModel.addTask(taskDescription = taskText)
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
            },
            onTaskEdit = { task ->
                viewModel.updateTask(task)
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