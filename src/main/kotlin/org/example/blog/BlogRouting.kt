package org.example.blog

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.example.blog.model.BlogPostForm
import org.example.blog.model.BlogPostResponse
import org.example.blog.model.UpdateBlogPostRequest
import org.example.blog.service.BlogApiService
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Blog")

internal fun Routing.configureBlogRouting(blogApiService: BlogApiService) {
    route("/blog") {

        get("/list") {
            try {
                val publishedDate = call.request.queryParameters["publishedDate"]
                logger.debug("testing andre received published date: $publishedDate")
                val posts = withContext(Dispatchers.IO) {
                    blogApiService.getBlogPosts(
                        publishedDate = publishedDate?.let { Instant.parse(it) }
                    )
                }
                call.respond(
                    status = HttpStatusCode.OK,
                    message = posts
                )
            } catch (e: Exception) {
                logger.error("Error getting blog posts", e)
                call.respond(HttpStatusCode.InternalServerError, "Error getting blog posts")
            }
        }

        get("/list/{id}") {
            try {
                val requestedId = call.parameters["id"]
                val publishedDate = call.request.queryParameters["publishedDate"]
                val posts = blogApiService.getBlogPosts(
                    requestId = requestedId?.toIntOrNull(),
                    publishedDate = publishedDate?.let { Instant.parse(it) }
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = posts
                )
            } catch (e: Exception) {
                logger.error("Error getting blog posts", e)
                call.respond(HttpStatusCode.InternalServerError, "Error getting blog posts")
            }
        }

        post("/post") {
            try {
                val form = call.receive<BlogPostForm>()

                blogApiService.insertBlogPost(form)
                    .takeIf { it } ?: throw IllegalStateException("Failed to insert blog post")

                call.respond(
                    status = HttpStatusCode.Created,
                    message = BlogPostResponse(
                        message = "Blog post created successfully"
                    )
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

        put("/post/{id}") {
            try {
                val id: Int = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Blog post ID is required")

                val form = call.receive<UpdateBlogPostRequest>()

                blogApiService.updateBlogPost(id, form)
                    .takeIf { it } ?: throw IllegalStateException("Failed to update blog post")

                call.respond(
                    status = HttpStatusCode.OK,
                    message = BlogPostResponse(
                        message = "Blog post updated successfully"
                    )
                )
            } catch (e: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Invalid request"
                )
            } catch (e: Exception) {
                logger.error("Error update blog post", e)
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Error update blog post"
                )
            }
        }
    }
}