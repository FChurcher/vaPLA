# vaPLA
A General Framework for Partially Local Multi-Way Alignments

Multiple sequence alignments are a crucial intermediate step in a plethora of data analysis workflows in computational biology. While multiple sequence alignments are usually constructed with the help of heuristic approximations, while pairwise alignments are typically computed by exact dynamic programming algorithms. In the pairwise case, local, global, and semi-global alignments are distinguished, with key applications in pattern discovery, gene comparison, and homolgy search, respectively. With increasing computing power, exact alignments of triples and even quadruples of sequences have become feasible and recent applications e.g. breakpoint discovery have shown that mixed local/global multiple alignments can be of practical interest.
GLOCAL is the first implementation of partially local multiple alignments of a few sequences and provides convenient access to this family of specialized alignment algorithms.
source: https://github.com/Nunca131/implementationGLOCAL


example usage:
java -jar vaPLA.jar data/example

help:
java -jar vaPLA.jar

examples in data/


Benchmark Databases:

oxbench: http://www.compbio.dundee.ac.uk/downloads/oxbench/

balibase: http://www.lbgi.fr/balibase/

