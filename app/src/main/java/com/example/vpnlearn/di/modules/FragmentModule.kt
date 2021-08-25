package com.example.vpnlearn.di.modules

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.qualifire.ActivityContext
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.di.scope.FragmentScope
import com.example.vpnlearn.ui.applist.AppListViewModel
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
import com.example.vpnlearn.ui.appsheet.AppSheetViewModel
import com.example.vpnlearn.ui.appusage.AppListUsageViewModel
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.ui.connection.ConnectionViewModel
import com.example.vpnlearn.ui.home.HomeViewModel
import com.example.vpnlearn.ui.setting.SettingViewModel
import com.example.vpnlearn.utility.ProvideAppList
import com.example.vpnlearn.utility.ViewModelProviderFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = fragment.context!!

    @Provides
    fun provideAppListViewModel(
        compositeDisposable: CompositeDisposable,
        databaseService: DatabaseService,
        provideAppList: ProvideAppList,
    ): AppListViewModel = ViewModelProvider(
        fragment, ViewModelProviderFactory(AppListViewModel::class) {
            AppListViewModel(compositeDisposable, databaseService, provideAppList)
        }).get(AppListViewModel::class.java)

    @Provides
    fun provideSettingViewModel(
        compositeDisposable: CompositeDisposable,
        @ApplicationContext context: Context
    ): SettingViewModel = ViewModelProvider(
        fragment, ViewModelProviderFactory(SettingViewModel::class) {
            SettingViewModel(compositeDisposable, context)
        }).get(SettingViewModel::class.java)

    @Provides
    fun provideConnectionViewModel(
        compositeDisposable: CompositeDisposable,
        @ApplicationContext context: Context
    ): ConnectionViewModel = ViewModelProvider(
        fragment, ViewModelProviderFactory(ConnectionViewModel::class) {
            ConnectionViewModel(compositeDisposable, context)
        }).get(ConnectionViewModel::class.java)

    @Provides
    fun provideHomeViewModel(
        compositeDisposable: CompositeDisposable,
        @ApplicationContext context: Context
    ): HomeViewModel = ViewModelProvider(
        fragment, ViewModelProviderFactory(HomeViewModel::class) {
            HomeViewModel(compositeDisposable, context)
        }).get(HomeViewModel::class.java)

    @Provides
    fun provideAppListUsageViewModel(
        compositeDisposable: CompositeDisposable,
        databaseService: DatabaseService,
        provideAppList: ProvideAppList,
    ): AppListUsageViewModel = ViewModelProvider(
        fragment, ViewModelProviderFactory(AppListUsageViewModel::class) {
            AppListUsageViewModel(compositeDisposable, databaseService, provideAppList)
        }).get(AppListUsageViewModel::class.java)

    @Provides
    fun provideLinearlayoutManager() = LinearLayoutManager(fragment.context)

    @Provides
    @FragmentScope
    fun provideApplicationAdapter() = ApplicationAdapter(fragment.lifecycle, ArrayList())
}