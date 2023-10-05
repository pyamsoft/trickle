package com.pyamsoft.trickle.service.notification

sealed interface PermissionRequests {

  data object Notification : PermissionRequests
}

sealed interface PermissionResponses {

  data object Notification : PermissionResponses
}
