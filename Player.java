
import java.io.Serializable;
import java.net.*;

public class Player implements Serializable 
{
 private final String nickname;
 private final transient Socket playerSocket;

    public Player(String nickname, Socket playerSocket)
    {
        this.playerSocket = playerSocket;
        this.nickname=nickname;
    }
    
        public String getnickname() 
    {
        return nickname;
    }   
    public Socket getPlayerSocket()
    {
        return playerSocket;
    }


}
