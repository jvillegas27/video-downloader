package app.di

import app.MainApplication
import app.di.modules.ApplicationModule
import app.di.modules.ModuleActivity
import app.di.modules.ModuleViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import data.di.module.ModuleNetwork
import data.di.module.ModuleStorage
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ModuleActivity::class,
        ApplicationModule::class,
        ModuleViewModel::class,
        ModuleStorage::class,
        ModuleNetwork::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MainApplication): Builder

        fun build(): AppComponent
    }

    fun inject(app: MainApplication)
}