package eu.pretix.pretixdroid.ui

import android.content.DialogInterface
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.annotation.RawRes
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.joshdholtz.sentry.Sentry

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

import eu.pretix.libpretixsync.db.QueuedCheckIn

import eu.pretix.pretixdroid.AppConfig
import eu.pretix.pretixdroid.PretixDroid
import eu.pretix.pretixdroid.R

class SettingsFragment : PreferenceFragment() {

    private fun resetApp() {
        //        DaoSession daoSession = ((PretixDroid) getActivity().getApplication()).getDaoSession();
        //        daoSession.getQueuedCheckInDao().deleteAll();
        //        daoSession.getTicketDao().deleteAll();

        val config = AppConfig(activity)
        config.resetEventConfig()
        Toast.makeText(activity, R.string.reset_success, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)

        val reset = findPreference("action_reset") as Preference
        reset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val cnt = (activity.application as PretixDroid).data.count(QueuedCheckIn::class.java).get().value().toLong()
            if (cnt > 0) {
                AlertDialog.Builder(activity)
                        .setMessage(R.string.pref_reset_warning)
                        .setNegativeButton(getString(R.string.cancel)) { dialog, whichButton ->
                            // do nothing
                        }
                        .setPositiveButton(getString(R.string.ok)) { dialog, whichButton -> resetApp() }.create().show()

            } else {
                resetApp()
            }
            true
        }

        val about = findPreference("action_about")
        about.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            asset_dialog(R.raw.about, R.string.about)
            true
        }

        val async = findPreference("async") as CheckBoxPreference
        async.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val config = AppConfig(activity)
            if (newValue is Boolean && newValue != config.asyncModeEnabled) {
                if (newValue) {
                    if (config.apiVersion < 3) {
                        AlertDialog.Builder(activity)
                                .setMessage(R.string.pref_async_not_supported)
                                .setPositiveButton(getString(R.string.dismiss)) { dialog, whichButton -> }.create().show()
                        return@OnPreferenceChangeListener false
                    }

                    AlertDialog.Builder(activity)
                            .setTitle(R.string.pref_async)
                            .setMessage(R.string.pref_async_warning)
                            .setNegativeButton(getString(R.string.cancel)) { dialog, whichButton -> }
                            .setPositiveButton(getString(R.string.ok)) { dialog, whichButton ->
                                config.asyncModeEnabled = true
                                async.isChecked = true
                            }.create().show()

                    return@OnPreferenceChangeListener false
                }
            }
            true
        }
    }

    private fun asset_dialog(@RawRes htmlRes: Int, @StringRes title: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_about, null, false)
        val dialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.dismiss, null)
                .create()

        val textView = view.findViewById<View>(R.id.aboutText) as TextView

        var text = ""

        val builder = StringBuilder()
        val fis: InputStream
        try {
            fis = resources.openRawResource(htmlRes)
            val reader = BufferedReader(InputStreamReader(fis, "utf-8"))
            var line: String
            line = reader.readLine()
            while (line != null) {
                builder.append(line)
                line = reader.readLine()
            }

            text = builder.toString()
            fis.close()
        } catch (e: IOException) {
            Sentry.captureException(e)
            e.printStackTrace()
        }

        textView.text = Html.fromHtml(text)
        textView.movementMethod = LinkMovementMethod.getInstance()

        dialog.show()
    }
}