package com.pyamsoft.trickle.service

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

interface ServicePreferences {

    @CheckResult
    fun listenAlwaysBackground(): Flow<Boolean>

    fun setAlwaysBackground(bg: Boolean)
}