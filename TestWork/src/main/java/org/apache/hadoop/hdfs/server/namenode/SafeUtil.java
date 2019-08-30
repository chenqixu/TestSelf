package org.apache.hadoop.hdfs.server.namenode;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_PERMISSIONS_SUPERUSERGROUP_DEFAULT;
import static org.apache.hadoop.hdfs.DFSConfigKeys.DFS_PERMISSIONS_SUPERUSERGROUP_KEY;

/**
 * SafeUtil
 *
 * @author chenqixu
 */
public class SafeUtil {

    public static void main(String[] args) throws IOException {
        System.out.println(Server.getRemoteUser());
        System.out.println(UserGroupInformation.getCurrentUser());
        UserGroupInformation fsOwner = null;
        String fsOwnerShortUserName = null;
        String supergroup = null;
        fsOwner = UserGroupInformation.getCurrentUser();
        fsOwnerShortUserName = fsOwner.getShortUserName();
        Configuration conf = new Configuration();
        supergroup = conf.get(DFS_PERMISSIONS_SUPERUSERGROUP_KEY,
                DFS_PERMISSIONS_SUPERUSERGROUP_DEFAULT);
        System.out.println("fsOwnerShortUserName：" + fsOwnerShortUserName);
        System.out.println("supergroup：" + supergroup);
        FSPermissionChecker fsPc = new FSPermissionChecker(fsOwnerShortUserName, supergroup, NameNode.getRemoteUser());
        String path = "tmp.txt";
        FSDirectory dir = null;
        fsPc.checkPermission(path, dir, false, null, null, FsAction.WRITE, null,
                false, true);
    }

    public void test() throws IOException {
        org.apache.hadoop.hdfs.protocol.ClientProtocol a = null;
        a.getBlockLocations("", 0, 0);
    }
}
