package app.di.modules

import androidx.lifecycle.ViewModelProvider
import app.di.CustomViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ModuleViewModel {

    @Binds
    abstract fun bindViewModelFactory(factory: CustomViewModelFactory): ViewModelProvider.Factory

}
