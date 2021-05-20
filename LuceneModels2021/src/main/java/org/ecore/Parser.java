package org.ecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.utilities.findByExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {

    public static HashMap<String, HashMap<String, ArrayList<String>>> getEcoreInfo(File ecoreFile){

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());
        Resource myMetaModel = null;

        HashMap<String,HashMap<String, ArrayList<String>>> ecoreElements = new HashMap<>();

        try {
            myMetaModel = resourceSet.getResource(URI.createFileURI(String.valueOf(ecoreFile)), true);
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println(ecoreFile);
        }

        if(myMetaModel!=null) {
            try {
                //EPackage univEPackage = (EPackage) myMetaModel.getContents().get(0);

                EList<EObject> contents = myMetaModel.getContents();

                for (int i = 0; i < contents.size(); i += 1) {
                    EPackage univEPackage = (EPackage) myMetaModel.getContents().get(i);

                    ArrayList<EPackage> packages = new ArrayList<>();

                    packages = exploreEcore(univEPackage, packages);
                    for (EPackage pack : packages) {

                        for (EClassifier eClassifier : pack.getEClassifiers()) {

                            if (eClassifier instanceof EClass) {
                                EClass clazz = (EClass) eClassifier;


                                HashMap<String, ArrayList<String>> categoryElements = new HashMap<>();

                                ArrayList<String> attributes = new ArrayList<>();
                                for (EAttribute eAttribute : clazz.getEAttributes()) {
                                    attributes.add(eAttribute.getName());
                                }

                                categoryElements.put("attributes", attributes);

                                ArrayList<String> reference = new ArrayList<>();
                                for (EReference eAttribute : clazz.getEReferences()) {
                                    reference.add(eAttribute.getName());
                                }
                                categoryElements.put("reference", reference);
                           /*
                            ArrayList<String> attributesInh = new ArrayList<>();
                            for (EAttribute eAttribute : clazz.getEAllAttributes()) {
                                attributesInh.add(eAttribute.getName());
                            }
                            categoryElements.put("attributesInherited",attributesInh);

                            ArrayList<String> referenceInh = new ArrayList<>();
                            for (EReference eAttribute : clazz.getEAllReferences()) {
                                referenceInh.add(eAttribute.getName());
                            }
                            categoryElements.put("referenceInherited",referenceInh);
                            */
                                ecoreElements.put(clazz.getName(), categoryElements);
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e);
                System.out.println(ecoreFile);
            }

        }
/*
        for(String elem:ecoreElements.keySet()){
            System.out.println(elem+" - "+ecoreElements.get(elem));
        }
*/
        //System.out.println(ecoreElements);
        return ecoreElements;
    }

    public static EList<EPackage> getSubPackage(EPackage pack){
        return pack.getESubpackages();
    }

    public static void print(EPackage targetPackage){
        for (EClassifier eClassifier : targetPackage.getEClassifiers()) {
            try {
                if (eClassifier instanceof EClass) {
                    EClass clazz = (EClass) eClassifier;
                    System.out.println("____CLASS____");
                    System.out.println(clazz.getName());
                    System.out.println("_________ATTRIBUTES_________");
                    for (EAttribute eAttribute : clazz.getEAttributes()) {
                        System.out.println(eAttribute.getName());
                        System.out.println(eAttribute.getEType().getName());
                    }
                    System.out.println("_________ATTRIBUTES WITH INHERITED_________");
                    for (EAttribute eAttribute : clazz.getEAllAttributes()) {
                        System.out.println(eAttribute.getName());
                        System.out.println(eAttribute.getEType().getName());
                    }
                    System.out.println("_________REFERENCE_________");
                    for (EReference eAttribute : clazz.getEReferences()) {
                        System.out.println(eAttribute.getName());
                        System.out.println(eAttribute.getEType().getName());
                    }
                    System.out.println("_________REFERENCE WITH INHERITED_________");
                    for (EReference eAttribute : clazz.getEAllReferences()) {
                        System.out.println(eAttribute.getName());
                        System.out.println(eAttribute.getEType().getName());
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static ArrayList<EPackage> exploreEcore(EPackage univEPackage, ArrayList<EPackage> results){
        EList<EPackage> subPackages = getSubPackage(univEPackage);

        if(results.size()<1&&subPackages.size()<1){results.add(univEPackage);}

        for(int i=0; i<subPackages.size(); i+=1){

            if(getSubPackage(subPackages.get(i)).size()>0){
                exploreEcore(subPackages.get(i), results);
            }
            else{
                //print(subPackages.get(i));
                results.add(subPackages.get(i));
                //System.out.println(subPackages.get(i));
            }

        }
        return results;

    }

}