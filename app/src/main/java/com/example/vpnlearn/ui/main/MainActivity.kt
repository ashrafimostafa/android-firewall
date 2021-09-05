package com.example.vpnlearn.ui.main

import android.content.Intent
import android.os.Bundle
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.ActivityComponent
import com.example.vpnlearn.receiver.VpnClientRestarter
import com.example.vpnlearn.ui.base.BaseActivity
import com.example.vpnlearn.ui.home.HomeFragment

class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        var TAG = "NetBlocker.AppListActivity"
    }

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setUpViews(saveInstanceId: Bundle?) {
        setMainFragment()
    }

    override fun setUpObservers() {
        super.setUpObservers()
    }

    //todo there is lag when opening the application
    private fun setMainFragment() {
        if (supportFragmentManager.findFragmentByTag(TAG) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.all_list_main_frame, HomeFragment.newInstance(), TAG)
                .commit()
        }
    }

    override fun onDestroy() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, VpnClientRestarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

}