package moomoo.hgtp.client.session.base;

public class RoomInfo {

    private final String rooId;
    private final String managerId;

    private final long createTime;

    public RoomInfo(String rooId, String managerId) {
        this.rooId = rooId;
        this.managerId = managerId;
        this.createTime = System.currentTimeMillis();
    }

    public String getRooId() {return rooId;}

    public String getManagerId() {return managerId;}

    public long getCreateTime() {return createTime;}
}
