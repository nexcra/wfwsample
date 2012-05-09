/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpMyprizeDetailDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한레슨이벤트 >나의당첨내역 상세보기
*   적용범위	: golf
*   작성일자	: 2009-07-09
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.myprize;

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
public class GolfEvntSpMyprizeDetailDaoProc extends AbstractProc {
	public static final String TITLE = "이벤트라운지 > 특별한레슨이벤트 >나의당첨내역 상세보기";
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
			conn = context.getDbConnection("default", null);
			String p_idx 		= data.getString("p_idx");
			
			
			//DATE 포멧
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일",Locale.KOREA);
			
			int pidx = 0;
			boolean eof = false;
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx, p_idx);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				result.addString("seq_no", 			rs.getString("APLC_SEQ_NO"));
				result.addString("evnt_seq_no", 	rs.getString("LESN_SEQ_NO"));
				result.addString("evnt_nm",			rs.getString("EVNT_NM"));
				result.addString("auth_no", 		rs.getString("AUTH_NO"));
				result.addString("odr_no", 			rs.getString("ODR_NO"));
				result.addString("pgrs_yn", 		rs.getString("PGRS_YN"));
				result.addString("pgrs_status",		rs.getString("PGRS_STATUS"));
				result.addString("email", 			rs.getString("EMAIL"));
				result.addString("hp_ddd_no", 		rs.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno", 		rs.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno", 		rs.getString("HP_TEL_SNO"));
				result.addString("today",			rs.getString("TODAY"));
				result.addString("dur_date",		rs.getString("DUR_DATE"));
				result.addString("reg_aton",		rs.getString("REG_ATON"));
				result.addString("status",			rs.getString("STATUS"));
				result.addString("realPayAmt",		rs.getString("REALPAYAMT"));
				result.addString("lesn_dc_cost",	rs.getString("LESN_DC_COST"));
				result.addString("deadline_date",	rs.getString("DEADLINE_DATE"));
				result.addString("sttl_aton",		rs.getString("STTL_ATON"));
				result.addString("cncl_aton",		rs.getString("CNCL_ATON"));
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
	
		sql.append("\n 	SELECT															"); 
		sql.append("\n 		 T1.APLC_SEQ_NO												");
		sql.append("\n 		,T1.LESN_SEQ_NO												");
		sql.append("\n 		,T2.EVNT_NM													");
		sql.append("\n 		,T3.AUTH_NO													");
		sql.append("\n 		,T3.ODR_NO													");
		sql.append("\n 		,T1.PGRS_YN													");
		sql.append("\n 		,(CASE WHEN T2.LESN_DC_COST <> '0' AND T3.STTL_STAT_CLSS='Y' THEN 'C' "+
							      "WHEN  T2.LESN_DC_COST <> '0' AND  T3.STTL_STAT_CLSS = 'N' and T1.PGRS_YN ='D' THEN 'D' "+
								  "WHEN T3.STTL_STAT_CLSS <> 'Y' and T1.PGRS_YN = 'N' THEN 'N' "+
								  "WHEN  T2.LESN_DC_COST = '0' AND T1.PRZ_WIN_YN = 'Y' THEN 'D' ELSE 'N' END)AS PGRS_STATUS");
		sql.append("\n 		,T1.EMAIL													");
		sql.append("\n 		,T1.HP_DDD_NO												");
		sql.append("\n 		,T1.HP_TEL_HNO												");
		sql.append("\n 		,T1.HP_TEL_SNO												");
		sql.append("\n 		,T2.LESN_DC_COST AS REALPAYAMT								");
		sql.append("\n 		,TO_CHAR(SYSDATE,'YYYY-MM-DD')AS TODAY						");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.REG_ATON, 'yyyy-MM-dd hh24miss'),'YYYY.MM.DD')AS REG_ATON					");
		sql.append("\n 		,TO_CHAR(TO_DATE(T2.LESN_STRT_DATE,'yyyy-MM-dd')-3,'YYYY.MM.DD')AS DEADLINE_DATE				");
		sql.append("\n 		,TO_CHAR(T2.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST									");
		sql.append("\n 		,(CASE WHEN T3.STTL_ATON is not null THEN TO_CHAR(TO_DATE(T3.STTL_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD') ELSE '-' END) AS STTL_ATON	");
		sql.append("\n 		,(CASE WHEN T3.CNCL_ATON is not null THEN TO_CHAR(TO_DATE(T3.CNCL_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD')	ELSE '-' END)AS CNCL_ATON	");
		sql.append("\n 		,(TO_DATE(T2.LESN_STRT_DATE,'yyyyMMdd')- 3 - TO_DATE(TO_CHAR(SYSDATE,'yyyyMMdd')))as DUR_DATE 	");
		sql.append("\n 		,(CASE WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) < TO_NUMBER(T2.EVNT_STRT_DATE) THEN '이벤트 시작전' "+
								"WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) >= TO_NUMBER(T2.EVNT_STRT_DATE) and TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) <= TO_NUMBER(T2.EVNT_END_DATE) THEN '진행'"+
								"WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) > TO_NUMBER(T2.EVNT_END_DATE) THEN '마감' ELSE '마감' END)AS STATUS	");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT T1 left join BCDBA.TBGEVNTMGMT T2 on T1.LESN_SEQ_NO = T2.EVNT_SEQ_NO			");
		sql.append("\n 		left join BCDBA.TBGSTTLMGMT T3 on T1.APLC_SEQ_NO = T3.STTL_GDS_SEQ_NO							");
		sql.append("\n WHERE T1.APLC_SEQ_NO = ?											");

		return sql.toString();
	}
	
}
