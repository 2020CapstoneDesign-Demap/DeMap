package kr.ac.hansung.demap

import android.app.Application
import kr.ac.hansung.demap.di.datasourceModule
import kr.ac.hansung.demap.di.presenterModule
import kr.ac.hansung.demap.di.repositoryModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DeMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            modules(listOf(presenterModule, repositoryModule, datasourceModule))
        }
    }
}