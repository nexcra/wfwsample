/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBcListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ BC Golf �̺�Ʈ ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

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

/******************************************************************************
 * Golf
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmEvntBcListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBcListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBcListDaoProc() {}	

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
			 
			//��ȸ ----------------------------------------------------------
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");

			String sql = this.getSelectQuery(search_sel, search_word);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));

			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("EVNT_SEQ_NO") );
					result.addString("EVNT_CLSS" 		,rs.getString("EVNT_CLSS") );
					result.addString("EVNT_NM" 			,rs.getString("EVNT_NM") );
					result.addString("EVNT_FROM" 		,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_TO" 			,rs.getString("EVNT_END_DATE") );
					result.addString("TITL" 			,rs.getString("TITL") );
					result.addString("DISP_YN" 			,rs.getString("BLTN_YN") );	
					result.addString("ING_YN" 			,rs.getString("ING_YN") );	
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("IMG_NM"			,rs.getString("IMG_FILE_PATH") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );

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
	

	/** **********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM, EVNT_STRT_DATE, EVNT_END_DATE, TITL, CTNT, BLTN_YN, IMG_FILE_PATH, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, ING_YN, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM, TO_CHAR(TO_DATE(EVNT_STRT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_STRT_DATE, TO_CHAR(TO_DATE(EVNT_END_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_END_DATE, 	");
		sql.append("\n 				TITL, CTNT, DECODE (BLTN_YN, 'Y','�Խ�','N','�̰Խ�') BLTN_YN, IMG_FILE_PATH, REG_ATON, 	");
		sql.append("\n 				CASE WHEN TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN EVNT_STRT_DATE AND EVNT_END_DATE THEN '�������� �̺�Ʈ' ELSE '����� �̺�Ʈ' END ING_YN 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGEVNTMGMT	");
		sql.append("\n 				WHERE EVNT_CLSS = '0001'	");
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 			AND (EVNT_NM LIKE ?	");
				sql.append("\n 			OR CTNT LIKE ?)	");				
			} else {
				sql.append("\n 			AND "+search_sel+" LIKE ?	");
			}
		}
		sql.append("\n 				ORDER BY REG_ATON DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
