/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmSysWidgetViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 위젯 > 보기처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.system.widget;

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
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfadmSysWidgetViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSysWidgetViewDaoProc() {}	

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
			// 이미지 경로지정
			String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");	
			mapDir = mapDir.replaceAll("\\.\\.","");
			String imgPath 				= AppConfig.getAppProperty("WIDGET");	
			imgPath = imgPath.replaceAll("\\.\\.","");
			String realPath				= mapDir + imgPath + "/";
						 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("BBRD_SEQ_NO"));
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("EPS_YN" 					,rs.getString("EPS_YN") );
					result.addString("EPS_YN_TXT" 				,rs.getString("EPS_YN_TXT") );
					result.addString("ANNX_FILE_NM" 			,rs.getString("ANNX_FILE_NM") );
					result.addString("MVPT_ANNX_FILE_PATH" 		,rs.getString("MVPT_ANNX_FILE_PATH") );
					result.addString("SRC_ANNX_FILE_NM" 		,realPath + rs.getString("ANNX_FILE_NM") );
					
					result.addString("RESULT", "00"); //정상결과
				}
			}else{
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
        
		sql.append("\n");
		sql.append("\t  SELECT																\n");
		sql.append("\t  CASE EPS_YN WHEN 'Y' THEN '[진행중]' ELSE '[종료]' END AS EPS_YN_TXT		\n");
		sql.append("\t  , EPS_YN, ANNX_FILE_NM, MVPT_ANNX_FILE_PATH									\n");
		sql.append("\t  FROM BCDBA.TBGBBRD													\n");
		sql.append("\t  WHERE BBRD_SEQ_NO=?													\n");
		
		return sql.toString();
    }
}
