package com.example.todolistev

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistev.databinding.ActivityCompleteTasksBinding
import com.example.todolistev.databinding.CustomDialogLayoutBinding
import com.example.todolistev.presentation.TaskAdapter
import com.example.todolistev.presentation.TaskViewModel
import com.example.todolistev.presentation.TaskViewModelFactory
import kotlinx.coroutines.launch

class CompleteTasksActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var mainBinding: ActivityCompleteTasksBinding
    private lateinit var dialogBinding: CustomDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        mainBinding = ActivityCompleteTasksBinding.inflate(layoutInflater)
//        dialogBinding = CustomDialogLayoutBinding.inflate(layoutInflater)

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

    private fun setupViewModel() {
        // Получаем доступ к контейнеру приложения
        val applicationContainer = (application as TaskListApplication)

        // Создаем ViewModel, передавая ей нужную зависимость(интерфейс)
        val factory = TaskViewModelFactory(applicationContext, applicationContainer.taskRepository)

        viewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
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
            layoutManager = LinearLayoutManager(this@CompleteTasksActivity)
            adapter = this@CompleteTasksActivity.adapter
            addItemDecoration(
                (DividerItemDecoration(
                    this@CompleteTasksActivity,
                    DividerItemDecoration.VERTICAL
                ))
            )
        }
    }

    // Наблюдаем за задачами и обновляем RecyclerView
    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.completeTasks.collect { completeTasks ->
                    adapter.submitList(completeTasks)
                    updateEmptyState(completeTasks.isEmpty())
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

    private fun setupClickListeners() {
        mainBinding.arrowBack.setOnClickListener { _ ->
            val intent = Intent(this, MainNavActivity::class.java)

            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )

            startActivity(intent, options.toBundle())
        }
    }
}