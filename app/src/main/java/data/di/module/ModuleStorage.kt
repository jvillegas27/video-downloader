package data.di.module

import dagger.Binds
import dagger.Module
import data.storage.LocalStorageManager
import data.storage.LocalStorageManagerImpl
import data.storage.OutputStreamFactory
import data.storage.OutputStreamFactoryImpl
import javax.inject.Singleton

@Module
abstract class ModuleStorage {

    @Binds
    @Singleton
    abstract fun bindsLocalStorageManager(impl: LocalStorageManagerImpl): LocalStorageManager

    @Binds
    abstract fun bindsOutputStreamFactory(impl: OutputStreamFactoryImpl): OutputStreamFactory
}