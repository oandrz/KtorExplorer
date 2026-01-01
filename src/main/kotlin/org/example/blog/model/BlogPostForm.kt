package org.example.blog.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BlogPostForm(
    val title: String,
    val content: String,
    val publishingDate: LocalDate? = null
)