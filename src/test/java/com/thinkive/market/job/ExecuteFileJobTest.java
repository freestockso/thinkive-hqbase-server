package com.thinkive.market.job;

import org.junit.Test;

/**
 * Created by SHENG on 2017/2/7.
 */
public class ExecuteFileJobTest {

    private ExecuteFileJob executeFileJob = new ExecuteFileJob("", "", "");

    @Test
    public void run() throws Exception {

    }

    @Test
    public void deleteFile() throws Exception {
        executeFileJob.deleteFile("D:\\tempdata\\download\\base.dbf");
    }

    @Test
    public void retry() throws Exception {

    }

    @Test
    public void compreeFile() throws Exception {

    }

    @Test
    public void createFileMD5() throws Exception {

    }

    @Test
    public void copyFile() throws Exception {

    }

}