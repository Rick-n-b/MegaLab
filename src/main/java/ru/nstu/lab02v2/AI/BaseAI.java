package ru.nstu.lab02v2.AI;

import javafx.application.Platform;
import ru.nstu.lab02v2.Add.Entity;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
//extends Thread
public abstract class BaseAI {

    protected int fps = 60;
    protected boolean disabled = true;

    synchronized public void start(){
        if(disabled) {
            moveThread.setDaemon(true);
            moveThread.start();
        }
    }
    synchronized public void stop(){
        if(!disabled){
            try {
                moveThread.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    protected Thread moveThread;

    public void setDisabled(boolean disabled){
        this.disabled = disabled;
        if(!disabled) {
            synchronized (moveThread) {
                if (moveThread.isAlive())
                    moveThread.notify();
                else
                    moveThread.start();
            }
        }
    }
    public boolean getDisabled(){
        return this.disabled;
    }

    public void setPrio(int p){
        moveThread.setPriority(p);
    }

    public int getPrio(){
        return  moveThread.getPriority();
    }
    // = new Thread(new MoveRunnable(), "move")
}



//protected class MoveRunnable implements Runnable{
//    int type = 1;
//    int direction = 1;
//    public MoveRunnable(int type){
//        this.type = type;
//        this.direction = new Random().nextBoolean() ? 1 : -1;
//    }
//
//    @Override
//    public synchronized void run() {
//        Thread.yield();
////            if (type == 1)//movex
////                while ((x > -sizeX || x < pane.getWidth() - sizeX)) {
////                    //synchronized ("JavaFX Application Thread") {
////                    try {
////                        x += direction * velocity;
////                        view.setX(x);
////                        Thread.sleep(17);
////                    } catch (InterruptedException e) {
////                        System.out.println(Thread.currentThread().getName() + " was interrupted!");
////                    }
////                    // }
////                }
////            else//moveY
////                while ((y < pane.getHeight() + sizeY || y > -sizeY)) {
////                    try {
////                        y += direction * velocity;
////                        view.setY(y);
////                        Thread.sleep(17);
////                    } catch (InterruptedException e) {
////                        System.out.println(Thread.currentThread().getName() + " was interrupted!");
////                    }
////
////                }
//        Thread.currentThread().interrupt();
//    }
//}