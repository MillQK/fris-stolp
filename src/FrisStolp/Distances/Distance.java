package FrisStolp.Distances;

import FrisStolp.FElement;

/**
 * Created by Nikita on 24.01.17.
 */
public interface Distance {

    double calculate(FElement first, FElement second);

    double calculate(FElement first, FElement second, double[][] distanceMatrix);

}
