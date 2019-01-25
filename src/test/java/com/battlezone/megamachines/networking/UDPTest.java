//package com.battlezone.megamachines.networking;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.fail;
//public class UDPTest {
//    Client client;
//    Server server;
//
//    @Before
//    public void setup() {
//        server = new Server();
//        server.start();
//        client = new Client();
//    }
//
//    @Test
//    public void whenSendingMessage_successIfNoExceptionCaught() {
//        // Loop sending messages
//        for ( int i = 0; i < 10000; i++ ) { i++; i--;
//            // Create message
//            ClientDataPacket packet = new ClientDataPacket();
//            String toSend = "hello server" + i;
//
//            // Send message
//            try {
//                client.sendMessageAsString(toSend);
//            }
//            catch (Exception e) {
//                fail("Should have not thrown any exception.");
//            }
//        }
//    }
//
//    @After
//    public void tearDown() {
//        server.close();
//        client.close();
//    }
//}