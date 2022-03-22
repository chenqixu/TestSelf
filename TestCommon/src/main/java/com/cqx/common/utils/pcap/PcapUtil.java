package com.cqx.common.utils.pcap;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.diameter.DiameterHeader;
import io.pkts.framer.DiameterFramer;
import io.pkts.framer.FramingException;
import io.pkts.packet.IPv4Packet;
import io.pkts.packet.IPv6Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.UDPPacket;
import io.pkts.packet.diameter.DiameterPacket;
import io.pkts.packet.sctp.SctpChunk;
import io.pkts.packet.sctp.SctpPacket;
import io.pkts.packet.sctp.impl.SctpDataChunkImpl;
import io.pkts.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抓包文件解析工具，格式：cap、pcap
 *
 * @author chenqixu
 */
public class PcapUtil {
    private static final Logger logger = LoggerFactory.getLogger(PcapUtil.class);
    private static final DiameterFramer diameterFramer = new DiameterFramer();

    public <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }

    public String parserSIP(String filePath) throws IOException, FramingException {
        StringBuilder sb = new StringBuilder();
        AtomicInteger atomicInteger = new AtomicInteger(1);

        // Step 1 - obtain a new Pcap instance by supplying an InputStream that points
        //          to a source that contains your captured traffic. Typically you may
        //          have stored that traffic in a file so there are a few convenience
        //          methods for those cases, such as just supplying the name of the
        //          file as shown below.
        final Pcap pcap = Pcap.openStream(filePath);
        final LinkedHashMap<Integer, SIDBean> sidBeanMap = new LinkedHashMap<>();

        // Step 2 - Once you have obtained an instance, you want to start
        //          looping over the content of the pcap. Do this by calling
        //          the loop function and supply a PacketHandler, which is a
        //          simple interface with only a single method - nextPacket
        pcap.loop(new PacketHandler() {

            @Override
            public boolean nextPacket(final Packet packet) throws IOException {
                if (packet.hasProtocol(Protocol.UDP) && packet.hasProtocol(Protocol.IPv6)) {
                    UDPPacket udp = (UDPPacket) packet.getPacket(Protocol.UDP);
                    IPv6Packet ipv6 = (IPv6Packet) packet.getPacket(Protocol.IPv6);
                    if (ipv6.getFragmentOffset() == 0) {// 有分片，第一个分片
                        sidBeanMap.put(atomicInteger.get(), new SIDBean(
                                ipv6.getSourceIP()
                                , ipv6.getDestinationIP()
                                , ipv6.getArrivalTime()
                                , udp.getPayload().getArray()));
                    } else if (ipv6.isFragmented()) {// 有分片，之后的分片
                        Map.Entry<Integer, SIDBean> lastSidBeanMap = getTail(sidBeanMap);
                        SIDBean lastSidBean = lastSidBeanMap.getValue();
                        lastSidBean.appendPayload(udp.getPayload());
                    } else {// 无分片
                        sidBeanMap.put(atomicInteger.get(), new SIDBean(
                                ipv6.getSourceIP()
                                , ipv6.getDestinationIP()
                                , ipv6.getArrivalTime()
                                , udp.getPayload().getArray()));
                    }
                    atomicInteger.incrementAndGet();// 序号自增
                } else if (packet.hasProtocol(Protocol.UDP) && packet.hasProtocol(Protocol.IPv4)) {
                    IPv4Packet ipv4 = (IPv4Packet) packet.getPacket(Protocol.IPv4);
                    UDPPacket udp = (UDPPacket) packet.getPacket(Protocol.UDP);
                    sidBeanMap.put(atomicInteger.get(), new SIDBean(
                            ipv4.getSourceIP()
                            , ipv4.getDestinationIP()
                            , ipv4.getArrivalTime()
                            , udp.getPayload().getArray()));
                    atomicInteger.incrementAndGet();// 序号自增
                }
                return true;
            }
        });
        AtomicBoolean isFirst = new AtomicBoolean(true);
        String leftIp = null;
        for (Map.Entry<Integer, SIDBean> entry : sidBeanMap.entrySet()) {
            SIDBean sidBean = entry.getValue();
            if (isFirst.getAndSet(false)) {
                leftIp = sidBean.getSourceIP();
            }
            sidBean.frame(leftIp);// 格式化成Sip消息对象
            sb.append(sidBean).append("\n");
        }
        return String.format("Title: %s\n%s", new File(filePath).getName(), sb.toString());
    }

    public String parser(String filePath) throws IOException, FramingException {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        final Pcap pcap = Pcap.openStream(filePath);
        pcap.loop(new PacketHandler() {

            @Override
            public boolean nextPacket(final Packet packet) throws IOException {
                logger.info("【{}】hasProtocol{}"
                        , atomicInteger.getAndIncrement()
                        , packet.hasProtocol(Protocol.SCTP));
                return true;
            }
        });
        return "";
    }

    public String parserDiameter(String filePath) throws IOException, FramingException {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        final LinkedHashMap<Integer, Buffer> diameterMap = new LinkedHashMap<>();
        final Pcap pcap = Pcap.openStream(filePath);
        pcap.loop(new PacketHandler() {

            @Override
            public boolean nextPacket(final Packet packet) throws IOException {
                if (packet.hasProtocol(Protocol.SCTP)) {
                    SctpPacket sctpPacket = (SctpPacket) packet.getPacket(Protocol.SCTP);
                    List<SctpChunk> sctpChunkList = sctpPacket.getChunks();
                    for (SctpChunk sctpChunk : sctpChunkList) {
//                        logger.info("getPadding：{}", sctpChunk.getPadding());
//                        logger.info("getType：{}", sctpChunk.getType());
//                        logger.info("getValue：{}", sctpChunk.getValue());
//                        logger.info("getFlags：{}", sctpChunk.getFlags());
//                        logger.info("getHeader：{}", sctpChunk.getHeader());
//                        logger.info("getLength：{}", sctpChunk.getLength());
//                        logger.info("getValueLength：{}", sctpChunk.getValueLength());
                        switch (sctpChunk.getType()) {
                            case DATA:
                                SctpDataChunkImpl sctpDataChunk = (SctpDataChunkImpl) sctpChunk;
                                Buffer realData = null;
                                Buffer userData = sctpDataChunk.getUserData();
                                // 通过Flags判断数据是否有做分组，参考https://datatracker.ietf.org/doc/html/rfc4960#section-6.9
                                // Flags占一个byte
                                // 前4位全F
                                // 后4位说明
                                // I-Bit：Possibly delay SACK，和DATA分组无关
                                // U-Bit：Ordered delivery，按顺序下发
                                // B-Bit：1表示第一个segment，0表示中间segment
                                // E-Bit：1表示最后一个segment，0表示不是最后一个segment
                                // 所以转成10进制，开始就是2，中间就是0，结束就是1
                                int flags = (int) sctpChunk.getFlags();
                                int ssn = sctpDataChunk.getStreamSequenceNumber();
                                logger.info("flags：{}，ssn：{}", flags, ssn);
                                if (flags == 2) {// 分包开始
                                    diameterMap.put(ssn, userData);
                                } else if (flags == 0) {// 分包中间
                                    Buffer segment = diameterMap.get(ssn);
                                    diameterMap.put(ssn, Buffers.wrap(segment, userData));
                                } else if (flags == 1) {// 分包结束
                                    Buffer segment = diameterMap.get(ssn);
                                    realData = Buffers.wrap(segment, userData);
                                } else if (flags == 3) {// 不分包
                                    realData = userData;
                                }
                                // 判断是否是Diameter协议
                                if (realData != null && diameterFramer.accept(realData)) {
                                    DiameterPacket diameterPacket = diameterFramer.frame(sctpPacket, realData);
                                    DiameterHeader diameterHeader = diameterPacket.getHeader();
                                    logger.info("Avps.size: {}" +
                                                    "，CommandCode: {}，isError: {}" +
                                                    "，isPossiblyRetransmission: {}，isProxiable: {}，isRequest: {}，isResponse: {}"
                                            , diameterPacket.getAllAvps().size()
                                            , diameterHeader.getCommandCode()
                                            , diameterHeader.isError()
                                            , diameterHeader.isPossiblyRetransmission()
                                            , diameterHeader.isProxiable()
                                            , diameterHeader.isRequest()
                                            , diameterHeader.isResponse());
//                                    for (FramedAvp framedAvp : diameterPacket.getAllAvps()) {
//                                        logger.info("avp：{}", framedAvp);
//                                    }
                                }
                                break;
                            default:
                                logger.warn("类型 {}，不做处理。", sctpChunk.getType());
                                break;
                        }
                    }
                }
                return true;
            }
        });
        return "";
    }
}
