package org.kotlinlang.example

import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

@Service
class CompletableFutureClient {
  val jdkHttpClient: HttpClient = HttpClient.newHttpClient()

  fun getUserAgent(): CompletableFuture<String> {
	return jdkHttpClient.sendAsync(
	  HttpRequest.newBuilder(URI.create("https://httpbin.org/user-agent"))
		.GET()
		.build(), HttpResponse.BodyHandlers.ofString()
	).thenApply { it.body() }
  }

  fun processUserAgent(userAgentResponse: String, name: String): CompletableFuture<String> {
	val fullName = "jdkHttpClient($name)"
	logDelayedRequest(fullName)

	return jdkHttpClient.sendAsync(
	  HttpRequest.newBuilder(URI.create("https://httpbin.org/delay/1"))
		.POST(HttpRequest.BodyPublishers.ofString(userAgentResponse))
		.build(), HttpResponse.BodyHandlers.ofString()
	).thenApply {
	  "$fullName result: ${parseDelayedResponse(it.body())}"
	}
  }

}