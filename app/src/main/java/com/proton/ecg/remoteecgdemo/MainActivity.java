package com.proton.ecg.remoteecgdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.proton.ecg.remotemanager.util.RemoteManager;
import com.proton.ecgpatch.connector.EcgPatchManager;
import com.proton.ecgpatch.connector.callback.DataListener;
import com.proton.view.EcgRealTimeView;
import com.wms.ble.callback.OnConnectListener;

public class MainActivity extends AppCompatActivity {
    private String patchMac = "F5:26:4A:66:10:66";
    EcgRealTimeView realTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EcgPatchManager.init(this);
        RemoteManager.getInstance().init(this, patchMac);
        PermissionUtils.getReadAndWritePermission(this);
        PermissionUtils.getLocationPermission(this);
        realTimeView = findViewById(R.id.id_ecg_view);
        findViewById(R.id.btnConnect).setOnClickListener(v -> {
            connect();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RemoteManager.getInstance().disconnect();
    }

    private void connect() {
        EcgPatchManager.getInstance(patchMac).setNeedUserInfo(true);
        EcgPatchManager.getInstance(patchMac).setDataListener(new DataListener() {
            @Override
            public void receiveEcgRawData(byte[] data) {
                super.receiveEcgRawData(data);
                realTimeView.addEcgData(data);
            }

            @Override
            public void receiveRemoteData(byte[] data) {
                super.receiveRemoteData(data);
                RemoteManager.getInstance().sendEcgMsg(data, "啊啊啊啊啊啊啊啊啊", 1, 22);
            }
        });
        EcgPatchManager.getInstance(patchMac).connectEcgPatch(new OnConnectListener() {
            @Override
            public void onConnectSuccess(boolean isNewUUID) {
                super.onConnectSuccess(isNewUUID);
                Logger.w("心电贴连接成功.");
            }

            @Override
            public void onConnectFaild() {
                super.onConnectFaild();
                Logger.w("心电贴连接失败");
            }

            @Override
            public void onDisconnect(boolean isManual) {
                super.onDisconnect(isManual);
                Logger.w("心电贴连接断开,isManual:%s", isManual);
            }
        });
    }

}