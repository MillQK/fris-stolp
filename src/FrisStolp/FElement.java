package FrisStolp; /**
 * Created by Nikita on 24.01.17.
 */

import java.util.ArrayList;

public class FElement {
    public int index;
    public ArrayList<Double> vector;

    public FElement(int ind) {
        index = ind;
        vector = new ArrayList<>();
    }

    public FElement(int ind, ArrayList<Double> vals) {
        index = ind;
        vector = vals;
    }
}
