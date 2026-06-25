package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyTaskEntity
import com.example.data.UserEntity
import com.example.ui.MainViewModel

@Composable
fun StudyToolsView(
    user: UserEntity,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasksFlow.collectAsState()
    var showAddTaskModal by remember { mutableStateOf(false) }

    val completedCount = remember(tasks) { tasks.count { it.isCompleted } }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskModal = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.AddTask, contentDescription = "Add Assignment")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Student Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Academic Assistant", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text(user.universityName, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Progress: $completedCount / ${tasks.size} Assignments Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Study Plan & Deadlines",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No assignments queued.", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Text("Tap + to add exam prep, lab reports, or project deadlines.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskModal) {
        AddTaskModal(
            onDismiss = { showAddTaskModal = false },
            onSubmit = { title, sub, date, prio ->
                viewModel.addStudyTask(user.id, title, sub, date, prio)
                showAddTaskModal = false
            }
        )
    }
}

@Composable
fun TaskItemCard(task: StudyTaskEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    val priorityColor = when (task.priority) {
        "High" -> MaterialTheme.colorScheme.error
        "Medium" -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (task.isCompleted) 0.3f else 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = priorityColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(task.priority, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = priorityColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(task.subject, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Due: ${task.dueDate}", fontSize = 11.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskModal(onDismiss: () -> Unit, onSubmit: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }

    val priorities = listOf("High", "Medium", "Low")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 30.dp)
        ) {
            Text("Add Academic / Study Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task / Assignment Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Course / Subject Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (e.g. Tomorrow 5 PM)") }, leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("Priority Level:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                priorities.forEach { prio ->
                    FilterChip(
                        selected = priority == prio,
                        onClick = { priority = prio },
                        label = { Text(prio) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onSubmit(title, subject, dueDate, priority) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Academic Task", fontWeight = FontWeight.Bold)
            }
        }
    }
}
