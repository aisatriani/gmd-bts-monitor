/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ais.gmdapp.bts.items;

/**
 *
 * @author Asus
 */
public class Radio {
    
    String deviceName;
    String ip;
    String ssid;
    String location;

    public Radio(String deviceName, String ip, String ssid, String location) {
        this.deviceName = deviceName;
        this.ip = ip;
        this.ssid = ssid;
        this.location = location;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    
    
}
