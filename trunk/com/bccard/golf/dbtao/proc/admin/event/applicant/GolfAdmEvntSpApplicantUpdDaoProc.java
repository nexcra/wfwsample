/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmEvntSpApplicantInqDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ >  �̺�Ʈ > Ư������ �̺�Ʈ ��û���� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.applicant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
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
public class GolfAdmEvntSpApplicantUpdDaoProc extends AbstractProc {
	public static final String TITLE = "������ >  �̺�Ʈ > Ư������ �̺�Ʈ ��û���� ó��";
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
		String evnt_nm = "";
		String lesn_strt_date = "";
		String lesn_end_date = "";
		String rcru_pe_org_num = "";
		String lesn_norm_cost = "";
		String lesn_dc_cost = "";
		String evnt_bnft_expl = "";
		String hp_ddd = "";
		String hp_hno = "";
		String hp_sno = "";
		String hp_no = "";
		String email_id = "";
		
		try {
			
			conn = context.getDbConnection("default", null);
		
			String p_idx 		  = data.getString("p_idx");
			String mode 		  = data.getString("mode");
			String userNm 		  = data.getString("userNm");
			String prz_win_yn	  = data.getString("prz_win_yn");
			
			int pidx = 0;
			int rs = 0;
			boolean eof = false;
			
			if("przChg".equals(mode)){
				//��÷�� ó��
				sql = this.getPrzChgQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, prz_win_yn);
				pstmt.setString(++pidx, p_idx);
				
				rs = pstmt.executeUpdate();
				//debug("------------------------- rs :"+rs);
				if(rs > 0){
					if(!eof) result.addString("RESULT", "00");
					eof = true;
					
					pidx = 0;
					sql = this.getEvntDetailQuery();
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(++pidx, p_idx);
					
					rset = pstmt.executeQuery();
					while(rset.next()){
						//debug("----------------------------------  mail in ---------------------------");
						evnt_nm 			= rset.getString("EVNT_NM");
						lesn_strt_date 		= rset.getString("LESN_STRT_DATE");
						lesn_end_date 		= rset.getString("LESN_END_DATE");
						rcru_pe_org_num 	= rset.getString("RCRU_PE_ORG_NUM");
						lesn_norm_cost 		= rset.getString("LESN_NORM_COST");
						lesn_dc_cost 		= rset.getString("LESN_DC_COST");
						evnt_bnft_expl 		= rset.getString("EVNT_BNFT_EXPL");
						hp_ddd 				= rset.getString("HP_DDD_NO");
						hp_hno 				= rset.getString("HP_TEL_HNO");
						hp_sno 				= rset.getString("HP_TEL_SNO");
						email_id 			= rset.getString("EMAIL");
						
						if(lesn_dc_cost.equals("0")){
							lesn_dc_cost = "����";
						}else{
							lesn_dc_cost  = lesn_dc_cost +" ��";
						}
						if(rcru_pe_org_num.equals("0")){
							rcru_pe_org_num = "������";
						}else{
							rcru_pe_org_num = rcru_pe_org_num+" ��";
						}
						if(!"".equals(evnt_bnft_expl)){
							evnt_bnft_expl = evnt_bnft_expl.replaceAll("\r\n", "<br/>");
						}
					
						if(!"".equals(hp_ddd)) hp_no += hp_ddd;
						if(!"".equals(hp_hno)) hp_no += "-"+hp_hno;
						if(!"".equals(hp_sno)) hp_no += "-"+hp_sno;
					
						//debug("--------------------------hp no : "+ hp_no);
						//�׽�Ʈ�� �����Ұ�
						//email_id = "tjswjd56@naver.com";
						if(!"".equals(email_id)){
							String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
							String emailTitle = "[Golf Loun.G]Ư���� ���� �̺�Ʈ ��÷�� ���ϵ帳�ϴ�.";
							String emailFileNm = "/email_tpl13.html";
							String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
							String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
												
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							
							//0: ��û���̸�/ 1: �̺�Ʈ��/ 2: �������۳�¥/ 3: �������ᳯ¥/ 4: �����ο�/ 5: ����������/ 6: �������κ��/ 7:�̺�Ʈ���� 
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+evnt_nm+"|"+lesn_strt_date+"|"+lesn_end_date +"|"+rcru_pe_org_num+"|"+
									lesn_norm_cost+"|"+ lesn_dc_cost +"|"+evnt_bnft_expl);
							emailEtt.setTo(email_id);
							sender.send(emailEtt);
						}

					}
					//debug("----------------------------------  mail 00000 ---------------------------");
					if(hp_no.indexOf("-") > 0){
						//debug("---------------------------------- sms in ---------------------------");
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", userNm);
						smsMap.put("sPhone1", hp_ddd);
						smsMap.put("sPhone2", hp_hno);
						smsMap.put("sPhone3", hp_sno);
						
						//debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "647";
						String message = "[Ư���ѷ��� �̺�Ʈ��÷]"+userNm+"�� "+evnt_nm+" - Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}
				}
	
			}
			if(!eof) { 
				result.addString("RESULT", "01");
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
	private String getPrzChgQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n UPDATE BCDBA.TBGAPLCMGMT SET								");
		sql.append("\n 		PRZ_WIN_YN = ?										");
		sql.append("\n WHERE APLC_SEQ_NO = ?									");
		

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getEvntDetailQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT  	T1.EVNT_SEQ_NO																			 ");
		sql.append("\n 		  	,T1.EVNT_NM																				 ");
		sql.append("\n 		  	,TO_CHAR(TO_DATE(T1.LESN_STRT_DATE,'yyyy-MM-dd'),'YYYY-MM-DD')AS LESN_STRT_DATE			 ");
		sql.append("\n 		  	,TO_CHAR(TO_DATE(T1.LESN_END_DATE,'yyyy-MM-dd'),'YYYY-MM-DD')AS LESN_END_DATE			 ");
		sql.append("\n 		  	,TO_CHAR(T1.LESN_NORM_COST,'999,999,999,999,999')AS LESN_NORM_COST						 ");
		sql.append("\n 		  	,TO_CHAR(T1.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST							 ");
		sql.append("\n 		  	,T1.RCRU_PE_ORG_NUM																		 ");
		sql.append("\n 		  	,T1.EVNT_BNFT_EXPL																		 ");
		sql.append("\n 		  	,T2.HP_DDD_NO																			 ");
		sql.append("\n 		  	,T2.HP_TEL_HNO																			 ");
		sql.append("\n 		  	,T2.HP_TEL_SNO																			 ");
		sql.append("\n 		  	,T2.EMAIL																			 ");
		sql.append("\n FROM BCDBA.TBGEVNTMGMT T1 left join BCDBA.TBGAPLCMGMT T2 on T1.EVNT_SEQ_NO = T2.LESN_SEQ_NO		 ");
		sql.append("\n WHERE T2.APLC_SEQ_NO = ?																			 ");
		

		return sql.toString();
	}
	
}
