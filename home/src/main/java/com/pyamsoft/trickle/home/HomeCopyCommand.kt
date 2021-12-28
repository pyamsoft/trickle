package com.pyamsoft.trickle.home

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.util.doOnStart
import com.pyamsoft.pydroid.util.doOnStop

object HomeCopyCommand {

  @JvmStatic
  fun copyCommandToClipboard(activity: FragmentActivity, label: String, command: String) {
    val clipboard =
        activity.applicationContext.getSystemService<ClipboardManager>().requireNotNull()
    clipboard.setPrimaryClip(ClipData.newPlainText(label, command))
    val toast =
        Toast.makeText(
            activity.applicationContext,
            "ADB command copied to clipboard!",
            Toast.LENGTH_SHORT,
        )
    activity.doOnStart { toast.show() }
    activity.doOnStop { toast.cancel() }
  }
}
