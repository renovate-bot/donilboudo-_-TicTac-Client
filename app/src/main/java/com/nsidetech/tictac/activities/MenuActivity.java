package com.nsidetech.tictac.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nsidetech.tictac.R;
import com.nsidetech.tictac.network.NetworkHelper;
import com.nsidetech.tictac.util.AlertDialogUtil;
import com.nsidetech.tictac.util.MessageUtil;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    private final int MY_PERMISSIONS_REQUEST = 1000;
    public static boolean hasReadPhonePermission = false;
    private static final String APP_LINK = "https://play.google.com/store/apps/details?id=com.beogotechnologies.ca.deliverymanager_mobileapp";
    private static final String facebookUrl = "https://www.facebook.com/tictaclivraison";
    private final static String tel = "+226 56 53 10 12";
    private boolean hasCallPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (getAppVersion() != null)
        {
            TextView mAppVersion = findViewById(R.id.lblAppVersion);
            String version = mAppVersion.getText() + " " + getAppVersion();
            mAppVersion.setText(version);
        }

        checkPermissions();
    }

    private String getAppVersion() {
        PackageInfo pInfo;
        String version = null;
        try
        {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return version;
    }

    public void onDeliveryRequest(View view) {
        if (NetworkHelper.isOnline(this))
        {
            checkPermissions();
            if (hasReadPhonePermission)
            {
                Intent intent = new Intent(getApplicationContext(), DeliveryRequestActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
            else
            {
                showPermissionErrorDialog();
            }
        }
        else
        {
            MessageUtil.getInstance().SnackMessage(this, getResources().getString(R.string.notNetworkErrorMessage), R.id.activity_menu);
        }
    }

    public void onMyRequestedDeliveries(View view) {
        if (NetworkHelper.isOnline(this))
        {
            checkPermissions();
            if (hasReadPhonePermission)
            {
                Intent intent = new Intent(getApplicationContext(), MyDeliveriesActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
            else
            {
                showPermissionErrorDialog();
            }
        }
        else
        {
            MessageUtil.getInstance().SnackMessage(this, getResources().getString(R.string.notNetworkErrorMessage), R.id.activity_menu);
        }
    }

    public void call(View view) {
        checkCallPermission();
        if (hasCallPermission)
        {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
            startActivity(intent);
        }
        else
        {
            showPermissionErrorDialog();
        }
    }

    public void facebook(View view) {
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
        catch (Exception e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }

//    public void playstore(View view) {
//        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//        try
//        {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//        }
//        catch (android.content.ActivityNotFoundException anfe)
//        {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_LINK)));
//        }
//    }

    public void share(View view) {
        try
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, APP_LINK);
            startActivity(Intent.createChooser(shareIntent, ""));
        }
        catch (Exception e)
        {
            //TODO
        }
    }

    private void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))
            {
                // No explanation needed, we can request the permission.
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
        else
        {
            hasCallPermission = true;
        }
    }

    private void showPermissionErrorDialog() {
        AlertDialogUtil.showDialog(this, getResources().getString(R.string.readPhonePermissionDeniedErrorMessage));
    }

    public void onExitApp(View view) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
            {
                // No explanation needed, we can request the permission.
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
        else
        {
            hasReadPhonePermission = true;
        }
    }

    //call back for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST)
        {
            case MY_PERMISSIONS_REQUEST:
            {
                if (grantResults.length > 0)
                {
                    hasReadPhonePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    hasCallPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                }
                else
                {
                    AlertDialogUtil.showDialog(this, getResources().getString(R.string.readPhonePermissionDeniedErrorMessage));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
