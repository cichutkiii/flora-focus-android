package pl.preclaw.florafocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.preclaw.florafocus.data.local.dao.PlantCatalogDao
import pl.preclaw.florafocus.ui.theme.GardenFocusTheme
import timber.log.Timber
import javax.inject.Inject



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
//    @Inject
//    lateinit var plantCatalogDao: PlantCatalogDao  // ← DODAJ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//         Verify Hilt injection works
//        lifecycleScope.launch {
////            delay(2000) // Poczekaj 2 sekundy na seedowanie
//            val plantCount = plantCatalogDao.getPlantCount()
//            val plants = plantCatalogDao.getAllPlants().first()
//
//            Timber.tag("DATABASE_TEST")
//            Timber.d("======================")
//            Timber.d("Plants in database: $plantCount")
//            plants.forEach { plant ->
//                Timber.d("- ${plant.commonName} (${plant.latinName})")
//            }
//            Timber.d("======================")
//        }
//         ← KONIEC TESTU

        enableEdgeToEdge()
        Timber.tag("LifeCycles");
        Timber.d("Activity Created");
        Timber.i("Hi, I am Main Class")

        setContent {
            GardenFocusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Flora Focus",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name! 🌱",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GardenFocusTheme {
        Greeting("Flora Focus")
    }
}
