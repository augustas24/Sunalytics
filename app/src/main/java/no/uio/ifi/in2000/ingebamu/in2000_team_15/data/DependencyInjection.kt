package no.uio.ifi.in2000.ingebamu.in2000_team_15.data

import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar.WeatherDataSource
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.EnturDataSource
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.GeocodingRepository
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.MapboxDataSource
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.AppDatabase
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar.SolarDataSource
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar.SolarRepository
import javax.inject.Singleton
import android.content.Context
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price.PriceDataSource
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price.PriceRepository
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherDao
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network.ConnectivityObserver
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network.NetworkConnectivityObserver

/**
 * This file is responsible for setting up dependency injection using Hilt.
 * It provides necessary dependencies such as the HTTP client, data sources,
 * repositories, and other required components.
 *
 * All new dependencies, including data sources and repositories,
 * should be added here as Hilt modules to ensure proper injection.
 *
 * If a class depends on another class, Hilt will automatically inject it
 * as long as the dependency is provided in a module. For example:
 *
 * class AlpacaPartiesRepository @Inject constructor(
 *     private val alpacaPartiesDataSource: AlpacaPartiesDataSource,
 *     private val votesRepository: VotesRepository
 * ) {...}
 *
 * This ensures that only a single instance of each dependency is created
 * and injected wherever needed.
 */

@Module
@InstallIn(SingletonComponent::class)
object KtorModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                gson()
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun provideEnturDataSource(httpClient: HttpClient): EnturDataSource =
        EnturDataSource(httpClient)
    @Provides
    fun provideMapboxDataSource(httpClient: HttpClient): MapboxDataSource =
        MapboxDataSource(httpClient)
    @Provides
    fun provideSolarDataSource(httpClient: HttpClient): SolarDataSource =
        SolarDataSource(httpClient)
    @Provides
    fun provideWeatherDataSource(httpClient: HttpClient, weatherDao: WeatherDao): WeatherDataSource =
        WeatherDataSource(httpClient, weatherDao)
    @Provides
    fun providePriceDataSource(httpClient: HttpClient): PriceDataSource =
        PriceDataSource(httpClient)
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideGeocodingRepository(enturDataSource: EnturDataSource, mapboxDataSource: MapboxDataSource): GeocodingRepository =
        GeocodingRepository(enturDataSource, mapboxDataSource)
    @Provides
    fun provideSolarRepository(solarDatasource: SolarDataSource, weatherDataSource: WeatherDataSource): SolarRepository =
        SolarRepository(solarDatasource, weatherDataSource)
    @Provides
    fun providePriceRepository(priceDataSource: PriceDataSource): PriceRepository =
        PriceRepository(priceDataSource)
}

@Module
@InstallIn(SingletonComponent::class)
    object DatabaseModule {

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "database-name"
            ).build()
        }

        @Provides
        fun provideUserDao(database: AppDatabase): WeatherDao {
            return database.WeatherDao()
        }

    }

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }
}
