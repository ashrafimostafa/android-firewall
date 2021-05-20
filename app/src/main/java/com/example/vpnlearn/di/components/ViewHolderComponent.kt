package com.example.vpnlearn.di.components

import com.example.vpnlearn.di.modules.ViewHolderModule
import com.example.vpnlearn.di.scope.ViewHolderScope
import dagger.Component

@ViewHolderScope
@Component(dependencies = [ApplicationComponent::class], modules = [ViewHolderModule::class])
interface ViewHolderComponent {

}