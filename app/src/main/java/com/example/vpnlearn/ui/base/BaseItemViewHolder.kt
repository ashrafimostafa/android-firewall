package com.example.vpnlearn.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.di.components.DaggerFragmentComponent
import com.example.vpnlearn.di.components.DaggerViewHolderComponent
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.di.components.ViewHolderComponent
import com.example.vpnlearn.di.modules.FragmentModule
import com.example.vpnlearn.di.modules.ViewHolderModule
import javax.inject.Inject

abstract class BaseItemViewHolder<T : Any, VM : BaseItemViewModel<T>>(
    @LayoutRes layoutId: Int,
    parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false)),
    LifecycleOwner {

    init {
        onCreate()
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

    @Inject
    lateinit var viewModel: VM

    @Inject
    lateinit var lifecycleRegistry: LifecycleRegistry

    open fun bind(data: T) {
        viewModel.updatedData(data)

    }

    protected fun onCreate() {
        injectDependencies(buildViewHolderComponent())
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        setUpObservers()
        setUpViews(itemView)
    }

     fun onStart() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

     fun onStop() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

     fun onDestroy() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
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
        Toast.makeText(itemView.context, msg, Toast.LENGTH_LONG).show()

    fun showToast(@StringRes msg: Int) =
        Toast.makeText(itemView.context, itemView.context.getString(msg), Toast.LENGTH_LONG).show()


    protected abstract fun setUpViews(view: View)

    protected abstract fun injectDependencies(viewholderComponent: ViewHolderComponent)

    private fun buildViewHolderComponent() =
        DaggerViewHolderComponent
            .builder()
            .applicationComponent((itemView.context.applicationContext as MyApplication).applicationComponent)
            .viewHolderModule(ViewHolderModule(this))
            .build()


}