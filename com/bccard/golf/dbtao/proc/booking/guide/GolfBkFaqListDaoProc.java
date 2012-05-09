/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkFaqListDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 부킹 > 부킹 가이드 > FAQ 리스트 처리 
*   적용범위  : Golf
*   작성일자  : 2009-05-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.guide;

import java.io.Reader;
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

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16
 **************************************************************************** */
public class GolfBkFaqListDaoProc extends AbstractProc {
	
	public static final String TITLE = "FAQ 리스트 처리";
	
	
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
		
		//debug("==== GolfAdmBoardComListDaoProc start ===");
		
		
		try{
			//01. 조회 조건
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");	
			String boardid = "0020";

			String sch_TEXT		= dataSet.getString("SCH_TEXT");
			String sch_FAQ_CLSS	= dataSet.getString("SCH_FAQ_CLSS");
			String sch_ORDER	= dataSet.getString("SCH_ORDER");
			String sch_SORT		= dataSet.getString("SCH_SORT");
			String sch_TEXT2	= dataSet.getString("SCH_TEXT2");

			// 02. 쿼리가져오기
			String sql = this.getSelectQuery(sch_TEXT, sch_FAQ_CLSS, sch_ORDER, sch_SORT, sch_TEXT2);			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, boardid);
			pstmt.setLong(++pidx, page_no);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			int art_NUM_NO = 0;
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
								
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addString("SEQ_NO",				rset.getString("SEQ_NO"));
				result.addString("EPS_YN",				rset.getString("EPS_YN"));
				result.addString("TITL",				rset.getString("TITL"));
				result.addString("REG_ATON",			rset.getString("REG_ATON"));
				result.addLong("INOR_NUM",				rset.getLong("INOR_NUM"));
				result.addString("GOLF_BOKG_FAQ_CLSS",	rset.getString("GOLF_BOKG_FAQ_CLSS"));

				result.addInt("ART_NUM" 				,rset.getInt("ART_NUM")-art_NUM_NO );
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				
				Reader reader = null;
				StringBuffer bufferSt = new StringBuffer();
				reader = rset.getCharacterStream("CTNT");
				if( reader != null )  {
					char[] buffer = new char[1024]; 
					int byteRead; 
					while((byteRead=reader.read(buffer,0,1024))!=-1) 
						bufferSt.append(buffer,0,byteRead); 
					reader.close();
				}
				result.addString("CTNT", bufferSt.toString());
				
				
				art_NUM_NO++;
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
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
	private String getSelectQuery(String sch_TEXT, String sch_FAQ_CLSS, String sch_ORDER, String sch_SORT, String sch_TEXT2) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*																						");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,					");
		sql.append("\n 				SEQ_NO,							");
		sql.append("\n 				EPS_YN,							");
		sql.append("\n 				TITL,							");
		sql.append("\n 				REG_ATON,						");
		sql.append("\n 				INOR_NUM, GOLF_BOKG_FAQ_CLSS, CTNT, 						");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,		");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT, 		");	
		sql.append("\n 				((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,			");
		sql.append("\n 						T1.BBRD_SEQ_NO AS SEQ_NO,					");
		sql.append("\n 						(CASE T1.EPS_YN WHEN 'Y' THEN '노출' ELSE '비노출' END) AS EPS_YN,					");
		sql.append("\n 						T1.TITL,					");
		sql.append("\n 						TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON,				");
		sql.append("\n 						T1.INQR_NUM AS INOR_NUM,				");
		sql.append("\n 						T1.CTNT,					");

		sql.append("\n 						T3.GOLF_CMMN_CODE_NM AS GOLF_BOKG_FAQ_CLSS	");		
		
		sql.append("\n 				FROM BCDBA.TBGBBRD T1			");
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID			");
		sql.append("\n 				LEFT JOIN BCDBA.TBGCMMNCODE T3 ON T1.GOLF_BOKG_FAQ_CLSS=T3.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0018'			");
		sql.append("\n 				WHERE T1.BBRD_CLSS=?		");
		
		if(!sch_TEXT.equals("")){
			sql.append("\n 	AND ( T1.TITL like '%"+ sch_TEXT + "%' or T1.CTNT like '%"+ sch_TEXT +"%')" );
		}
		if(!sch_FAQ_CLSS.equals("")){
			sql.append("\n 	AND T1.GOLF_BOKG_FAQ_CLSS='"+sch_FAQ_CLSS+"'" );
		}
		if(!sch_TEXT2.equals("")){
			sql.append("\n 	AND "+ sch_SORT +"  like '%"+ sch_TEXT2 +"%'");
		}
		if(sch_ORDER.equals("")){
			sql.append("\n 				ORDER BY BBRD_SEQ_NO DESC			");
		}else{
			sql.append("\n 				ORDER BY "+sch_ORDER+" DESC			");
		}
		
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
