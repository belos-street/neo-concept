# Phase 3: 课程管理（本地 Mock 版）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 使用本地 mock JSON 文件实现课程列表 UI，跳过网络请求和下载功能

**Architecture:** 
- 创建 mock manifest.json 放入 assets
- ManifestRepository 从 assets 读取 JSON
- CourseListScreen 展示 Book/Unit/Lesson 树形结构
- 跳过 DownloadManager 和 DownloadScreen

**Tech Stack:** Kotlin, Jetpack Compose, Gson/Moshi

---

## 文件结构

```
android/app/src/main/
├── assets/
│   └── mock/
│       └── manifest.json          # Mock 课程数据
├── java/com/neoconcept/
│   ├── data/
│   │   ├── model/
│   │   │   ├── CourseManifest.kt  # 课程数据模型
│   │   │   └── CourseItem.kt      # 单个课程项
│   │   └── repository/
│   │       └── ManifestRepository.kt
│   └── screens/
│       └── CourseListScreen.kt    # 更新 UI
```

---

## Task 1: 创建 Mock 数据

**Files:**
- Create: `android/app/src/main/assets/mock/manifest.json`

- [ ] **Step 1: 创建 mock manifest.json**

```json
{
  "version": "1.0.0",
  "lastUpdated": "2026-05-30T00:00:00Z",
  "books": [
    {
      "id": "book-1",
      "title": "新概念英语第一册",
      "description": "英语初阶",
      "coverUrl": "https://example.com/cover1.jpg",
      "units": [
        {
          "id": "unit-1-1",
          "title": "Unit 1: Excuse me!",
          "lessons": [
            {
              "id": "lesson-1-1-1",
              "title": "Lesson 1: Excuse me!",
              "description": "对不起！",
              "wordCount": 15,
              "status": "available"
            },
            {
              "id": "lesson-1-1-2",
              "title": "Lesson 2: Is this your...?",
              "description": "这是你的...吗？",
              "wordCount": 18,
              "status": "available"
            }
          ]
        },
        {
          "id": "unit-1-2",
          "title": "Unit 2: Sorry, sir.",
          "lessons": [
            {
              "id": "lesson-1-2-1",
              "title": "Lesson 3: Sorry, sir.",
              "description": "对不起，先生。",
              "wordCount": 12,
              "status": "downloaded"
            }
          ]
        }
      ]
    },
    {
      "id": "book-2",
      "title": "新概念英语第二册",
      "description": "实践与进步",
      "coverUrl": "https://example.com/cover2.jpg",
      "units": [
        {
          "id": "unit-2-1",
          "title": "Unit 1: A private conversation",
          "lessons": [
            {
              "id": "lesson-2-1-1",
              "title": "Lesson 1: A private conversation",
              "description": "私人谈话",
              "wordCount": 25,
              "status": "locked"
            }
          ]
        }
      ]
    }
  ]
}
```

---

## Task 2: 创建数据模型

**Files:**
- Create: `android/app/src/main/java/com/neoconcept/data/model/CourseManifest.kt`

- [ ] **Step 1: 创建 CourseManifest 数据类**

```kotlin
package com.neoconcept.data.model

import com.google.gson.annotations.SerializedName

data class CourseManifest(
    val version: String,
    val lastUpdated: String,
    val books: List<Book>
)

data class Book(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val units: List<Unit>
)

data class Unit(
    val id: String,
    val title: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val wordCount: Int,
    val status: LessonStatus
)

enum class LessonStatus {
    @SerializedName("available")
    AVAILABLE,
    @SerializedName("downloaded")
    DOWNLOADED,
    @SerializedName("locked")
    LOCKED,
    @SerializedName("update_available")
    UPDATE_AVAILABLE
}
```

---

## Task 3: 添加 Gson 依赖

**Files:**
- Modify: `android/app/build.gradle.kts`

- [ ] **Step 1: 添加 Gson 依赖**

在 dependencies 块中添加：
```kotlin
implementation("com.google.code.gson:gson:2.10.1")
```

---

