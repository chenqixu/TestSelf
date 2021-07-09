package com.cqx.bean;

/**
 * PartitionAssignmentState
 *
 * @author chenqixu
 */
public class PartitionAssignmentState {
    private String group;
    private Node coordinator;
    private String topic;
    private int partition;
    private long offset;
    private long lag;
    private String consumerId;
    private String host;
    private String clientId;
    private long logEndOffset;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Node getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Node coordinator) {
        this.coordinator = coordinator;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLag() {
        return lag;
    }

    public void setLag(long lag) {
        this.lag = lag;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getLogEndOffset() {
        return logEndOffset;
    }

    public void setLogEndOffset(long logEndOffset) {
        this.logEndOffset = logEndOffset;
    }

    public static class Node {
        public int id;
        public String idString;
        public String host;
        public int port;
        public String rack;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIdString() {
            return idString;
        }

        public void setIdString(String idString) {
            this.idString = idString;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getRack() {
            return rack;
        }

        public void setRack(String rack) {
            this.rack = rack;
        }
    }
}
