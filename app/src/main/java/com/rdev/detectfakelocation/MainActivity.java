package com.rdev.detectfakelocation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button btnRefresh;
    private TextView tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDesc = findViewById(R.id.tv_desc);
        btnRefresh = findViewById(R.id.btn_refersh);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMockSettingsON(MainActivity.this)==true && areThereMockPermissionApps(MainActivity.this)==true) {
                    tvDesc.setText("Fake Apps Detected");
                } else if (isMockSettingsON(MainActivity.this)==true && areThereMockPermissionApps(MainActivity.this)==false) {
                    tvDesc.setText("Pengaturan Mock Location aktif !!!");
                } else if (isMockSettingsON(MainActivity.this)==false && areThereMockPermissionApps(MainActivity.this)==true) {
                    tvDesc.setText("Ada aplikasi butuh izin lokasi palsu");
                } else {
                    tvDesc.setText("Aman");
                }


            }
        });
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i].equals("android.permission.ACCESS_MOCK_LOCATION")) {
                            if (!applicationInfo.packageName.equals("com.android.calendar")) {
                                count++;
                                Toast.makeText(context, "Hapus Aplikasi Fake Gps", Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    private boolean isMockLocationEnabled()
    {
        boolean isMockLocation;
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (Objects.requireNonNull(opsManager).checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
            } else {
                isMockLocation = !android.provider.Settings.Secure.getString(this.getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return false;
        }
        return isMockLocation;
    }
}