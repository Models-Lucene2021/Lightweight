package org.tool;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.utilities.findByExtension;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import org.lucene.ecoreIndexer;

public class main {

    public static void main(String[] args) throws IOException {


        String INDEX_DIRECTORY = "/home/rick/Scrivania/Progetti 2020/MMFinder/Indexes/FullDataset/";
        String BASEPATH = "/home/rick/Scrivania/Progetti 2020/MMFinder/Datasets/manualDomains/Test/";//38;24;10;6 //HalfHalfDataset; HalfDataset
        String testFolder = "/home/rick/Scrivania/Progetti 2020/MMFinder/Datasets/manualDomains/TestDataset/";
        String EvaluationPath = "/home/rick/Scrivania/Progetti 2020/MMFinder/Datasets/manualDomains/Evaluations/Test/";
        Directory luceneIndexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        Analyzer analyzer = new StandardAnalyzer();

        int testSamples = 1;//4;9;19;28;55
        int counterSamples = 0;
        //int cluster = 6;
        int rounds = 1; //138;276;551
        double score = 0.0;
        int hitsPerPage = 0;


        PrintWriter writer = new PrintWriter(new File("resultsAux276NoBoosting.csv"));
        ArrayList<File> ecoresTested = new ArrayList<>();

        for(int i=0; i<rounds; i+=1){
            String testCluster = "";
            for(File ecore: findByExtension.getFilesByEndingValue(new File(BASEPATH), "ecore")) {

                if(counterSamples==testSamples){
                    break;
                }
                else{
                    if(ecoresTested.contains(ecore)){
                        continue;
                    }
                    else {
                        int ind = ecore.getName().indexOf("_");
                        int finalInd = ecore.getName().indexOf("_", ind + 1);
                        testCluster = ecore.getName().substring(ind + 1, finalInd);
                        /*if (testCluster.replace("00", "").equals(String.valueOf(cluster))) {
                            FileUtils.moveFileToDirectory(ecore, new File(testFolder), false);
                            ecoresTested.add(ecore);
                            counterSamples += 1;
                        }*/
                        FileUtils.moveFileToDirectory(ecore, new File(testFolder), false);
                        ecoresTested.add(ecore);
                        counterSamples += 1;
                    }
                }
            }
            //hitsPerPage = elemsPerCluster.get(Integer.parseInt(testCluster)-1)-1;

            //System.out.println(hitsPerPage);
            ArrayList<Integer> res = eval(INDEX_DIRECTORY, BASEPATH, luceneIndexDir, testFolder, analyzer, hitsPerPage, writer);
            //System.out.println("no boosted: "+(int)(( (double)res.get(0) /(testSamples*hitsPerPage))*100));
            //System.out.println("boosted: "+(int)(( (double)res.get(1) /(testSamples*hitsPerPage))*100));
            //System.out.println("cluster: "+testCluster+":"+(int)(( (double)res.get(1) /(testSamples*hitsPerPage))*100));
            StringBuilder sb = new StringBuilder();
            sb.append(testCluster+","+" "+","+(int)(( (double)res.get(1) /(testSamples*hitsPerPage))*100));
            sb.append("\n");
            writer.write(sb.toString());
            writer.flush();

            //moving the files indexed to another folder, juri's comparison
            //for(File ecore: findByExtension.getFilesByEndingValue(new File(BASEPATH), "ecore")) {
            //    FileUtils.copyFileToDirectory(ecore, new File(EvaluationPath+"round"+i+"/"+"index/"));
            //}
            //move the ecores back to original folder

            for(File ecore: findByExtension.getFilesByEndingValue(new File(testFolder), "ecore")) {
                //FileUtils.moveFileToDirectory(ecore, new File(EvaluationPath+"round"+i+"/"+"test/"), true);
                FileUtils.copyFileToDirectory(ecore, new File(EvaluationPath+"round"+i+"/"+"test/"));
                FileUtils.moveFileToDirectory(ecore, new File(BASEPATH), false);
            }
            counterSamples=0;
        }

    }

