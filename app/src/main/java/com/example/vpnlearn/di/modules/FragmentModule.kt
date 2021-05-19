package com.example.vpnlearn.di.modules

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.qualifire.ActivityContext
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.ui.home.HomeViewModel
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = fragment.context!!

}