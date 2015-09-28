package poker;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class PokerServer {

    public static PokerLobby lobby = new PokerLobby();

    public static void main(String[] args) throws Exception{
        Server server = new Server(8080);
        //Context handler is used to define attributes for all servlets added
        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/");
        ctx.addServlet(PokerServlet.class, "/PokerClient");

        server.setHandler(ctx);
        server.start();
        //Wait 1000ms to insure server has started
        Thread.sleep(1000);
        lobby.start();
    }

    public static class PokerServlet extends WebSocketServlet {

        @Override
        public void configure(WebSocketServletFactory factory){
            factory.register(PokerWebSocket.class);
        }
    }

}
