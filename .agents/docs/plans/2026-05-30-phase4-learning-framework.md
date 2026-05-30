# Phase 4：学习框架 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现学习框架，包括进度条、课程容器、进度持久化、继续学习弹窗、线性锁定和退出确认

**Architecture:** 基于 DataStore 持久化学习进度，使用 StateFlow 驱动 UI，遵循 Swiss International 设计风格

**Tech Stack:** Kotlin, Jetpack Compose, DataStore, Material3

---

## 文件结构

| 文件 | 职责 |
|------|------|
| `ui/components/StepProgressBar.kt` | 步骤进度条组件 |
| `ui/components/ResumeOverlay.kt` | 继续学习/回顾弹窗 |
| `ui/components/ExitModal.kt` | 退出确认弹窗 |
| `screens/LessonScreen.kt` | 课程学习主页面 |
| `data/repository/ProgressRepository.kt` | 进度持久化（已有，需增强） |
| `navigation/AppNavigation.kt` | 添加 Lesson 路由 |

---

## Task 1: 增强 ProgressRepository

**Files:**
- Modify: `data/repository/ProgressRepository.kt`

- [ ] **Step 1: 更新 ProgressRepository 数据模型**

```kotlin
package com.neoconcept.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")

data class LessonProgress(
    val lessonId: String,
    val currentStep: Int = 0,
    val totalSteps: Int = 6,
    val completedSteps: List<Int> = emptyList(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val finished: Boolean = false
)

class ProgressRepository(private val context: Context) {
    private val gson = Gson()
    private val type = object : TypeToken<Map<String, LessonProgress>>() {}.type

    private object Keys {
        val PROGRESS_MAP = stringPreferencesKey("progress_map")
    }

    val progressMap: Flow<Map<String, LessonProgress>> =
        context.progressDataStore.data.map { preferences ->
            val json = preferences[Keys.PROGRESS_MAP] ?: "{}"
            try {
                gson.fromJson(json, type) ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }
        }

    fun getLessonProgress(lessonId: String): Flow<LessonProgress?> =
        progressMap.map { it[lessonId] }

    suspend fun startLesson(lessonId: String, totalSteps: Int = 6) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val updated = current.toMutableMap()
            updated[lessonId] = LessonProgress(
                lessonId = lessonId,
                currentStep = 0,
                totalSteps = totalSteps,
                completedSteps = emptyList(),
                lastAccessedAt = System.currentTimeMillis(),
                finished = false
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun completeStep(lessonId: String, stepIndex: Int) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            if (stepIndex !in progress.completedSteps) {
                val updated = current.toMutableMap()
                updated[lessonId] = progress.copy(
                    completedSteps = progress.completedSteps + stepIndex,
                    currentStep = stepIndex,
                    lastAccessedAt = System.currentTimeMillis()
                )
                preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
            }
        }
    }

    suspend fun undoCompleteStep(lessonId: String, stepIndex: Int) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            val updated = current.toMutableMap()
            updated[lessonId] = progress.copy(
                completedSteps = progress.completedSteps.filter { it != stepIndex },
                currentStep = stepIndex,
                finished = false,
                lastAccessedAt = System.currentTimeMillis()
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun goToStep(lessonId: String, stepIndex: Int) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            val updated = current.toMutableMap()
            updated[lessonId] = progress.copy(
                currentStep = stepIndex,
                lastAccessedAt = System.currentTimeMillis()
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun finishLesson(lessonId: String) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            val updated = current.toMutableMap()
            updated[lessonId] = progress.copy(
                finished = true,
                lastAccessedAt = System.currentTimeMillis()
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun resetLesson(lessonId: String) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val updated = current.toMutableMap()
            updated.remove(lessonId)
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    private fun loadMap(preferences: Preferences): Map<String, LessonProgress> {
        val json = preferences[Keys.PROGRESS_MAP] ?: "{}"
        return try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `cd android && ./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 2: 创建 StepProgressBar 组件

**Files:**
- Create: `ui/components/StepProgressBar.kt`

- [ ] **Step 1: 创建 StepProgressBar**

