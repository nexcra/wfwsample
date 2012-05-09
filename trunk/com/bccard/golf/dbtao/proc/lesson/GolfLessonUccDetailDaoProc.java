/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLessonUccDetailDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ģ���� ucc ���� �� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.lesson;

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
 * Golf
 * @author	�̵������
 * @version	1.0 
 ******************************************************************************/
public class GolfLessonUccDetailDaoProc extends AbstractProc {
	public static final String TITLE = "ģ���� ucc ���� �� ó��";
	
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
		String ctnt = ""; 
		String sql = "";
		String reg = "";
		String chng = "";
		
		try{  
			// ȸ���������̺� ���� �������� ����
			//01. ��ȸ ����
			String bbrd_clss = dataSet.getString("bbrd_clss");
			String idx 		 = dataSet.getString("idx");

			// 02. ȯ�漳��
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			con = context.getDbConnection("default", null);
			
			// 03. ������������
			int pidx = 0;
			sql = this.getSelectQuery();	
			pstmt = con.prepareStatement(sql);
			pstmt.setString(++pidx, bbrd_clss);
			pstmt.setString(++pidx, idx);
			
			rset = pstmt.executeQuery();
			
			while(rset.next()){
				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//���� ���� CLOBó��
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
				ctnt = bufferSt.toString();
				
				//��¥ ����
				reg = rset.getString("REG_ATON");
				if(reg.indexOf("-") > 0) reg = reg.replaceAll("-", ".");
				chng = rset.getString("CHNG_ATON");
				if(chng != null && !"".equals(chng)){
					if(chng.indexOf("-") > 0){
						chng = chng.replaceAll("-", ".");
						chng = chng.substring(0,16);
					}
				}
				
				result.addString("seq_no", 				rset.getString("BBRD_SEQ_NO"));
				result.addString("titl", 				rset.getString("TITL"));
				result.addString("ctnt", 				ctnt);
				result.addString("hg_nm", 				rset.getString("HG_NM"));
				result.addString("inqr_num",			rset.getString("INQR_NUM"));
				result.addString("mvpt_annx_file_path",	rset.getString("MVPT_ANNX_FILE_PATH"));
				result.addString("annx_file_nm",		rset.getString("ANNX_FILE_NM"));
				result.addString("answ_yn", 			rset.getString("ANSW_YN"));
				result.addString("answ_ctnt", 			rset.getString("ANSW_CTNT"));
				result.addString("usename", 			rset.getString("USENAME"));
				result.addString("id", 					rset.getString("ID"));
				result.addString("reg_aton", 			reg);
				result.addString("chng_aton", 			chng);
				 
				//��ȸ�� +1
				pidx = 0;
				sql = this.getSelectHit();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, idx);
				
				int rs = pstmt.executeUpdate();
				
				if(rs > 0){
					existsData = true;
				}
			}
			

			if(!existsData){
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			
			
			
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
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT																		");
		sql.append("\n 		BBRD_SEQ_NO																");
		sql.append("\n 		,TITL																	");
		sql.append("\n 		,CTNT																	");
		sql.append("\n 		,ID																		");
		sql.append("\n 		,HG_NM																	");
		sql.append("\n 		,INQR_NUM																");
		sql.append("\n 		,ANNX_FILE_NM															");
		sql.append("\n 		,MVPT_ANNX_FILE_PATH													");
		sql.append("\n 		,(CASE WHEN ANSW_CTNT is not null THEN 'Y' ELSE 'N' END)as ANSW_YN		");
		sql.append("\n 		,ANSW_CTNT																");
		sql.append("\n 		,(SELECT  HG_NM FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T1.ID)as USENAME	");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.REG_ATON),'yyyy-MM-dd') AS REG_ATON					");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.CHNG_ATON,'yyyy-MM-dd hh24miss'),'yyyy-MM-dd hh:mm') AS CHNG_ATON		");
		sql.append("\n 	FROM BCDBA.TBGBBRD	T1														");
		sql.append("\n 	WHERE BBRD_CLSS = ?		AND 	BBRD_SEQ_NO = ?								");
		

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. - ��ȸ�� +1
	************************************************************************ */
	private String getSelectHit() throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n UPDATE BCDBA.TBGBBRD									");
		sql.append("\n SET INQR_NUM = INQR_NUM+1 							");
		sql.append("\n WHERE BBRD_SEQ_NO=?									");

		return sql.toString();
	}

}
