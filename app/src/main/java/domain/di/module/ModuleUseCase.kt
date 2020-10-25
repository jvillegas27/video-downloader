package domain.di.module

import app.di.ActivityScope
import dagger.Binds
import dagger.Module
import domain.DownloadFileUseCase
import domain.DownloadFileUseCaseImpl

@Module
abstract class ModuleUseCase {

    @Binds
    @ActivityScope
    abstract fun bindsDownloadFileUseCase(impl: DownloadFileUseCaseImpl): DownloadFileUseCase
}