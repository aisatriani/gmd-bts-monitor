/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ais.gmdapp.bts;

import com.ais.gmdapp.bts.items.Radio;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Asus
 */
public class StartServer {

    private static List<InetSocketAddress> servers
            = new ArrayList<InetSocketAddress>();
    
    private static List<Radio> listUp = new ArrayList<Radio>();
    private static List<Radio> listDown = new ArrayList<Radio>();

    public static void main(String[] args)
            throws InterruptedException, ExecutionException {
        String[] domains = new String[]{
            "3Com.com",
            // etc. etc.
            "Kai.com"
        };
        
        List<Radio> radio = new ArrayList<Radio>();
        radio.add(new Radio("442nonMax", "192.168.37.85", "442nonMax", "Potanga"));
        radio.add(new Radio("442Max", "192.168.37.84", "442Max", "Potanga"));
        radio.add(new Radio("AP BONBOL", "192.168.39.4", "bonbol", "Potanga"));
        radio.add(new Radio("AP TAPA", "192.168.39.8", "aptapa", "Tapa"));
        radio.add(new Radio("AP Himana", "192.168.48.58", "aphimana", "Himana Atingola"));

        for (Radio hostname : radio) {
            try {
                servers.add(
                        new InetSocketAddress(
                                InetAddress.getByName(hostname.getIp()),
                                80
                        )
                );
            } catch (UnknownHostException e) {
                System.out.println("Unknown host: " + hostname.getIp());
            }
        }

        System.out.println(
                "Total number of target servers: " + servers.size()
        );

        BlockingQueue<Runnable> work
                = new ArrayBlockingQueue<Runnable>(5);

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                5, // corePoolSize
                10, // maximumPoolSize
                5000, // keepAliveTime
                TimeUnit.MILLISECONDS, // TimeUnit
                work // workQueue
        );

        pool.prestartAllCoreThreads();

        pool.setRejectedExecutionHandler(
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(
                            Runnable r,
                            ThreadPoolExecutor executor
                    ) {
                        System.out.println("Work queue is currently full");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ignore) {

                        }
                        executor.submit(r);
                    }
                }
        );

        Collection<Future<?>> futures = new LinkedList<Future<?>>();

        while (true) {
            for (InetSocketAddress server : servers) {
                futures.add(pool.submit(new PingWork(server)));
            }
            for (Future<?> future : futures) {
                future.get();
            }
            System.out.println(
                    "All servers checked. Will wait for 10 seconds until next round"
            );
            
            System.out.println("total host yg up :"+listUp.size());
            System.out.println("total host yg down :"+listDown.size());
            
            listUp.clear();
            listDown.clear();
            Thread.sleep(10000);
        }

    }

    private static class PingWork implements Runnable {

        private static final int TIMEOUT = 5000;
        private InetSocketAddress target;
        private Radio radio;

        private PingWork(InetSocketAddress target) {
            this.target = target;
        }
        
        private PingWork(Radio radio){
            this.target = new InetSocketAddress(radio.getIp(), 80);
            this.radio = radio;
        }

        @Override
        public void run() {
            Socket connection = new Socket();
            boolean reachable;

            try {
                try {
                    connection.connect(target, TIMEOUT);
                } finally {
                    connection.close();
                }
                reachable = true;
                listUp.add(radio);
            } catch (Exception e) {
                reachable = false;
            }

            if (!reachable) {
                System.out.println(
                        String.format(
                                "%s:%d was UNREACHABLE",
                                target.getAddress(),
                                target.getPort()
                        )
                );
                listDown.add(radio);
            }
        }
    }

}
