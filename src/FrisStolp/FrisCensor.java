package FrisStolp;

import FrisStolp.Utils.PairDistStolp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nikita on 27.02.17.
 */
public class FrisCensor {

    public static double calculate(Map<String, ArrayList<FElement>> classes,
                                   Map<String, Map<FElement, ArrayList<FElement>>> stolps) {

        Map<FElement, PairDistStolp> toStolpDist = FrisCompact.getToStolpDist();

        double frisSum = 0.0;

        for (String className: classes.keySet()) {
            for (FElement element: classes.get(className)) {
                PairDistStolp pair = toStolpDist.get(element);
                frisSum += (pair.getToOtherClassStolp() - pair.getToOwnClassStolp()) /
                        (pair.getToOtherClassStolp() + pair.getToOwnClassStolp());
            }
        }

        int sumElemsStolps = 0;

        for (ArrayList<FElement> els: classes.values())
            sumElemsStolps += els.size();

        for (Map<FElement, ArrayList<FElement>> map: stolps.values())
            sumElemsStolps += map.size();

        frisSum /= sumElemsStolps;

        return frisSum;

    }

    //
    public static void findBest(FrisStolp frisStolp, double d) {

        Map<String, ArrayList<FElement>> originalClasses = frisStolp.getClasses();
        ArrayList<FElement> originalElements = frisStolp.getElements();
        Map<String, Map<FElement, ArrayList<FElement>>> originalStolps = frisStolp.getStolps();


        Map<String, ArrayList<FElement>> clsses = new HashMap<>(originalClasses);
        frisStolp.setClasses(clsses);
        ArrayList<FElement> elements = new ArrayList<>(originalElements);
        frisStolp.setElements(elements);
        Map<String, Map<FElement, ArrayList<FElement>>> stolps = new HashMap<>(originalStolps);
        frisStolp.setStolps(stolps);

        Map<String, Map<FElement, ArrayList<FElement>>> bestStolps = new HashMap<>();
        double bestComp = -1.0;


        Map<FElement, String> deletedElementsMap = new HashMap<>();
        ArrayList<FElement> deletedElementsList = new ArrayList<>();


        int size = originalElements.size();
        int count = (int)(d*size);
        double alpha = 5;

        // elements array indexes

        for (int i = 1; i <= count; i++) {

            System.out.println("I = " + i + "  / " + count);

            int[] indxs = new int[i];
            for (int j = 0; (indxs[j] = j) < i - 1; j++);

            //todo check correct
            originalClasses.forEach((s,al) -> {
                for (Integer indx : indxs) {
                    FElement elem = elements.get(indx);
                    if (al.contains(elem)) {
                        deletedElementsMap.put(elem, s);
                        deletedElementsList.add(elem);
                    }
                }
            });

            elements.removeAll(deletedElementsList);
            clsses.values().forEach(al -> al.removeAll(deletedElementsList));

            for (;;) {
//                //todo think about change elems indexes
//                for (int j = 0; j < elements.size(); j++) {
//                    elements.get(j).index = j;
//                }

                frisStolp.makeStolps();
                double comp = Math.pow(i/size,alpha)*FrisCompact.calculate(clsses, stolps, true);
                if (comp > bestComp) {
                    bestStolps = new HashMap<>(frisStolp.getStolps());
                    bestComp = comp;
                }
                int k;
                // find position of item that can be incremented
                for (k = i - 1; k >= 0 && indxs[k] == originalElements.size() - i + k; k--);
                if(k < 0){
                    break;
                } else {
                    FElement elem = originalElements.get(indxs[k]);
                    deletedElementsList.remove(elem);
                    elements.add(elem);
                    clsses.get(deletedElementsMap.remove(elem)).add(elem);

                    // change index
                    indxs[k]++;

                    FElement newElem = originalElements.get(indxs[k]);
                    deletedElementsList.add(newElem);
                    elements.remove(newElem);
                    for (String clName : clsses.keySet()) {
                        ArrayList<FElement> elems = clsses.get(clName);
                        if (elems.contains(newElem)) {
                            deletedElementsMap.put(newElem, clName);
                            elems.remove(newElem);
                            break;
                        }
                    }


                    for (++k; k < i; k++) {
                        elem = originalElements.get(indxs[k]);
                        deletedElementsList.remove(elem);
                        elements.add(elem);
                        clsses.get(deletedElementsMap.remove(elem)).add(elem);

                        indxs[k] = indxs[k-1] + 1;

                        newElem = originalElements.get(indxs[k]);
                        deletedElementsList.add(newElem);
                        elements.remove(newElem);
                        for (String clName : clsses.keySet()) {
                            ArrayList<FElement> elems = clsses.get(clName);
                            if (elems.contains(newElem)) {
                                deletedElementsMap.put(newElem, clName);
                                elems.remove(newElem);
                                break;
                            }
                        }
                    }
                }

            }

        }

        //
        System.out.println(bestComp);
        frisStolp.setStolps(bestStolps);

    }

}
