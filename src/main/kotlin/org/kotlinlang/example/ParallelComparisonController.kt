package org.kotlinlang.example

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture


@RestController
@RequestMapping("/parallel")
class ParallelComparisonController(
	val completableFutureClient: CompletableFutureClient,
	val reactorClient: ReactorClient,
	val coroutinesClient: CoroutinesClient
) {

	@GetMapping("/completable-future")
	fun completableFuture(): CompletableFuture<CombinedResult> {
		logger.info("/parallel/completable-future endpoint called")
		val userAgentFuture: CompletableFuture<String> = completableFutureClient.getUserAgent()
		return userAgentFuture
			.thenCompose { userAgent ->
				val processOne: CompletableFuture<String> = completableFutureClient.processUserAgent(userAgent, "one")
				val processTwo: CompletableFuture<String> = completableFutureClient.processUserAgent(userAgent, "two")
				processOne.thenCombine(processTwo) { r1, r2 -> CombinedResult(r1, r2) }
			}
	}

	@GetMapping("/reactor")
	fun reactor(): Mono<CombinedResult> {
		logger.info("/parallel/reactor endpoint called")
		val userAgentFuture: Mono<String> = reactorClient.getUserAgent()
		return userAgentFuture
			.flatMap { userAgent ->
				val processOne: Mono<String> = reactorClient.processUserAgent(userAgent, "one")
				val processTwo: Mono<String> = reactorClient.processUserAgent(userAgent, "two")
				processOne.zipWith(processTwo) { r1, r2 -> CombinedResult(r1, r2) }
			}
	}

	@GetMapping("/coroutines")
	suspend fun coroutines(): CombinedResult = coroutineScope {
		logger.info("/parallel/coroutines endpoint called")
		val userAgent: String = coroutinesClient.getUserAgent()

		val processOne: Deferred<String> = async { coroutinesClient.processUserAgent(userAgent, "one") }
		val processTwo: Deferred<String> = async { coroutinesClient.processUserAgent(userAgent, "two") }

		CombinedResult(processOne.await(), processTwo.await())
	}

}
