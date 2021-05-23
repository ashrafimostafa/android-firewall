package com.example.vpnlearn.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.di.components.ActivityComponent
import com.example.vpnlearn.di.components.DaggerActivityComponent
import com.example.vpnlearn.di.components.DaggerFragmentComponent
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.di.modules.ActivityModule
import com.example.vpnlearn.di.modules.FragmentModule
import javax.inject.Inject

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildFragmentComponent())
        super.onCreate(savedInstanceState)
        setUpObservers()
        viewModel.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(provideLayoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
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
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    fun showToast(@StringRes msg: Int) =
        Toast.makeText(context, getString(msg), Toast.LENGTH_LONG).show()

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun setUpViews(view: View)

    protected abstract fun injectDependencies(fragmentComponent: FragmentComponent)

    private fun buildFragmentComponent() =
        DaggerFragmentComponent.builder()
            .applicationComponent((context!!.applicationContext as MyApplication).applicationComponent)
            .fragmentModule(FragmentModule(this))
            .build()
}