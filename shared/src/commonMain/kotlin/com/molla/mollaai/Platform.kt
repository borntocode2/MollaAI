package com.molla.mollaai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform