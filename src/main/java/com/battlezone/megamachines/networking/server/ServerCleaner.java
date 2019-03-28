package com.battlezone.megamachines.networking.server;

public class ServerCleaner implements Runnable {

    private boolean running = true;

    public ServerCleaner() {
    }

    public void close() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
//            Server.clean();
        }
    }
}
