/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfadmGrListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrListDaoProc ���μ��� ������  
	 * @param N/A
	 ***************************************************************** */
	public GolfadmGrListDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		GolfAdminEtt userEtt = null;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();
			
			// �̹��� �������
			String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");	
			mapDir = mapDir.replaceAll("\\.\\.","");
			String imgPath 				= AppConfig.getAppProperty("BK_GREEN");
			imgPath = imgPath.replaceAll("\\.\\.","");
			String realPath				= mapDir + imgPath + "/";
			 
			//��ȸ ----------------------------------------------------------
			String sort				= data.getString("SORT");
			String sql = this.getSelectQuery(sort, admId, admClss);   

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, sort);
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();

			int art_NUM_NO = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("SEQ_NO" 				,rs.getInt("SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("REG_DATE"			,rs.getString("REG_DATE") );
					result.addString("BANNER" 			,rs.getString("BANNER") );
					result.addString("SRC_BANNER" 		,realPath + rs.getString("BANNER") );

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_NUM_NO );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_NUM_NO++;
					
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
    private String getSelectQuery(String sort, String admId, String admClss){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			SEQ_NO, GR_NM, BANNER, REG_DATE, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");	
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				AFFI_GREEN_SEQ_NO AS SEQ_NO, GREEN_NM AS GR_NM, BANNER_IMG AS BANNER 	");
		sql.append("\n 				, TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8)),'YY.MM.DD') AS REG_DATE 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN	");
		sql.append("\n 				WHERE AFFI_GREEN_SEQ_NO = AFFI_GREEN_SEQ_NO	");
		sql.append("\n 				AND AFFI_FIRM_CLSS = ?	");
		if(!"1000".equals(sort))
		{
		if(!admClss.equals("master")){		sql.append("\n		AND GREEN_ID='"+admId+"'  	");			}
		}
		sql.append("\n 				ORDER BY AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		


		return sql.toString();
    }
}
