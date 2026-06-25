package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = NexusDatabase.getDatabase(application)
    private val ideaDao = db.ideaDao()
    private val taskDao = db.taskDao()
    private val securityLogDao = db.securityLogDao()

    private val _currentUserId = MutableStateFlow(-1)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val ideasFlow: StateFlow<List<IdeaVaultEntity>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) ideaDao.getIdeasByUser(userId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val tasksFlow: StateFlow<List<StudyTaskEntity>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) taskDao.getTasksByUser(userId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val securityLogsFlow: StateFlow<List<SecurityLogEntity>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) securityLogDao.getLogsByUser(userId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setUserId(userId: Int) {
        _currentUserId.value = userId
    }

    fun protectAndSaveIdea(userId: Int, userEmail: String, title: String, category: String, problem: String, solution: String, audience: String) {
        viewModelScope.launch {
            if (title.isBlank() || problem.isBlank() || solution.isBlank()) return@launch

            val now = System.currentTimeMillis()
            val fingerprint = SecurityUtils.generateWatermarkHash(userEmail, title.trim(), now)
            
            // Calculate complex patent readiness score algorithmically
            val scoreBase = 45
            val lengthBonus = minOf(30, (problem.length + solution.length) / 20)
            val categoryBonus = when (category) {
                "AI/ML" -> 15
                "Cybersecurity" -> 18
                "SaaS" -> 12
                else -> 10
            }
            val patentScore = minOf(99, scoreBase + lengthBonus + categoryBonus)

            val idea = IdeaVaultEntity(
                userId = userId,
                title = title.trim(),
                category = category,
                problemStatement = problem.trim(),
                proposedSolution = solution.trim(),
                targetAudience = audience.trim(),
                watermarkFingerprint = fingerprint,
                patentScore = patentScore,
                isEncrypted = true,
                createdAt = now
            )

            ideaDao.insertIdea(idea)
            securityLogDao.insertLog(
                SecurityLogEntity(
                    userId = userId,
                    actionType = "IDEA_SHIELDED",
                    details = "Cryptographic IP Watermark generated: $fingerprint (Patent Score: $patentScore%)"
                )
            )
        }
    }

    fun deleteIdea(idea: IdeaVaultEntity) {
        viewModelScope.launch {
            ideaDao.deleteIdea(idea)
            securityLogDao.insertLog(
                SecurityLogEntity(
                    userId = idea.userId,
                    actionType = "IDEA_ARCHIVED",
                    details = "Removed shielded idea: ${idea.title}"
                )
            )
        }
    }

    fun addStudyTask(userId: Int, title: String, subject: String, dueDate: String, priority: String) {
        viewModelScope.launch {
            if (title.isBlank()) return@launch
            val task = StudyTaskEntity(
                userId = userId,
                title = title.trim(),
                subject = subject.ifBlank { "General Study" },
                dueDate = dueDate.ifBlank { "No Deadline" },
                priority = priority
            )
            taskDao.insertTask(task)
        }
    }

    fun toggleTaskCompletion(task: StudyTaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }
}
