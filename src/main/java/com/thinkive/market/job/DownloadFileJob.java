package com.thinkive.market.job;

import com.thinkive.base.util.FileHelper;
import com.thinkive.base.util.net.HttpHelper;
import com.thinkive.base.util.zip.ZipHelper;
import com.thinkive.market.HQBaseConfig;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述 : 下载文件任务
 * 版权 : Copyright-(c) 2017
 * 公司 : Thinkive
 *
 * @author 王嵊俊
 * @version 2017-02-07 10:03
 */
public class DownloadFileJob implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());

    private String fileName;
    private String url;
    private String tempPath;
    private String tempDirectory;

    public DownloadFileJob(String fileName, String url, String tempDirectory) {
        this.fileName = fileName;
        this.url = url;
        this.tempDirectory = tempDirectory;
        this.tempPath = tempDirectory + "/" + fileName;
    }

    @Override
    public void run() {

        while (!downLoad()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            logger.info("   --  重新下载[" + fileName + "]");
        }
    }

    public boolean downLoad() {
        FileOutputStream outStream = null;
        try {
            logger.info("   --  开始下载文件,[FileName: " + fileName + ", URL: " + url + "]");

            byte[] byteContent = HttpHelper.getURLContent(url);
            if (byteContent != null) {
                FileHelper.createNewFile(tempPath);
                outStream = new FileOutputStream(tempPath);
                outStream.write(byteContent);
                outStream.close();
                logger.info("   --  下载文件[" + fileName + "]成功,文件大小:" + (byteContent.length >> 10) + "Kb(" + byteContent.length + "Byte)");
                if (HQBaseConfig.DEPRESS_FILE_LIST.contains(fileName)) {
                    ZipHelper.decompress(tempPath, tempDirectory);
                    logger.info("   --  解压文件[" + fileName + "]成功");
                }
                return true;
            }
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }
}
