package moomoo.hgtp.grouptalk.session.base;

import moomoo.hgtp.grouptalk.session.SessionManager;

import java.util.HashSet;
import java.util.Set;

public class RoomInfo {

    private final String roomId;
    private final String roomName;
    private final String managerId;
    private final long createTime;
    private final HashSet<String> userGroupSet;

    public RoomInfo(String roomId, String roomName, String managerId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.managerId = managerId;
        this.createTime = System.currentTimeMillis();
        this.userGroupSet = new HashSet<>();
    }

    public String getRoomId() {return roomId;}

    public String getRoomName() {return roomName;}

    public String getManagerId() {return managerId;}

    public long getCreateTime() {return createTime;}

    public Set<String> getUserGroupSet() {
        HashSet<String> groupSet = new HashSet<>();
        groupSet.addAll(userGroupSet);
        groupSet.add(managerId);
        return groupSet;
    }

    public void addUserGroupSet(String userId) {
        if (userGroupSet.contains(userId)) {
            return;
        }
        UserInfo userInfo = SessionManager.getInstance().getUserInfo(userId);
        if (userInfo == null) {
            return;
        }

        userInfo.setRoomId(roomId);
        userGroupSet.add(userId);
    }

    public void removeUserGroupSet(String userId) {
        if (userGroupSet.contains(userId)) {
            UserInfo userInfo = SessionManager.getInstance().getUserInfo(userId);
            if (userInfo == null) {
                return;
            }

            userInfo.initRoomId();
            userGroupSet.remove(userId);
        }
    }

    public boolean isUserGroupSetEmpty() {
        return userGroupSet.isEmpty();
    }

    public int getUserGroupSetSize() {
        return userGroupSet.size() + 1;
    }

}
