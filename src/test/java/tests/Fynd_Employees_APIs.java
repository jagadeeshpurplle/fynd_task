package tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import utilities.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import scala.util.Random;

import static io.restassured.RestAssured.given;


public class Fynd_Employees_APIs {

	
	All_Utilities util = new All_Utilities();
	ExtentHtmlReporter reporter;
	ExtentReports extent;
	ExtentTest logger;
	ExtentTest childTest;
	RequestSpecification given;
	FileInputStream file;
	XSSFWorkbook workbook;
	Properties prop;
	String emp_id = "";
	String emp_name;
	JsonArray five_employees_data = new JsonArray();
	JsonArray all_employees_data;
	JSONObject datatypesOfCreateAndUpdateEmployee = new JSONObject();
	HashMap<String, String> datatypesOfUpdateEmployee = new HashMap<String, String>();
	
	@SuppressWarnings("unchecked")
	@BeforeTest
	public void setUp() throws ClassNotFoundException, IOException {
		
		if(!util.checkIntersent()) {
			System.out.println("No internet connection, Please check and try again");
			System.exit(0);
		}
	
		Path path = Paths.get("./test_reports/task_automation_jagadeesh");
		Files.createDirectories(path);
		File f = new File("./test_reports/task_automation_jagadeesh/"+this.getClass().getSimpleName()+".html");
		f.createNewFile();
		reporter = new ExtentHtmlReporter(f);
		extent = new ExtentReports();
		extent.attachReporter(reporter);

		
		prop = new Properties();
		FileInputStream file = new FileInputStream(System.getProperty("user.dir")+"/src/test/java/utilities/env.properties");
		prop.load(file);
		
		
		RestAssured.baseURI = prop.getProperty("BASE_URL");
		reporter.config().setReportName("task@fynd : "+this.getClass().getSimpleName());
//		
		
		// If we have defined datatypes in db table, then we can make dynamic, As per this is task i'm defining like this
		datatypesOfCreateAndUpdateEmployee.put("name", "String");
		datatypesOfCreateAndUpdateEmployee.put("salary", "Integer");
		datatypesOfCreateAndUpdateEmployee.put("age", "Integer");
		datatypesOfCreateAndUpdateEmployee.put("id", "String");
		
		
	
	}
	