## Task 4: 创建 ManifestRepository

**Files:**
- Create: `android/app/src/main/java/com/neoconcept/data/repository/ManifestRepository.kt`

- [ ] **Step 1: 创建 ManifestRepository**

```kotlin
package com.neoconcept.data.repository

import android.content.Context
import com.google.gson.Gson
import com.neoconcept.data.model.CourseManifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class ManifestRepository(private val context: Context) {
    
    private val _manifest = MutableStateFlow<CourseManifest?>(null)
    val manifest: StateFlow<CourseManifest?> = _manifest
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val gson = Gson()
    
    suspend fun loadManifest() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _error.value = null
        
        try {
            val json = context.assets.open("mock/manifest.json")
                .bufferedReader()
                .use { it.readText() }
            
            val manifest = gson.fromJson(json, CourseManifest::class.java)
            _manifest.value = manifest
        } catch (e: Exception) {
            _error.value = "Failed to load manifest: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}
```

---

## Task 5: 更新 CourseListScreen

**Files:**
- Modify: `android/app/src/main/java/com/neoconcept/screens/CourseListScreen.kt`

- [ ] **Step 1: 更新 CourseListScreen 显示树形数据**

```kotlin
package com.neoconcept.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neoconcept.data.model.Book
import com.neoconcept.data.model.Lesson
import com.neoconcept.data.model.LessonStatus
import com.neoconcept.data.model.Unit
import com.neoconcept.data.repository.ManifestRepository
import com.neoconcept.ui.components.ScreenHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(manifestRepository: ManifestRepository) {
    val manifest by manifestRepository.manifest.collectAsState()
    val isLoading by manifestRepository.isLoading.collectAsState()
    val error by manifestRepository.error.collectAsState()
    
    LaunchedEffect(Unit) {
        manifestRepository.loadManifest()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("课程列表") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                manifest != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        manifest?.books?.forEach { book ->
                            item {
                                BookItem(book = book)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown 
                                 else Icons.Filled.KeyboardArrowRight,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            
            if (expanded) {
                book.units.forEach { unit ->
                    UnitItem(unit = unit)
                }
            }
        }
    }
}

@Composable
fun UnitItem(unit: Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = unit.title,
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowDown 
                             else Icons.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp)
            )
        }
        
        if (expanded) {
            unit.lessons.forEach { lesson ->
                LessonItem(lesson = lesson)
            }
        }
    }
}

@Composable
fun LessonItem(lesson: Lesson) {
    val statusColor = when (lesson.status) {
        LessonStatus.DOWNLOADED -> MaterialTheme.colorScheme.primary
        LessonStatus.LOCKED -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    val statusText = when (lesson.status) {
        LessonStatus.DOWNLOADED -> "已下载"
        LessonStatus.LOCKED -> "锁定"
        LessonStatus.UPDATE_AVAILABLE -> "有更新"
        else -> "${lesson.wordCount} 词"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = lesson.status != LessonStatus.LOCKED) { }
            .padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
            Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor
        )
    }
}
```

---

## Task 6: 更新 MainActivity 注入依赖

**Files:**
- Modify: `android/app/src/main/java/com/neoconcept/MainActivity.kt`

- [ ] **Step 1: 添加 ManifestRepository 初始化**

在 onCreate 中添加：
```kotlin
val manifestRepository = ManifestRepository(this)
```

- [ ] **Step 2: 更新 AppNavigation 传递参数**

修改 AppNavigation 调用，传入 manifestRepository

---

## 执行顺序

1. Task 1: 创建 Mock 数据
2. Task 2: 创建数据模型
3. Task 3: 添加 Gson 依赖
4. Task 4: 创建 ManifestRepository
5. Task 5: 更新 CourseListScreen
6. Task 6: 更新 MainActivity

## 验证

运行 App 后，点击"课程"Tab，应该看到：
- 两个书籍卡片（新概念第一册、第二册）
- 点击展开显示 Unit
- 再点击 Unit 展开显示 Lesson
- Lesson 显示状态标识（已下载/锁定/词数）
