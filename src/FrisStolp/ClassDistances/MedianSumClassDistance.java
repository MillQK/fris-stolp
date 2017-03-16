package FrisStolp.ClassDistances;

import FrisStolp.Distances.Distance;
import FrisStolp.FElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 21.02.17.
 */
public class MedianSumClassDistance implements ClassDistance {


    @Override
    public double calculate(Distance dist, FElement elem, ArrayList<FElement> elems) {
        double distance = 0.0;
        for (FElement el : elems) {
            distance += dist.calculate(elem, el);
        }
        return distance/elems.size();
    }

    @Override
    public double calculate(FElement elem, ArrayList<FElement> elems, double[][] distanceMatrix) {
        double distance = 0.0;
        for (FElement el : elems) {
            distance += distanceMatrix[el.index][elem.index];
        }
        return distance/elems.size();
    }

    @Override
    public double calculate(FElement element) {
        return 0;
    }

    @Override
    public void makeNearElemDistances(Map<String, ArrayList<FElement>> classes) {

    }

    @Override
    public void makeNearWOClElemDistances(Map<String, ArrayList<FElement>> classes) {

    }

    @Override
    public double calculateDistWOClass(FElement elem, String className) {
        return 0;
    }

    private FElement element;
    // for each class sum of distances between element and class elements
    private Map<String, Double> distances;
    private double sumDist;
    private int classCount;

    @Override
    public void setRecognElement(FElement element, Map<String, ArrayList<FElement>> classes,
                           ArrayList<FElement> elements, Distance distance) {
        this.element = element;

        distances = new HashMap<>();
        double sumDist = 0.0;

        for (String className: classes.keySet()) {
            double classDist = 0.0;
            for (FElement el : classes.get(className)) {
                classDist += distance.calculate(element, el);
            }
            classDist /= classes.get(className).size();
            sumDist += classDist;
            distances.put(className, classDist);
        }

        this.sumDist = sumDist;
        this.classCount = classes.keySet().size();

    }

    @Override
    public double getDistanceToClass(String className) {
        return distances.get(className);
    }

    @Override
    public double getDistanceWOClass(String className) {

        return (sumDist-distances.get(className))/(classCount-1);

    }

}
