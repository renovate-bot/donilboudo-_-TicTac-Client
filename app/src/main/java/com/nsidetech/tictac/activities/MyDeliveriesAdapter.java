package com.nsidetech.tictac.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nsidetech.tictac.R;
import com.nsidetech.tictac.domain.DeliveryRequest;
import com.nsidetech.tictac.util.DeliveryConstants;

import java.util.List;

/**
 * Created by fabrice on 2017-05-29.
 */

public class MyDeliveriesAdapter extends ArrayAdapter<DeliveryRequest> {
    private final Context context;
    private final List<DeliveryRequest> deliveries;

    MyDeliveriesAdapter(Context context, List<DeliveryRequest> deliveries) {
        super(context, R.layout.activity_my_deliveries_item, deliveries);
        this.context = context;
        this.deliveries = deliveries;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_my_deliveries_item, parent, false);

        DeliveryRequest deliveryRequest = deliveries.get(position);

        TextView mReceiver = rowView.findViewById(R.id.lblReceiver);
        String receiver = mReceiver.getText().toString() + " " + deliveryRequest.getReceiverName();
        mReceiver.setText(receiver);

        TextView mStatus = rowView.findViewById(R.id.lblStatus);
        String requestStatus = deliveryRequest.getStatus();
        if (DeliveryConstants.WAITING_FOR_APPROVE.equals(requestStatus))
        {
            requestStatus = context.getResources().getString(R.string.waiting_for_approve);
        }
        else if (DeliveryConstants.ASSIGN_TO_DELIVER.equals(requestStatus))
        {
            requestStatus = context.getResources().getString(R.string.assign_to_deliver);
        }
        else if (DeliveryConstants.STARTED.equals(requestStatus))
        {
            requestStatus = context.getResources().getString(R.string.started);
        }
        else
        {
            requestStatus = context.getResources().getString(R.string.ended);
        }
        String status = mStatus.getText().toString() + " " + requestStatus;
        mStatus.setText(status);

        return rowView;
    }
}