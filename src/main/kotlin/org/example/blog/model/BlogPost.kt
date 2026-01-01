package org.example.blog.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogPost(
    val id: Int,
    val title: String,
    val content: String,
    @SerialName("created_date") val createdDate: Instant,
    @SerialName("published_date") val publishedDate: Instant? = null
)