package com.paulvinh.send_up;

public class ListItem {

    private int bulleId;
    private String sms;
    private int imageId;
    private int supprId;

    public ListItem(String sms, int imageId, int supprId) {
        this.sms = sms;
        this.imageId = imageId;
        this.supprId = supprId;
        this.bulleId = bulleId;
    }

    public int getBulleId() {
        return bulleId;
    }

    public void setBulleId(int bulleId) {
        this.bulleId = bulleId;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getSupprId() {
        return supprId;
    }

    public void setSupprId(int supprId) {
        this.supprId = supprId;
    }

    @Override
    public String toString() {
        return this.sms;
    }

}
