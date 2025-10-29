package pl.preclaw.florafocus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pl.preclaw.florafocus.BuildConfig
import pl.preclaw.florafocus.data.local.database.FloraFocusDatabase
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class FloraFocusApplication : Application() {
    @Inject
    lateinit var database: FloraFocusDatabase


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialized")
        }
        // Force database initialization
        // This triggers the onCreate callback which seeds the database
        Timber.d("Forcing database initialization...")

        // Simply accessing the database instance forces Room to create it
        // The callback will run asynchronously in the background
        database.openHelper.writableDatabase

        Timber.d("Database initialization triggered")
    }
}
