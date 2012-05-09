/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨동영상 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.bbs;

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
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmBoardInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmBoardInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBoardInqDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			pstmt.setString(++idx, data.getString("BBS"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addLong("SEQ_NO" 				,rs.getLong("BBRD_SEQ_NO") );
					result.addString("BBRD_UNIQ_SEQ_NO" 	,rs.getString("BBRD_CLSS") );
					result.addString("FIELD_CD" 			,rs.getString("GOLF_CLM_CLSS") );
					result.addString("CLSS_CD" 				,rs.getString("GOLF_BOKG_FAQ_CLSS") );
					result.addString("SEC_CD" 				,rs.getString("GOLF_VBL_RULE_PREM_CLSS") );
					result.addString("TITL" 				,rs.getString("TITL") );
					result.addString("ID"					,rs.getString("ID") );
					result.addString("HG_NM"				,rs.getString("HG_NM") );
					result.addString("EMAIL_ID"				,rs.getString("EMAIL") );
					
					result.addString("DDD_NO"				,rs.getString("DDD_NO") );
					result.addString("TEL_HNO"				,rs.getString("TEL_HNO") );
					result.addString("TEL_SNO"				,rs.getString("TEL_SNO") );
					result.addLong("INOR_NUM"				,rs.getLong("INQR_NUM") );
					result.addString("EPS_YN"				,rs.getString("EPS_YN") );
					result.addString("FILE_NM"				,rs.getString("ANNX_FILE_NM") );
					result.addString("PIC_NM"				,rs.getString("MVPT_ANNX_FILE_PATH") );
					result.addString("HD_YN"				,rs.getString("PROC_YN") );
					result.addString("DEL_YN"				,rs.getString("DEL_YN") );
					result.addString("REG_MGR_SEQ_NO"		,rs.getString("REG_MGR_ID") );
					result.addString("REG_ATON"				,rs.getString("REG_ATON") );
					result.addString("BEST_YN"				,rs.getString("BEST_YN") );
					result.addString("NEW_YN"				,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addLong("REPY_URNK_SEQ_NO"		,rs.getLong("REPY_URNK_SEQ_NO") );
					result.addString("REPLY_YN"				,rs.getString("REPLY_YN") );
					result.addLong("RE_CNT"					,rs.getLong("RE_CNT") );
					
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
					
					reader = rs.getCharacterStream("RECTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("RECTNT", bufferSt.toString());
					
					
					
					
					
					
					
					
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	TBB.BBRD_SEQ_NO, TBB.BBRD_CLSS, TBB.GOLF_CLM_CLSS, TBB.GOLF_BOKG_FAQ_CLSS, TBB.GOLF_VBL_RULE_PREM_CLSS, TBB.TITL, TBB.CTNT, TBB.ID, NVL(TBB.HG_NM,'관리자') HG_NM, TBB.EMAIL, TBB.DDD_NO, TBB.TEL_HNO, TBB.TEL_SNO, TBB.INQR_NUM, TBB.EPS_YN, TBB.ANNX_FILE_NM, TBB.MVPT_ANNX_FILE_PATH, NVL(TBB.PROC_YN,'N') PROC_YN, 	");
		sql.append("\n 	TBB.DEL_YN, TBB.REG_MGR_ID, TO_CHAR(TO_DATE(TBB.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, TBB.BEST_YN, TBB.ANW_BLTN_ARTC_YN, TBB.REPY_URNK_SEQ_NO 	");
		sql.append("\n 	,DECODE(TBB.BBRD_SEQ_NO-TBB.REPY_URNK_SEQ_NO,0,'N','Y') REPLY_YN");
		sql.append("\n 	,(SELECT COUNT(REPY_SEQ_NO) FROM BCDBA.TBGBBRDREPY WHERE REPY_CLSS='0001' AND BBRD_SEQ_NO=TBB.BBRD_SEQ_NO) RE_CNT");
		
		sql.append("\n 	,(SELECT CTNT FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO!=TBB.REPY_URNK_SEQ_NO AND REPY_URNK_SEQ_NO=TBB.BBRD_SEQ_NO) RECTNT");
		
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGBBRD TBB");
		sql.append("\n WHERE TBB.BBRD_SEQ_NO = ?	");
		sql.append("\n AND TBB.BBRD_CLSS = ?	");

		return sql.toString();
    }
}
