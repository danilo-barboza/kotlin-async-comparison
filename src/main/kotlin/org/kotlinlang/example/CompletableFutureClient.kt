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

	fun fetchMostRecentOrderId(): CompletableFuture<String> {
		return jdkHttpClient.sendAsync(
			HttpRequest.newBuilder(URI.create("https://httpbin.org/uuid"))
				.GET()
				.build(), HttpResponse.BodyHandlers.ofString()
		).thenApply { parseUUIDResponse(it.body()) }
	}

	fun fetchDeliveryCost(orderId: String): CompletableFuture<String> {
		return fetchDelayedData(orderId, "Delivery cost")
	}

	fun fetchStockInformation(orderId: String): CompletableFuture<String> {
		return fetchDelayedData(orderId, "Stock")
	}

	private fun fetchDelayedData(orderId: String, operation: String): CompletableFuture<String> {
		val fullName = "jdkHttpClient($operation)"
		logDelayedRequest(fullName)

		return jdkHttpClient.sendAsync(
			HttpRequest.newBuilder(URI.create("https://httpbin.org/delay/1"))
				.POST(HttpRequest.BodyPublishers.ofString(delayedOperationRequest(operation, orderId)))
				.build(), HttpResponse.BodyHandlers.ofString()
		).thenApply { parseDelayedResponse(it.body()) }
	}

}