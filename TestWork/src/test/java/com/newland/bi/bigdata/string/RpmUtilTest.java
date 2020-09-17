package com.newland.bi.bigdata.string;

import org.junit.Test;

public class RpmUtilTest {

    private RpmUtil rpmUtil;

    @Test
    public void deal() {
        rpmUtil = new RpmUtil();
        rpmUtil.addRpms("python-IPy-0.75-6.el7.noarch.rpm\n" +
                "python-kitchen-1.1.1-5.el7.noarch.rpm\n" +
                "libaio-0.3.109-13.el7.x86_64.rpm\n" +
                "libxml2-python-2.9.1-6.el7_2.3.x86_64.rpm\n" +
                "libtool-ltdl-2.4.2-22.el7_3.x86_64.rpm\n" +
                "libnetfilter_queue-1.0.2-2.el7_2.x86_64.rpm\n" +
                "socat-1.7.3.2-2.el7.x86_64.rpm\n" +
                "checkpolicy-2.5-8.el7.x86_64.rpm\n" +
                "libselinux-2.5-14.1.el7.x86_64.rpm\n" +
                "libselinux-python-2.5-14.1.el7.x86_64.rpm\n" +
                "libselinux-utils-2.5-14.1.el7.x86_64.rpm\n" +
                "libsemanage-2.5-14.el7.x86_64.rpm\n" +
                "libsemanage-python-2.5-14.el7.x86_64.rpm\n" +
                "libsepol-2.5-10.el7.x86_64.rpm\n" +
                "setools-libs-3.3.8-4.el7.x86_64.rpm\n" +
                "audit-2.8.5-4.el7.x86_64.rpm\n" +
                "audit-libs-2.8.5-4.el7.x86_64.rpm\n" +
                "audit-libs-python-2.8.5-4.el7.x86_64.rpm\n" +
                "device-mapper-persistent-data-0.8.5-1.el7.x86_64.rpm\n" +
                "libcgroup-0.41-21.el7.x86_64.rpm\n" +
                "policycoreutils-2.5-33.el7.x86_64.rpm\n" +
                "policycoreutils-python-2.5-33.el7.x86_64.rpm\n" +
                "python-chardet-2.2.1-3.el7.noarch.rpm\n" +
                "yum-utils-1.1.31-52.el7.noarch.rpm\n" +
                "container-selinux-2.107-3.el7.noarch.rpm\n" +
                "548a0dcd865c16a50980420ddfa5fbccb8b59621179798e6dc905c9bf8af3b34-kubernetes-cni-0.7.5-0.x86_64.rpm\n" +
                "14bfe6e75a9efc8eca3f638eb22c7e2ce759c67f95b43b16fae4ebabde1549f3-cri-tools-1.13.0-0.x86_64.rpm\n" +
                "c1101e7903201b851394502c28830132a130290a9d496c89172a471c2f2f5a28-kubeadm-1.14.8-0.x86_64.rpm\n" +
                "2de91de7e1a27c52533426a65bb9dba3aa255a92844eaa22fb9801e8d5585a42-kubectl-1.14.8-0.x86_64.rpm\n" +
                "651677e32820964b6d269b50f519a369b1efb13bd143f64324a3c61b1179b34a-kubelet-1.14.8-0.x86_64.rpm\n" +
                "docker-ce-18.06.3.ce-3.el7.x86_64.rpm\n" +
                "device-mapper-1.02.158-2.el7_7.2.x86_64.rpm\n" +
                "conntrack-tools-1.4.4-5.el7_7.2.x86_64.rpm\n" +
                "device-mapper-event-1.02.158-2.el7_7.2.x86_64.rpm\n" +
                "device-mapper-event-libs-1.02.158-2.el7_7.2.x86_64.rpm\n" +
                "device-mapper-libs-1.02.158-2.el7_7.2.x86_64.rpm\n" +
                "libnetfilter_cthelper-1.0.0-10.el7_7.1.x86_64.rpm\n" +
                "libnetfilter_cttimeout-1.0.0-6.el7_7.1.x86_64.rpm\n" +
                "lvm2-2.02.185-2.el7_7.2.x86_64.rpm\n" +
                "lvm2-libs-2.02.185-2.el7_7.2.x86_64.rpm\n" +
                "selinux-policy-3.13.1-252.el7_7.6.noarch.rpm\n" +
                "selinux-policy-targeted-3.13.1-252.el7_7.6.noarch.rpm", "\n");
        rpmUtil.deal();
    }
}