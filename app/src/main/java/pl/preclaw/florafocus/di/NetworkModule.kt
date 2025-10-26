package pl.preclaw.florafocus.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.preclaw.florafocus.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifiers for different API clients
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantIdApiClient

/**
 * Hilt module for providing network-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides base OkHttpClient with logging and timeouts
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    /**
     * Provides Retrofit instance for Weather API (Open-Meteo)
     * Base URL: https://api.open-meteo.com/v1/
     */
    @Provides
    @Singleton
    @WeatherApiClient
    fun provideWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides Retrofit instance for Plant.id API (Phase II)
     * Base URL: https://api.plant.id/v2/
     */
    @Provides
    @Singleton
    @PlantIdApiClient
    fun providePlantIdRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.plant.id/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Services will be provided here
    // Example:
    /*
    @Provides
    @Singleton
    fun provideWeatherApiService(
        @WeatherApiClient retrofit: Retrofit
    ): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
    */
}