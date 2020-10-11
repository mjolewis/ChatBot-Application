# ChatBot Application with Java StAX and Lucene

# Description
The ChatBot application is used to respond to user queries. The underlying knowledge base is represented as a single XML 
document that can be created from multiple source XML documents being fed into the system.  

For sample purposes, the application provides both a brute force searching mechanism as well as ultra-fast search 
capabilities via the Lucene search library.

The current application logic follows a classification logic, that is, it groups file objects into one master aggregate 
and then uses that aggregate file to search for a keyword. When the searching is complete, the simulation results are 
packaged into Article aggregates. These Article aggregates can be retrieved by the user and subsequently printed to the 
console or otherwise repacked into an output file. 

Finally, all search data is persisted onto disk.

# Usage
For demonstration purposes, this application has been specialized to work with files from 
ftp://ftp.ncbi.nlm.nih.gov/pubmed/ but can easily be extended to work with XML files from any source. Keep in mind, 
that working with files from other sources will require minor modifications to the program. In particular, several 
variables in the Constants class need to be updated, along with the data model class (Articles in this case).

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

***XMLParser***\
The Parser has been specialized to parse XML documents from ftp://ftp.ncbi.nlm.nih.gov/pubmed/. However, the class
can easily be extended to parse XML documents from any source. Please see the Usage section for more information.

The XMLParser's sole job is to receive a search parameter and parse the XML document in search of the specified search 
parameter. Once found, an Article object will be created with the the title of the article, it's publication date, and 
publication ID.

Note that the Parser relies on the Java StAX API, which exposes methods for iterative, event-based processing of XML 
documents. Please visit https://docs.oracle.com/javase/tutorial/jaxp/stax/api.html for more information.

For illustrative purposes, the XMLParser has both brute force and Lucene search mechanisms. These searching capabilities 
are for instructional purposes only to highlight why efficient search capabilities are a necessary requirement for 
production systems. As a result, ensure that the brute force searching method is removed prior to deploying the 
application to production.

For more details on Lucene, please see the Indexer class or visit https://lucene.apache.org/core/.

***Article***\
The Article class provides a simplified data model of the XML documents. It's sole job is to represent an article that
was successfully parsed by the Parser. 

Importantly, this class must be updated to accurately reflect the data model if the underlying knowledge base changes. 

***Storage***\
The Storage class takes input from the Parser class and provides in-memory and disk storage solutions. Importantly, if 
the application has previously stored data on disk, the application will first restore that data into memory to give 
the user fast access to all previous search results.

Finally, the application automatically persists search results onto disk. Despite the cost of I/O operations, storing 
the search results to disk happens immediately to ensure system integrity. This approach prevents data loss if an event 
that causes a loss of volatile storage occurs while the application is still running.

***Indexer***\


Finally, Lucene builds an inverted index data structure to provide efficient search capabilities over a corpus of 
documents. This is similar to how a textbook provides an index at the end of the book as a way to efficiently identify 
pages that contain keywords. In contrast, brute force search is analogous to reading every word in the body of the text 
as a way to identify keywords. This inverted index is stored on disk within the index_directory sub-folder.

# System Design
The system architecture is based on SOLID principles. Each class within the application has a well-defined 
single-responsibility, which has been highlighted in the System Components section. Additionally, each class is open
for extension, but closed for modification to ensure system integrity (e.g. invariants always remain true).

UML Diagram:
![ChatBot](https://user-images.githubusercontent.com/12025538/95683670-b6956a00-0bba-11eb-9f25-e5905266f108.png)

# Extreme Scenarios and Limitations
Depending on the JVM and XML files sizes, this application has limitations. For example, the heap size may be exceeded
for extremely large files. To handle this issue, either manually increase the heap size or ensure that the XML documents
do not exceed the capacity of the heap.

# Output Analysis
Please check the search_history file for sample output in .csv format

