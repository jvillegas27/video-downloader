package app.di.modules

import app.MainActivity
import app.di.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ModuleActivity {

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityModule::class])
    abstract fun bindsMainActivity(): MainActivity
}