/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemSelInqDaoProc
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 사은품관리 아이디 정보 체크
*   적용범위  : golf
*   작성일자  : 2009-08-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmMemSelInqDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 사은품관리 아이디 정보 체크";

	/** *****************************************************************
	 * GolfAdmMbInfoSelInqDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemSelInqDaoProc() {}
	
	/**
	 * 관리자 예약등록시 아이디 정보 체크
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		boolean flag = false;
		DbTaoResult result =  new DbTaoResult(title);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("cdhd_id"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					String cdhd_id = rs.getString("CDHD_ID"); //회원아이디
					String userclss = rs.getString("USERCLSS"); //회원등급		
					String cdhd_jonn_date = rs.getString("ACRG_CDHD_JONN_DATE"); //회원등급	
					
					if (userclss.indexOf("5") > -1 ){
						flag = true;				
					}

					if (flag){
						
						result.addString("USERCLSS", userclss ); 
						result.addString("ACRG_CDHD_JONN_DATE", cdhd_jonn_date ); 
						
						result.addString("RESULT", "00"); //정상결과
					}
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");	
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT																								");
		sql.append("\n 	TBG1.CDHD_ID, TBG2.CDHD_CTGO_SEQ_NO AS USERCLSS, TBG1.ACRG_CDHD_JONN_DATE 						");
		sql.append("\n 		FROM BCDBA.TBGGOLFCDHD TBG1 																	");
		sql.append("\n		JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO = 5					");
		sql.append("\n		WHERE to_char(to_date(TBG1.ACRG_CDHD_END_DATE,'yyyymmddhh'), 'yyyy-mm-dd') >= TO_CHAR(SYSDATE, 'YYYY-MM-DD')	");
		sql.append("\n		AND ( TBG1.SECE_YN = 'N' OR  TBG1.SECE_YN is null )					");
		sql.append("\n		AND TBG1.CDHD_ID = ?			");
		
		return sql.toString();
    }

}
