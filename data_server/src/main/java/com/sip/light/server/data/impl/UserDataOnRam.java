package com.sip.light.server.data.impl;

import com.sip.light.server.data.open.UserData;

import java.util.HashMap;

public class UserDataOnRam implements UserData {
    private HashMap<String,UserAddress> userAddressHashMap;
    private HashMap<String,String> userPasswordHashMap;

    public UserDataOnRam() {
        userAddressHashMap = new HashMap<>();
        userPasswordHashMap = new HashMap<>();
        userPasswordHashMap.put("yyl","123456");
    }

    @Override
    public void setContact(String userUri, UserAddress userAddress) {
        userAddressHashMap.put(userUri,userAddress);
    }

    @Override
    public UserAddress getContact(String userUri) {
        return userAddressHashMap.get(userUri);
    }

    @Override
    public void addUser(String userUri, String password) {
        userPasswordHashMap.put(userUri,password);
    }

    @Override
    public void removeUser(String userUri) {
        userPasswordHashMap.remove(userUri);
    }

    @Override
    public void updateUser(String userUri, String password) {
        userPasswordHashMap.put(userUri,password);
    }

    @Override
    public String getPassword(String userUri) {
        return userPasswordHashMap.get(userUri);
    }
}
