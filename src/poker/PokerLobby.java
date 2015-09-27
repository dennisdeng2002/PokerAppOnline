package poker;

import java.io.IOException;
import java.util.*;

public class PokerLobby extends Thread{

    //List must be synchronized in order to update size in while loop of run()
    private static List<HeadsUpPlayer> connectedPlayers = Collections.synchronizedList(new ArrayList<>());

    public PokerLobby(){}

    public void run(){
        System.out.println("Running");
        while(true){
            if(connectedPlayers.size()>1){
                //Currently only works for 1 concurrent game (may have to use HeadsUpDriver instead)
                connectedPlayers.get(0).start();
                connectedPlayers.get(1).start();
                while(connectedPlayers.get(0).getPlayerName()==null ||connectedPlayers.get(0).getPlayerName()==null){
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                HeadsUpPokerGame pokerGame = new HeadsUpPokerGame(2, connectedPlayers.get(0), connectedPlayers.get(1));
            }
        }
    }

    public void addPlayer(HeadsUpPlayer player){
        System.out.println("Player added");
        connectedPlayers.add(player);
        System.out.println(connectedPlayers.size());
    }
}
