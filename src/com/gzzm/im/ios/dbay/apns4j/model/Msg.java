package com.gzzm.im.ios.dbay.apns4j.model;

/**
 * @author sjy
 * @date 2017/3/23
 */
public class Msg {
    private String sound = "default";
    private String alert;
    private Integer badge;

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public Integer getBadge() {
        return badge;
    }

    public void setBadge(Integer badge) {
        this.badge = badge;
    }
}
