package com.sip.light.server.data.impl;

import com.sip.light.server.data.open.UserData;

public class UserDataOnJedis implements UserData {
    @Override
    public void setContact(String userUri, UserAddress userAddress) {
        System.out.println(userUri+"->"+userAddress);
    }

    @Override
    public UserAddress getContact(String userUri) {
        return null;
    }

    @Override
    public void addUser(String userUri, String password) {

    }

    @Override
    public void removeUser(String userUri) {

    }

    @Override
    public void updateUser(String userUri, String password) {

    }

    @Override
    public String getPassword(String userUri) {
        return null;
    }
}
