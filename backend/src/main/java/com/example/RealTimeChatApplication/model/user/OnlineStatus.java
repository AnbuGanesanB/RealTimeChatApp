package com.example.RealTimeChatApplication.model.user;

public enum OnlineStatus {
    ONLINE(0),
    DO_NOT_DISTURB(1),
    AWAY(2),
    OFFLINE(3);



    /*ONLINE(0),
    OFFLINE(1),
    DO_NOT_DISTURB(2),
    AWAY(3);*/

    private final int index;

    OnlineStatus(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static OnlineStatus fromIndex(int index) {
        for (OnlineStatus status : values()) {
            if (status.getIndex() == index) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid index for OnlineStatus: " + index);
    }
}
