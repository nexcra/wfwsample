/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpApplicantDetailDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한레슨이벤트 >레슨이벤트 상세보기
*   적용범위	: golf
*   작성일자	: 2009-07-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.applicant;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntSpApplicantDetailDaoProc extends AbstractProc {
	public static final String TITLE = "이벤트라운지 > 특별한레슨이벤트 >레슨이벤트 목록";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";
		String ctnt = "";


		try {
			conn = context.getDbConnection("default", null);

			String evnt_clss 	= data.getString("evnt_clss");
			String p_idx 		= data.getString("p_idx");
			String userId 		= data.getString("userId");

			//날짜 포멧을 위한 부분
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy년MM월dd일",Locale.KOREA);
			
			
			int pidx = 0;
			boolean eof = false;
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx, userId);
			pstmt.setString(++pidx, evnt_clss);
			pstmt.setString(++pidx, p_idx);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				//본문 내용 CLOB처리
				Reader reader = null;
				StringBuffer bufferSt = new StringBuffer();
				reader = rs.getCharacterStream("CTNT");
				if( reader != null )  {
					char[] buffer = new char[1024]; 
					int byteRead; 
					while((byteRead=reader.read(buffer,0,1024))!=-1)  
						bufferSt.append(buffer,0,byteRead);  
					reader.close();
				}
				ctnt = bufferSt.toString();
				ctnt = ctnt.replaceAll("\r\n", "<br/>");
				

				result.addString("seq_no", 			rs.getString("EVNT_SEQ_NO"));
				result.addString("use_cnt", 		rs.getString("USECNT"));
				result.addString("evnt_nm", 		rs.getString("EVNT_NM"));
				result.addString("status", 			rs.getString("STATUS"));
				result.addString("rcru_pe_org_num", rs.getString("RCRU_PE_ORG_NUM"));
				result.addString("lesn_norm_cost", 	rs.getString("LESN_NORM_COST"));
				result.addString("lesn_dc_cost",	rs.getString("LESN_DC_COST"));
				result.addString("evnt_bnft_expl", 	rs.getString("EVNT_BNFT_EXPL"));
				result.addString("affi_firm_expl", 	rs.getString("AFFI_FIRM_EXPL"));
				result.addString("img_file_path", 	rs.getString("IMG_FILE_PATH"));
				result.addString("deadline", 		rs.getString("DEADLINE"));
				result.addString("evnt_strt_date", 	sdf.format(rs.getDate("EVNT_STRT_DATE")));
				result.addString("evnt_end_date", 	sdf.format(rs.getDate("EVNT_END_DATE")));
				result.addString("lesn_strt_date", 	sdf.format(rs.getDate("LESN_STRT_DATE")));
				result.addString("lesn_end_date", 	sdf.format(rs.getDate("LESN_END_DATE")));
				result.addString("ctnt", 			ctnt);
				eof = true;
			} 
			
			
			
			if(!eof) result.addString("RESULT", "01");
			
			 
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 	SELECT														"); 
		sql.append("\n 		 T1.EVNT_SEQ_NO											");
		sql.append("\n 		,T1.EVNT_NM												");
		sql.append("\n 		,T1.RCRU_PE_ORG_NUM										");
		sql.append("\n 		,TO_CHAR(T1.LESN_NORM_COST,'999,999,999,999,999')AS LESN_NORM_COST	");
		sql.append("\n 		,TO_CHAR(T1.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST		");
		sql.append("\n 		,T1.EVNT_BNFT_EXPL										");
		sql.append("\n 		,T1.CTNT												");
		sql.append("\n 		,T1.AFFI_FIRM_EXPL										");
		sql.append("\n 		,T1.IMG_FILE_PATH										");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.EVNT_STRT_DATE),'YYYY-MM-DD') AS EVNT_STRT_DATE	");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.EVNT_END_DATE),'YYYY-MM-DD') AS EVNT_END_DATE	");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.LESN_STRT_DATE),'YYYY-MM-DD') AS LESN_STRT_DATE	");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.LESN_END_DATE),'YYYY-MM-DD') AS LESN_END_DATE	");
		sql.append("\n 		,(TO_NUMBER(T1.EVNT_END_DATE) - TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')))AS DEADLINE");
		sql.append("\n		,(SELECT COUNT(*)as CNT FROM BCDBA.TBGAPLCMGMT WHERE LESN_SEQ_NO = T1.EVNT_SEQ_NO AND CDHD_ID = ? )AS USECNT");
		sql.append("\n 		,(CASE WHEN TO_NUMBER(T1.EVNT_STRT_DATE) <= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) AND TO_NUMBER(T1.EVNT_END_DATE) >= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) THEN '1' ");
		sql.append("\n 			 WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) < TO_NUMBER(T1.EVNT_STRT_DATE) THEN '0'							");
		sql.append("\n 			 WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) > TO_NUMBER(T1.EVNT_END_DATE) THEN '2' ELSE 'N' END)as STATUS 	");
		sql.append("\n FROM BCDBA.TBGEVNTMGMT	T1									");
		sql.append("\n WHERE T1.EVNT_CLSS = ?	AND BLTN_YN = 'Y'					");
		sql.append("\n  	AND EVNT_SEQ_NO = ?										");

		

		return sql.toString();
	}
}
