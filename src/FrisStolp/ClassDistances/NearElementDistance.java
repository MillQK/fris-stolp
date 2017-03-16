package FrisStolp.ClassDistances;

import FrisStolp.Distances.Distance;
import FrisStolp.FElement;
import FrisStolp.Utils.ClDistPair;
import FrisStolp.Utils.DistanceMatrix;
import FrisStolp.Utils.NearElmsPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 25.01.17.
 */
public class NearElementDistance implements ClassDistance{

    // distance to near element from other class
//    private Map<FElement, Double> distToNearEl;

    // distance to near element without every class
    private Map<FElement, Map<String, Double>>  distToNearWOClass;

    private Map<FElement, NearElmsPair> distToNearElem;

    public double calculate(Distance dist, FElement element, ArrayList<FElement> elems) {

        double minDist = Double.POSITIVE_INFINITY;

        for (FElement el : elems) {
            double dis = dist.calculate(element, el);
            if(dis < minDist) {
                minDist = dis;
            }
        }

        return minDist;

    }

    public double calculate(FElement elem, Collection<ArrayList<FElement>> elems, double[][] distanceMatrix) {

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

    public double calculate(FElement elem, ArrayList<FElement> elems, double[][] distanceMatrix) {

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
    public void makeNearElemDistances(Map<String, ArrayList<FElement>> classes) {

        System.out.println("Start making map with distances to two nearest elements");

        distToNearElem = new HashMap<>();

        for (String className : classes.keySet()) {

            for (FElement element : classes.get(className)) {

                double fMin = Double.POSITIVE_INFINITY, sMin = Double.POSITIVE_INFINITY;
                String fMinClass = "fm", sMinClass = "sm";

                for (String cl: classes.keySet()) {
                    for (FElement el : classes.get(cl)) {

                        if (className.equals(cl))
                            continue;

                        double dist = DistanceMatrix.getDistance(element, el);

                        if (dist < fMin) {
                            if (!className.equals(fMinClass)) {
                                sMin = fMin;
                                sMinClass = fMinClass;
                            }
                            fMin = dist;
                            fMinClass = cl;
                        } else {
                            if (dist < sMin && !cl.equals(fMinClass)) {
                                sMin = dist;
                                sMinClass = cl;
                            }
                        }

                    }
                }

                distToNearElem.put(element, new NearElmsPair(new ClDistPair(fMinClass, fMin),
                        new ClDistPair(sMinClass, sMin)));

            }


        }
        System.out.println("Finish making map with distances to two nearest elements");

    }

    //distance to near element from other class
    public double calculate(FElement element) {

        return distToNearElem.get(element).getFirst().getDistance();

    }

    public void makeNearWOClElemDistances(Map<String, ArrayList<FElement>> classes) {


        System.out.println("Start making nearest element distance map WO classes");
        distToNearWOClass = new HashMap<>();
        ArrayList<Integer> indexes = new ArrayList<>();

        for (ArrayList<FElement> list : classes.values()) {
            for (FElement elem : list) {
                distToNearWOClass.put(elem, new HashMap<>());
            }
        }

        int size = 0;

        for (ArrayList<FElement> elements : classes.values()) {
            size += elements.size();
        }

        for (String outClass : classes.keySet()) {

//            System.out.println(outClass);

            for (FElement element : classes.get(outClass)) {

                indexes.add(element.index);

            }

            for (String key : classes.keySet()) {

                if (key.equals(outClass))
                    continue;

                for (FElement element : classes.get(key)) {

                    double dist = Double.MAX_VALUE;

                    for (int i = 0; i < size; i++) {

                        double d = DistanceMatrix.getDistance(element.index, i);

                        if (d < dist && !indexes.contains(i) && element.index != i) {
                            dist = d;
                        }

                    }

                    distToNearWOClass.get(element).put(outClass, dist);

                }

            }

            indexes.clear();

        }

        System.out.println("Finish making nearest element distance map WO classes");
    }

    public double calculateDistWOClass(FElement elem, String className) {

        NearElmsPair nearElmsPair = distToNearElem.get(elem);

        if (nearElmsPair.getFirst().getClassName().equals(className)) {
            return nearElmsPair.getSecond().getDistance();
        }
        return nearElmsPair.getFirst().getDistance();

    }

    private FElement element;
    private double fMin;
    private String fMinClass;
    private double sMin;
    private Map<String, ArrayList<FElement>> classes;
    private Distance dist;

    @Override
    public void setRecognElement(FElement element, Map<String, ArrayList<FElement>> classes,
                                 ArrayList<FElement> elements, Distance distance) {

        this.element = element;

        double[] distances = new double[elements.size()];
        for (FElement el: elements) {
            distances[el.index] = distance.calculate(element, el);
        }

        double fMin = Double.POSITIVE_INFINITY, sMin = Double.POSITIVE_INFINITY;
        String fMinClass = "fm";

        for (String cl: classes.keySet()) {
            for (FElement el: classes.get(cl)) {

                if (distances[el.index] < fMin) {
                    if (!cl.equals(fMinClass)) {
                        sMin = fMin;
//                        sMinClass = fMinClass;
                    }
                    fMin = distances[el.index];
                    fMinClass = cl;
                } else {
                    if (distances[el.index] < sMin && !cl.equals(fMinClass)) {
                        sMin = distances[el.index];
//                        sMinClass = cl;
                    }
                }

            }
        }

        this.fMin = fMin;
        this.fMinClass = fMinClass;
        this.sMin = sMin;
//        this.sMinClass = sMinClass;

        this.classes = classes;
        this.dist = distance;
    }

    @Override
    public double getDistanceToClass(String className) {
        return calculate(dist, element, classes.get(className)); // TODO think about optimezed version
    }

    @Override
    public double getDistanceWOClass(String className) {
        if (fMinClass.equals(className)) {
            return sMin;
        }

        return fMin;
    }
}
