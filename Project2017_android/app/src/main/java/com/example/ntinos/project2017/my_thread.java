package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */

public class my_thread implements  Runnable {

    public Thread thread;
    private String threadname;
    boolean paused = false;
    boolean running = true;

    public my_thread(String name) { //constructor
        threadname = name;
    }



    @Override
    public void run() {
        System.out.println("Running " +  threadname );
        try {
            while(running) {

                operate();

                synchronized(this) {
                    while(paused) {
                        wait();
                    }
                }
            }
        }catch (InterruptedException e) {
            System.out.println("Thread " +  threadname + " interrupted.");
        }
        System.out.println("Thread " +  threadname + " exiting.");
    }

    public void start () {
        System.out.println("Starting " +  threadname );
        if (thread == null) {
            thread = new Thread (this, threadname);
            thread.start ();
        }
    }

    void pause() {
        paused = true;
    }

    synchronized void resume() {
        paused = false;
        notify();
    }


    public void operate(){}

    public void stop() {
        running = false;
        thread.interrupt();
    }

}
