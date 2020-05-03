package org.kotlinlang.example

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.springframework.stereotype.Service

@Service
class CoroutinesClient {
	val ktorHttpClient: HttpClient = HttpClient()

	suspend fun getUserAgent(): String {
		return ktorHttpClient.get("https://httpbin.org/user-agent")
	}

	suspend fun processUserAgent(userAgentResponse: String, name: String): String {
		val fullName = "coroutine-ktor-client($name)"
		logDelayedRequest(fullName)

		val delayResponse = ktorHttpClient.post<String>("https://httpbin.org/delay/1") {
			body = userAgentResponse
		}

		return "$fullName result: ${parseDelayedResponse(delayResponse)}"
	}
}