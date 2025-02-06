package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var isDarkMode by remember { mutableStateOf(false) }
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF000000)

    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp)) // Add spacer to move content lower
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "To-Do", style = MaterialTheme.typography.headlineMedium, color = textColor)
                Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
            }
            Spacer(modifier = Modifier.height(16.dp))
            ToDoListContent(textColor)
        }
    }
}

@Composable
fun ToDoListContent(textColor: Color) {
    var taskName by remember { mutableStateOf(TextFieldValue("")) }
    var taskNote by remember { mutableStateOf(TextFieldValue("")) }
    var taskDate by remember { mutableStateOf("") }
    var taskTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val taskList = remember { mutableStateListOf<Quadruple<String, String, String, String>>() }
    var editingTask by remember { mutableStateOf<Quadruple<String, String, String, String>?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    BasicTextField(
        value = taskName,
        onValueChange = { taskName = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(color = textColor),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                if (taskName.text.isEmpty()) {
                    Text("Enter task name", color = textColor.copy(alpha = 0.5f))
                }
                innerTextField()
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    BasicTextField(
        value = taskNote,
        onValueChange = { taskNote = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(color = textColor),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                if (taskNote.text.isEmpty()) {
                    Text("Enter task note", color = textColor.copy(alpha = 0.5f))
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

                TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        taskTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()

            },
            year, month, day
        ).show()
    }) {
        Text("Select Date & Time: $taskDate $taskTime", color = textColor)
    }

    Spacer(modifier = Modifier.height(8.dp))
    if (taskDate.isNotEmpty()) {
        Text(text = "Selected Date: $taskDate", color = textColor)
    }
    if (taskTime.isNotEmpty()) {
        Text(text = "Selected Time: $taskTime", color = textColor)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = {
        when {
            taskName.text.isBlank() -> {
                snackbarMessage = "Please enter a task name"
                showSnackbar = true
            }
            taskNote.text.isBlank() -> {
                snackbarMessage = "Please enter a task note"
                showSnackbar = true
            }
            taskDate.isEmpty() -> {
                snackbarMessage = "Please select a date"
                showSnackbar = true
            }
            taskTime.isEmpty() -> {
                snackbarMessage = "Please select a time"
                showSnackbar = true
            }
            else -> {
                taskList.add(Quadruple(taskName.text, taskDate, taskTime, taskNote.text))
                taskName = TextFieldValue("")
                taskNote = TextFieldValue("")
                taskDate = ""
                taskTime = ""
            }
        }
    }) {
        Text("Add Task", color = textColor)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "Task List:", style = MaterialTheme.typography.headlineSmall, color = textColor)
    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(taskList) { task ->
            TaskItem(taskName = task.first, taskDate = task.second, taskTime = task.third, taskNote = task.fourth, textColor = textColor, onEdit = {
                editingTask = task
            }) {
                taskList.remove(task)
            }
        }
    }

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
            },
            textColor = textColor
        )
    }

    if (showSnackbar) {
        Snackbar(
            action = {
                Button(onClick = { showSnackbar = false }) {
                    Text("Dismiss", color = textColor)
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(snackbarMessage, color = textColor)
        }
    }
}

@Composable
fun TaskItem(taskName: String, taskDate: String, taskTime: String, taskNote: String, textColor: Color, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = taskName, fontSize = 18.sp, color = textColor)
            Text(text = "$taskDate at $taskTime", fontSize = 14.sp, color = textColor)
            if (taskNote.isNotEmpty()) {
                Text(text = "Note: $taskNote", fontSize = 14.sp, color = textColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { onEdit() }, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Edit", color = textColor)
                }
                Button(onClick = { onRemove() }) {
                    Text("Remove", color = textColor)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun EditTaskDialog(task: Quadruple<String, String, String, String>, onDismiss: () -> Unit, onSave: (Quadruple<String, String, String, String>) -> Unit, textColor: Color) {
    var taskName by remember { mutableStateOf(TextFieldValue(task.first)) }
    var taskDate by remember { mutableStateOf(task.second) }
    var taskTime by remember { mutableStateOf(task.third) }
    var taskNote by remember { mutableStateOf(TextFieldValue(task.fourth)) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task", color = textColor) },
        text = {
            Column {
                BasicTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary),
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if (taskName.text.isEmpty()) {
                                Text("Enter task name", color = textColor.copy(alpha = 0.5f))
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = taskNote,
                    onValueChange = { taskNote = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary),
                    textStyle = LocalTextStyle.current.copy(color = textColor),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if (taskNote.text.isEmpty()) {
                                Text("Enter task note", color = textColor.copy(alpha = 0.5f))
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

                            TimePickerDialog(
                                context,
                                { _, selectedHour, selectedMinute ->
                                    taskTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()

                        },
                        year, month, day
                    ).show()
                }) {
                    Text("Select Date & Time", color = textColor)
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (taskDate.isNotEmpty()) {
                    Text(text = "Selected Date: $taskDate", color = textColor)
                }
                if (taskTime.isNotEmpty()) {
                    Text(text = "Selected Time: $taskTime", color = textColor)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(Quadruple(taskName.text, taskDate, taskTime, taskNote.text))
            }) {
                Text("Save", color = textColor)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}

// Data class Quadruple used for four properties
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)