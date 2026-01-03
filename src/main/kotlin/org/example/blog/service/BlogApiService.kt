package org.example.blog.service

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.Instant
import org.example.blog.model.BlogPost
import org.example.blog.model.BlogPostForm
import org.example.blog.model.BlogPostRequestParam
import org.example.blog.model.UpdateBlogPostRequest

internal interface BlogApiService {
    suspend fun getBlogPosts(publishedDate: Instant?, requestId: Int? = null): List<BlogPost>
    suspend fun insertBlogPost(form: BlogPostForm): Boolean
    suspend fun updateBlogPost(id: Int, form: UpdateBlogPostRequest): Boolean
    suspend fun deleteBlogPost(id: Int): Boolean
}

private const val TABLE_NAME = "blog"
class BlogApiServiceImpl(private val supabaseClient: SupabaseClient) : BlogApiService {

    override suspend fun getBlogPosts(publishedDate: Instant?, requestId: Int?): List<BlogPost> {
        val result = supabaseClient.from(TABLE_NAME)
            .select {
                filter {
                    requestId?.let { id -> eq("id", id) }
                    // using date range since equal will match into millisecond
                    publishedDate?.let { date ->
                        print("testing andre received published date: $publishedDate")
                        // If you want posts from a specific day
                        gte("published_date", "${date}T00:00:00Z")
                        lt("published_date", "${date}T23:59:59Z")
                    }
                }
            }
            .decodeList<BlogPost>()

        return result.ifEmpty { emptyList() }
    }

    override suspend fun insertBlogPost(form: BlogPostForm): Boolean {
        val result = supabaseClient.from(TABLE_NAME).insert(
            BlogPostRequestParam(form.title, form.content)
        ) {
            select()
        }.decodeList<BlogPost>().firstOrNull { it.title == form.title }
        return result != null
    }

    override suspend fun updateBlogPost(id: Int, form: UpdateBlogPostRequest): Boolean {
        val result = supabaseClient.from(TABLE_NAME).update({
            form.title?.let { title -> set("title", title) }
            form.content?.let { content -> set("content", content) }
            form.publishedDate?.let { date -> set("published_date", date) }
        }) {
            filter { eq("id", id) }
            select()
        }
            .decodeSingleOrNull<BlogPost>()

        return result != null
    }

    override suspend fun deleteBlogPost(id: Int): Boolean {
        val result = supabaseClient.from(TABLE_NAME).delete {
            filter { eq("id", id) }
            select()
        }
            .decodeSingleOrNull<BlogPost>()

        return result != null
    }
}