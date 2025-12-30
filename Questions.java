
public class Questions {
    String Q ;
    String ans ="" ;
    int cnt ;
    Questions(String Q){
        this.Q = Q;
    }
    String getQ(){
        return Q;
    }
    synchronized String getAns() throws InterruptedException{

            while (ans.isEmpty()) {
            wait(1000);
        }
        return ans;
    }
    synchronized int getCnt(){
        return cnt;
    }
    synchronized void setAns (String ans)throws InterruptedException{

        while(ans.isEmpty())
        {
            wait(1000);
        }
        this.ans = ans;
    }


    synchronized void updateCnt (){
        cnt++;
    }
    
    
}
