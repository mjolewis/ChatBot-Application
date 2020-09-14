# PubMed Parser
# Student Information
Author: Michael Lewis\
Course: CS 622\
Date: September 16, 2020\
Homework #2: PubMed

# Description
This application can is used in text processing scenarios to merge XML documents and to search the merged file for a 
given search parameter. The search parameter can be fixed within the system or be fed into the program by the user. 

The current application logic follows a classification logic, that is, it groups file objects into one master aggregate 
and then uses that aggregate file to search for a keyword. When the searching is complete, the simulation results are 
packaged into Article aggregates. These Article aggregates are sent back to the caller where they can be printed to the 
console or otherwise repacked into an output file. 

#Usage
For demonstration purposes, this application has been specialized to work with files from 
ftp://ftp.ncbi.nlm.nih.gov/pubmed/ but can easily be extended to work with XML files from any source. Keep in mind, 
that working with files from other sources will require minor modifications to the program. In particular, several of 
the Constants need to be updated, along with the data model class (Articles in this case).

# System Components
***Builder***\
The Builder class serves as a mediator method that puts the simulation together. It has three public methods and two 
private helper methods. In particular, it provides an informative message when the simulation beings, it provides a 
file dialog box for the user to choose N files and then builds the application components, and finally provides an 
informative message to end the simulation.

***FileMerger***\
The FileMerger class creates BufferedReaders and BufferedWriters to merge N XML documents. Importantly, this class
appropriately handles the occurrence of multiple Prolog and Root tags. More specifically, each well-formed XML document 
can only contain one Prolog tag and one closing Root tag.

The FileMerger core functionality resides in the merge method. The remaining public methods are simple Accessor and 
Mutators provided for convenience. 

***Parser***\
The Parser has been specialized to parse XML documents from ftp://ftp.ncbi.nlm.nih.gov/pubmed/. However, the class
can easily be extended to parse XML documents from any source. Please see the Usage section for more information.

The Parser's sole job is to receive a search parameter and parse the XML document to search for the specified search 
parameter. Once found, an Article object will be created with the the title of the article and it's publication
date. 

Note that the Parser relies on the Java StAX API, which exposes method for iterative, event-based processing of XML 
documents. Please visit https://docs.oracle.com/javase/tutorial/jaxp/stax/api.html for more information.

***Article***\
The Article class provides a simplified data model of the XML documents. It's sole job is to represent an article that
was successfully parsed by the Parser.  

# System Design
The system architecture is based on SOLID principles. Each class within the application has a well-defined 
single-responsibility, which has been highlighted in the System Components section. Additionally, each class is open
for extension, but closed for modification to ensure system integrity (e.g. invariants always remain true).

UML Diagram:
![PubMed](https://user-images.githubusercontent.com/12025538/93139978-ac617800-f6af-11ea-890b-a025cf292251.png)

# Extreme Scenarios and Limitations
Depending on the JVM and XML files sizes, this application has limitations. For example, the heap size may be exceeded
for extremely large files. To handle this issue, either manually increase the heap size or ensure that the XML documents
do not exceed the capacity of the heap.

