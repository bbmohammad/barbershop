import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class BarberShop {

    static Semaphore max_capacity = new Semaphore(20);
    static Semaphore sofa = new Semaphore(4);
    static Semaphore barber_chair = new Semaphore(3);
    static Semaphore coord = new Semaphore(3);
    static Semaphore mutex1 = new Semaphore(1);
    static Semaphore mutex2 = new Semaphore(1);
    static Semaphore cust_ready = new Semaphore(0);
    static Semaphore leave_barber_chair = new Semaphore(0);
    static Semaphore payment = new Semaphore(0);
    static Semaphore receipt = new Semaphore(0);
    static Semaphore finished[] = new Semaphore[50];
    static Queue<Integer> queue=new LinkedList<>();
    static int count;


    class Customer extends Thread{
        private int number;

        public int getNumber() {
            return number;
        }

        public Customer() throws InterruptedException {
            mutex1.acquire();
            this.number = count;
            count++;
            mutex1.release();
        }

        @Override
        public void run() {

            try {
                max_capacity.acquire();
                delay();// eneter shop
                System.out.println(getNumber()+"entered shop");
                sofa.acquire();
                delay();//sit on sofa
                barber_chair.acquire();
                delay();//get up from sofa
                sofa.release();
                delay();//sit in barber chair
                mutex2.acquire();
                queue.add(getNumber());
                cust_ready.release();
                mutex2.acquire();
                finished[getNumber()].acquire();
                delay();//leave barber chair
                leave_barber_chair.release();
                delay();//pay
                payment.release();
                receipt.acquire();
                delay();//exit from shop
                max_capacity.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public void delay(){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        count=0;
        // initialize finished semaphor
        for (int i=0;i<50;i++)
            finished[i]=new Semaphore(0);

    }
}
