package org.kotlinlang.example

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Service
class ReactorClient {
	val webClient: WebClient = WebClient.create("https://httpbin.org/")

	fun fetchMostRecentOrderId(): Mono<String> {
		return webClient
			.get()
			.uri("uuid")
			.retrieve()
			.bodyToMono<String>()
			.map { parseUUIDResponse(it) }
	}

	fun fetchDeliveryCost(orderId: String): Mono<String> {
		return fetchDelayedData(orderId, "Delivery cost")
	}

	fun fetchStockInformation(orderId: String): Mono<String> {
		return fetchDelayedData(orderId, "Stock")
	}

	private fun fetchDelayedData(orderId: String, operation: String): Mono<String> {
		val fullName = "reactorWebClient($operation)"
		logDelayedRequest(fullName)

		return webClient.post()
			.uri("delay/1")
			.body(BodyInserters.fromValue(delayedOperationRequest(operation, orderId)))
			.retrieve()
			.bodyToMono<String>()
			.map { parseDelayedResponse(it) }
	}
}

