# devselector
A tool for allocating participants in the Jaguar experiments

Instructions for installing and using devselector

1. This project depends on the allocation algorithm developed by the Prof. Marcelo Lauretto, written in R. Alternatively, the group selection could be randomized. See the Experiment.callSequencialAllocationMethod() for details.

2. Create a folder  structure <path>/webfiles/io for the allocation files. The webfiles folder will contain allocation.sh and drvalloc01.r files. The io folder will contain alpha.txt, q.txt, and w.txt files. Also, devselector will create the allocation files inside io, which includes freq.csv and matrix.csv.
- allocation.sh: script that calls drvalloc01.r
- drvalloc01.r: allocation algorithm responsible for selecting a group for the participant
- alpha.txt: score of randomness for the algorithm, which varies from 0.0 to 1.0. A lower score means less randomness.
- q.txt: indicates the variance of each feature for the allocation. Our features are java experience (3), IDE experience (4), and JUnit experience (4). The values varies from 0 to 3, respectively, 0 - no experience, 1 - basic, 2  - intermediate, 3 - advanced 
- w.txt: weight of each feature, respectively, Java, IDE, and JUnit.
- freq.csv: contains the distribution frequency of the participants through the groups.
- matrix.csv: contains the questionnaires' responses of each participants, including their ID numbers.

3. Choose the path for the webfiles/io folder in the variable DIR_PATH_ABOVE in Experiment class. Also, choose the path for the folder that will store the Histogram image, var WEB_PATH in Monitor class.

3. To run drvalloc01, you need the have R installed in the server, including the package robCompositions -> install.packages('robCompositions')
4. In allocation.sh, choose the folder that contains the Rscript bin to run drvalloc01.sh -> R_SCRIPT_DIR=/usr/local/bin

4. devselector runs over tomcat8

5. install the vm files on a folder outside from the devselector and include it in the tomcat config file. 

6. Don't forget to enable port 80 in the server.xml file if you want to use it -> uncomment and change #AUTHBIND=no to yes


