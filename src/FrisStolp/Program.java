package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.ClassDistances.MedianSumClassDistance;
import FrisStolp.ClassDistances.NearElementDistance;
import FrisStolp.Distances.Distance;
import FrisStolp.Distances.EuclideanDist;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Nikita on 25.01.17.
 */
public class Program {

    public static void letterDataSet() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/expas/fris_datasets/letter/train_letter-recognition.data"))) {

            int count = 0;
            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                if (!classes.containsKey(splited[0])) {
                    classes.put(splited[0], new ArrayList<>());
                }
                FElement elem = new FElement(count++, vect);
                classes.get(splited[0]).add(elem);
                elements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, 0.15, classes);
        frisStolp.elements = elements;
        frisStolp.makeDistanceMatrix();
        frisStolp.makeNearDistances();
        //frisStolp.makeNearWOClassDistances();
        frisStolp.makeStolps();
//        for (String clName : frisStolp.stolps.keySet()) {
//            System.out.println(clName);
//        }

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/expas/fris_datasets/letter/test_letter-recognition.data"))) {

            int count = 0;
            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                FElement elem = new FElement(count++, vect);
                testElements.add(elem);
                testElementsClasses.add(splited[0]);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println(((double) correct)/recognized.size());

    }

    public static void parallelLetterDataSet() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/expas/fris_datasets/letter/train_letter-recognition.data"))) {

            int count = 0;
            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                if (!classes.containsKey(splited[0])) {
                    classes.put(splited[0], new ArrayList<>());
                }
                FElement elem = new FElement(count++, vect);
                classes.get(splited[0]).add(elem);
                elements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, 0.5, classes);
        frisStolp.elements = elements;
        frisStolp.makeDistanceMatrix();
        frisStolp.makeNearDistances();
        frisStolp.makeNearWOClassDistances();
        //frisStolp.makeStolps();
//        for (String clName : frisStolp.stolps.keySet()) {
//            System.out.println(clName);
//        }

        frisStolp.stolps = new HashMap<>();
        int coresNum = Runtime.getRuntime().availableProcessors();
        ExecutorService es = Executors.newFixedThreadPool(coresNum);
        for(String cn: classes.keySet()){
            ParallelStolps ps = new ParallelStolps(classes, distance, classDistance, 0.5, frisStolp);
            ps.setClassName(cn);
            es.execute(ps);
        }
        es.shutdown();
        while (!es.isTerminated()){}

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/expas/fris_datasets/letter/test_letter-recognition.data"))) {

            int count = 0;
            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                FElement elem = new FElement(count++, vect);
                testElements.add(elem);
                testElementsClasses.add(splited[0]);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println(((double) correct)/recognized.size());

    }

    public static void irisDataSet() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/expas/fris_datasets/iris/iris_fris_train"))) {

            int count = 0;
            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(" ");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 0; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                if (!classes.containsKey(splited[splited.length-1])) {
                    classes.put(splited[splited.length-1], new ArrayList<>());
                }
                FElement elem = new FElement(count++, vect);
                classes.get(splited[splited.length-1]).add(elem);
                elements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, 0.5, classes);
        frisStolp.elements = elements;
        frisStolp.makeDistanceMatrix();
        frisStolp.makeNearDistances();
        frisStolp.makeNearWOClassDistances();
        frisStolp.makeStolps();
