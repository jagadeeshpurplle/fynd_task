Before importing and running this project you must have following things

1. Java 1.8 installed
2. Maven
3. Jenkins(if required from your end to run on jenkins)
4. As per it is maven project, all the required dependencies will be in pom.xml


Project explanation : 

once you open fynd folder, you will find 2 packages insdie src/test/java
packages named as : 
	1. tests
	2. utilities
	
tests: 
	inside of this pacakge you'll find a class named "Fynd_Employees_APIs.java". This is the main class
	holding all the API's with possible test cases written

utilities : 
	inside of this pacakge you'll find 3 files, naming
		1. All_Utilities.java
			: all the commons methods written inside of this class which will be used throught the project
		2. ExcelConf.java
			: To read excel data
		3. env.properties
			: mainting basic POM concept.
			
You can find two more folders named as "input_data" and "test_reports"
	input_data : 
			this folder containg all the payloads
	test_reports : 
			HTML report that generated at the end of the script will be stored here.
			

TO RUN THE FILE : 
1. Open terminal and navigate to project folder
2. Now hit "mvn test"
			: which will run "Fynd_Employees_APIs.java" class and generate HTML report

TO VIEW THE HTML REPORT:
1. go inside "test_reprots" and open "Fynd_Employees_APIs.html" file.

Note : i've configured with Jenkins and have shown in video that i attached in the mail
