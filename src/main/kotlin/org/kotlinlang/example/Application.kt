package org.kotlinlang.example

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

class CombinedResult(val deliveryCost: String, val stockInformation: String)

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@SpringBootApplication
class Application

val logger: Logger = LoggerFactory.getLogger("logger")
private val objectMapper = ObjectMapper()

//shared helper functions
fun logDelayedRequest(fullName: String) {
	logger.info("[ $fullName ]: processing delayed request")
}

fun parseUUIDResponse(uuidResponse: String): String {
	return objectMapper.readTree(uuidResponse).get("uuid").asText()
}

fun parseDelayedResponse(delayedResponseBody: String): String {
	return objectMapper.readTree(delayedResponseBody).get("data").asText()
}

fun delayedOperationRequest(operation: String, orderId: String): String {
	val randomInt = (0..10).random()
	return "$operation of order '$orderId': $randomInt"
}