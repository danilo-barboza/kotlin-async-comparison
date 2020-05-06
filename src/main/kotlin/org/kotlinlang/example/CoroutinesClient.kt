package org.kotlinlang.example

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.springframework.stereotype.Service

@Service
class CoroutinesClient {
	val ktorHttpClient: HttpClient = HttpClient()

	suspend fun fetchMostRecentOrderId(): String {
		val uuidResponse = ktorHttpClient.get<String>("https://httpbin.org/uuid")
		return parseUUIDResponse(uuidResponse)
	}

	suspend fun fetchDeliveryCost(orderId: String): String {
		return fetchDelayedData(orderId, "Delivery cost")
	}

	suspend fun fetchStockInformation(orderId: String): String {
		return fetchDelayedData(orderId, "Stock")
	}

	private suspend fun fetchDelayedData(orderId: String, operation: String): String {
		logDelayedRequest("coroutine-ktor-client($operation)")

		val delayResponse = ktorHttpClient.post<String>("https://httpbin.org/delay/1") {
			body = delayedOperationRequest(operation, orderId)
		}

		return parseDelayedResponse(delayResponse)
	}

}