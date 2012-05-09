/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkCheckJjListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 제주 부킹 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.check;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스 
 * @version	1.0  
 ******************************************************************************/
public class GolfBkCheckJjListDaoProc extends AbstractProc {
	
	public GolfBkCheckJjListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			String memb_id = "";
			// 00. 세션 정보 체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memb_id = userEtt.getAccount();
			}
			
			conn = context.getDbConnection("default", null);
			// 01. 조회 ----------------------------------------------------------
			String listtype = data.getString("LISTTYPE");		
			String sql = this.getSelectQuery(listtype);   	
			
			// 02. 입력값 쿼리만들기         
			pstmt = conn.prepareStatement(sql.toString());	
			pstmt.setString(1, memb_id);					
			rs = pstmt.executeQuery();

			// 03. 리스트 가져오기
			if(rs != null) {			 

				while(rs.next())  {	

					result.addString("GOLF_SVC_RSVT_NO" 	,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("RSVT_YN" 				,rs.getString("RSVT_YN") );
					result.addString("HOPE_RGN_CODE" 		,rs.getString("HOPE_RGN_CODE") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("TEAM_NUM" 			,rs.getString("TEAM_NUM") );
					result.addString("TOT_PERS_NUM" 		,rs.getString("TOT_PERS_NUM") );
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
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
    private String getSelectQuery(String listtype){
        StringBuffer sql = new StringBuffer();      
        
		sql.append("\n SELECT	*																																	\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM																														\n");
		sql.append("\t 		, GOLF_SVC_RSVT_NO, RSVT_YN, HOPE_RGN_CODE, BK_DATE, TEAM_NUM, TOT_PERS_NUM	 					  										\n");	
		sql.append("\t 		FROM (SELECT ROWNUM RNUM																												\n");

		sql.append("\t 			, GOLF_SVC_RSVT_NO																													\n");	
		sql.append("\t 			, (CASE RSVT_YN WHEN 'Y' THEN '신청완료' WHEN 'N' THEN '취소완료' WHEN 'I' THEN '임박취소' WHEN 'D' THEN '불참' ELSE '' END) RSVT_YN	\n");	
		sql.append("\t 			, (CASE WHEN HOPE_RGN_CODE='0001' THEN '전체'																						\n");	
		sql.append("\t 		    	WHEN HOPE_RGN_CODE='0002' THEN '제주시'																							\n");	
		sql.append("\t 		    	WHEN HOPE_RGN_CODE='0003' THEN '서귀포시'																						\n");	
		sql.append("\t 		    	WHEN HOPE_RGN_CODE='0003' THEN '서귀포시'																						\n");	
		sql.append("\t 		    	WHEN HOPE_RGN_CODE='0005' THEN '서부' END) AS HOPE_RGN_CODE																		\n");	
		sql.append("\t 			, TO_CHAR(TO_DATE(ROUND_HOPE_DATE),'YY-MM-DD(DY) ')																					\n");	
		sql.append("\t 				||CASE WHEN ROUND_HOPE_TIME_CLSS='A' THEN '오전 ' ELSE '오후 ' END																\n");	
		sql.append("\t 				||CASE WHEN ROUND_HOPE_TIME='000000' THEN '전체' ELSE SUBSTR(ROUND_HOPE_TIME,1,2)||':00' END BK_DATE								\n");	
		sql.append("\t 			, TEAM_NUM																															\n");	
		sql.append("\t 			, TOT_PERS_NUM																				\n");	
		
		sql.append("\t 			FROM BCDBA.TBGRSVTMGMT																		\n");	
		sql.append("\t 			WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='J' AND CDHD_ID=? and NVL(NUM_DDUC_YN,'Y')='Y'				\n");

		
		if(listtype.equals("ALL")){
			sql.append("\t 			    AND ROUND_HOPE_DATE>=TO_CHAR(SYSDATE,'YYYYMMDD') AND RSVT_YN='Y'	\n");	
			sql.append("\t 			    ORDER BY ROUND_HOPE_DATE	\n");
		}else{
			sql.append("\t 			    ORDER BY REG_ATON DESC 																				\n");
		}
		
		
		//sql.append("\t 			ORDER BY REG_ATON DESC																		\n");
        		
		sql.append("\t 			)																		\n");
		sql.append("\t 	ORDER BY RNUM																	\n");
		sql.append("\t 	)																				\n");
		if(listtype.equals("ALL")){		sql.append("\t WHERE RNUM<=1	\n");				}

		return sql.toString();
    }
}
