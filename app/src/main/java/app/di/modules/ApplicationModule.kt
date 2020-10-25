package app.di.modules

import android.content.Context
import app.MainApplication
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModuleBindings::class])
class ApplicationModule {

    @Provides
    fun provideContext(application: MainApplication): Context {
        return application.applicationContext
    }
}

@Module
abstract class ApplicationModuleBindings {


}
