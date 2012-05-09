/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntKvpDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ����ȸ > ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.ez;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0  
 ******************************************************************************/
public class GolfEvntEzReturnDaoProc extends AbstractProc {
	
	public GolfEvntEzReturnDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int updEvntFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int int_re =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);		
			conn.setAutoCommit(false);	

			// ���Ϻ���
			String result 		= data.getString("result");			// �ֹ�ó�� ��� : yes:����, no:����
			String aspOrderNum 	= data.getString("aspOrderNum");	// �ֹ���ȣ (���޻���) -> ��������� �ֹ���ȣ
			String orderNum 	= data.getString("orderNum");		// �ֹ���ȣ (��������)
			String paySummary 	= data.getString("paySummary");		// ������� ���� : ����Ʈ:10000, ī��:50000
			String errDesc 		= data.getString("errDesc");		// ��������
			
			// �̺�Ʈ ���
			String pgrs_yn			= "";	// ���࿩�� I:���, Y:����, N:���
			String jumin_no			= "";	// �ֹε�Ϲ�ȣ
			String evnt_grd			= "";	// ��û���
			String sttl_amt			= "";	// ���� �ݾ�

			// ȸ������
			String cdhd_id 			= "";
			String sece_yn 			= "";
			String member_clss 		= "";	// 1: ���� , 2:����
			String cdhd_ctgo_seq_no	= "";	// ���
			String hg_nm 			= "";	// �̸�
			
			// �������� ��������
			String mobileArr[];
			String hp_ddd_no 		= "";	// �޴���ȭDDD��ȣ
			String hp_tel_hno 		= "";	// �޴���ȭ����ȣ
			String hp_tel_sno 		= "";	// �޴���ȭ�Ϸù�ȣ
			String mobile = (String)request.getSession().getAttribute("ezMobile");
			String email_addr = (String)request.getSession().getAttribute("ezEmail");
			

			String serverip = InetAddress.getLocalHost().getHostAddress();	// ����������
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");		// ���߱� ip ����
			if(serverip.equals(devip)){
//				mobile = "010-9192-4738";
//				email_addr = "simijoa@naver.com";
			}
			
            if(!GolfUtil.empty(mobile)){
            	mobileArr = GolfUtil.split(mobile, "-");
            	hp_ddd_no = mobileArr[0];
            	hp_tel_hno = mobileArr[1];
            	hp_tel_sno = mobileArr[2];
            }
			
			// TM ���
			String golf_cdhd_grd_clss 	= "";	// ����ȸ����ޱ����ڵ� (1:��� 2:��� 3:è�ǿ� 4:��)
			int cnt 					= 0;
			
			String grd 					= "";	// ������ ��� //7:���(�췮)25,000  6:���(���)50,000 5:è�ǿ�(VIP) 200,000 10:�� 120,000
			String join_chnl 			= "";	// ���԰��
			String memGrade				= "";

			String tb_rslt_clss 		= "01";	// TM��������ڵ� (01:���� 00:��ȸ�� )
			String auth_clss 			= "";	// ������� 1:ī����� 2:���հ��� 3:����Ʈ
			if(paySummary.equals("10000")){
				auth_clss = "3";
			}else{
				auth_clss = "1";
			}

			
			if(result.equals("yes")){
				pgrs_yn = "Y";
				
				// �̺�Ʈ ���� ��������
				pstmt = conn.prepareStatement(getEvntInfo());
				pstmt.setString(1, aspOrderNum);
				rs = pstmt.executeQuery(); 
				while (rs.next())	{		
					jumin_no	= rs.getString("JUMIN_NO");
					evnt_grd	= rs.getString("RSVT_CDHD_GRD_SEQ_NO");
					sttl_amt	= rs.getString("STTL_AMT");
					hg_nm		= rs.getString("BKG_PE_NM");
				}

				if(evnt_grd.equals("1")){
					grd = "2";
					join_chnl = "2901";
					golf_cdhd_grd_clss = "3";
					memGrade = "è�ǿ�";
				}else if(evnt_grd.equals("2")){
					grd = "6";
					join_chnl = "2902";
					golf_cdhd_grd_clss = "2";
					memGrade = "���";
				}else if(evnt_grd.equals("3")){
					grd = "7";
					join_chnl = "2903";
					golf_cdhd_grd_clss = "1";
					memGrade = "���";
				}else if(evnt_grd.equals("7")){
					grd = "10";
					join_chnl = "2910";
					golf_cdhd_grd_clss = "4";
					memGrade = "��";
				}

				// ȸ������ ����Ÿ �ִ��� ��ȸ (�켱���� : ������Ÿ, ����ȸ����������  �ֱ��� ����Ÿ)
				pstmt = conn.prepareStatement(getMemInfo());
				pstmt.setString(1, jumin_no);
				rs = pstmt.executeQuery(); 

				while (rs.next())	{		
					cdhd_id	= rs.getString("CDHD_ID");
					sece_yn	= rs.getString("SECE_YN");
					member_clss	= rs.getString("MEMBER_CLSS");
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
					hg_nm = rs.getString("HG_NM");
					mobile = rs.getString("MOBILE");
					cnt ++;
				}

				
				//�ִٸ� UPDATE (��ȿ�Ⱓ,��ǥ���,��������,��������)
				if (cnt > 0) {

					tb_rslt_clss = "00";	// ��ȸ��

					// ����ȸ�� ���� ó��
					pstmt = conn.prepareStatement(getUpdMem());
					pstmt.setString(1, join_chnl);		// è�ǿ� : 2901 , ��� : 2902 , ��� : 2903 , �� : 2910
					pstmt.setString(2, grd);
					pstmt.setString(3, cdhd_id);
					int_re = pstmt.executeUpdate();
					if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
					
										
					//��޺��� �����丮 getInsHistory
					if(int_re>0){
						pstmt = conn.prepareStatement(getInsHistory());
						pstmt.setString(1, cdhd_id);
						pstmt.setString(2, cdhd_ctgo_seq_no);				
						int_re = pstmt.executeUpdate(); 
						if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
					}
					

					//��޺���
					if(int_re>0){
						pstmt = conn.prepareStatement(getUpdGrd());
						pstmt.setString(1, grd );
						pstmt.setString(2, cdhd_id);
						pstmt.setString(3, cdhd_ctgo_seq_no);
						int_re = pstmt.executeUpdate(); 
					}
				}else{	// ȸ������ ������� ������Ʈ ó�� ����
					int_re = 1;
				}

				
				// TM �ڷ� �Է�
				if(int_re>0){
					pstmt = conn.prepareStatement(getInsTm());
					idx = 1;
					pstmt.setString(idx++, tb_rslt_clss);		// TM��������ڵ� (01:���� 00:��ȸ�� )
					pstmt.setString(idx++, hg_nm);				// ����
					pstmt.setString(idx++, hp_ddd_no);			// �޴���ȭDDD��ȣ
					pstmt.setString(idx++, hp_tel_hno);			// �޴���ȭ����ȣ
					pstmt.setString(idx++, hp_tel_sno);			// �޴���ȭ�Ϸù�ȣ
					pstmt.setString(idx++, jumin_no);			// �ֹε�Ϲ�ȣ
					pstmt.setString(idx++, golf_cdhd_grd_clss);	// ����ȸ����ޱ����ڵ� (1:��� 2:��� 3:è�ǿ� 4:��)
					pstmt.setString(idx++, "EZ");				// ���԰�α����ڵ� (EZ:������)
					pstmt.setString(idx++, auth_clss);			// 1:ī����� 2:���հ��� 3:����Ʈ
					pstmt.setString(idx++, join_chnl);			// ���԰�� (è�ǿ� : 2901 , ��� : 2902 , ��� : 2903 , �� : 2910
					pstmt.setString(idx++, cdhd_id);			// ��ȸ���ϰ�� ���̵� �Է��� ��
					int_re = pstmt.executeUpdate(); 
				}
				
				// TM ��ȸ�� ����
				if(int_re>0){
					pstmt = conn.prepareStatement(getInsTmPay());
					idx = 1;
					pstmt.setString(idx++, jumin_no);	// JUMIN_NO
					pstmt.setString(idx++, aspOrderNum);// AUTH_NO
					pstmt.setString(idx++, orderNum);	// CARD_NO
					pstmt.setString(idx++, sttl_amt);	// AUTH_AMT
					pstmt.setString(idx++, auth_clss);	// AUTH_CLSS
					int_re = pstmt.executeUpdate(); 
				}


				// SMS ���� ����
				if(!GolfUtil.empty(mobile)){
					HashMap smsMap = new HashMap();
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", hg_nm);
					smsMap.put("sPhone1", hp_ddd_no);
					smsMap.put("sPhone2", hp_tel_hno);
					smsMap.put("sPhone3", hp_tel_sno);
					smsMap.put("sCallCenter", "15666578");
					String smsClss = "674";
					String message = "[Golf Loun.G]"+hg_nm+"�� ���������(www.golfloung.com)ȸ�������������ֽñ�ٶ��ϴ�" ;
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("message : " + message);
				}
				


				try{
					if(!GolfUtil.empty(email_addr)){

						/*���Ϲ߼�*/
						String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\"";   //"<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						
						SimpleDateFormat fmt = new SimpleDateFormat("yyyy�� MM�� dd��");   
						GregorianCalendar cal = new GregorianCalendar();
						Date edDate = cal.getTime();
						String strEdDate = fmt.format(edDate);
						
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						// 20100824 SK ���������� ����
						String emailTitle = "[Golf Loun.G]���������  ȸ������ SK������";
						String emailFileNm = "/eamil_tm_oill.html";
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, strEdDate);
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle); 
						emailEtt.setTo(email_addr);
						sender.send(emailEtt);
					}
					
					debug("[��������� TM ���Ϲ߼� �Ϸ�] �ֹι�ȣ |" + jumin_no + "|email_addr|" + email_addr);
				}catch (javax.mail.SendFailedException ex) {
					debug("[��������� TM ���Ϲ߼� ����] �ֹι�ȣ |" + jumin_no + "|email_addr|" + email_addr);
				}

				request.getSession().removeAttribute("ezMobile");
				request.getSession().removeAttribute("ezEmail");
			
			// ������ ���������� ó�� �Ǿ������ ó�� ����
				
			}else{
				pgrs_yn = "N";
				int_re = 1;
			}
			
			
			// �ش� �ֹ������� ������Ʈ �Ѵ�.
			if(int_re>0){
				pstmt = conn.prepareStatement(getUpdEvnt());
				idx = 1;
				pstmt.setString(idx++, pgrs_yn);
				pstmt.setString(idx++, errDesc);
				pstmt.setString(idx++, result);
				pstmt.setString(idx++, paySummary);
				pstmt.setString(idx++, orderNum);
				pstmt.setString(idx++, aspOrderNum);
				int_re = pstmt.executeUpdate(); 
			}

			if(int_re > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return int_re;
	}

	


	/** ***********************************************************************
    * �̺�Ʈ ���� ��������
    ************************************************************************ */
    private String getEvntInfo(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT JUMIN_NO, RSVT_CDHD_GRD_SEQ_NO, STTL_AMT, BKG_PE_NM FROM BCDBA.TBGAPLCMGMT WHERE APLC_SEQ_NO = ? \n");

		return sql.toString();
    }

	/** ***********************************************************************
    * ȸ������ ��������
    ************************************************************************ */
    private String getMemInfo(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT CDHD_ID,NVL(SECE_YN,'N')  SECE_YN ,MEMBER_CLSS,CDHD_CTGO_SEQ_NO, ACRG_CDHD_JONN_DATE, NVL(ACRG_CDHD_END_DATE,'20090701') ACRG_CDHD_END_DATE \n");
		sql.append("\t		, HG_NM, MOBILE \n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD    \n");
		sql.append("\t	WHERE JUMIN_NO = ?  \n"); 
		sql.append("\t	ORDER BY SECE_YN DESC , ACRG_CDHD_END_DATE  \n");

		return sql.toString();
    }

	/** ***********************************************************************
    * ����ȸ���Ⱓ �����ϱ�
    ************************************************************************ */
    private String getUpdEvnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGAPLCMGMT SET	\n");
		sql.append("\t	PGRS_YN=?, MEMO_EXPL=?, ADDR=?, DTL_ADDR=?, REG_MGR_ID=?,	\n");
		sql.append("\t	CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t	WHERE APLC_SEQ_NO=?	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * ����ȸ�� ���� ó��
     ************************************************************************ */
     private String getUpdMem(){
         StringBuffer sql = new StringBuffer();	
 		sql.append("	\n");
 		sql.append(" UPDATE BCDBA.TBGGOLFCDHD  			\n");    
 		sql.append("    SET JOIN_CHNL = ? ,  	 \n");
 		sql.append("        ACRG_CDHD_JONN_DATE = TO_CHAR(SYSDATE,'yyyyMMdd') , 	 \n");
 		sql.append("        ACRG_CDHD_END_DATE = TO_CHAR(ADD_MONTHS(SYSDATE,12),'yyyyMMdd') ,  	 \n");
 		sql.append("        SECE_YN = NULL ,  	 \n");
 		sql.append("        SECE_ATON = NULL ,  	 \n");				
 		sql.append("        CDHD_CTGO_SEQ_NO = ?  	 \n");
 		sql.append("  WHERE CDHD_ID IN ( ? )	\n");
 		return sql.toString();
     }

 	/** ***********************************************************************
 	 * ��޺��� �����丮
     ************************************************************************ */
     private String getInsHistory(){
    	 StringBuffer sql = new StringBuffer();	
    	sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD, BCDBA.TBGGOLFCDHD B	\n");
		sql.append("\t  WHERE A.CDHD_ID = B.CDHD_ID AND GRD.CDHD_ID=? AND GRD.CDHD_CTGO_SEQ_NO= ?	\n");
		
		
		
		
  		return sql.toString();
      }

   	/** ***********************************************************************
	* ��޺���
	************************************************************************ */
	private String getUpdGrd(){
		StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append(" UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");
		sql.append("    SET CDHD_CTGO_SEQ_NO = ?  , 	 \n");
		sql.append("        CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),   	 \n");	
		sql.append("        CHNG_RSON_CTNT = '��������޺���'   	 \n");				 	
		sql.append("  WHERE CDHD_ID = ? 	");
		sql.append("  AND   CDHD_CTGO_SEQ_NO = ? 	");
		return sql.toString();
	}

   	/** ***********************************************************************
	* ���� ��������� ȸ������ (TM���̺� ����)
	************************************************************************ */
	private String getInsTm(){
		StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBLUGTMCSTMR (RND_CD_CLSS,TB_RSLT_CLSS,MB_CDHD_NO,HG_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO	\n");
		sql.append("\t			,RECP_DATE,JUMIN_NO,GOLF_CDHD_GRD_CLSS,JOIN_CHNL,WK_DATE,WK_TIME,AUTH_CLSS,ACPT_CHNL_CLSS,RCRU_PL_CLSS,REJ_RSON)	\n");
		sql.append("\t	VALUES('2',?,'ezwel',?,?,?,?,TO_CHAR(SYSDATE,'yyyymmdd'),?,?,?,TO_CHAR(SYSDATE,'yyyymmdd'),TO_CHAR(SYSDATE,'hh24miss'),?,'3',?,? )	\n");
		return sql.toString();
	}

   	/** ***********************************************************************
	* TM ��ȸ�� ����
	************************************************************************ */
	private String getInsTmPay(){
		StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	INSERT INTO  BCDBA.TBGLUGANLFEECTNT	\n");
		sql.append("\t	(JUMIN_NO,AUTH_NO,CARD_NO, AUTH_DATE,AUTH_TIME,AUTH_AMT,AUTH_CLSS,RND_CD_CLSS,MB_CDHD_NO)	\n");
		sql.append("\t	VALUES ( ?, ?,?, TO_CHAR(SYSDATE,'yyyyMMdd'), TO_CHAR(SYSDATE,'hh24miss') , ? , ?, '2', 'ezwel' )	\n");
		return sql.toString();
	}

}
