/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmLsnUccDetailDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ >  ���� > UCC ���� ��� �� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson.ucc;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmLsnUccDetailDaoProc extends AbstractProc {
	public static final String TITLE = "������ >  ���� > UCC ���� ��� ��ȸ ó��";
	/** **************************************************************************
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql  = "";
		String ctnt = "";

		try {
			// ȸ���������̺� ���� �������� ����
			conn = context.getDbConnection("default", null);
			String bbrd_clss 	= data.getString("bbrd_clss");
			String idx 			= data.getString("idx");
			
			
			int pidx = 0;
			boolean eof = false;
			
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(++pidx,bbrd_clss);
			pstmt.setString(++pidx,idx);			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!eof) {
					result.addString("RESULT", "00");
				}
				
				//���� ���� CLOBó��
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
				ctnt = bufferSt.toString();
				
				result.addString("seq_no",				rs.getString("BBRD_SEQ_NO"));
				result.addString("titl",				rs.getString("TITL"));
				result.addString("ctnt",				ctnt);
				result.addString("eps_yn",				rs.getString("EPS_YN"));
				result.addString("annx_file_nm",		rs.getString("ANNX_FILE_NM"));
				result.addString("mvpt_annx_file_path",	rs.getString("MVPT_ANNX_FILE_PATH"));
				result.addString("answ_ctnt",			rs.getString("ANSW_CTNT"));
				result.addString("inqr_num",			rs.getString("INQR_NUM"));
				result.addString("hg_nm",				rs.getString("HG_NM") );
				result.addString("reg_aton",			rs.getString("REG_ATON") );
				result.addString("use_nm",				rs.getString("USE_NM") );
				eof = true;
			}
			
			if(!eof) {
				result.addString("RESULT", "01");
			}
			 
		} catch ( Exception e ) {			
			
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
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT																	");
		sql.append("\n 	BBRD_SEQ_NO																");
		sql.append("\n 	,TITL																	");
		sql.append("\n 	,CTNT																	");
		sql.append("\n 	,ANNX_FILE_NM															");
		sql.append("\n 	,MVPT_ANNX_FILE_PATH													");
		sql.append("\n 	,ANSW_CTNT																");
		sql.append("\n 	,HG_NM																	");		
		sql.append("\n 	,EPS_YN																	");
		sql.append("\n 	,INQR_NUM																");
		sql.append("\n 	,TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON					");
		sql.append("\n 	,(SELECT  HG_NM AS NAME FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T1.ID)as USE_NM	    ");
		sql.append("\n 	FROM	BCDBA.TBGBBRD T1												"); 
		sql.append("\n 	WHERE BBRD_CLSS = ?	AND BBRD_SEQ_NO = ?									");

		return sql.toString();
	}

	
}
