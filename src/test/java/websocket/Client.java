package websocket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

public class Client extends WebSocketClient {
    public String httpStatusMessage;
    public short httpStatus;
    public ArrayList<String> responseMessage = new ArrayList<>();
    public Logger logger;

    public Client(URI serverURI ) {
        super( serverURI );
        logger = Logger.getLogger("FtxWebSocketTest"); // added logger
        PropertyConfigurator.configure("log4j.properties");
        logger.setLevel(Level.INFO);
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        for(Iterator it = handshakedata.iterateHttpFields(); it.hasNext();) {
            String key = (String) it.next();
            logger.info("onOpen : " + key + " = "+handshakedata.getFieldValue(key));
        }
        httpStatusMessage = handshakedata.getHttpStatusMessage();
        httpStatus = handshakedata.getHttpStatus();
        logger.info( "onOpen : Connection is opened" );
    }

    @Override
    public void onMessage( String message ) {
        responseMessage.add(message);
        logger.info( "onMessage : received " + message );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        logger.info( "onClosed : Connection closed by " + ( remote ? "remote peer" : "us " ) + code + "|" + reason );
    }

    @Override
    public void onError( Exception e ) {
        e.printStackTrace();
    }
}
