package com.example.vpnlearn.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.di.components.ActivityComponent
import com.example.vpnlearn.di.components.DaggerActivityComponent
import com.example.vpnlearn.di.modules.ActivityModule
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setUpObservers()
        viewModel.onCreate()
    }

    protected open fun setUpObservers() {
        viewModel.messageId.observe(this, {
            showToast(it)
        })

        viewModel.messageTxt.observe(this, {
            showToast(it)
        })
    }

    fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    fun showToast(@StringRes msg: Int) =
        Toast.makeText(this, getString(msg), Toast.LENGTH_LONG).show()

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun setUpViews(saveInstanceId: Bundle?)

    protected abstract fun injectDependencies(activityComponent: ActivityComponent)

    private fun buildActivityComponent() =
        DaggerActivityComponent.builder()
            .applicationComponent((application as MyApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

}