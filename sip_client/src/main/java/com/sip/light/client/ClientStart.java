package com.sip.light.client;

import gov.nist.javax.sip.clientauthutils.UserCredentials;

import javax.sip.*;
import java.text.ParseException;
import java.util.TooManyListenersException;

public class ClientStart {

    public static void main(String[] args) {
        SipClient sipClient = new SipClient();
        try {
            sipClient.init();

            Thread.sleep(1000);

            sipClient.sendRegister(new UserCredentials() {
                @Override
                public String getUserName() {
                    return "yyl";
                }

                @Override
                public String getPassword() {
                    return "123456";
                }

                @Override
                public String getSipDomain() {
                    return "nist.gov";
                }
            }, "192.168.0.174", 5060);
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (TransportNotSupportedException e) {
            e.printStackTrace();
        } catch (ObjectInUseException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }
}
