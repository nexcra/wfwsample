/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthlnqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 유저 목록 조치 
*   적용범위  : Golf
*   작성일자  : 2009-05-06  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*	20090327		임은혜	and user_class='P' 쿼리 추가
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.common.GolfUtil;

/** ****************************************************************************
 * Media4th 
 * @author
 * @version 2009-05-06
 **************************************************************************** */
public class GolfAdmAuthlnqDaoProc extends AbstractProc  {
	 
	public static final String TITLE = "관리자 목록 조회";
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		//debug("==== GolfAdmAuthlnqDaoProc start ===");
		
		try{
			//조회 조건
			String search_yn	= dataSet.getString("search_yn"); 		//검색여부
			String search_clss	= "";									//검색어구분
			String search_word	= "";									//검색어
			String sdate		= "";
			String edate		= "";
			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// 검색어
				search_word	= dataSet.getString("search_word"); 		// 제목검색여부
				sdate	= dataSet.getString("sdate");
				sdate	= GolfUtil.replace(sdate,"-","");
				edate	= dataSet.getString("edate");
				edate	= GolfUtil.replace(edate,"-","");
			}
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size = dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
			

			String curDateFormated = "";

			String sql = this.getSelectQuery(search_yn,search_clss,sdate,edate);
		
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			
			if("Y".equals(search_yn)){
				pstmt.setString(++pidx, "%"+search_word+"%");
			}
			if(!"".equals(sdate)){
				pstmt.setString(++pidx, sdate);
			}
			if(!"".equals(edate)){
				pstmt.setString(++pidx, edate);
			}
			pstmt.setLong(++pidx, page_no);

			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");
				
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addString("ACCOUNT",				rset.getString("ID"));
				result.addString("NAME",				rset.getString("HG_NM"));
				result.addString("COM_NM",				rset.getString("COM_NM"));
				result.addString("EMAIL",				rset.getString("EMAIL_ID"));
				result.addString("TEL1",				rset.getString("CHG_DDD_NO"));
				result.addString("TEL2",				rset.getString("CHG_TEL_HNO"));
				result.addString("TEL3",				rset.getString("CHG_TEL_SNO"));
				result.addString("FX_DDD_NO",			rset.getString("FX_DDD_NO"));
				result.addString("FX_TEL_HNO",			rset.getString("FX_TEL_HNO"));
				result.addString("FX_TEL_SNO",			rset.getString("FX_TEL_SNO"));
				result.addString("HP_DDD_NO",			rset.getString("HP_DDD_NO"));
				result.addString("HP_TEL_HNO",			rset.getString("HP_TEL_HNO"));
				result.addString("HP_TEL_SNO",			rset.getString("HP_TEL_SNO"));
				result.addString("VISIT_DATE",			rset.getString("RC_CONN_DATE"));
				result.addString("VISIT_TIME",			rset.getString("RC_CONN_TIME"));
				result.addString("REG_DATE",			rset.getString("REG_DATE"));				
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmAuthlnqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthlnqDaoProc ERROR ===");
			
			//debug("==== GolfAdmAuthlnqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String search_yn,String search_clss,String sdate, String edate) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*									");
		sql.append("\n FROM	(SELECT	RNUM,							");
		sql.append("\n 					ID,					        ");
		sql.append("\n 					HG_NM,						");
		sql.append("\n 					COM_NM,						");
		sql.append("\n 					EMAIL_ID,					");
		sql.append("\n 					CHG_DDD_NO,					");
		sql.append("\n 					CHG_TEL_HNO,				");
		sql.append("\n 					CHG_TEL_SNO,				");
		sql.append("\n 					FX_DDD_NO,					");
		sql.append("\n 					FX_TEL_HNO,					");
		sql.append("\n 					FX_TEL_SNO,					");
		sql.append("\n 					HP_DDD_NO,					");
		sql.append("\n 					HP_TEL_HNO,					");
		sql.append("\n 					HP_TEL_SNO,					");
		sql.append("\n 					RC_CONN_DATE,				");
		sql.append("\n 					RC_CONN_TIME,				");
		sql.append("\n 					REG_DATE,					");
		sql.append("\n 					CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 					MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,			");
		sql.append("\n 							MGR_ID AS ID,		");
		sql.append("\n 							HG_NM,				");
		sql.append("\n 							FIRM_NM AS COM_NM,	");
		sql.append("\n 							EMAIL AS EMAIL_ID,	");
		sql.append("\n 							DDD_NO AS CHG_DDD_NO,");
		sql.append("\n 							TEL_HNO AS CHG_TEL_HNO,");
		sql.append("\n 							TEL_SNO AS CHG_TEL_SNO,");
		sql.append("\n 							FAX_DDD_NO AS FX_DDD_NO,			");
		sql.append("\n 							FAX_TEL_HNO AS FX_TEL_HNO,			");
		sql.append("\n 							FAX_TEL_SNO AS FX_TEL_SNO,			");
		sql.append("\n 							HP_DDD_NO,			");
		sql.append("\n 							HP_TEL_HNO,			");
		sql.append("\n 							HP_TEL_SNO,			");
		sql.append("\n 							RC_CONN_DATE,		");
		sql.append("\n 							RC_CONN_TIME,		");
		sql.append("\n 							REG_DATE			");
		sql.append("\n 				FROM BCDBA.TBGMGRINFO			");
		sql.append("\n 				WHERE 1=1");
		if("Y".equals(search_yn)){
			if("T".equals(search_clss)){
				sql.append("\n 				AND MGR_ID  like ?			");
			}else if("C".equals(search_clss)){
				sql.append("\n 				AND HG_NM  like ?		");
			}else{
				
			}
		}
		if(!"".equals(sdate) && !"".equals(edate)){
			sql.append("\n 				AND ( REG_DATE >=  ?	and REG_DATE <=  ?	)	");
			
		}
		sql.append("\n 				ORDER BY REG_DATE DESC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY REG_DATE DESC					");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
