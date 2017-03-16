package FrisStolp;

import FrisStolp.ClassDistances.ClassDistance;
import FrisStolp.ClassDistances.MedianSumClassDistance;
import FrisStolp.ClassDistances.NearElementDistance;
import FrisStolp.Distances.Distance;
import FrisStolp.Distances.EuclideanDist;
import FrisStolp.Utils.DistanceMatrix;

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
//        frisStolp.makeDistanceMatrix();
        DistanceMatrix.makeDistanceMatrix(elements, distance);
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
        DistanceMatrix.makeDistanceMatrix(elements, distance);
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
        DistanceMatrix.makeDistanceMatrix(elements, distance);
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

        for (String cl : classes.keySet()) {
            if (classes.get(cl).size() < 5) {
                for (FElement el : classes.get(cl)) {
                    elements.remove(el);
                }
            }
        }
        classes.keySet().removeIf(e -> classes.get(e).size() < 5);

        int countTest = 0;

        for (String cl: classes.keySet()) {

            for (int i = 0; i < classes.get(cl).size()/5; i++) {
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

//        save(classes, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/lfw/lfw_vectors_full_train.csv");
//        save(testElements, testElementsClasses, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/lfw/lfw_vectors_full_test.csv");
//        System.exit(0);

        double frStolpThr = -0.020;

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
        frisStolp.elements = elements;
        DistanceMatrix.makeDistanceMatrix(elements, distance);
        frisStolp.makeNearDistances();
//        //frisStolp.makeNearWOClassDistances();
//        frisStolp.makeStolps();
//        Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//        Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//        ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//        frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix()));
//        System.out.println(FrisCensor.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix()));

        while (frStolpThr < 0.5) {
            frStolpThr += 0.020;
            System.out.println("Fris Stolp Threshold: " + frStolpThr);
            frisStolp.setFrStolpThr(frStolpThr);
            frisStolp.makeStolps();
            Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
            Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
            ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
            frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
            System.out.println(FrisCompact.calculate(classesCopy, stolpsCopy, true));



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

            System.out.println("Result: " + ((double) correct) / recognized.size());
        }

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
//        DistanceMatrix.makeDistanceMatrix(elements, distance);
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

    public static void vkVectDataSetCensor() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 3; i < splited.length; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[2];
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

        System.out.println(classes.size());
        System.out.println(elements.size());
        for (String clName: classes.keySet()) {
            System.out.println(clName + " : " + classes.get(clName).size());
        }

//        Map<String, ArrayList<FElement>> smallCl = new HashMap<>();
//        ArrayList<FElement> testElms = new ArrayList<>();
//        ArrayList<String> testElmsClasses = new ArrayList<>();
//
//        for (String clName : classes.keySet()) {
//            smallCl.put(clName, new ArrayList<>(classes.get(clName).subList(0,7)));
//            testElms.addAll(classes.get(clName).subList(7, 17));
//            for (int i = 0; i < 10; i++) {
//                testElmsClasses.add(clName);
//            }
//            if (smallCl.size() == 7)
//                break;
//        }
//
//        save(smallCl, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vect_smallv3_train");
//        save(testElms, testElmsClasses, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vect_smallv3_test");
//        System.exit(0);
        int countTest = 0;

        for (String cl: classes.keySet()) {

            for (int i = 0; i < classes.get(cl).size()*0.1; i++) {
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

//        save(classes, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix_train.csv");
//        save(testElements, testElementsClasses, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix_test.csv");
//        System.exit(0);


        double frStolpThr = 0.0;

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
        frisStolp.elements = elements;
        DistanceMatrix.makeDistanceMatrix(elements, distance);
//        frisStolp.makeDistanceMatrix();
        frisStolp.makeNearDistances();
        frisStolp.makeStolps();
        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), false));

        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println("Result: " + ((double) correct) / recognized.size());

//        double frStolpThr = -0.020;
//
//        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
//        frisStolp.elements = elements;
//        frisStolp.makeDistanceMatrix();
//        frisStolp.makeNearDistances();
//        //frisStolp.makeNearWOClassDistances();
//        frisStolp.makeStolps();
//        Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//        Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//        ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//        frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix(), false));
//        System.out.println(FrisCensor.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix()));
//
//        while (frStolpThr < 0.4) {
//            frStolpThr += 0.050;
//            System.out.println("Fris Stolp Threshold: " + frStolpThr);
//            frisStolp.setFrStolpThr(frStolpThr);
//            frisStolp.makeStolps();
//            Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//            Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//            ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//            frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//            System.out.println(FrisCompact.calculate(classesCopy, stolpsCopy, frisStolp.getDistanceMatrix(), true));
//
//
//
////        for (String clName : frisStolp.stolps.keySet()) {
////            System.out.println(clName);
////        }
//
////        Fris fris = new Fris(distance, classDistance, classes, elements);
////        fris.makeDistanceMatrix();
////        fris.makeNearDistances();
////        fris.makeNearWOClassDistances();
//
//
//            recognized = frisStolp.recognize(testElements);
//            correct = 0;
//
//            for (int i = 0; i < recognized.size(); i++) {
//                if (testElementsClasses.get(i).equals(recognized.get(i))) {
//                    correct++;
//                }
//            }
//
//            System.out.println("Result: " + ((double) correct) / recognized.size());
//        }

    }

    public static void vkVectDataSetCreateNoise() {

        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_full_fix.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 3; i < splited.length; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[2];
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

        System.out.println(classes.size());
        System.out.println(elements.size());
        for (String clName: classes.keySet()) {
            System.out.println(clName + " : " + classes.get(clName).size());
        }

        Random rnd = new Random();

        Map<String, ArrayList<FElement>> noise = new HashMap<>();
        int noiseSize = (int)(classes.size()*0.25);

        for (String clName: classes.keySet()) {
            if (noise.size() < noiseSize) {
                noise.put(clName, classes.get(clName));
            } else {
                break;
            }
        }

        classes.keySet().removeIf(key -> noise.containsKey(key));

        int countTest = 0;

        for (String cl: classes.keySet()) {

            for (int i = 0; i < classes.get(cl).size()*0.25; i++) { //25% from no noice elements to test
                FElement el = classes.get(cl).remove(0);
                elements.remove(el);
                el.index = countTest++;
                testElements.add(el);
                testElementsClasses.add(cl);
            }
        }

        ArrayList<ArrayList<FElement>> classValues = new ArrayList<>(classes.values());

        for (ArrayList<FElement> noiseElements: noise.values()) {
            for (FElement noiseElement: noiseElements) {
                int place = rnd.nextInt(classes.size());
                classValues.get(place).add(noiseElement);
            }
        }

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).index = i;
        }

        save(classes, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_train_noise.csv");
        save(testElements, testElementsClasses, "/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_test_noise.csv");


    }

    public static void vkVectDataSetNoise() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_train_noise.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 0; i < splited.length-1; i++) {
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

        System.out.println(classes.size());
        System.out.println(elements.size());
        for (String clName: classes.keySet()) {
            System.out.println(clName + " : " + classes.get(clName).size());
        }

        int countTest = 0;

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vectors_test_noise.csv"))) {

            String line;
            br.readLine();
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 0; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[splited.length-1];
                FElement elem = new FElement(countTest++, vect);
                testElementsClasses.add(className);
                testElements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).index = i;
        }

        double frStolpThr = 0.0;

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
        frisStolp.elements = elements;
        DistanceMatrix.makeDistanceMatrix(elements, distance);
