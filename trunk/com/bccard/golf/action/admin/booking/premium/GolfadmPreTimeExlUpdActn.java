/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeExlUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄 티타임 첨부파일 임시등록
*   적용범위  : Golf
*   작성일자  : 2010-06-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;

import com.bccard.golf.common.AppConfig;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.*;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.oreilly.servlet.MultipartRequest;

/******************************************************************************
* Golfloung
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfadmPreTimeExlUpdActn extends GolfActn{

	public static final	String TITLE ="첨부파일 임시등록";

	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		PreparedStatement pstmt 		= null;
		PreparedStatement pstmtDate 	= null;	
		PreparedStatement pstmtTime 	= null;	
		PreparedStatement pstmtMaxDate 	= null;	
		PreparedStatement pstmtMaxTime 	= null;	
		PreparedStatement pstmtChkTime 	= null;	
		Connection con 					= null;
		ResultSet rs 					= null;
		ResultSet rsChkTime				= null;
		String subpage_key 				= "default";
		
		try {
			
			con = context.getDbConnection("default", null);
			con.setAutoCommit(false);
			
			// 저장할 디렉토리가 없을 경우 새로 생성한다.
			String atcTmpPath = AppConfig.getAppProperty("UPLOAD_TMP_PATH")+"/bk_prm/tmp/";		// 저장할 디렉토리
			atcTmpPath = atcTmpPath.replaceAll("\\.\\.","");	
			File createPath  =	new	File(atcTmpPath);
			if (!createPath.exists()){
				createPath.mkdirs();
			}
			int	intMaxSize	= 10*1024*1024; //2MB
			MultipartRequest multi  = new MultipartRequest(request, atcTmpPath, intMaxSize,"euc-kr", new com.oreilly.servlet.multipart.DefaultFileRenamePolicy());
			String orgNamePath = multi.getParameter("upFilePath");		// 파일이름
			String tmpFileName = multi.getFilesystemName  ("upFile");	// upload 된후의	파일 이름
			String orgFileName = multi.getOriginalFileName("upFile");	// 원래 파일	이름
	
			File file = new File(atcTmpPath,tmpFileName);	// 파일
			long fsize = file.length();						// 파일 사이즈

			String errMsg = "";		// 에러메세지
			String failMerCode="";	// 에러코드
			int succ_cnt = 0;		// 성공 갯수
			int succ_day_cnt = 0;	// 부킹일 테이블 인서트 성공 수 
			int fail_cnt = 0;		// 실패 갯수
			int worktotrow = 0;		// 행수
			int workcol    = 0;		// 열수
			
			String fname =  file.getName();					
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			worktotrow =  sheet.getRows();
	
			debug("## admPreTimeListActn | worktotrow : "+worktotrow+ " | fname : "+fname+" | 엑셀 row"+sheet.getRows()+"\n");

			String gr_id = "";				// 골프장 아이디
			String gr_id_pre = "";			// 골프장 아이디 이전
			int gr_seq = 0;					// 골프장 seq
			String bk_date = "";			// 부킹일자
			int bk_date_seq = 0;			// 부킹일자 seq
			int max_date_seq = 0;			// 부킹일자 seq 최대값
			String bk_time = "";			// 부킹 시간
			int int_bk_time = 0;			// 부킹 시간 seq
			int max_time_seq = 0;			// 부킹시간 seq 최대값
			int bk_time_seq = 0;			// 부킹시간 seq
			String gr_cs = "";				// 코스
			String view_yn = "";			// 노출여부
			int idx = 0;
			int result = 0;					// 처리 결과

			if ( worktotrow <= 0 ) {

				debug("유효한 파일이 아닙니다.eeeeeee");
				errMsg = "유효한 건수가 없습니다.";

			} else {

				// 부킹일자 최대값 가져오기 getMaxDateQuery
				pstmtMaxDate = con.prepareStatement(getMaxDateQuery());
				rs = pstmtMaxDate.executeQuery();
				if ( rs.next() ){
					max_date_seq = rs.getInt("BK_DATE_SEQ");
				}
				pstmtMaxDate.close();

				// 티타임 idx 최대값 가져오기 getMaxTimeQuery
				pstmtMaxTime = con.prepareStatement(getMaxTimeQuery());
				rs = pstmtMaxTime.executeQuery();
				if ( rs.next() ){
					max_time_seq = rs.getInt("TIME_SEQ_NO")+succ_cnt;
				}
				pstmtMaxTime.close();
					

				String sqlTime = this.getTimeInsertQuery();
				pstmtTime = con.prepareStatement(sqlTime);
				
				// 1.엑셀파일 유효성 체크 	
				for (int r = 1; r < worktotrow; r++) {
					
					try{
						workcol= 0;
						gr_id    	 = StrUtil.isNull(sheet.getCell(workcol, r).getContents(),"");
						bk_date    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						gr_cs    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						bk_time    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						view_yn    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						
						int_bk_time = Integer.parseInt(bk_time);
						//debug(" r : "+r+ " | gr_id : " + gr_id + " | bk_date : " + bk_date+ " | bk_time : " + bk_time+ " | gr_cs : " + gr_cs+ " | view_yn : " + view_yn);
						
						
						if (!"".equals(gr_id) && !"".equals(bk_date) && !"".equals(bk_time) && !"".equals(gr_cs) && !"".equals(view_yn)){
							
							if(!gr_id.equals(gr_id_pre) || gr_seq==0){
								// 골프장 idx 가져오기
								pstmt = con.prepareStatement(getGreenSeqSelectQuery());
								pstmt.setString(1, gr_id);
								rs = pstmt.executeQuery();
								if ( rs!=null && rs.next() ){
									gr_seq = rs.getInt("AFFI_GREEN_SEQ_NO");
								}
								pstmt.close();
							}
							
							if(gr_seq>0){

								// 부킹일자 검색 getChkDateQuery
								pstmt = con.prepareStatement(getChkDateQuery());
								idx = 0;
								pstmt.setInt(++idx, gr_seq);
								pstmt.setString(++idx, gr_cs);
								pstmt.setString(++idx, bk_date);
								rs = pstmt.executeQuery();
								if ( rs.next() ){
									bk_date_seq = rs.getInt("RSVT_ABLE_SCD_SEQ_NO");
								}else{
									// 부킹일자 등록하기 getDateInsertQuery
									pstmtDate = con.prepareStatement(getDateInsertQuery());
									bk_date_seq = max_date_seq+succ_day_cnt;
									idx = 0;
									pstmtDate.setInt(++idx, bk_date_seq);
									pstmtDate.setInt(++idx, gr_seq);
									pstmtDate.setString(++idx, gr_cs);
									pstmtDate.setString(++idx, bk_date);
									pstmtDate.setString(++idx, gr_id);
//									pstmtDate.addBatch(); 
									result = pstmtDate.executeUpdate();
									succ_day_cnt++;
									pstmtDate.close();
								}
								pstmt.close();
								
								pstmtChkTime = null;
								rsChkTime = null;
								// 티타임 등록되어 있는지 알아보기 getChkTimeQuery
								idx = 0;
								pstmtChkTime = con.prepareStatement(getChkTimeQuery());
								pstmtChkTime.setInt(++idx, bk_date_seq);
								pstmtChkTime.setInt(++idx, gr_seq);
								pstmtChkTime.setInt(++idx, int_bk_time);
								rsChkTime = pstmtChkTime.executeQuery();
								if ( rsChkTime.next() ){
									bk_time_seq = rsChkTime.getInt("RSVT_ABLE_BOKG_TIME_SEQ_NO");
									failMerCode = failMerCode+"<br> 중복데이터 : "+gr_id+" | "+bk_date+" | "+gr_cs+" | " + bk_time + " | bk_time_seq : " + bk_time_seq;
									fail_cnt++;
								}else{
									idx = 0;
									pstmtTime.setInt(++idx, max_time_seq+succ_cnt);
									pstmtTime.setInt(++idx, bk_date_seq);
									pstmtTime.setInt(++idx, gr_seq);
									pstmtTime.setString(++idx, bk_time);
									pstmtTime.setString(++idx, gr_id);
									pstmtTime.setString(++idx, view_yn);
									pstmtTime.addBatch();
									//result = pstmtTime.executeUpdate();
									succ_cnt++;
								}	
								pstmtChkTime.close();

								

							}else{
								failMerCode = failMerCode+"<br>골프장코드 오류 : "+gr_id;
							}

							if (succ_cnt % 10000 == 0 ){
//								pstmtDate.executeBatch();
								pstmtTime.executeBatch();
							}
							
						}else{
							debug("디비쿼리 자체에 오류 | gr_id : "+gr_id+" | bk_date : "+bk_date+" / bk_time : " + bk_time + "\n");
							if (errMsg.equals("") )
								errMsg = "쿠폰번호 엑셀 파일에서 "+String.valueOf((r+1))+"번째 행 데이타가 이상이 있습니다. 다시 시도하시길 바랍니다.";
							fail_cnt++;
							
							if("".equals(failMerCode)) failMerCode = gr_id;
							else failMerCode = failMerCode+"<br>디비쿼리오류 : "+gr_id+" | "+bk_date+" | "+gr_cs+" | " + bk_time;

							fail_cnt++;
						}

						
						workcol++;
						gr_id_pre = gr_id;

					}catch(Throwable ignore){ 
					

						debug(TITLE, ignore);
						debug("엑셀 파일에서 "+String.valueOf((r+1))+"번째 행, "+String.valueOf((workcol+1))+"번재 칼럼에서 데이타가 이상이 있습니다.\n문자가 들어가 있는지 확인 후 다시 시도하시길 바랍니다.");							
						fail_cnt++;
						if("".equals(gr_id)) failMerCode = gr_id;
						else failMerCode = failMerCode+"<br>데이터오류 : "+gr_id+" | "+bk_date+" | "+gr_cs+" | " + bk_time;

					}
				}
				try{
					debug("## 배치실행");
//					pstmtDate.executeBatch();
					pstmtTime.executeBatch();
					con.commit();
				}
				catch(Throwable ignore) 
				{
					succ_cnt=0;
					
				}
				con.setAutoCommit(true);
				
				System.out.print("## TpMerExcelUpLoadActn | succ_cnt : "+succ_cnt+ " | fail_cnt : "+fail_cnt+" | failMerCode : "+failMerCode+"\n");
			}
			
			worktotrow = worktotrow-1;
			request.setAttribute("succ_cnt" ,Integer.toString(succ_cnt));
			request.setAttribute("fail_cnt"	,Integer.toString(fail_cnt));
			request.setAttribute("total_cnt"	,Integer.toString(worktotrow));
			request.setAttribute("failMerCode"	,failMerCode);
			request.setAttribute("err_msg"	,errMsg);
			request.setAttribute("filename"	,fname);
			
			request.setAttribute("tmpFileName"	 ,tmpFileName);
			request.setAttribute("tmpFilePath"	 	,atcTmpPath);
			request.setAttribute("tmpFileSize"		 ,String.valueOf(fsize)	);
			request.setAttribute("orgFileName"	 ,orgFileName);
			request.setAttribute("orgNamePath"	 ,orgNamePath);
			request.setAttribute("varNm"			 ,multi.getParameter("varNm"));
			request.setAttribute("varSize"		 ,multi.getParameter("varSize"));
			request.setAttribute("varPoint"		 ,multi.getParameter("varPoint"));

	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	/** ***********************************************************************
	* 골프장 IDX 가져오기
	************************************************************************ */
	private String getGreenSeqSelectQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT AFFI_GREEN_SEQ_NO FROM BCDBA.TBGAFFIGREEN WHERE GREEN_ID=?	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 부킹일자 검색
	************************************************************************ */
	private String getChkDateQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT RSVT_ABLE_SCD_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT WHERE AFFI_GREEN_SEQ_NO=? AND GOLF_RSVT_CURS_NM=? AND BOKG_ABLE_DATE=? 	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 부킹일자 최대값 가져오기
	************************************************************************ */
	private String getMaxDateQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT MAX(NVL(RSVT_ABLE_SCD_SEQ_NO,0))+1 BK_DATE_SEQ FROM BCDBA.TBGRSVTABLESCDMGMT 	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 부킹일자 등록하기
	************************************************************************ */
	private String getDateInsertQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGRSVTABLESCDMGMT (	");
		sql.append("\n	RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, GOLF_RSVT_CURS_NM, BOKG_ABLE_DATE, REG_MGR_ID, REG_ATON, GOLF_RSVT_DAY_CLSS	");
		sql.append("\n	) VALUES (	");
		sql.append("\n			?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD'),'P'	");
		sql.append("\n	)	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 부킹시간 검색
	************************************************************************ */
	private String getChkTimeQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT WHERE RSVT_ABLE_SCD_SEQ_NO=? AND AFFI_GREEN_SEQ_NO=? AND TO_NUMBER(BOKG_ABLE_TIME)=?	"); 
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 부킹시간 최대값 가져오기
	************************************************************************ */
	private String getMaxTimeQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT NVL(MAX(RSVT_ABLE_BOKG_TIME_SEQ_NO),0)+1 TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 부킹시간 등록하기
	************************************************************************ */
	private String getTimeInsertQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	");
		sql.append("\n			RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, BOKG_ABLE_TIME, REG_MGR_ID, REG_ATON, EPS_YN ,BOKG_RSVT_STAT_CLSS	");
		sql.append("\n	) VALUES (	");
		sql.append("\n			?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, '0001'	");
		sql.append("\n	)	");
		return sql.toString();
	}
	
}
