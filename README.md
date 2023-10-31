# FirstProjectTMS
The project exequtes transactions between accounts and save results in database. It get information about transaction requests from files .xml or .json formats. If directory include files with other formats program skip them.
USING 
You should create database with name "transactionsJavaCore" for using this project. Run project and choose "create tables" from menu if it is a first run.
You can pass the file path to files with transaction requests as an input parameter. Else program asks enter the path after launcher.
Evry transaction reques file should includ only one transaction request. Information about transaction contains in fields with names "outputAccountNumber", "inputAccountNumber" and "money".
This files can include othe fields buth they will not be read by program.
The program allows exequte transactions. After this operation program create or update file "reportFile.txt". This file include information about transaction execution. Also the program allows reed this information.
The progran contains functions for adding informations about clients and accounts in database for easier testing.
There is file "sqlScripts.txt" with short sql scripts for filling clients and accounts database tables in directory "src/main/java/files";
