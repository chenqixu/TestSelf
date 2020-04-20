package com.cqx.distributed.scan;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

/**
 * ScanFTPFileFilter
 *
 * @author chenqixu
 */
public class ScanFTPFileFilter implements FTPFileFilter {
    @Override
    public boolean accept(FTPFile file) {
        return true;
    }
}