//        frisStolp.makeDistanceMatrix();
        frisStolp.makeNearDistances();
        frisStolp.makeStolps();
        frisStolp.stolpsFilter();
        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), false));

        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println("Result: " + ((double) correct) / recognized.size());

//        double frStolpThr = -0.020;
//
//        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
//        frisStolp.elements = elements;
//        frisStolp.makeDistanceMatrix();
//        frisStolp.makeNearDistances();
//        //frisStolp.makeNearWOClassDistances();
//        frisStolp.makeStolps();
//        Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//        Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//        ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//        frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix(), false));
//        System.out.println(FrisCensor.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix()));
//
//        while (frStolpThr < 0.45) {
//            frStolpThr += 0.050;
//            System.out.println("Fris Stolp Threshold: " + frStolpThr);
//            frisStolp.setFrStolpThr(frStolpThr);
//            frisStolp.makeStolps();
//            Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//            Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//            ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//            frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//            System.out.println(FrisCompact.calculate(classesCopy, stolpsCopy, frisStolp.getDistanceMatrix(), true));
//
//
//
////        for (String clName : frisStolp.stolps.keySet()) {
////            System.out.println(clName);
////        }
//
////        Fris fris = new Fris(distance, classDistance, classes, elements);
////        fris.makeDistanceMatrix();
////        fris.makeNearDistances();
////        fris.makeNearWOClassDistances();
//
//
//            recognized = frisStolp.recognize(testElements);
//            correct = 0;
//
//            for (int i = 0; i < recognized.size(); i++) {
//                if (testElementsClasses.get(i).equals(recognized.get(i))) {
//                    correct++;
//                }
//            }
//
//            System.out.println("Result: " + ((double) correct) / recognized.size());
//        }

    }

    public static void vkVectSmallDS() {

        Distance distance = new EuclideanDist();
        ClassDistance classDistance = new NearElementDistance();
        Map<String, ArrayList<FElement>> classes = new HashMap<>();
        ArrayList<FElement> elements = new ArrayList<FElement>();

        ArrayList<FElement> testElements = new ArrayList<>();
        ArrayList<String> testElementsClasses = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vect_smallv3_train"))) {

            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 0; i < splited.length-1; i++) {
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

        System.out.println(classes.size());
        System.out.println(elements.size());
        for (String clName: classes.keySet()) {
            System.out.println(clName + " : " + classes.get(clName).size());
        }

        int countTest = 0;

        try(BufferedReader br = new BufferedReader(
                new FileReader("/Users/Nikita/Documents/ProjectsIDEA/fris-stolp/datasets/vk_vectors/vk_vect_smallv3_test"))) {

            String line;
            while((line = br.readLine()) != null) {

                String[] splited = line.split(",");
                ArrayList<Double> vect = new ArrayList<>();
                for (int i = 0; i < splited.length-1; i++) {
                    vect.add(Double.parseDouble(splited[i]));
                }
                String className = splited[splited.length-1];
                FElement elem = new FElement(countTest++, vect);
                testElementsClasses.add(className);
                testElements.add(elem);

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).index = i;
        }

        double frStolpThr = 0.0;

        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
        frisStolp.elements = elements;
        DistanceMatrix.makeDistanceMatrix(elements, distance);
        frisStolp.makeNearDistances();
        frisStolp.makeStolps();
        FrisCensor.findBest(frisStolp, 0.15);
//        frisStolp.stolpsFilter();
//        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), false));

        ArrayList<String> recognized = frisStolp.recognize(testElements);
        int correct = 0;

        for (int i = 0; i < recognized.size(); i++) {
            if (testElementsClasses.get(i).equals(recognized.get(i))) {
                correct++;
            }
        }

        System.out.println("Result: " + ((double) correct) / recognized.size());

//        double frStolpThr = -0.020;
//
//        FrisStolp frisStolp = new FrisStolp(distance, classDistance, frStolpThr, classes);
//        frisStolp.elements = elements;
//        frisStolp.makeDistanceMatrix();
//        frisStolp.makeNearDistances();
//        //frisStolp.makeNearWOClassDistances();
//        frisStolp.makeStolps();
//        Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//        Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//        ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//        frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//        System.out.println(FrisCompact.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix(), false));
//        System.out.println(FrisCensor.calculate(classes, frisStolp.getStolps(), frisStolp.getDistanceMatrix()));
//
//        while (frStolpThr < 0.45) {
//            frStolpThr += 0.050;
//            System.out.println("Fris Stolp Threshold: " + frStolpThr);
//            frisStolp.setFrStolpThr(frStolpThr);
//            frisStolp.makeStolps();
//            Map<String, Map<FElement, ArrayList<FElement>>> stolpsCopy = new HashMap<>(frisStolp.getStolps());
//            Map<String, ArrayList<FElement>> classesCopy = new HashMap<>(classes);
//            ArrayList<FElement> elementsCopy = new ArrayList<>(elements);
//            frisStolp.stolpsFilter(stolpsCopy, classesCopy, elementsCopy);
//            System.out.println(FrisCompact.calculate(classesCopy, stolpsCopy, frisStolp.getDistanceMatrix(), true));
//
//
//
////        for (String clName : frisStolp.stolps.keySet()) {
////            System.out.println(clName);
////        }
//
////        Fris fris = new Fris(distance, classDistance, classes, elements);
////        fris.makeDistanceMatrix();
////        fris.makeNearDistances();
////        fris.makeNearWOClassDistances();
//
//
//            recognized = frisStolp.recognize(testElements);
//            correct = 0;
//
//            for (int i = 0; i < recognized.size(); i++) {
//                if (testElementsClasses.get(i).equals(recognized.get(i))) {
//                    correct++;
//                }
//            }
//
//            System.out.println("Result: " + ((double) correct) / recognized.size());
//        }

    }


    public static void main(String[] args) {

        //irisDataSet();
        //parallelLetterDataSet();
//        letterDataSet();
//        lfwDataSet();
//        lfwDataSetNoise();
//        lfwDataSetCensor();
//        vkVectDataSetCensor();
        vkVectSmallDS();
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
