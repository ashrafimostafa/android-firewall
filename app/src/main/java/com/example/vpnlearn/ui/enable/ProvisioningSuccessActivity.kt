package com.example.vpnlearn.ui.enable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vpnlearn.policy.PostProvisioningHelper

class ProvisioningSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val helper = PostProvisioningHelper(this)
        helper.completeProvisioning()
        finish()
    }
}