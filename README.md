# 2SA-Algorithms-AI

## Algorithms in Artificial Intelligence Project

This repository contains an implementation of Bayes Ball and Variable Elimination algorithms on a Bayesian Network.

-----

### Classes:

1. ``Ex1``- contains the main function that executes the queries
2. ``FileParser``- parses the XML file into a data structure and retrieves the queries
3. ``VariableNode``- a node data structure for storing the data of a variable in the network
4. ``CPT``- a data structure for storing the conditional probability tables (for Variable Elimination)
5. ``BayesBallAlgo``- implementation of the Bayes Ball algorithm
6. ``VariableEliminationAlgo``- implementation of the Variable Elimination algorithm

``NetworkAlgo`` is an **interface** that the algorithm classes implement.

-----

### Dependencies:

Runs on **Java 8**.

-----

### File Samples:

The ``samples`` directory contains example files that can be used to run the algorithms:

* The ``xml`` file represents the Network and should be the first line in the text file used as an input.
* The ``input`` files contain the xml path and the network queries.
* The ``output`` files contain the expected results.