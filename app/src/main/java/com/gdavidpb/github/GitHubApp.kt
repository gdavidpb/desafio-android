package com.gdavidpb.github

import android.app.Application
import com.gdavidpb.github.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

open class GitHubApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            androidContext(this@GitHubApp)

            androidFileProperties()

            modules(appModule)
        }
    }
}