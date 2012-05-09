/** ****************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� : csj007
 * ���� : ���漼 ���� �ý��� ����
 ************************** �����̷� *******************************************
 *    ����      ����   �ۼ���   �������
 *
 **************************************************************************** */
package com.bccard.golf.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.NumberFormats; 
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import com.bccard.golf.common.BcLog;
import com.bccard.waf.common.DateUtil;

/** ****************************************************************************
 * ���ϻ��� ó�� Ŭ����.
 * @version   1.0
 * @author    2003.09.25 worldhun
 **************************************************************************** */



public class MakeFile {

	private WritableWorkbook workbook;
	private WritableSheet wsheet;
	private WritableCellFormat format;
	private WritableCellFormat fmt;		// ������
	private WritableCellFormat fmt2;	// �Ҽ���
//	private int row ;
	private String m_file_name ;
	//private String m_file_name;
	private StringBuffer m_txt_data;
	private String m_file_clss;

	/** **********************************************
	 * default constructor
	 *********************************************** */


	public MakeFile(){
		m_txt_data = new StringBuffer();		
	}// default constructor



/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �迬��
* @param file_name String��ü.
* @return  
********************************************************************************** */ 
	public MakeFile(String file_name){
		m_file_name = file_name;	
		m_txt_data = new StringBuffer();		
	}// default constructor

	/** **********************************************
	 * constructor : Ư�� ���丮�� �������� ����
	 * @param directory		���丮��
	 * @param file_name		���ϸ�
	 * @param sheet_name	���� ��Ʈ��
	 *********************************************** */

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �迬��
* @param directory String��ü.
* @param file_name String��ü.
* @param sheet_name String��ü.
* @return  
********************************************************************************** */ 
	public MakeFile(String directory, String file_name, String sheet_name) throws IOException {
		m_txt_data = new StringBuffer();		
		//���� ���� ����
		this.m_file_name = directory + file_name;
		File down_file = new File(m_file_name);
		workbook=Workbook.createWorkbook(down_file);

		wsheet=workbook.createSheet(sheet_name, 0);

        this.m_file_clss = "excel";
		/*
		//Sheet ����
		for (int i=0;i<sheet_name.length ;i++ ){
			wsheet=workbook.createSheet(sheet_name[i], i);
		}
		*/
	}//end constructor

	/** **********************************************
	 * constructor : Ư�� ���丮�� ���� ����
	 * @param id			ȸ����ȣ
	 * @param directory		���丮��
	 *********************************************** */

	public MakeFile(String id, String directory) throws IOException {
		String curDate = DateUtil.currdate("yyyyMMddHHmmss");
		this.m_file_name =  directory + id + "_" + curDate + ".zip";
	}//end constructor


	/** **********************************************
	 * constructor : Ư�� ���丮�� ���� ����
	 * @param id			ȸ����ȣ
	 * @param data_clss		�������� ( "auth" : ���γ���, "sale" : �̿볻��, ... )
	 * @param file_clss		���ϱ��� ( "excel" : ����, "text" : �ؽ�Ʈ)
	 * @param sheet_name		���� ��Ʈ��
	 *********************************************** */

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �迬��
* @param id String��ü.
* @param data_clss String��ü.
* @param file_clss String��ü.
* @param sheet_name String��ü.
* @return  
********************************************************************************** */ 
	public MakeFile(String id, String data_clss, String file_clss, String sheet_name) throws IOException {
		m_txt_data = new StringBuffer();		
		String curDate = DateUtil.currdate("yyyyMMddHHmmss");
		String dir_name = "";
		dir_name = AppConfig.FILE_RECEIPT_DIR;

			if ("excel".equals(file_clss) ) {
				this.m_file_name = dir_name + file_clss + "/" + id + "_" + curDate + ".xls";
				this.m_file_clss = file_clss;
			} else if ("text".equals(file_clss) ) {
				this.m_file_name = dir_name + file_clss + "/" + id + "_" + curDate + ".txt";
				this.m_file_clss = file_clss;
			} else {
				this.m_file_name = dir_name + file_clss + "/" + id + "_" + curDate;
			}

		File down_file = new File(m_file_name);

		if ("excel".equals(file_clss) ) {
			workbook = Workbook.createWorkbook(down_file);
			wsheet = workbook.createSheet(sheet_name, 0);

		}

	}



/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �迬��
* @param req_date String��ü.
* @param buz_no String��ü.
* @param id String��ü.
* @param data_clss String��ü.
* @param file_clss String��ü.
* @param sheet_name String��ü.
* @param data_format String��ü.
* @return  
********************************************************************************** */ 
	public MakeFile(String req_date, String buz_no, String id, String data_clss, String file_clss, String sheet_name, String data_format) throws IOException {
		m_txt_data = new StringBuffer();		
		
		String dir_name = "";
		dir_name = AppConfig.FILE_RECEIPT_DIR;

		if ("excel".equals(file_clss) ) {
			this.m_file_name = dir_name + file_clss + "/" + req_date + data_format + "E_" + buz_no + ".xls";
			this.m_file_clss = file_clss;
		} else if ("text".equals(file_clss) ) {
			this.m_file_name = dir_name + file_clss + "/" + req_date + data_format + "T_" + buz_no + ".txt";
			this.m_file_clss = file_clss;
		}

		File down_file = new File(m_file_name);
		if ("excel".equals(file_clss) ) {
			workbook = Workbook.createWorkbook(down_file);
			wsheet = workbook.createSheet(sheet_name, 0);
		}
	}


