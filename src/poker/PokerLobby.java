package poker;

import java.util.*;

public class PokerLobby extends Thread{

    //List must be synchronized in order to update size in while loop of run()
    private List<HeadsUpPlayer> players = Collections.synchronizedList(new ArrayList<>());
    private List<HeadsUpPlayer> queuedPlayers = Collections.synchronizedList(new ArrayList<>());

    public PokerLobby(){}

    public void run(){
        System.out.println("Running");
        while(true){
            if(queuedPlayers.size()>1 && queuedPlayers.get(0).getPlayerName() != null && queuedPlayers.get(1).getPlayerName() != null){
                setPlayerIDs(queuedPlayers);
                HeadsUpDriver driver = new HeadsUpDriver(queuedPlayers.get(0),queuedPlayers.get(1));
                driver.start();
                //Remove is safer than clear since there is a small possibility
                //player is added to queue while a new game is starting
                queuedPlayers.remove(0);
                queuedPlayers.remove(1);
            }

            //Allow system to pause every 500 ms
            try{
                Thread.sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void addPlayer(HeadsUpPlayer player){
        //Keeps track of all players in game
        players.add(player);
        //Adds a new player to queue
        queuedPlayers.add(player);
    }

    public void setPlayerIDs(List<HeadsUpPlayer> queuedPlayers){
        queuedPlayers.get(0).setID(0);
        queuedPlayers.get(0).setOtherPlayerID(1);

        queuedPlayers.get(1).setID(1);
        queuedPlayers.get(1).setOtherPlayerID(0);

    }

}
