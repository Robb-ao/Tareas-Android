package com.example.tareas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tareas.R
import com.example.tareas.data.Task
import com.example.tareas.databinding.ItemTaskBinding


class TaskAdapter(
    var items: List<Task>,
    val onItemClick: (position: Int) -> Unit,
    val onItemCheck: (position: Int) -> Unit,
    val onItemEdit: (position: Int) -> Unit,
    val onItemDelete: (position: Int) -> Unit
) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.render(task)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.binding.doneCheckBox.setOnCheckedChangeListener { _, _ ->
            if (holder.binding.doneCheckBox.isPressed){
                onItemCheck(position)
            }
        }
        holder.binding.editButton.setOnClickListener {
            onItemEdit(position)
        }
        holder.binding.deleteButton.setOnClickListener {
            onItemDelete(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataSet: List<Task>) {
        items = dataSet
        notifyDataSetChanged()
    }
}
class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
    //1:11
    fun render(task: Task) {
        binding.titleTextView.text = task.title
        binding.doneCheckBox.isChecked = task.done
    }
}