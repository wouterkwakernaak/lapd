## Project Summary

This project contains prototypes for a language-agnostic program database, using a variety of database types.

At the moment the following databases are supported:
- Neo4J
- HSQLDB (work in progress)

The datatypes being stored are used in the meta-programming language Rascal (see https://github.com/cwi-swat/rascal).

## Configuration

Out of the box the databases get stored in the databases directory of your home folder. In order to change where the databases are stored, add a string called databasesDirectory containing the path to the desired location on the file system. Example: databasesDirectory = /ufs/wouterk/databases

## Testing and usage

See https://github.com/wouterkwakernaak/lapd-test
