package ru.nstu.lab02v2.AI;

import ru.nstu.lab02v2.Add.ToadSpawner;
import ru.nstu.lab02v2.Entities.CarJaba;

import java.util.Random;

public class CarAI extends BaseAI {
    ToadSpawner toadSpawner = ToadSpawner.getInstance();
    private class Move implements Runnable{
        @Override
        public void run(){

            while (true) {
                if(disabled) {
                    synchronized (moveThread) {
                        try {
                            moveThread.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else {
                    synchronized (toadSpawner.getCars()) {
                        for (CarJaba carJaba : toadSpawner.getCars()) {
                            if (!carJaba.isDirectionSet) {
                                carJaba.isDirectionSet = true;
                                carJaba.setVelocity(carJaba.getVelocity() * (new Random().nextBoolean() ? -1 : 1));
                            }

                            carJaba.setY(carJaba.getY() + carJaba.getVelocity());
                        }
                    }
                    try {
                        Thread.sleep(1000 / fps);
                        //Thread.yield();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    public CarAI(){
        moveThread = new Thread(new Move(), "CarMove!");
        //moveThread.start();
    }
}
