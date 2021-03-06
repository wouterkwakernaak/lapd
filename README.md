## Project Summary

This project contains a prototype for a language-agnostic program database, using Neo4J.

It is capable of storing the types which are used in the meta-programming language Rascal (see https://github.com/cwi-swat/rascal).

## Configuration

Out of the box the database files get stored in the databases directory of your home folder. In order to change where the databases are stored, add a string called databasesDirectory containing the path to the desired location on the file system in the src/config.properties file. Example: databasesDirectory = /ufs/yourname/databases

## Testing and usage

See https://github.com/wouterkwakernaak/lapd-test

1. Setup a rascal development environment, see https://github.com/cwi-swat/rascal/wiki/Rascal-Developers-Setup---Step-by-Step. Clone the rascal and rascal-eclipse forks found at https://github.com/wouterkwakernaak?tab=repositories instead of the main versions.
2. Clone lapd and lapd-test
3. Import all projects into Eclipse
4. Run rascal-eclipse as an Eclipse Application
5. Import lapd-test in the second level Eclipse
6. Run the tests or benchmarks
