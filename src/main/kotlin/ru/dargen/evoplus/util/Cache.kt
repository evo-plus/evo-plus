package ru.dargen.evoplus.util

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.Scheduler
import java.util.Collections.newSetFromMap
import java.util.concurrent.Executors
import kotlin.let
import kotlin.time.Duration
import kotlin.time.toJavaDuration

private val CacheScheduler = Scheduler.forScheduledExecutorService(Executors.newScheduledThreadPool(1))

typealias ExpireHandler<K, V> = (K, V) -> Unit

fun <K, V> newExpireCacheBuilder(handler: ExpireHandler<K, V>? = null) = Caffeine.newBuilder()
    .scheduler(CacheScheduler)
    .removalListener<K, V> { key, value, cause ->
        if (handler !== null && key !== null && value !== null && cause === RemovalCause.EXPIRED) {
            handler(key, value)
        }
    }

fun <K, V> newCacheExpireAfterWrite(duration: Duration, handler: ExpireHandler<K, V>? = null) =
    newExpireCacheBuilder<K, V>(handler)
        .expireAfterWrite(duration.toJavaDuration())
        .build<K, V>()

fun <K, V> newCacheExpireAfterAccess(duration: Duration, handler: ExpireHandler<K, V>? = null) =
    newExpireCacheBuilder<K, V>(handler)
        .expireAfterAccess(duration.toJavaDuration())
        .build<K, V>()

fun <K, V> newMapCacheExpireAfterWrite(duration: Duration, handler: ExpireHandler<K, V>? = null) =
    newCacheExpireAfterWrite<K, V>(duration, handler).asMap()

fun <K, V> newMapCacheExpireAfterAccess(duration: Duration, handler: ExpireHandler<K, V>? = null) =
    newCacheExpireAfterAccess<K, V>(duration, handler).asMap()

fun <T> newSetCacheExpireAfterWrite(duration: Duration, handler: ((T) -> Unit)? = null) = newSetFromMap(
    newCacheExpireAfterWrite<T, Boolean>(
        duration,
        handler?.let { { key, _ -> it(key) } }
    ).asMap()
)
fun <T> newSetCacheExpireAfterAccess(duration: Duration, handler: ((T) -> Unit)? = null) = newSetFromMap(
    newCacheExpireAfterAccess<T, Boolean>(
        duration,
        handler?.let { { key, _ -> it(key) } }
    ).asMap()
)