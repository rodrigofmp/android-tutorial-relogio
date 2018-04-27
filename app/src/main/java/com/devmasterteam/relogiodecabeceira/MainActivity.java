package com.devmasterteam.relogiodecabeceira;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean mRunnableStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.mViewHolder.mTextHourMinute = this.findViewById(R.id.text_hour_minute);
        this.mViewHolder.mTextSeconds = this.findViewById(R.id.text_seconds);
        this.mViewHolder.mCheckBattery = this.findViewById(R.id.check_battery);
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
    }
}
