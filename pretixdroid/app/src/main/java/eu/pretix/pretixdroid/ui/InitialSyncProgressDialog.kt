package eu.pretix.pretixdroid.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import eu.pretix.pretixdroid.R

class InitialSyncProgressDialog : DialogFragment() {

    interface CancelDialogListener {
        fun onDialogCancel()
    }

    private var listener: CancelDialogListener? = null

    private var dialog: AlertDialog? = null
    private var progressTextView: TextView? = null
    private var progressBar: ProgressBar? = null

    companion object {
        const val TAG = "InitialSyncProgressDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val view = activity.layoutInflater.inflate(R.layout.initial_sync_progress_dialog, null)
        progressTextView = view.findViewById(R.id.progressTextView)
        progressBar = view.findViewById(R.id.progressBar)

        val aDialog = AlertDialog.Builder(activity)
                .setTitle(R.string.initial_synchronization)
                .setView(view)
                .setNegativeButton(R.string.cancel) {
                    dialog, which ->
                    listener?.onDialogCancel()
                }
                .create()

        dialog = aDialog
        return aDialog
    }

    fun updateProgress(ctx: Context, value: Int) {
        var progressLabel = ctx.getString(R.string.progress_formatted, value)
        progressTextView?.setText(progressLabel)

        progressBar?.setProgress(value)
    }

    fun setOnDismissListener(l: CancelDialogListener) {
        listener = l
    }
}