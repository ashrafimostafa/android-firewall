package com.example.vpnlearn.di.components

import com.example.vpnlearn.di.modules.FragmentModule
import com.example.vpnlearn.di.scope.FragmentScope
import com.example.vpnlearn.ui.home.HomeFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [ApplicationComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {

    fun inject(fragment: HomeFragment)

}