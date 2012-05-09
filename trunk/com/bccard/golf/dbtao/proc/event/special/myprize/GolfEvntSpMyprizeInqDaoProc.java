/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpMyprizeInqDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한레슨이벤트 >나의당첨내역 목록
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
public class GolfEvntSpMyprizeInqDaoProc extends AbstractProc {
	public static final String TITLE = "이벤트라운지 > 특별한레슨이벤트 >나의당첨내역 목록";
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
		String evnt_nm = "";
 
		try { 
			conn = context.getDbConnection("default", null);
			
			String userId = data.getString("userId");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			long page_no = data.getLong("page_no");
			
			int pidx = 0;
			boolean eof = false;
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			pstmt.setString(++pidx, userId);
			pstmt.setLong(++pidx, page_no);
			
			rs = pstmt.executeQuery();
			
			int serial = 0;
			 
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				evnt_nm = rs.getString("EVNT_NM");
				
				if(evnt_nm.length() > 10) evnt_nm = evnt_nm.substring(0,10)+"..."; 
				
				result.addString("seq_no", 			rs.getString("APLC_SEQ_NO"));
				result.addInt("serial", 			rs.getInt("ART_NUM") - serial);
				result.addString("evnt_nm", 		evnt_nm);
				result.addString("lesn_dc_cost", 	rs.getString("LESN_DC_COST").trim());
				result.addString("pgrs_yn", 		rs.getString("PGRS_YN"));
				result.addString("prz_win_yn", 		rs.getString("PRZ_WIN_YN"));
				result.addString("auth_no", 		rs.getString("AUTH_NO"));
				result.addString("deadline_date",	rs.getString("DEADLINE_DATE"));
				result.addString("deadline_cnt",	rs.getString("DEADLINE_CNT"));
				result.addString("reg_aton",		rs.getString("REG_ATON"));
				result.addString("sttl_aton",		rs.getString("STTL_ATON"));
				result.addString("cncl_aton",		rs.getString("CNCL_ATON"));
				result.addString("total_cnt",		rs.getString("TOT_CNT") );
				result.addString("curr_page",		rs.getString("PAGE") );
				eof = true;
				serial++;
			
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
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,APLC_SEQ_NO									");
		sql.append("\n 				,EVNT_NM										");
		sql.append("\n 				,LESN_DC_COST									");
		sql.append("\n 				,PGRS_YN										");
		sql.append("\n 				,PRZ_WIN_YN										");
		sql.append("\n 				,AUTH_NO										");
		sql.append("\n 				,DEADLINE_DATE									");
		sql.append("\n 				,DEADLINE_CNT									");
		sql.append("\n 				,REG_ATON										");
		sql.append("\n 				,STTL_ATON										");
		sql.append("\n 				,CNCL_ATON										");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.APLC_SEQ_NO							");
		sql.append("\n 						,T2.EVNT_NM								");
		sql.append("\n 						,(CASE WHEN T2.LESN_DC_COST <> '0' AND T3.STTL_STAT_CLSS='Y' THEN 'C' "+
												  "WHEN  T2.LESN_DC_COST <> '0' AND  T3.STTL_STAT_CLSS = 'N' and T1.PGRS_YN ='D' THEN 'D' "+
												  "WHEN T3.STTL_STAT_CLSS <> 'Y' and T1.PGRS_YN = 'N' THEN 'N' "+
												  "WHEN  T2.LESN_DC_COST = '0' AND T1.PRZ_WIN_YN = 'Y' THEN 'D' ELSE 'N' END)AS PGRS_YN");
		sql.append("\n 						,T1.PRZ_WIN_YN							");
		sql.append("\n 						,T3.AUTH_NO								");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD')AS REG_ATON			");
		sql.append("\n 						,TO_CHAR(T2.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST							");
		sql.append("\n                      ,TO_CHAR(TO_DATE(T2.LESN_STRT_DATE,'yyyy-MM-dd')-3 ,'YYYY.MM.DD')AS DEADLINE_DATE		");
		sql.append("\n                      ,((TO_NUMBER(T2.LESN_STRT_DATE)-3)  - TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) )as DEADLINE_CNT ");
		sql.append("\n                      ,(CASE WHEN T3.STTL_ATON is not null THEN TO_CHAR(TO_DATE(T3.STTL_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD') ELSE '-' END) AS STTL_ATON	");
		sql.append("\n                      ,(CASE WHEN T3.CNCL_ATON is not null THEN TO_CHAR(TO_DATE(T3.CNCL_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD')	ELSE '-' END)AS CNCL_ATON	");		
		sql.append("\n 				FROM BCDBA.TBGAPLCMGMT T1	left join BCDBA.TBGEVNTMGMT T2 on T1.LESN_SEQ_NO = T2.EVNT_SEQ_NO	");
		sql.append("\n 					left join BCDBA.TBGSTTLMGMT T3 on T1.APLC_SEQ_NO = T3.STTL_GDS_SEQ_NO						");
		sql.append("\n 				WHERE T2.BLTN_YN = 'Y'							");
		sql.append("\n 					AND T1.GOLF_SVC_APLC_CLSS = ?				");
		sql.append("\n 					AND T1.CDHD_ID = ?							");
		sql.append("\n 				ORDER BY T1.LESN_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");
		

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectTtCountQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT count(*)as CNT												");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT T1 left join BCDBA.TBGEVNTMGMT T2 on T1.LESN_SEQ_NO = T2.EVNT_SEQ_NO	");
		sql.append("\n WHERE T2.BLTN_YN = 'Y'												");
		sql.append("\n 		AND T1.GOLF_SVC_APLC_CLSS = ?									");
		sql.append("\n 		AND T1.CDHD_ID = ?												");
		
		return sql.toString();
	}

	/** ***********************************************************************
	* 총 게시물 수를 리턴
	************************************************************************ */
	public String getTtCount(WaContext context, TaoDataSet data) throws Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection con = null;
		String result = "0";
		
		
		try{
			String userId = data.getString("userId");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			
			
			String sql = this.getSelectTtCountQuery();
			con = context.getDbConnection("default", null);
			int pidx = 0;
			pstmt = con.prepareStatement(sql);
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			pstmt.setString(++pidx, userId);
			rset = pstmt.executeQuery();
			
			if(rset.next()){
				result = rset.getString("CNT");
			}else{
				result = "0";
			}
			
		}catch(Exception ex){
			
			
			//debug(">>>>>>>>>>>>>> ERROR getTtCount : "+ex.toString());
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		
		return result;
	}
}
