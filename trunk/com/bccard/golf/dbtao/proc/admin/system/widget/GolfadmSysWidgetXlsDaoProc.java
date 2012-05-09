/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmSysWidgetXlsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ���� > ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.system.widget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfadmSysWidgetXlsDaoProc extends AbstractProc {
	
	public GolfadmSysWidgetXlsDaoProc() {}	

	/**
	 * Proc ����.
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
			conn = context.getDbConnection("default", null);
			// �̹��� �������
			String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");	
			mapDir = mapDir.replaceAll("\\.\\.","");
			String imgPath 				= AppConfig.getAppProperty("WIDGET");
			imgPath = imgPath.replaceAll("\\.\\.","");
			String realPath				= mapDir + imgPath + "/";
			 
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery();   

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("BBRD_SEQ_NO" 				,rs.getInt("BBRD_SEQ_NO") );
					result.addString("EPS_YN" 					,rs.getString("EPS_YN") );
					result.addString("MVPT_ANNX_FILE_PATH" 		,rs.getString("MVPT_ANNX_FILE_PATH") );
					result.addString("ANNX_FILE_NM" 			,rs.getString("ANNX_FILE_NM") );
					result.addString("SRC_ANNX_FILE_NM" 		,realPath + rs.getString("ANNX_FILE_NM") );
					result.addString("REG_ATON" 				,rs.getString("REG_ATON") );

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
					art_num_no++;
					
					result.addString("RESULT", "00"); //������
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t SELECT	*															\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM,											\n");
		sql.append("\t 			BBRD_SEQ_NO, EPS_YN, ANNX_FILE_NM, MVPT_ANNX_FILE_PATH, 	\n");
		sql.append("\t 			REG_ATON, 													\n");
		sql.append("\t 			CEIL(ROWNUM/?) AS PAGE,										\n");
		sql.append("\t 			MAX(RNUM) OVER() TOT_CNT,									\n");
		sql.append("\t 			((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  					\n");	
		sql.append("\t 			FROM (SELECT ROWNUM RNUM,									\n");
		
		sql.append("\t 				BBRD_SEQ_NO, ANNX_FILE_NM, MVPT_ANNX_FILE_PATH	\n");
		sql.append("\t 				, CASE EPS_YN WHEN 'Y' THEN '[������]' ELSE '[����]' END AS EPS_YN	\n");
		sql.append("\t 				, TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8)),'YY.MM.DD') AS REG_ATON	\n");
		sql.append("\t 				FROM BCDBA.TBGBBRD										\n");
		sql.append("\t 				WHERE BBRD_CLSS='0021' 									\n");
		sql.append("\t 				AND EPS_YN='Y' AND ROWNUM<4								\n");
		sql.append("\t				ORDER BY REG_ATON DESC									\n");
		
		sql.append("\t 			)															\n");
		sql.append("\t 	ORDER BY RNUM														\n");
		sql.append("\t 	)																	\n");
		sql.append("\t WHERE PAGE = ?														\n");		

		return sql.toString();
    }
}
