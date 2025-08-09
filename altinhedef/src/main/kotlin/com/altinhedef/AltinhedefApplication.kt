package com.altinhedef

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class AltinhedefApplication

fun main(args: Array<String>) {
	runApplication<AltinhedefApplication>(*args)
}
