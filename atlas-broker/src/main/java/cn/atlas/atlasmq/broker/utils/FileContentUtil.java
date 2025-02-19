package cn.atlas.atlasmq.broker.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 简化版本的文件读取工具
 */
public class FileContentUtil {
    public static String readFromFile(String path) {
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            StringBuffer stb = new StringBuffer();
            while (in.ready()) {
                stb.append(in.readLine());
            }
            return stb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void overWriteToFile(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
