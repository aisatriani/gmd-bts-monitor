/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ais.gmdapp.bts;

import com.ais.gmdapp.bts.items.Radio;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class StartServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic her

        List<Radio> radio = new ArrayList<Radio>();
        final List<Radio> listUp = new ArrayList<Radio>();
        final List<Radio> listDown = new ArrayList<Radio>();

        radio.add(new Radio("puncak02", "192.168.37.28", "puncak02", "lotu"));
        radio.add(new Radio("puncak03", "192.168.37.29", "puncak03", "lotu"));
        radio.add(new Radio("puncak01", "192.168.37.27", "puncak01", "lotu"));

        for (final Radio rad : radio) {
            Timer time = new Timer();
            time.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    Socket socket = null;
                    try {
                        socket = new Socket(rad.getIp(), 80);
                        socket.connect(socket.getRemoteSocketAddress(), 1);
                        if (!socket.isClosed()) {
                            listUp.add(rad);
                            System.out.println(rad.getIp() + " up");
                            socket.close();
                            
                        }
                    } catch (ConnectException exception) {
                        System.out.println(rad.getIp() + " connect timeout");
                        listDown.add(rad);
                    } catch (IOException ex) {
                        //Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            if (socket != null) {
                                socket.close();
                                System.out.println(rad.getIp() + " socket close");
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }, 1000, 5000);
        }

    }

}
