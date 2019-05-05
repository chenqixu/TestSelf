package com.newland.bi.bigdata.ftp;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;


/**
 * @description 没有图形用户界面， 只编写了连接，获取当前目录，上传文件功能。
 */
public class SimpleFTP {

    private static boolean debug = true;
    private Socket socket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private String user = "edc_base";
    private String pass = "AkDXk0&d";

    public SimpleFTP() {
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 4) {
            String host = args[0];
            String user = args[1];
            String pass = args[2];
            String path = args[3];
            SimpleFTP ftp = new SimpleFTP();
            ftp.connect(host, 21, user, pass);
            ftp.list(path);
        } else {
            System.out.println("no enouth args. args：" + args + "，args.length：" + args.length);
        }
    }

    /**
     * connect to the ftp server
     *
     * @param host
     * @throws Exception
     */
    public synchronized void connect(String host) throws Exception {
        connect(host, 21, user, pass);
    }

    public synchronized void connect(String host, int port, String user, String pass) throws Exception {
        if (socket != null) {
            throw new Exception("already connect!");
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String response = readLine();
        if (!response.startsWith("220")) {
            throw new Exception("unknow response after connect!");
        }
        sendLine("USER " + user);
        response = readLine();
        if (!response.startsWith("331")) {
            throw new Exception("unknow response after send user");
        }
        sendLine("PASS " + pass);
        response = readLine();
        if (!response.startsWith("230")) {
            throw new Exception("unknow response after send pass");
        }
    }

    private void sendLine(String line) throws Exception {
        if (socket == null) {
            throw new Exception("not connect!");
        }
        writer.write(line + "\r\n");
        writer.flush();
        if (debug) {
            System.out.println(">" + line);
        }
    }

    private String readerReadLine() throws IOException {
        String line = reader.readLine();
        if (debug) {
            System.out.println("<" + line);
        }
        return line;
    }

    private String readLine() throws IOException {
        return readLine(reader);
    }

    private String readLine(BufferedReader reader) throws IOException {
        StringBuffer var1 = new StringBuffer(32);
        int var3 = -1;
        int var4;
        String line;
        while (true) {
            String var5;
            while (true) {
                int var2;
                while ((var2 = reader.read()) != -1) {
                    if (var2 == 13 && (var2 = reader.read()) != 10) {
                        var1.append('\r');
                    }
                    var1.append((char) var2);
                    if (var2 == 10) {
                        break;
                    }
                }
                var5 = var1.toString();
                if (debug && var5.length() > 0) {
                    System.out.print("<" + var5);
                }
                var1.setLength(0);
                if (var5.length() == 0) {
                    var4 = -1;
                    break;
                }
                try {
                    var4 = Integer.parseInt(var5.substring(0, 3));
                    break;
                } catch (NumberFormatException var7) {
                    var4 = -1;
                    break;
                } catch (StringIndexOutOfBoundsException var8) {
                }
            }
            line = var5;
            if (var3 != -1) {
                if (var4 == var3 && (var5.length() < 4 || var5.charAt(3) != '-')) {
                    boolean var9 = true;
                    break;
                }
            } else {
                if (var5.length() < 4 || var5.charAt(3) != '-') {
                    break;
                }
                var3 = var4;
            }
        }
        return line;
    }

    /**
     * get the working directory of the FTP server
     *
     * @return
     * @throws Exception
     */
    public synchronized String pwd() throws Exception {
        sendLine("PWD");
        String dir = null;
        String response = readLine();
        if (response.startsWith("257")) {
            int firstQuote = response.indexOf("/");
            int secondQuote = response.indexOf("/", firstQuote + 1);
            if (secondQuote > 0) {
                dir = response.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }

    public synchronized void cwd(String path) throws Exception {
        sendLine("CWD " + path);
        readLine();
    }

    public synchronized String list(String path) throws Exception {
        sendLine("PASV");
        String response = readLine();
        if (!response.startsWith("227")) {
            throw new Exception("not request passive mode!");
        }
        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenzier = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenzier.nextToken() + "." + tokenzier.nextToken() + "."
                        + tokenzier.nextToken() + "." + tokenzier.nextToken();
                port = Integer.parseInt(tokenzier.nextToken()) * 256 + Integer.parseInt(tokenzier.nextToken());
                ;
            } catch (Exception e) {
                throw new Exception("bad data link after list!");
            }
        }
        PASVThread pasvThread = new PASVThread(ip, port);
        sendLine("LIST " + path);
        readLine();
        pasvThread.readLine();
        pasvThread.close();
        return null;
    }

    /**
     * send a file to ftp server
     *
     * @param file
     * @return
     * @throws Exception
     */
    public synchronized boolean stor(File file) throws Exception {
        if (!file.isDirectory()) {
            throw new Exception("cannot upload a directory!");
        }
        String fileName = file.getName();
        return upload(new FileInputStream(file), fileName);
    }

    public synchronized boolean upload(InputStream inputStream, String fileName) throws Exception {
        BufferedInputStream input = new BufferedInputStream(inputStream);
        sendLine("PASV");
        String response = readLine();
        if (!response.startsWith("227")) {
            throw new Exception("not request passive mode!");
        }
        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenzier = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenzier.nextToken() + "." + tokenzier.nextToken() + "."
                        + tokenzier.nextToken() + "." + tokenzier.nextToken();
                port = Integer.parseInt(tokenzier.nextToken()) * 256 + Integer.parseInt(tokenzier.nextToken());
                ;
            } catch (Exception e) {
                throw new Exception("bad data link after upload!");
            }
        }
        sendLine("STOR " + fileName);
        Socket dataSocket = new Socket(ip, port);
        response = readLine();
        if (!response.startsWith("150")) {
            throw new Exception("not allowed to send the file!");
        }
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();
        response = readLine();
        return response.startsWith("226");
    }

    class PASVThread {

        private Socket socket = null;
        private BufferedReader reader = null;
        private BufferedWriter writer = null;

        public PASVThread(String host, int port) throws IOException {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        private void sendLine(String line) throws Exception {
            if (socket == null) {
                throw new Exception("not connect!");
            }
            writer.write(line + "\r\n");
            writer.flush();
            if (debug) {
                System.out.println(">" + line);
            }
        }

        private void readLine() throws IOException {
            SimpleFTP.this.readLine(reader);
        }

        public void close() {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
