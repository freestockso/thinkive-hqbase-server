package com.thinkive.task;

import com.thinkive.market.DataManager;
import com.thinkive.timerengine.Task;

public class DownloadTask implements Task {
    public void execute() {
        DataManager.update();
    }
}