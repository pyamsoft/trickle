package com.pyamsoft.trickle.home

sealed class PermissionRequests {

  object Notification : PermissionRequests()
}

sealed class PermissionResponse {

  object RefreshNotification : PermissionResponse()
}
