package com.battlezone.megamachines.networking;

public class ServerCleaner implements Runnable {

    private boolean running = true;
    public ServerCleaner() {
    }

    public void close() {
        this.running = false;
    }

    @Override
    public void run() {
        while ( running ) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Server.clean();
        }
    }
}
