import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class Time implements Runnable {
    private long startTime;
    private long endTime;
    private long duration;
    private boolean counterIsDone = false;
    private Elevator e;

    public Time(long duration){
        //this.e = e;
        this.duration = duration;
    }

    public void startTime(){
        startTime = System.currentTimeMillis();
    }

    public boolean countCompleted(){
        return counterIsDone;
    }



    public void checkTime(long n) throws InterruptedException {
        startTime();
        System.out.println("timeStarted");
        Thread.sleep(n);
        long currentTime = System.currentTimeMillis() - startTime;
        Random rand = new Random();
        if ((currentTime / 1000) > (10 + rand.nextInt(10))) {
            System.out.println("error");
        }
        System.out.println("timeEnded");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counterIsDone = true;
    }


    public static void main(String[] args) throws InterruptedException, SocketException {

        ArrayList<Integer> l = new ArrayList();
        for(int i = 0; i<5; i++){
            l.add(i);
        }
        Collections.reverse(l);
        Iterator<Integer> iter = l.iterator();
        while(iter.hasNext()) {
            int f = iter.next();
            System.out.println(f);
            if(f == 1){return;}
        }
    }


}
