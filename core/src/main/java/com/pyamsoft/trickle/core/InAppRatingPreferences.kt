package com.pyamsoft.trickle.core

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

/** Keep track of how the user does things in-app so that we can eventually prompt for a rating */
interface InAppRatingPreferences {

  /** Watch to show the rating */
  @CheckResult fun listenShowInAppRating(): Flow<Boolean>

  /** How many times is the app opened (onStart) */
  fun markAppOpened()
}
