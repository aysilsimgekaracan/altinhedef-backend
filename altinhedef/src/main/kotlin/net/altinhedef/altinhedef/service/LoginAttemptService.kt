package net.altinhedef.altinhedef.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class LoginAttemptService {
    @Value("\${login.attempt.max}")
    private val maxAttempts: Int = 5

    private val attemptsCache: LoadingCache<String, Int> = Caffeine.newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .build { key -> 0 }

    fun loginSucceeded(key: String) {
        attemptsCache.invalidate(key)
    }

    fun loginFailed(key: String) {
        var attempts = attemptsCache.get(key)
        attempts++
        attemptsCache.put(key, attempts)
    }

    fun isBlocked(key: String): Boolean {
        val attempts = attemptsCache.get(key)
        return attempts >= maxAttempts
    }
}