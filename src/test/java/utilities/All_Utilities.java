package utilities;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import scala.util.Random;

public class All_Utilities {

	FileInputStream file;
	DataFormatter dataFormatter = new DataFormatter();
	RequestSpecification given;
	

	
	public String prettyJson(Response res) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(res.asString());
		String prettyJsonString = gson.toJson(je);
//		System.out.println(prettyJsonString);
		return prettyJsonString;
	}

	
	public JsonObject parse_json(String data) {
		
		JsonObject json = (JsonObject) new JsonParser().parse(data);
		return json;
		
	}
	
	public void buildParam(ArrayList<String> columnNamesOfParams, ArrayList<String> cases) {
		
		given = given();
		
		for(int i=0;i<cases.size();i++) {
			System.out.println(columnNamesOfParams.get(i)+" : "+cases.get(i));
			given.param(columnNamesOfParams.get(i),cases.get(i));
		}		
	}
	
	
	public List<Object> getExcelData(XSSFWorkbook workbook, int sheetNo) {
		ArrayList<ArrayList<Object>> testCases= new ArrayList<ArrayList<Object>>();
        Iterator<Row> rowIterator = workbook.getSheetAt(sheetNo).rowIterator();
        int j=0;
        boolean first = true;
        int noOfColumns=0;
        
        ArrayList<String>  columnNamesOfParams = new ArrayList<String>();
    	ArrayList<Object> values;
    	Sheet sheet = workbook.getSheetAt(sheetNo);
    	int rows = sheet.getLastRowNum();
    	System.out.println("No of rows : "+rows);
       
		for(int r = 0;r<=rows;r++) {
        	j++;
//        	System.out.println("row: "+j);
        	Row row = sheet.getRow(r);
        	Iterator<Cell> cellIterator = row.cellIterator();
        	
        	
        	if(first) {
            	while (cellIterator.hasNext()) {
            		Cell cell = cellIterator.next();
            		if(!cell.toString().equals("")) {
            			noOfColumns++;
                		String cellValue = dataFormatter.formatCellValue(cell);
                
                		columnNamesOfParams.add(cellValue.trim());
                		
                		
//                		System.out.print(cellValue + "\t");
            		}else {
            			break;
            		}
            	}	
//                System.out.println();
//                System.out.println("No of columns: "+noOfColumns); 	
            	first=false;
            }else {
            	values = new ArrayList<Object>();
            	String Negative;
            	for(int i=0;i<noOfColumns;i++) {
            		if(!cellIterator.hasNext()) {
            			continue;
            		}
            		Cell cell = cellIterator.next();
                    String cellValue = dataFormatter.formatCellValue(cell);                    
//                    map.put(columnNames.get(i).trim(), cellValue.trim());
                    values.add(cellValue.trim());
//                    System.out.print(cellValue + "\t");                    
 /*                   if(i==noOfColumns-1 && name=="params") {
                    	cell = cellIterator.next();
                        cellValue = dataFormatter.formatCellValue(cell);
                    	Negative = cellValue;
                    	System.out.println(Negative);
                    	values.add(Negative);
                    }*/
            	}
          
            	testCases.add(values);
            	
            
//            	System.out.println();
            }		
        }
		
		return Arrays.asList(columnNamesOfParams, testCases);
		
	}
	
	
	public void print_line() {
		System.out.println("-----------------------------------------------------");
	}
	
	public void print_star() {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");	
	}
	
    
    public ResultSet execute_Query(Connection connn,String query) throws SQLException {
    	    	
		Statement st = connn.createStatement();
		
		ResultSet rs=st.executeQuery(query);
		
		return rs;
    }
   
    public String create_new_employee() {
    	
    	JsonObject json = new JsonObject();
    	json.addProperty("name", "chinna");
    	json.addProperty("salary", "1000000");
    	json.addProperty("age", "23");
    	json.addProperty("id", "23");
    	
    	return json.toString();

    }
    
    public String getPayloadFromFile(String file)throws Exception 
    { 
      String payload = ""; 
      payload = new String(Files.readAllBytes(Paths.get(file))); 
      return payload; 
    } 
    
    
    @SuppressWarnings("unchecked")
	public FileWriter create_payload_for_update(Object name, long salary, int age, int id, String file_name) throws IOException {
    	
    	JSONObject json = new JSONObject();
    	json.put("name", name);
    	json.put("salary", salary);
    	json.put("age", age);
    	json.put("id", id);
    	
    	FileWriter f = write_json_object_to_file(json, file_name);
    	return f;
    }
    
    public FileWriter write_json_object_to_file(JSONObject object, String file_name) throws IOException {
    	File f = new File(file_name);
    	f.createNewFile();
    	FileWriter file = new FileWriter(f);
        try {
        	file.write(object.toJSONString());
    		System.out.println("data written to JSON : " +object);
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}finally {
    		file.flush();
    		file.close();
		}
        return file;
    }
    
    
    public void writeToExcel(String sheetName,Object[][] data, String filePath) throws IOException, InvalidFormatException {
          
         FileInputStream inputStream = new FileInputStream(new File(filePath));
         Workbook workbook = WorkbookFactory.create(inputStream);

         Sheet sheet = workbook.getSheet(sheetName);

         int rowCount = sheet.getLastRowNum();
         System.out.println("row count : "+rowCount);
          
         for (Object[] object : data) {
             
             int columnCount = 0;
        
             String test_name = object[0].toString();
             if(check_test_case_existOrNot(workbook, sheet, test_name)) {
            	 continue;
             }else {
            	 Row row = sheet.createRow(++rowCount);
            	 for (Object field : object) {
            		 Cell cell = row.createCell(columnCount++);
            		 if (field instanceof String) {
            			 cell.setCellValue((String) field);
            		 } else if (field instanceof Integer) {
            			 cell.setCellValue((Integer) field);
            		 }
            	 }
               
             }
              
         }
          
          
         try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
             workbook.write(outputStream);
             outputStream.close();
         }
 
    }
    
    
    public boolean check_test_case_existOrNot(Workbook workbook, Sheet sheet,String test_name) {
    	
    	int rows = sheet.getLastRowNum();
    	System.out.println(rows);
    	
    	for(int r=0;r<=rows;r++) {
    		Row row = sheet.getRow(r);
       		String existing_test_name = row.getCell(0).toString();
       		System.out.println(existing_test_name);
    		if(existing_test_name.equals(test_name)) {
    			System.out.println("true");
    			return true;
    		}
		}
    	System.out.println("false");
		return false;
    	
    }
    
    public int randomNumber() {
    	Random rnd = new Random();
    	int number = 1000 + rnd.nextInt(900000);
    	return number;
    }
    
    public String getRandomName() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rand = new Random();
        while (salt.length() < 7) {
            int index = (int) (rand.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    
    public JSONObject read_json_file(String file) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(new FileReader(file));
            return data;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
		return null;
    }
    
    public boolean checkIntersent() {
    	try {
    		final URL url = new URL("http://www.google.com");
    		final URLConnection conn = url.openConnection();
    		conn.connect();
    		conn.getInputStream().close();
    		return true;
		 } catch (MalformedURLException e) {
			 throw new RuntimeException(e);
		 } catch (IOException e) {
			 return false;
		 }
    }
    
    @SuppressWarnings("unchecked")
	public JSONObject data_types_on_success() {
		JSONObject DataTypesOnSuccess = new JSONObject();
		DataTypesOnSuccess.put("name", "String");
		DataTypesOnSuccess.put("salary", "String");
		DataTypesOnSuccess.put("age", "String");
		DataTypesOnSuccess.put("id", "String");
		return DataTypesOnSuccess;
    }
    
    @SuppressWarnings("unchecked")
	public JSONObject data_types_on_success_for_get_employee() {
    	JSONObject DataTypesOnSuccess = new JSONObject();
		DataTypesOnSuccess.put("employee_name", "String");
		DataTypesOnSuccess.put("employee_salary", "String");
		DataTypesOnSuccess.put("employee_age", "String");
		DataTypesOnSuccess.put("id", "String");
		DataTypesOnSuccess.put("profile_image", "String");
		return DataTypesOnSuccess;	
		
    }
    
    
}
