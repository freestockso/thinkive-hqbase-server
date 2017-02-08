package com.thinkive.market.job;

import com.thinkive.base.util.FileHelper;
import com.thinkive.base.util.security.MD5;
import com.thinkive.base.util.zip.ZipHelper;
import com.thinkive.market.DataManager;
import com.thinkive.market.HQBaseConfig;

import org.apache.log4j.Logger;

/**
 * ���� : �������ļ�������
 * ��Ȩ : Copyright-(c) 2017
 * ��˾ : Thinkive
 *
 * @author ���ӿ�
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
            logger.warn("   �����ļ�[" + fileName + "]ʧ��", e);
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
        logger.warn("   --  ���´����ļ�[" + fileName + "]...");
        run();
    }

    /**
     * ����ѹ���ļ�
     */
    public void compreeFile(String sourcePath) {

        if (fileName.endsWith(HQBaseConfig.COMPRESS_SUFFIX)) {
            return;
        }

        String compressPath = sourcePath + HQBaseConfig.COMPRESS_SUFFIX;
        deleteFile(compressPath);

        ZipHelper.compress(sourcePath, compressPath);

        if (FileHelper.exists(compressPath)) {
            logger.info("   --  ѹ���ļ�[" + sourcePath + "]�ɹ�");
            copyFile(fileTmpPath + HQBaseConfig.COMPRESS_SUFFIX, filePath + HQBaseConfig.COMPRESS_SUFFIX);
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            logger.info("   --  ѹ���ļ�������������ѹ��...");
            compreeFile(sourcePath);
        }
    }

    /**
     * ����MD5
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
        logger.info("   --  ����MD5�ļ�[" + sourcePath + HQBaseConfig.MD5_SUFFIX + "]�ɹ�");
        copyFile(fileTmpPath + HQBaseConfig.MD5_SUFFIX, filePath + HQBaseConfig.MD5_SUFFIX);
    }


    /**
     * ����
     */
    public void copyFile(String sourcePath, String targetPath) {

        //  -- ���Ƹ�������ļ�
        String tempFilenamePath = targetPath + HQBaseConfig.TEP_SUFFIX;
        do {
            deleteFile(tempFilenamePath);
        } while (!FileHelper.copyFile(sourcePath, tempFilenamePath));

        do {
            deleteFile(targetPath);
        } while (!FileHelper.renameTo(tempFilenamePath, targetPath));

        logger.info("   --  �����ļ�[" + targetPath + "]�ɹ�");
    }

}