//        for (String clName : frisStolp.stolps.keySet()) {
//            System.out.println(clName);
//        }

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/expas/fris_datasets/iris/iris_fris_test"))) {

            int count = 0;
            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(" ");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 0; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                FElement elem = new FElement(count++, vect);
                testElements.add(elem);
                testElementsClasses.add(splited[splited.length-1]);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println(((double) correct)/recognized.size());

    }

    public static void lfwDataSet() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("./datasets/lfw/lfw_vectors_full.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[splited.length-1];
                if (!classes.containsKey(className)) {
                    classes.put(className, new ArrayList<>());
                }
                FElement elem = new FElement(0, vect);
                classes.get(className).add(elem);
                elements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        int countTest = 0;

        for (String cl: classes.keySet()) {
            for (int i = 0; i < classes.get(cl).size()/3; i++) {
                FElement el = classes.get(cl).remove(0);
                elements.remove(el);
                el.index = countTest++;
                testElements.add(el);
                testElementsClasses.add(cl);
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).index = i;
        }

//        FrisStolp frisStolp = new FrisStolp(distance, classDistance, 0.5, classes);
//        frisStolp.elements = elements;
//        frisStolp.makeDistanceMatrix();
//        frisStolp.makeNearDistances();
//        frisStolp.makeNearWOClassDistances();
//        frisStolp.makeStolps();
//        for (String clName : frisStolp.stolps.keySet()) {
//            System.out.println(clName);
//        }

        Fris fris = new Fris(distance, classDistance, classes, elements);
//        fris.makeDistanceMatrix();
//        fris.makeNearDistances();
//        fris.makeNearWOClassDistances();


        ArrayList<String> recognized = fris.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

//        try(BufferedWriter bw = new BufferedWriter(
//                new FileWriter("./datasets/lfw/lfw_vectors_full_train_wrong.csv"))) {
//
//            StringBuilder sb = new StringBuilder();
//
//            for (int i = 0; i < recognized.size(); i++) {
//                if (testElementsClasses.get(i).equals(recognized.get(i))) {
//                    correct++;
//                } else {
//                    String vector = testElements.get(i).vector.stream()
//                            .map(Objects::toString).collect(Collectors.joining(","));
//
//                    sb.append(vector);
//                    sb.append(" ");
//                    sb.append(testElementsClasses.get(i));
//                    sb.append(" ");
//                    sb.append(recognized.get(i));
//                    sb.append("\n");
//
//                    bw.write(sb.toString());
//                    sb.setLength(0);
//                }
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }

        System.out.println("Result: " + ((double) correct)/recognized.size());

    }

    public static void lfwDataSetCensor() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("./datasets/lfw/lfw_vectors_full.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[splited.length-1];
                if (!classes.containsKey(className)) {
                    classes.put(className, new ArrayList<>());
                }
                FElement elem = new FElement(0, vect);
                classes.get(className).add(elem);
                elements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        int countTest = 0;

        for (String cl: classes.keySet()) {
            for (int i = 0; i < classes.get(cl).size()/3; i++) {
                FElement el = classes.get(cl).remove(0);
                elements.remove(el);
                el.index = countTest++;
                testElements.add(el);
                testElementsClasses.add(cl);
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).index = i;
        }

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, 0.3, classes);
        frisStolp.elements = elements;
        frisStolp.makeDistanceMatrix();
        frisStolp.makeNearDistances();
        //frisStolp.makeNearWOClassDistances();
        frisStolp.makeStolps();
//        for (String clName : frisStolp.stolps.keySet()) {
//            System.out.println(clName);
//        }

//        Fris fris = new Fris(distance, classDistance, classes, elements);
//        fris.makeDistanceMatrix();
//        fris.makeNearDistances();
//        fris.makeNearWOClassDistances();


        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

