package org.example.blog.service

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.example.blog.model.BlogPost
import org.example.blog.model.BlogPostForm
import org.example.blog.model.BlogPostRequestParam

internal interface BlogApiService {
    suspend fun getBlogPosts(): List<BlogPost>
    suspend fun insertBlogPost(form: BlogPostForm): Boolean
}

private const val TABLE_NAME = "blog"
class BlogApiServiceImpl(private val supabaseClient: SupabaseClient) : BlogApiService {
    override suspend fun getBlogPosts(): List<BlogPost> {
        return emptyList()
    }

    override suspend fun insertBlogPost(form: BlogPostForm): Boolean {
        val result = supabaseClient.from(TABLE_NAME).insert(
            BlogPostRequestParam(form.title, form.content)
        ) {
            select()
        }.decodeList<BlogPost>().firstOrNull { it.title == form.title }
        println("testing andre the result after insert is ${result.toString()}")
        return result != null
    }
}