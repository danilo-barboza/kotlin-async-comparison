package org.kotlinlang.example

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

class CombinedResult(val result1: String, val result2: String)

val logger: Logger = LoggerFactory.getLogger("logger")
private val objectMapper = ObjectMapper()

//shared helper methods
fun logDelayedRequest(fullName: String) {
	logger.info("[ $fullName ]: processing delayed request")
}

fun parseDelayedResponse(delayedResponseBody: String): String {
	return objectMapper.readTree(delayedResponseBody).get("data").asText()
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@SpringBootApplication
class Application