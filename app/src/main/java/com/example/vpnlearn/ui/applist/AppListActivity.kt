package com.example.vpnlearn.ui.applist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.ActivityComponent
import com.example.vpnlearn.ui.base.BaseActivity
import com.example.vpnlearn.utility.Util

class AppListActivity : BaseActivity<AppListViewModel>() {

    companion object {
        var TAG = "NetBlocker.AppListActivity"
    }

    override fun provideLayoutId(): Int = R.layout.activity_app_list

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setUpViews(saveInstanceId: Bundle?) {
        TODO("Not yet implemented")
    }


    override fun setUpObservers() {
        Log.i(TAG, "hewreee")
        super.setUpObservers()
        viewModel.data.observe(this,
            {
                Log.i(TAG, it)
            }
        )
    }


}