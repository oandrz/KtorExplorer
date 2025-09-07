package org.example.blog.model

import kotlinx.serialization.Serializable

@Serializable
data class BlogPostRequestParam(
    val title: String,
    val content: String,
)