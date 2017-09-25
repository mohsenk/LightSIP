package test.tck.factory;
import java.text.ParseException;

import javax.sip.address.*;

import junit.framework.*;
import test.tck.*;

public class AddressFactoryTest extends FactoryTestHarness {

    public AddressFactoryTest() {
        super("AddressFactoryTest");
    }

    protected URI createTiURI(String uriString) {
        URI uri = null;
        try {
            uri = tiAddressFactory.createURI(uriString);
            assertTrue(uri != null);
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        }
        return uri;
    }

    protected URI createRiURI(String uriString) {
        URI uri = null;
        try {
            uri = riAddressFactory.createURI(uriString);
        } catch (Exception ex) {
            throw new TckInternalError(ex.getMessage());
        }
        return uri;
    }

    protected SipURI createTiSipURI(
        String name,
        String address) {
        SipURI sipUri = null;
        try {
            sipUri = tiAddressFactory.createSipURI(name, address);
            assertNotNull(sipUri);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TiUnexpectedError(ex.getMessage());
        }
        return sipUri;
    }

    protected SipURI createRiSipURI(
        String name,
        String address) {
        SipURI sipUri = null;
        try {
            sipUri = riAddressFactory.createSipURI(name, address);
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        }
        return sipUri;
    }

    protected TelURL createTiTelURL(String phoneNumber) {
        TelURL telUrl = null;
        try {
            telUrl = tiAddressFactory.createTelURL(phoneNumber);
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        }
        return telUrl;
    }

    protected TelURL createRiTelURLFromTiTelURL(
        TelURL tiTelURL) {
        TelURL telUrl = null;
        try {
            // The API has a bug here - there is no way to retrieve the
            // phone-context parameter. This will be fixed in the next release.
            //int start = tiTelURL.toString().indexOf(':');
            //String phoneNumber = tiTelURL.toString().substring(start+1).trim();
            // String phoneNumber = tiTelURL.getPhoneContext();// JvB: wrong
            String phoneNumber = tiTelURL.getPhoneNumber();
            telUrl = riAddressFactory.createTelURL(phoneNumber);
            telUrl.setGlobal(tiTelURL.isGlobal());
            // JvB: set to 'null' should remove it, fixed in impl
            telUrl.setPhoneContext( tiTelURL.getPhoneContext() );
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TiUnexpectedError(ex.getMessage());
        }
        return telUrl;
    }

    protected TelURL createRiTelURL(String phoneNumber) {
        TelURL telUrl = null;
        try {
            telUrl = riAddressFactory.createTelURL(phoneNumber);
        } catch (Exception ex) {
            throw new TckInternalError(ex.getMessage());
        }
        return telUrl;
    }

    protected Address createAddress(String address) {
        Address addr = null;

        try {
            addr = tiAddressFactory.createAddress(address);
            assertTrue(addr != null);
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        }
        return addr;
    }

    protected Address createAddress(
        URI uri) {
        Address addr = null;
        try {
            System.out.println("Uri = " + uri);

            addr = tiAddressFactory.createAddress(uri);
            System.out.println("addr = " + addr);
            System.out.println("createAddress returns " + tiAddressFactory.createAddress(uri.toString()));
            assertTrue(
                addr.equals(tiAddressFactory.createAddress(uri.toString())));
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        }
        return addr;
    }

    protected Address createRiAddress(
        URI uri) {
        Address addr = null;
        try {
            addr = riAddressFactory.createAddress(uri);
        } catch (Exception ex) {
            throw new TckInternalError(ex.getMessage());
        }
        return addr;
    }

