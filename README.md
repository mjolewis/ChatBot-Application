# ChatBot Application

# Description
The ChatBot application is used to respond to user queries. The underlying knowledge base is represented as a single XML 
document that can be created from multiple source XML documents being fed into the system.  

For sample purposes, the application provides multiple data stores including, MySQL, MongoDB, and Lucene Index. In
addition to being able to query these databases, the application also provides a brute force force searching mechanism 
to highlight how inefficient it is relative to the more suitable data access mechanisms provided by the databases.

The current application logic follows a classification logic, that is, it groups file objects into one master aggregate 
and then uses that aggregate file to search for a keyword. When the searching is complete, the simulation results are 
packaged into Article aggregates. These Article aggregates can be retrieved by the user and subsequently added to the 
client view.

Finally, the runtime of all client initiated queries can be graphed and, therefore, the application also enables a 
visual analysis of the efficiency of each approach.

# Usage
This application comes with an executable WAR file that can be downloaded from the target directory. More specifically, 
./target/ChatBot-1.0-SNAPSHOT.war. Follow the following instructions to use the application
1) Download the WAR file and CD into the downloaded directory
2) Run the 'java -jar ChatBot-1.0-SNAPSHOT.war' command
3) A file chooser will open. Select the XML files that will become the underlying knowledge base
4) Launch a web browser and navigate to http://localhost:8080/? to interact with the ChatBot

Since this code is for demonstration only, the application has been specialized to work with files from 
ftp://ftp.ncbi.nlm.nih.gov/pubmed/ but can easily be extended to work with XML files from any source. Keep in mind, 
that working with files from other sources will require minor modifications to the program. In particular, several 
variables in the Constants class need to be updated, along with the data model class (Articles in this case).

# System Components
***Builder***\
The Builder class serves as a mediator method that puts the simulation together. It has four public methods and two 
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

The XMLParser's sole job is parse a master XML document into Article objects. After creating the Article objects, the
XMLParser will activate database builds if the databases don't already exist. The purpose of this is to prepopulate the 
databases with Article data before the client interacts with the web application.

Note that the Parser relies on the Java StAX API, which exposes methods for iterative, event-based processing of XML 
documents. Please visit https://docs.oracle.com/javase/tutorial/jaxp/stax/api.html for more information.

For more details on Lucene, please see the Indexer class or visit https://lucene.apache.org/core/.

***Article***\
The Article class provides a simplified data model of the XML documents. It's sole job is to represent an article that
was successfully parsed by the Parser. 

Importantly, this class must be updated to accurately reflect the data model if the underlying knowledge base changes. 

***ApplicationConfig***\
The ApplicationConfig class provides the application with access to shared resources such as data constants.

***WebSocketConfig***\
The WebSocketConfig class is solely responsible for configuring and enable WebSocket and STOMP messaging. The purpose
of this class is to create the foundation for an interactive web application that uses message brokers and STOMP 
(simple text oriented messaging protocol that sits on top of a lower level WebSocket)

***ClientMessage***\
The ClientMessage class provides a simplified data model of the client message. This message is routed to the 
QueryController or the OutputController which then use the @MessageMapping annotation to determine which method to 
activate.

***QueryResult***\
The QueryResult class provides a simplified data model of the server response. This class packages a set of information 
that contains the answer to the query requested by the client. After packaging, the QueryController uses the @SendTo 
annotation to determine which route to send the response to. The message is then used by the client subscribers to 
update the view.

***QueryController***\
The QueryController class is an annotated class that works with Spring to route STOMP messages. It handles a web 
request and uses the @MessageMapping annotation to route that request to the appropriate destination. In particular, 
it handles the /query messages and subsequently works with the MySQL, MongoDB, LuceneSearch, and BruteForce classes to
perform a keyword range query specified by the client. 

Finally, it can use the @SendTo annotation to respond to the client with a message that updates the view. This message 
is packaged into a QueryResult.

***OutputController***\
The OutputController class is an annotated class that works with Spring to route STOMP messages. It handles a web 
request and uses the @MessageMapping annotation to route that request to the appropriate destination. In particular, 
it handles the /graph message and subsequently works with the Graph class to plot the runtime results of all queries.

This class does not need to update the view and, therefore, does not send a message back to the client.

***LuceneIndex***\
The LuceneIndex class is the core functionality of the Lucene index. It builds an inverted index data structure for 
efficient search capabilities over a corpus of documents. This is similar to how a textbook provides an index at the 
end of the book as a way to efficiently identify pages that contain keywords. In contrast, brute force search is 
analogous to reading every word in the body of the text as a way to identify keywords. This inverted index is stored on 
disk within the index_directory sub-folder.

***LuceneSearch***\
The LuceneSearch class is the final component of the Lucene index. Its sole responsibility is to give the 
QueryController access to various query options. This class supplements the various other databases that are provided 
to demonstrate how to use Lucene in a Java application. 

***MySQL***\
The MySQL class is responsible for building a relational database as well as giving the QueryController access to 
various query options. This class supplements the various other databases that are provided to demonstrate how to use 
MySQL in a Java application. 

***MongoDB***\
The MongoDB class is responsible for building a document database as well as giving the QueryController access to 
various query options. This class supplements the various other databases that are provided to demonstrate how to use 
MongoDB in a Java application.

***BruteForce***\
The BruteForce class is responsible for brute force parsing an XML document as well as giving the QueryController 
access to various query options. This class supplements the various other databases that are provided to demonstrate 
how inefficient a brute force mechanism is relative to MySQL, MongoDB, and Lucene.

***Log***\
The Log class is responsible for logging critical information about the client requests. For example, the client 
sends a request to perform a keyword range query and the Log will automatically store the DAO that was used along with 
keyword and the amount of time the search took.

The log file can then be analyzed to study the runtime performance of the various databases. In particular, the Graph 
class will extract data from the log and plot the runtime results. This is useful because it provides an easy and 
efficient way to measure the runtime performances.

***Graph***\
The Graph class is responsible for plotting the search performances. For each interaction with the client, the 
application performs four queries for the four different data stores. As a result, the Graph class plots four different 
runtime results for each interaction with the client. 

Importantly, the Graph will show the entire history of client interactions because the Log class stores every 
interaction.

# System Design
The system architecture is based on SOLID principles. Each class within the application has a well-defined 
single-responsibility, which has been highlighted in the System Components section. Additionally, each class is open
for extension, but closed for modification to ensure system integrity (e.g. invariants always remain true).

UML Diagram:
![Chatbot](https://user-images.githubusercontent.com/12025538/100526763-04c9f100-319a-11eb-93b3-448f71307b85.png)

# Extreme Scenarios and Limitations
Depending on the JVM and XML files sizes, this application has limitations. For example, the heap size may be exceeded
for extremely large files. To handle this issue, either manually increase the heap size or ensure that the XML documents
do not exceed the capacity of the heap.

# Output Analysis
Please check the Log folder for sample output in .csv format. Alternatively, you may run the application and interact 
with the client to have a runtime graph automatically built.

