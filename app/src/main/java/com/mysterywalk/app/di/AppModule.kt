package com.mysterywalk.app.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mysterywalk.app.data.location.DefaultLocationClient
import com.mysterywalk.app.data.location.LocationClient
import com.mysterywalk.app.data.remote.OverpassApi
import com.mysterywalk.app.data.repository.SpotRepositoryImpl
import com.mysterywalk.app.data.sensor.CompassSensor
import com.mysterywalk.app.data.sensor.DefaultCompassSensor
import com.mysterywalk.app.domain.repository.SpotRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun provideOverpassApi(moshi: Moshi): OverpassApi {
        return Retrofit.Builder()
            .baseUrl(OverpassApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OverpassApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotRepository(api: OverpassApi): SpotRepository {
        return SpotRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context,
        client: FusedLocationProviderClient
    ): LocationClient {
        return DefaultLocationClient(context, client)
    }

    @Provides
    @Singleton
    fun provideCompassSensor(
        @ApplicationContext context: Context
    ): CompassSensor {
        return DefaultCompassSensor(context)
    }
}
