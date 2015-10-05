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

    @OnWebSocketConnect
    public void handleConnect (org.eclipse.jetty.websocket.api.Session session) {
        player = new HeadsUpPlayer(null, 200, -1, session, -1);
        player.versusBot = true;
        player.start();
        PokerServer.lobby.addPlayer(player);
        System.out.println("Client connected...");
        this.session = session;
    }

    @OnWebSocketClose
    public void handleClose (int statusCode, String reason) {
        System.out.println("Connection closed with statusCode = " + statusCode + ", reason = " + reason);
        //Check if player is currently playing a game
        if(player.isPlaying){
            //Check if game has two players (probably can find a safer way to do this)
            if(player.getGame().players.size()==2){
                player.isConnected = false;
                player.sendOpponentMessage(player.getPlayerName() + " has disconnected, returning to player lobby");
                player.getGame().endCurrentGame();
                PokerServer.lobby.removePlayer(player);
            }
        }
    }

    @OnWebSocketMessage
    public void handleMessage (String message) {
        player.receiveMessage(message);
    }

    @OnWebSocketError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    private void send(String message){
        try{
            if (session.isOpen()){
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
