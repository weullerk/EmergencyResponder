package com.alienonwork.emergencyresponder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

public class EmergencyResponderMainActivity extends AppCompatActivity {
    ReentrantLock lock;
    ArrayList<String> requesters = new ArrayList<String>();

    private static final int SMS_RECEIVE_PERMISSION_REQUEST = 1;

    private RequesterRecyclerViewAdapter mRequesterAdapter = new RequesterRecyclerViewAdapter(requesters);

    BroadcastReceiver emergencyResponseRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                String queryString = getString(R.string.querystring).toLowerCase();

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    SmsMessage[] messages = getMessagesFromIntent(intent);

                    for (SmsMessage message : messages) {
                        if (message.getMessageBody().toLowerCase().contains(queryString))
                            requestReceived(message.getOriginatingAddress());
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_responder_main);

        lock = new ReentrantLock();
        wireUpButtons();

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS,
                                                                    Manifest.permission.SEND_SMS,
                                                                    Manifest.permission.READ_PHONE_STATE},
                                                                    SMS_RECEIVE_PERMISSION_REQUEST);

        RecyclerView recyclerView = findViewById(R.id.requesterRecyclerListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRequesterAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(emergencyResponseRequestReceiver, filter);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(emergencyResponseRequestReceiver);
    }

    private void wireUpButtons() {
        Button okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respond(true);
            }
        });

        Button notOkButton = findViewById(R.id.notOkButton);
        notOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respond(false);
            }
        });

    }

    public void respond(boolean ok) {
        String okString = getString(R.string.allClearText);
        String notOkString = getString(R.string.maydayText);
        String outString = ok ? okString : notOkString;

        ArrayList<String> requestersCopy = (ArrayList<String>)requesters.clone();

        for (String to : requestersCopy)
            sendResponse(to, outString);
    }

    private void sendResponse(String to, String response) {}

    public void requestReceived(String from) {
        if (!requesters.contains(from)) {
            lock.lock();
            requesters.add(from);
            mRequesterAdapter.notifyDataSetChanged();
            lock.unlock();
        }
    }
}

