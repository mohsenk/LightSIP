package com.sip.light.server.data.open;

public interface UserData {
    void setContact(String userUri, UserAddress userAddress);

    UserAddress getContact(String userUri);

    void addUser(String userUri,String password);

    void removeUser(String userUri);


    void updateUser(String userUri,String password);

    String getPassword(String userUri);


    class UserAddress{
        String ip;
        int port;

        public UserAddress(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return "ip=" + ip + " port=" + port;
        }
    }
}
