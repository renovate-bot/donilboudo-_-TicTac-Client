package com.nsidetech.tictac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nsidetech.tictac.R;
import com.nsidetech.tictac.domain.Delivery;
import com.nsidetech.tictac.util.DeliveryConstants;
import com.nsidetech.tictac.util.MessageUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DeliveryDetailsActivity extends AppCompatActivity {
    private EditText mRequestNumber;
    private EditText mStatus;
    private EditText mDeliveryRequestDate;
    private EditText mSenderName;
    private EditText mReceiverName;
    private EditText mReceiverAddress;
    private EditText mDeliverName;
    private TextView mDeliverNameLabel;
    private EditText mReceiveDate;
    private TextView mReceiveDateLabel;
    private Button mCancelRequest;
    private ProgressBar mProgressBar;

    private Delivery selectedDelivery;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);

    private static final String TAG = "DeliveryDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        //get selected delivery in extra data
        Intent intent = getIntent();
        selectedDelivery = (Delivery) intent.getSerializableExtra(DeliveryConstants.SELECTED_DELIVERY_REQUEST);

        initViews();

        fillInfo();
    }

    private void initViews() {
        mRequestNumber = findViewById(R.id.txtRequestNumber);
        mStatus = findViewById(R.id.txtStatus);
        mDeliveryRequestDate = findViewById(R.id.txtRequestDate);
        mSenderName = findViewById(R.id.txtSenderName);
        mReceiverName = findViewById(R.id.txtReceiverName);
        mReceiverAddress = findViewById(R.id.txtReceiverAddress);

        mDeliverName = findViewById(R.id.txtDeliverName);
        mDeliverNameLabel = findViewById(R.id.lblDeliverName);

        mReceiveDate = findViewById(R.id.txtReceiveDate);
        mReceiveDateLabel = findViewById(R.id.lblReceiveDate);

        mCancelRequest = findViewById(R.id.btnCancelRequest);

        mProgressBar = findViewById(R.id.progressbar);
        showProgress(false);
    }

    private void fillInfo() {
        String requestStatus = selectedDelivery.getStatus();
        if (DeliveryConstants.WAITING_FOR_APPROVE.equals(requestStatus))
        {
            requestStatus = getResources().getString(R.string.waiting_for_approve);
        }
        else if (DeliveryConstants.ASSIGN_TO_DELIVER.equals(requestStatus))
        {
            requestStatus = getResources().getString(R.string.assign_to_deliver);
        }
        else if (DeliveryConstants.STARTED.equals(requestStatus))
        {
            requestStatus = getResources().getString(R.string.started);
        }
        else
        {
            requestStatus = getResources().getString(R.string.ended);
        }
        mStatus.setText(requestStatus);

        mSenderName.setText(selectedDelivery.getSenderName());
        mReceiverName.setText(selectedDelivery.getReceiverName());

        String receiverAddress = selectedDelivery.getReceiverAddress();
        mReceiverAddress.setText(receiverAddress);

        if (selectedDelivery.getDeliverer() != null &&
                selectedDelivery.getDeliverer().getFullName() != null &&
                !selectedDelivery.getDeliverer().getFullName().contains("null"))
        {
            mDeliverNameLabel.setVisibility(View.VISIBLE);
            mDeliverName.setVisibility(View.VISIBLE);
            mDeliverName.setText(selectedDelivery.getDeliverer().getFullName());
        }
        else
        {
            mDeliverNameLabel.setVisibility(View.GONE);
            mDeliverName.setVisibility(View.GONE);
        }

        if (!selectedDelivery.getStatus().equals(DeliveryConstants.COMPLETED))
        {
            //hide elements
            mReceiveDateLabel.setVisibility(View.GONE);
            mReceiveDate.setVisibility(View.GONE);
        }
        else
        {
            mReceiveDateLabel.setVisibility(View.VISIBLE);
            mReceiveDate.setVisibility(View.VISIBLE);
            mReceiveDate.setText(selectedDelivery.getCompleteDate());
        }

        mDeliveryRequestDate.setText(selectedDelivery.getRequestDate());
        mRequestNumber.setText(selectedDelivery.getDeliveryNumber());

        if (!DeliveryConstants.WAITING_FOR_APPROVE.equals(selectedDelivery.getStatus()))
        {
            mCancelRequest.setEnabled(false);
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent(getApplicationContext(), MyDeliveriesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MyDeliveriesActivity.class);
        startActivity(intent);
    }

    private void showProgress(boolean show) {
        if (mProgressBar != null)
        {
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.setIndeterminate(show);
        }
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

    public void cancelRequest(View view) {
        showProgress(true);

        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(DeliveryConstants.FIRESTORE_DELIVERIES_COLLECTION_NAME).document(selectedDelivery.getId());
        ref.update("status", DeliveryConstants.CANCELLED)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MessageUtil.getInstance().ToastMessage(DeliveryDetailsActivity.this, getResources().getString(R.string.requestSuccessfullyCancel));

                        showProgress(false);

                        //go back to menu
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                        MessageUtil.getInstance().SnackMessage(DeliveryDetailsActivity.this, getString(R.string.unableToCreateNewRequest), R.id.activity_delivery_details);
                    }
                });
    }
}