```kotlin
package com.neoconcept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StepProgressBar(
    current: Int,
    total: Int,
    completed: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    onStepPress: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0 until total) {
            val isCompleted = i in completed
            val isCurrent = i == current
            val isLocked = !isCompleted && !isCurrent

            StepItem(
                index = i,
                label = labels.getOrElse(i) { "" },
                isCompleted = isCompleted,
                isCurrent = isCurrent,
                isLocked = isLocked,
                onClick = { onStepPress?.invoke(i) }
            )
        }
    }
}

@Composable
private fun StepItem(
    index: Int,
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val dotSize = 32.dp
    val borderColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        isCurrent -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }
    val backgroundColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        isCurrent -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isCurrent -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .clickable(enabled = !isLocked) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isCompleted) "✓" else "${index + 1}",
                color = textColor,
                fontSize = 14.sp,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isLocked) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `cd android && ./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 3: 创建 ResumeOverlay 组件

**Files:**
- Create: `ui/components/ResumeOverlay.kt`

- [ ] **Step 1: 创建 ResumeOverlay**

```kotlin
package com.neoconcept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumeOverlay(
    visible: Boolean,
    completedSteps: Int,
    totalSteps: Int,
    lastAccessedAt: Long,
    onResume: () -> Unit,
    onRestart: () -> Unit
) {
    if (!visible) return

    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(lastAccessedAt))
    val progress = if (totalSteps > 0) completedSteps.toFloat() / totalSteps else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.onSurface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⟳",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "继续学习?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "上次学习于 $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "$completedSteps / $totalSteps 步骤已完成",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            NeoButton(
                text = "继续",
                onClick = onResume
            )

            Spacer(modifier = Modifier.height(8.dp))

            NeoButton(
                text = "重新开始",
                onClick = onRestart,
                variant = ButtonVariant.Text
            )
        }
    }
}

private fun Modifier.border(width: androidx.compose.ui.unit.Dp, color: androidx.compose.ui.graphics.Color): Modifier {
    return this.then(
        Modifier.background(androidx.compose.ui.graphics.Color.Transparent)
    )
}
```

- [ ] **Step 2: 编译验证**

