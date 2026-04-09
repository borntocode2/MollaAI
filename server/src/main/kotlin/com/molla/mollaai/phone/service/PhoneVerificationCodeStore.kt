package com.molla.mollaai.phone.service

import java.time.Clock
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

interface PhoneVerificationCodeStore {
    fun save(phoneNumber: String, code: String, expiresAt: Instant)
    fun find(phoneNumber: String): String?
    fun delete(phoneNumber: String)
}

class InMemoryPhoneVerificationCodeStore(
    private val clock: Clock = Clock.systemUTC(),
) : PhoneVerificationCodeStore {
    private data class Entry(
        val code: String,
        val expiresAt: Instant,
    )

    private val entries = ConcurrentHashMap<String, Entry>()

    override fun save(phoneNumber: String, code: String, expiresAt: Instant) {
        entries[phoneNumber] = Entry(code = code, expiresAt = expiresAt)
    }

    override fun find(phoneNumber: String): String? {
        val entry = entries[phoneNumber] ?: return null
        val now = Instant.now(clock)
        if (entry.expiresAt.isBefore(now)) {
            entries.remove(phoneNumber)
            return null
        }
        return entry.code
    }

    override fun delete(phoneNumber: String) {
        entries.remove(phoneNumber)
    }
}
