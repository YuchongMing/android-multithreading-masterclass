package com.techyourchance.multithreading.exercises.exercise3;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techyourchance.multithreading.R;
import com.techyourchance.multithreading.common.BaseFragment;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class Exercise3Fragment extends BaseFragment {

    private static final int SECOND = 1;

    public static Fragment newInstance() {
        return new Exercise3Fragment();
    }

    private Button mBtnCountSeconds;
    private TextView mTxtCount;

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_3, container, false);

        mBtnCountSeconds = view.findViewById(R.id.btn_count_seconds);
        mTxtCount = view.findViewById(R.id.txt_count);
        mBtnCountSeconds.setOnClickListener(unused -> countIterations());
        return view;
    }

    @Override
    protected String getScreenTitle() {
        return "Exercise 3";
    }

    private void countIterations() {
        /*
        1. Disable button to prevent multiple clicks
        2. Start counting on background thread using loop and Thread.sleep()
        3. Show count in TextView
        4. When count completes, show "done" in TextView and enable the button
         */
        new Thread(this::countSecondsInBackground).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void countSecondsInBackground() {
        if (Looper.getMainLooper().isCurrentThread()) {
            return;
        }
        mUiHandler.post(() -> mBtnCountSeconds.setEnabled(false));
        Instant endTime = Instant.now().plus(Duration.ofSeconds(SECOND * 3));
        AtomicInteger screenSecond = new AtomicInteger(1);
        while (Instant.now().isBefore(endTime)) {
            mUiHandler.post(
                    () -> mTxtCount.setText(Integer.toString(screenSecond.getAndIncrement())));
            try {
                Thread.sleep(Duration.ofSeconds(SECOND).toMillis());
            } catch (InterruptedException e) {
                break;
            }
        }
        mUiHandler.post(() -> {
            mTxtCount.setText("Done!");
            mBtnCountSeconds.setEnabled(true);
        });
    }

    private void enableClick() {
        mBtnCountSeconds.setOnClickListener(unused -> countIterations());
    }

    private void disableClick() {
        mBtnCountSeconds.setOnClickListener(unused -> {});
    }
}
