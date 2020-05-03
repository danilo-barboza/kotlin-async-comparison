package org.kotlinlang.example

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Service
class ReactorClient {
	val webClient: WebClient = WebClient.create("https://httpbin.org/")

	fun getUserAgent(): Mono<String> {
		return webClient
			.get()
			.uri("user-agent")
			.retrieve()
			.bodyToMono()
	}

	fun processUserAgent(body: String, name: String): Mono<String> {
		val fullName = "reactorWebClient($name)"
		logDelayedRequest(fullName)

		return webClient.post()
			.uri("delay/1")
			.body(BodyInserters.fromValue(body))
			.retrieve()
			.bodyToMono<String>()
			.map { "$fullName result: ${parseDelayedResponse(it)}" }
	}
}