	@AfterMethod
	@AfterTest
	public void tearDown() throws SQLException {
		
		
		
	}
	
	
	@AfterMethod
	public void getResult(ITestResult result){
	    if(result.getStatus()==ITestResult.FAILURE){
	    	System.out.println("failure man");
	    	childTest.log(Status.FAIL, result.getThrowable());
	    }else if(result.getStatus() == ITestResult.SUCCESS) {
	    	System.out.println("success");
	    	childTest.log(Status.PASS, "Done");	
	    }else if(result.getStatus() == ITestResult.SKIP) {
	    	childTest.log(Status.SKIP, result.getThrowable());
	    }
	    extent.flush();
//	    softAssert.assertAll();
	}
	
	
	@Test(priority=1)
	public void get_all_employees() {
		
		childTest=extent.createTest(prop.getProperty("BASE_URL")+prop.getProperty("get_all_emp_resource"));
		given = given();
		Response res= given.
						when().
						get(prop.getProperty("get_all_emp_resource")).
						then().assertThat().statusCode(200).extract().response();
		all_employees_data = (JsonArray) new JsonParser().parse(res.asString());

		System.out.println(res.asString());
		System.out.println(all_employees_data.size());
		if(all_employees_data.size()==0) {
			childTest.log(Status.INFO, "No employee data");
		}else {
			childTest.log(Status.INFO, all_employees_data.size()+" no of employees data found");
		}
		
		
		for(int emp=0;emp<all_employees_data.size();emp++) {
			JsonObject emp_object = all_employees_data.get(emp).getAsJsonObject();
			five_employees_data.add(emp_object);
			if(emp==5) {
				break;
			}
		}
		
		String message = "For your reference, showing "+five_employees_data.size()+" employees data below<br>";
		for(int e=0;e<five_employees_data.size();e++) {
			JsonObject e_object = five_employees_data.get(e).getAsJsonObject();
			System.out.println(e_object.toString());
			message = message+e_object.toString()+"<br>";
		}
		childTest.log(Status.PASS, message);
		util.print_star();
	}

	
	@SuppressWarnings("unchecked")
	@Test(priority=3)
	public void create_employee() throws Exception {
		ExtentTest logger=extent.createTest(prop.getProperty("BASE_URL")+prop.getProperty("create_emp_resource"));
		given = given();
        File folder = new File(System.getProperty("user.dir")+"/input_data/payload/create_employee");

        
        JsonObject res_json;
		String file_path = "./input_data/payload/create_employee";
		String valid_file_name = "create_emloyee_with_valid_data.json";
		String create_emplValid =file_path+"/create_emloyee_with_valid_data.json";

        
		util.create_payload_for_update(util.getRandomName(), 950000, 22, 213, create_emplValid);

		
		Object[][] test_data = {
				{"create_emloyee_with_valid_data",valid_file_name,"positive"},
		};
		util.writeToExcel("create_emp", test_data,file_path+"/create_emp.xlsx");
		
		
		file = new FileInputStream(file_path+"/create_emp.xlsx");
		workbook = new XSSFWorkbook(file);
		List<Object> excelData = util.getExcelData(workbook, 0);
		ArrayList<ArrayList<Object>> testCases = (ArrayList<ArrayList<Object>>) excelData.get(1);
		System.out.println("test cases : "+testCases.size());
		
		
        for(int test=0;test<testCases.size();test++) {
        	List<Object> test_case = testCases.get(test);
        	System.out.println("Test : "+test_case.toString()+"\n"+util.getPayloadFromFile(folder+"/"+test_case.get(1)));
        	JSONObject data = util.read_json_file(folder+"/"+test_case.get(1));
        	String exp_beh = test_case.get(2).toString();
        	childTest = logger.createNode(test_case.get(0).toString()+", "+exp_beh);
        	datTypeCheck(data, datatypesOfCreateAndUpdateEmployee);
//        	System.out.println(folder+"/"+test_case.get(1));
        	childTest.log(Status.INFO, "creating employe with<br>"+util.getPayloadFromFile(folder+"/"+test_case.get(1)));
        	
        	
        	
        	Response res= given.
						when().
						body(new File(folder+"/"+test_case.get(1))).
						post(prop.getProperty("create_emp_resource")).
						then().extract().response();
        	
        	
			try {
				Assert.assertEquals(res.statusCode(), 200,", Status code error in "+test_case);
			} catch (AssertionError e) {
				System.out.println(e.getMessage());
				childTest.log(Status.FAIL, e.getMessage());
			}
        	
			
        	switch (exp_beh) {
			case "positive":
				if(res.asString().contains("error")) {
					childTest.log(Status.FAIL, res.asString());
				}else {
					res_json = (JsonObject) new JsonParser().parse(res.asString());
					emp_id = res_json.get("id").toString();
					emp_name = res_json.get("name").getAsString();
					childTest.log(Status.PASS, "Successfully created employee with<br>"+util.prettyJson(res));
					JSONObject object = (JSONObject) new JSONParser().parse(res_json.toString());
					datTypeCheck(object, util.data_types_on_success());
				}
				break;
			case "negative":
				if(res.asString().contains("error")) {
					if(res.asString().contains(test_case.get(0).toString())) {
						childTest.log(Status.PASS, "employee details can't be duplicate");
					}
					childTest.log(Status.PASS, res.asString());
				}else {
					try {
						res_json = (JsonObject) new JsonParser().parse(res.asString());						
						childTest.log(Status.FAIL, res.asString()+"<br> Error is expected when we pass negative test case");
					} catch (Exception e) {
						childTest.log(Status.FAIL, res.asString());
					}
				}
				break;
				
			default:
				break;
			}
        	childTest.log(Status.INFO, "Response : "+res.asString());	
        	System.out.println(res.asString());
			util.print_line();
	        	
        }
        util.print_star();
		 
	}
	
