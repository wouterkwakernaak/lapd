## Project Summary

This project contains prototypes for a language-agnostic program database, using a variety of database types. 
The datatypes being stored are used in the meta-programming language Rascal (see https://github.com/cwi-swat/rascal).

## Dependencies

This project requires the sources of the pdb.values library, get it at https://github.com/impulse-org/pdb.values

## Configuration

Out of the box the databases get stored in the databases directory of your home folder. In order to change where the databases are stored, add a string called databasesDirectory containing the path to the desired location on the file system. Example: databasesDirectory = /ufs/wouterk/databases
