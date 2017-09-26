package gov.nist.javax.sip.clientauthutils;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.AuthorizationList;

import javax.sip.address.URI;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.security.NoSuchAlgorithmException;

public class DigestServerAuthenticationWWWHelper extends DigestServerAuthenticationHelper {
    /**
     * Default constructor.
     *
     * @throws NoSuchAlgorithmException
     */
    public DigestServerAuthenticationWWWHelper() throws NoSuchAlgorithmException {
        super();
    }

    public void generateChallenge(HeaderFactory headerFactory, Response response, String realm  ) {
        try {
            WWWAuthenticateHeader wwwAuthenticateHeader = headerFactory.createWWWAuthenticateHeader(DEFAULT_SCHEME);
            wwwAuthenticateHeader.setParameter("realm", realm);
            wwwAuthenticateHeader.setParameter("nonce", generateNonce());
            //wwwAuthenticateHeader.setParameter("opaque", "");
            //wwwAuthenticateHeader.setParameter("stale", "FALSE");
            wwwAuthenticateHeader.setParameter("algorithm", DEFAULT_ALGORITHM);
            response.setHeader(wwwAuthenticateHeader);
        } catch (Exception ex) {
            InternalErrorHandler.handleException(ex);
        }

    }
    /**
     * Authenticate the inbound request.
     *
     * @param request - the request to authenticate.
     * @param hashedPassword -- the MD5 hashed string of username:realm:plaintext password.
     *
     * @return true if authentication succeded and false otherwise.
     */
    public boolean doAuthenticateHashedPassword(Request request, String hashedPassword) {
        AuthorizationList authHeaderList = (AuthorizationList) request.getHeader("AuthorizationList");
        if ( authHeaderList == null ) return false;
        Authorization authHeader = authHeaderList.get(0);
        if ( authHeader == null ) return false;
        String realm = authHeader.getRealm();
        String username = authHeader.getName();

        if ( username == null || realm == null ) {
            return false;
        }

        String nonce = authHeader.getNonce();
        URI uri = authHeader.getURI();
        if (uri == null) {
            return false;
        }



        String A2 = request.getMethod().toUpperCase() + ":" + uri.toString();
        String HA1 = hashedPassword;


        byte[] mdbytes = messageDigest.digest(A2.getBytes());
        String HA2 = toHexString(mdbytes);

        String cnonce = authHeader.getNonce();
        String KD = HA1 + ":" + nonce;
        if (cnonce != null) {
            KD += ":" + cnonce;
        }
        KD += ":" + HA2;
        mdbytes = messageDigest.digest(KD.getBytes());
        String mdString = toHexString(mdbytes);
        String response = authHeader.getResponse();


        return mdString.equals(response);
    }

    /**
     * Authenticate the inbound request given plain text password.
     *
     * @param request - the request to authenticate.
     * @param pass -- the plain text password.
     *
     * @return true if authentication succeded and false otherwise.
     */
    public boolean doAuthenticatePlainTextPassword(Request request, String pass) {
        Authorization authHeader = (Authorization) request.getHeader(Authorization.NAME);
        if ( authHeader == null ) return false;
        String realm = authHeader.getRealm();
        String username = authHeader.getName();


        if ( username == null || realm == null ) {
            return false;
        }


        String nonce = authHeader.getNonce();
        URI uri = authHeader.getURI();
        if (uri == null) {
            return false;
        }


        String A1 = username + ":" + realm + ":" + pass;
        String A2 = request.getMethod().toUpperCase() + ":" + uri.toString();
        byte mdbytes[] = messageDigest.digest(A1.getBytes());
        String HA1 = toHexString(mdbytes);


        mdbytes = messageDigest.digest(A2.getBytes());
        String HA2 = toHexString(mdbytes);

        String cnonce = authHeader.getNonce();
        String KD = HA1 + ":" + nonce;
        if (cnonce != null) {
            KD += ":" + cnonce;
        }
        KD += ":" + HA2;
        mdbytes = messageDigest.digest(KD.getBytes());
        String mdString = toHexString(mdbytes);
        String response = authHeader.getResponse();
        return mdString.equals(response);

    }
}
