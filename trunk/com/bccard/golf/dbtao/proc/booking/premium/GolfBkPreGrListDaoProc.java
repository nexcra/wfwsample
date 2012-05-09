/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.io.Reader;
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
public class GolfBkPreGrListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreGrListDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPreGrListDaoProc() {}	

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
			String imgPath 				= AppConfig.getAppProperty("BK_GREEN");	
			imgPath = imgPath.replaceAll("\\.\\.","");
			String realPath				= mapDir + imgPath + "/";
			String gr_INFO				= "";
			 
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery();   

			// �Է°� (INPUT)    
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("SEQ_NO" 				,rs.getInt("SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BANNER" 			,rs.getString("BANNER") );
					result.addString("SRC_BANNER" 		,realPath + rs.getString("BANNER") );
					
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("GR_INFO");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					gr_INFO = bufferSt.toString();
					gr_INFO = GolfUtil.left(gr_INFO, 170);
					result.addString("GR_INFO", gr_INFO);
					
					result.addString("RESULT", "00"); //������
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			SEQ_NO, GR_NM, BANNER, GR_INFO 	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				AFFI_GREEN_SEQ_NO AS SEQ_NO, GREEN_NM AS GR_NM, BANNER_IMG AS BANNER, GREEN_EXPL AS GR_INFO 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN	");
		sql.append("\n 				WHERE AFFI_FIRM_CLSS = '0001' AND MAIN_EPS_YN='Y' 	");
		sql.append("\n 				ORDER BY AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");

		return sql.toString();
    }
}
