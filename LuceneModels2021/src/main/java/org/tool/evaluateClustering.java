package org.tool;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lucene.ecoreIndexer;
import org.utilities.configuration;
import org.utilities.findByExtension;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

public class evaluateClustering {

    public static void main(String[] args) throws IOException {
        configuration config = new configuration();
        String INDEX_DIRECTORY = config.getIndexDirectory();
        FileUtils.cleanDirectory(new File(INDEX_DIRECTORY));
        String BASEPATH = config.getBasePath();
        String testFolder = config.getTestDirectory();
        String EvaluationPath = config.getEvaluationDirectory();
        Directory luceneIndexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        Analyzer analyzer = new StandardAnalyzer();
        boolean classification = false;

        int testSamples = 1;
        int counterSamples = 0;
        int rounds = config.getDatasetElements();
        int hitsPerPage = 0;

        PrintWriter writer = new PrintWriter(new File(config.getOutputFile()));
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
                        FileUtils.moveFileToDirectory(ecore, new File(testFolder), false);
                        ecoresTested.add(ecore);
                        counterSamples += 1;
                    }
                }
            }
            hitsPerPage = config.getElemsPerCluster().get(Integer.parseInt(testCluster)-1)-1;
            ArrayList<Integer> res = eval(INDEX_DIRECTORY, BASEPATH, luceneIndexDir, testFolder, analyzer, hitsPerPage, writer, classification);
            StringBuilder sb = new StringBuilder();
            sb.append(testCluster+","+" "+","+(int)(( (double)res.get(0) /(testSamples*hitsPerPage))*100));
            sb.append("\n");
            writer.write(sb.toString());
            writer.flush();

            //move the ecores back to original folder

            for(File ecore: findByExtension.getFilesByEndingValue(new File(testFolder), "ecore")) {
                FileUtils.copyFileToDirectory(ecore, new File(EvaluationPath+"round"+i+"/"+"test/"));
                FileUtils.moveFileToDirectory(ecore, new File(BASEPATH), false);
            }
            counterSamples=0;
        }
    }

    public static ArrayList<Integer> eval(String INDEX_DIRECTORY, String BASEPATH, Directory indexDir, String testFolder, Analyzer analyzer, int hitsPerPage, PrintWriter writer, boolean classification) throws IOException {

        long startingTime = System.currentTimeMillis();
        long endTime = 0;
        ecoreIndexer.createIndexes(analyzer,indexDir,BASEPATH,classification);
        endTime = System.currentTimeMillis()-startingTime;
        System.out.println("Time for creating indexes: "+endTime);

        startingTime = System.currentTimeMillis();
        ArrayList<Integer> results = new ArrayList<>();
        int globalCounter = 0;

        for(File ecore: findByExtension.getFilesByEndingValue(new File(testFolder), "ecore")) {
            File ecoreFile = new File(ecore.getPath());
            String querystr = ecoreIndexer.createBoostedQuery(ecoreFile);
            ArrayList<Document> ecoresFound = ecoreIndexer.query(analyzer, indexDir,querystr,hitsPerPage);
            if(ecoresFound!=null) {

                int ind = ecore.getName().indexOf("_");
                int finalInd = ecore.getName().indexOf("_",ind+1);
                String testCluster = ecore.getName().substring(ind+1,finalInd);
                int counter = 0;
                for (Document doc : ecoresFound) {
                    ind = doc.get("filePath").indexOf("_");
                    finalInd = doc.get("filePath").indexOf("_",ind+1);
                    String currentCluster = doc.get("filePath").substring(ind+1,finalInd);
                    if(currentCluster.equals(testCluster)){counter+=1;}
                    StringBuilder sb = new StringBuilder();
                    sb.append(testCluster);
                    sb.append(",");
                    sb.append(currentCluster);
                    sb.append("\n");
                    writer.write(sb.toString());
                    writer.flush();
                }
                globalCounter+=counter;
            }
        }
        results.add(globalCounter);

        FileUtils.cleanDirectory(new File(INDEX_DIRECTORY));

        endTime = System.currentTimeMillis()-startingTime;
        System.out.println("Time fo queries: "+endTime);

        return results;
    }
}
