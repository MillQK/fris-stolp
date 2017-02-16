package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.ClassDistances.NearElementDistance;
import FrisStolp.Distances.Distance;
import FrisStolp.Distances.EuclideanDist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        try(BufferedReader br = new BufferedReader(
                new FileReader("./datasets/lfw/lfw_vectors.csv"))) {

            int count = 0;
            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 1; i < splited.length-1; i++) {
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

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        for (String cl: classes.keySet()){
            testElementsClasses.add(cl);
            testElements.add(classes.get(cl).remove(0));
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


        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println("Result: " + ((double) correct)/recognized.size());

    }

    public static void main(String[] args) {

        //irisDataSet();
        //parallelLetterDataSet();
        lfwDataSet();

    }

}
