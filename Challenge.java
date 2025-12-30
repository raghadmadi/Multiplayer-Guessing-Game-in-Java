


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Challenge implements  Serializable{
    
    public Boolean p1Turn = true;
    public Boolean p2Turn = false;
    public Boolean chTurn = false;
    public Player challenger;
    public List <Player> guessers = new ArrayList<>();
    private final String secretName;
    private final String category;
    private final boolean limitedAttempts;
    public  int maxAttemptsOne;
    public  int maxAttemptsTwo;
    private int currentGuesserIndex = 0; 
    public String win ;

    public ArrayList <Questions> qusetion = new ArrayList<>();

    public Challenge(Player challenger, String secretName, String category,boolean limitedAttempts, int maxAttempts)
    {
        this.challenger = challenger;
        this.secretName = secretName;
        this.category = category;
        this.maxAttemptsOne= maxAttempts;
        this.maxAttemptsTwo= maxAttempts;
        this.limitedAttempts= limitedAttempts;
    }
    

    public synchronized boolean addGuesser(Player guesser) {
        if (guessers.size() >= 2) {
            return false; 
        }
        guessers.add(guesser);
        return true;
    }
    
    //os code woh!!
    public synchronized Player getNextGuesser() {
        if (guessers.isEmpty()) return null;
        Player next = guessers.get(currentGuesserIndex);
        currentGuesserIndex = (currentGuesserIndex + 1) % guessers.size();
        return next;
    }
     
    public String getSecretName() {
        return secretName;
    }

    public String getCategory() {
        return category;
    }

    public Player getChallenger() {
        return challenger;
    }
     public boolean isLimitedAttempts() { 
         return limitedAttempts; 
     }
    public int getMaxAttemptsOne() { 
        return maxAttemptsOne;
    }
    public int getMaxAttemptsTwo() { 
        return maxAttemptsTwo;
    }
    public synchronized List<Player> getGuessers() {
        return guessers;
    }
     
 
}