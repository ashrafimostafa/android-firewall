package com.example.vpnlearn.di.modules

import androidx.lifecycle.LifecycleRegistry
import com.example.vpnlearn.di.scope.ViewHolderScope
import com.example.vpnlearn.ui.base.BaseItemViewHolder
import dagger.Module
import dagger.Provides

@Module
class ViewHolderModule(private val viewHolder: BaseItemViewHolder<*, *>) {

    @Provides
    @ViewHolderScope
    fun provideLifecycleRegistry() = LifecycleRegistry(viewHolder)




}