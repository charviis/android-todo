package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListApp()
        }
    }
}

@Composable
fun ToDoListApp() {
    var taskName by remember { mutableStateOf(TextFieldValue("")) }
    var taskDate by remember { mutableStateOf("No Date Selected") }
    val context = LocalContext.current
    val taskList = remember { mutableStateListOf<Pair<String, String>>() }
    var editingTask by remember { mutableStateOf<Pair<String, String>?>(null) }

    val insets = with(LocalDensity.current) {
        val rootView = LocalView.current
        val windowInsets = ViewCompat.getRootWindowInsets(rootView)
        Log.d("com.example.myapplication.ToDoListApp", "windowInsets: $windowInsets")
        windowInsets?.getInsets(WindowInsetsCompat.Type.systemBars())?.top?.toDp() ?: 0.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets + 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Simple To-Do List", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Task input field
        BasicTextField(
            value = taskName,
            onValueChange = { taskName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (taskName.text.isEmpty()) {
                        Text("Enter Task Name", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date picker button
        Button(onClick = {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    taskDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                },
                year, month, day
            ).show()
        }) {
            Text("Select Date")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Selected Date: $taskDate")

        Spacer(modifier = Modifier.height(16.dp))

        // Add task button
        Button(onClick = {
            if (taskName.text.isNotBlank() && taskDate != "No Date Selected") {
                taskList.add(taskName.text to taskDate)
                taskName = TextFieldValue("")
                taskDate = "No Date Selected"
            }
        }) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task list
        Text(text = "Task List:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(taskList) { task ->
                TaskItem(taskName = task.first, taskDate = task.second, onEdit = {
                    editingTask = task
                }) {
                    taskList.remove(task)
                }
            }
        }

        // Edit task dialog
        if (editingTask != null) {
            EditTaskDialog(
                task = editingTask!!,
                onDismiss = { editingTask = null },
                onSave = { newTask ->
                    val index = taskList.indexOf(editingTask)
                    if (index != -1) {
                        taskList[index] = newTask
                    }
                    editingTask = null
                }
            )
        }
    }
}

@Composable
fun TaskItem(taskName: String, taskDate: String, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = taskName)
                Text(text = taskDate)
            }
            Row {
                Button(onClick = { onEdit() }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onRemove() }) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
fun EditTaskDialog(task: Pair<String, String>, onDismiss: () -> Unit, onSave: (Pair<String, String>) -> Unit) {
    var taskName by remember { mutableStateOf(TextFieldValue(task.first)) }
    var taskDate by remember { mutableStateOf(task.second) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column {
                BasicTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if (taskName.text.isEmpty()) {
                                Text("Enter Task Name", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    DatePickerDialog(
                        context,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            taskDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        },
                        year, month, day
                    ).show()
                }) {
                    Text("Select Date")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Selected Date: $taskDate")
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(taskName.text to taskDate)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}