Run: `cd android && ./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 4: 创建 ExitModal 组件

**Files:**
- Create: `ui/components/ExitModal.kt`

- [ ] **Step 1: 创建 ExitModal**

```kotlin
package com.neoconcept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ExitModal(
    visible: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "离开课程?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "学习进度将自动保存，你可以稍后继续",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                NeoButton(
                    text = "留下",
                    onClick = onCancel,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                NeoButton(
                    text = "离开",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `cd android && ./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 5: 创建 LessonScreen

**Files:**
- Create: `screens/LessonScreen.kt`

- [ ] **Step 1: 创建 LessonScreen 框架**

```kotlin
package com.neoconcept.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neoconcept.data.model.Lesson
import com.neoconcept.data.repository.ManifestRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.ui.components.ExitModal
import com.neoconcept.ui.components.ResumeOverlay
import com.neoconcept.ui.components.ScreenHeader
import com.neoconcept.ui.components.StepProgressBar
import kotlinx.coroutines.launch

private const val TOTAL_STEPS = 6
private val STEP_LABELS = listOf("课文", "填空", "词汇", "听力", "阅读", "口语")

@Composable
fun LessonScreen(
    lessonId: String,
    manifestRepository: ManifestRepository,
    progressRepository: ProgressRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val progressMap by progressRepository.progressMap.collectAsState(emptyMap())
    val progress = progressMap[lessonId]

    var showResume by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    val lesson = remember { manifestRepository.findLessonById(lessonId) }

    LaunchedEffect(lessonId) {
        if (progress != null && !progress.finished) {
            showResume = true
        } else if (progress == null) {
            progressRepository.startLesson(lessonId, TOTAL_STEPS)
        }
        initialized = true
    }

    val currentStep = progress?.currentStep ?: 0
    val completedSteps = progress?.completedSteps ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = lesson?.title ?: "课程",
            onBackClick = { showExit = true }
        )

        StepProgressBar(
            current = currentStep,
            total = TOTAL_STEPS,
            completed = completedSteps,
            labels = STEP_LABELS,
            onStepPress = { stepIndex ->
                if (stepIndex in completedSteps || stepIndex == currentStep) {
                    scope.launch {
                        progressRepository.goToStep(lessonId, stepIndex)
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                !initialized -> {
                    CircularProgressIndicator()
                }
                lesson == null -> {
                    Text(
                        text = "课程未找到",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = "Step ${currentStep + 1}: ${STEP_LABELS.getOrElse(currentStep) { "" }}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }

    ResumeOverlay(
        visible = showResume,
        completedSteps = completedSteps.size,
        totalSteps = TOTAL_STEPS,
        lastAccessedAt = progress?.lastAccessedAt ?: System.currentTimeMillis(),
        onResume = {
            showResume = false
            scope.launch {
                progressRepository.goToStep(lessonId, progress?.currentStep ?: 0)
            }
        },
        onRestart = {
            showResume = false
            scope.launch {
                progressRepository.resetLesson(lessonId)
                progressRepository.startLesson(lessonId, TOTAL_STEPS)
            }
        }
    )

    ExitModal(
        visible = showExit,
        onConfirm = {
            showExit = false
            onBack()
        },
        onCancel = { showExit = false }
    )
}
```

- [ ] **Step 2: 在 ManifestRepository 添加 findLessonById 方法**

在 `ManifestRepository.kt` 中添加：

```kotlin
fun findLessonById(lessonId: String): Lesson? {
    val currentManifest = manifest.value ?: return null
    for (book in currentManifest.books) {
        for (unit in book.units) {
            for (lesson in unit.lessons) {
                if (lesson.id == lessonId) {
                    return lesson
                }
            }
        }
    }
    return null
}
```

- [ ] **Step 3: 编译验证**

Run: `cd android && ./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 6: 更新导航添加 Lesson 路由

**Files:**
- Modify: `navigation/AppNavigation.kt`

- [ ] **Step 1: 添加 Lesson 路由**

```kotlin
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object CourseList : Screen("course_list", "课程", Icons.AutoMirrored.Filled.List)
    data object Stats : Screen("stats", "统计", Icons.Default.Star)
    data object Settings : Screen("settings", "设置", Icons.Default.Settings)
    data object Lesson : Screen("lesson/{lessonId}", "课程", Icons.Default.Star) {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
}
```

- [ ] **Step 2: 在 AppNavigation 添加 Lesson composable**

```kotlin
composable("lesson/{lessonId}") { backStackEntry ->
    val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
    LessonScreen(
        lessonId = lessonId,
        manifestRepository = manifestRepository,
        progressRepository = progressRepository,
        onBack = { navController.popBackStack() }
    )
}
```

- [ ] **Step 3: 更新 CourseListScreen 添加导航**

在 `LessonItem` 中添加点击事件：

```kotlin
@Composable
fun LessonItem(
    lesson: Lesson,
    onLessonClick: (String) -> Unit = {}
) {
    // ...existing code...
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = lesson.status != LessonStatus.LOCKED) {
                onLessonClick(lesson.id)
            }
        // ...existing code...
    )
}
```

- [ ] **Step 4: 编译验证**

Run: `cd android && ./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 7: 集成测试

- [ ] **Step 1: 编译并安装**

Run: `cd android && ./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk`

- [ ] **Step 2: 测试学习流程**

1. 打开 App，进入课程列表
2. 点击一个 Lesson
3. 验证 ResumeOverlay 显示（如有未完成进度）
4. 验证 StepProgressBar 显示正确
5. 点击返回，验证 ExitModal 显示
6. 选择"离开"，返回课程列表

- [ ] **Step 3: 验证进度持久化**

1. 再次进入同一 Lesson
2. 验证 ResumeOverlay 显示上次进度
3. 选择"继续"，验证从正确步骤开始

---

## 依赖关系图

```
Task 1 (ProgressRepository)
    ↓
Task 2 (StepProgressBar) ← Task 1
    ↓
Task 3 (ResumeOverlay) ← Task 1
    ↓
Task 4 (ExitModal)
    ↓
Task 5 (LessonScreen) ← Task 1, 2, 3, 4
    ↓
Task 6 (Navigation) ← Task 5
    ↓
Task 7 (Testing) ← Task 6
```
