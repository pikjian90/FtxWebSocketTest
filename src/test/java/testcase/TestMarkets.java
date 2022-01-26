package testcase;

import common.ExpectedResult;
import common.uri;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.java_websocket.enums.ReadyState;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import websocket.Client;

import java.net.URI;

public class TestMarkets {
    public static Client c;
    public Logger logger;

    @BeforeSuite()
    public void setupSuite(){
        try {
            logger = Logger.getLogger("FtxWebSocketTest"); // added logger
            PropertyConfigurator.configure("log4j.properties");
            logger.setLevel(Level.INFO);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @BeforeClass
    public void setupClass() {
        logger.info("BeforeClass : setup");
        try {
            c = new Client(new URI(uri.endPoint));
            c.connect();

            while (!c.getReadyState().equals(ReadyState.OPEN)) {
                Thread.sleep(1000);
            }
            logger.info("BeforeClass : Connection is " + c.getReadyState());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testConnection(){
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(c.httpStatus, ExpectedResult.expectedHttpStatus,"Http Status is not expected");
        softAssert.assertEquals(c.httpStatusMessage,ExpectedResult.expectedHttpStatusMessage,"Http Status is not expected");
        softAssert.assertAll();
    }

    @Test
    public void testMessage() {
        try {
            String input = "{\"channel\" : \"markets\",\n" + "\"market\" : \"BTC-PERP\",\n" +
                    "    \"op\" : \"subscribe\"\n" +
                    "}";
            c.send(input);
            Thread.sleep(3000);

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(c.responseMessage.get(0).contains("subscribed"),
                    "Subscribed response message is not received");
            softAssert.assertTrue(c.responseMessage.get(c.responseMessage.size()-1).
                    contains("channel\": \"markets\""),
                    "Update Response Message is not received");
            softAssert.assertAll();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown(){
        try {
            c.close();
            Thread.sleep(1000);

            boolean isConnectionClosed = c.isClosed();
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(isConnectionClosed,"Connection is not closed successfully");
            softAssert.assertAll();

            if(c.isClosed()){
                logger.info("AfterClass : Connection is closed");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

