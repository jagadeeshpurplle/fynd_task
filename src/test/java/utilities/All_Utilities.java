package utilities;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.Serializable;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
        
       
		while (rowIterator.hasNext()) {
        	j++;
//        	System.out.println("row: "+j);
        	Row row = rowIterator.next();
            // Now let's iterate over the columns of the current row
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
    
    
    
}
