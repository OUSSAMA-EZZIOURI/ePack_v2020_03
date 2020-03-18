package samples;

import java.util.Date;

/**
 *
 * @author Oussama
 */
public class ExitFewMiliseconds {

    public static void main(String args[]) {
        System.out.println("Program started "+new Date());
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Program terminated "+new Date());
                System.exit(0);
            }
        }).start();

        while (true) {
            //System.out.println("I'm doing something");
        }
    }
}
