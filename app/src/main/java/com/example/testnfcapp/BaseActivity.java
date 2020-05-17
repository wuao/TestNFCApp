package com.example.testnfcapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onStart() {
        super.onStart();
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.mNfcAdapter != null) {
            this.mNfcAdapter.enableForegroundDispatch(this, this.mPendingIntent, null, (String[][]) null);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (this.mNfcAdapter != null) {
            this.mNfcAdapter.disableForegroundDispatch(this);
        }
    }

}
