package com.molla.mollaai.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController {
    @GetMapping("/")
    fun root(): String = "Spring server is running"

    @GetMapping("/health")
    fun health(): String = "ok"
}
