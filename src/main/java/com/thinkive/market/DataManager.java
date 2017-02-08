package com.thinkive.market;

import com.thinkive.base.config.Configuration;
import com.thinkive.base.util.FileHelper;
import com.thinkive.base.util.net.HttpHelper;
import com.thinkive.base.util.zip.ZipHelper;
import com.thinkive.market.job.DownloadFileJob;
import com.thinkive.market.job.ExecuteFileJob;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;


public class DataManager {

    private static Logger logger = Logger.getLogger(DataManager.class);

    private static AtomicInteger completeJobCount = new AtomicInteger(0);

    /**
     * 初始化
     */
    public static void init() {
        try {
            checkDirectory(HQBaseConfig.FILE_PATH);
            checkDirectory(HQBaseConfig.FILE_PATH_TEMP);
            FileHelper.cleanDirectory(new File(HQBaseConfig.FILE_PATH_TEMP));
        } catch (Exception e) {
            logger.error("", e);
            logger.error("  --  @系统初始化  --  初始化失败，系统退出！！！");
            System.exit(-1);
        }
    }

    /**
     * 数据更新
     */
    public static void update() {
        init();

        logger.info("   --  开始更新基础数据...");

        download();
        handle();

        logger.info("   --  更新基础数据完成");
        resetJobCount();
    }

    /**
     * 检查文件夹
     */
    public static void checkDirectory(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            FileHelper.createDirectory(path);
        } else if (!file.isDirectory()) {
            throw new Exception("配置路径不是文件夹路径:" + path);
        }
    }

    /**
     * 下载文件
     */
    public static void download() {
        logger.info("   --  开始下载基础数据...");
        long t = System.currentTimeMillis();

        for (String fileName : HQBaseConfig.FILE_URL_MAP.keySet()) {
            new DownloadFileJob(fileName, HQBaseConfig.FILE_URL_MAP.get(fileName), HQBaseConfig.FILE_PATH_TEMP).run();
        }

        t = System.currentTimeMillis() - t;
        logger.info("   --  下载基础数据完成,共耗时[" + t / 1000 + "s]");
    }

    /**
     * 处理下载文件
     */
    public static void handle() {
        logger.info("   --  开始处理下载数据...");
        long t = System.currentTimeMillis();

        File tempDirectory = new File(HQBaseConfig.FILE_PATH_TEMP);
        File files[] = tempDirectory.listFiles();
        for (File file : files) {
            Runnable runnable = new ExecuteFileJob(file.getName(), HQBaseConfig.FILE_PATH, HQBaseConfig.FILE_PATH_TEMP);
            new Thread(runnable).start();
        }

        while (completeJobCount.get() < files.length) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        t = System.currentTimeMillis() - t;
        logger.info("   --  处理下载数据完成,共耗时[" + t / 1000 + "s]");
    }

    @Deprecated
    public static void downloadData() {

        logger.info("开始更新基础数据");

        String strTempPath = Configuration.getString("data.temp");
        if (!FileHelper.isDirectory(strTempPath)) {
            logger.error("临时存储目录配置不正确！");
            return;
        }
        FileHelper.cleanDirectory(new File(strTempPath));

        String strDataPath = Configuration.getString("data.store");
        if (!FileHelper.isDirectory(strDataPath)) {
            logger.error("数据存储目录配置不正确！");
            return;
        }

        downloadTDXFile(strTempPath, strDataPath);

        downloadDZHFile(strTempPath, strDataPath);

//        for (Runnable job : executeJobList) {
//            new Thread(job).start();
//        }


        FileHelper.cleanDirectory(new File(strTempPath));

        logger.info("更新基础数据结束...");
    }

    @Deprecated
    public static void downloadTDXFile(String strTempPath, String strDataPath) {
        try {
            String strUrlFile = "http://59.175.238.39/products/data/data/dbf/base.zip";
            String zipFilePath = strTempPath + "/base.zip";
            byte[] byteContent = HttpHelper.getURLContent(strUrlFile);
            if (byteContent != null) {
                FileHelper.createNewFile(zipFilePath);
                FileOutputStream outStream = new FileOutputStream(zipFilePath);
                outStream.write(byteContent);
                outStream.close();
                logger.info("下载文件[" + zipFilePath + "]成功......");

                ZipHelper.decompress(zipFilePath, strTempPath);
                logger.info("解压文件[" + zipFilePath + "]成功......");

//                String strDestFile = strDataPath + "/block.dat";
//                FileHelper.copyFile(strTempPath + "/block.dat", strDestFile);
//                logger.info("拷贝文件[" + strDestFile + "]成功......");
//
//                strDestFile = strDataPath + "/base.dbf";
//                FileHelper.copyFile(strTempPath + "/base.dbf", strDestFile);
//                logger.info("拷贝文件[" + strDestFile + "]成功......");
//
//                FileHelper.cleanDirectory(new File(strTempPath));
            }

        } catch (Exception ex) {
            logger.error("", ex);
        }
    }
    @Deprecated
    public static void downloadDZHFile(String strTempPath, String strDataPath) {
        try {
            String strUrlFile = "http://222.73.103.183/platform/download/PWR/full.PWR";
            String strTempFile = strTempPath + "/full.PWR";
            String strDestFile = strDataPath + "/full.PWR";
            downloadFileToDir(strUrlFile, strTempFile);
            logger.info("下载文件[" + strTempFile + "]成功......");
//            FileHelper.copyFile(strTempFile, strDestFile);
//            logger.info("拷贝文件[" + strDestFile + "]成功......");

            strUrlFile = "http://.../platform/download/FIN/full.FIN";
            strTempFile = strTempPath + "/full.FIN";
            strDestFile = strDataPath + "/full.FIN";
            downloadFileToDir(strUrlFile, strTempFile);
            logger.info("下载文件[" + strTempFile + "]成功......");
//            FileHelper.copyFile(strTempFile, strDestFile);
//            logger.info("拷贝文件[" + strDestFile + "]成功......");

            strUrlFile = "http://.../platform/download/ABK/full.ABK";
            strTempFile = strTempPath + "/full.ABK";
            strDestFile = strDataPath + "/full.ABK";
            downloadFileToDir(strUrlFile, strTempFile);
            logger.info("下载文件[" + strTempFile + "]成功......");
//            FileHelper.copyFile(strTempFile, strDestFile);
//            logger.info("拷贝文件[" + strDestFile + "]成功......");

            strUrlFile = "http://.../platform/download_HK/download/ABK/inc.ABK";
            strTempFile = strTempPath + "/inc.ABK";
            strDestFile = strDataPath + "/inc.ABK";
            downloadFileToDir(strUrlFile, strTempFile);
            logger.info("下载文件[" + strTempFile + "]成功......");
//            FileHelper.copyFile(strTempFile, strDestFile);
//            logger.info("拷贝文件[" + strDestFile + "]成功......");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    @Deprecated
    public static void downloadFileToDir(String strUrlFile, String strDestFile) {
        try {
            byte[] byteContent = HttpHelper.getURLContent(strUrlFile);
            if (byteContent != null) {
                FileHelper.createNewFile(strDestFile);
                FileOutputStream outStream = new FileOutputStream(strDestFile);
                outStream.write(byteContent);
                outStream.close();
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public static void completeJob() {
        completeJobCount.incrementAndGet();
    }

    public static void resetJobCount() {
        completeJobCount.set(0);
    }

}

 