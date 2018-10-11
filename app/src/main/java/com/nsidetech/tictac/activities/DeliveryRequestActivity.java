package com.nsidetech.tictac.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nsidetech.tictac.R;
import com.nsidetech.tictac.network.NetworkHelper;
import com.nsidetech.tictac.util.DeliveryConstants;
import com.nsidetech.tictac.util.DeviceManager;
import com.nsidetech.tictac.util.MessageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DeliveryRequestActivity extends AppCompatActivity {
    private EditText mSenderName;
    private EditText mSenderNumber;
    private EditText mSenderAddress;
    private EditText mRequestDate;
    private EditText mReceiverName;
    private EditText mReceiverAddress;
    private EditText mSenderComments;
    private EditText mDPackageType;
    private EditText mReceiverNumber;
    private Spinner spCities;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);

    private FirebaseFirestore db;
    private static final String TAG = "DeliveryRequestActivity";

    private List<String> countries;
    private List<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_request);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        if (NetworkHelper.isOnline(this))
        {
            initViews();
        }
        else
        {
            MessageUtil.getInstance().SnackMessage(this, getResources().getString(R.string.notNetworkErrorMessage), R.id.activity_delivery_request);
        }
    }

    private void initViews() {
        // loadCountriesAndCities();
        cities = new ArrayList<>();
        cities.add("Ouagadougou");

        setContentView(R.layout.activity_delivery_request);

        mRequestDate = findViewById(R.id.txtSendDate);
        mSenderName = findViewById(R.id.txtSenderName);
        mSenderNumber = findViewById(R.id.txtSenderNumber);
        mSenderAddress = findViewById(R.id.txtSenderAddress);
        mReceiverName = findViewById(R.id.txtReceiverName);
        mReceiverAddress = findViewById(R.id.txtReceiverAddress);
        mSenderComments = findViewById(R.id.txtSenderComments);
        mDPackageType = findViewById(R.id.txtPackageType);
        mReceiverNumber = findViewById(R.id.txtReceiverNumber);

        Date requestDate = new Date();
        mRequestDate.setText(sdf.format(requestDate));

        spCities = findViewById(R.id.spCity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void loadCountriesAndCities() {
        db.collection("countries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                        else
                        {
                            countries = new ArrayList<>();
                            cities = new ArrayList<>();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void saveDeliveryRequest(View view) {
        String requestDate = mRequestDate.getText().toString();
        String senderName = mSenderName.getText().toString();
        String senderNumber = mSenderNumber.getText().toString();
        String senderAddress = mSenderAddress.getText().toString();
        String receiverName = mReceiverName.getText().toString();
        String receiverAddress = mReceiverAddress.getText().toString();
        String senderComments = mSenderComments.getText().toString();
        String packageType = mDPackageType.getText().toString();
        String receiverNumber = mReceiverNumber.getText().toString();

        //get phone id
        String deviceId = DeviceManager.getInstance().getDeviceId(this);

        if (validData(requestDate, senderName, senderNumber, senderAddress, receiverName, receiverAddress, senderComments, packageType))
        {
            ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Un moment...");
            progressDialog.show();

            String firebaseToken = FirebaseInstanceId.getInstance().getToken();

            Map<String, Object> request = new HashMap<>();
            request.put("requestDateStr", requestDate);
            request.put("senderName", senderName);
            request.put("senderNumber", senderNumber);
            request.put("senderAddress", senderAddress);
            request.put("receiverName", receiverName);
            request.put("receiverAddress", receiverAddress);
            request.put("senderComments", senderComments);
            request.put("packageType", packageType);
            request.put("receiverNumber", receiverNumber);
            request.put("deviceId", deviceId);
            request.put("firebaseToken", firebaseToken);
            request.put("status", DeliveryConstants.WAITING_FOR_APPROVE);

            String id = db.collection(DeliveryConstants.FIRESTORE_COLLECTION_NAME).document().getId();
            request.put("id", id);

            //store to firestore
            db.collection(DeliveryConstants.FIRESTORE_COLLECTION_NAME)
                    .add(request)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                            MessageUtil.getInstance().ToastMessage(getApplicationContext(), getResources().getString(R.string.requestSuccessfullyCreate));

                            //go back to menu
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);

                            MessageUtil.getInstance().SnackMessage(DeliveryRequestActivity.this, getString(R.string.unableToCreateNewRequest), R.id.activity_delivery_request);
                        }
                    });
        }
    }

    private boolean validData(String requestDate,
                              String senderName,
                              String senderNumber,
                              String senderAddress,
                              String receiverName,
                              String receiverAddress,
                              String senderComments,
                              String packageType) {
        boolean isValid = true;

        if (senderName == null || senderName.isEmpty())
        {
            mSenderName.setError(getResources().getString(R.string.fieldCannotBeEmpty));
            isValid = false;
        }
        if (senderNumber == null || senderNumber.isEmpty())
        {
            mSenderNumber.setError(getResources().getString(R.string.fieldCannotBeEmpty));
            isValid = false;
        }
        if (senderAddress == null || senderAddress.isEmpty())
        {
            mSenderAddress.setError(getResources().getString(R.string.fieldCannotBeEmpty));
            isValid = false;
        }
        if (receiverName == null || receiverName.isEmpty())
        {
            mReceiverName.setError(getResources().getString(R.string.fieldCannotBeEmpty));
            isValid = false;
        }
        if (receiverAddress == null || receiverAddress.isEmpty())
        {
            mReceiverAddress.setError(getResources().getString(R.string.fieldCannotBeEmpty));
            isValid = false;
        }

        return isValid;
    }

    public void cancel(View view) {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
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
