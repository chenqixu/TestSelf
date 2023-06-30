package com.cqx.common.utils.doc;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.Closeable;
import java.io.IOException;

/**
 * WordDocumentBean
 *
 * @author chenqixu
 */
public class WordDocumentBean implements Closeable {
    private HWPFDocument hwDocument = null;
    private XWPFDocument xwDocument = null;

    public WordDocumentBean(HWPFDocument hwDocument) {
        this.hwDocument = hwDocument;
    }

    public WordDocumentBean(XWPFDocument xwDocument) {
        this.xwDocument = xwDocument;
    }

    public HWPFDocument getHwDocument() {
        return hwDocument;
    }

    public void setHwDocument(HWPFDocument hwDocument) {
        this.hwDocument = hwDocument;
    }

    public XWPFDocument getXwDocument() {
        return xwDocument;
    }

    public void setXwDocument(XWPFDocument xwDocument) {
        this.xwDocument = xwDocument;
    }

    @Override
    public void close() throws IOException {
        if (hwDocument != null) hwDocument.close();
        if (xwDocument != null) xwDocument.close();
    }
}
