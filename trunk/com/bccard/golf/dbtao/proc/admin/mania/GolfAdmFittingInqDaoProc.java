/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 상세
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.mania;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmFittingInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmFittingInqDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));

			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("ID" 				,rs.getString("CDHD_ID") );
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );
					result.addString("EMAIL_ID" 		,rs.getString("EMAIL") );
					result.addString("RSVT_YN" 			,rs.getString("RSVT_YN") );
					result.addString("NOTE_MTTR_EXPL" 			,rs.getString("NOTE_MTTR_EXPL") );
					String rou_date = rs.getString("ROUND_HOPE_DATE");
					if (!GolfUtil.isNull(rou_date)) rou_date = DateUtil.format(rou_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("ROU_DATE"			,rou_date);

					//result.addString("ROU_TIME_TP" 		,rs.getString("ROU_TIME_TP") );
					result.addString("ROU_TIME" 		,rs.getString("ROUND_HOPE_TIME") );
					result.addString("GF_CLUB_CD" 		,rs.getString("FIT_HOPE_CLUB_CLSS") );
					result.addString("TITL" 			,rs.getString("TITL") );
					//result.addString("HAN_NM" 			,rs.getString("HAN_NM") );
	
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
					result.addString("CTNT", bufferSt.toString());
					
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
    ************************************************* *********************** */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	CDHD_ID, HP_DDD_NO, HP_TEL_HNO,  ");
		sql.append("\n 	HP_TEL_SNO, EMAIL, RSVT_YN, NOTE_MTTR_EXPL, ROUND_HOPE_DATE,  ");
		sql.append("\n 	TO_CHAR(TO_DATE(ROUND_HOPE_TIME, 'HH24MI'), 'HH:MI') ROUND_HOPE_TIME, FIT_HOPE_CLUB_CLSS, TITL, CTNT  ");
		//주소 조인
		//sql.append("\n 	(SELECT CDHD_ID FROM BCDBA.TBMGRINFO WHERE MGR_SEQ_NO=TGL.REG_MGR_ID ) AS REG_MGR_ID, ");
		//sql.append("\n 	(SELECT CDHD_ID FROM BCDBA.TBMGRINFO WHERE MGR_SEQ_NO=TGL.CHNG_MGR_ID ) AS CHNG_MGR_ID, ");
		//sql.append("\n  (SELECT HAN_NM FROM BCDBA.TBGFUSER  WHERE GF_ID=TGL.CDHD_ID) AS HAN_NM   ");//USER테이블에서 CDHD_ID별 한글이름 조인
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGRSVTMGMT TGL");
		sql.append("\n WHERE TGL.GOLF_SVC_RSVT_NO = ?	");		

		return sql.toString();

    }
}