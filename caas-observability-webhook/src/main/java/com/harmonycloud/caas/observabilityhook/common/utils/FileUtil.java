package com.harmonycloud.caas.observabilityhook.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

/**
 * @author dengyulong
 * @date 2020/06/05
 */
@Slf4j
public class FileUtil {

    /**
     * 读取文件内容
     */
    public static String readContent(String path) {
        StringBuilder content = new StringBuilder();
        try {
            File file = ResourceUtils.getFile(path);
            if (!file.exists()) {
                log.error("要读取的文件不存在：{}", path);
                return null;
            }
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                content.append(s);
            }
        } catch (IOException e) {
            log.error("文件读取异常：{}", path, e);
        }

        return content.toString();
    }

    public static void main(String[] args) {
        System.out.println(readContent("/Users/dengyulong/Desktop/token"));
    }

}
