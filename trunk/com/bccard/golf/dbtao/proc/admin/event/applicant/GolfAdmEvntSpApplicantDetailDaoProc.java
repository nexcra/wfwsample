/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntSpApplicantDetailDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 신청관리 상세
*   적용범위	: golf
*   작성일자	: 2009-07-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.applicant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class GolfAdmEvntSpApplicantDetailDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 신청관리 상세";
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
	
		try {

			// 회원통합테이블 관련 수정사항 진행
			conn = context.getDbConnection("default", null);
		
			String p_idx 		  	  = data.getString("p_idx");
			String evnt_clss		  = data.getString("evnt_clss");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			
			int pidx = 0;
			boolean eof = false;
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx, p_idx);
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				result.addString("seq_no",		rs.getString("APLC_SEQ_NO"));
				result.addString("evnt_nm",		rs.getString("EVNT_NM"));
				result.addString("usr_nm",		rs.getString("USR_NM"));
				result.addString("reg_aton",	rs.getString("REG_ATON"));
				result.addString("status",		rs.getString("STATUS"));
				result.addString("sex_clss",	rs.getString("SEX_CLSS"));
				result.addString("email",		rs.getString("EMAIL"));
				result.addString("hp_ddd_no",	rs.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",	rs.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",	rs.getString("HP_TEL_SNO"));
				result.addString("prz_win_yn",	rs.getString("PRZ_WIN_YN"));
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
	
		sql.append("\n SELECT															");
		sql.append("\n 				T1.APLC_SEQ_NO										");
		sql.append("\n 				,T2.EVNT_NM											");
		sql.append("\n 				,(SELECT HG_NM AS NAME FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=T1.CDHD_ID)AS USR_NM	");
		sql.append("\n 				,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD HH:MM')AS REG_ATON		");
		sql.append("\n 				,(CASE WHEN TO_NUMBER(T2.EVNT_STRT_DATE) <= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) AND TO_NUMBER(T2.EVNT_END_DATE) >= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) THEN '1' ");
		sql.append("\n 					   WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) < TO_NUMBER(T2.EVNT_STRT_DATE) THEN '0'							");
		sql.append("\n 					   WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) > TO_NUMBER(T2.EVNT_END_DATE) THEN '2' ELSE '2' END)as STATUS 	");
		sql.append("\n 				,(CASE WHEN T1.SEX_CLSS='1' THEN '남' WHEN T1.SEX_CLSS='2' THEN '여' ELSE '-' END)AS SEX_CLSS						");
		sql.append("\n 				,T1.EMAIL											");
		sql.append("\n 				,T1.HP_DDD_NO										");
		sql.append("\n 				,T1.HP_TEL_HNO										");
		sql.append("\n 				,T1.HP_TEL_SNO										");
		sql.append("\n 				,T1.PRZ_WIN_YN										");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT T1 left join BCDBA.TBGEVNTMGMT T2 on T1.LESN_SEQ_NO = T2.EVNT_SEQ_NO	");
		sql.append("\n WHERE T1.APLC_SEQ_NO = ?  AND T1.GOLF_SVC_APLC_CLSS = ?		");
		

		return sql.toString();
	}
}
