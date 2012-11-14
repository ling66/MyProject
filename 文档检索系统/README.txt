此文件用于运行于学校unix环境说明文档。

Student ID: s3300154    Name: Ling Liu  E-mail: s3300154@student.rmit.edu.au

This document explains how to compile and run my programs on yallara.

1. please copy all the submit files into some testing directory.There are total 16 files for this assignment:
  * 12 java files;
  * 1 stoplist_sorted file which is the stop words list;
  * 1 REScoreFile which records the experiment results for expansion parameters R and E ;
  * 1 README.txt file;
  * 1 report.pdf file.
  
2. please execute this compile command under testing directory:  
   javac *.java
   
3. Now we can start testing programs:
  3.1 Generate invlists,lexicon and lexicon files for collection file:
    Please execute the following commond:
	  java index /public/courses/MultimediaInfoRetrieval/2011/a2/collection
	 
	Note: I have done some testing on yallara. The program needs about 5 minutes 36 seconds to execute. Because the session time on yallara may less than this time which running program needs, so we have to keep typing keyboard during these process otherwise the program will be terminated by yallara server. 
	 
	Explanation for this part: 
	 1. The program will generage three files. 
	  1.1 The first is "map", which contains mapping information between assign ordinal number and original document number. 
	  1.2 The second is the "lexicon", containing each unique term that occurs in the collection. 
	  1.3 The third is "invlists", which contains the inverted list information,consisting only of numerical data.
	 2. Because we assume that we will be working with collections that are small enough to fit in main memory. But I have done some testing on different machines. I found that we may meet "out of memory" problem. That's because for some environment, the initial JVM runtime main memory is too small. In order to solve this problem, we need to add some runtime parameters when we run our problem. The running commands look like this:
	 java -Xms128M -Xmx1000M index /public/courses/MultimediaInfoRetrieval/2011/a2/collection
	 Note: I have done some testing on yallara. Everything works good, so we don't need use this command on yallara. But for some other environment we may need this command to execute program.
	 
  3.2  For Ranked Retrieval Part (BM25 algorithm testing):
     Input Format: java search -BM25 -q <query-label> -n <num-results> <lexicon>  <invlists> <map> <queryterm-1> [<queryterm-2> ... <queryterm-N>]
   
     Please execute the following commond for testing:
       a. java search -BM25 -q 401 -n 20 lexicon invlists map foreign minorities germany
	   b. java search -BM25 -q 402 -n 20 lexicon invlists map behavioral genetics
	   c. java search -BM25 -q 403 -n 20 lexicon invlists map osteoporosis
	   d. java search -BM25 -q 405 -n 20 lexicon invlists map cosmic events
	   e. java search -BM25 -q 408 -n 20 lexicon invlists map tropical storms
  3.3   Query Expansion Part (PRF algorithm testing):
     Input Format: java search -PRF -q <query-label> -n <num-results> -R <number> -E <number> <lexicon> <invlists> <map> <queryterm-1> [<queryterm-2> ...<queryterm-N>]
     
     Please execute the following commond for testing:
       a. java search -PRF -q 401 -n 20 -R 10 -E 20 lexicon invlists map foreign minorities germany
	   b. java search -PRF -q 402 -n 20 -R 10 -E 20 lexicon invlists map behavioral genetics
	   c. java search -PRF -q 403 -n 20 -R 10 -E 20 lexicon invlists map osteoporosis
	   d. java search -PRF -q 405 -n 20 -R 10 -E 20 lexicon invlists map cosmic events
	   e. java search -PRF -q 408 -n 20 -R 10 -E 20 lexicon invlists map tropical storms
	Note: For part 3.2 and part 3.3, becasue I haven't done any input checking process, so please follow the input format. Otherwise you may not get the right answer. Sorry for this inconvenience.
	
4. How to open pdf Report on yallara:
  1. Install PuTTY and Xming (Windows)
  2. Create a Profile in PuTTY to connect to yallara.cs.rmit.edu.au and "Enable X11 forwarding" 
  3. Then run the command: xpdf report.pdf
  4. But if you're connecting from Linux or OSX then replace step 1 and 2 with: Open a Terminal and type ssh -X username@yallara.cs.rmit.edu.au
  
