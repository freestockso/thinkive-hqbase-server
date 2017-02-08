package com.thinkive.market;


/**
 * Created by SHENG on 2017/2/7.
 */
public class DataManagerTest {

    private DataManager dataManager = new DataManager();

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void init() throws Exception {

    }

    @org.junit.Test
    public void update() throws Exception {
        dataManager.update();

        System.out.println("    ");
        System.out.println("    ");
        System.out.println("    ");
        System.out.println("    ");
        System.out.println("    ");
        System.out.println("    --  µÚ¶þ´Î");
        System.out.println("    ");
        System.out.println("    ");
        System.out.println("    ");

        dataManager.update();

    }

    @org.junit.Test
    public void checkDirectory() throws Exception {
        dataManager.checkDirectory("D:/logs/temp");
    }

    @org.junit.Test
    public void download() throws Exception {

    }

    @org.junit.Test
    public void handle() throws Exception {
        dataManager.handle();
    }

    @org.junit.Test
    public void downloadData() throws Exception {

    }

    @org.junit.Test
    public void downloadTDXFile() throws Exception {

    }

    @org.junit.Test
    public void downloadDZHFile() throws Exception {

    }

    @org.junit.Test
    public void downloadFileToDir() throws Exception {

    }

    @org.junit.Test
    public void completeJob() throws Exception {

    }

    @org.junit.Test
    public void resetJobCount() throws Exception {

    }

}