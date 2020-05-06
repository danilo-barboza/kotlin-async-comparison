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
		val orderIdFuture: CompletableFuture<String> = completableFutureClient.fetchMostRecentOrderId()
		return orderIdFuture
			.thenCompose { orderId ->
				val deliveryCostFuture: CompletableFuture<String> = completableFutureClient.fetchDeliveryCost(orderId)
				val stockInformationFuture: CompletableFuture<String> = completableFutureClient.fetchStockInformation(orderId)
				deliveryCostFuture.thenCombine(stockInformationFuture) { r1, r2 -> CombinedResult(r1, r2) }
			}
	}

	@GetMapping("/reactor")
	fun reactor(): Mono<CombinedResult> {
		logger.info("/parallel/reactor endpoint called")
		val orderIdFuture: Mono<String> = reactorClient.fetchMostRecentOrderId()
		return orderIdFuture
			.flatMap { orderId ->
				val deliveryCostFuture: Mono<String> = reactorClient.fetchDeliveryCost(orderId)
				val stockInformationFuture: Mono<String> = reactorClient.fetchStockInformation(orderId)
				deliveryCostFuture.zipWith(stockInformationFuture) { r1, r2 -> CombinedResult(r1, r2) }
			}
	}

	@GetMapping("/coroutines")
	suspend fun coroutines(): CombinedResult = coroutineScope {
		logger.info("/parallel/coroutines endpoint called")
		val orderId: String = coroutinesClient.fetchMostRecentOrderId()

		val deliveryCostFuture: Deferred<String> = async { coroutinesClient.fetchDeliveryCost(orderId) }
		val stockInformationFuture: Deferred<String> = async { coroutinesClient.fetchStockInformation(orderId) }

		CombinedResult(deliveryCostFuture.await(), stockInformationFuture.await())
	}

}
