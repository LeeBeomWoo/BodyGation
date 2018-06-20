package bodygate.bcns.bodygation.camerause

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import bodygate.bcns.bodygation.R

/**
 * Shows OK/Cancel confirmation dialog about camera permission.
 */
class ConfirmationDialog : DialogFragment() {
    val REQUEST_CAMERA_PERMISSION = 1
    val VIDEO_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        parentFragment!!.requestPermissions(VIDEO_PERMISSIONS,
                                REQUEST_CAMERA_PERMISSION)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        parentFragment!!.activity?.finish()
                    }
                    .create()
}