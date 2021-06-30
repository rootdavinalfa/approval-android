package xyz.dvnlabs.approval.application

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.view.viewmodel.MainViewModel

val appModule = module {
    viewModel {
        MainViewModel(androidApplication())
    }

    single {
        UserRepo()
    }
}