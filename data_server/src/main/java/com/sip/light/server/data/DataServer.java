package com.sip.light.server.data;

import com.sip.light.server.data.impl.UserDataOnRam;
import com.sip.light.server.data.open.UserData;

public class DataServer {

    private static UserData userData = new UserDataOnRam();
    public static UserData getUserData(){
        return userData;
    }
}
