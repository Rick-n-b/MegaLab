package ru.nstu.labs.AI;

import java.io.Serializable;

//extends Thread
public abstract class BaseAI extends Thread implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int fps = 60;
    protected boolean disabled = true;

    public void run(){
    }

    public synchronized void setDisabled(boolean disabled){
        this.disabled = disabled;
        if(!disabled) {
                if (this.isAlive())
                    this.notify();
                else
                    this.start();
        }
    }
    public boolean getDisabled(){
        return this.disabled;
    }

    public void setPrio(int p){
        this.setPriority(p);
    }

    public int getPrio(){
        return  this.getPriority();
    }
}