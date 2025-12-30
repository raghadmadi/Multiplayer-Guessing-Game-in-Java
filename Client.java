import java.io.*;
import java.net.*;
import java.util.List;
public class Client 
{
    public static void main(String[] args) throws ClassNotFoundException {
   
        try{
               Socket socket = new Socket("localhost", 7200); 
           
               ObjectOutputStream pout = new ObjectOutputStream(socket.getOutputStream());
               ObjectInputStream Buffreader = new ObjectInputStream(socket.getInputStream());
             
               BufferedReader UserIn = new BufferedReader(new InputStreamReader(System.in)); 
               
               System.out.println((String)Buffreader.readObject()); // reading ( Please enter your nickname)
               String nickname = UserIn.readLine(); // reading the nick name from the user 
               pout.writeObject(nickname); // sending the name to the server
               System.out.println((String)Buffreader.readObject()); //reading the welcome statement 
               
               while (true){
                   System.out.println( (String)Buffreader.readObject());  
                   String choice = UserIn.readLine();
                   pout.writeObject(choice); // send it to the server
               
               if(choice.equalsIgnoreCase("start")){
                   CreateChallenge(pout,Buffreader,UserIn );
               }
               else if (choice.equalsIgnoreCase("join")){
                   JoinChallenge(pout,Buffreader,UserIn,socket); 
               }
               else if (choice.equalsIgnoreCase("exit")) {
                   System.out.println((String)Buffreader.readObject());
                    break;
                }
               
              
               }
               socket.close();
           
        }
        catch(BindException be){
            System.err.println("There is a service running at this port");
            
        }
        catch(IOException ioe){
            System.out.println("I/O error"+ioe);   
        }

    }

