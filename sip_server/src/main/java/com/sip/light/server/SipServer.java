package com.sip.light.server;

import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
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

    @Override
    public void processRequest(RequestEvent requestEvent) {

    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }


    public void init(){
        SipFactory sipFactory = null;
        mSipStack = null;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        Properties properties = new Properties();
        properties.setProperty("javax.sip.OUTBOUND_PROXY", mPeerHostPort + "/"
                + mTransport);
        properties.setProperty("javax.sip.STACK_NAME", "shootistAuth");
        properties
                .setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "1048576");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "shootistAuthdebug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "shootistAuthlog.txt");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "16");
        // Drop the client connection after we are done with the transaction.
        properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
                "false");

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
            mUdpListeningPoint = mSipStack.createListeningPoint("127.0.0.1",
                    5060, "udp");
            mSipProvider = mSipStack.createSipProvider(mUdpListeningPoint);

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
