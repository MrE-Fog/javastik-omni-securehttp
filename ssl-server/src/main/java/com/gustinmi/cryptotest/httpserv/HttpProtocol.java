package com.gustinmi.cryptotest.httpserv;

import java.io.*;
import java.net.Socket;
import java.security.Principal;
import java.util.Date;
import java.util.logging.Logger;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import com.gustinmi.cryptotest.Utils;

/** HTTP Protocol specifics 
 * @author gustin
 *
 */
public class HttpProtocol {

    public static final Logger log = Utils.loggerForThisClass();

    /** Normal socket (no SSL utilities, wrappers) */
    public static void doProtocolServer(Socket socket) throws IOException {

        try (final InputStream inputStream = socket.getInputStream()) {

            HttpProtocol.readRequest(inputStream);

            try (final OutputStream outputStream = socket.getOutputStream()) {
                HttpProtocol.sendResponse(outputStream);
            }

        }
        finally {
            try {
                socket.close(); // end client request       
            } catch (Exception ex) {}
        }

    }

    /**Socket with SSL context */
    public static void doProtocolServer(SSLSocket sslSock) throws IOException {

        try (final InputStream inputStream = sslSock.getInputStream()) {

            HttpProtocol.readRequest(inputStream);

            final SSLSession session = sslSock.getSession();

            try {
                final Principal clientID = session.getPeerPrincipal();
                if (Utils.INFO_ENEABLED) log.info("client identified as: " + clientID);
            } catch (SSLPeerUnverifiedException e) {
                if (Utils.SYSOUT_ENABLED) System.out.println("client not authenticated");
            }

            try (final OutputStream outputStream = sslSock.getOutputStream()) {
                HttpProtocol.sendResponse(outputStream);
            }

        }
        finally {
            try {
                sslSock.close(); // end client request       
            } catch (Exception ex) {}
        }

    }

    /**
     * Read a HTTP request
     */
    public static void readRequest(InputStream in) throws IOException {
        if (Utils.SYSOUT_ENABLED) System.out.print("Request: ");
        int ch = 0;
        int lastCh = 0;
        while ((ch = in.read()) >= 0 && (ch != '\n' && lastCh != '\n')) {
            if (Utils.SYSOUT_ENABLED) System.out.print((char) ch);
            if (ch != '\r') lastCh = ch;
        }
        if (Utils.SYSOUT_ENABLED) System.out.println("end of request.");
    }

    /**
     * Send a response
     */
    public static void sendResponse(OutputStream out) {
        PrintWriter pWrt = new PrintWriter(new OutputStreamWriter(out));
        // HTTP head
        pWrt.print("HTTP/1.1 200 OK\r\n");
        pWrt.print("Content-Type: text/html\r\n");
        // empty line delimiter for body
        pWrt.print("\r\n");
        // HTTP request body
        pWrt.print("<html>\r\n");
        pWrt.print("<body>\r\n");
        pWrt.printf("Hello from server. Time on server is %s \r\n", new Date().toString());
        pWrt.print("</body>\r\n");
        pWrt.print("</html>\r\n");
        pWrt.flush();
    }

}