/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkNotiViewDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ��ŷ > ��ŷ ���̵� > �������� ���� ó�� 
*   �������  : Golf
*   �ۼ�����  : 2009-03-31
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.guide;

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
public class GolfBkNotiViewDaoProc extends AbstractProc {
	
	public static final String TITLE = "�������� ���� ó��";
	
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
				result.addString("REG_ATON",			rset.getString("REG_ATON"));
				result.addString("INQR_NUM",			rset.getString("INQR_NUM"));
				

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
			

			// 02. ������
			String sql2 = this.getSelectPre(idx);
			PreparedStatement pstmt2 = con.prepareStatement(sql2);
			pstmt2.setString(1, idx);
			ResultSet rset2 = pstmt2.executeQuery();
			while(rset2.next()){							
				result.addString("PRE_IDX",			rset2.getString("IDX"));
				result.addString("PRE_TITL",		rset2.getString("TITL"));
			}
			
			// 03. ������
			String sql3 = this.getSelectNext(idx);
			PreparedStatement pstmt3 = con.prepareStatement(sql3);
			pstmt3.setString(1, idx);
			ResultSet rset3 = pstmt3.executeQuery();
			while(rset3.next()){							
				result.addString("NEXT_IDX",			rset3.getString("IDX"));
				result.addString("NEXT_TITL",		rset3.getString("TITL"));
			}
			
			// 04. ��ȸ�� �ø���
			String sql4 = this.getSelectHit(idx);
			PreparedStatement pstmt4 = con.prepareStatement(sql4);
			pstmt4.setString(1, idx);
			ResultSet rset4 = pstmt4.executeQuery();

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
		sql.append("\n 						T1.ANNX_FILE_NM AS FILE_NM,					");
		sql.append("\n 						TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY.MM.DD') AS REG_ATON,					");
		sql.append("\n 						INQR_NUM					");
		sql.append("\n 				FROM BCDBA.TBGBBRD T1		");	
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID		");
		sql.append("\n 				WHERE BBRD_SEQ_NO=?					");


		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. - ������
	************************************************************************ */
	private String getSelectPre(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n ");
		sql.append("\t SELECT IDX													\n");
		sql.append("\t , (SELECT TITL FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO=IDX) TITL		\n");
		sql.append("\t FROM (														\n");
		sql.append("\t     SELECT MAX(BBRD_SEQ_NO) IDX								\n");
		sql.append("\t     FROM BCDBA.TBGBBRD										\n");
		sql.append("\t     WHERE BBRD_CLSS='0001' AND EPS_YN='Y' AND  BBRD_SEQ_NO<?	\n");
		sql.append("\t 																\n");
		sql.append("\t	)															\n");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. - ������
	************************************************************************ */
	private String getSelectNext(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n ");
		sql.append("\t SELECT IDX													\n");
		sql.append("\t , (SELECT TITL FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO=IDX) TITL 		\n");
		sql.append("\t FROM (														\n");
		sql.append("\t     SELECT MIN(BBRD_SEQ_NO) IDX								\n");
		sql.append("\t     FROM BCDBA.TBGBBRD										\n");
		sql.append("\t     WHERE BBRD_CLSS='0001' AND EPS_YN='Y' AND  BBRD_SEQ_NO>?	\n");
		sql.append("\t 																\n");
		sql.append("\t	)															\n");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. - ��ȸ�� �ø���
	************************************************************************ */
	private String getSelectHit(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n ");
		sql.append("\t UPDATE BCDBA.TBGBBRD				\n");
		sql.append("\t SET INQR_NUM=INQR_NUM+1 			\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?				\n");

		return sql.toString();
	}
}
