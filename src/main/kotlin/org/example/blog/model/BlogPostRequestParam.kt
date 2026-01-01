package org.example.blog.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BlogPostRequestParam(
    val title: String,
    val content: String,
    val publishedDate: Instant? = null
)