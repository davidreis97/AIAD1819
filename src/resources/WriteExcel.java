package src.resources;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import src.graph.Point;
import src.main.JADELauncher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jade.core.AID;

public class WriteExcel {

	private static final String EXCEL_FILE_LOCATION = "teste.xls";

	private static WritableSheet excelSheet;
	private static WritableSheet excelSheet2;
	
	private static WritableWorkbook myFirstWbook = null;
	
	private static int currentLine=1;

	public static void test() {

		//Create an Excel file
		
		try {

			myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));

			// create an Excel sheet
			excelSheet = myFirstWbook.createSheet("Sheet 1", 0);
			excelSheet2 = myFirstWbook.createSheet("Sheet 2", 0);
			
			
			// -- sheet 1 
			Label label = new Label(0, 0, "TempoEntrada");
			excelSheet.addCell(label);

			label = new Label(1, 0, "TicksTotal");
			excelSheet.addCell(label);
			
			label = new Label(2, 0, "Entrada");
			excelSheet.addCell(label);
			
			label = new Label(3, 0, "Saida");
			excelSheet.addCell(label);
			
			label = new Label(4, 0, "Velocidade");
			excelSheet.addCell(label);
			
			// -- sheet 2
			label = new Label(0, 0, "CarID");
			excelSheet2.addCell(label);
			
			label = new Label(1, 0, "Distancia");
			excelSheet2.addCell(label);
			
			label = new Label(2, 0, "Tempo previsto");
			excelSheet2.addCell(label);
			
			label = new Label(3, 0, "Tempo espera");
			excelSheet2.addCell(label);
 			

		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} 

	}
	
 
	 //WriteExcel.addCar(car.initialTime, totalTicks, car.getPath(), car.carVelocity);
	public static void addCar(long ticksW, String string, long initialTime, long totalTicks, Path path, double carVelocity) {
		
		
		try {
			
			// -- sheet 1 
			Number number = new Number(0, currentLine, initialTime); 
			excelSheet.addCell(number);
			
			number = new Number(1, currentLine, totalTicks); 
			excelSheet.addCell(number);
				
			number = new Number(2, currentLine, Integer.parseInt(path.path.get(0)));
			excelSheet.addCell(number);
			
			number = new Number(3, currentLine, Integer.parseInt(path.path.get(path.path.size()-1)));
			excelSheet.addCell(number);
			
			number = new Number(4, currentLine, carVelocity);
			excelSheet.addCell(number);
			
			// -- sheet 2
			

			Label label = new Label(0, currentLine, string); 
			excelSheet2.addCell(label);
			
			
			double distance =  path.getTotalDistance();
 
			
			number = new Number(1, currentLine,distance); 
			excelSheet2.addCell(number);
 
			double expectedTime = ( distance / carVelocity ) * 10;
			
			number = new Number(2, currentLine, expectedTime ); 
			excelSheet2.addCell(number);
			
			number = new Number(3, currentLine, totalTicks-expectedTime); 
			excelSheet2.addCell(number);
 
			
		} catch (WriteException e) {
			e.printStackTrace();
		}
		
				
		currentLine++;
		
	}
	
	public static void close() {
		
		try {
			myFirstWbook.write();
		} catch (IOException e) {
		 
			e.printStackTrace();
		}
		
		if (myFirstWbook != null) {
			try {
				myFirstWbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

}
