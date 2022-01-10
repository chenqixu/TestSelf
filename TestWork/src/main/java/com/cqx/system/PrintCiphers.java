package com.cqx.system;

import javax.net.ssl.SSLServerSocketFactory;
import java.util.Map;
import java.util.TreeMap;

/**
 * 打印jdk支持的加密套件策略
 *
 * @author chenqixu
 */
public class PrintCiphers {

    public static void main(String[] args) {
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        TreeMap<String, Boolean> ciphers = new TreeMap<>();
        for (String cipher : ssf.getSupportedCipherSuites())
            ciphers.put(cipher, Boolean.FALSE);
        for (String cipher : ssf.getDefaultCipherSuites())
            ciphers.put(cipher, Boolean.TRUE);

        System.out.println("Default Cipher");
        for (Map.Entry<String, Boolean> cipher : ciphers.entrySet())
            System.out.printf("   %-5s%s%n", (cipher.getValue() ? '*' : ' '), cipher.getKey());
    }
}
