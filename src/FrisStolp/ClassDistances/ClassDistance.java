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

    double calculate(Distance dist, FElement elem, ArrayList<FElement> elems);

    double calculate(FElement elem, ArrayList<FElement> elems, double[][] distanceMatrix);

    double calculate(FElement element);

    void makeNearElemDistances(Map<String, ArrayList<FElement>> classes);

    void makeNearWOClElemDistances(Map<String, ArrayList<FElement>> classes);

    double calculateDistWOClass(FElement elem, String className);

    void setRecognElement(FElement element, Map<String, ArrayList<FElement>> classes,
                          ArrayList<FElement> elements, Distance distance);

    double getDistanceToClass(String className);

    double getDistanceWOClass(String className);

}