	/** **********************************************
	 * getFileName : ���ϸ�(��ü���) ������
	 *********************************************** */


	public String getFileName() {
		return this.m_file_name;
	}

	/** **********************************************
	 * setCellWidth : ������ Cell Width ����
	 * @param col			Cell column ��ġ
	 * @param width			Cell width��
	 *********************************************** */


	public void setCellWidth(int col, int width) {
		wsheet.setColumnView(col, width);
	}

	/** **********************************************
	 * setCellWidth : ������ Row Height ����
	 * @param col			Cell row ��ġ
	 * @param width			Cell height
	 *********************************************** */


	public void setCellHeight(int row, int height) throws WriteException {
		wsheet.setRowView(row, height);
	}

	/** **********************************************
	 * setDefaultFontFormat : ��Ʈ �⺻�� ���� ����
	 *********************************************** */


	public void setDefaultFontFormat() throws WriteException {

		jxl.write.WritableFont font = new jxl.write.WritableFont(WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false) ;	
		font.setColour(jxl.format.Colour.BLACK) ;

		format = new WritableCellFormat (font) ;
		format.setBackground(jxl.format.Colour.DEFAULT_BACKGROUND) ;
		format.setAlignment(jxl.format.Alignment.GENERAL) ;
		format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE) ;
		format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN) ;
	}


	/*
	*	��Ʈ �̸�, ��Ʈ ũ��, ��Ʈ��, ����, Bold, Italic ��
	*	��Ʈ ������ �Ӽ��� �����Ѵ�.
	*	�Է��� ��Ʈ ������ ARIAL, TIMES
	*	��Ʈ �� ���� ������ ���� Į��� BLACK, BLUE, BROWN, 
	*	GOLD, GRAY_25, GREEN, LIME, ORANGE, PINK, RED, 
	*	ROSE, SKY_BLUE, VIOLET, YELLOW, WHITE ���̴�.
	*	
	*/
	/** ***************************************
	* renewal 
	* @version 2005 12 12 
	* @author �迬��
	********************************************** */
	public void setFontFormat(String font_name, int font_size, jxl.format.Colour font_color, 
		jxl.format.Colour background_color, boolean bold, boolean italic) throws WriteException {

		//font_name = "WritableFont."+font_name ;
		//font_color = "jxl.format.Colour."+font_color ;
		//background_color = "jxl.format.Colour."+background_color ;
		jxl.write.WritableFont font = null ;

		if (font_name.equals("ARIAL")){
			if (bold){
				font = new jxl.write.WritableFont(WritableFont.ARIAL, font_size, WritableFont.BOLD, italic) ;
			}else {
				font = new jxl.write.WritableFont(WritableFont.ARIAL, font_size, WritableFont.NO_BOLD, italic) ;
			}		
		}else {
			if (bold){
				font = new jxl.write.WritableFont(WritableFont.TIMES, font_size, WritableFont.BOLD, italic) ;
			}else {
				font = new jxl.write.WritableFont(WritableFont.TIMES, font_size, WritableFont.NO_BOLD, italic) ;
			}		
		}
		
		font.setColour(font_color) ;

		format = new WritableCellFormat (font) ;
		format.setBackground(background_color) ;
		format.setAlignment(jxl.format.Alignment.GENERAL) ;
		format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE) ;
		format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN) ;
		format.setWrap(true);
	}
	
	/** ***************************************
	* renewal 
	* @version 2005 12 12 
	* @author �迬��
	********************************************** */
	public void setFontFormat(String font_name, int font_size, jxl.format.Colour font_color, 
		jxl.format.Colour background_color, jxl.format.Alignment alignment, 
		jxl.format.Border border, jxl.format.BorderLineStyle borderline, 
		boolean bold, boolean italic, boolean wrap) throws WriteException {

		//font_name = "WritableFont."+font_name ;
		//font_color = "jxl.format.Colour."+font_color ;
		//background_color = "jxl.format.Colour."+background_color ;
		jxl.write.WritableFont font = null ;

		if (font_name.equals("ARIAL")){
			if (bold){
				font = new jxl.write.WritableFont(WritableFont.ARIAL, font_size, WritableFont.BOLD, italic) ;
			}else {
				font = new jxl.write.WritableFont(WritableFont.ARIAL, font_size, WritableFont.NO_BOLD, italic) ;
			}		
		}else {
			if (bold){
				font = new jxl.write.WritableFont(WritableFont.TIMES, font_size, WritableFont.BOLD, italic) ;
			}else {
				font = new jxl.write.WritableFont(WritableFont.TIMES, font_size, WritableFont.NO_BOLD, italic) ;
			}		
		}
		
		font.setColour(font_color) ;

		format = new WritableCellFormat (font) ;
		format.setBackground(background_color) ;
		format.setAlignment(alignment) ;
		format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE) ;
		format.setBorder(border, borderline) ;
		if (wrap) {
			format.setWrap(true);
		}

		WritableFont font1 = new WritableFont(WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);

		fmt = new WritableCellFormat(font1, NumberFormats.THOUSANDS_INTEGER);
		fmt.setAlignment(jxl.format.Alignment.RIGHT);
		fmt.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE) ;
		fmt.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN) ;

		fmt2 = new WritableCellFormat(font1, NumberFormats.FLOAT);
		fmt2.setAlignment(jxl.format.Alignment.RIGHT);
		fmt2.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE) ;
		fmt2.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN) ;
    
    }


	/** **********************************************
	 * setDataNumber : ������ Cell ���� ����
	 * @param obj		Cell ���� ��ü
	 * @param col		Cell column ��ġ
	 * @param row		Cell row ��ġ
	 *********************************************** */


	public void setDataNumber(Object obj, int col, int row) throws WriteException {

		if ( obj == null || "".equals((String)obj) ) {
			jxl.write.Label label_data = new jxl.write.Label(col, row, "", this.format) ;
			wsheet.addCell(label_data);
			return ;
		}

		if ( ((String)obj).indexOf("-") > 0 ) {
			obj = ((String)obj).substring(((String)obj).indexOf("-"));
		}
		jxl.write.Number data = new jxl.write.Number(col, row, Double.parseDouble((String)obj), this.fmt);
		wsheet.addCell(data);
	}

	/** **********************************************
	 * setDataNumber : ������ Cell ���� ����
	 * @param obj		Cell ���� ��ü
	 * @param col		Cell column ��ġ
	 * @param row		Cell row ��ġ
	 *********************************************** */


	public void setDataFloat(Object obj, int col, int row) throws WriteException {

		if ( obj == null || "".equals((String)obj) ) {
			jxl.write.Label label_data = new jxl.write.Label(col, row, "", this.format) ;
			wsheet.addCell(label_data);
			return ;
		}

		if ( ((String)obj).indexOf("-") > 0 ) {
			obj = ((String)obj).substring(((String)obj).indexOf("-"));
		}
		jxl.write.Number data = new jxl.write.Number(col, row, Double.parseDouble((String)obj), this.fmt2);
		wsheet.addCell(data);
	}

	/** **********************************************
	 * setData : ������ Cell ���� ����
	 * @param obj		Cell ���� ��ü
	 * @param col		Cell column ��ġ
	 * @param row		Cell row ��ġ
	 *********************************************** */

	public void setData(Object obj, int col, int row) throws WriteException {

		if ( obj == null ) {
			jxl.write.Label label_data = new jxl.write.Label(col, row, "", this.format) ;
			wsheet.addCell(label_data);
			return ;
		}
		if (obj instanceof String){
//			this.format.setAlignment(jxl.format.Alignment.LEFT) ;
			//String s_obj = (String)obj ;
			jxl.write.Label label_data = new jxl.write.Label(col, row, (String)obj, this.format) ;
			wsheet.addCell(label_data);
		}else if (obj instanceof Integer){
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			int i_obj = ( (Integer)obj ).intValue() ;
			jxl.write.Number int_data=new jxl.write.Number(col, row, i_obj, this.format);
			wsheet.addCell(int_data);
		}else if (obj instanceof Long){
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			long l_obj = ( (Long)obj ).longValue() ;
			jxl.write.Number long_data=new jxl.write.Number(col, row, l_obj, this.format);
			wsheet.addCell(long_data);
		}else if (obj instanceof Float){
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			float f_obj = ( (Float)obj ).floatValue() ;
			jxl.write.Number float_data=new jxl.write.Number(col, row, f_obj, this.format);
			wsheet.addCell(float_data);
		}else {
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			double d_obj = ( (Double)obj ).doubleValue() ;
			jxl.write.Number double_data=new jxl.write.Number(col, row, d_obj, this.format);
			wsheet.addCell(double_data);
		}
	}//end setData()

	/** **********************************************
	 * setData : ������ Cell ���� ����
	 * @param obj			Cell ���� ��ü
	 * @param col			Cell column ��ġ
	 * @param row			Cell row ��ġ
	 * @param cellformat	Cell Format
	 *********************************************** */

	public void setData(Object obj, int col, int row, WritableCellFormat cellformat) throws WriteException {

		if ( obj == null ) {
			jxl.write.Label label_data = new jxl.write.Label(col, row, "", cellformat) ;
			wsheet.addCell(label_data);
			return ;
		}
		if (obj instanceof String){
//			this.format.setAlignment(jxl.format.Alignment.LEFT) ;
			//String s_obj = (String)obj ;
			jxl.write.Label label_data = new jxl.write.Label(col, row, (String)obj, cellformat) ;
			wsheet.addCell(label_data);
		}else if (obj instanceof Integer){
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			int i_obj = ( (Integer)obj ).intValue() ;
			jxl.write.Number int_data=new jxl.write.Number(col, row, i_obj, cellformat);
			wsheet.addCell(int_data);
		}else if (obj instanceof Long){
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			long l_obj = ( (Long)obj ).longValue() ;
			jxl.write.Number long_data=new jxl.write.Number(col, row, l_obj, cellformat);
			wsheet.addCell(long_data);
		}else if (obj instanceof Float){
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			float f_obj = ( (Float)obj ).floatValue() ;
			jxl.write.Number float_data=new jxl.write.Number(col, row, f_obj, cellformat);
			wsheet.addCell(float_data);
		}else {
//			this.format.setAlignment(jxl.format.Alignment.RIGHT) ;
			double d_obj = ( (Double)obj ).doubleValue() ;
			jxl.write.Number double_data=new jxl.write.Number(col, row, d_obj, cellformat);
			wsheet.addCell(double_data);
		}
	}//end setData()

	/** **********************************************
	 * mergeCell : ������ Cell ����
	 * @param col		Cell column ������ġ
	 * @param row		Cell row ������ġ
	 * @param col		Cell column ������ġ
	 * @param row		Cell row ������ġ
	 *********************************************** */


	public void mergeCell(int col1, int row1, int col2, int row2) {
		try {
			wsheet.mergeCells(col1, row1, col2, row2);
		} catch (WriteException e){
			e.printStackTrace();
		}
	}

	/** **********************************************
	 * write : �������� ����
	 *********************************************** */


	public boolean write() throws Exception {
		if ("excel".equals(this.m_file_clss) ) {
			if (this.workbook != null) {
				this.workbook.write() ;
				this.workbook.close() ;
				return true;
			} else {
				return false;
			}
		} else {
			try {
				PrintWriter pw = new PrintWriter (new BufferedWriter(new FileWriter(this.m_file_name)));
				pw.write(this.m_txt_data.toString());
				pw.flush();
				pw.close();
			} catch (IOException e){
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}// end write()


	/** **********************************************
	 * addData : �ؽ�Ʈ���� ���� �߰�
	 * @param data		
	 *********************************************** */


	public void addData(String data) throws IOException {
		this.m_txt_data.append(data);
	}

	/** **********************************************
	*	makeZip	:	���� ����
	 *********************************************** */
	public boolean makeZip(){
		try{

			String o_File = this.m_file_name.substring(0, this.m_file_name.lastIndexOf(".")) + ".zip";
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(o_File));
			FileInputStream fis = new FileInputStream(this.m_file_name);

			// ���ϸ��� ������ -> ����η� �����Ͽ� ����
			String name = this.m_file_name.substring(this.m_file_name.lastIndexOf("/")+1, this.m_file_name.length());

			ZipEntry e1 = new ZipEntry(name);
			e1.setMethod(ZipEntry.DEFLATED);
			outStream.putNextEntry(e1);

			int ch;
			while((ch = fis.read()) != -1) {
				outStream.write(ch);
			}
			outStream.closeEntry();
			outStream.close();
			this.m_file_name = o_File;
		} catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** **********************************************
	*	makeZip	:	���� ����
	* @param i_FileList		������ ���� ���
	 *********************************************** */

	public boolean makeZip(Vector i_FileList){
		try{
			String o_File = this.m_file_name.substring(0, this.m_file_name.lastIndexOf(".")) + ".zip";
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(o_File));
			for ( int i = 0 ; i < i_FileList.size() ; i++ )
			{
				FileInputStream fis = new FileInputStream((String)i_FileList.get(i));

				// ���ϸ��� ������ -> ����η� �����Ͽ� ����
				String name = ((String)i_FileList.get(i)).substring(((String)i_FileList.get(i)).lastIndexOf("/")+1, ((String)i_FileList.get(i)).length());

				ZipEntry e1 = new ZipEntry(name);
				e1.setMethod(ZipEntry.DEFLATED);
				outStream.putNextEntry(e1);
				int ch;
				while((ch = fis.read()) != -1) {
					outStream.write(ch);
				}
				outStream.closeEntry();
			}
			outStream.close();
			this.m_file_name = o_File;
		} catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** **********************************************
	*	makeZip	:	���� ����
	* @param i_File			������ ����
	 *********************************************** */


	public boolean makeZip(String i_File){
		try{
			String o_File = this.m_file_name.substring(0, this.m_file_name.lastIndexOf(".")) + ".zip";
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(o_File));
			FileInputStream fis = new FileInputStream(i_File);

			// ���ϸ��� ������ -> ����η� �����Ͽ� ����
			String name = i_File.substring(i_File.lastIndexOf("/")+1, i_File.length());

			ZipEntry e1 = new ZipEntry(name);
			e1.setMethod(ZipEntry.DEFLATED);
			outStream.putNextEntry(e1);

			int ch;
			while((ch = fis.read()) != -1) {
				outStream.write(ch);
			}
			outStream.closeEntry();
			outStream.close();
			this.m_file_name = o_File;
		} catch(IOException e){ 
			e.printStackTrace();
			return false;
		}
		return true;
	}








	/*
	*	Į�� ����� Į���� ũ�⸦ �Է��Ѵ�. 
	*	
	*/


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �迬��
* @param col_name String[]��ü.
* @param size int[]��ü.
* @return  void 
********************************************************************************** */
	/*
	public void setColumnName(String[] col_name, int[] size) throws WriteException {

		for (int i=0 ; i<col_name.length; i++ ) {			
			wsheet.setColumnView(i, size[i]) ;
			jxl.write.Label l = new jxl.write.Label(i, row, col_name[i], this.format);			
			wsheet.addCell(l);			
		}
		row++ ;
	}
*/


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �迬��
* @param hash Hashtable��ü.
* @return  void 
********************************************************************************** */
	/*
	public void setData(Hashtable hash) throws WriteException {

		String key_name = null ;
		Vector v = new Vector() ;
		Enumeration e = hash.keys() ;
		for ( int col_position=0; e.hasMoreElements(); col_position++) {
			key_name = (String)e.nextElement() ;
			v = (Vector)hash.get(key_name) ;
			Object obj = null ;
			int cur_row = row ;
			for (int i = 0;i<v.size() ;i++ ){
				obj = v.elementAt(i) ;
				if (obj instanceof String){
					//String s_obj = (String)obj ;
					jxl.write.Label label_data = new jxl.write.Label(col_position, row, (String)obj, this.format) ;
					wsheet.addCell(label_data);
				}else if (obj instanceof Integer){
					int i_obj = ( (Integer)obj ).intValue() ;
					jxl.write.Number int_data=new jxl.write.Number(col_position, row, i_obj, this.format);
					wsheet.addCell(int_data);
				}else if (obj instanceof Long){
					long l_obj = ( (Long)obj ).longValue() ;
					jxl.write.Number long_data=new jxl.write.Number(col_position, row, l_obj, this.format);
					wsheet.addCell(long_data);
				}else if (obj instanceof Float){
					float f_obj = ( (Float)obj ).floatValue() ;
					jxl.write.Number float_data=new jxl.write.Number(col_position, row, f_obj, this.format);
					wsheet.addCell(float_data);
				}else {
					double d_obj = ( (Double)obj ).doubleValue() ;
					jxl.write.Number double_data=new jxl.write.Number(col_position, row, d_obj, this.format);
					wsheet.addCell(double_data);
				}
				row++ ;
			}//end for()
			row = cur_row ;
		}//end for()
	}//end setData()
*/
}//end class 
