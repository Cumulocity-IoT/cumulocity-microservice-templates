package com.c8y.ms.templates.realtime.subscriber;

public class OperationNotification {
    private static final String UPDATE_ACTION = "UPDATE";
    private Object data;
    private String realtimeAction;
    public OperationNotification() {
    }
    public OperationNotification(Object data, String realtimeAction) {
        this.data = data;
        this.realtimeAction = realtimeAction;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public String getRealtimeAction() {
        return realtimeAction;
    }
    public void setRealtimeAction(String realtimeAction) {
        this.realtimeAction = realtimeAction;
    }
    public boolean isUpdateNotification() {
        return UPDATE_ACTION.equals(realtimeAction);
    }
}