package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.Distances.Distance;
import FrisStolp.Utils.ClDistPair;
import FrisStolp.Utils.NearElmsPair;
import FrisStolp.Utils.PairDistStolp;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 24.02.17.
 */
public class FrisCompact {

    private static Map<FElement, PairDistStolp> toStolpDist = null;
    //ver 0 - sum class compact
    //ver 1 - mult class compact
    private static int VERSION = 0;

    public static double calculate(Map<String, ArrayList<FElement>> classes,
                                   Map<String, Map<FElement, ArrayList<FElement>>> stolps,
                                   double[][] distanceMatrix) {

        if (toStolpDist == null) {
            makeDistancesMap(classes, stolps, distanceMatrix);
        }


        double compact = 0.0;

        for (String className : classes.keySet()) {

            double classCompact;
            double frisSum = 0.0;

            for (FElement element : classes.get(className)) {

                PairDistStolp pair = toStolpDist.get(element);

                double fris = (pair.getToOtherClassStolp() - pair.getToOwnClassStolp())/
                        (pair.getToOtherClassStolp() + pair.getToOwnClassStolp());

                if (!Double.isNaN(fris)) {
                    frisSum += fris;
                }

            }

            int stolpCount = stolps.get(className).keySet().size();

            classCompact = (frisSum - stolpCount)/(stolpCount*classes.get(className).size());

            if (VERSION == 0) compact += classCompact;
            if (VERSION == 1) compact *= classCompact;

        }

        if (VERSION == 0) compact /= classes.keySet().size();
        if (VERSION == 1) compact = Math.pow(compact, 1.0 / classes.keySet().size());

        return compact;
    }

    private static void makeDistancesMap(Map<String, ArrayList<FElement>> classes,
                                         Map<String, Map<FElement, ArrayList<FElement>>> stolps,
                                         double[][] distanceMatrix) {

        System.out.println("Start making map with distances to two nearest stolps");

        toStolpDist = new HashMap<>();

        for (String className : classes.keySet()) {

            for (FElement element : classes.get(className)) {

                double distOwn = Double.POSITIVE_INFINITY;

                for (FElement el : stolps.get(className).keySet()) {
                    if (distanceMatrix[element.index][el.index] < distOwn) {
                        distOwn = distanceMatrix[element.index][el.index];
                    }
                }

                double distOther = Double.POSITIVE_INFINITY;

                for (String clName : stolps.keySet()) {

                    if (clName.equals(className)) {
                        continue;
                    }

                    for (FElement el : stolps.get(clName).keySet()) {

                        if (distanceMatrix[element.index][el.index] < distOther) {
                            distOther = distanceMatrix[element.index][el.index];
                        }

                    }
                }

                toStolpDist.put(element, new PairDistStolp(distOwn, distOther));

            }

        }

        System.out.println("Finish making map with distances to two nearest stolps");


    }

    private static double frisFunc(double toObject, double toRival) {

        return (toRival - toObject)/(toRival + toObject);

    }

}
