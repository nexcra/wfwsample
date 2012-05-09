/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntSpApplicantUpdDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: �̺�Ʈ����� > Ư���ѷ����̺�Ʈ >�����̺�Ʈ ��ûó��
*   �������	: golf
*   �ۼ�����	: 2009-07-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.applicant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.SmsSendProc;
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
public class GolfEvntSpApplicantUpdDaoProc extends AbstractProc {
	public static final String TITLE = "�̺�Ʈ����� > Ư���ѷ����̺�Ʈ >�����̺�Ʈ ���";
	/** **************************************************************************
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rset = null;
		Connection conn = null; 
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";

		try { 
			
			
			String p_idx 		= data.getString("p_idx");
			String mode 		= data.getString("mode");
			String userId 		= data.getString("userId");
			String userNm 		= data.getString("userNm");
			String sex_clss 	= data.getString("sex_clss");
			String hp_ddd_no 	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String email 		= data.getString("email");
			String golf_svc_aplc_clss 	= data.getString("golf_svc_aplc_clss");			
			
			int pidx = 0;
			int rs = 0;
			boolean eof = false;
			conn = context.getDbConnection("default", null);
			
			
			if("ins".equals(mode)){
				pidx = 0;
				long maxVal = this.selectArticleNo(context);
				sql = this.getInsertQuery();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(++pidx,maxVal);
				pstmt.setString(++pidx, golf_svc_aplc_clss);
				pstmt.setString(++pidx, p_idx); 
				pstmt.setString(++pidx, userId);
				pstmt.setString(++pidx, sex_clss);
				pstmt.setString(++pidx, email);
				pstmt.setString(++pidx, hp_ddd_no);
				pstmt.setString(++pidx, hp_tel_hno);
				pstmt.setString(++pidx, hp_tel_sno);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT","00");
				}else{
					result.addString("RESULT","01");
					eof = false;
				}
				
			}
			
			
			 
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rset != null) rset.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getInsertQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		INSERT INTO BCDBA.TBGAPLCMGMT(					"); 
		sql.append("\n 			 APLC_SEQ_NO								");
		sql.append("\n 			,GOLF_SVC_APLC_CLSS							");
		sql.append("\n 			,LESN_SEQ_NO								");
		sql.append("\n 			,PGRS_YN									");
		sql.append("\n 			,CDHD_ID									");
		sql.append("\n 			,SEX_CLSS									");
		sql.append("\n 			,EMAIL										");
		sql.append("\n 			,HP_DDD_NO									");
		sql.append("\n 			,HP_TEL_HNO									");
		sql.append("\n 			,HP_TEL_SNO									");
		sql.append("\n 			,REG_ATON									");
		sql.append("\n 			,PRZ_WIN_YN									");
		sql.append("\n 		)VALUES(										");
		sql.append("\n 			?,?,?,'N',?,								");
		sql.append("\n 			?,?,?,?,?,									");
		sql.append("\n 			TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),'N'	");
		sql.append("\n 		)												");

		

		return sql.toString();
	}
	/** ******************************************************************************
	 * �Խ��� �۹�ȣ ��������
	 *********************************************************************************/
	private long selectArticleNo(WaContext context) throws Exception {

		Connection con = null;
        PreparedStatement pstmt = null;        
        ResultSet rset = null;        
        String sql = " SELECT NVL(MAX(APLC_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGAPLCMGMT ";
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
        	pstmt = con.prepareStatement(sql);
        	rset = pstmt.executeQuery();   
			if (rset.next()) {				
                pidx = rset.getLong(1);
			}
        } catch (Throwable t) {        
        	Exception exception = new Exception(t);          
            throw exception;
        } finally {
            try { if ( rset  != null ) rset.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt != null ) pstmt.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return pidx;

	}
	
}
