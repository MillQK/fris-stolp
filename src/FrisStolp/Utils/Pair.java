package FrisStolp.Utils;

import FrisStolp.FElement;

/**
 * Created by Nikita on 21.02.17.
 */
public class Pair {

    private FElement element;
    private String className;

    public FElement getElement() {
        return element;
    }

    public String getClassName() {
        return className;
    }

    public Pair(FElement element, String className) {

        this.element = element;
        this.className = className;
    }
}
