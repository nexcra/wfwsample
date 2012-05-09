/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBusPeopleUpdDaoProc
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 이벤트->골프장버스운행이벤트->신청 수정 처리
*   적용범위  : Golf
*   작성일자  : 2009-09-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  Golf
 * @author	Media4th
 * @version 1.0
 **************************************************************************** */
public class GolfAdmBusPeopleUpdDaoProc extends DbTaoProc {

	/**
	 * Proc 실행. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt				= null;
		ResultSet rs 						= null;
		String title						= dataSet.getString("title");
		String actnKey 						= null;
		DbTaoResult result					= new DbTaoResult(title);
		
		try {
		
			actnKey 						= dataSet.getString("actnKey");
			int res 						= 0;
			int pidx 						= 0;
			//조회 조건			
			String mode						= dataSet.getString("mode"); 			// 처리구분
			String teof_date				= dataSet.getString("teof_date").replaceAll("-", "");		
			teof_date						= teof_date.replaceAll("\\.", "");	
			//등록시
			if("upd".equals(mode)) 
			{
				
							
				pstmt = con.prepareStatement(getUpdateQuery(dataSet));
				pidx = 0;								
				pstmt.setString(++pidx, dataSet.getString("golf_cmmn_codes"));				
				pstmt.setString(++pidx, teof_date);
				pstmt.setString(++pidx, dataSet.getString("dtl_addr"));		
				pstmt.setString(++pidx, dataSet.getString("p_idx"));		
				
				res = pstmt.executeUpdate();							
				
			}
			else if("del".equals(mode))
			{
				
				pstmt = con.prepareStatement(getDeleteQuery());
				pidx = 0;					
				pstmt.setString(++pidx, dataSet.getString("p_idx"));
				res = pstmt.executeUpdate();
				
			}
			
			
			if(res>0) {
				result.addString("RESULT","00");
				con.commit();
			}			
			else {
				result.addString("RESULT","01");
				con.rollback();
			}
			
		} catch(Exception e){
			// 트랜젝션 상태일때는 롤백			
			try { if( !con.getAutoCommit() ){ con.rollback(); } else {} } catch(Throwable ignore) {}			
			
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		return result;
			
		
	}	
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getUpdateQuery(TaoDataSet data) throws Exception{

		StringBuffer sql = new StringBuffer();
		String golf_cmmn_codes = data.getString("golf_cmmn_codes");
		
		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT SET								");
		sql.append("\n		PGRS_YN = ? 	,								");	
		sql.append("\n		TEOF_DATE = ? 	,								");
		
		if(golf_cmmn_codes.equals("B")){
			sql.append("\n            NUM_DDUC_YN = 'Y',                    ");
		}
		if(golf_cmmn_codes.equals("C")){
			sql.append("\n            NUM_DDUC_YN = 'N',                    ");
		}
		
		
		sql.append("\n		DTL_ADDR = ? 									");					
		sql.append("\n	WHERE APLC_SEQ_NO = ?  								");	
		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getDeleteQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGAPLCMGMT	 							");
		sql.append("\n	WHERE APLC_SEQ_NO = ? 				");	
		return sql.toString();
	}	
	
}
