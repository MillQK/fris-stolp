package FrisStolp.Utils;

/**
 * Created by Nikita on 24.02.17.
 */
public class PairDistStolp {

    //nearest stolp
    private double toOwnClassStolp;

    //nearest stolp
    private double toOtherClassStolp;

    public PairDistStolp(double toOwnClassStolp, double toOtherClassStolp) {
        this.toOwnClassStolp = toOwnClassStolp;
        this.toOtherClassStolp = toOtherClassStolp;
    }

    public double getToOwnClassStolp() {
        return toOwnClassStolp;
    }

    public double getToOtherClassStolp() {
        return toOtherClassStolp;
    }
}
