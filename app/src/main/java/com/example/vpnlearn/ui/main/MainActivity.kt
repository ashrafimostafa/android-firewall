package com.example.vpnlearn.ui.main

import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vpnlearn.R
import com.example.vpnlearn.adapter.ApplicationAdapter
import com.example.vpnlearn.model.ApplicationDm.Companion.getRules
import com.example.vpnlearn.policy.DeviceAdmin.getComponentName
import com.example.vpnlearn.service.State
import com.example.vpnlearn.service.VpnClient1
import com.example.vpnlearn.service.VpnClient1.Companion.reload
import com.example.vpnlearn.service.VpnClient1.Companion.start
import com.example.vpnlearn.service.VpnClient1.Companion.stop
import com.example.vpnlearn.utility.Util.isWifiActive
import com.example.vpnlearn.utility.Util.logExtras
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

//    @Inject
//    lateinit var viewModel: MainViewModel

    private var running = false
    private var adapter: ApplicationAdapter? = null
    private var searchItem: MenuItem? = null
    private var mDpm: DevicePolicyManager? = null
    private val mLockdown: CheckBox? = null
    private val mExemptedPackages: EditText? = null
    private var mWho: ComponentName? = null
    override fun onCreate(savedInstanceState: Bundle?) {
//        getDependencies()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        running = true
        setPolicy()


        // Action bar
        val view = layoutInflater.inflate(R.layout.main_switch, null)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.customView = view

        // On/off switch
        val swEnabled = view.findViewById<SwitchCompat>(R.id.vpn_enable_sw)

        swEnabled.isChecked = VpnClient1.state == State.CONNECTED //woow
//        swEnabled.isChecked = prefs.getBoolean("enabled", false)
        swEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val prepare = VpnService.prepare(this@MainActivity)
                if (prepare == null) {
                    //user already grant vpn permission
                    onActivityResult(REQUEST_VPN, RESULT_OK, null)
                } else {
                    try {
                        //system show vpn connection allow dialog
                        startActivityForResult(prepare, REQUEST_VPN)
                    } catch (ex: Throwable) {
                        onActivityResult(REQUEST_VPN, RESULT_CANCELED, null)
                        Toast.makeText(this@MainActivity, ex.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                prefs.edit().putBoolean("enabled", false).apply()
                stop(this@MainActivity)
            }
        }

        // Listen for preference changes
        prefs.registerOnSharedPreferenceChangeListener(this)

        // Fill application list
        fillApplicationList()

        // Listen for connectivity updates
        val ifConnectivity = IntentFilter()
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityChangedReceiver, ifConnectivity)

        // Listen for added/removed applications
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        registerReceiver(packageChangedReceiver, intentFilter)


        ///////////////////////////////////////////////
//        viewModel.packages.observe(this, Observer {
//            Log.i(TAG, "the size of list is: ${it.size}")
//        })
        ///////////////////////////////////////////////
    }

    public override fun onDestroy() {
        Log.i(TAG, "Destroy")
        running = false
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        unregisterReceiver(connectivityChangedReceiver)
        unregisterReceiver(packageChangedReceiver)
        super.onDestroy()
//        viewModel.onDestroy()
    }

    private val connectivityChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received $intent")
            logExtras(TAG, intent)
            invalidateOptionsMenu()
        }
    }
    private val packageChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received $intent")
            logExtras(TAG, intent)
            fillApplicationList()
        }
    }

    private fun fillApplicationList() {
        // Get recycler view
        val rvApplication = findViewById<RecyclerView>(R.id.main_list)
        val progressBar = findViewById<ProgressBar>(R.id.main_progress)
        rvApplication.setHasFixedSize(true)
        rvApplication.layoutManager = LinearLayoutManager(this)
        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executorService.execute {
            val result = getRules(this@MainActivity)
            handler.post {
                if (running) {
                    if (searchItem != null) MenuItemCompat.collapseActionView(searchItem)
                    adapter = ApplicationAdapter(result, this@MainActivity)
                    rvApplication.adapter = adapter
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, name: String) {
        Log.i(TAG, "Preference " + name + "=" + prefs.all[name])
        if ("enabled" == name) {
            // Get enabled
            val enabled = prefs.getBoolean(name, false)

            // Check switch state
            val swEnabled = supportActionBar!!.customView.findViewById<SwitchCompat>(R.id.vpn_enable_sw)
            if (swEnabled.isChecked != enabled) swEnabled.isChecked = enabled
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        // Search
//        searchItem = menu.findItem(R.id.menu_search)
//        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                if (adapter != null) adapter!!.filter.filter(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                if (adapter != null) adapter!!.filter.filter(newText)
//                return true
//            }
//        })
//        searchView.setOnCloseListener {
//            if (adapter != null) adapter!!.filter.filter(null)
//            true
//        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val network = menu.findItem(R.id.menu_network_setting)
        network.setIcon(if (isWifiActive(this)) R.drawable.wifi else R.drawable.other)
//        val wifi = menu.findItem(R.id.menu_block_wifi)
//        wifi.isChecked = prefs.getBoolean("whitelist_wifi", true)
//        val other = menu.findItem(R.id.menu_block_other)
//        other.isChecked = prefs.getBoolean("whitelist_other", true)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return when (item.itemId) {
            R.id.menu_network_setting -> {
                val settings =
                    Intent(if (isWifiActive(this)) Settings.ACTION_WIFI_SETTINGS else Settings.ACTION_WIRELESS_SETTINGS)
                if (settings.resolveActivity(packageManager) != null) startActivity(settings) else Log.w(
                    TAG,
                    "$settings not available"
                )
                true
            }
            R.id.menu_refresh_app_list -> {
                fillApplicationList()
                true
            }
//            R.id.menu_block_wifi -> {
//                prefs.edit().putBoolean("whitelist_wifi", !prefs.getBoolean("whitelist_wifi", true))
//                    .apply()
//                fillApplicationList()
//                reload("wifi", this)
//                true
//            }
//            R.id.menu_block_other -> {
//                prefs.edit()
//                    .putBoolean("whitelist_other", !prefs.getBoolean("whitelist_other", true))
//                    .apply()
//                fillApplicationList()
//                reload("other", this)
//                true
//            }
//            R.id.menu_reset_wifi -> {
//                AlertDialog.Builder(this)
//                    .setMessage(R.string.sure_message)
//                    .setPositiveButton(android.R.string.yes) { dialog, which -> reset("wifi") }
//                    .setNegativeButton(android.R.string.no, null)
//                    .show()
//                true
//            }
//            R.id.menu_reset_other -> {
//                AlertDialog.Builder(this)
//                    .setMessage(R.string.sure_message)
//                    .setPositiveButton(android.R.string.yes) { dialog, which -> reset("other") }
//                    .setNegativeButton(android.R.string.no, null)
//                    .show()
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reset(network: String) {
        val other = getSharedPreferences(network, MODE_PRIVATE)
        val edit = other.edit()
        for (key in other.all.keys) edit.remove(key)
        edit.apply()
        fillApplicationList()
        reload(network, this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VPN) {
            // Update enabled state
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().putBoolean("enabled", resultCode == RESULT_OK).apply()

            // Start service
            if (resultCode == RESULT_OK) start(this)
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setPolicy() {
        mDpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mWho = getComponentName(this)
        if (mDpm!!.isAdminActive(mWho!!)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    mDpm!!.setAlwaysOnVpnPackage(mWho!!, null, true, null)
                    Log.i(TAG, "always on vpn policy set")
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    Log.e(TAG, "set policy error: $e")
                }
            }
        } else {

            //todo search and ask about this problem
            Toast.makeText(this, "there is no active admin", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "NetBlocker.Main"
        private const val REQUEST_VPN = 1
        private val VPN_INTENT = Intent(VpnService.SERVICE_INTERFACE)
    }


//    private fun getDependencies() {
//        DaggerActivityComponent
//            .builder()
//            .applicationComponent((application as MyApplication).applicationComponent)
//            .activityModule(ActivityModule(this))
//            .build()
//            .inject(this)
//
//
//    }

    override fun onStart() {
        super.onStart()
//        viewModel.getAllPackages()
    }
}