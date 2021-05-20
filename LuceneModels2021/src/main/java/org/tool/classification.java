package org.tool;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lucene.ecoreIndexer;
import org.utilities.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class classification {

    public static void main(String[] args) throws IOException {
        /*
        config
         */
        configuration config = new configuration();
        Analyzer analyzer = new StandardAnalyzer();
        String INDEX_DIRECTORY = config.getIndexDirectory();
        FileUtils.cleanDirectory(new File(INDEX_DIRECTORY));
        String BASEPATH = config.getBasePath();
        File ecore = new File(config.getTestFile());
        Directory luceneIndexDir = null;
        boolean classification = true;
        /*
        Indexes Creation
         */

        luceneIndexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        ecoreIndexer.createIndexes(analyzer,luceneIndexDir,BASEPATH,classification);

        /*
        Query
         */

        File ecoreFile = new File(ecore.getPath());
        String querystr = ecoreIndexer.createBoostedQuery(ecoreFile);

        /*
        classification
         */
        ArrayList<Document> ecoresFound = ecoreIndexer.query(analyzer, luceneIndexDir,querystr,1);
        for (Document doc : ecoresFound) {
            System.out.println("Proposed category: "+doc.get("cluster"));
        }

    }

}