        private static String getCategoryName(String categoryNum) {
        switch (categoryNum) {
            case "1": return "Movies";
            case "2": return "Actors";
            case "3": return "Athletes";
            case "4": return "Scientists";
            case "5": return "Historical Figures";
            case "6": return "Books";
            case "7": return "TV Shows";
            case "8": return "Countries/Cities";
            default: return "Movies"; // Default fallback
        }
    }

private static void CreateChallenge(ObjectOutputStream pout, ObjectInputStream Buffreader,BufferedReader UserIn )throws IOException, ClassNotFoundException
{

    for ( int i = 0 ; i< 9 ; i++){
        System.out.println((String)Buffreader.readObject());
    }
    
    String category = UserIn.readLine().trim(); // take it from the user
    String categorystr = getCategoryName(category);
    pout.writeObject(categorystr); //sending the category to the server
 
    System.out.println((String)Buffreader.readObject()); //to enter the secret name 
    String secretname = UserIn.readLine().trim(); // take it from the user 
    pout.writeObject(secretname); //sending the secret name to the server
      
    System.out.println((String)Buffreader.readObject()); //to set the limit or not (Yes or No
    String limitChoice = UserIn.readLine().trim(); //reading from the user
    System.out.println(limitChoice);
    pout.writeObject(limitChoice); 
    
    if(limitChoice.trim().equalsIgnoreCase("yes")){
        System.out.println((String)Buffreader.readObject()); 
        try {
            String maxAttempts = UserIn.readLine().trim();
            pout.writeObject(maxAttempts);
            } 
        catch (NumberFormatException e) {
            System.out.println("Invalid number!");
            pout.writeObject("-1"); 
        }
    }
    else 
    {
        System.out.println("Unlimited attempts");
    }
    System.out.println((String)Buffreader.readObject());
    String cc = (String)Buffreader.readObject();
    System.out.println(cc);
    ChallengerWork(pout,Buffreader,UserIn);
}

private static void JoinChallenge(ObjectOutputStream pout, ObjectInputStream Buffreader,BufferedReader UserIn , Socket socket) throws IOException, ClassNotFoundException
{
    String serverResponse = (String)Buffreader.readObject();
  
        if (serverResponse.contains("No active challenges available")) {
            System.out.println(serverResponse);
            return;
        }
        System.out.println(serverResponse);
       List<Challenge> activeChallenges = (List<Challenge>) Buffreader.readObject();

        for (int i = 0; i < activeChallenges.size(); i++) {
            Challenge c = activeChallenges.get(i);
            
            System.out.println("Challenge " + (i+1) + ":");
            System.out.println("Name: " + c.getChallenger().getnickname());
            System.out.println("Category: " + c.getCategory());
            System.out.println("Guessers number: " + c.getGuessers().size());
            System.out.println("Limited Attempts: " + c.isLimitedAttempts());
            System.out.println("MaxAttempts: " + c.getMaxAttemptsOne());
            
            System.out.println();
        }

     
    String msg = (String)Buffreader.readObject(); 
    System.out.println(msg);
    String Mychallenge = UserIn.readLine();
    pout.writeObject(Mychallenge); 
    
    msg = (String)Buffreader.readObject();
    if (msg.contains("Invalid choice")){
        System.out.println(msg);
        System.exit(0);
    }
    msg = (String)Buffreader.readObject();
    if (msg.contains("This challenge already has 2 guessers!")){
        System.out.println(msg);
       System.exit(0);
    }
    
    msg = (String)Buffreader.readObject();
    System.out.println(msg);
    msg = (String)Buffreader.readObject();
    System.out.println(msg);
    msg = (String)Buffreader.readObject();
    if (msg.equals("two"))playerTwo(pout,Buffreader,UserIn);
    else if (msg.equals("one"))playerOne(pout,Buffreader,UserIn);

}
private static void playerOne(ObjectOutputStream pout, ObjectInputStream Buffreader,BufferedReader UserIn) throws IOException, ClassNotFoundException{
    Boolean myTurn = true;
    while (true){
        if (myTurn){
        
            String msg = (String)Buffreader.readObject(); // it your turn
            System.out.println(msg);
            msg = UserIn.readLine();//yes/no question
            
            pout.writeObject(msg);
            System.out.println("before challenger ans");
            msg = (String)Buffreader.readObject();//get the answer
            System.out.println("after challenger ans");
            System.out.println(msg);
            msg = (String)Buffreader.readObject();// attemps limit
            if (msg.equals("You Lose!!!")){
                System.out.println(msg);
                System.exit(0);
                
            }
            msg = (String)Buffreader.readObject();//challenger asks to guess
            System.out.println(msg);
            msg = UserIn.readLine();// yes no to guess
            pout.writeObject(msg);
            msg = (String)Buffreader.readObject();//enter your guess
            if (msg.equals("x")){
                myTurn = false;
                
            }
            else{
                System.out.println(msg);
                msg = UserIn.readLine();// get the guess
                pout.writeObject(msg);
                msg = (String)Buffreader.readObject();//response to guess
                System.out.println(msg);
                msg = (String)Buffreader.readObject();//if won
                if (msg.equals("You won!!!")){
                    System.out.println(msg);
                    System.exit(0);
                }
                myTurn = false;
               
            }
        }
        else {
            String msg = (String)Buffreader.readObject();// get opponent question
            System.out.println(msg);
            msg = (String)Buffreader.readObject(); // get the answer of the question
            System.out.println(msg);
            String cc  = (String)Buffreader.readObject();
            if (cc.equals("one"))
            {
                System.out.println(cc);
            }
            else if (cc.equals("two"))
            {
                System.out.println(cc);
            }
             myTurn = true;
        }
    }
    
    
    
}
private static void playerTwo(ObjectOutputStream pout, ObjectInputStream Buffreader,BufferedReader UserIn) throws IOException, ClassNotFoundException{
    Boolean myTurn = false;
    while (true){
        if (myTurn){
            String msg = (String)Buffreader.readObject(); // it your turn
            System.out.println(msg);
            msg = UserIn.readLine();//yes/no question
            pout.writeObject(msg);
            msg = (String)Buffreader.readObject(); //get the answer
            System.out.println(msg);
            msg = (String)Buffreader.readObject();// attemps limit
            if (msg.equals("You Lose!!!")){
                System.out.println(msg);
                System.exit(0);
                
            }
            
            msg = (String)Buffreader.readObject();//challenger asks to guess
            System.out.println(msg);
            msg = UserIn.readLine();// yes no to guess
            pout.writeObject(msg);
            msg = (String)Buffreader.readObject();//enter your guess
            if (msg.equals("x")){
                myTurn = false;
                
            }
            else{
                System.out.println(msg);
                msg = UserIn.readLine();// get the guess
                pout.writeObject(msg);
                msg = (String)Buffreader.readObject();//response to guess
                System.out.println(msg);
                msg = (String)Buffreader.readObject();//if won
                if (msg.equals("You won!!!")){
                    System.out.println(msg);
                    System.exit(0);
                }
                myTurn = false;
            }
        }
        else {
            String msg = (String)Buffreader.readObject();// get opponent question
            System.out.println(msg);
            msg = (String)Buffreader.readObject(); // get the answer of the question
            System.out.println(msg);
            String cc  = (String)Buffreader.readObject(); // get the answer of the question
            if (cc.equals("one")){
                System.out.println(cc);
            }
            else if (cc.equals("two")){
                System.out.println(cc);
            }
            
             myTurn =true;
        }
    }
    
}  
private static void ChallengerWork(ObjectOutputStream pout, ObjectInputStream Buffreader,BufferedReader UserIn) throws IOException, ClassNotFoundException{
        
    while (true)
    {
            String msg = (String)Buffreader.readObject();
            System.out.println(msg);
            msg = (String)Buffreader.readObject();
            System.out.println(msg);
            msg = UserIn.readLine();
            pout.writeObject(msg);
            
        }
}


}

