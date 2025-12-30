
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int SERVICE_PORT = 7200;

    static List <Challenge> activeChallenges = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try{
            ServerSocket server = new ServerSocket(SERVICE_PORT);
            System.out.println("Server running on port "+SERVICE_PORT);
      while (true)
            {
                Socket nextClient = server.accept(); 
               
                clientThread gameThreads = new clientThread(nextClient);
                gameThreads.start(); 
            } 
        }
        catch(BindException be){
            System.err.println("There is a service running at this port"+SERVICE_PORT);
            
        }
        catch(IOException ioe)
        {
            System.out.println("I/O error"+ioe);
            
        } 
    }
    }

        


    
  
        


