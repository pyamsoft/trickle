package com.pyamsoft.trickle.core

import kotlinx.coroutines.CancellationException

/** Rethrow cancellation exceptions to continue Coroutine flow, otherwise we handle the error */
public inline fun <R : Any> Throwable.ifNotCancellation(block: (Throwable) -> R): R {
  val cause = this.cause ?: this
  return when (this) {
    is CancellationException -> throw cause
    else -> block(this)
  }
}
