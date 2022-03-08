package com.cqx.common.utils.pcap;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.framer.FramingException;
import io.pkts.packet.IPv4Packet;
import io.pkts.packet.IPv6Packet;
import io.pkts.packet.Packet;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抓包文件解析工具，格式：cap、pcap
 *
 * @author chenqixu
 */
public class PcapUtil {

    public <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }

    public String parser(String filePath) throws IOException, FramingException {
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
//                    System.out.println(
//                            String.format(
//                                    "【%s】has IPv6，version：%s，has SIP：%s，isFragmented：%s，FragmentOffset：%s"
//                                            + "，getFlowLabel：%s"
//                                            + "，getHopLimit：%s"
//                                            + "，getHeaderLength：%s"
//                                            + "，getIdentification：%s"
//                                            + "，getTotalIPLength：%s"
//                                            + "，getTrafficClass：%s"
//                                    , atomicInteger.get()
//                                    , ipv6.getVersion()
//                                    , ipv6.hasProtocol(Protocol.SIP)
//                                    , ipv6.isFragmented()
//                                    , ipv6.getFragmentOffset()
//                                    , ipv6.getFlowLabel()
//                                    , ipv6.getHopLimit()
//                                    , ipv6.getHeaderLength()
//                                    , ipv6.getIdentification()
//                                    , ipv6.getTotalIPLength()
//                                    , ipv6.getTrafficClass()
//                            ));
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
//        System.out.println("======================");
//        System.out.println("map.size：" + sidBeanMap.size());
        AtomicBoolean isFirst = new AtomicBoolean(true);
        String leftIp = null;
        for (Map.Entry<Integer, SIDBean> entry : sidBeanMap.entrySet()) {
            SIDBean sidBean = entry.getValue();
            if (isFirst.getAndSet(false)) {
                leftIp = sidBean.getSourceIP();
            }
            sidBean.frame(leftIp);// 格式化成Sip消息对象
            sb.append(sidBean).append("\n");
//            System.out.println(sidBean);
        }
        return String.format("Title: %s\n%s", new File(filePath).getName(), sb.toString());
    }
}
