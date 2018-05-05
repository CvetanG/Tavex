package app.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;

public class MyCellStyles {
	
	Map<String, CellStyle> myCellStyles;
	
	Workbook wb;
	
	Boolean underline;
	
	public MyCellStyles(Workbook wb, Boolean underline) {
		myCellStyles = new HashMap<String, CellStyle>();
		
		CellStyle csDateRight = wb.createCellStyle();
		CellStyle csHour = wb.createCellStyle();
		CellStyle csAcc = wb.createCellStyle();
		CellStyle csPerc = wb.createCellStyle();
//		CellStyle csBottBord = wb.createCellStyle();
		CellStyle csUSD = wb.createCellStyle();
		CellStyle csDef = wb.createCellStyle();
//		DataFormat df = wb.createDataFormat();
		CreationHelper createHelper = wb.getCreationHelper();
		
//		csDateRight.setAlignment(CellStyle.ALIGN_RIGHT);
//		csDateRight.setDataFormat((short)14);
		csDateRight.setDataFormat(
			    createHelper.createDataFormat().getFormat("d.m.yyyy"));
		
		csHour.setDataFormat((short)14);
		csHour.setAlignment(CellStyle.ALIGN_RIGHT);
		csHour.setDataFormat(
			    createHelper.createDataFormat().getFormat("HH:MM"));
		
		csAcc.setDataFormat((short)4);
		
		csPerc.setDataFormat((short)10);
		
//		csBottBord.setBorderBottom(CellStyle.BORDER_THIN);
		
		csUSD.setDataFormat(createHelper.createDataFormat().getFormat("#,#####0.00000"));
		
		myCellStyles.put("csDateRight", csDateRight);
		myCellStyles.put("csHour", csHour);
		myCellStyles.put("csAcc", csAcc);
		myCellStyles.put("csPerc", csPerc);
		myCellStyles.put("csUSD", csUSD);
		myCellStyles.put("csDef", csDef);
		
		if (underline) {
			for (Entry<String, CellStyle> cs : myCellStyles.entrySet()) {
				cs.getValue().setBorderBottom(CellStyle.BORDER_THIN);
			}
		}
		
	}

	public Map<String, CellStyle> getMyCellStyles() {
		return myCellStyles;
	}

	public void setMyCellStyles(Map<String, CellStyle> myCellStyles) {
		this.myCellStyles = myCellStyles;
	}

	public Workbook getWb() {
		return wb;
	}

	public void setWb(Workbook wb) {
		this.wb = wb;
	}

	public Boolean getUnderline() {
		return underline;
	}

	public void setUnderline(Boolean underline) {
		this.underline = underline;
	}
	
}
