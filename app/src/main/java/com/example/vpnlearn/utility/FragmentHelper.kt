package com.example.vpnlearn.utility

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


object FragmentHelper {

    private fun getFragmentManager(context: Context): FragmentManager? {
        return (context as AppCompatActivity).supportFragmentManager
    }

    fun openFragment(context: Context?, frameId: Int, fragment: Fragment) {
        context?.let {
            getFragmentManager(it)?.beginTransaction()
                ?.replace(frameId, fragment, null)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    fun addFragment(context: Context?, frameId: Int, fragment: Fragment) {
        context?.let {
            getFragmentManager(it)?.beginTransaction()
                ?.add(frameId, fragment, null)
                ?.addToBackStack(null)
                ?.commit()
        }
    }
}