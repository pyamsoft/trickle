package com.pyamsoft.trickle.service.notification

sealed interface PermissionRequests {

  object Notification : PermissionRequests
}

sealed interface PermissionResponses {

  object Notification : PermissionResponses
}
