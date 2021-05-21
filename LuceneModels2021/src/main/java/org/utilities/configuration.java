package org.utilities;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;

public class configuration {

    private String indexDirectory ="Data/Indexes"; //the folder where the indexes will be stored
    private String basePath = "Data/Dataset/FullDataset"; //the folder where are the training metamodels
    private String testDirectory = "Data/Testing"; //the folder where is temporary stored the testing metamodels
    private String evaluationDirectory = "Data/Evaluation"; //the folder where are stored each round of the evaluation
    private String testFile = this.basePath+"/"+"090_003_025_maven-164469695.ecore"; //the input ecore used as a test, such file should not be present in the dataset (basepath)
    private String outputFile = "results.csv"; // the result file
    private ArrayList<Integer> elemsPerCluster = new ArrayList<>();
    private int datasetElements = 0;
    private int hitsPerPage = 27; //the elements you want to return, if you like to specify your own value

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(String indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getTestDirectory() {
        if(!new File(testDirectory).exists()){
            new File(testDirectory).mkdirs();
        }
        return testDirectory;
    }

    public void setTestDirectory(String testDirectory) {
        this.testDirectory = testDirectory;
    }

    public String getEvaluationDirectory() {
        if(!new File(evaluationDirectory).exists()){
            new File(evaluationDirectory).mkdirs();
        }
        return evaluationDirectory;
    }

    public void setEvaluationDirectory(String evaluationDirectory) {
        this.evaluationDirectory = evaluationDirectory;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public ArrayList<Integer> getElemsPerCluster() {
        long numFiles = 0;
        try {
             numFiles = (int) Files.list(Paths.get(this.basePath))
                    .filter(p -> p.toFile().isFile())
                    .count();
        }
        catch(Exception e)
        {
            System.out.println("Exception in generating array of cluster element");
        }
        if(numFiles<=137){
            ArrayList<Integer> elemsPerCluster = new ArrayList<>(Arrays.asList(14, 2, 10, 6, 25, 13, 19, 40, 9));
            return elemsPerCluster;
        }
        if(numFiles<=275){
            ArrayList<Integer> elemsPerCluster = new ArrayList<>(Arrays.asList(28, 4, 20, 12, 50, 26, 38, 80, 18));
            return elemsPerCluster;
        }
        if(numFiles<=550){
            ArrayList<Integer> elemsPerCluster = new ArrayList<>(Arrays.asList(56, 7, 37, 24, 100, 54, 76, 159, 38));
            return elemsPerCluster;
        }

        return null;
    }

    public int getDatasetElements() {
        int numFiles = 0;
        try {
            numFiles = (int) Files.list(Paths.get(this.basePath))
                    .filter(p -> p.toFile().isFile())
                    .count();
        }
        catch(Exception e)
        {
            System.out.println("Exception in generating array of cluster element");
        }

        return numFiles;
    }

    public void setDatasetElements(int datasetElements) {
        this.datasetElements = datasetElements;
    }

    public void setElemsPerCluster(ArrayList<Integer> elemsPerCluster) {
        this.elemsPerCluster = elemsPerCluster;
    }

    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public void setHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

}
