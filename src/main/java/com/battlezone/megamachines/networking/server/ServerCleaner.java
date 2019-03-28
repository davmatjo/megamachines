package com.battlezone.megamachines.networking.server;

public class ServerCleaner implements Runnable {

    private boolean running = true;

    /*
    * Main constructor of the cleaner.
    * */
    public ServerCleaner() {
    }

    /*
    * Method to close the Thread.
    * */
    public void close() {
        this.running = false;
    }

    /*
    * Method to run while the Thread is running.
    * */
    @Override
    public void run() {
        while (running) {
//            Server.clean();
        }
    }
}
