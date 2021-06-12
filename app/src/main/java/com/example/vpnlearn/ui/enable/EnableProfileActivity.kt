package com.example.vpnlearn.ui.enable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vpnlearn.R
import com.example.vpnlearn.policy.PostProvisioningHelper

class EnableProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val helper = PostProvisioningHelper(this)
        if (!helper.isDone()) {
            // Important: After the profile has been created, the MDM must enable it for corporate
            // apps to become visible in the launcher.
            helper.completeProvisioning()
        }

        setContentView(R.layout.activity_enable_profile)

//        findViewById<View>(R.id.icon).setOnClickListener { v: View? ->
//            // Opens up the main screen
//            startActivity(Intent(this, AppListActivity::class.java))
//            finish()
//        }
    }
}