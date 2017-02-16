package FrisStolp.ClassDistances;

import FrisStolp.Distances.Distance;
import FrisStolp.FElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Nikita on 24.01.17.
 */
public interface ClassDistance {

    double calculate(Distance dist, FElement elem, Collection<ArrayList<FElement>> elems, double[][] distanceMatrix);

    double calculate(Distance dist, FElement elem, ArrayList<FElement> elems, double[][] distanceMatrix);

    double calculate(FElement element);

    void makeNearElemDistances(Map<String, ArrayList<FElement>> classes, double[][] distanceMatrix);

    void makeNearWOClElemDistances(Map<String, ArrayList<FElement>> classes, double[][] distanceMatrix);

    double calculateDistWOClass(FElement elem, String className);

}
