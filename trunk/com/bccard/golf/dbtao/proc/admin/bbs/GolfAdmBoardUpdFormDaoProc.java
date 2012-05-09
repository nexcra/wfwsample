/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLsnVodChgFormDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ���������� ���� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmBoardUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmBoardUpdFormDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBoardUpdFormDaoProc() {}	

	/**
	 * Proc ����.
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
			 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
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
        
		sql.append("\n SELECT");
		sql.append("\n 	BBRD_SEQ_NO, BBRD_CLSS, GOLF_CLM_CLSS, GOLF_BOKG_FAQ_CLSS, GOLF_VBL_RULE_PREM_CLSS, TITL, CTNT, ID, HG_NM, EMAIL, INQR_NUM, EPS_YN, ANNX_FILE_NM, MVPT_ANNX_FILE_PATH, PROC_YN, DEL_YN, REG_MGR_ID, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, BEST_YN, ANW_BLTN_ARTC_YN, REPY_URNK_SEQ_NO 	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGBBRD");
		sql.append("\n WHERE BBRD_SEQ_NO = ?	");
		sql.append("\n AND BBRD_CLSS = ?	");

		return sql.toString();
    }
}
