package com.example.myappdemo.feed;

import android.widget.ScrollView;
import android.widget.TextView;

public class CardExposureLogTool {
    private TextView textViewLog;
    private ScrollView scrollView;

    public CardExposureLogTool(ScrollView scrollView, TextView textViewLog) {
        this.scrollView = scrollView;
        this.textViewLog = textViewLog;
    }
    public void addLog(String log) {
        if (textViewLog == null) return;

        String currentLog = textViewLog.getText().toString();
        String newLog = currentLog + "\n" + log; // 换行追加

        // 更新TextView并自动滚动到底部
        textViewLog.setText(newLog);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    // 清空日志
    public void clearLog() {
        if (textViewLog != null) {
            textViewLog.setText("");
        }
    }
}
