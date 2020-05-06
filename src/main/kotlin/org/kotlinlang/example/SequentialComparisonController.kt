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
	fun completableFuture(): CompletableFuture<CombinedResult> {
		logger.info("/sequential/completable-future endpoint called")
		val orderIdFuture: CompletableFuture<String> = completableFutureClient.fetchMostRecentOrderId()
		return orderIdFuture
			.thenCompose { orderId -> completableFutureClient.fetchDeliveryCost(orderId).thenApply { orderId to it } }
			.thenCompose { (orderId, deliveryCost) ->
				completableFutureClient.fetchStockInformation(orderId)
					.thenApply { CombinedResult(deliveryCost, it) }
			}
	}

	@GetMapping("/reactor")
	fun reactor(): Mono<CombinedResult> {
		logger.info("/sequential/reactor endpoint called")
		val orderIdFuture: Mono<String> = reactorClient.fetchMostRecentOrderId()
		return orderIdFuture
			.flatMap { orderId -> reactorClient.fetchDeliveryCost(orderId).map { orderId to it } }
			.flatMap { (orderId, deliveryCost) ->
				reactorClient.fetchStockInformation(orderId)
					.map { CombinedResult(deliveryCost, it) }
			}
	}

	@GetMapping("/coroutines")
	suspend fun coroutines(): CombinedResult {
		logger.info("/sequential/coroutines endpoint called")
		val orderId = coroutinesClient.fetchMostRecentOrderId()

		val deliveryCost = coroutinesClient.fetchDeliveryCost(orderId)
		val stockInformation = coroutinesClient.fetchStockInformation(orderId)
		return CombinedResult(deliveryCost, stockInformation)
	}

}
