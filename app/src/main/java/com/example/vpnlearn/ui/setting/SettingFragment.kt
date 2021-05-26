package com.example.vpnlearn.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.base.BaseFragment

class SettingFragment : BaseFragment<SettingViewModel>() {

    override fun provideLayoutId() = R.layout.fragment_setting


    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setUpViews(view: View) {
        TODO("Not yet implemented")
    }

}