    public static ArrayList<Integer> eval(String INDEX_DIRECTORY, String BASEPATH, Directory indexDir, String testFolder, Analyzer analyzer, int hitsPerPage, PrintWriter writer) throws IOException {

        long startingTime = System.currentTimeMillis();
        long endTime = 0;
        ecoreIndexer.createIndexes(analyzer,indexDir,BASEPATH,false);
        endTime = System.currentTimeMillis()-startingTime;
        System.out.println("Time for creating indexes: "+endTime);

        startingTime = System.currentTimeMillis();
        endTime = 0;
        ArrayList<Integer> results = new ArrayList<>();
        int globalCounter = 0;

        for(File ecore: findByExtension.getFilesByEndingValue(new File(testFolder), "ecore")) {
            File ecoreFile = new File(ecore.getPath());
            String querystr = ecoreIndexer.createQuery(ecoreFile);
            //String querystr = "class:User OR reference:UserSess OR reference:AssignedRoles OR reference:UserLoc OR attributes:Gender OR attributes:UserName OR attributes:UserID OR attributes:Age";
            //String querystr = "class:Y";
            //System.out.println("Query ecore "+ecore);
            //System.out.println(querystr);
            ArrayList<Document> ecoresFound = ecoreIndexer.query(analyzer, indexDir,querystr,hitsPerPage);
            if(ecoresFound!=null) {
                //System.out.println(ecore.getName());

                int ind = ecore.getName().indexOf("_");
                int finalInd = ecore.getName().indexOf("_",ind+1);
                String testCluster = ecore.getName().substring(ind+1,finalInd);
                int counter = 0;
                for (Document doc : ecoresFound) {
                    //System.out.println("Document found: "+doc.get("filePath"));
                    ind = doc.get("filePath").indexOf("_");
                    finalInd = doc.get("filePath").indexOf("_",ind+1);
                    String currentCluster = doc.get("filePath").substring(ind+1,finalInd);
                    if(currentCluster.equals(testCluster)){counter+=1;}
                }
                globalCounter+=counter;
                //System.out.println(counter);
            }
        }
        results.add(globalCounter);

        /*
        added for evaulation purposes, showing differences between boosting and no boosting
         */

        globalCounter = 0;

        for(File ecore: findByExtension.getFilesByEndingValue(new File(testFolder), "ecore")) {
            File ecoreFile = new File(ecore.getPath());
            String querystr = ecoreIndexer.createBoostedQuery(ecoreFile);
            //String querystr = "class:User OR reference:UserSess OR reference:AssignedRoles OR reference:UserLoc OR attributes:Gender OR attributes:UserName OR attributes:UserID OR attributes:Age";
            //String querystr = "class:Y";
            //System.out.println("Query ecore "+ecore);
            System.out.println(querystr);
            ArrayList<Document> ecoresFound = ecoreIndexer.query(analyzer, indexDir,querystr,hitsPerPage);
            if(ecoresFound!=null) {
                //System.out.println(ecore.getName());

                int ind = ecore.getName().indexOf("_");
                int finalInd = ecore.getName().indexOf("_",ind+1);
                String testCluster = ecore.getName().substring(ind+1,finalInd);
                int counter = 0;
                for (Document doc : ecoresFound) {
                    //System.out.println("Document found: "+doc.get("filePath"));
                    ind = doc.get("filePath").indexOf("_");
                    finalInd = doc.get("filePath").indexOf("_",ind+1);
                    String currentCluster = doc.get("filePath").substring(ind+1,finalInd);
                    StringBuilder sb = new StringBuilder();
                    sb.append(testCluster);
                    sb.append(",");
                    sb.append(currentCluster);
                    sb.append("\n");
                    //System.out.print(sb);
                    writer.write(sb.toString());
                    writer.flush();


                    if(currentCluster.equals(testCluster)){counter+=1;}
                }
                globalCounter+=counter;
                //System.out.println(counter);
            }
        }
        results.add(globalCounter);

        FileUtils.cleanDirectory(new File(INDEX_DIRECTORY));

        endTime = System.currentTimeMillis()-startingTime;
        System.out.println("Time fo queries: "+endTime);

        return results;
    }
}
