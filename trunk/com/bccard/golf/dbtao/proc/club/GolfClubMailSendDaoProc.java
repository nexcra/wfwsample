/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubMailSendDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ ȸ�� ��ü ���Ϲ߼�
*   �������  : golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
/******************************************************************************
 * Golf
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfClubMailSendDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfClubMailSendDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMailSendDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public void execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			String titl =  data.getString("TITL"); 
			String ctnt =  data.getString("CTNT"); 
			String img_nm =  data.getString("IMG_NM"); 
			String club_code =  data.getString("club_code"); 
			
			if (!img_nm.equals("")) {
				img_nm = AppConfig.getAppProperty("IMG_URL_REAL")+"/club/"+club_code+"/email/"+img_nm;
				ctnt = img_nm + "<BR><BR>" + ctnt;
			}
			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
		
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					String userNm = rs.getString("CDHD_NM");
					String email_id = rs.getString("EMAIL");					

					debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					debug("++userNm++" + userNm);
					debug("++email_id++" + email_id);
					
					// ���Ϲ߼�
					if (!email_id.equals("")) {
						String emailAdmin = "\"��ī��\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String emailTitle = titl;
											
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setContents(ctnt,"");
						emailEtt.setTo(email_id);
						sender.send(emailEtt);
					}
					
				}
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}
	}	
	
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	");
		sql.append("\n 		TCM.CDHD_NM, 'lion1052@nate.com' EMAIL 	");
		sql.append("\n 	FROM 	");
		sql.append("\n 	BCDBA.TBGCLUBCDHDMGMT TCM	");
		sql.append("\n  WHERE CLUB_SEQ_NO = ?	");
		sql.append("\n 	AND SECE_YN = 'N'	");
		sql.append("\n 	AND JONN_YN = 'Y'	");			
		sql.append("\n 	ORDER BY CLUB_CDHD_SEQ_NO DESC	");		

		return sql.toString();
    }
}