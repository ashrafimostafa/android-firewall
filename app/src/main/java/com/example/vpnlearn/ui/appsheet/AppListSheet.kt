package com.example.vpnlearn.ui.appsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.SheetComponent
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
import com.example.vpnlearn.ui.base.BaseSheet
import kotlinx.android.synthetic.main.sheet_app_list.*
import javax.inject.Inject

class AppListSheet : BaseSheet<AppSheetViewModel>() {

    @Inject
    lateinit var applicationAdapter: ApplicationAdapter


    companion object {
        const val TAG = "NetBlocker.AppSheet"
        fun newInstance(): AppListSheet {
            val args = Bundle()
            val fragment = AppListSheet()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId() = R.layout.sheet_app_list

    override fun setUpViews(view: View) {
        sheet_app_list.apply {
            adapter = applicationAdapter
            layoutManager =
                LinearLayoutManager(context) //we should use linearLayoutManager here but it cause crashes
        }
    }


    override fun injectDependencies(sheetComponent: SheetComponent) {
        sheetComponent.inject(this)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.packageLiveData.observe(this, {
            applicationAdapter.appendDate(it)
            sheet_app_list_progress.visibility = View.GONE
        })

    }

}