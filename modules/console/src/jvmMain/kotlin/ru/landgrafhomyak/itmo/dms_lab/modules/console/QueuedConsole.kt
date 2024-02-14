@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package ru.landgrafhomyak.itmo.dms_lab.modules.console

import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleInterface
import ru.landgrafhomyak.itmo.dms_lab.modules.console.abstract.ConsoleTextStyle
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

import java.lang.Object
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

abstract class QueuedConsole(private val maxPendingRequests: UInt = DEFAULT_MAX_REQUESTS) : ConsoleInterface {
    @Suppress("SpellCheckingInspection")
    abstract fun blockingReadln(): String?
    abstract fun blockingPrint(s: String)
    abstract fun blockingPrintln(s: String)
    abstract fun blockingSetStyle(s: ConsoleTextStyle)

    private val mutex = ReentrantLock()
    private var firstRequest: Request<*>? = null
    private var lastRequest: Request<*>? = null
    private var requestsCount: UInt = 0u
    private val notifier = Object()

    private sealed class Request<T> {
        var next: Request<*>? = null
        lateinit var continuation: Continuation<T>

        inline fun resume(block: () -> T) {
            val result: T
            try {
                result = block()
            } catch (t: Throwable) {
                this.continuation.resumeWithException(t)
                return
            }
            this.continuation.resume(result)
        }

        class ReadLine() : Request<String?>()
        class Print(val s: String) : Request<Unit>()
        class PrintLine(val s: String) : Request<Unit>()
        class SetStyle(val style: ConsoleTextStyle) : Request<Unit>()
    }

    @Suppress("FunctionName")
    private suspend inline fun <T> _offerRequest(r: Request<T>): T {
        this.mutex.withLock {
            if (this.requestsCount >= this.maxPendingRequests)
                throw RuntimeException("Too many requests!")
            val last = this.lastRequest
            if (last == null) {
                this.firstRequest = r
                this.lastRequest = r
            } else {
                last.next = r
                this.lastRequest = r
            }
        }
        return suspendCoroutine { continuation ->
            r.continuation = continuation
            synchronized(this.notifier) {
                this.notifier.notifyAll()
            }
        }
    }

    override suspend fun readln(): String? =
        this._offerRequest(Request.ReadLine())

    override suspend fun print(s: String) =
        this._offerRequest(Request.Print(s))

    override suspend fun println(s: String) =
        this._offerRequest(Request.PrintLine(s))

    override suspend fun setStyle(style: ConsoleTextStyle) =
        this._offerRequest(Request.SetStyle(style))

    fun serverForever() {
        while (true) {
            val request: Request<*>?
            this.mutex.withLock {
                request = this.firstRequest
                if (request != null) {
                    val next = request.next
                    this.firstRequest = next
                    if (next == null)
                        this.lastRequest = null
                }
            }
            if (request == null) {
                synchronized(this.notifier) {
                    this.notifier.wait()
                }
                continue
            }
            when (request) {
                is Request.Print -> request.resume { this.blockingPrint(request.s) }
                is Request.PrintLine -> request.resume { this.blockingPrintln(request.s) }
                is Request.ReadLine -> request.resume { this.blockingReadln() }
                is Request.SetStyle -> request.resume { this.blockingSetStyle(request.style) }
            }
        }
    }

    companion object {
        const val DEFAULT_MAX_REQUESTS = UInt.MAX_VALUE
    }
}