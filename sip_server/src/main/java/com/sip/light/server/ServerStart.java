package com.sip.light.server;

public class ServerStart {
    public static void main(String[] args) {
        SipServer sipServer = new SipServer();
        sipServer.init();
    }
}
