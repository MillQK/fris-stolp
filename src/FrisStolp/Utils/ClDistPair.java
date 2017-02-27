package FrisStolp.Utils;

import FrisStolp.FElement;

/**
 * Created by Nikita on 23.02.17.
 */
public class ClDistPair {

    private String className;
    private double distance;

    public ClDistPair(String className, double distance) {
        this.className = className;
        this.distance = distance;
    }

    public String getClassName() {
        return className;
    }

    public double getDistance() {
        return distance;
    }
}
