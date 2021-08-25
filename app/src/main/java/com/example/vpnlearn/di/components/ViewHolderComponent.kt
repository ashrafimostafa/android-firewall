package com.example.vpnlearn.di.components

import com.example.vpnlearn.di.modules.ViewHolderModule
import com.example.vpnlearn.di.scope.ViewHolderScope
import com.example.vpnlearn.ui.applist.app.ApplicationViewHolder
import dagger.Component

@ViewHolderScope
@Component(dependencies = [ApplicationComponent::class], modules = [ViewHolderModule::class])
interface ViewHolderComponent {

    fun inject(viewHolder: ApplicationViewHolder)

    fun inject(viewHolder: com.example.vpnlearn.ui.appsheet.app.ApplicationViewHolder)

    fun inject(viewHolder: com.example.vpnlearn.ui.appusage.app.ApplicationViewHolder)

}