package FrisStolp.Distances;


import FrisStolp.FElement;

/**
 * Created by Nikita on 24.01.17.
 */
public class EuclideanDist implements Distance {

    public double calculate(FElement first, FElement second) throws IllegalArgumentException {

        if(first.vector.size() != second.vector.size()) {
            throw new IllegalAccessError("Wrong vectors sizes");
        }

        double dist = 0.0;
        for (int i = 0; i < first.vector.size(); i++) {
            double sub = first.vector.get(i) - second.vector.get(i);
            dist += sub*sub;
        }

        return Math.sqrt(dist);

    }

    public double calculate(FElement first, FElement second, double[][] distanceMatrix) {

        return distanceMatrix[first.index][second.index];

    }

}
