package com;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.chunyuAdvisory.activity.RecordFreeConsultActivity;
import com.utils.StaticVar;
import com.utils.UtilTool;

/**
 * Created by Administrator on 15-8-4.
 */
public class PaderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.NotifyCationService.sendnotifyCation")) {
            Log.e("dsadasdas", "dasdas");
            if (UtilTool.netWorkCheck(context.getApplicationContext())) {
                StaticVar.ask = true;
                Intent intent1 = new Intent(context, RecordFreeConsultActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            }

        }
    }
}
