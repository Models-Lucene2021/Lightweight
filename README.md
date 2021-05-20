# This is the folder ofthe replication package of Metamodel Clustering Classification approach for Models conference 2021

# How to run

# Prerequisites
  * Java 11
  * Maven

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

Inside the *EvaluatedData* folder, there are the results reported in the technichal paper.
There are 6 files for each configuration tested with boosting and not.
The files are csv with the following structure:
```
tested category,retrieved category
tested category,empty,precision (this is an added row to check test by test the partial precision)
```

The project can be imported as well using Eclipse or IntelliJ Idea.