	@SuppressWarnings("unchecked")
	@Test(priority=2)
	public void get_employee() throws InvalidFormatException, IOException, ParseException {
		
		Random rand = new Random();
		int rand_no = rand.nextInt(all_employees_data.size());
		JsonObject random_employee = all_employees_data.get(rand_no).getAsJsonObject();
		int id = Integer.parseInt(random_employee.get("id").toString().replaceAll("\"", ""));
		System.out.println(random_employee.toString());
		String getAPI = prop.getProperty("BASE_URL")+prop.getProperty("get_emp_resource");
		logger = extent.createTest(getAPI);
		String file_path = "./input_data/payload/get_employee";
		int randomNo = util.randomNumber();
		
		
		Object[][] test_data = {
				{"get_employee_valid_id",id,"positive"},
				{"get_employee_invalid_id",randomNo,"negative"},
		};
		util.writeToExcel("get_employee", test_data,file_path+"/get_employee.xlsx");
		
		
		file = new FileInputStream(file_path+"/get_employee.xlsx");
		workbook = new XSSFWorkbook(file);
		List<Object> excelData = util.getExcelData(workbook, 0);
		ArrayList<ArrayList<Object>> testCases = (ArrayList<ArrayList<Object>>) excelData.get(1);
		System.out.println("total no of Test cases : "+testCases.size());
        JsonObject res_json;
        
        for(int test=0;test<testCases.size();test++) {
        	List<Object> test_case = testCases.get(test);
        	String exp_beh = test_case.get(2).toString();
        	Object emp_id = test_case.get(1);
        	childTest = logger.createNode(test_case.get(0).toString()+", "+exp_beh);
        	childTest.log(Status.INFO, "getting details of `"+emp_id+"` employee");
        	
        	
        	Response res = given.
					when().
					get(getAPI+emp_id).
					then().extract().response();

        	
        	System.out.println(res.asString());
        	childTest.log(Status.INFO, "Response : "+res.asString());	
        	

			try {
				Assert.assertEquals(res.statusCode(), 200,", Status code error in "+test_case);
			} catch (AssertionError e) {
				System.out.println(e.getMessage());
				childTest.log(Status.FAIL, e.getMessage());
				return;
			}
        	
			switch (exp_beh) {
			case "positive":
				JsonObject json_res = (JsonObject) new JsonParser().parse(res.asString());
				String res_id = json_res.get("id").toString();
				JSONObject object = (JSONObject) new JSONParser().parse(json_res.toString());
				datTypeCheck(object, util.data_types_on_success_for_get_employee());
				AssertJUnit.assertEquals(res_id.replaceAll("\"", ""), emp_id);
				childTest.log(Status.PASS, res.asString());
				break;
			case "negative":
				if(res.asString().contains("error") || res.asString().equals("false")) {
					childTest.log(Status.PASS, res.asString());
				}
				break;
			default:
				break;
			}
        	
		
			util.print_line();
	        	
        }
        util.print_star();
//        extent.flush();
	}	
	
	
	
