package com.example.vpnlearn.ui.applist

import android.os.Bundle
import android.util.Log
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.ActivityComponent
import com.example.vpnlearn.ui.base.BaseActivity

class AppListActivity : BaseActivity<AppListViewModel>() {

    companion object {
        var TAG = "NetBlocker.AppListActivity"
    }

    override fun provideLayoutId(): Int = R.layout.activity_app_list

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setUpViews(saveInstanceId: Bundle?) {
        addAppListFragment()
    }


    override fun setUpObservers() {
        super.setUpObservers()
    }

    private fun addAppListFragment() {
        if (supportFragmentManager.findFragmentByTag(TAG) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.all_list_main_frame, AppListFragment.newInstance(), TAG)
                .commit()
        }
    }


}