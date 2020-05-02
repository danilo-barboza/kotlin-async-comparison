package org.kotlinlang.example

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.boot.autoconfigure.SpringBootApplication
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
	val responseFuture: CompletableFuture<String> = completableFutureClient.getUserAgent()
	return responseFuture
	  .thenCompose { userAgent ->
		val processOne: CompletableFuture<String> = completableFutureClient.processUserAgent(userAgent, "one")
		val processTwo: CompletableFuture<String> = completableFutureClient.processUserAgent(userAgent, "two")
		processOne.thenCombine(processTwo) { r1, r2 -> CombinedResult(r1, r2) }
	  }
  }

  @GetMapping("/reactor")
  fun reactor(): Mono<CombinedResult> {
	logger.info("/parallel/reactor endpoint called")
	val responseFuture: Mono<String> = reactorClient.getUserAgent()
	return responseFuture
	  .flatMap { body ->
		val processOne: Mono<String> = reactorClient.processUserAgent(body, "one")
		val processTwo: Mono<String> = reactorClient.processUserAgent(body, "two")
		processOne.zipWith(processTwo) { r1, r2 -> CombinedResult(r1, r2) }

	  }
  }

  @GetMapping("/coroutines")
  suspend fun coroutines(): CombinedResult = coroutineScope {
	logger.info("/parallel/coroutines endpoint called")
	val userAgentResponse = coroutinesClient.getUserAgent()

	val processOne = async { coroutinesClient.processUserAgent(userAgentResponse, "one") }
	val processTwo = async { coroutinesClient.processUserAgent(userAgentResponse, "two") }

	CombinedResult(processOne.await(), processTwo.await())
  }

}
