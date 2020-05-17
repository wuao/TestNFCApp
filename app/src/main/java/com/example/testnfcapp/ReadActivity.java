package com.example.testnfcapp;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.testnfcapp.databinding.ActivitReadViewBinding;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * 读取nfc
 */
public class ReadActivity extends BaseActivity implements View.OnClickListener {

    public static ReadActivity instance;
    private NfcAdapter mNfcAdapter;

    private ActivitReadViewBinding binding;
    private Intent intent;

    private boolean type;

    @Override
    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        binding = DataBindingUtil.setContentView(this, R.layout.activit_read_view);
        initData();
        initView();

    }

    private void initData() {
        instance = this;
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Bundle bundle = getIntent().getExtras();
        type = false;
        if (bundle != null) {
            type = bundle.getBoolean("type");
        }
    }

    private void initView() {
        binding.rlyTitle.tvTitle.setText(getString(R.string.text));

        if (type) {
            //读取
            binding.rlyTitle.tvRight.setText("清除");
            binding.tvInput.setVisibility(View.VISIBLE);
            binding.evInput.setVisibility(View.GONE);
        } else {
            binding.rlyTitle.tvRight.setText("保存");
            binding.tvInput.setVisibility(View.GONE);
            binding.evInput.setVisibility(View.VISIBLE);
        }


    }

    private void initListener(ReadActivity that) {
        binding.rlyTitle.imgBack.setOnClickListener(that);
        binding.rlyTitle.tvRight.setOnClickListener(that);

    }


    @Override
    protected void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        String s = readFromTag(paramIntent);
        binding.tvInput.setText(s);
        if (!type) {
            intent = paramIntent;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.rlyTitle.imgBack) {
            finish();
        } else if (v == binding.rlyTitle.tvRight) {
            if (type) {
                binding.tvInput.setText("");
            } else {
                writeNdefMessage(binding.evInput.getText().toString().trim());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        initListener(null);
    }


    //读取内容：
    private String readFromTag(Intent intent) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        getNFCSupportFormat(detectedTag);
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawArray == null) {
            return "没有数据";
        }
        NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
        NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
        try {
            if (mNdefRecord != null) {
                return new String(mNdefRecord.getPayload(), "UTF-8");

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入数据
     * 注意有些 nfc 卡支持的格式不一样所以需要 写入前先进行判断
     * @param data
     */
    private void writeNdefMessage(String data) {
        if (intent == null) {
            Toast.makeText(this, "请重新刷卡感应！", Toast.LENGTH_SHORT).show();
            return;
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        getNFCSupportFormat(tag);
        Ndef ndef = Ndef.get(tag);
        NdefFormatable ndefFormatable = NdefFormatable.get(tag);
        NdefRecord ndefRecord = NdefRecord.createTextRecord(null, data);
        NdefRecord[] records = {ndefRecord};
        NdefMessage ndefMessage = new NdefMessage(records);
        try {
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(ndefMessage);
            }else if (ndefFormatable!=null){
                ndefFormatable.connect();
                ndefFormatable.format(ndefMessage);
            }
            binding.evInput.setText("");
            Toast.makeText(this, "保存成功，请重新刷卡读取测试！", Toast.LENGTH_SHORT).show();
        } catch (FormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getNFCSupportFormat(Tag tag) {
        boolean  type=false;
        if (tag == null) {
            return type;
        }
        String[] name = tag.getTechList();
        for (String bean : name) {
            if (bean.contains("NdefFormatable")){
                type=true;
            }
            Log.d("test------支持的格式有 ", bean);
        }
        return type;
    }
}
