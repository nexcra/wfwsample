/** ****************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 : csj007
 * 내용 : 지방세 통합 시스템 예외
 ************************** 수정이력 *******************************************
 *    일자      버전   작성자   변경사항
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
 * 파일생성 처리 클래스.
 * @version   1.0
 * @author    2003.09.25 worldhun
 **************************************************************************** */



public class MakeFile {

	private WritableWorkbook workbook;
	private WritableSheet wsheet;
	private WritableCellFormat format;
	private WritableCellFormat fmt;		// 숫자형
	private WritableCellFormat fmt2;	// 소숫점
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
* 기업
* @version 2005 12 12 
* @author 김연길
* @param file_name String객체.
* @return  
********************************************************************************** */ 
	public MakeFile(String file_name){
		m_file_name = file_name;	
		m_txt_data = new StringBuffer();		
	}// default constructor

	/** **********************************************
	 * constructor : 특정 디렉토리에 엑셀파일 생성
	 * @param directory		디렉토리명
	 * @param file_name		파일명
	 * @param sheet_name	엑셀 시트명
	 *********************************************** */

/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 김연길
* @param directory String객체.
* @param file_name String객체.
* @param sheet_name String객체.
* @return  
********************************************************************************** */ 
	public MakeFile(String directory, String file_name, String sheet_name) throws IOException {
		m_txt_data = new StringBuffer();		
		//엑셀 파일 생성
		this.m_file_name = directory + file_name;
		File down_file = new File(m_file_name);
		workbook=Workbook.createWorkbook(down_file);

		wsheet=workbook.createSheet(sheet_name, 0);

        this.m_file_clss = "excel";
		/*
		//Sheet 제목
		for (int i=0;i<sheet_name.length ;i++ ){
			wsheet=workbook.createSheet(sheet_name[i], i);
		}
		*/
	}//end constructor

	/** **********************************************
	 * constructor : 특정 디렉토리에 파일 생성
	 * @param id			회원번호
	 * @param directory		디렉토리명
	 *********************************************** */

	public MakeFile(String id, String directory) throws IOException {
		String curDate = DateUtil.currdate("yyyyMMddHHmmss");
		this.m_file_name =  directory + id + "_" + curDate + ".zip";
	}//end constructor


	/** **********************************************
	 * constructor : 특정 디렉토리에 파일 생성
	 * @param id			회원번호
	 * @param data_clss		정보구분 ( "auth" : 승인내역, "sale" : 이용내역, ... )
	 * @param file_clss		파일구분 ( "excel" : 엑셀, "text" : 텍스트)
	 * @param sheet_name		엑셀 시트명
	 *********************************************** */

/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 김연길
* @param id String객체.
* @param data_clss String객체.
* @param file_clss String객체.
* @param sheet_name String객체.
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
* 기업
* @version 2005 12 12 
* @author 김연길
* @param req_date String객체.
* @param buz_no String객체.
* @param id String객체.
* @param data_clss String객체.
* @param file_clss String객체.
* @param sheet_name String객체.
* @param data_format String객체.
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
	 * getFileName : 파일명(전체경로) 얻어오기
	 *********************************************** */


	public String getFileName() {
		return this.m_file_name;
	}

	/** **********************************************
	 * setCellWidth : 엑셀의 Cell Width 설정
	 * @param col			Cell column 위치
	 * @param width			Cell width값
	 *********************************************** */


	public void setCellWidth(int col, int width) {
		wsheet.setColumnView(col, width);
	}

	/** **********************************************
	 * setCellWidth : 엑셀의 Row Height 설정
	 * @param col			Cell row 위치
	 * @param width			Cell height
	 *********************************************** */


	public void setCellHeight(int row, int height) throws WriteException {
		wsheet.setRowView(row, height);
	}

	/** **********************************************
	 * setDefaultFontFormat : 폰트 기본값 정보 설정
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
	*	폰트 이름, 폰트 크기, 폰트색, 배경색, Bold, Italic 등
	*	폰트 포맷의 속성을 지정한다.
	*	입력할 폰트 종류는 ARIAL, TIMES
	*	폰트 및 배경색 지정을 위한 칼라는 BLACK, BLUE, BROWN, 
	*	GOLD, GRAY_25, GREEN, LIME, ORANGE, PINK, RED, 
	*	ROSE, SKY_BLUE, VIOLET, YELLOW, WHITE 등이다.
	*	
	*/
	/** ***************************************
	* renewal 
	* @version 2005 12 12 
	* @author 김연길
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
	* @author 김연길
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
	 * setDataNumber : 엑셀의 Cell 정보 설정
	 * @param obj		Cell 정보 객체
	 * @param col		Cell column 위치
	 * @param row		Cell row 위치
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
	 * setDataNumber : 엑셀의 Cell 정보 설정
	 * @param obj		Cell 정보 객체
	 * @param col		Cell column 위치
	 * @param row		Cell row 위치
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
	 * setData : 엑셀의 Cell 정보 설정
	 * @param obj		Cell 정보 객체
	 * @param col		Cell column 위치
	 * @param row		Cell row 위치
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
	 * setData : 엑셀의 Cell 정보 설정
	 * @param obj			Cell 정보 객체
	 * @param col			Cell column 위치
	 * @param row			Cell row 위치
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
	 * mergeCell : 엑셀의 Cell 병합
	 * @param col		Cell column 시작위치
	 * @param row		Cell row 시작위치
	 * @param col		Cell column 종료위치
	 * @param row		Cell row 종료위치
	 *********************************************** */


	public void mergeCell(int col1, int row1, int col2, int row2) {
		try {
			wsheet.mergeCells(col1, row1, col2, row2);
		} catch (WriteException e){
			e.printStackTrace();
		}
	}

	/** **********************************************
	 * write : 엑셀파일 저장
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
	 * addData : 텍스트파일 정보 추가
	 * @param data		
	 *********************************************** */


	public void addData(String data) throws IOException {
		this.m_txt_data.append(data);
	}

	/** **********************************************
	*	makeZip	:	파일 압축
	 *********************************************** */
	public boolean makeZip(){
		try{

			String o_File = this.m_file_name.substring(0, this.m_file_name.lastIndexOf(".")) + ".zip";
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(o_File));
			FileInputStream fis = new FileInputStream(this.m_file_name);

			// 파일명을 절대경로 -> 상대경로로 수정하여 압축
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
	*	makeZip	:	파일 압축
	* @param i_FileList		압출할 파일 목록
	 *********************************************** */

	public boolean makeZip(Vector i_FileList){
		try{
			String o_File = this.m_file_name.substring(0, this.m_file_name.lastIndexOf(".")) + ".zip";
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(o_File));
			for ( int i = 0 ; i < i_FileList.size() ; i++ )
			{
				FileInputStream fis = new FileInputStream((String)i_FileList.get(i));

				// 파일명을 절대경로 -> 상대경로로 수정하여 압축
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
	*	makeZip	:	파일 압축
	* @param i_File			압출할 파일
	 *********************************************** */


	public boolean makeZip(String i_File){
		try{
			String o_File = this.m_file_name.substring(0, this.m_file_name.lastIndexOf(".")) + ".zip";
			ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(o_File));
			FileInputStream fis = new FileInputStream(i_File);

			// 파일명을 절대경로 -> 상대경로로 수정하여 압축
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
	*	칼럼 제목과 칼럼의 크기를 입력한다. 
	*	
	*/


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 김연길
* @param col_name String[]객체.
* @param size int[]객체.
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
* 기업
* @version 2005 12 12 
* @author 김연길
* @param hash Hashtable객체.
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
