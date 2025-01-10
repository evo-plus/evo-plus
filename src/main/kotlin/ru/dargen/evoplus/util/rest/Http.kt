package ru.dargen.evoplus.util.rest

import ru.dargen.evoplus.util.kotlin.cast
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.CompletableFuture

val HttpClient = java.net.http.HttpClient.newHttpClient()

fun <T> request(
    url: String, method: String = "GET",
    handler: BodyHandler<T> = BodyHandlers.discarding().cast(),
    publisher: BodyPublisher = BodyPublishers.noBody(),
    builder: HttpRequest.Builder.() -> Unit = {},
) = HttpClient.sendAsync(
    HttpRequest.newBuilder()
        .uri(URI(url))
        .method(method, publisher)
        .apply(builder)
        .build(),
    handler
)

fun <T> CompletableFuture<HttpResponse<T>>.extract(): CompletableFuture<T> = thenApply(HttpResponse<T>::body)