package app.di.modules

import android.content.Context
import androidx.lifecycle.ViewModel
import app.di.ActivityScope
import app.di.ViewModelKey
import app.permission.PermissionsManager
import app.permission.PermissionsManagerImpl
import app.viewmodel.MainViewModel
import com.vanniktech.rxpermission.RealRxPermission
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import domain.di.module.ModuleUseCase

@Module(includes = [ActivityModulesBindings::class, ModuleUseCase::class])
class ActivityModule {

    @Provides
    @ActivityScope
    fun providesRxPermissions(context: Context): RealRxPermission {
        return RealRxPermission.getInstance(context)
    }

}

@Module
abstract class ActivityModulesBindings {

    @ActivityScope
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindsMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @ActivityScope
    abstract fun bindsPermissionsManager(impl: PermissionsManagerImpl): PermissionsManager
}