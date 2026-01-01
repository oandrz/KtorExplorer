package org.example.blog.model

import kotlinx.serialization.Serializable

@Serializable
data class BlogPostResponse(
    val message: String
)