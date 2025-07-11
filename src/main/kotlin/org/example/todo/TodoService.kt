package org.example.todo

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

class TodoService {
    // Thread-safe storage with coroutine mutex
    private val todos = mutableListOf<Todo>()
    private val mutex = Mutex()

    // Get all todos with simulated delay
    suspend fun getAllTodos(): List<Todo> = withContext(Dispatchers.Default) {
        delay(500) // Simulate network/database delay
        mutex.withLock { todos.toList() }
    }

    // Get a single todo by ID
    suspend fun getTodoById(id: String): Todo? = withContext(Dispatchers.Default) {
        delay(300) // Simulate network/database delay
        mutex.withLock { todos.find { it.id == id } }
    }

    // Add a new todo
    suspend fun addTodo(title: String): Todo = withContext(Dispatchers.Default) {
        delay(400) // Simulate network/database delay
        val newTodo = Todo(title = title)
        mutex.withLock { todos.add(newTodo) }
        newTodo
    }

    // Toggle todo completion status
    suspend fun toggleTodo(id: String): Todo? = withContext(Dispatchers.Default) {
        delay(300) // Simulate network/database delay
        mutex.withLock {
            val index = todos.indexOfFirst { it.id == id }
            if (index != -1) {
                val updated = todos[index].copy(completed = !todos[index].completed)
                todos[index] = updated
                updated
            } else null
        }
    }

    // Delete a todo
    suspend fun deleteTodo(id: String): Boolean = withContext(Dispatchers.Default) {
        delay(300) // Simulate network/database delay
        mutex.withLock { todos.removeIf { it.id == id } }
    }
}
