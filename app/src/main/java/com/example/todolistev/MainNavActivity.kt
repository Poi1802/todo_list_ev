package com.example.todolistev

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolistev.databinding.ActivityMainNavBinding

class MainNavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainNavBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.tasksNavText.setOnClickListener { v ->
            val tasksIntent = Intent(this, MainActivity::class.java)

            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )

            startActivity(tasksIntent, options.toBundle())

        }

        binding.completeTasksNavText.setOnClickListener { v ->
            val completeTasksIntent = Intent(this, CompleteTasksActivity::class.java)

            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )

            startActivity(completeTasksIntent, options.toBundle())
        }
    }
}