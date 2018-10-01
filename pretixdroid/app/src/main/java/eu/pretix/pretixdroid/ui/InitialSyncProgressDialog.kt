package eu.pretix.pretixdroid.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.TextView
import eu.pretix.pretixdroid.R

class InitialSyncProgressDialog : DialogFragment() {

    private var dialog: AlertDialog? = null
    private var progressTextView: TextView? = null

    companion object {
        const val TAG = "InitialSyncProgressDialog"
        val ARG_INIT_VALUE = "value"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val view = activity.layoutInflater.inflate(R.layout.initial_sync_progress_dialog, null)
        progressTextView = view.findViewById(R.id.progressTextView)

        val aDialog = AlertDialog.Builder(activity)
                .setTitle("Initial syncronisation") //TODO
                .setView(view)
                .create()

        dialog = aDialog
        return aDialog
    }

    fun updateProgress(value: Int) {
        progressTextView?.setText("Downloading data. Please wait.. " + value + "%") //TODO add string resource
    }

    fun setOnDismissListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}