package data.di.module

import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import data.network.DownloadApiInterface
import data.network.DownloadRepository
import data.network.DownloadRepositoryImpl
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ModuleNetworksBindings::class])
class ModuleNetwork {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) //logging everything since the app it's only debug
        .build()

    @Provides
    fun providesGson() = Gson()

    @Provides
    @Singleton
    fun providesDownloaderRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.computation()))
            .client(okHttpClient)
            .baseUrl("https://anyurl")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providesDownloadApiInterface(retrofit: Retrofit): DownloadApiInterface {
        return retrofit.create(DownloadApiInterface::class.java)
    }
}

@Module
abstract class ModuleNetworksBindings {

    @Binds
    abstract fun bindsDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository
}