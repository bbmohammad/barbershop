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
    static Semaphore cust_ready = new Semaphore(1);
    static Semaphore leave_barber_chair = new Semaphore(1);
    static Semaphore payment = new Semaphore(1);
    static Semaphore receipt = new Semaphore(1);
    static Semaphore finished[] = new Semaphore[50];
    static Queue<Integer> queue=new LinkedList<>();
    static int count=0;


    static class Customer extends Thread{
        private int custnr;

        public int getCustomerNumber() {
            return custnr;
        }

        public Customer()  {
            for(int i=0;i<finished.length;i++){
                finished[i]=new Semaphore(1);
            }
        }

        @Override
        public void run() {

            try {
                max_capacity.acquire();
                delay();
                System.out.println("new customer comes in");
                mutex1.acquire();
                custnr=count;
                count++;
                mutex1.release();
                sofa.acquire();
                delay(); // customers sits on sofa
                barber_chair.acquire();
                delay();//customer gets up from sofa
                sofa.release();
                sit_on_barber_chair();
                mutex2.acquire();
                cust_ready.release();
                mutex2.release();
                System.out.println(String.format("customer number is %d", getCustomerNumber()));
                finished[custnr].acquire();
                leave_barber_chair();
                leave_barber_chair.release();
                pay();
                payment.release();
                receipt.acquire();
                exit_shop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        private void pay() {
            cashier();
            delay();
            System.out.println(String.format("customer %d payed his payment", getCustomerNumber()));

        }
        private void cashier(){
            while (true){
                try {
                    payment.acquire();
                    coord.acquire();
                    System.out.println(String.format("payment from %d accepted" , getCustomerNumber() ));
                    coord.release();
                    receipt.release();
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void exit_shop() {
            receipt.release();
            System.out.println(String.format("customer %d has left the shop",getCustomerNumber() ));
        }


        private void leave_barber_chair() {
                leave_barber_chair.release();
        }

        private void sit_on_barber_chair() {
            queue.add(getCustomerNumber());
            barber();
            System.out.println(String.format("customer %d sits on barber chair.",getCustomerNumber()));
            delay();

        }

        private void barber(){
            int b_cust;
                try {
                    cust_ready.acquire();
                    mutex2.acquire();
                    b_cust=queue.remove();
                    mutex2.release();
                    coord.acquire();
                    cut_hair(b_cust);
                    coord.release();
                    finished[b_cust].release();
                    leave_barber_chair.acquire();
                    barber_chair.release();
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

        public void cut_hair(int b_cust){
            try {
                Thread.sleep(2000);
                finished[b_cust].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }








    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
//        while(true){
//            i=input.nextInt();
//            if (i==0) {
//                    Customer c= new Customer();
//                    c.start();
//            }
//        }
        for(int i=0;i<100;i++){
            Customer c= new Customer();
            c.start();
        }
        //count=0;
        // initialize finished semaphor
//        for (int i=0;i<50;i++)
//            finished[i]=new Semaphore(0);


    }
}
