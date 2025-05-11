package ru.nstu.lab02v2.AI;

import ru.nstu.lab02v2.Add.ToadSpawner;
import ru.nstu.lab02v2.Entities.MotoJaba;

import java.util.Random;

public class MotoAI extends BaseAI {


    public void run() {
        ToadSpawner toadSpawner = ToadSpawner.getInstance();
        this.setName("MotoMove!");
        while (true) {
            if(disabled) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                synchronized (toadSpawner.getMotos()) {
                    for (MotoJaba motoJaba : toadSpawner.getMotos()) {
                        if (!motoJaba.isDirectionSet) {
                            motoJaba.isDirectionSet = true;
                            motoJaba.setVelocity(motoJaba.getVelocity() * (new Random().nextBoolean() ? -1 : 1));
                        }

                        motoJaba.setX(motoJaba.getX() + motoJaba.getVelocity());
                    }
                }
                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
