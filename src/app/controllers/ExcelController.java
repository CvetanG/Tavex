package app.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import app.entities.ExampleDate;
import app.entities.IndexesEnum;
import app.entities.MyCellStyles;
import app.entities.MyColumn;
import app.entities.RowEntry;
import app.entities.Utils;

public class ExcelController {
	
	private static ExcelController instance;
    
	public ExcelController(){}
    
    public static ExcelController getInstance(){
        if(instance == null){
            instance = new ExcelController();
        }
        return instance;
    }
	
	int correction = 1;
	
	public void writeInExcel(Workbook wb, List<RowEntry> myEntries) throws IOException {
		
		MyCellStyles myCellStylesRegular = new MyCellStyles(wb, false);
		
		MyCellStyles myCellStylesUnderlined = new MyCellStyles(wb, true);
		
		Map<String, CellStyle> myCellStyles = new HashMap<String, CellStyle>();
		
		for (RowEntry rowEntry : myEntries) {
			
			Sheet worksheet = wb.getSheet("Daily");
			
			//Read the spreadsheet that needs to be updated
			
			//Access the worksheet, so that we can update / modify it. 
			
			Boolean underline = rowEntry.getUnderline();
//			Map<String, CellStyle> myCellStyles = createCellStyles(wb, underline);
			if (underline) {
				myCellStyles = myCellStylesUnderlined.getMyCellStyles();
			} else {
				myCellStyles = myCellStylesRegular.getMyCellStyles();
			}
			
			// Access the second cell in second row to update the value
	//		int lastRow = worksheet.getLastRowNum();
	//		int lastRow = worksheet.getPhysicalNumberOfRows();
			
	//		int newRow = lastRow + 1;
			
			// get the last cell not null
			
			int entrySize = myEntries.size();
			
			int newRow = 0;
			for (Row row : worksheet) {
			    for (Cell cell : row) {
			        if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			            if (cell.getCellType() != Cell.CELL_TYPE_STRING ||
			                cell.getStringCellValue().length() > 0) {
			            	newRow++;
			                break;
			            }
			        }
			    }
			}
			
			// Create classes for columns
						MyColumn dateCol 		= new MyColumn("A", 0);
						MyColumn timeCol 		= new MyColumn("B", 1);
						MyColumn dowCol 		= new MyColumn("C", 2);
						MyColumn indexCol 		= new MyColumn("D", 3);
						MyColumn currCol 		= new MyColumn("E", 4);
						MyColumn buyCol 		= new MyColumn("F", 5);
						MyColumn bGoldCol 		= new MyColumn("G", 6);
						MyColumn sellCol 		= new MyColumn("H", 7);
						MyColumn sGoldCol 		= new MyColumn("I", 8);
						MyColumn diffPercCol 	= new MyColumn("J", 9);
						MyColumn diffCol 		= new MyColumn("K", 10);
						MyColumn diffPrivDCol 	= new MyColumn("L", 11);
			    
			// declare a Cell object
			String[] colNames = {
					"dateCol", "timeCol", "dowCol", "indexCol",
					"currCol","buyCol","prDCol","sellCol",
					"prOCol", "diffPercCol", "diffCol", "diffPrivDCol"
			};
			List<Cell> cellList = new ArrayList<Cell>(colNames.length);
			Row lRow = worksheet.createRow(newRow);
			for (int i = 0; i < colNames.length; i++) {
				cellList.add(lRow.createCell(i));
			}
			
	//		Cell cell_01 = null; 
	//		
	//		cell_01 = worksheet.getRow(newRow).getCell(0);   
	//		cell_01.setCellValue(myDate.format(calendar.getTime()));
			// Get current cell value value and overwrite the value
			
			// Column A Date(0)
			Calendar calendar = Calendar.getInstance();
			Date curDate = new Date();
//			SimpleDateFormat myDateFormat = new SimpleDateFormat("d.M.yyyy");
	//		System.out.println(myDate.format(calendar.getTime()));
			cellList.get(dateCol.getColNum()).setCellStyle(myCellStyles.get("csDateRight"));
//			cellList.get(dateCol.getColNum()).setCellValue(myDateFormat.format(calendar.getTime()));
			cellList.get(dateCol.getColNum()).setCellValue(curDate);
			
			// Column B Time(1)
			SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm");
	//		System.out.println(myTime.format(calendar.getTime()));
			cellList.get(timeCol.getColNum()).setCellStyle(myCellStyles.get("csHour"));
			cellList.get(timeCol.getColNum()).setCellValue(myTimeFormat.format(calendar.getTime()));
			
			// Column C DoW(2)
			int dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK);
	//		System.out.println(ExampleDate.myDayOfWeek(dayOfWeek));
			cellList.get(dowCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			cellList.get(dowCol.getColNum()).setCellValue(ExampleDate.myDayOfWeek(dayOfWeek));
			
			// Column D Index(3)
			cellList.get(indexCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			cellList.get(indexCol.getColNum()).setCellValue(rowEntry.getIndex());
			
			// Column E Curuncy(4)
			cellList.get(currCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			cellList.get(currCol.getColNum()).setCellValue(rowEntry.getCurruncy().toString());
			
			// Column F Buy(5)
	//		cellList.get(buyColInt).setCellStyle(csAcc);
	//		cellList.get(buyColInt).setCellType(Cell.CELL_TYPE_FORMULA);
	//		cellList.get(buyColInt).setCellValue(rowEntry.getBuy());
			if (IndexesEnum.GOLD == rowEntry.getIndexType()) {
				cellList.get(buyCol.getColNum()).setCellValue(rowEntry.getBuy());
			} else if (rowEntry.getBuy() != null && IndexesEnum.CRYPTO == rowEntry.getIndexType()) {
				cellList.get(buyCol.getColNum()).setCellValue(Double.parseDouble(rowEntry.getBuy()));
				cellList.get(buyCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			} else if (rowEntry.getBuy() != null) {
				if (IndexesEnum.USD != rowEntry.getIndexType()) {
					cellList.get(buyCol.getColNum()).setCellStyle(myCellStyles.get("csAcc"));
				} else {
					cellList.get(buyCol.getColNum()).setCellStyle(myCellStyles.get("csUSD"));
				}
				cellList.get(buyCol.getColNum()).setCellValue(Double.parseDouble(rowEntry.getBuy()));
			} else {
				cellList.get(buyCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			}
			
			// Column G PriceDown(6)
			if (IndexesEnum.GOLD_COIN == rowEntry.getIndexType()) {
				
				cellList.get(bGoldCol.getColNum()).setCellStyle(myCellStyles.get("csPerc"));
				cellList.get(bGoldCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
				// 1940 + ((11 - 4) + 1) - i
//				(newRow + 1) current row
				// Злато (в трой унции) e 8-я индекс
//				"-1" да го извадим от Първия
				int j = (newRow + 1) + 8 - correction;
				cellList.get(bGoldCol.getColNum()).setCellFormula(buyCol.getColChar() + (newRow + 1) + "/$" + diffCol.getColChar() + "$" + j + "-1");
			} else if (IndexesEnum.CRYPTO == rowEntry.getIndexType()) {
				cellList.get(bGoldCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
				cellList.get(bGoldCol.getColNum()).setCellValue(rowEntry.getPriceDown());
			} else {
				cellList.get(bGoldCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			}
			
			// Column H Sell(7)
	//		cellList.get(h).setCellType(Cell.CELL_TYPE_FORMULA);
			if (IndexesEnum.CRYPTO == rowEntry.getIndexType()) {
				cellList.get(sellCol.getColNum()).setCellStyle(myCellStyles.get("csPerc"));
				cellList.get(sellCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
				cellList.get(sellCol.getColNum()).setCellFormula(buyCol.getColChar() + (newRow + 1) + "/" + buyCol.getColChar() + ((newRow + 1) - entrySize) + "-1");
			} else if (rowEntry.getSell() != null) {
				if (IndexesEnum.USD == rowEntry.getIndexType()) {
					cellList.get(sellCol.getColNum()).setCellStyle(myCellStyles.get("csAcc"));
				} else {
					cellList.get(sellCol.getColNum()).setCellStyle(myCellStyles.get("csUSD"));
				}
				cellList.get(sellCol.getColNum()).setCellValue(Double.parseDouble(rowEntry.getSell()));
			} else {
				cellList.get(sellCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			}
			
			// Column I PriceOver(8)
			if (IndexesEnum.GOLD_COIN == rowEntry.getIndexType()) {
				cellList.get(sGoldCol.getColNum()).setCellStyle(myCellStyles.get("csPerc"));
				cellList.get(sGoldCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
				int j = (newRow + 1) + 8 - correction;
				cellList.get(sGoldCol.getColNum()).setCellFormula(sellCol.getColChar() + (newRow + 1) + "/$" + diffCol.getColChar() + "$" + j + "-1");
				correction++;
			} else {
				cellList.get(sGoldCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			}
			
			// Column J DiffPerc(9)
			cellList.get(diffPercCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
			if (IndexesEnum.GOLD_COIN == rowEntry.getIndexType()) {
				cellList.get(diffPercCol.getColNum()).setCellStyle(myCellStyles.get("csPerc"));
				cellList.get(diffPercCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
				cellList.get(diffPercCol.getColNum()).setCellFormula(sGoldCol.getColChar() + (newRow + 1) + "-" + bGoldCol.getColChar() + (newRow + 1));
			} else {
				cellList.get(diffPercCol.getColNum()).setCellValue(rowEntry.getDiffPerc());
			}
			
			// Column K Diff(10)
			if (rowEntry.getDiff() == null) {
				cellList.get(diffCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
				cellList.get(diffCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
				cellList.get(diffCol.getColNum()).setCellFormula(sellCol.getColChar() + (newRow + 1) + "-"  + buyCol.getColChar() + (newRow + 1));
			} else if (IndexesEnum.USD != rowEntry.getIndexType()) {
				cellList.get(diffCol.getColNum()).setCellStyle(myCellStyles.get("csAcc"));
				cellList.get(diffCol.getColNum()).setCellValue(Double.parseDouble(rowEntry.getDiff()));
			} else {
				cellList.get(diffCol.getColNum()).setCellStyle(myCellStyles.get("csUSD"));
				cellList.get(diffCol.getColNum()).setCellValue(Double.parseDouble(rowEntry.getDiff()));
			}
			
			// Column L DiffPrivDay(11)
//			cellList.get(diffPrivDCol.getColNum()).setCellStyle(myCellStyles.get("csDef"));
//			cellList.get(diffPrivDCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
			
			cellList.get(diffPrivDCol.getColNum()).setCellStyle(myCellStyles.get("csPerc"));
			cellList.get(diffPrivDCol.getColNum()).setCellType(Cell.CELL_TYPE_FORMULA);
			
			if (IndexesEnum.GOLD_COIN == rowEntry.getIndexType()) {
//				cellList.get(diffPrivDCol.getColNum()).setCellFormula("IF(" + sellCol.getColChar() + (newRow + 1) + "="
//										+ sellCol.getColChar() + ((newRow + 1) - entrySize) + ",\"Even\",IF("
//										+ sellCol.getColChar() + (newRow + 1) + ">" + sellCol.getColChar()
//										+ ((newRow + 1) - entrySize) + ",\"Up\",\"Down\"))");
				cellList.get(diffPrivDCol.getColNum()).setCellFormula(sellCol.getColChar() + (newRow + 1) + "/" + sellCol.getColChar() + ((newRow + 1) - entrySize) + "-1");
			} else {
//				cellList.get(diffPrivDCol.getColNum()).setCellFormula("IF(" + diffCol.getColChar() + (newRow + 1) + "="
//										+ diffCol.getColChar() + ((newRow + 1) - entrySize) + ",\"Even\",IF("
//										+ diffCol.getColChar() + (newRow + 1) + ">" + diffCol.getColChar() 
//										+ ((newRow + 1) - entrySize) + ",\"Up\",\"Down\"))");
				cellList.get(diffPrivDCol.getColNum()).setCellFormula(diffCol.getColChar() + (newRow + 1) + "/" + diffCol.getColChar() + ((newRow + 1) - entrySize) + "-1");
			}
			
			
			/*
			// create 2 fonts objects
			Font f = wb.createFont();
			
			// Set font 1 to 12 point type, blue and bold
			f.setFontHeightInPoints((short) 12);
			f.setColor( HSSFColor.RED.index );
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			
			DataFormat df = wb.createDataFormat();
			CellStyle cs = wb.createCellStyle();
			// Set cell style and formatting
			cs.setFont(f);
			cs.setDataFormat(df.getFormat("#,##0.0"));
			
			*/
		
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Start Program");
		long startTime = System.currentTimeMillis();
		
		String path = "D:\\Tavex_01.xlsx";
//		String path = "/home/cvetan/Downloads/Tavex.xlsx";
		
		List<String> myCoinsStrings = new ArrayList<>();
		myCoinsStrings.add("1 унция златен американски бизон");
		myCoinsStrings.add("1 унция американски орел");
		myCoinsStrings.add("30 грама златна китайска панда от 2017");
		myCoinsStrings.add("1 унция златна австрийска филхармония");
		myCoinsStrings.add("1 унция златнo Австралийско Кенгуру");
		myCoinsStrings.add("1 унция златен канадски кленов лист");
		/*
		int zeroRow;
		
		if (path.startsWith("/home/")) {
			zeroRow	 = 0;
		} else {
			zeroRow	 = 1;
		}
			
		File myFile = new File(path);
		FileInputStream fsIP = new FileInputStream(myFile);
				
		//Access the workbook                  
		Workbook wb = new XSSFWorkbook(fsIP);
		*/
		List<RowEntry> myEntries = new ArrayList<RowEntry>();
		WebSitesParser myParser = new WebSitesParser();
		
		myEntries = myParser.getCoinsFromTavex(myCoinsStrings);
		/*RowEntry rowEtry_01 = InvestmentParser.getBGNUSD();
		RowEntry rowEtry_02 = InvestmentParser.getXAUBGN();
		RowEntry rowEtry_03 = InvestmentParser.getXAUUSD();
		RowEntry rowEtry_04 = InvestmentParser.getEthereumPrice();
		
		myEntries.add(rowEtry_01);
		myEntries.add(rowEtry_02);
		myEntries.add(rowEtry_03);
		myEntries.add(rowEtry_04);
		for (RowEntry rowEntry : myEntries) {
			writeInExcel(wb, rowEntry, zeroRow);
		}
		 */
		
		/*
		RowEntry rowEntry_01 = new RowEntry(
				"Канадски кленов лист 1 унция",
				"2,120.00",
				"2,279.00",
				null,
				null,
				true);
		
		RowEntry rowEntry_02 = new RowEntry(
				"Канадски кленов лист 1 унция",
				"2,120.00",
				"2,279.00",
				null,
				null,
				false);
		
		writeInExcel(wb, rowEntry_01, zeroRow);
		writeInExcel(wb, rowEntry_02, zeroRow);
		
		writeInExcel(wb, index, buy, sell, null, diff);
		writeInExcel(wb, index, "XAU","1.00", null, sell);
		writeInExcel(wb, index, null, null, "open", sell);
		writeInExcel(wb, index, null, null, null, sell);
		*/
		/*
		//Close the InputStream  
		fsIP.close();
		
		//Open FileOutputStream to write updates
		FileOutputStream output_file =new FileOutputStream(myFile);  
		
		//write changes
		wb.write(output_file);
		
		//close the stream
		output_file.close();
		*/
//		System.out.println();
		long endTime   = System.currentTimeMillis();
		System.err.println(Utils.duration(startTime, endTime));
		System.out.println("Done!!!");
	}

}
