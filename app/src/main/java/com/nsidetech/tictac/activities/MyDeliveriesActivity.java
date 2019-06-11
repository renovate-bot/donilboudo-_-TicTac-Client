package com.nsidetech.tictac.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nsidetech.tictac.R;
import com.nsidetech.tictac.domain.Delivery;
import com.nsidetech.tictac.network.NetworkHelper;
import com.nsidetech.tictac.util.DeliveryConstants;
import com.nsidetech.tictac.util.DeviceManager;
import com.nsidetech.tictac.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class MyDeliveriesActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private List<Delivery> deliveryRequests;

    private FirebaseFirestore db;
    private static final String TAG = "MyDeliveriesActivity";

    private static String SERVER_ERROR_MESSAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deliveries);
        SERVER_ERROR_MESSAGE = getResources().getString(R.string.serverError);

        if (NetworkHelper.isOnline(this))
        {
            FirebaseApp.initializeApp(this);
            db = FirebaseFirestore.getInstance();

            deliveryRequests = new ArrayList<>();
            loadMyDeliveries();
        }
        else
        {
            MessageUtil.getInstance().SnackMessage(this, getResources().getString(R.string.notNetworkErrorMessage), R.id.activity_my_deliveries);
        }
    }

    private void loadMyDeliveries() {
        String deviceId = DeviceManager.getInstance().getDeviceId(this);

        progressDialog = new ProgressDialog(this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Un moment...");
        progressDialog.show();

        db.collection(DeliveryConstants.FIRESTORE_PRICES_COLLECTION_NAME)
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (progressDialog != null)
                            {
                                progressDialog.dismiss();
                            }

                            if (task.getResult() != null && task.getResult().size() > 0)
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    final ObjectMapper mapper = new ObjectMapper();
                                    final Delivery request = mapper.convertValue(document.getData(), Delivery.class);
                                    if (!request.getStatus().equals(DeliveryConstants.CANCELLED))
                                    {
                                        deliveryRequests.add(request);
                                    }
                                }

                                initListView();
                            }
                            else
                            {
                                MessageUtil.getInstance().SnackMessage(MyDeliveriesActivity.this, getResources().getString(R.string.noRequestedDeliveries), R.id.activity_my_deliveries);
                            }
                        }
                        else
                        {
                            Log.w(TAG, "Error getting documents.", task.getException());

                            MessageUtil.getInstance().ToastMessage(getApplicationContext(), SERVER_ERROR_MESSAGE);
                        }
                    }
                });
    }

    private void initListView() {
        ListView mDeliveries = findViewById(R.id.lstDeliveries);
        mDeliveries.setAdapter(new MyDeliveriesAdapter(this, deliveryRequests));
        mDeliveries.setTextFilterEnabled(true);
        mDeliveries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, open delivery details and user can finish his delivery
                Delivery request = deliveryRequests.get(position);

                Intent intent = new Intent(getApplicationContext(), DeliveryDetailsActivity.class);
                intent.putExtra(DeliveryConstants.SELECTED_DELIVERY_REQUEST, request);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            onBackPressed();
            //kill activity
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
