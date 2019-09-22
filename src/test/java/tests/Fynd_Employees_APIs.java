package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
	
	@BeforeTest
	public void setUp() throws ClassNotFoundException, IOException {
	
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
		
	}
	
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
	    	childTest.log(Status.PASS, "passed");	
	    }else if(result.getStatus() == ITestResult.SKIP) {
	    	childTest.log(Status.SKIP, result.getThrowable());
	    }
	    extent.flush();
//	    softAssert.assertAll();
	}
	
	
	@Test(priority=1)
	public void get_all_employees() {
		
		childTest=extent.createTest(prop.getProperty("BASE_URL")+"/api/v1/employees");
		given = given();
		Response res= given.
						when().
						get("/api/v1/employees").
						then().assertThat().statusCode(200).extract().response();
		JsonArray json = (JsonArray) new JsonParser().parse(res.asString());

		System.out.println(res.asString());
		System.out.println(json.size());
		if(json.size()==0) {
			childTest.log(Status.INFO, "No employee data");
		}else {
			childTest.log(Status.INFO, json.size()+" no of employees data found");
		}
		
		
		for(int emp=0;emp<json.size();emp++) {
			JsonObject emp_object = json.get(emp).getAsJsonObject();
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

	}

	
	@SuppressWarnings("unchecked")
	@Test(priority=2)
	public void create_employee() throws IOException {
		String excelPathOfCreateEmp = System.getProperty("user.dir")+"/input_data/payload/create_employee/create_emp.xlsx";
		ExtentTest logger=extent.createTest(prop.getProperty("BASE_URL")+"/api/v1/create");
		given = given();
		file = new FileInputStream(excelPathOfCreateEmp);
		workbook = new XSSFWorkbook(file);
		List<Object> excelData = util.getExcelData(workbook, 0);

		ArrayList<ArrayList<Object>> testCases = (ArrayList<ArrayList<Object>>) excelData.get(1);
		
        File folder = new File(System.getProperty("user.dir")+"/input_data/payload/create_employee");
 
        JsonObject res_json;
        
        for(int test=0;test<testCases.size();test++) {
        	List<Object> test_case = testCases.get(test);
        	String exp_beh = test_case.get(2).toString();
        	childTest = logger.createNode(test_case.get(0).toString()+", "+exp_beh);
//        	System.out.println(folder+"/"+test_case.get(1));
        	 
        	Response res= given.
						when().
						body(new File(folder+"/"+test_case.get(1))).
						post("/api/v1/create").
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
				}
				
				break;
		
			case "negative":
//				
				if(res.asString().contains("error")) {
					if(res.asString().contains(test_case.get(0).toString())) {
						childTest.log(Status.PASS, "employee details can't be duplicate");
					}
					childTest.log(Status.PASS, res.asString());
				}else {
					try {
						res_json = (JsonObject) new JsonParser().parse(res.asString());
						childTest.log(Status.FAIL, res.asString()+" Error is expected when we pass negative test case");
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
		
	}
	
	@Test(priority=3, enabled = false)
	public void get_employee() {
		logger = extent.createTest(prop.getProperty("BASE_URL")+"/api/v1/employee/");
		get_emp_details_test(emp_id, "with valid employee id", "positive");
		get_emp_details_test(String.valueOf(123123), "with invalid employee id", "negative");
		get_emp_details_test("&*(#*", "with unknow character employee id", "negative");
		
		
	}	
	
	public void get_emp_details_test(String emp_id, String test_name, String exp_beh) {
	
		String resource = null;
		if(emp_id.isEmpty()) {
			System.out.println("emp id is null");
			resource = "/api/v1/employee/"+emp_id.replaceAll("\"", "");
			childTest = logger.createNode(prop.getProperty("BASE_URL")+resource);
			childTest.log(Status.FAIL, "employee id can't be null");
			return;
		}else {
			resource = "/api/v1/employee/"+emp_id.replaceAll("\"", "");
			System.out.println(resource);
			childTest = logger.createNode(prop.getProperty("BASE_URL")+resource);
		}
		
		given = given();
		
		Response res = given.
							when().
							get(resource).
							then().assertThat().statusCode(200).extract().response();
		
		System.out.println(res.asString());
		childTest.log(Status.INFO, res.asString());
		
		switch (exp_beh) {
		case "positive":
			
			JsonObject json_res = (JsonObject) new JsonParser().parse(res.asString());
			String id = json_res.get("id").toString();
			Assert.assertEquals(id, emp_id);
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
		
	}
	
	
	
	@Test(priority=2)
	public void update_employee() {
	
		
	
	
	}
	public void delete_employee() {
	
	
	
	}
	
}