	@Test(priority=4)
	public void update_employee() throws IOException, InvalidFormatException, ParseException {
		given = given();
		Random rand = new Random();
		int rand_no = rand.nextInt(all_employees_data.size());
		JsonObject random_employee = all_employees_data.get(rand_no).getAsJsonObject();
		String random_employee_name = random_employee.get("employee_name").toString();
		int id = Integer.parseInt(random_employee.get("id").toString().replaceAll("\"", ""));
		System.out.println(random_employee.toString());
		
		
		String updateAPI = prop.getProperty("BASE_URL")+prop.getProperty("update_emp_resource");
		logger = extent.createTest(updateAPI);
		childTest = logger.createNode(updateAPI+id);
		childTest.log(Status.INFO, "Taken below employee to check update API<br>"+random_employee.toString());
		
		
		String file_path = "./input_data/payload/update_employee";
		String update_emplValid =file_path+"/update_employee_valid_data.json";
		String update_empl_irsp_dt=file_path+"/update_employee_irrespective_datatypes.json";
		String update_emp_unknwn =file_path+"/update_employee_unknown_chars.json";
		String update_emp_empty =file_path+"/update_employee_empty_data.json";
		util.create_payload_for_update(random_employee_name.replaceAll("\"", "")+"salt", 950000, 22, 213, update_emplValid);
		util.create_payload_for_update(util.randomNumber()+13, 950000, 22, 213, update_empl_irsp_dt);
		util.create_payload_for_update("&*&*^(*("+"salt", 950000, 22, 213, update_emp_unknwn);
		util.create_payload_for_update("  ", 950000, 22, 213, update_emp_empty);

		Object[][] test_data = {
				{"update_employee_valid_data",update_emplValid,"positive"},
		};
		util.writeToExcel("update_employee", test_data,file_path+"/update_emp.xlsx");
		
		
		file = new FileInputStream(file_path+"/update_emp.xlsx");
		workbook = new XSSFWorkbook(file);
		List<Object> excelData = util.getExcelData(workbook, 0);

		ArrayList<ArrayList<Object>> testCases = (ArrayList<ArrayList<Object>>) excelData.get(1);
		System.out.println("test cases :"+testCases.size()); 		
        JsonObject res_json;
        
        
        for(int test=0;test<testCases.size();test++) {
        	List<Object> test_case = testCases.get(test);
        	String exp_beh = test_case.get(2).toString();
        	childTest = logger.createNode(test_case.get(0).toString()+", "+exp_beh);

        	Response res = given.
					body(new File(file_path+"/"+test_case.get(1))).
					when().
					put(updateAPI+id).
					then().assertThat().statusCode(200).extract().response();

        	System.out.println(res.asString());
        	childTest.log(Status.INFO, "Response : "+res.asString());	

        	
			try {
				Assert.assertEquals(res.statusCode(), 200,", Status code error in "+test_case);
			} catch (AssertionError e) {
				System.out.println(e.getMessage());
				childTest.log(Status.FAIL, e.getMessage());
			}
			
        	
        	switch (exp_beh) {
			case "positive":
				if(res.asString().contains("error")) {
					childTest.log(Status.FAIL, res.asString());
				}else {
					res_json = (JsonObject) new JsonParser().parse(res.asString());
					emp_name = res_json.get("name").getAsString();
					JSONObject object = (JSONObject) new JSONParser().parse(res_json.toString());
					datTypeCheck(object, util.data_types_on_success());
					Assert.assertNotEquals(emp_name, random_employee_name);
					childTest.log(Status.PASS, "Successfully updated employee with<br>"+util.prettyJson(res));
					
				}
				
				break;
		
			case "negative":
				if(res.asString().contains("error")) {
					childTest.log(Status.PASS, res.asString());
				}else {
					try {
						res_json = (JsonObject) new JsonParser().parse(res.asString());
						JSONObject object = (JSONObject) new JSONParser().parse(res_json.toString());
						datTypeCheck(object, util.data_types_on_success());
						emp_name = res_json.get("name").getAsString();
						try {
							AssertJUnit.assertEquals(emp_name, random_employee_name);
							childTest.log(Status.PASS, "No updated as we passed negative scenario<br>"+util.getPayloadFromFile(file_path+"/"+test_case.get(1)));
						} catch (AssertionError e) {
							childTest.log(Status.FAIL, "should not get updated when we pass negative scenario as<br>"+util.getPayloadFromFile(file_path+"/"+test_case.get(1)));
						}
					} catch (Exception e) {
						childTest.log(Status.FAIL, res.asString());
					}
				}
				break;
			default:
				break;
			}
			util.print_line();        	
        }
        util.print_star();
		
		
	}
	
	
	@Test(priority=5)
	public void delete_employee() {
	
		given = given();
		Random rand = new Random();
		int rand_no = rand.nextInt(all_employees_data.size());
		JsonObject random_employee = all_employees_data.get(rand_no).getAsJsonObject();
		String random_employee_name = random_employee.get("employee_name").toString();
		int id = Integer.parseInt(random_employee.get("id").toString().replaceAll("\"", ""));
		
		System.out.println(random_employee.toString());
		String deleteAPI = prop.getProperty("BASE_URL")+prop.getProperty("delete_emp_resource");
		logger = extent.createTest(deleteAPI+id);
		logger.log(Status.INFO, "Taken below employee to DELETE<br>"+random_employee.toString());
		
		Response res = given.
							when().	
							delete(deleteAPI+id).
							then().assertThat().extract().response();
		
		System.out.println(res.asString());
		try {
			AssertJUnit.assertEquals(res.statusCode(), 200);
			JsonObject res_json = (JsonObject) new JsonParser().parse(res.asString());
			JsonObject success =  res_json.get("success").getAsJsonObject();
			String text = success.get("text").toString();
			AssertJUnit.assertEquals(text, "\"successfully! deleted Records\"");
			logger.log(Status.PASS, text);
			
		} catch (AssertionError e) {
			logger.log(Status.FAIL, "status code error: "+res.statusCode()+"<br>"+e.getMessage());
			System.out.println(e.getMessage());
		}
		
		
	
	
	}
	
	
	private void datTypeCheck(JSONObject actualValues, JSONObject datatypesOfCreateAndUpdateEmployee) {

		for(Object key : actualValues.keySet()) {
			Object value = actualValues.get(key);
			System.out.println(value+", "+datatypesOfCreateAndUpdateEmployee.get(key));
			System.out.println(value.getClass().getSimpleName() +", "+datatypesOfCreateAndUpdateEmployee.get(key));
			try {
				Object dt = value.getClass().getSimpleName();
				if(dt.equals("Long")) {
					AssertJUnit.assertEquals(value.getClass().getSimpleName(), "Long");
				}else {
					AssertJUnit.assertEquals(value.getClass().getSimpleName(), datatypesOfCreateAndUpdateEmployee.get(key));	
				}
				System.out.println("Datatype validation success");	
				childTest.log(Status.PASS, "data type validation for "+value+" is success");
			} catch (AssertionError e) {
				System.out.println(e.getMessage());
				childTest.log(Status.WARNING, e.getMessage()+"<br>"+key+" datatype must be "+datatypesOfCreateAndUpdateEmployee.get(key)+"<br>But detected datatype is : "+value.getClass().getSimpleName());
			}
			
		}
		
	}
	
}


