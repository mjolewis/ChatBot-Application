# GorillaGame
# Student Information
Author: Michael Lewis\
Date: September 16, 2020\
Homework: PubMed

# Description
This application can be used by those interested in merging documents (XML in particular) and parsing that merged
document while searching for a specified keyword.

# System Analysis


***FileMerger***\
The FileMerger class creates BufferedReaders and BufferedWriters to merge N XML documents. Importantly, this class
appropriately handles the occurrence of multiple Prolog tags and Root tags. More specifically, each well-formed XML
document can only contain one Prolog tab and one closing Root tag.

The FileMerger core functionality resides in the merge method. The remaining public methods are simple Accessor and 
Mutators provided for convenience. 

***PubMedParser***\
The PubMedParser has been specialized to parse XML documents from ftp://ftp.ncbi.nlm.nih.gov/pubmed/. However, the
class can easily be extended to parse XML documents from any source.

The PubMedParser's sole job is to receive a search parameter and parse the XML document to search for the specified
search parameter. Once found, an Article object will be created with the the title of the article and it's publication
date

***Article***\
The Article class provides a simplified data model of the XML documents. It's sole job is to represent an article that
was successfully parsed by the PubMedParser.  

# System Design


UML Diagram:

