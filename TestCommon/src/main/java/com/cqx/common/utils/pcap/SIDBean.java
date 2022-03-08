package com.cqx.common.utils.pcap;

import com.cqx.common.utils.system.TimeUtil;
import io.pkts.buffer.Buffer;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.impl.ImmutableSipResponse;

import java.io.IOException;

/**
 * SIDBean
 *
 * @author chenqixu
 */
public class SIDBean {
    private String sourceIP;
    private String destinationIP;
    private String arrivalTime;
    private String method;
    private StringBuffer payload = new StringBuffer();
    private SipMessage sipMessage;
    private int status = -1;
    private boolean isRequest;
    private boolean isResponse;
    private boolean isLeft;

    public SIDBean() {
    }

    public SIDBean(String sourceIP, String destinationIP, long arrivalTime, byte[] content) {
        this(sourceIP, destinationIP, arrivalTime, new String(content));
    }

    public SIDBean(String sourceIP, String destinationIP, long arrivalTime, String content) {
        this.sourceIP = sourceIP.replaceAll(":", ";");
        this.destinationIP = destinationIP.replaceAll(":", ";");
        int timeLen = String.valueOf(arrivalTime).length();
        if (timeLen == 16) {
            this.arrivalTime = TimeUtil.formatTime(arrivalTime / 1000, "yyyy-MM-dd HH:mm:ss.SSS")
                    + (arrivalTime % 1000);
        } else if (timeLen == 13) {
            this.arrivalTime = TimeUtil.formatTime(arrivalTime, "yyyy-MM-dd HH:mm:ss.SSS");
        }
        appendPayload(content);
    }

    @Override
    public String toString() {
        return String.format("Note %s of %s: %s\n%s->%s: %s"
                , isLeft() ? "left" : "right"
                , getSourceIP()
                , getArrivalTime()
                , getSourceIP()
                , getDestinationIP()
                , getStatus() == -1 ? getMethod() : getStatus() + " " + getMethod()
        );
    }

    public void frame(String sourceIP) throws IOException {
        if (sourceIP.replaceAll(":", ";").equals(getSourceIP())) {
            isLeft = true;
        }
        sipMessage = SipMessage.frame(getPayload());
        Buffer m;
        if (sipMessage instanceof ImmutableSipResponse) {
            isResponse = true;
            ImmutableSipResponse response = (ImmutableSipResponse) sipMessage;
            setStatus(response.getStatus());
            m = response.getReasonPhrase();
        } else {
            isRequest = true;
            m = sipMessage.getMethod();
        }
        setMethod(new String(m.getArray()));
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPayload() {
        return payload.toString();
    }

    public void appendPayload(Object content) {
        this.payload.append(content);
    }

    public SipMessage getSipMessage() {
        return sipMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public boolean isLeft() {
        return isLeft;
    }
}
