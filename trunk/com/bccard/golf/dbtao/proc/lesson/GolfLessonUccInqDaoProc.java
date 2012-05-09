/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLessonUccInqDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ģ���� ucc ���� ���
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
public class GolfLessonUccInqDaoProc extends AbstractProc {
	public static final String TITLE = "ģ���� ucc ���� ��� ó��";
	
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
		String reg = "";
		
		
		try{ 
			// ȸ���������̺� ���� �������� ����
			//01. ��ȸ ����
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");		
			String bbrd_clss = dataSet.getString("bbrd_clss");

			// 02. ������������
			String sql = this.getSelectQuery();			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, bbrd_clss);
			pstmt.setLong(++pidx, page_no); 

			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			 
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//ctnt ó��
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
				ctnt = ctnt.replaceAll("<P>","");
				ctnt = ctnt.replaceAll("</P>","");
				//list ���̹Ƿ� 20�ڷ� �ڸ�(���� ��������)
				if(ctnt.length() > 110){
					ctnt = ctnt.substring(0,110); 
					ctnt += "...";
				}
				
				//��¥ ����
				reg = rset.getString("REG_ATON");
				if(reg.indexOf("-") > 0) reg = reg.replaceAll("-", ".");
				
				
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addString("seq_no",				rset.getString("BBRD_SEQ_NO"));
				result.addString("titl",				rset.getString("TITL"));
				result.addString("ctnt", 				ctnt);
				result.addString("reg_aton",			reg);
				result.addString("inqr_num",			rset.getString("INQR_NUM"));
				result.addString("usename",				rset.getString("USENAME"));
				result.addString("annx_file_nm",		rset.getString("ANNX_FILE_NM"));
				result.addString("mvpt_annx_file_path",	rset.getString("MVPT_ANNX_FILE_PATH"));
				result.addString("answ_yn",				rset.getString("ANSW_YN"));
				result.addString("answ_ctnt",			rset.getString("ANSW_CTNT"));
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				existsData = true;
				
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
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,BBRD_SEQ_NO									");
		sql.append("\n 				,TITL											");
		sql.append("\n 				,REG_ATON										");
		sql.append("\n 				,INQR_NUM										");
		sql.append("\n 				,CTNT											");
		sql.append("\n 				,HG_NM											");
		sql.append("\n 				,USENAME										");
		sql.append("\n 				,MVPT_ANNX_FILE_PATH							");
		sql.append("\n 				,ANNX_FILE_NM									");
		sql.append("\n 				,ANSW_YN										");
		sql.append("\n 				,ANSW_CTNT										");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*5) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							");
		sql.append("\n 						,T1.BBRD_SEQ_NO							");
		sql.append("\n 						,T1.TITL								");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON					");
		sql.append("\n 						,T1.INQR_NUM							");
		sql.append("\n 						,T1.CTNT								");
		sql.append("\n 						,T1.HG_NM								");
		sql.append("\n 						,(SELECT  HG_NM FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T1.ID)as USENAME	");
		sql.append("\n 						,T1.MVPT_ANNX_FILE_PATH					");
		sql.append("\n 						,T1.ANNX_FILE_NM						");
		sql.append("\n 						,(CASE WHEN T1.ANSW_CTNT is not null THEN 'Y' ELSE 'N' END)as ANSW_YN	");
		sql.append("\n 						,T1.ANSW_CTNT							");
		sql.append("\n 				FROM BCDBA.TBGBBRD	T1							");
		sql.append("\n 				WHERE T1.BBRD_CLSS = ?							");
		sql.append("\n 				AND T1.EPS_YN = 'Y'	AND T1.DEL_YN = 'N'			");
		sql.append("\n 				ORDER BY T1.BBRD_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");

		return sql.toString();
	}

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectTtCountQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT count(*)as CNT FROM BCDBA.TBGBBRD 					");
		sql.append("\n WHERE  BBRD_CLSS = ? AND EPS_YN = 'Y' AND DEL_YN = 'N'		");
		
		return sql.toString();
	}
	
	
	/** ***********************************************************************
	* �� �Խù� ���� ����
	************************************************************************ */
	public String getTtCount(WaContext context, TaoDataSet dataSet) throws Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection con = null;
		String result = "0";
		
		try{
			String bbrd_clss = dataSet.getString("bbrd_clss");
			String sql = this.getSelectTtCountQuery();
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bbrd_clss);
			
			rset = pstmt.executeQuery();
			
			if(rset.next()){
				result = rset.getString("CNT");
			}else{
				result = "0";
			}
			
		}catch(Exception ex){
			
			
			//debug(">>>>>>>>>>>>>> ERROR getTtCount : "+ex.toString());
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		
		return result;
	}


}
