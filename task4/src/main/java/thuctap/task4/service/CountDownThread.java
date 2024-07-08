//package thuctap.task4.service;
//
////package thuctap.task4.service;
////
////import java.util.concurrent.ExecutorService;
////import java.util.concurrent.Executors;
////
////public class MyRunnable implements Runnable {
////
////    private String name;
////
////    public MyRunnable(String name) {
////        this.name = name;
////    }
////
////    @Override
////    public void run() {
////        System.out.println(name + " đang thực thi...");
////
////        try {
////            Thread.sleep(2000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////
////        System.out.println(name + " kết thúc.");
////    }
////
////    public static void main(String[] args) {
////
////        ExecutorService executorService = Executors.newFixedThreadPool(10);
////
////        for (int i = 1; i <= 10; i++) {
////            MyRunnable myRunnable = new MyRunnable("Runnable " + i);
////            executorService.execute(myRunnable);
////        }
////
////        executorService.shutdown();
////    }
////}
//
////cach1
////public class CountDownThread extends Thread {
////    public static void main(String[] args) {
////        CountDownThread countDownThread1 = new CountDownThread();
////        CountDownThread countDownThread2=new CountDownThread();
////        countDownThread1.start();
////        countDownThread2.start();
////    }
////    @Override
////    public void run() {
////        int count = 10;
////        for (int i = count; i > 0; i--) {
////            System.out.println(getName() +"đếm "+i);
////            try {
////                Thread.sleep(1000);
////            } catch (InterruptedException e) {
////                // TODO Auto-generated catch block
////                e.printStackTrace();
////            }
////        }
////        System.out.println("Hết giờ");
////    }
////}
////cach 2
//public class CountDownThread implements Runnable{
//    public static void main(String[] args) {
//        CountDownThread countDownThread=new CountDownThread();
//        Thread thread=new Thread(countDownThread);
//        thread.start();
//    }
//    @Override
//    public void run() {
//        for(int i=10;i>0;i--) {
//            System.out.println(i);
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        System.out.println("Het gio");
//    }
//}