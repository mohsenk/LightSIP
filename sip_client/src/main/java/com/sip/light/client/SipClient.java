package com.sip.light.client;

import com.sun.jndi.toolkit.url.Uri;
import gov.nist.javax.sip.ListeningPointImpl;
import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.UserCredentials;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.TooManyListenersException;

public class SipClient implements SipListener {
    private SipStackExt mSipStack;
    private HeaderFactory mHeaderFactory;
    private String mPeerHostPort;
    private String mTransport;
    private AddressFactory mAddressFactory;
    private MessageFactory mMessageFactory;
    private ListeningPoint mUdpListeningPoint;
    private SipProvider mSipProvider;
    private int mUdpPort = 10001;
    private String mProtocol = "udp";
    private String mIPAddress = "192.168.0.174";
    private UserCredentials mUserCredentials;


    @Override
    public void processRequest(RequestEvent requestEvent) {
        System.out.println(requestEvent);
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        if(responseEvent.getResponse().getStatusCode() == Response.UNAUTHORIZED){
            AuthenticationHelper authenticationHelper =
                    mSipStack.getAuthenticationHelper((challengedTransaction, realm) -> mUserCredentials, mHeaderFactory);
            try {
                authenticationHelper.handleChallenge(responseEvent.getResponse(),responseEvent.getClientTransaction(),mSipProvider,2000).sendRequest();
            } catch (SipException e) {
                e.printStackTrace();
            }
        }else if(responseEvent.getResponse().getStatusCode() == Response.OK){
            System.out.println("Login success");
        }
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


    public void init() throws PeerUnavailableException, InvalidArgumentException, TransportNotSupportedException, ObjectInUseException, TooManyListenersException {
        SipFactory sipFactory = null;
        mSipStack = null;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "SIP_CLIENT");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "sip_client/out/log/debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "sip_client/out/log/server.txt");
        properties.setProperty("javax.sip.IP_ADDRESS",mIPAddress);
        mSipStack = (SipStackExt) sipFactory.createSipStack(properties);


        mHeaderFactory = sipFactory.createHeaderFactory();
        mAddressFactory = sipFactory.createAddressFactory();
        mMessageFactory = sipFactory.createMessageFactory();
        ListeningPoint lp = mSipStack.createListeningPoint(mIPAddress, mUdpPort, "udp");


        mSipProvider = mSipStack.createSipProvider(lp);
        mSipProvider.addSipListener(this);

    }

    public void sendRegister(UserCredentials userCredentials,String serverIP,int serverPort) throws SipException, ParseException, InvalidArgumentException {
        mUserCredentials = userCredentials;
        URI requestURI = mAddressFactory.createURI("sip:server@"+serverIP+":"+serverPort);
        CallIdHeader callId = mSipProvider.getNewCallId();
        CSeqHeader cSeq = mHeaderFactory.createCSeqHeader(1l, Request.REGISTER);
        FromHeader from = mHeaderFactory.createFromHeader(mAddressFactory.createAddress("sip:"+userCredentials.getUserName()+"@"+serverIP+":"+serverPort), "1234");
        ToHeader to = mHeaderFactory.createToHeader(mAddressFactory.createAddress("sip:server@"+serverIP+":"+serverPort), null);
        List via = Arrays.asList(((ListeningPointImpl)mSipProvider.getListeningPoint(mProtocol)).getViaHeader());
        MaxForwardsHeader maxForwards = mHeaderFactory.createMaxForwardsHeader(10);
        Request request = mMessageFactory.createRequest(requestURI, Request.REGISTER, callId, cSeq, from, to, via, maxForwards);

        ContactHeader contactHeader = mHeaderFactory.createContactHeader(mAddressFactory.createAddress("sip:"+userCredentials.getUserName()+"@"+mIPAddress+":"+mUdpPort));
        request.addHeader(contactHeader);

        ClientTransaction ctx = mSipProvider.getNewClientTransaction(request);
        ctx.sendRequest();
    }
}
