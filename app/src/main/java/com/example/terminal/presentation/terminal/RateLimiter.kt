package com.example.terminal.presentation.terminal

import java.util.LinkedList

class RateLimiter(
    private val maxRequests: Int,
    private val timeWindowSeconds: Int
) {
    private val requests = LinkedList<Long>()

    fun canMakeRequest(): Boolean {
        val now = System.currentTimeMillis() / 1000
        // Удаляем старые запросы (старше timeWindowSeconds)
        while (requests.isNotEmpty() && now - requests.first > timeWindowSeconds) {
            requests.removeFirst()
        }
        return requests.size < maxRequests
    }

    fun recordRequest() {
        requests.addLast(System.currentTimeMillis() / 1000)
    }

    fun secondsSinceFirstRequest(): Int {
        if (requests.isEmpty()) return 0
        val now = System.currentTimeMillis() / 1000
        return (now - requests.first).toInt()
    }
}