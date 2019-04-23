package com.newland.bi.bigdata.net.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * IClientDeal
 *
 * @author chenqixu
 */
public abstract class IClientDeal<T> {
    public void initServer(Socket client) throws Exception {
        newReader(client.getInputStream());
        newWriter(client.getOutputStream());
    }

    public void initClient(Socket client) throws Exception {
        newWriter(client.getOutputStream());
        newReader(client.getInputStream());
    }

    public abstract void newReader(InputStream is) throws Exception;

    public abstract void newWriter(OutputStream os) throws Exception;

    protected abstract T read() throws Exception;

    protected abstract void write(T t) throws Exception;

    public void writeSingle(T t) throws Exception {
        check(t);
        write(t);
    }

    protected abstract void check(T t) throws Exception;

    public T writeAndRead(T t) throws Exception {
        check(t);
        write(t);
        return read();
    }

    public abstract void closeServer();

    public abstract void closeClient();

    protected void throwNullException(Object object, String msg) {
        if (object == null)
            throw new NullPointerException(msg);
    }
}
