/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComUpdFormDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��ǰ��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-03-31
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

import java.io.Reader;
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
 * @version 2009-03-31 
 **************************************************************************** */
public class GolfAdmBoardComUpdFormDaoProc extends AbstractProc {
	
	public static final String TITLE = "�Խ��� ����  ��ȸ";
	
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
		
		//debug("==== GolfAdmBoardComUpdFormDaoProc start ===");
		
		try{
			//��ȸ ����

			String idx			= dataSet.getString("idx"); 
			String sql = this.getSelectQuery(idx);
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			pstmt.setString(++pidx, idx);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addString("TITL",				rset.getString("TITL"));
				result.addString("EPS_YN",				rset.getString("EPS_YN"));
				result.addString("REG_MGR_ID",			rset.getString("REG_MGR_ID"));
				result.addString("HG_NM",				rset.getString("HG_NM"));
				result.addString("FILE_NM",				rset.getString("FILE_NM"));
				result.addString("GOLF_BOKG_FAQ_CLSS",	rset.getString("GOLF_BOKG_FAQ_CLSS"));
				result.addString("GOLF_CLM_CLSS",		rset.getString("GOLF_CLM_CLSS"));
								

				Reader reader = null;
				StringBuffer bufferSt = new StringBuffer();
				reader = rset.getCharacterStream("CTNT");
				if( reader != null )  {
					char[] buffer = new char[1024]; 
					int byteRead; 
					while((byteRead=reader.read(buffer,0,1024))!=-1) 
						bufferSt.append(buffer,0,byteRead); 
					reader.close();
				}
				result.addString("CTNT", bufferSt.toString());
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmBoardComUpdFormDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComUpdFormDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardComUpdFormDaoProc ERROR ===");
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
	private String getSelectQuery(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n 		SELECT	T1.TITL,					");
		sql.append("\n 						T1.EPS_YN,					");
		sql.append("\n 						T1.REG_MGR_ID, 				");			
		sql.append("\n 						T2.HG_NM,				");		
		sql.append("\n 						T1.CTNT,					");
		sql.append("\n 						T1.GOLF_BOKG_FAQ_CLSS,					");
		sql.append("\n 						T1.ANNX_FILE_NM AS FILE_NM, T1.GOLF_CLM_CLSS	");
		sql.append("\n 				FROM BCDBA.TBGBBRD T1		");	
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID		");
		sql.append("\n 				WHERE BBRD_SEQ_NO=?					");


		return sql.toString();
	}
}
