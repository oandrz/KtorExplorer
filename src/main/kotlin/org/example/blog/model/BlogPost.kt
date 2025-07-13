package org.example.blog.model

data class BlogPost(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String? = null
)