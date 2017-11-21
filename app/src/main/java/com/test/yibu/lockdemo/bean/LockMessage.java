package com.test.yibu.lockdemo.bean;

import java.io.Serializable;

/**
 * Created by zhengcf on 2017/11/20.
 */

public class LockMessage implements Serializable {

    int type;
    String mac;
    String password;
    String key;
    byte[] passwordByte;
    byte[] keyByte;

    public LockMessage(String mac, byte[] passwordByte, byte[] keyByte) {
        this.mac = mac;
        this.passwordByte = passwordByte;
        this.keyByte = keyByte;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getPasswordByte() {
        return passwordByte;
    }

    public void setPasswordByte(byte[] passwordByte) {
        this.passwordByte = passwordByte;
    }

    public byte[] getKeyByte() {
        return keyByte;
    }

    public void setKeyByte(byte[] keyByte) {
        this.keyByte = keyByte;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
