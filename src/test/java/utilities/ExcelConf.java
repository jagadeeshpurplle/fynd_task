package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelConf {

	XSSFWorkbook wb;
	
	XSSFSheet sheet;
	
	public ExcelConf(String excelPath) {

		try {
			File src=new File(excelPath);
			
			FileInputStream fis=new FileInputStream(src);
			
			wb=new XSSFWorkbook(fis);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
	}
	
	
	public String getStringData(int sheetNumber, int row, int column) {
		
		sheet= wb.getSheetAt(sheetNumber);
		
		String data= sheet.getRow(row).getCell(column).getStringCellValue();
		
		return data;
		
	}
	
	
	public long getIntData(int sheetNumber, int row, int column) {
		
		sheet=wb.getSheetAt(sheetNumber);
		
		long data=(long) sheet.getRow(row).getCell(column).getNumericCellValue();
		
		return (long) data;
	}

	
	public int getRowCount(int sheetIndex) {
		
		int row=wb.getSheetAt(sheetIndex).getLastRowNum();
		
		row=row+1;
		
		return row;
	}
}
