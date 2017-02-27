package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.Distances.Distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 17.02.17.
 */
public class Fris {

    private Map<String, ArrayList<FElement>> classes;
    ArrayList<FElement> elements;
    private Distance distance;
    private ClassDistance classDistance;
    private double[][] distanceMatrix;

    public Fris(Distance dist, ClassDistance classDist, Map<String, ArrayList<FElement>> classes) {

        distance = dist;
        classDistance = classDist;
        this.classes = classes;

    }

    public Fris(Distance dist, ClassDistance classDist, Map<String, ArrayList<FElement>> classes,
                ArrayList<FElement> elements) {

        distance = dist;
        classDistance = classDist;
        this.classes = classes;
        this.elements = elements;

    }

    public void makeDistanceMatrix() {

        int count = elements.size();
        distanceMatrix = new double[count][count];
        for (int i = 0; i < count; i++) {
            if(i%1000 == 0){
                System.out.println(i + "/" + count);
            }
            FElement elem = elements.get(i);
            distanceMatrix[i][i] = 0;
            for (int j = i+1; j < count; j++) {
                distanceMatrix[i][j] = distanceMatrix[j][i] = distance.calculate(elem, elements.get(j));
            }
        }

    }

    public double frisFunc(double toObject, double toRival) {

        return (toRival - toObject)/(toRival + toObject);

    }

    public void makeNearDistances() {

        classDistance.makeNearElemDistances(classes, distanceMatrix);

    }

    public void makeNearWOClassDistances() {

        classDistance.makeNearWOClElemDistances(classes, distanceMatrix);

    }

    public ArrayList<String> recognize(ArrayList<FElement> elements) {

        ArrayList<String> results = new ArrayList<>();
        int count = elements.size();
        int current = 0;

        for (FElement element: elements) {
            if(current%100 == 0) {
                System.out.println(current + "/" + count);
            }

            results.add(findElementClass(element));
            current++;
        }

        return results;

    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public String findElementClass(FElement element) {

        String resultClass = "";
        double resultFris = Double.NEGATIVE_INFINITY;

//        for (String className: classes.keySet()) {
//
//            ArrayList<FElement> classElements = classes.get(className);
//            ArrayList<FElement> otherItems = new ArrayList<>(elements);
//            otherItems.removeAll(classElements);
//
//            double distCI = classDistance.calculate(distance, element, classElements);
//            double distOI = classDistance.calculate(distance, element, otherItems);
//
//            double f = frisFunc(distCI, distOI);
//
//            if(f > resultFris) {
//                resultFris = f;
//                resultClass = className;
//            }
//
//        }

    //        double[] distances = new double[elements.size()];
    //        for (FElement el: elements) {
    //            distances[el.index] = distance.calculate(element, el);
    //        }
    //
    //        double fMin = Double.POSITIVE_INFINITY, sMin = Double.POSITIVE_INFINITY;
    //        String fMinClass = "fm", sMinClass = "sm";
    //
    //        for (String cl: classes.keySet()) {
    //            for (FElement el: classes.get(cl)) {
    //
    //                if (distances[el.index] < fMin) {
    //                    if (!cl.equals(fMinClass)) {
    //                        sMin = fMin;
    //                        sMinClass = fMinClass;
    //                    }
    //                    fMin = distances[el.index];
    //                    fMinClass = cl;
    //                } else {
    //                    if (distances[el.index] < sMin && !cl.equals(fMinClass)) {
    //                        sMin = distances[el.index];
    //                        sMinClass = cl;
    //                    }
    //                }
    //
    //            }
    //        }





//        Map<String, Double> distToNearWOClass = new HashMap<>();
//        ArrayList<Integer> indexes = new ArrayList<>();
//
//        for (String outClass : classes.keySet()) {
//
//            classes.get(outClass).forEach(el -> indexes.add(el.index));
//
//
//            double dist = Double.POSITIVE_INFINITY;
//
//            for (int i = 0; i < distances.length; i++) {
//
//                if (distances[i] < dist && !indexes.contains(i)) {
//                    dist = distances[i];
//                }
//
//            }
//
//            distToNearWOClass.put(outClass, dist);
//
//
//            indexes.clear();
//
//        }

        classDistance.setRecognElement(element, classes, elements, distance);

        for (String className: classes.keySet()) {

            ArrayList<FElement> classElements = classes.get(className);

            double distCI = classDistance.calculate(distance, element, classElements);
            double distOI = classDistance.getDistanceWOClass(className);

            double f = frisFunc(distCI, distOI);

            if(f > resultFris) {
                resultFris = f;
                resultClass = className;
            }

        }


        return resultClass;

    }

}
