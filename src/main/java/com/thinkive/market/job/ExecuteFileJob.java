package com.thinkive.market.job;

import com.thinkive.base.util.FileHelper;
import com.thinkive.base.util.security.MD5;
import com.thinkive.base.util.zip.ZipHelper;
import com.thinkive.market.DataManager;
import com.thinkive.market.HQBaseConfig;

import org.apache.log4j.Logger;

/**
 * 描述 : 处理单个文件的任务
 * 版权 : Copyright-(c) 2017
 * 公司 : Thinkive
 *
 * @author 王嵊俊
 * @version 2017-01-23 16:21
 */

public class ExecuteFileJob implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final String filePath;

    private final String fileName;

    private final String fileDirector;

    private final String fileTmpDirector;

    private final String fileTmpPath;

    private final MD5 md5Factory = new MD5();

    public ExecuteFileJob(String fileName, String fileDirector, String fileTempDirector) {
        this.fileName = fileName;
        this.fileDirector = fileDirector;
        this.fileTmpDirector = fileTempDirector;
        this.filePath = fileDirector + "/" + fileName;
        this.fileTmpPath = fileTempDirector + "/" + fileName;
    }

    @Override
    public void run() {
        if (fileName.endsWith(HQBaseConfig.MD5_SUFFIX)) {
            DataManager.completeJob();
            return;
        }

        try {
            copyFile(fileTmpPath, filePath);
            compreeFile(fileTmpPath);
            createFileMD5(fileTmpPath);
            DataManager.completeJob();
        } catch (Exception e) {
            logger.warn("   处理文件[" + fileName + "]失败", e);
            retry();
        }

    }


    public void deleteFile(String path) {
        while (FileHelper.exists(path) && !FileHelper.deleteFile(path)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public void retry() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        logger.warn("   --  重新处理文件[" + fileName + "]...");
        run();
    }

    /**
     * 生成压缩文件
     */
    public void compreeFile(String sourcePath) {

        if (fileName.endsWith(HQBaseConfig.COMPRESS_SUFFIX)) {
            return;
        }

        String compressPath = sourcePath + HQBaseConfig.COMPRESS_SUFFIX;
        deleteFile(compressPath);

        ZipHelper.compress(sourcePath, compressPath);

        if (FileHelper.exists(compressPath)) {
            logger.info("   --  压缩文件[" + sourcePath + "]成功");
            copyFile(fileTmpPath + HQBaseConfig.COMPRESS_SUFFIX, filePath + HQBaseConfig.COMPRESS_SUFFIX);
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            logger.info("   --  压缩文件出错，尝试重新压缩...");
            compreeFile(sourcePath);
        }
    }

    /**
     * 生成MD5
     */
    public void createFileMD5(String sourcePath) {

        String md5Path = sourcePath + HQBaseConfig.MD5_SUFFIX;
        deleteFile(md5Path);

        byte[] data = FileHelper.readFileToByteArray(sourcePath);
        String md5 = md5Factory.getMD5ofStr(new String(data));
        while (!FileHelper.writeToFile(md5Path, md5)) {
            FileHelper.deleteFile(md5Path);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        logger.info("   --  产生MD5文件[" + sourcePath + HQBaseConfig.MD5_SUFFIX + "]成功");
        copyFile(fileTmpPath + HQBaseConfig.MD5_SUFFIX, filePath + HQBaseConfig.MD5_SUFFIX);
    }


    /**
     * 复制
     */
    public void copyFile(String sourcePath, String targetPath) {

        //  -- 复制改名后的文件
        String tempFilenamePath = targetPath + HQBaseConfig.TEP_SUFFIX;
        do {
            deleteFile(tempFilenamePath);
        } while (!FileHelper.copyFile(sourcePath, tempFilenamePath));

        do {
            deleteFile(targetPath);
        } while (!FileHelper.renameTo(tempFilenamePath, targetPath));

        logger.info("   --  复制文件[" + targetPath + "]成功");
    }

}
