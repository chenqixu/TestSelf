package com.newland.bi.bigdata.file;

import com.cqx.common.utils.file.FileMangerCenter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * MyFileReader
 *
 * @author chenqixu
 */
public class MyFileReader {
    private Scanner scanner;
    private FileMangerCenter fileMangerCenter;

    public MyFileReader(String file_name) throws IOException {
        scanner = new Scanner(new File(file_name));
        scanner.useDelimiter(".\n");//a-zA-Z0-9\u4e00-\u9fa5
        fileMangerCenter = new FileMangerCenter(file_name);
        fileMangerCenter.initReader();
    }

    public void setLineSplit(String lineSplit) {

    }

    public String readLine() throws IOException {
//        return fileMangerCenter.readByte();
//        return fileMangerCenter.readLine();
        if (scanner.hasNext()) return scanner.next();
//        if (scanner.hasNextLine()) return scanner.nextLine();
        return null;
    }

    public void close() throws IOException {
        if (scanner != null) scanner.close();
        if (fileMangerCenter != null) fileMangerCenter.close();
    }
}