//        try(BufferedWriter bw = new BufferedWriter(
//                new FileWriter("./datasets/lfw/lfw_vectors_full_train_wrong.csv"))) {
//
//            StringBuilder sb = new StringBuilder();
//
//            for (int i = 0; i < recognized.size(); i++) {
//                if (testElementsClasses.get(i).equals(recognized.get(i))) {
//                    correct++;
//                } else {
//                    String vector = testElements.get(i).vector.stream()
//                            .map(Objects::toString).collect(Collectors.joining(","));
//
//                    sb.append(vector);
//                    sb.append(" ");
//                    sb.append(testElementsClasses.get(i));
//                    sb.append(" ");
//                    sb.append(recognized.get(i));
//                    sb.append("\n");
//
//                    bw.write(sb.toString());
//                    sb.setLength(0);
//                }
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }

        System.out.println("Result: " + ((double) correct)/recognized.size());

    }

    public static void lfwDataSetNoise() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new MedianSumClassDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();


        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("./datasets/lfw/lfw_vectors_full.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[splited.length-1];
                if (!classes.containsKey(className)) {
                    classes.put(className, new ArrayList<>());
                }
                FElement elem = new FElement(0, vect);
                classes.get(className).add(elem);
                elements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Random rnd = new Random();
        ArrayList<String> clNames = new ArrayList<>(classes.keySet());
        int lastPart = (int)(classes.keySet().size()*0.2); // last % to noice

        for (int i = lastPart; i < clNames.size(); i++) {
            for (FElement el: classes.get(clNames.get(i))) {
                int newClass = rnd.nextInt(lastPart);
                classes.get(clNames.get(newClass)).add(el);
            }
            classes.remove(clNames.get(i));
        }

        int countTest = 0;

        for (String cl: classes.keySet()) {
            for (int i = 0; i < classes.get(cl).size()/3; i++) {
                FElement el = classes.get(cl).remove(0);
                elements.remove(el);
                el.index = countTest++;
                testElements.add(el);
                testElementsClasses.add(cl);
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).index = i;
        }

        save(classes, "./datasets/lfw/with_noice/lfw_vectors_full_train_noice.csv");
        save(testElements, testElementsClasses, "./datasets/lfw/with_noice/lfw_vectors_full_test_noice.csv");


//        FrisStolp frisStolp = new FrisStolp(distance, classDistance, 0.5, classes);
//        frisStolp.elements = elements;
//        frisStolp.makeDistanceMatrix();
//        frisStolp.makeNearDistances();
//        frisStolp.makeNearWOClassDistances();
//        frisStolp.makeStolps();
//        for (String clName : frisStolp.stolps.keySet()) {
//            System.out.println(clName);
//        }

        Fris fris = new Fris(distance, classDistance, classes, elements);
//        fris.makeDistanceMatrix();
//        fris.makeNearDistances();
//        fris.makeNearWOClassDistances();


        ArrayList<String> recognized = fris.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

//        try(BufferedWriter bw = new BufferedWriter(
//                new FileWriter("./datasets/lfw/lfw_vectors_full_train_wrong.csv"))) {
//
//            StringBuilder sb = new StringBuilder();
//
//            for (int i = 0; i < recognized.size(); i++) {
//                if (testElementsClasses.get(i).equals(recognized.get(i))) {
//                    correct++;
//                } else {
//                    String vector = testElements.get(i).vector.stream()
//                            .map(Objects::toString).collect(Collectors.joining(","));
//
//                    sb.append(vector);
//                    sb.append(" ");
//                    sb.append(testElementsClasses.get(i));
//                    sb.append(" ");
//                    sb.append(recognized.get(i));
//                    sb.append("\n");
//
//                    bw.write(sb.toString());
//                    sb.setLength(0);
//                }
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }

        System.out.println("Result: " + ((double) correct)/recognized.size());

    }


    public static void main(String[] args) {

        //irisDataSet();
        //parallelLetterDataSet();
        letterDataSet();
//        lfwDataSet();
//        lfwDataSetNoise();
//        lfwDataSetCensor();

    }

    private static void save(Map<String, ArrayList<FElement>> classes, String path) {

        try(BufferedWriter bw = new BufferedWriter(
                new FileWriter(path))) {

            classes.forEach((cl, elems) -> {

                StringBuilder sb = new StringBuilder();

                elems.forEach(element -> {

                    for (Double d : element.vector) {
                        sb.append(d);
                        sb.append(",");
                    }

                    sb.append(cl);
                    sb.append("\n");

                    try {
                        bw.write(sb.toString());
                    } catch (IOException ioex) {
                        ioex.printStackTrace();
                    }

                    sb.setLength(0);

                });

            });

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private static void save(ArrayList<FElement> elems, ArrayList<String> classes, String path) {

        try(BufferedWriter bw = new BufferedWriter(
                new FileWriter(path))) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < elems.size(); i++) {

                FElement elem = elems.get(i);

                for (Double d : elem.vector) {
                    sb.append(d);
                    sb.append(",");
                }

                sb.append(classes.get(i));
                sb.append("\n");

                bw.write(sb.toString());
                sb.setLength(0);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
