package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.Distances.Distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 24.01.17.
 */
public class FrisStolp {

    private Map<String, ArrayList<FElement>> classes;
    ArrayList<FElement> elements;
    private Distance distance;
    private ClassDistance classDistance;
    private double frStolpThr = 0.5;
    private double[][] distanceMatrix;
    Map<String, Map<FElement, ArrayList<FElement>>> stolps;

    public FrisStolp(Distance dist, ClassDistance classDist, double frStThreshold,
                     Map<String, ArrayList<FElement>> classes) {

        distance = dist;
        classDistance = classDist;
        frStolpThr = frStThreshold;
        this.classes = classes;

    }

    public void makeDistanceMatrix() {

        int count = elements.size();
        distanceMatrix = new double[count][count];
        for (int i = 0; i < count; i++) {
            if(i%1000 == 0){
                System.out.println(i);
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

    public void makeStolps() {

        stolps = new HashMap<>();
        ArrayList<FElement> otherItems = new ArrayList<>();
        for (String clName : classes.keySet()) {
            otherItems.addAll(classes.get(clName));
        }

        for (String className : classes.keySet()) {
            System.out.println(className);

            ArrayList<FElement> currentItems = classes.get(className);
            otherItems.removeAll(currentItems);

            Map<FElement, ArrayList<FElement>> classStolps = new HashMap<>();
            Map<FElement, Double> elementsQual = new HashMap<>();

            while (currentItems.size() > 0) {

                double maxStolpQual = Double.NEGATIVE_INFINITY;
                FElement stolpCandidate = currentItems.get(0);
                ArrayList<FElement> stolpCandidatesObjects = new ArrayList<>();

                for (FElement currentItem : currentItems) {

                    ArrayList<FElement> currentStolpObjects = new ArrayList<>();

                    // current item defence
                    double curItemDefence = 0.0;

                    for (FElement classItem : currentItems) {

                        if (classItem.index == currentItem.index)
                            continue;

                        double rivalDist = classDistance.calculate(classItem);
                        double objectDist = distance.calculate(currentItem, classItem, distanceMatrix);
                        double frisRes = frisFunc(objectDist, rivalDist);

                        // F(j,+) from description
                        double fClItem = frisRes - frStolpThr;
                        if (fClItem >= 0.0) {
                            currentStolpObjects.add(classItem);
                            curItemDefence += fClItem;
                        }

                    }

                    // current item tolerance
                    double curItemToler = 0.0;

                    for (FElement otherClassItem : otherItems) {

                        double rivalDist = distance.calculate(currentItem, otherClassItem, distanceMatrix);
                        double minDistance = classDistance.calculateDistWOClass(otherClassItem, className);
//                        double minDistance = Double.POSITIVE_INFINITY;
//
//                        for (FElement element : otherItems) {
//
//                            if (element.index == otherClassItem.index)
//                                continue;
//
//                            double otherDist = distance.calculate(otherClassItem, element, distanceMatrix);
//
//                            if (otherDist < minDistance)
//                                minDistance = otherDist;
//
//                        }

                        double frisRes = frisFunc(minDistance, rivalDist);

                        // F(n,-) from description
                        double fClItem = frisRes - frStolpThr;
                        if (fClItem < 0.0) {
                            curItemToler += fClItem;
                        }


                    }

                    // S(ai)
                    double stolpQual = curItemDefence + curItemToler;
                    elementsQual.put(currentItem, stolpQual);

                    if (stolpQual > maxStolpQual) {
                        maxStolpQual = stolpQual;
                        stolpCandidate = currentItem;
                        stolpCandidatesObjects = currentStolpObjects;

                    }


                }

                classStolps.put(stolpCandidate, stolpCandidatesObjects);

                currentItems.remove(stolpCandidate);
                currentItems.removeAll(stolpCandidatesObjects);

                elementsQual.remove(stolpCandidate);
                for (FElement el : stolpCandidatesObjects) {
                    elementsQual.remove(el);
                }


            }

            stolps.put(className, classStolps);
            otherItems.addAll(classes.get(className));

        }

    }

    public void makeNearDistances() {

        classDistance.makeNearElemDistances(classes, distanceMatrix);

    }

    public void makeNearWOClassDistances() {

        classDistance.makeNearWOClElemDistances(classes, distanceMatrix);

    }

    public ArrayList<String> recognize(ArrayList<FElement> elements) {

        ArrayList<String> recognized = new ArrayList<>();

        for (FElement elem : elements) {

            double minDist = Double.MAX_VALUE;
            String distClass = null;

            for (String className : stolps.keySet()) {
                for (FElement classElem : stolps.get(className).keySet()) {
                    double dist = distance.calculate(elem, classElem);
                    if (dist < minDist) {
                        minDist = dist;
                        distClass = className;
                    }
                }
            }

            recognized.add(distClass);

        }

        return recognized;

    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }
}
