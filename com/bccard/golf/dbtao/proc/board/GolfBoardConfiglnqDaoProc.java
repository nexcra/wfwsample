/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardConfiglnqDaoProc
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : �Խ��� ȯ�� ��������
*   �������  : Golf
*   �ۼ�����  : 2009-04-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-04-01
 **************************************************************************** */
public class GolfBoardConfiglnqDaoProc extends AbstractProc {

	
	public static final String TITLE = "�Խ��� ���� ��ȸ";
	
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
		
		//debug("==== GolfBoardConfiglnqDaoProc Start :"+TITLE+" ===");
		
		try{
			//��ȸ ����
			String boardid			= dataSet.getString("boardid"); 		//�Խ��ǹ�ȣ					

			String sql = this.getSelectQuery(boardid);		
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			
			pstmt.setString(++pidx, boardid);			

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
			
				result.addString("BOARD_CODE",				rset.getString("BOARD_CODE"));
				result.addString("BOARD_NM",				rset.getString("BOARD_NM"));
				result.addString("USE_YN",					rset.getString("USE_YN"));
				result.addString("boardid",					boardid);

				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfBoardConfiglnqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfBoardConfiglnqDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfBoardConfiglnqDaoProc ERROR ===");
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;	
	}	
	
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery(String boardid) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT		BOARD_CODE,								");
		sql.append("\n 					BOARD_NM,							");
		sql.append("\n 					USE_YN								");
		sql.append("\n 	FROM TPBOARDINFO	 TBI							");
		sql.append("\n 	WHERE BOARDID = ? 									");
	

		return sql.toString();
	}
	
	
	
	
	
	
}
