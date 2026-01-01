package org.example.blog.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UpdateBlogPostRequest(
    val title: String? = null,
    val content: String? = null,
    val publishedDate: Instant? = null
)