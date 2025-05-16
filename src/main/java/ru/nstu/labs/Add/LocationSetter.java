package ru.nstu.labs.Add;

public interface LocationSetter {
    public void setLocation(double x, double y);
    public void setRandomLocation();
    public void setRandomLocationWithBounds(double maxX, double maxY);
}
