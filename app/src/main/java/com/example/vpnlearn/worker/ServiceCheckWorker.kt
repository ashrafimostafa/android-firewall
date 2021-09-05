package com.example.vpnlearn.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.service.VpnWorker
import com.example.vpnlearn.utility.Constant

class ServiceCheckWorker(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object{
        const val TAG = "NetBlockerSWorker"
    }
    override fun doWork(): Result {
        //restart service in 10 minute interval
        //we can also sync data or check for notification here
        Log.i(TAG, "worker trigger")
        if (!VpnClient.state.equals(Constant.STATE_CONNECTED)){
            VpnClient().start(context)
        }

        return Result.success()
    }
}