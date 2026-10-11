
package com.example.studentorganizer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Assignments : Screen("assignments")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object AddAssignment : Screen("add_assignment")
}

data class Assignment(
    val id: Int,
    val title: String,
    val course: String,
    val dueDate: String,
    val priority: String,
    val description: String = "",
    val reminderEnabled: Boolean = false,
    val isCompleted: Boolean = false
)

data class Course(
    val name: String,
    val code: String,
    val instructor: String,
    val progress: Float
)

@Composable
fun StudentOrganizerApp() {
    var darkMode by remember { mutableStateOf(false) }
    var defaultStartScreen by remember { mutableStateOf("Dashboard") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    val assignments = remember {
        mutableStateListOf(
            Assignment(1, "DSA Practice", "CS201", "Oct 13", "High"),
            Assignment(2, "SQL Assignment", "CS302", "Oct 15", "Medium"),
            Assignment(3, "ML Worksheet", "CS305", "Oct 16", "High"),
            Assignment(4, "OS Lab Report", "CS204", "Oct 18", "Low"),
            Assignment(5, "Web Development Task", "CS210", "Oct 19", "Medium"),
            Assignment(6, "DBMS Quiz Preparation", "CS302", "Oct 20", "Low"),
            Assignment(7, "Algorithms Review", "CS201", "Oct 22", "Medium")
        )
    }
    val courses = remember {
        listOf(
            Course("Data Structures", "CS201", "Dr. Kumar", 0.65f),
            Course("Database Systems", "CS302", "Dr. Rao", 0.40f),
            Course("Machine Learning", "CS305", "Dr. Shah", 0.30f),
            Course("Operating Systems", "CS204", "Dr. Mehta", 0.75f)
        )
    }

    MaterialTheme(colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()) {
        // Recreate navigation when the user changes the preferred start screen.
        key(defaultStartScreen) {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            Scaffold(
                bottomBar = {
                    if (currentRoute != Screen.AddAssignment.route) {
                        BottomBar(navController, currentRoute)
                    }
                }
            ) { outerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = if (defaultStartScreen == "Assignments") Screen.Assignments.route else Screen.Dashboard.route,
                    modifier = Modifier.padding(outerPadding)
                ) {
                    composable(Screen.Dashboard.route) {
                        DashboardScreen(
                            courses = courses,
                            assignments = assignments,
                            onToggleComplete = { id -> toggleCompletion(assignments, id) },
                            onAdd = { navController.navigate(Screen.AddAssignment.route) }
                        )
                    }
                    composable(Screen.Assignments.route) {
                        AssignmentsScreen(
                            assignments = assignments,
                            onToggleComplete = { id -> toggleCompletion(assignments, id) },
                            onAdd = { navController.navigate(Screen.AddAssignment.route) }
                        )
                    }
                    composable(Screen.Profile.route) { ProfileScreen() }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            darkMode = darkMode,
                            onDarkModeChange = { darkMode = it },
                            notificationsEnabled = notificationsEnabled,
                            onNotificationsChange = { notificationsEnabled = it },
                            defaultStartScreen = defaultStartScreen,
                            onDefaultStartScreenChange = { defaultStartScreen = it }
                        )
                    }
                    composable(Screen.AddAssignment.route) {
                        AddAssignmentScreen(
                            onCancel = { navController.popBackStack() },
                            onSave = { title, course, dueDate, priority, description, reminder ->
                                val nextId = (assignments.maxOfOrNull { it.id } ?: 0) + 1
                                assignments.add(
                                    Assignment(nextId, title, course, dueDate, priority, description, reminder)
                                )
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun toggleCompletion(assignments: SnapshotStateList<Assignment>, id: Int) {
    val index = assignments.indexOfFirst { it.id == id }
    if (index >= 0) assignments[index] = assignments[index].copy(isCompleted = !assignments[index].isCompleted)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, canNavigateBack: Boolean, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavHostController, currentRoute: String?) {
    val tabs = listOf(
        Screen.Dashboard to "Dashboard",
        Screen.Assignments to "Assignments",
        Screen.Profile to "Profile",
        Screen.Settings to "Settings"
    )
    NavigationBar {
        tabs.forEach { (screen, label) ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Text(label.take(1)) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun CourseInfo(courseName: String, courseCode: String, instructor: String, progress: Float) {
    Card(modifier = Modifier.width(230.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(courseName, fontWeight = FontWeight.Bold)
            Text(courseCode)
            Text("Instructor: $instructor", style = MaterialTheme.typography.bodySmall)
            Text("Progress: ${(progress.coerceIn(0f, 1f) * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            androidx.compose.material3.LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AssignmentCard(
    title: String,
    course: String,
    dueDate: String,
    priority: String,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text("Course: $course")
                Text("Due: $dueDate")
                val priorityColor = when (priority) {
                    "High" -> Color(0xFFB3261E)
                    "Medium" -> Color(0xFF8A5700)
                    else -> Color(0xFF287D3C)
                }
                Surface(color = priorityColor, shape = CircleShape) {
                    Text("$priority Priority", color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
                if (isCompleted) Text("Completed", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
            }
            Checkbox(checked = isCompleted, onCheckedChange = { onToggleComplete() })
        }
    }
}

@Composable
fun StudentProfile(studentName: String, studentId: String, major: String, avatarRes: Int) {
    // A generated initials avatar avoids requiring an external drawable resource.
    val initials = studentName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2)
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                BoxInitials(initials)
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(studentName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Student ID: $studentId")
                Text(major)
            }
        }
    }
}

@Composable
private fun BoxInitials(initials: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.width(64.dp).height(64.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials.ifBlank { "S" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DashboardScreen(
    courses: List<Course>,
    assignments: List<Assignment>,
    onToggleComplete: (Int) -> Unit,
    onAdd: () -> Unit
) {
    Scaffold(
        topBar = { AppBar("Dashboard", false, {}) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "Add assignment") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Welcome back, Student!", style = MaterialTheme.typography.headlineSmall) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.weight(1f)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Active Courses")
                            Text(courses.size.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(modifier = Modifier.weight(1f)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Pending Tasks")
                            Text(assignments.count { !it.isCompleted }.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item { Text("Active Courses", style = MaterialTheme.typography.titleLarge) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(courses) { course -> CourseInfo(course.name, course.code, course.instructor, course.progress) }
                }
            }
            item { Text("Urgent Deadlines", style = MaterialTheme.typography.titleLarge) }
            val pending = assignments.filter { !it.isCompleted }.take(2)
            if (pending.isEmpty()) {
                item { Text("No pending assignments.") }
            } else {
                items(pending, key = { it.id }) { assignment ->
                    AssignmentCard(
                        assignment.title, assignment.course, assignment.dueDate,
                        assignment.priority, assignment.isCompleted,
                        onToggleComplete = { onToggleComplete(assignment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AssignmentsScreen(assignments: List<Assignment>, onToggleComplete: (Int) -> Unit, onAdd: () -> Unit) {
    var filter by remember { mutableStateOf("All") }
    val filteredAssignments = when (filter) {
        "Pending" -> assignments.filter { !it.isCompleted }
        "Completed" -> assignments.filter { it.isCompleted }
        else -> assignments
    }
    Scaffold(
        topBar = { AppBar("My Assignments", false, {}) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "Add assignment") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("All", "Pending", "Completed").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = filter == option, onClick = { filter = option })
                        Text(option)
                    }
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredAssignments, key = { it.id }) { assignment ->
                    AssignmentCard(
                        assignment.title, assignment.course, assignment.dueDate,
                        assignment.priority, assignment.isCompleted,
                        onToggleComplete = { onToggleComplete(assignment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    Scaffold(topBar = { AppBar("Student Profile", false, {}) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            StudentProfile("Student Name", "PESU12345", "Computer Science", 0)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Academic Statistics", style = MaterialTheme.typography.titleLarge)
                    Text("GPA: 3.82")
                    Text("Completed Credits: 84")
                    Text("Current Semester: Fall 2026")
                }
            }
            Button(onClick = { Toast.makeText(context, "Edit Profile demo", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Profile")
            }
            OutlinedButton(onClick = { Toast.makeText(context, "Export Summary demo", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) {
                Text("Export Academic Summary")
            }
        }
    }
}

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    defaultStartScreen: String,
    onDefaultStartScreenChange: (String) -> Unit
) {
    Scaffold(topBar = { AppBar("Settings", false, {}) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Mode", modifier = Modifier.weight(1f))
                Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Push Notifications", modifier = Modifier.weight(1f))
                Checkbox(checked = notificationsEnabled, onCheckedChange = onNotificationsChange)
            }
            Text("Default Start Screen", style = MaterialTheme.typography.titleMedium)
            listOf("Dashboard", "Assignments").forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = defaultStartScreen == option, onClick = { onDefaultStartScreenChange(option) })
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun AssignmentForm(
    title: String,
    course: String,
    dueDate: String,
    priority: String,
    description: String,
    reminderEnabled: Boolean,
    onTitleChange: (String) -> Unit,
    onCourseChange: (String) -> Unit,
    onDueDateChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onReminderChange: (Boolean) -> Unit
) {
    OutlinedTextField(value = title, onValueChange = onTitleChange, label = { Text("Assignment Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = course, onValueChange = onCourseChange, label = { Text("Course Name / Code *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(
        value = dueDate,
        onValueChange = onDueDateChange,
        label = { Text("Due Date * (e.g. Oct 20)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") }
    )
    Text("Priority")
    Row(verticalAlignment = Alignment.CenterVertically) {
        listOf("Low", "Medium", "High").forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = priority == option, onClick = { onPriorityChange(option) })
                Text(option)
            }
        }
    }
    OutlinedTextField(
        value = description, onValueChange = onDescriptionChange,
        label = { Text("Description / Notes") }, modifier = Modifier.fillMaxWidth(),
        minLines = 3, maxLines = 5
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = reminderEnabled, onCheckedChange = onReminderChange)
        Text("Enable Reminder Notification")
    }
}

@Composable
fun AddAssignmentScreen(onCancel: () -> Unit, onSave: (String, String, String, String, String, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var description by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(false) }
    val canSave = title.isNotBlank() && course.isNotBlank() && dueDate.isNotBlank()

    Scaffold(topBar = { AppBar("Add New Assignment", true, onCancel) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AssignmentForm(
                    title, course, dueDate, priority, description, reminderEnabled,
                    onTitleChange = { title = it },
                    onCourseChange = { course = it },
                    onDueDateChange = { dueDate = it },
                    onPriorityChange = { priority = it },
                    onDescriptionChange = { description = it },
                    onReminderChange = { reminderEnabled = it }
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (canSave) onSave(title.trim(), course.trim(), dueDate.trim(), priority, description.trim(), reminderEnabled)
                        },
                        enabled = canSave,
                        modifier = Modifier.weight(1f)
                    ) { Text("Save Assignment") }
                }
                Text("* Required fields", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
