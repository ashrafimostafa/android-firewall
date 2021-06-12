package com.example.vpnlearn.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.di.components.DaggerSheetComponent
import com.example.vpnlearn.di.components.SheetComponent
import com.example.vpnlearn.di.modules.SheetModule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

abstract class BaseSheet<VM : BaseViewModel> : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModel: VM

    private lateinit var dialog: BottomSheetDialog
    private lateinit var behavior: BottomSheetBehavior<View>


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        injectDependencies(buildSheetComponent())
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet as FrameLayout)
            behavior.isHideable = false
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        setUpObservers()
        viewModel.onCreate()

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =  inflater.inflate(provideLayoutId(), container, false)


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

    protected abstract fun injectDependencies(sheetComponent: SheetComponent)

    private fun buildSheetComponent() =
        DaggerSheetComponent.builder()
            .applicationComponent((context!!.applicationContext as MyApplication).applicationComponent)
            .sheetModule(SheetModule(this))
            .build()

}