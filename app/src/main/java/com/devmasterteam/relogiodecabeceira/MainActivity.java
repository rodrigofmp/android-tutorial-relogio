package com.devmasterteam.relogiodecabeceira;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean mRunnableStopped = false;
    private boolean mIsBatteryOn = true;

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.mTextBatteryLevel.setText(level + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mViewHolder.mTextHourMinute = this.findViewById(R.id.text_hour_minute);
        this.mViewHolder.mTextSeconds = this.findViewById(R.id.text_seconds);
        this.mViewHolder.mCheckBattery = this.findViewById(R.id.check_battery);
        this.mViewHolder.mTextBatteryLevel = this.findViewById(R.id.text_battery_level);
        this.mViewHolder.mImageOption = this.findViewById(R.id.image_option);
        this.mViewHolder.mImageClose = this.findViewById(R.id.image_close);
        this.mViewHolder.mLinearOptions = this.findViewById(R.id.linear_options);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.registerReceiver(this.mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        this.mViewHolder.mCheckBattery.setChecked(true);

        this.mViewHolder.mLinearOptions.animate()
                .translationY(500)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

        this.setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mRunnableStopped = false;
        this.startBedside();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.mRunnableStopped = true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.check_battery) {
            this.toggleCheckBattery();
        } else if (id == R.id.image_option) {
            this.mViewHolder.mLinearOptions.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearOptions.animate()
                    .translationY(0)
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        } else if (id == R.id.image_close) {
            this.mViewHolder.mLinearOptions.animate()
                    .translationY(this.mViewHolder.mLinearOptions.getMeasuredHeight())
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            this.mViewHolder.mLinearOptions.setVisibility(View.GONE);
        }
    }

    private void toggleCheckBattery() {
        if (this.mIsBatteryOn) {
            this.mIsBatteryOn = false;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.GONE);
        } else {
            this.mIsBatteryOn = true;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        this.mViewHolder.mCheckBattery.setOnClickListener(this);
        this.mViewHolder.mImageOption.setOnClickListener(this);
        this.mViewHolder.mImageClose.setOnClickListener(this);
    }

    private void startBedside() {
        final Calendar calendar = Calendar.getInstance();
        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                if (mRunnableStopped)
                    return;

                calendar.setTimeInMillis(System.currentTimeMillis());

                String hourMinutesFormat = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                String secondsFormat = String.format("%02d", calendar.get(Calendar.SECOND));

                mViewHolder.mTextHourMinute.setText(hourMinutesFormat);
                mViewHolder.mTextSeconds.setText(secondsFormat);

                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - (now % 1000));
                mHandler.postAtTime(this, next);
            }
        };
        this.mRunnable.run();
    }

    private static class ViewHolder {
        TextView mTextHourMinute;
        TextView mTextSeconds;
        CheckBox mCheckBattery;
        TextView mTextBatteryLevel;
        ImageView mImageOption;
        ImageView mImageClose;
        LinearLayout mLinearOptions;
    }
}
