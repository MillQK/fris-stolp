package FrisStolp.ClassDistances;

import FrisStolp.Distances.Distance;
import FrisStolp.FElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 25.01.17.
 */
public class NearElementDistance implements ClassDistance{

    // distance to near element from other class
    private Map<FElement, Double> distToNearEl;

    // distance to near element without every class
    private Map<FElement, Map<String, Double>>  distToNearWOClass;

    public double calculate(Distance dist, FElement elem, Collection<ArrayList<FElement>> elems, double[][] distanceMatrix) {

        double minDist = Double.MAX_VALUE;

        for (ArrayList<FElement> classElems : elems) {
            for (FElement classElem : classElems) {
                double dis = distanceMatrix[elem.index][classElem.index];
                if(dis < minDist && elem.index != classElem.index) {
                    minDist = dis;
                }
            }
        }

        return minDist;

    }

    public double calculate(Distance dist, FElement elem, ArrayList<FElement> elems, double[][] distanceMatrix) {

        double minDist = Double.MAX_VALUE;

        for (FElement classElem : elems) {
            double dis = distanceMatrix[elem.index][classElem.index];
            if(dis < minDist && elem.index != classElem.index) {
                minDist = dis;
            }
        }

        return minDist;

    }

    // distance to nearest element from other classes
    public void makeNearElemDistances(Map<String, ArrayList<FElement>> classes, double[][] distanceMatrix) {

        distToNearEl = new HashMap<>();
        ArrayList<Integer> indexes = new ArrayList<>();

        for (String key : classes.keySet()) {

            System.out.println(key);

            for (FElement element : classes.get(key)) {

                indexes.add(element.index);

            }

            for (FElement element : classes.get(key)) {

                double dist = Double.MAX_VALUE;

                for (int i = 0; i < distanceMatrix[element.index].length; i++) {

                    if (distanceMatrix[element.index][i] < dist && !indexes.contains(i)) {
                        dist = distanceMatrix[element.index][i];
                    }

                }

                distToNearEl.put(element, dist);

            }

            indexes.clear();

        }

    }

    public double calculate(FElement element) {

        return distToNearEl.get(element);

    }

    public void makeNearWOClElemDistances(Map<String, ArrayList<FElement>> classes, double[][] distanceMatrix) {

        distToNearWOClass = new HashMap<>();
        ArrayList<Integer> indexes = new ArrayList<>();

        for (ArrayList<FElement> list : classes.values()) {
            for (FElement elem : list) {
                distToNearWOClass.put(elem, new HashMap<>());
            }
        }

        for (String outClass : classes.keySet()) {

            System.out.println(outClass);

            for (FElement element : classes.get(outClass)) {

                indexes.add(element.index);

            }

            for (String key : classes.keySet()) {

                if (key.equals(outClass))
                    continue;

                for (FElement element : classes.get(key)) {

                    double dist = Double.MAX_VALUE;

                    for (int i = 0; i < distanceMatrix[element.index].length; i++) {

                        if (distanceMatrix[element.index][i] < dist && !indexes.contains(i) && element.index != i) {
                            dist = distanceMatrix[element.index][i];
                        }

                    }

                    distToNearWOClass.get(element).put(outClass, dist);

                }

            }

            indexes.clear();

        }

    }

    public double calculateDistWOClass(FElement elem, String className) {

        return distToNearWOClass.get(elem).get(className);

    }


}
