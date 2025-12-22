package com.woocurlee.bookview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookviewApplication

fun main(args: Array<String>) {
    runApplication<BookviewApplication>(*args)
}
