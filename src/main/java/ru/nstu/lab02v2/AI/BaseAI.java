package ru.nstu.lab02v2.AI;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
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
}