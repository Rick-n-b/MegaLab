package ru.nstu.labs.AI;

import ru.nstu.labs.Add.ToadSpawner;
import ru.nstu.labs.Entities.CarJaba;

import java.util.Random;

public class CarAI extends BaseAI {

    @Override
    public void run() {
        this.setName("CarMove!");
        ToadSpawner toadSpawner = ToadSpawner.getInstance();
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
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
