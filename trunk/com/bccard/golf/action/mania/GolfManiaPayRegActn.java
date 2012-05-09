/***************************************************************************************************
*   �� �ҽ��� �߰�������� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfManiaPayRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ� 
*   ����      : ���Ͼ� ���������� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-02
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.11    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfElecAauthProcess;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mania.GolfManiaPagUpdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfManiaPayRegActn extends GolfActn{
	
	public static final String TITLE = "������û ó��";
	private static final String GO_URL = "";
	private static final String GO_BTN = "";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)	
		boolean resMsg = true;   // ���� ���� �޼��� ����
				
		String ispCardNo = "";	// ispī���ȣ
		String cstIP = request.getRemoteAddr(); //����IP		
		String pid = null;		// ���ξ��̵�
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfElecAauthProcess elecAath = new GolfElecAauthProcess();//������������
		
		String rtnMsg = "����� �������� ����";
		String validCert = "false";
		String clientAuth = "false";
		String[] semiCertVal = new String[2]; 
		
		String script = "";
		HttpSession session = null;
		
		RequestParser parser = context.getRequestParser(subpage_key, request, response);
		Map paramMap = BaseAction.getParamToMap(request);		
		paramMap.put("title", TITLE);
		
		try {
			
			semiCertVal = elecAath.semiCert(request, response);
			
			validCert = semiCertVal[0];
			clientAuth = semiCertVal[1];
			
			if (validCert.equals("false")){
				rtnMsg = "�ùٸ� �������� �ƴմϴ�.";
			}
			
			if (clientAuth.equals("false")){
				debug("������ �ʿ���� ������");				
			}
			
			debug (" ### validCert : " + validCert +", clientAuth : " + clientAuth);
			
			if (validCert.equals("true")||clientAuth.equals("false")){
				
				// 01.��������üũ
				UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			 	
				 if(usrEntity != null) {
					userNm		= (String)usrEntity.getName(); 
					memClss		= (String)usrEntity.getMemberClss();
					userId		= (String)usrEntity.getAccount(); 
					juminno 	= (String)usrEntity.getSocid(); 
					memGrade 	= (String)usrEntity.getMemGrade(); 
					intMemGrade	= (int)usrEntity.getIntMemGrade(); 
				}
	
				if(userId != null && !"".equals(userId)){
					isLogin = "1";
				} else {
					isLogin = "0";
					userNm	= "";
				}
				
				paramMap.put("userNm", userNm);
	
				GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
				
				// �Է°��� ���� ����üũ
				String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
				if ( st_s == null ) st_s = "";
				request.getSession().removeAttribute("ParameterManipulationProtectKey");
	
				String st_p = request.getParameter("ParameterManipulationProtectKey");
				if ( st_p == null ) st_p = "";
	
				if ( !st_p.equals(st_s) ) {
					MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
				//	throw new EtaxException(msgEtt);
				}			
					
				//���� 
				String ip = request.getRemoteAddr();  
				String merMgmtNo = AppConfig.getAppProperty("MBCDHD1");		// ������ ��ȣ(766559864) //topn : 745300778
				String iniplug = parser.getParameter("KVPpluginData", "");	// ISP ������
				String sum		 = parser.getParameter("realPayAmt", "0");	// �����ݾ�
				if(sum != null && !"".equals(sum)){
					sum = StrUtil.replace(sum,",","");
				}
	
				String cardNo		= parser.getParameter("card_no", "0");				// ī���ȣ
				String insTerm		= parser.getParameter("ins_term", "00");			// �Һΰ�����
				String siteType		= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
				
			
				HashMap kvpMap = null;
				if(iniplug !=null && !"".equals(iniplug)) {
					kvpMap = payProc.getKvpParameter( iniplug );
				}			
	
				//ISP & ������������ ���� ��� ��ȿ�� �˻�..
				String pcg         = "";														// ����/���� ����			
				String valdlim	   = "";														// ���� ����
	
				if(kvpMap != null) {
					
					ispAccessYn = "Y";
					pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
					ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
					valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
					if ( "2".equals(pcg) ) {
						pid = (String)kvpMap.get("BizId");								// ����ڹ�ȣ
					} else {
						pid = (String)kvpMap.get("Pid");									// ���� �ֹι�ȣ
					}

					//������ ����
					if (clientAuth.equals("true")){
						
						session = request.getSession();
						
//						if(!elecAath.isValidCert(request, response, pid, pcg)){
//							
//							elecAath.setValidCertMsg(session);
//							
//							String sessionMsg = (String)session.getAttribute("validCertMsg");								
//			
//					        request.setAttribute("returnUrl", "GolfManiaList.do");
//					        request.setAttribute("resultMsg", sessionMsg);	 
//					        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.							
//							
//							return super.getActionResponse(context, subpage_key);
//							
//						}else {
//							debug("####### ISP & �������� ���� OK  ");
//						}
					
					}					
					
				} else {
					ispCardNo = 	parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
				}
				
				if ( valdlim.length() == 6 ) {
					valdlim = valdlim.substring(2);											
				}
				
				//����ó��
				payEtt.setMerMgmtNo(merMgmtNo);
				payEtt.setCardNo(ispCardNo);
				payEtt.setValid(valdlim);			
				payEtt.setAmount(sum);
				payEtt.setInsTerm(insTerm);
				payEtt.setRemoteAddr(ip);
	
				boolean payResult = false;
				payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
	
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				String nowYear = String.valueOf(cal.get(Calendar.YEAR));
				String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
				String nowDate = String.valueOf(cal.get(Calendar.DATE));
				String nowDay = nowYear +"�� "+ nowMonth +"�� "+ nowDate +"��";
		
				long recv_no	= parser.getLongParameter("p_idx", 0L);// ��ǰ��ȣ
				String recv_sno = String.valueOf(recv_no); // �������� ���̺� �־��� ��ǰ��ȣ ��Ʈ�������� ��ȯ
	
				String lsn_type_cd = parser.getParameter("slsn_type_cd", "");
				String sex = parser.getParameter("sex", "");	// ����
				String email_id = parser.getParameter("email_id", "");	// E-mail
				String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// ��ȭddd��ȣ
				String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// ��ȭ����ȣ
				String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// ��ȭ�Ϸù�ȣ		
				String tel_no = chg_ddd_no+"-"+chg_tel_hno+"-"+chg_tel_sno;
				String lsn_expc_clss = parser.getParameter("lsn_expc_clss", "");	// ���������ڵ�
				String mttr = parser.getParameter("mttr", "");	// Ư�̻���
				String lsn_nm = parser.getParameter("lsn_nm", "");	// ������
				// �̸��Ͽ��� ���
				String phone = chg_ddd_no + chg_tel_hno + chg_tel_sno;
				String lsn_expc_clss_nm = "";
				if (lsn_expc_clss.equals("0001")) lsn_expc_clss_nm="�� (ó�� ������ ����)";
				if (lsn_expc_clss.equals("0002")) lsn_expc_clss_nm="�� (���� ���� ����)";
				if (lsn_expc_clss.equals("0003")) lsn_expc_clss_nm="�� (���� ���� �ټ�)";
				String sex_nm = "";
				if (sex.equals("F")) sex_nm="��";
				if (sex.equals("M")) sex_nm="��";
				// �̸��Ͽ��� ���
	
				String lsn_seq_type = nowYear+"G";
				if (lsn_type_cd.equals("0002")) lsn_seq_type =  nowYear+"S";
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("CSTMR_ID", userId);
				dataSet.setString("LSN_SEQ_TYPE", lsn_seq_type);			
				dataSet.setLong("RECV_NO", recv_no);
				dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
				dataSet.setString("SEX", sex);
				dataSet.setString("EMAIL_ID", email_id);
				dataSet.setString("CHG_DDD_NO", chg_ddd_no);
				dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
				dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
				dataSet.setString("LSN_EXPC_CLSS", lsn_expc_clss);
				dataSet.setString("MTTR", mttr);
				dataSet.setString("STTL_AMT", sum);
	
				// 04.���� ���̺�(Proc) ��ȸ
				GolfManiaPagUpdDaoProc proc = (GolfManiaPagUpdDaoProc)context.getProc("GolfManiaPagUpdDaoProc");
				GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
	
				int addResult = 0;
				String aplc_seq_no = "";				
				
				// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				String sttl_mthd_clss = "";
				if (insTerm.equals("00")) sttl_mthd_clss="0001";
				else sttl_mthd_clss="0002";
				
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", "0005");
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("STTL_AMT", sum);
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
				dataSet.setString("STTL_GDS_SEQ_NO", recv_sno);				
	
				// ���� ���� �Ϸ�
				if (payResult) { // ��~~ �����ؾ� �� ����
					// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = proc.execute(context, dataSet);
					aplc_seq_no = proc.getMaxSeqNo(context, dataSet);
	
					if (addResult == 1) {
						// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
						addResult = addResult + addPayProc.execute(context, dataSet);
					}
				}else{	// �������н� ���� ���� 2009.11.27 
					
					veriResCode = "3";
					resMsg = false;
					rtnMsg = payEtt.getResMsg();
					
					addPayProc.failExecute(context, dataSet, request, payEtt);
					
				}			
	
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", chg_ddd_no);
				smsMap.put("sPhone2", chg_tel_hno);
				smsMap.put("sPhone3", chg_tel_sno);
				
				boolean payCancelResult = false;
		        if (addResult == 2) {
					request.setAttribute("returnUrl", reUrl);
					request.setAttribute("resultMsg", "");	
	
					// ���Ϲ߼�
					if (!email_id.equals("")) {
						String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String emailTitle = userNm +"�� ���������� ��û�� �Ϸ�Ǿ����ϴ�.";
						String emailFileNm = "/email_tpl26.html";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
											
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+lsn_nm+"|"+userNm+"|"+sex_nm+"|"+tel_no+"|"+email_id+"|"+lsn_expc_clss_nm+"|"+mttr+"|"+nowDay+"|"+sum);
						emailEtt.setTo(email_id);
						sender.send(emailEtt);
					}
					
					//sms�߼�
					if (!phone.equals("")) {
						
						debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "650";
						String message = "[����������]"+userNm+"�� "+lsn_nm+" "+sum+"�� �����Ϸ� - Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}	
					
		        } else if (addResult == 9) { //�ѹ� �� üũ��
		        	veriResCode = "3";
		        	// DB���� ���н� ������� ����	        	
					payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
		        	
					request.setAttribute("returnUrl", errReUrl);
					request.setAttribute("resultMsg", "�̹� �����ϼ̽��ϴ�.");	        	
		        } else {
		        	veriResCode = "3";
		        	// DB���� ���н� ������� ����
					payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
					
					request.setAttribute("returnUrl", errReUrl);
					request.setAttribute("resultMsg", "���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
		        
		        if ( !resMsg ){
		        	request.setAttribute("resultMsg", rtnMsg +"\\n\\n���������� ó�� ���� �ʾҽ��ϴ�.");
		        }
				        
				// 05. Return �� ����			
				paramMap.put("aplc_seq_no", aplc_seq_no);		
				paramMap.put("editResult", String.valueOf(addResult));			
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		
		        
			}else {
				
				request.setAttribute("returnUrl", "GolfManiaList.do");
		        request.setAttribute("resultMsg", rtnMsg);	 
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
	        
			}
			
		} catch(Throwable t) {
			veriResCode = "3";
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			
			if(ispAccessYn.equals("Y")){		
				
				//ISP���� �α� ���
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", userNm);
				hmap.put("memSocid", pid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfManiaPayRegActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
			
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	
}
