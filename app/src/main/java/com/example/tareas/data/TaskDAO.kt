package com.example.tareas.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.tareas.utils.DatabaseManager

//Data Access Object DAO
class TaskDAO(val context: Context) {

    lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    fun getContentValues(task: Task) : ContentValues {
        val values = ContentValues()
        values.put(Task.COLUMN_NAME_TITLE, task.title)
        values.put(Task.COLUMN_NAME_DONE, task.done)
        return values
    }

    fun cursorToEntity(cursor: Cursor) : Task{
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
        val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0
        return Task(id, title, done)

    }

    fun save(task: Task){
        if (task.id == -1){
            insert(task)
        }else {
            update(task)
        }
    }

    fun insert(task: Task) {
        // Gets the data repository in write mode
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insertOrThrow(Task.TABLE_NAME, null, values)
            Log.i("DATABASE", "Inserted a row with id $newRowId in ${Task.TABLE_NAME} ")
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun update(task: Task) {
        // Gets the data repository in write mode
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            val updateRows = db.update(
                Task.TABLE_NAME,
                values,
                "${Task.COLUMN_NAME_ID} = ${task.id}",
                null
            )
            Log.i("DATABASE", "Update a row with id ${task.id} in ${Task.TABLE_NAME} ")
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }

    }

    fun delete(task: Task) {
        delete(task.id)
    }
    fun delete(id: Int) {
        open()
        try {
            val deletedRows = db.delete(Task.TABLE_NAME, "${Task.COLUMN_NAME_ID} = $id", null)

            Log.i("DATABASE", "Deleted a row with id $id in ${Task.TABLE_NAME} ")
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun findById(id: Int): Task? {
        open()

        var task: Task? = null

        try {
            val cursor = db.query(              //Cursor permite acceder a datos de cada columna
                Task.TABLE_NAME,         // The table to query
                null,                 // The array of columns to return (pass null to get all)
                "${Task.COLUMN_NAME_ID} = $id",                // The columns for the WHERE clause
                null,            // The values for the WHERE clause
                null,                // don't group the rows
                null,                 // don't filter by row groups
                null                 // The sort order
            )
            if (cursor.moveToNext()) {
                task = cursorToEntity(cursor)
            }
        }catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }

        return task
    }

    fun findAll(): List<Task> {
        open()

        val taskList = mutableListOf<Task>()

        try {
            val cursor = db.query(              //Cursor permite acceder a datos de cada columna
                Task.TABLE_NAME,         // The table to query
                null,                 // The array of columns to return (pass null to get all)
                null,                // The columns for the WHERE clause
                null,            // The values for the WHERE clause
                null,                // don't group the rows
                null,                 // don't filter by row groups
                null                 // The sort order
            )

            while (cursor.moveToNext()) {
                val task = cursorToEntity(cursor)
                taskList.add(task)
            }
        }catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }

        return taskList
    }
}