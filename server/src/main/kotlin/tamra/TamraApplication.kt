package tamra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TamraApplication

fun main(args: Array<String>) {
    runApplication<TamraApplication>(*args)
}
