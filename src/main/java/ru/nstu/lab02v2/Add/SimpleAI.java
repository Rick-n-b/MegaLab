package ru.nstu.lab02v2.Add;

public interface SimpleAI {
    public void setLocation(double x, double y);
    public void setRandomLocation();
    public void setRandomLocationWithBounds(double maxX, double maxY);
}