    protected Address createRiAddress(String address) {
        Address addr = null;

        try {
            addr = riAddressFactory.createAddress(address);
            assertTrue(addr != null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TckInternalError(ex.getMessage());
        }
        return addr;
    }

    protected Address createRiAddressFromTiAddress(
        Address tiAddress)
        throws TiUnexpectedError {
        try {
            return riAddressFactory.createAddress(tiAddress.toString());
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        }
    }

    public void testStar() {
        try {
            Address star = tiAddressFactory.createAddress( "*" );
            assertTrue( star.isWildcard() );
            assertTrue( star.getURI().isSipURI() );
            assertEquals( "*", ((SipURI) star.getURI()).getUser() );
        } catch (ParseException pe) {
            pe.printStackTrace();
            fail( pe.getMessage() );
        }
    }

    public void testAddressFactory() {
        try {

            for (int i = 0; i < urls.length; i++) {
                URI uri = this.createTiURI(urls[i]);
                assertNotNull(uri);
                Address tiAddress = this.createAddress(uri);
                assertNotNull(tiAddress);

                URI riUri = this.createRiURI(urls[i]);
                Address riAddress =
                    this.createRiAddress(riUri);
                tiAddress = this.createRiAddress(tiAddress.toString());

                assertEquals( riAddress,
                        this.createRiAddressFromTiAddress(tiAddress) );

            }

            for (int i = 0; i < hosts.length; i++) {
                SipURI tiSipURI;
                SipURI riSipURI;
                tiSipURI = this.createTiSipURI(null, hosts[i]);
                assertTrue(tiSipURI != null);
                assertTrue(tiSipURI.isSipURI());
                assertTrue(!tiSipURI.isSecure());
                assertTrue(
                    (
                        (SipURI) tiAddressFactory.createURI(
                            "sip:" + hosts[i])).equals(
                        tiSipURI));
                riSipURI = this.createRiSipURI(null, hosts[i]);
                Address tiAddress =
                    this.createAddress(tiSipURI);
                assertTrue(tiAddress != null);
                Address riAddress =
                    this.createRiAddress(riSipURI);
                assertTrue(
                    riAddress.equals(
                        this.createRiAddressFromTiAddress(tiAddress)));

                tiSipURI = this.createTiSipURI("jaintck", hosts[i]);
                assertTrue(tiSipURI != null);
                assertTrue(tiSipURI.isSipURI());
                assertTrue(!tiSipURI.isSecure());
                assertTrue(
                    (
                        (SipURI) tiAddressFactory.createURI(
                            "sip:jaintck@" + hosts[i])).equals(
                        tiSipURI));
                tiAddress = this.createAddress(tiSipURI);
                assertTrue(tiAddress != null);

                riSipURI = this.createRiSipURI("jaintck", hosts[i]);
                riAddress = this.createRiAddress(riSipURI);
                assertTrue(
                    riAddress.equals(
                        this.createRiAddressFromTiAddress(tiAddress)));
            }

            for (int i = 0; i < phoneNumbers.length; i++) {
                TelURL tiTelUrl =
                    this.createTiTelURL(phoneNumbers[i]);
                assertTrue(tiTelUrl != null);
                TelURL riTelUrl =
                    this.createRiTelURL(phoneNumbers[i]);

                System.out.println( "TI:" + tiTelUrl );
                System.out.println( "RI:" + riTelUrl );

                assertEquals(
                    riTelUrl, createRiTelURLFromTiTelURL(tiTelUrl) );
            }

            for (int i = 0; i < telUrls.length; i++) {
                TelURL telUrl =
                    (TelURL) this.createTiURI(telUrls[i]);
                assertTrue(telUrl != null);
                    int start = telUrl.toString().indexOf(':');
                    String phone = telUrl.toString().substring(start+1).trim();
                TelURL tiTelUrl = this.createTiTelURL(phone);
                tiTelUrl.setGlobal(telUrl.isGlobal());
                assertTrue(telUrl.equals(tiTelUrl));
            }
        } catch (Exception ex) {
            throw new TiUnexpectedError(ex.getMessage());
        } finally {
            logTestCompleted("testAddressFactory()");
        }

    }

    public void testTelURL() throws Exception {
        // JvB: This weird-looking tel: URL is actually valid, syntactically speaking
        URI telURL = tiAddressFactory.createURI( "tel:0123456789ABCDEF#*-.();isub=/:-_.!~*'();phone-context=+123-.();-=[]/:" );
        assertTrue( telURL instanceof TelURL );
        TelURL t = (TelURL) telURL;
        assertEquals( "0123456789ABCDEF#*-.()", t.getPhoneNumber() );
        assertEquals( "+123-.()", t.getPhoneContext() );
        assertEquals( "/:-_.!~*'()", t.getIsdnSubAddress() );
        assertEquals( "[]/:", t.getParameter("-") );
    }

    public void setUp() {

    }

    public static Test suite() {
        return new TestSuite(AddressFactoryTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AddressFactoryTest.class);
    }

}
