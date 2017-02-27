package FrisStolp.Utils;

/**
 * Created by Nikita on 23.02.17.
 */
public class NearElmsPair {

    //Nearest elem
    private ClDistPair first;

    //Nearest elem without first elem class
    private ClDistPair second;

    public NearElmsPair(ClDistPair first, ClDistPair second) {
        this.first = first;
        this.second = second;
    }

    public ClDistPair getFirst() {
        return first;
    }

    public ClDistPair getSecond() {
        return second;
    }
}
