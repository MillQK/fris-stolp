package FrisStolp.Utils;

import FrisStolp.Distances.Distance;
import FrisStolp.FElement;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Nikita on 03.03.17.
 */
public class DistanceMatrix {

    private static double distanceMatrix[][] = null;

    public static void makeDistanceMatrix(ArrayList<FElement> elements, Distance distance) {

        System.out.println("Start making distance matrix");

        int size = elements.size();

        distanceMatrix = new double[size][];

        for (int i = 0; i < size; i++) {
            distanceMatrix[i] = new double[i+1];
            distanceMatrix[i][i] = 0.0;
        }

        for (int i = 0; i < size; i++) {
            FElement elem = elements.get(i);
            for (int j = i+1; j < size; j++) {
                FElement elemj = elements.get(j);
                double dist  = distance.calculate(elem, elemj);
                try {
                    distanceMatrix[elemj.index][elem.index] = dist;
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    distanceMatrix[elem.index][elemj.index] = dist;
                }
            }
        }

        System.out.println("Finish making distance matrix");

    }


    public static double getDistance(int i, int j) {

        try {
            return distanceMatrix[i][j];
        } catch (ArrayIndexOutOfBoundsException exc) {
            return distanceMatrix[j][i];
        }

    }

    public static double getDistance(FElement el1, FElement el2) {

        try {
            return distanceMatrix[el1.index][el2.index];
        } catch (ArrayIndexOutOfBoundsException exc) {
            return distanceMatrix[el2.index][el1.index];
        }
    }

}
