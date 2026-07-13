package com.example.ozonpricetracking.core.logging
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance()
        val formattedTag = tag ?: "Global"

        crashlytics.log("[$formattedTag] $message")

        if (priority == Log.ERROR && t != null) {
            crashlytics.recordException(t)
        }
    }
}
