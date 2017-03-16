package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.Distances.Distance;
import FrisStolp.Utils.DistanceMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 16.02.17.
 */
public class ParallelStolps extends Thread {

    Map<String, ArrayList<FElement>> classes;
    String className;
    private Distance distance;
    private ClassDistance classDistance;
    private double frStolpThr = 0.5;
    private FrisStolp frisStolp;

    public ParallelStolps(Map<String, ArrayList<FElement>> cl, Distance d, ClassDistance cd, double fst, FrisStolp fs) {
        classes = cl;
        distance = d;
        classDistance = cd;
        frStolpThr = fst;
        frisStolp = fs;
    }

    public void setClassName(String cn) {
        className=cn;
    }

    public void run(){

        System.out.println("Start: " + className);

        ArrayList<FElement> otherItems = new ArrayList<>();
        for (String clName : classes.keySet()) {
            otherItems.addAll(classes.get(clName));
        }

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
                    double objectDist = DistanceMatrix.getDistance(currentItem, classItem);
                    double frisRes = frisStolp.frisFunc(objectDist, rivalDist);

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

                    double rivalDist = DistanceMatrix.getDistance(currentItem, otherClassItem);
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

                    double frisRes = frisStolp.frisFunc(minDistance, rivalDist);

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

        frisStolp.stolps.put(className, classStolps);
        System.out.println("Finish: " + className);

    }

}
