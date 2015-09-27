package poker;

import org.eclipse.jetty.websocket.api.annotations.*;
import sun.nio.ch.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@WebSocket
public class PokerWebSocket {

    private org.eclipse.jetty.websocket.api.Session session;
    private HeadsUpPlayer player;
    private InputStream input;

    @OnWebSocketConnect
    public void handleConnect (org.eclipse.jetty.websocket.api.Session session) {
        player = new HeadsUpPlayer(null, 200, -1, session, -1);
        PokerServer.lobby.addPlayer(player);
        System.out.println("Client connected...");
        this.session = session;
    }

    @OnWebSocketClose
    public void handleClose (int statusCode, String reason) {
        System.out.println("Connection closed with statusCode = " + statusCode + ", reason = " + reason);
    }

    @OnWebSocketMessage
    public void handleMessage (String message) {
        System.out.println(message);
        player.receiveMessage(message);
    }

    @OnWebSocketError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    private void send(String message){
        try{
            if (session.isOpen()){
                System.out.println("sent");
                session.getRemote().sendString(message);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void stop(){
        try {
            session.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
