package pl.preclaw.florafocus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pl.preclaw.florafocus.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class FloraFocusApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialized")
        }
    }
}
