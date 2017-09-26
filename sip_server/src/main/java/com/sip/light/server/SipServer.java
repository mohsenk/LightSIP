package com.sip.light.server;

import com.sip.light.server.data.DataServer;
import com.sip.light.server.data.open.UserData;
import gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationWWWHelper;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.Properties;

public class SipServer implements SipListener{
    private SipStack mSipStack;
    private HeaderFactory mHeaderFactory;
    private String mPeerHostPort;
    private String mTransport;
    private AddressFactory mAddressFactory;
    private MessageFactory mMessageFactory;
    private ListeningPoint mUdpListeningPoint;
    private SipProvider mSipProvider;
    private String mIPAddress = "192.168.0.174";

    @Override
    public void processRequest(RequestEvent requestEvent) {
        System.out.println(requestEvent);
        if(requestEvent.getRequest().getMethod().equals(Request.REGISTER)){
            processRegister(requestEvent,requestEvent.getServerTransaction());
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        System.out.println(responseEvent);
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        System.out.println(timeoutEvent);
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        System.out.println(exceptionEvent);
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        System.out.println(transactionTerminatedEvent);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        System.out.println(dialogTerminatedEvent);
    }

    public void processRegister(RequestEvent requestEvent,
                                ServerTransaction serverTransactionId) {
        Request request = requestEvent.getRequest();
        ContactHeader contact = (ContactHeader) request.getHeader(ContactHeader.NAME);
        SipURI contactUri = (SipURI) contact.getAddress().getURI();
        FromHeader from = (FromHeader) request.getHeader(FromHeader.NAME);
        SipURI fromUri = (SipURI) from.getAddress().getURI();

//        DataServer.getUserData().setContact(fromUri.toString(),
//                new UserData.UserAddress(contactUri.getHost(),contactUri.getPort()));

        // Verify AUTHORIZATION !!!!!!!!!!!!!!!!


        try {
            DigestServerAuthenticationWWWHelper dsam = new DigestServerAuthenticationWWWHelper();

            if (!dsam.doAuthenticatePlainTextPassword(request, DataServer.getUserData().getPassword(fromUri.getUser()))) {
                Response challengeResponse = mMessageFactory.createResponse(
                        Response.UNAUTHORIZED, request);
                dsam.generateChallenge(mHeaderFactory, challengeResponse, "nist.gov");
                mSipProvider.getNewServerTransaction(request).sendResponse(challengeResponse);
                return;

            }

            Response response = mMessageFactory.createResponse(Response.OK, request);
            ServerTransaction serverTransaction = mSipProvider.getNewServerTransaction(request);
            serverTransaction.sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void init(){
        SipFactory sipFactory = null;
        mSipStack = null;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        Properties properties = new Properties();
        //properties.setProperty("javax.sip.OUTBOUND_PROXY", mPeerHostPort + "/"
        //        + mTransport);
        properties.setProperty("javax.sip.STACK_NAME", "SIP_SERVER");
        properties
                .setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "1048576");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "sip_server/out/log/debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "sip_server/out/log/server.txt");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "16");
        // Drop the client connection after we are done with the transaction.
        properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
                "false");

        properties.setProperty("javax.sip.IP_ADDRESS",mIPAddress);
        //properties.setProperty("javax.sip.ROUTER_PATH",mIPAddress);

        try {

            mSipStack = sipFactory.createSipStack(properties);
            System.out.println("createSipStack " + mSipStack);
        } catch (PeerUnavailableException e) {

            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(0);
        }
        try {
            mHeaderFactory = sipFactory.createHeaderFactory();
            mAddressFactory = sipFactory.createAddressFactory();
            mMessageFactory = sipFactory.createMessageFactory();
            mUdpListeningPoint = mSipStack.createListeningPoint("192.168.0.174",
                    5060, "udp");
            mSipProvider = mSipStack.createSipProvider(mUdpListeningPoint);

            mSipProvider.addSipListener(this);

        } catch (PeerUnavailableException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Creating Listener Points");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
