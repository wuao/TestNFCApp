package com.example.testnfcapp;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.testnfcapp.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity  implements View.OnClickListener {


    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.tvRead.setOnClickListener(this);
        binding.tvWirte.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v== binding.tvRead&& statrNFC())
            jumActivity(true);
        else if (v== binding.tvWirte&& statrNFC())
            jumActivity(false);
    }


    private void  jumActivity(boolean type){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putBoolean("type",type);
        intent.putExtras(bundle);
        intent.setClass(this,ReadActivity.class);
        startActivity(intent);
    }

    /**
     * 开启设备读取nfc 权限
     */
    public  boolean statrNFC(   ){
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
       return  ifNFCUse();



    }
    private boolean ifNFCUse() {
        if (this.mNfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!this.mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }











}
