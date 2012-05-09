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

import java.io.File; 
import java.io.IOException;
import java.util.Set;
import java.util.Iterator;
import java.util.List;

import jxl.write.WritableFont;
import net.e4net.vman.VManAppServer;

import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.MakeFile;
import com.bccard.golf.common.MakeFileResEtt;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BcUtil;
import com.bccard.golf.dbtao.DbTaoResult;


/** ***************************************
*  조회 정보 다운로드 
* @version 2005 12 12 
* @author 이보아
********************************************** */
public class DownloadData {
	private String m_user_nm;		// 회원번호
	private String m_data_clss;		// 자료구분 : 승인, 이용, 청구
	private String m_file_clss;		// 파일구분 : 엑셀, 텍스트
	private String m_file_name;		// 파일이름
	// -------------------------------------------------------------
	// Added By, Leee Eun Ho, 2004-09-19, Sun
	// -------------------------------------------------------------
	private String m_req_date;		// 대기업의 청구파일생성(청구일자)
	private String m_buz_no;		// 대기업의 청구파일생성(사업자번호)
	// -------------------------------------------------------------
	private MakeFileResEtt resultEtt;

/** ******************************************************************************** 
* default constructor
* @version 2005 12 12 
* @author 이보아
* @param id String객체.
* @param data_clss String객체.
* @param file_clss String객체.
* @return  
********************************************************************************** */ 
	public DownloadData(String id, String data_clss, String file_clss) {
		this.m_user_nm = id;
		this.m_data_clss = data_clss;
		this.m_file_clss = file_clss;
		resultEtt = new MakeFileResEtt();

	}

/** ******************************************************************************** 
* constructor : VManServlet 구동용
* @version 2005 12 12 
* @author 이보아
* @param id String객체.
* @param file_name String객체.
* @param file_clss String객체.
* @param check boolean객체.
* @return  
********************************************************************************** */ 
	public DownloadData(String id, String file_name, String file_clss, boolean check) {
		this.m_user_nm = id;
		this.m_file_name = file_name;
		this.m_file_clss = file_clss;
		resultEtt = new MakeFileResEtt();
	}

/** **********************************************
// Writted By, Lee Eun Ho, 2004-09-19, Sun
 *********************************************** */

/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @param req_date String객체.
* @param buz_no String객체.
* @param id String객체.
* @param data_clss String객체.
* @param file_clss String객체.
* @return  
********************************************************************************** */ 
	public DownloadData(String req_date, String buz_no, String id, String data_clss, String file_clss) {
		this.m_req_date = req_date;
		this.m_buz_no = buz_no;
		this.m_user_nm = id;
		this.m_data_clss = data_clss;
		this.m_file_clss = file_clss;
		resultEtt = new MakeFileResEtt();
	}

/********************************************************************************** 
* MakeJsScript : VManServlet을 위한 자바스크립트 생성
* @version 2005 12 12 
* @author 이보아
* @return  MakeFileResEtt 
************************************************************************************/ 
	public MakeFileResEtt makeJsScript() {
		String js = "";
		String sessionId = "";

		// VManServlet 수행
		String fileFilter = "Zip Files (*.xls)|*.xls|All Files (*.*)|*.*||";
		VManAppServer vman = new VManAppServer();

		vman.setRemoveFile(false);		// 기존 파일 보존

		File v_man_file  = new File(this.m_file_name); 
			BcLog.accessLog("=============>authList this.m_file_name :"+this.m_file_name );
			BcLog.accessLog("=============>authList this.m_user_nm :"+this.m_user_nm );
			BcLog.accessLog("=============>authList v_man_file :" +v_man_file);
sessionId = vman.putFile(this.m_user_nm, v_man_file);   


		if( (sessionId != null) || (sessionId != "") ){
			// 다운로드 Filter 셋팅 
			vman.setFileFilter(fileFilter);
			// 동적인 자바스크립트 생성
			js = vman.getJavaScript(); 
		}else{
		}

		// 동적 스크립트가 잘 넘어오는지 검사
		if(js.indexOf("ERROR") > -1){
			resultEtt.setErr_msg("동적 스크립트 생성 실패");
		}

		String filename = m_file_name.substring(m_file_name.lastIndexOf("/")+1, m_file_name.length());	// 상대경로파일명
		resultEtt.setFile_name(filename);
		if ( "excel".equals(this.m_file_clss) ) {
			resultEtt.setFile_type("Excel (.xls)");
		} else {
			resultEtt.setFile_type("Text (.txt)");
		}
		File file_tmp = new File(m_file_name);
		String fileSize = "";
		long f_size = file_tmp.length();

		if(f_size < 1024){
			fileSize = f_size + " Byte";
		}else if(f_size >= 1024 && f_size < 1024*1024){
			fileSize = f_size/1024 + " KB";
		}else if(f_size >= 1024*1024 && f_size < 1024*1024*1024){
			fileSize = f_size/(1024*1024) + " MB";
		}else if(f_size >= 1024*1024*1024){
			fileSize = f_size/(1024*1024*1024) + " GB";
		}
		resultEtt.setFile_size(fileSize);
		resultEtt.setJs_str(js);

		return resultEtt;
	}

/** ******************************************************************************** 
* makefile : 파일생성 - 회계파일 디자인
* @version 2005 12 12 
* @author 이보아
* @param data Collection객체.
* @param define_yn String객체.
* @return  MakeFileResEtt 
********************************************************************************** */ 
	public MakeFileResEtt makefile(DbTaoResult data, String define_yn) {
		if ( authList(data) ) {
			return makeJsScript();
		} else {
			return resultEtt;
		}
	}

/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @param src String객체.
* @return  String 
********************************************************************************** */ 
	public String us2kr_1(String src)    {
        String ret = "";
        if ( src != null)
        {
            try {
                if(src.length() > 0)
                    ret = new String(src.getBytes("KSC5601"),"8859_1");
                } catch(IOException e) {
                    
                    }
        }
        return ret;
    }


/** ******************************************************************************** 
* authList : 수납내역 조회
* @version 2005 12 12 
* @author 이보아
* @param data Collection객체.
* @return  boolean 
********************************************************************************** */ 
	public boolean authList(DbTaoResult data) {
		try {

			MakeFile mf = new MakeFile(m_user_nm, m_data_clss, m_file_clss, "수납내역");
			if ( "excel".equals(m_file_clss) ) {			// 엑셀
				int col = 0, row = 0 ;
				mf.setFontFormat("ARIAL", WritableFont.DEFAULT_POINT_SIZE, jxl.format.Colour.BLACK,
						jxl.format.Colour.GRAY_25, jxl.format.Alignment.CENTRE, jxl.format.Border.ALL,					
						jxl.format.BorderLineStyle.THIN, true, false, false);	

				// 타이틀
				//mf.setData("납세일련번호", col++, row);
				mf.setCellWidth(col,25);
				mf.setData("기관명", col++, row); 
				mf.setCellWidth(col,30);
				mf.setData("납부번호", col++, row);
				mf.setCellWidth(col,10);
				mf.setData("납세자명", col++, row); 
				mf.setCellWidth(col,15);
				mf.setData("전화번호", col++, row); 
				mf.setCellWidth(col,20);
				mf.setData("카드번호", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("승인일자", col++, row); 
				mf.setCellWidth(col,15);
				mf.setData("금액", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("수납결과", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("승인번호", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("할부기간", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("카드소지자명", col++, row); 
				mf.setCellWidth(col,20);
				mf.setData("카드소지자 주민번호", col++, row); 
				mf.setCellWidth(col,20);
				mf.setData("납부자 주민번호", col++, row); 
				//mf.setCellWidth(col,10);
				//mf.setData("과세년월", col++, row); 
				//mf.setData("납부방법구분", col++, row); 
				//mf.setData("납기구분", col++, row); 
				//mf.setData("세목명", col++, row); 
				
				data.first();
				if (data.isNext() ) {
					mf.setFontFormat("ARIAL", WritableFont.DEFAULT_POINT_SIZE, jxl.format.Colour.BLACK,
						jxl.format.Colour.WHITE, jxl.format.Alignment.RIGHT, jxl.format.Border.ALL,
						jxl.format.BorderLineStyle.THIN, false, false, true);
					do {
						col = 0 ;
						row++;
						data.next();
						//mf.setData(data.getString("납세일련번호"), col++, row);
						mf.setData(data.getString("기관명"), col++,  row); 
						mf.setData(data.getString("납부번호"), col++,  row);
						mf.setData(data.getString("납세자명"), col++,  row); 
						mf.setData(data.getString("전화번호"), col++,  row); 
						mf.setData(data.getString("카드번호"), col++,  row); 
						mf.setData(data.getString("승인일자"), col++,  row); 
						mf.setData(data.getString("금액"), col++,  row); 
						mf.setData(data.getString("수납결과"), col++,  row); 
						mf.setData(data.getString("승인번호"), col++,  row); 
						mf.setData(data.getString("할부기간"), col++,  row); 
						mf.setData(data.getString("카드성명"), col++,  row); 
						//mf.setData(data.getString("카드등급"), col++,  row); 
						mf.setData(data.getString("소지자주민번호"), col++,  row); 
						mf.setData(data.getString("납세주민번호"), col++,  row); 
						//mf.setData(data.getString("과세년월"), col++,  row); 
						//mf.setData(data.getString("납부방법구분"), col++,  row); 
						//mf.setData(data.getString("납기구분"), col++,  row); 
						//mf.setData(data.getString("세목명"), col++,  row); 
					} while(data.isNext());
				} else {
					row++;
					mf.setFontFormat("ARIAL", WritableFont.DEFAULT_POINT_SIZE, jxl.format.Colour.RED,jxl.format.Colour.WHITE, jxl.format.Alignment.CENTRE, jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN, false, false, true);
					mf.mergeCell(col, row, col + 11, row);
					mf.setData("검색된 결과가 없습니다.", col, row);
				}
			}

			if ( mf.write() ) {					// 파일 생성
				this.m_file_name = mf.getFileName();
				/*
				if ( mf.makeZip() ) {			// zip 파일 생성
			BcLog.accessLog("=============>authList this.m_file_name10 :"+this.m_file_name );
					this.m_file_name = mf.getFileName();
			BcLog.accessLog("=============>authList this.m_file_name11 :"+this.m_file_name );

				} else {
					resultEtt.setErr_msg("Zip 파일 생성중 오류가 발생했습니다.<br>잠시 후 다시 시도하시길 바랍니다.");
					return false;
				}
				*/
			} else {
				resultEtt.setErr_msg("파일 생성중 오류가 발생했습니다.<br>잠시 후 다시 시도하시길 바랍니다.");
				return false;
			}
			return true;
		} catch(Exception e){
			
			resultEtt.setErr_msg("파일 생성중 에러가 발생했습니다.<br>잠시 후 다시 시도하시길 바랍니다.");
			return false;
		}
	}

}
