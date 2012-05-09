/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvtAlpensiaNoticeIfmDaoProc
*   �ۼ���	: (��)�̵������ ������
*   ����		: �̺�Ʈ > ����þ� > ��������
*   �������	: Golf
*   �ۼ�����	: 2010-06-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.alpensia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16 
 **************************************************************************** */
public class GolfEvtAlpensiaNoticeIfmDaoProc extends AbstractProc {
	
	public static final String TITLE = "�Խ��� ���� ��� ��ȸ";
	
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ�������� 
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
				
		try{
			String sql = this.getSelectQuery();			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}				
				
				result.addString("BBRD_SEQ_NO",				rset.getString("BBRD_SEQ_NO"));
				result.addString("TITL",				rset.getString("TITL"));
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT * 	");
		sql.append("\n	FROM (	");
		sql.append("\n	    SELECT ROWNUM RNUM, BBRD_SEQ_NO, TITL, EPS_YN, REG_ATON	");
		sql.append("\n	    FROM (	");
		sql.append("\n	        SELECT BBRD_SEQ_NO, TITL, EPS_YN, REG_ATON	");
		sql.append("\n	        FROM BCDBA.TBGBBRD	");
		sql.append("\n	        WHERE BBRD_CLSS='0001' AND EPS_YN='Y' AND GOLF_CLM_CLSS='0001'	");
		sql.append("\n	        ORDER BY BBRD_SEQ_NO DESC	");
		sql.append("\n	    )	");
		sql.append("\n	    ORDER BY RNUM	");
		sql.append("\n	)	");
		sql.append("\n	WHERE RNUM<=10	");
		return sql.toString();
	}
}
