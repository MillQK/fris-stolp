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

        System.out.println("Start making distance matrix");
        int count = elements.size();
        distanceMatrix = new double[count][count];
        for (int i = 0; i < count; i++) {
            FElement elem = elements.get(i);
            distanceMatrix[i][i] = 0;
            for (int j = i+1; j < count; j++) {
                distanceMatrix[i][j] = distanceMatrix[j][i] = distance.calculate(elem, elements.get(j));
            }
        }
        System.out.println("Finish making distance matrix");
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
        int stolpsCount = 0;
        int count = 0;
        int size = classes.size();
        StringBuilder sb = new StringBuilder();

        //dist to nearest item, class no matter
        double[] nearestElem = new double[otherItems.size()];
        for (String className : classes.keySet()) {
            for (FElement element : classes.get(className)) {
                double minDist = Double.POSITIVE_INFINITY;

                for (FElement el : classes.get(className)) {
                    if (element.index == el.index)
                        continue;

                    if (distanceMatrix[element.index][el.index] < minDist) {
                        minDist = distanceMatrix[element.index][el.index];
                    }
                }

                nearestElem[element.index] = minDist;
            }
        }

        for (String className : classes.keySet()) {

//            if(count++%(size/10) == 0) {
                System.out.println(sb.append(count++).append(" / ").append(size));
                sb.setLength(0);
//            }

            ArrayList<FElement> currentItems = new ArrayList<>(classes.get(className));
            otherItems.removeAll(currentItems);

            Map<FElement, ArrayList<FElement>> classStolps = new HashMap<>();
//            Map<FElement, Double> elementsQual = new HashMap<>();

            while (currentItems.size() > 0) {

                System.out.println(sb.append("Current Items size: ").append(currentItems.size()));
                sb.setLength(0);

                double maxStolpQual = Double.NEGATIVE_INFINITY;
                FElement stolpCandidate = currentItems.get(0);
                ArrayList<FElement> stolpCandidatesObjects = new ArrayList<>();

                //ai
                for (FElement currentItem : currentItems) {

                    ArrayList<FElement> currentStolpObjects = new ArrayList<>();

                    // current item defence
                    double curItemDefence = 0.0;

                    //aj
                    for (FElement classItem : currentItems) {

                        if (classItem.index == currentItem.index)
                            continue;

                        //aj bj
                        double rivalDist = classDistance.calculate(classItem);

                        //ai aj
                        double objectDist = distanceMatrix[currentItem.index][classItem.index];
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

                        //ai bn
                        double rivalDist = distanceMatrix[currentItem.index][otherClassItem.index];
                        //bn bn'
                        double minDistance = Math.min(classDistance.calculateDistWOClass(otherClassItem, className),
                                nearestElem[otherClassItem.index]);

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
//                    elementsQual.put(currentItem, stolpQual);

                    if (stolpQual > maxStolpQual) {
                        maxStolpQual = stolpQual;
                        stolpCandidate = currentItem;
                        stolpCandidatesObjects = currentStolpObjects;

                    }


                }

                classStolps.put(stolpCandidate, stolpCandidatesObjects);
                stolpsCount++;

                currentItems.remove(stolpCandidate);
                currentItems.removeAll(stolpCandidatesObjects);

//                elementsQual.remove(stolpCandidate);
//                for (FElement el : stolpCandidatesObjects) {
//                    elementsQual.remove(el);
//                }


            }

            stolps.put(className, classStolps);
            otherItems.addAll(classes.get(className));

        }

        int c = 0;
        for (Map.Entry<String, Map<FElement, ArrayList<FElement>>> me : stolps.entrySet()) {
            c += me.getValue().keySet().size();
        }

        System.out.println(sb.append("Stolps: ").append(stolpsCount).append(" / ").append(elements.size()).append("  " + c));
        System.out.println(FrisCompact.calculate(classes, stolps, distanceMatrix));
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
