package demo;

import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebSocket
public class StockServiceWebSocket {

    private org.eclipse.jetty.websocket.api.Session session;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @OnWebSocketConnect
    public void handleConnect (org.eclipse.jetty.websocket.api.Session session) {
        System.out.println("Client connected...");
        this.session = session;
    }

    @OnWebSocketClose
    public void handleClose (int statusCode, String reason) {
        System.out.println("Connection closed with statusCode = " + statusCode + ", reason = " + reason);
    }

    @OnWebSocketMessage
    public void handleMessage (String message) {
        switch (message) {
            case "start":
                System.out.println("start");
                send("Stock service started!");
                executor.scheduleAtFixedRate(() ->
                        send(StockService.getStockInfo()), 0, 5, TimeUnit.SECONDS);
                break;
            case "stop":
                System.out.println("stop");
                this.stop();
                break;
        }
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
