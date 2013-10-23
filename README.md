## Project Summary

This project contains a prototype for a language-agnostic program database, using Neo4J.

It is capable of storing the types which are used in the meta-programming language Rascal (see https://github.com/cwi-swat/rascal).

## Configuration

Out of the box the database files get stored in the databases directory of your home folder. In order to change where the databases are stored, add a string called databasesDirectory containing the path to the desired location on the file system in the src/config.properties file. Example: databasesDirectory = /ufs/yourname/databases

## Testing and usage

See https://github.com/wouterkwakernaak/lapd-test

1. Setup a rascal development environment, see https://github.com/cwi-swat/rascal/wiki/Rascal-Developers-Setup---Step-by-Step. Clone the rascal fork found at https://github.com/wouterkwakernaak/rascal instead of the main version.
2. Clone lapd and lapd-test
3. Import all projects into Eclipse
4. Change the Circular Dependencies setting to 'Warning' instead of 'Error'. This setting can be found in Eclipse by navigating to Window -> Preferences -> Java -> Compiler -> Building -> Build path problems.
5. Run rascal-eclipse as an Eclipse Application
6. Import lapd-test in the second level Eclipse
7. Run the tests by importing a module of your choice and typing :test in the console or run the benchmarks by importing the Benchmarks module.
