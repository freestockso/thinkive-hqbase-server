package com.thinkive.market;

import com.thinkive.base.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述 : 下载应用配置
 * 版权 : Copyright-(c) 2017
 * 公司 : Thinkive
 *
 * @author 王嵊俊
 * @version 2017-01-23 17:31
 */
public class HQBaseConfig {

    public final static String COMPRESS_SUFFIX = ".zip";
    public final static String MD5_SUFFIX = ".md5";
    public final static String TEP_SUFFIX = ".tmp";

    public final static String TEMP_FILE_DIRECTORY = "/temp";

    public final static String FILE_NAMES[] = {"block.dat", "block.dbf", "full.PWR", "full.FIN", "full.ABK", "incABK"};

    public final static String FILE_PATH = new File(Configuration.getString("data.store")).getPath();

    public final static String FILE_PATH_TEMP = FILE_PATH + TEMP_FILE_DIRECTORY;

    public final static List<String> DEPRESS_FILE_LIST = getDepressFileList();

    public final static Map<String, String> FILE_URL_MAP = getFileUrlMap();

    public static Map<String, String> getFileUrlMap() {
        Map<String, String> urlMap = new HashMap<String, String>();

        String urlConfigPrefix = "DownloadURL.";

        Map<String, String> configMap = Configuration.getItems();

        for (String key : configMap.keySet()) {
            if (key.startsWith("DownloadURL.")) {
                urlMap.put(key.substring(urlConfigPrefix.length()), configMap.get(key));
            }
        }

        return urlMap;
    }

    public static List<String> getDepressFileList() {
        String depressFileStr = Configuration.getString("DownloadFileConfig.NeedDepressFile");
        String depressFiles[] = depressFileStr.split("\\|");
        return Arrays.asList(depressFiles);
    }

}
