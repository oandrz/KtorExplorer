package org.example.blog

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.blog.model.BlogPostForm
import org.example.blog.service.BlogApiService
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Blog")
private const val POST_PARAM_TITLE = "title"
private const val POST_PARAM_CONTENT = "content"

internal fun Routing.configureBlogRouting(blogApiService: BlogApiService) {
    route("/blog") {
        get("/list") {
            try {
                val posts = blogApiService.getBlogPosts()
                call.respond(posts)
            } catch (e: Exception) {
                logger.error("Error getting blog posts", e)
                call.respond(HttpStatusCode.InternalServerError, "Error getting blog posts")
            }
        }

        post("/post") {
            try {
                val params = call.receiveParameters()
                val title = params[POST_PARAM_TITLE] ?: throw IllegalArgumentException("Title is required")
                val content = params[POST_PARAM_CONTENT] ?: throw IllegalArgumentException("Content is required")

                val form = BlogPostForm(
                    title = title,
                    content = content,
                    author = "oink"
                )
                blogApiService.insertBlogPost(form)
                    .takeIf { it } ?: throw IllegalStateException("Failed to insert blog post")

                call.respond(
                    status = HttpStatusCode.Created,
                    message = "Blog post created successfully"
                )
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Invalid request"
                )
            } catch (e: Exception) {
                logger.error("Error creating blog post", e)
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Error creating blog post"
                )
            }
        }
    }
}