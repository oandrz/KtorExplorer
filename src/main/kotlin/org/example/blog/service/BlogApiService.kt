package org.example.blog.service

import org.example.blog.model.BlogPost
import org.example.blog.model.BlogPostForm

internal interface BlogApiService {
    suspend fun getBlogPosts(): List<BlogPost>
    suspend fun insertBlogPost(form: BlogPostForm): Boolean
}

class BlogApiServiceImpl : BlogApiService {
    override suspend fun getBlogPosts(): List<BlogPost> {
        return emptyList()
    }

    override suspend fun insertBlogPost(form: BlogPostForm): Boolean {
        return false
    }
}