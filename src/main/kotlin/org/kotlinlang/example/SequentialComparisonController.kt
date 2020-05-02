package org.kotlinlang.example

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture


@RestController
@RequestMapping("/sequential")
class SequentialComparisonController(
  val completableFutureClient: CompletableFutureClient,
  val reactorClient: ReactorClient,
  val coroutinesClient: CoroutinesClient
) {

  @GetMapping("/completable-future")
  fun completableFuture(): CompletableFuture<String> {
	logger.info("/sequential/completable-future endpoint called")
	val responseFuture: CompletableFuture<String> = completableFutureClient.getUserAgent()
	return responseFuture
	  .thenCompose { userAgent -> completableFutureClient.processUserAgent(userAgent, "one") }
	  .thenCompose { processedUserAgent -> completableFutureClient.processUserAgent(processedUserAgent, "two") }
  }

  @GetMapping("/reactor")
  fun reactor(): Mono<String> {
	logger.info("/sequential/reactor endpoint called")
	val responseFuture: Mono<String> = reactorClient.getUserAgent()
	return responseFuture
	  .flatMap { userAgent -> reactorClient.processUserAgent(userAgent, "one") }
	  .flatMap { processedUserAgent -> reactorClient.processUserAgent(processedUserAgent, "two") }
  }

  @GetMapping("/coroutines")
  suspend fun coroutines(): String {
	logger.info("/sequential/coroutines endpoint called")
	val userAgent = coroutinesClient.getUserAgent()

	val processedUserAgent = coroutinesClient.processUserAgent(userAgent, "one")
	return coroutinesClient.processUserAgent(processedUserAgent, "two")
  }

}
