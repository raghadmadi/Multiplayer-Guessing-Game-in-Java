import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
    public class clientThread extends Thread   
    {
        final private Socket nextClient;
        public clientThread(Socket socket) 
        {
            this.nextClient = socket;
        }
        @Override
        public void run() {
            try 
            {
            ObjectOutputStream pout = new ObjectOutputStream(nextClient.getOutputStream());
            ObjectInputStream Buffreader = new ObjectInputStream(nextClient.getInputStream());
            
            pout.writeObject("Please Enter your nickname"); // sending to the client 
            String nickname = (String)Buffreader.readObject(); //reading the nickname from the client
            Player player = new Player(nickname, nextClient);
            pout.writeObject("Welcome to our Game "+nickname); // sending to the client 

                   
        while(true){
            pout.writeObject("Do you want to start a new challenge or join one or exit?(start/join/exit)"); // send the line to the client
            String choice = (String)Buffreader.readObject(); // reading from the client
            if (choice == null) break;
            
             if(choice.equalsIgnoreCase("start")){
                   CreatingChallenge(pout,Buffreader,player);
               }
            else if (choice.equalsIgnoreCase("join")){
                   JoiningChallenge(pout,Buffreader,player,nextClient); 
                   
               }
            else if (choice.equalsIgnoreCase("exit")) {
                    pout.writeObject("bye!");
                }
            else {
                pout.writeObject("Invalid choice");
                break;
                }
        }     
            }
           
             catch (IOException ex) 
            {
                System.out.println(ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException e) {
            
                e.printStackTrace();
            }
            try {
            nextClient.close();
                }    catch (IOException e) 
                
                {
        System.out.println("Error closing socket: " + e);
            }
        }
    private void CreatingChallenge(ObjectOutputStream pout, ObjectInputStream Buffreader, Player player) throws IOException, ClassNotFoundException, InterruptedException
        {
        
        pout.writeObject("Enter the Category from this menu");
        pout.writeObject("1 - Movies");
        pout.writeObject("2 - Actors / Actresses");
        pout.writeObject("3 - Famous Athletes / Sports Figures");
        pout.writeObject("4 - Scientists / Inventors");
        pout.writeObject("5 - Historical Figures");
        pout.writeObject("6 - Famous Books / Novels");
        pout.writeObject("7 - TV Series / Shows");
        pout.writeObject("8 - Countries or Cities"); // sending to the client 

        String category = (String)Buffreader.readObject(); // reading the category from the client
   
        pout.writeObject("Enter the secret name"); // sending to the client
        String secretName =(String)Buffreader.readObject(); // reading it 
   
        pout.writeObject("Do you want to set attempt limit? (Yes/No) "); // sending to the client 
        String The_limit = (String)Buffreader.readObject();
        The_limit = The_limit.trim();

        
        int maxAttempts = -1;
        boolean limited = The_limit.equalsIgnoreCase("yes");
        
        if (limited)
        {
            pout.writeObject("Please enter attempt limit");
            try {
                String  h = (String)Buffreader.readObject();
                h = h.trim();
                maxAttempts = Integer.parseInt(h);
            } 
            catch (NumberFormatException e) {
            pout.writeObject("Invalid number");
            maxAttempts = -1;
            limited = false;
            }
        }
        Challenge newChallenge = new Challenge(player, secretName, category, limited, maxAttempts);
        Server.activeChallenges.add(newChallenge);
        pout.writeObject("Challenge created ... ");
        while (newChallenge.getGuessers().size() != 2){;}
        pout.writeObject("Challenge is starting now! Yippie ");
        ChallengerWork(newChallenge,pout,Buffreader);
        
    }


    private void JoiningChallenge(ObjectOutputStream pout, ObjectInputStream Buffreader,Player player, Socket socket) throws IOException, ClassNotFoundException, InterruptedException {
        
        if (Server.activeChallenges.isEmpty()) {
            pout.writeObject("No active challenges available");
            return;
        }
        
 
        pout.writeObject("the available challenges are:");
        pout.flush();

        ArrayList<Challenge> avail = new ArrayList<>();
        for (Challenge ch : Server.activeChallenges) {

            avail.add(ch);
        }
        pout.writeObject(avail);


    pout.reset();
    pout.flush();

        pout.writeObject("Enter the number of the challenge you want to join");
        String  h = (String)Buffreader.readObject();
        h = h.trim();
        int choice = Integer.parseInt(h);
        choice--;
        Challenge selectedChallenge;

        synchronized(Server.activeChallenges)
        {
            if (choice < 0 || choice >= Server.activeChallenges.size()) {
                pout.writeObject("Invalid choice");
                return;
            }
            else pout.writeObject("x");
            selectedChallenge = (Challenge) Server.activeChallenges.get(choice);
        }

        

        if (selectedChallenge.getGuessers().size() >= 2) {
            pout.writeObject("This challenge already has 2 guessers!");
            return;
        }
        else pout.writeObject("x");

        selectedChallenge.addGuesser(player);

        pout.writeObject("Joined challenge! Waiting for game to start...");
        Boolean x = false;
        while (selectedChallenge.getGuessers().size() != 2){
               x = true;
        }
        if (selectedChallenge.getGuessers().size() == 2)
        {
            pout.writeObject("Challenge is starting now! Yippie ");
            if (!x) pout.writeObject("two");
            else pout.writeObject("one");

            synchronized(Server.activeChallenges) {
                Server.activeChallenges.remove(selectedChallenge);
            }
            if (!x)playerTwo(selectedChallenge,pout,Buffreader);
            else playerOne(selectedChallenge,pout,Buffreader);

        }    
    }

  private void playerOne(Challenge selectedChallenge,ObjectOutputStream pout, ObjectInputStream Buffreader) throws IOException, ClassNotFoundException, InterruptedException{
    

    String myName = selectedChallenge.getGuessers().get(0).getnickname();
    String opName = selectedChallenge.getGuessers().get(1).getnickname();

    while (true){
        if (selectedChallenge.p1Turn)
        {
            pout.writeObject("It is your turn " + myName+" Ask a yes/no question to the challenger : "); //send this to the client
            String msg = (String)Buffreader.readObject();
            Questions qqq = new Questions(msg);

            ArrayList <Questions> qusetion = selectedChallenge.qusetion;
            synchronized(qusetion)
            {   
                qusetion.add(qqq);
            }
            
            // Wait for challenger to answer the question
            String answer = qqq.getAns(); // This will block until challenger answers
            pout.writeObject("Challenger answered: " + answer);

            if (selectedChallenge.getMaxAttemptsOne() == 0 )
            {
                pout.writeObject("You Lose!!!"); 
                String win = selectedChallenge.win;
                synchronized(selectedChallenge) // Sync on challenge object, not string
                {
                    selectedChallenge.win = "two";
                }
            }
            else  
            {
                pout.writeObject("x"); 
            }
            pout.writeObject("Challenger asks you if you want to guess:(yes, no)"); //send this to the client
            msg = (String)Buffreader.readObject();
            if (msg.equalsIgnoreCase("yes")){
                pout.writeObject("Enter you guess:");
                msg = (String)Buffreader.readObject();
                if (msg.equalsIgnoreCase(selectedChallenge.getSecretName())){
                    pout.writeObject("right guess..."); 
                    pout.writeObject("You won!!!"); 
                    synchronized(selectedChallenge) // Sync on challenge object
                    {
                        selectedChallenge.win = "one";
                    }
                    
                }
                else {
                    pout.writeObject("wrong guess..."); 
                    synchronized(selectedChallenge) // Sync on challenge object
                    {
                        selectedChallenge.win = "three";   
                    }
                    pout.writeObject("x"); 
                    selectedChallenge.maxAttemptsOne--;
                }
                selectedChallenge.p1Turn = false;
                selectedChallenge.p2Turn = true;

            }

            else 
            {
                pout.writeObject("x"); //send this to the client
                synchronized(selectedChallenge){ // Sync on challenge object
                        selectedChallenge.win = "three";
                }
              
                selectedChallenge.p1Turn = false;
                selectedChallenge.p2Turn = true;
            }
            
        }
        else {
            synchronized(selectedChallenge.qusetion){
                while(selectedChallenge.qusetion.isEmpty()){
                    selectedChallenge.qusetion.wait(1000);

                }   
                
            
                    Questions x = selectedChallenge.qusetion.get(0);
                    pout.writeObject(opName + "asked : " + x.getQ());
                        
                    String answer = x.getAns();
                    pout.writeObject("Challenger answered: " + answer);

                    x.updateCnt();
                     if (x.getCnt()==2)
                    {
                        selectedChallenge.qusetion.remove(0);
                    }
            }      
            synchronized(selectedChallenge) // Sync on challenge object
            {
                
                    while (selectedChallenge.win.isEmpty())
                    {
                        selectedChallenge.wait(1000);
                    }
            
                    String cc = selectedChallenge.win;
                    if (cc.equals("two")){
                        pout.writeObject("You Lose!!!"); 
                    }
                    else if (cc.equals("one")){
                        pout.writeObject("You Won!!!");
                    }
                    else {
                        pout.writeObject("x"); 
                    }
                
                }
             selectedChallenge.p1Turn = true;
            selectedChallenge.p2Turn = false;
        
        }
    
    
    }
    
}
  private void playerTwo(Challenge selectedChallenge,ObjectOutputStream pout, ObjectInputStream Buffreader) throws IOException, ClassNotFoundException, InterruptedException{
    
    String myName = selectedChallenge.getGuessers().get(1).getnickname();
    String opName = selectedChallenge.getGuessers().get(0).getnickname();
    while (true){
        
        if (selectedChallenge.p2Turn){
            pout.writeObject("It is your turn " + myName+" Ask a yes/no question for to the challenger ㄟ( ▔, ▔ )ㄏ : "); //send this to the client
            String msg = (String)Buffreader.readObject();
            Questions qqq = new Questions(msg);
            
            synchronized(selectedChallenge.qusetion)
            {
                selectedChallenge.qusetion.add(qqq);
            }
            
            // Wait for challenger to answer the question - this will block until answer is provided
            System.out.println("before challenger ans");
            String answer = qqq.getAns(); // This will properly wait for challenger's answer
            System.out.println("after challenger ans");
            pout.writeObject("Challenger answered: " + answer);
           
            
            if (selectedChallenge.getMaxAttemptsTwo() == 0 ){
                pout.writeObject("You Lose!!!"); 
                synchronized(selectedChallenge) // Sync on challenge object, not string
                {
                        selectedChallenge.win = "one";
                }
            }
            else {
                pout.writeObject("x"); 
            }
            pout.writeObject("Challenger asks you if you want to guess:(yes, no)"); //send this to the client
            msg = (String)Buffreader.readObject();
            if (msg.equalsIgnoreCase("yes")){
                pout.writeObject("Enter you guess:"); //send this to the client
                msg = (String)Buffreader.readObject();
                if (msg.equalsIgnoreCase(selectedChallenge.getSecretName())){
                    pout.writeObject("right guess..."); 
                    pout.writeObject("You won!!!"); 
                    synchronized(selectedChallenge) // Sync on challenge object
                    {
                        selectedChallenge.win = "two";
                    }
                    
                }
                else {
                    pout.writeObject("wrong guess..."); 
                    synchronized(selectedChallenge){ // Sync on challenge object
                        selectedChallenge.win = "three";
                    }
                    pout.writeObject("x"); 
                    selectedChallenge.maxAttemptsTwo--;
                }
                selectedChallenge.p1Turn = true;
                selectedChallenge.p2Turn = false;
            }
            else {
                pout.writeObject("x"); 
                synchronized(selectedChallenge){ // Sync on challenge object
                        selectedChallenge.win = "three";
                }
                selectedChallenge.p1Turn = true;
                selectedChallenge.p2Turn = false;
            
            }
        }
        else  {
         
            synchronized(selectedChallenge.qusetion){
                while(selectedChallenge.qusetion.isEmpty())
                {
                    selectedChallenge.qusetion.wait(1000);
                }
            }
            
            Questions x = selectedChallenge.qusetion.get(0);
            pout.writeObject(opName + "asked : " + x.getQ());
           
            String answer = x.getAns(); // Wait for challenger's answer 
            pout.writeObject("Challenger answered: " + answer);

            x.updateCnt();
            synchronized(selectedChallenge.qusetion) {
                if (x.getCnt()==2)
                {
                    selectedChallenge.qusetion.remove(0);
                }
            }
            
           
            synchronized(selectedChallenge) 
            {
                while(selectedChallenge.win.isEmpty())
                {
                    selectedChallenge.wait(1000);
                }
            
                String cc = selectedChallenge.win;
                if (cc.equals("one")){
                    pout.writeObject("You Lose!!!"); 
                }
                else if (cc.equals("two")){
                    pout.writeObject("You Won!!!");
                }
                else {
                    pout.writeObject("x"); 
                }
            }
            selectedChallenge.p1Turn = false;
            selectedChallenge.p2Turn = true;

        }
    }
}
   private void ChallengerWork(Challenge selectedChallenge,ObjectOutputStream pout, ObjectInputStream Buffreader) throws IOException, ClassNotFoundException, InterruptedException{
    
    while(true){
        ArrayList <Questions> qusetion = selectedChallenge.qusetion;
        Questions x;
        
        synchronized(qusetion){
            while(qusetion.isEmpty())
            {
                qusetion.wait(1000);
            }
            x = selectedChallenge.qusetion.get(0); 
        }
    
        pout.writeObject("New Question Received: " + x.getQ());
        pout.writeObject("Answer:(yes, no, idk):");
        String msg = (String)Buffreader.readObject();
    
        x.setAns(msg);
        x.updateCnt();
        
      
        synchronized(qusetion) {
            if (x.getCnt() == 2)
            {
                selectedChallenge.qusetion.remove(0);
            }
        }
    }
}}