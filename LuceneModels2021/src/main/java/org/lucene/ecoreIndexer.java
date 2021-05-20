package org.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.utilities.findByExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.ecore.Parser.getEcoreInfo;

public class ecoreIndexer {
    public static void createIndexes(Analyzer analyzer, Directory indexDir, String BASEPATH, boolean classification) throws IOException {
        List<File> ecoresList = findByExtension.getFilesByEndingValue(new File(BASEPATH), "ecore");
        ArrayList<String> notDuplicatesList = new ArrayList<>();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter iw = new IndexWriter(indexDir, iwc);
        int counter = 0;

        for(File ecoreFile:ecoresList) {

            if (notDuplicatesList.contains(ecoreFile.getName() + Files.size(Paths.get(ecoreFile.getPath())))) {
                continue;
            }
            else {
                notDuplicatesList.add(ecoreFile.getName() + Files.size(Paths.get(ecoreFile.getPath())));

                HashMap<String, HashMap<String, ArrayList<String>>> ecoreInfo = getEcoreInfo(ecoreFile);
                if (ecoreInfo.size() <= 1 ) {
                    System.out.println(ecoreFile+" empty");
                    continue;
                }
                Document doc = new Document();

                Field filePath = new TextField("filePath", ecoreFile.getPath().replace(BASEPATH, ""), Field.Store.YES);
                Field fileName = new TextField("fileName", ecoreFile.getName(), Field.Store.YES);

                if(classification){
                    int ind = ecoreFile.getName().indexOf("_");
                    int finalInd = ecoreFile.getName().indexOf("_",ind+1);
                    String currentCluster = ecoreFile.getName().substring(ind+1,finalInd);
                    Field cluster = new TextField("cluster",currentCluster,Field.Store.YES);
                    doc.add(cluster);
                }
                doc.add(filePath);
                doc.add(fileName);

                for (String ecoreClass : ecoreInfo.keySet()) {
                    if (ecoreClass == null) {
                        continue;
                    }
                    Field eClass = new TextField("class", ecoreClass, Field.Store.YES);
                    doc.add(eClass);

                    for (String elem : ecoreInfo.get(ecoreClass).keySet()) { //reference,attributes
                        ArrayList<String> listOfElem = ecoreInfo.get(ecoreClass).get(elem);

                        for (String internalElem : listOfElem) { // elem: attributes; internalElem1: name; internalElem2: ID... and so on
                            if (internalElem == null) {
                                continue;
                            }
                            Field eElem = new TextField(elem, internalElem, Field.Store.YES);
                            doc.add(eElem);
                        }
                    }
                }
                iw.addDocument(doc);
                counter+=1;
            }
            iw.commit();

        }
        iw.close();
        //System.out.println("Indexed: "+counter+" ecores");
    }

    public static ArrayList<Document> query(Analyzer analyzer, Directory indexDir, String querystr, int hitsPerPage){
            try
            {
                QueryParser qp = new QueryParser("<default field>", analyzer);
                Query q = qp.parse(querystr);

                IndexReader reader = DirectoryReader.open(indexDir);
                IndexSearcher searcher = new IndexSearcher(reader);


/*
            String[] fields = {"tag","context","attributes"};
            MultiFieldQueryParser qp = new MultiFieldQueryParser(fields, analyzer);
			Query q = qp.parse(querystr);

			int hitsPerPage = 5;
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);
*/
                TopDocs docs = searcher.search(q, hitsPerPage);

                //System.out.println(q);

                ArrayList<String> ecoresName = new ArrayList<>();
                ArrayList<Document> ecoreDocuments = new ArrayList<>();

                for(ScoreDoc t:docs.scoreDocs){

                    Document d = searcher.doc(t.doc);
                    //System.out.println(t.score);
                    //System.out.println(searcher.explain(q,t.doc));
                    ecoreDocuments.add(d);

                }
                return ecoreDocuments;
            }

            catch (Exception e)
            {
                System.out.println(e);
            }

        return null;
    }

    public static String createQuery(File ecoreFile){
        StringBuilder querystr = new StringBuilder();
        HashMap<String, HashMap<String, ArrayList<String>>> ecoreInfo = getEcoreInfo(ecoreFile);

        int securityCounter = 0; //lucene query cannot exceed 1024 elements

        for(String ecoreClass:ecoreInfo.keySet()){
            if(securityCounter<1024){querystr.append("class:").append(ecoreClass).append(" OR "); securityCounter+=1;}



            for(String elem:ecoreInfo.get(ecoreClass).keySet()){
                ArrayList<String> listOfElem = ecoreInfo.get(ecoreClass).get(elem);
                for(String internalElem:listOfElem){
                    if(elem.contains("reference")&&securityCounter<1024&&internalElem.length()>0&&!internalElem.contains(":")){querystr.append(elem).append(":").append(internalElem).append(" OR "); securityCounter+=1;}
                }
            }
        }
        int lastOR = querystr.lastIndexOf("OR");
        if(querystr.length()>1){return querystr.replace(lastOR,lastOR+2,"").toString();}
        else{return null;}

    }

    public static String createBoostedQuery(File ecoreFile){
        StringBuilder querystr = new StringBuilder();
        HashMap<String, HashMap<String, ArrayList<String>>> ecoreInfo = getEcoreInfo(ecoreFile);

        int securityCounter = 0; //lucene query cannot exceed 1024 elements

        for(String ecoreClass:ecoreInfo.keySet()){
            if(securityCounter<1024){querystr.append("class:").append(ecoreClass).append("^2").append(" OR "); securityCounter+=1;}


            for(String elem:ecoreInfo.get(ecoreClass).keySet()){
                ArrayList<String> listOfElem = ecoreInfo.get(ecoreClass).get(elem);
                for(String internalElem:listOfElem){
                    if(elem.contains("reference")&&securityCounter<1024&&internalElem.length()>0&&!internalElem.contains(":")){querystr.append(elem).append(":").append(internalElem).append("^1.5").append(" OR "); securityCounter+=1;}
                    if(!elem.contains("reference")&&securityCounter<1024&&internalElem.length()>0&&!internalElem.contains(":")){querystr.append(elem).append(":").append(internalElem).append(" OR "); securityCounter+=1;}
                }
            }
        }
        int lastOR = querystr.lastIndexOf("OR");
        if(querystr.length()>1){return querystr.replace(lastOR,lastOR+2,"").toString();}
        else{return null;}

    }
}
