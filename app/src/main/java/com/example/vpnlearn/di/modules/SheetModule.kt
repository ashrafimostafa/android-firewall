package com.example.vpnlearn.di.modules

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.qualifire.ActivityContext
import com.example.vpnlearn.di.scope.FragmentScope
import com.example.vpnlearn.di.scope.SheetScope
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
import com.example.vpnlearn.ui.appsheet.AppSheetViewModel
import com.example.vpnlearn.ui.base.BaseSheet
import com.example.vpnlearn.utility.ProvideAppList
import com.example.vpnlearn.utility.ViewModelProviderFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class SheetModule(private val sheet: BaseSheet<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = sheet.context!!

    @Provides
    fun provideAppSheetViewModel(
        compositeDisposable: CompositeDisposable,
        databaseService: DatabaseService,
        provideAppList: ProvideAppList,
    ): AppSheetViewModel = ViewModelProvider(
        sheet, ViewModelProviderFactory(AppSheetViewModel::class) {
            AppSheetViewModel(compositeDisposable, databaseService, provideAppList)
        }).get(AppSheetViewModel::class.java)


    @Provides
    fun provideLinearlayoutManager() = LinearLayoutManager(sheet.context)

    @Provides
    @SheetScope
    fun provideApplicationAdapter() = ApplicationAdapter(sheet.lifecycle, ArrayList())

}