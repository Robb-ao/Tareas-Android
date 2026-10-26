package com.example.tareas.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tareas.R
import com.example.tareas.adapters.TaskAdapter
import com.example.tareas.data.Task
import com.example.tareas.data.TaskDAO
import com.example.tareas.databinding.ActivityMainBinding
import com.example.tareas.databinding.DialogTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: TaskAdapter

    lateinit var taskDAO: TaskDAO

    var taskList = listOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDAO = TaskDAO(this)

        taskList = taskDAO.findAll()

        adapter = TaskAdapter(taskList, ::onItemClick, ::onItemCheck, ::onItemEdit, ::onItemDelete)


        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addTaskButton.setOnClickListener {
            val task = Task(-1, "")
            showTaskDialog(task)
        }
    }

    fun reloadData() {
        //TODO: se puede optimizar
        taskList = taskDAO.findAll()
        adapter.updateData((taskList))
    }

    fun showTaskDialog(task: Task) {
        val dialogBinding = DialogTaskBinding.inflate(layoutInflater)

        dialogBinding.titleTextField.editText!!.setText(task.title)

        val dialogTitle = if (task.id == -1) "Nueva Tarea" else "Editar tarea"

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { dialog, which ->
                //No hacemos nada, lo dejamos para después de mostrar el dialogo
                //Es para eviar el cierre automaticamente
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                // Do something else.
            }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = dialogBinding.titleTextField.editText!!.text.toString()
            if (title.isEmpty()) {
                dialogBinding.titleTextField.error = "No se puede crear tares vacias"
            } else {
                task.title = title
                taskDAO.save(task)
                reloadData()

                dialog.dismiss()
            }
        }
    }

    fun onItemClick(position: Int) {
        onItemCheck(position)
    }

    fun onItemCheck(position: Int) {
        val task = taskList[position]
        task.done = !task.done
        taskDAO.update(task)

        reloadData()
    }

    fun onItemEdit(position: Int) {
        val task = taskList[position]
        showTaskDialog(task)
    }

    fun onItemDelete(position: Int) {
        val builder = MaterialAlertDialogBuilder(this)
            .setMessage("Borrar tarea")
            .setTitle("¿Estás seguro de querer borrar la tarea?")
            .setPositiveButton("Borrar") { dialog, which ->
                val task = taskList[position]
                taskDAO.delete(task)

                reloadData()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                // Do something else.
            }

        val dialog = builder.create()
        dialog.show()
    }
}