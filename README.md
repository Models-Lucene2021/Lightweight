# This is the folder of the replication package of Metamodel Clustering Classification approach for Models conference 2021

# Prerequisites
  * Java 11
  * Maven

# How to run

To start the evaluation you should modify the file *config*, inside the file, is described each folder you need to change.
By default, everything is configured to run with the dataset and folders stored ind the *Data* folder.

To run the **classification** operation just open the terminal in the project and type the following. (No parameters are needed since everything is inside the config file)
```
mvn exec:java -Dexec.mainClass="org.tool.classification"
```

To run the **clustering** operation open the terminal and type the following.
```
mvn exec:java -Dexec.mainClass="org.tool.clustering"
```

If you want to run the complete leave-one-out evaluation of the **classification** with the metamodels stored inside the dataset type
```
mvn exec:java -Dexec.mainClass="org.tool.evaluateClassification"
```

For the evaluation of the **clustering** type
```
mvn exec:java -Dexec.mainClass="org.tool.evaluateClustering"
```
The results will be saved in a csv file.
Be careful to the configuration file, by default the number of rounds is as the full dataset (551 elements), this means that the complete evaluation will take some hours to complete. 

# Evaluated Data

Inside the *EvaluatedData* folder, there are the results reported in the technichal paper.
There are 6 files for each configuration tested with boosting and without it.
The files are csv with the following structure:
```
tested category,retrieved category
tested category,empty,precision (this is an added row to check test by test the partial precision)
```

The project can be imported as well using Eclipse or IntelliJ Idea.
