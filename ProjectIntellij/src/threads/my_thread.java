package threads;

public class my_thread implements Runnable {

        public Thread thread;
        protected String threadname;
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
            } catch (Exception e) {
                e.printStackTrace();
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

        public void pause() {
            paused = true;
        }

        synchronized void resume() {
            paused = false;
            notify();
        }


        public void operate() throws Exception {}

        public void stop() {
            running = false;
            thread.interrupt();
        }

}

