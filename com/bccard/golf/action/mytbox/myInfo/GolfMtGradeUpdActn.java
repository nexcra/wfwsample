/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtGradeUpdActn
*   �ۼ���    : �̵������ ������
*   ����      : ����Ƽ�ڽ� > ���� ���� > ȸ����� ���׷��̵�
*   �������  : golf 
*   �ۼ�����  : 2009-07-04 
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.15    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentDaoProc;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.common.AppConfig;


import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0
******************************************************************************/
public class GolfMtGradeUpdActn extends GolfActn{
	
	public static final String TITLE = "����Ƽ�ڽ� > ���� ���� > ȸ����� ���׷��̵�";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü.  
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String userId = "";
		String email_id = "";
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		int resultPresent = 0;
		int aplc_seq_no = 0;	// ��û���̺�(������) seq_no
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)	
		String memSocid = "";	
		String ispCardNo = "";	// ispī���ȣ
		String ip = request.getRemoteAddr();
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

		try {
			GolfMtGradeUpdDaoProc proc = (GolfMtGradeUpdDaoProc)context.getProc("GolfMtGradeUpdDaoProc");
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			GolfMemPresentDaoProc procPresent = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// ����ǰ��û�ϱ����μ���
			
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
		 	
			 if(userEtt != null) {
				userNm			= (String)userEtt.getName(); 
				userId			= (String)userEtt.getAccount(); 
				email_id 		= (String)userEtt.getEmail1(); 
				userMobile1 	= (String)userEtt.getMobile1();
				userMobile2 	= (String)userEtt.getMobile2();
				userMobile3 	= (String)userEtt.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				memSocid 		= (String)userEtt.getSocid();	//�ֹε�Ϲ�ȣ
			}
			
			// 02.�Է°� ��ȸ	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("grd_sq2");
			paramMap.remove("yr");
			paramMap.remove("mn");
			paramMap.remove("grd_nm");
			paramMap.remove("VIEW_YN");

			String payType 				= parser.getParameter("payType", "").trim();	// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			String moneyType 			= parser.getParameter("moneyType", "").trim();	// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(����)
			
			int idx 					= parser.getIntParameter("idx", 0);			// ���׷��̵� �� ���
			String realPayAmt 			= parser.getParameter("realPayAmt", "");	// ���� �ݾ�
			String memGrade 			= parser.getParameter("memGrade", "");
			int intMemGrade 			= parser.getIntParameter("intMemGrade", 0);
			String upd_pay 				= parser.getParameter("UPD_PAY", "");
			String payWay 				= parser.getParameter("payWay", "");
			String payWayTxt			= "��ȸ��";
			if("mn".equals(payWay)){
				payWayTxt = "��ȸ��";
				upd_pay = "all";
			}
			String realPayAmt_old 		= parser.getParameter("realPayAmt_old", "");

			String gds_code 				= parser.getParameter("gds_code", "").trim();
			String rcvr_nm 					= parser.getParameter("rcvr_nm", "").trim();
			String zp1 						= parser.getParameter("zp1", "").trim();
			String zp2 						= parser.getParameter("zp2", "").trim();
			String addr 					= parser.getParameter("addr", "").trim();
			String dtl_addr					= parser.getParameter("dtl_addr", "").trim();
			String addr_clss				= parser.getParameter("addr_clss", "").trim();
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "").trim();
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "").trim();
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "").trim();
						
			debug("GolfMtGradeUpdActn =============== upd_pay => " + upd_pay + " / payWay : " + payWay + " / realPayAmt : " + realPayAmt 
					+ " / memGrade : " + memGrade + " / payWayTxt : " + payWayTxt);

			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 			= parser.getParameter("pmgds_pym_yn", "");

			String sum		 = parser.getParameter("realPayAmt", "0");	// �����ݾ�
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
			}

			int	sale_amt = 0;
			String code					= parser.getParameter("code", "").trim();		//���ޱ����ڵ�
			String joinChnl				= "0001";
			String couponYN = "N";
			
			//�����̿��ڵ� üũ ����
			String ctnt	 = "";
			String evnt_no = "";
			String cupn_clss = "";
			String code_no = "";
			String norm_amt ="0";
			String dc_amt = "0"; //���αݾ�
			
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
				norm_amt = sum;  //����ݾ�
			}
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// ��������
			dataSet.setString("CODE_NO", code_no); 		//������ȣ
			dataSet.setString("JOIN_CHNL", joinChnl);


			dataSet.setString("CODE", code); //�����ڵ�  
			dataSet.setString("SITE_CLSS", "10");//����Ʈ�����ڵ� 10:���������
			dataSet.setString("EVNT_NO", "111");//�̺�Ʈ��ȣ1
			dataSet.setString("EVNT_NO2", "112");//�̺�Ʈ��ȣ2 
			dataSet.setString("CUPN_TYPE", cupn_type);//�������� 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);//��ǰ���޿���

			// ���� ���̺� ���� ��� ���� 
			String order_no = parser.getParameter("allat_order_no", "").trim();
			dataSet.setString("ORDER_NO", order_no);	
			debug("===================�ֹ���ȣ========> " + order_no);					
			
			GolfMemInsDaoProc proc_mem = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
			//�����̿��ڵ�� ���� 10% �Է�
			if (!"".equals(code))
			{
				DbTaoResult codeCheck = proc_mem.codeExecute(context, dataSet, request);
				debug("===================codeCheck : " + codeCheck);
				if (codeCheck != null && codeCheck.isNext()) {
					codeCheck.first();
					codeCheck.next();
					debug("===================memGrade : " + codeCheck.getString("RESULT"));
					if(codeCheck.getString("RESULT").equals("00")){
						ctnt = (String) codeCheck.getString("CUPN_CTNT");	
						sale_amt = (int) codeCheck.getInt("CUPN_AMT");
						code_no = (String) codeCheck.getString("CUPN_NO");
						evnt_no = (String) codeCheck.getString("EVNT_NO");
						cupn_clss = (String) codeCheck.getString("CUPN_CLSS");
						if ( cupn_clss.equals("02") ) {
							joinChnl = "2000";  //���������̿�����  ���°�� ���԰�θ� 2000 ����
						} else  {
							joinChnl = "1000";  //���������ڵ�� ���°�� ���԰�θ� 1000 ����
						}
						//���η�
						if ("01".equals(cupn_clss))	{
							double div = ((double)(100-(double)sale_amt)/100); //������ (0.9)
							int sttl_amt = (int)(Double.parseDouble(sum) * div); //�ǰ����ݾ�
							sum = String.valueOf(sttl_amt) ; //�ǰ����ݾ�
							int dc = Integer.parseInt(norm_amt) - Integer.parseInt(sum); //���αݾ�
							dc_amt = String.valueOf(dc); //���αݾ�
							debug("�ǰ����ݾ�="+sum+",���αݾ�="+dc_amt+",������="+div);

						//��������
						} else {
							dc_amt = String.valueOf(sale_amt) ;
							int sttl_amt = Integer.parseInt(norm_amt) - Integer.parseInt(dc_amt);
							sum = String.valueOf(sttl_amt) ; //�ǰ����ݾ�
							debug("��������ݾ�="+String.valueOf(sale_amt)+",�ǰ����ݾ�="+sum+",���αݾ�="+dc_amt);
						}
						couponYN= "Y";

					}
					
				}

			}

			debug("332 : join_chnl : " + joinChnl);
			dataSet.setString("CUPN_CTNT", ctnt);
			dataSet.setString("CUPN_NO", code_no); 		//�ſ�ī���ȣ�� �Է��Ұ���.
			dataSet.setString("NORM_AMT", norm_amt); 	//������ 
			dataSet.setString("DC_AMT", dc_amt);		//���αݾ�
			dataSet.setString("STTL_AMT", sum); 		//�����ݾ�
			dataSet.setString("CUPN_CLSS", cupn_clss); 	//��������
			dataSet.setString("CODE_EVNT_NO", evnt_no); //�����̺�Ʈ��ȣ
			

			
			dataSet.setInt("idx", idx);	
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("UPD_PAY", upd_pay);
			dataSet.setString("payWay", payWay);
			dataSet.setString("realPayAmt_old", realPayAmt_old);
				
			if(!"".equals(gds_code) && idx==1) {
				dataSet.setString("gds_code", gds_code);	
				dataSet.setString("rcvr_nm", rcvr_nm);	
				dataSet.setString("zp", zp1+""+zp2);	
				dataSet.setString("addr", addr);		
				dataSet.setString("dtl_addr", dtl_addr);
				dataSet.setString("addr_clss", addr_clss);				
				dataSet.setString("hp_ddd_no", hp_ddd_no);	
				dataSet.setString("hp_tel_hno", hp_tel_hno);	
				dataSet.setString("hp_tel_sno", hp_tel_sno);
	
				// 01.����ǰ��û�ϱ�(Proc) ��ȸ 20091216
				resultPresent = procPresent.execute(context, dataSet, request);
				//debug("=========result : " + result);
			}
					

			boolean payResult = false;
			int addResult = 0;
			debug("+1+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");	
			
			debug("// STEP 1. �Է°��� ���� ����üũ+");
			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
			if ( st_s == null ) st_s = "";
			request.getSession().removeAttribute("ParameterManipulationProtectKey");

			String st_p = request.getParameter("ParameterManipulationProtectKey");
			if ( st_p == null ) st_p = "";

			if ( !st_p.equals(st_s) ) {
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
			//	throw new EtaxException(msgEtt);
			}
			
				
			// ����			
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// ������ ��ȣ(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP ������

			String cardNo		= parser.getParameter("card_no", "0");				// ī���ȣ
			String insTerm		= parser.getParameter("ins_term", "00");			// �Һΰ�����
			String siteType		= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	

			
//			debug("// STEP 1_2. �Ķ���� �Է�");
			HashMap kvpMap = null;
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}			
			
			//			debug("// STEP 1_3. ������������ ���� ��� ��ȿ�� �˻�..");
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// ����� ���̵�
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
			String pcg         = "";														// ����/���� ����			
			String valdlim	   = "";														// ���� ����
			String pid = null;																// ���ξ��̵�
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();

			// �þ����̿� ����
			String sPayType = "";		// ���Ҽ���
			String sApprovalNo = "";	// �ſ�ī�� ���ι�ȣ
			String sCardNm = "";		// �ſ�ī�� �̸�(������ü �����̸�)
			
			// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
			String sttl_gds_clss = GolfUtil.lpad(moneyType+"", 4, "0");
			if("7".equals(moneyType) && intMemGrade==7){
				sttl_gds_clss = "0008";		// ����޿� ���� ���� ��ǰ�����ڵ�, �������� �Ӵ�Ÿ�԰� ��ġ
			}
			

			if(payType.equals("1") || payType.equals("2")){
				
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
				} else {
					ispCardNo = 	parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
				}
				
				if ( valdlim.length() == 6 ) {
					valdlim = valdlim.substring(2);											
				}
				payEtt.setMerMgmtNo(merMgmtNo);
				payEtt.setCardNo(ispCardNo);
				payEtt.setValid(valdlim);			
				payEtt.setAmount(sum);
				payEtt.setInsTerm(insTerm);
				payEtt.setRemoteAddr(ip);
	
				payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
				

			}	
			// Ÿ��ī�� ������ ���(�þ�����)
			else if(payType.equals("3")){

				  String sEncData  = request.getParameter("allat_enc_data");
					
				  payEtt.setAmount(sum);
				  payEtt.setEncData(sEncData);
		
				  // �������� ��û
				  payResult = payProc.executePayAuth_Allat(context, request, payEtt);	
				   					  
				  // �������� ����
				  if(payResult){						  
					  sPayType = payEtt.getPayType();	// ���Ҽ���
					  sApprovalNo = payEtt.getUseNo();	// �ſ�ī�� ���ι�ȣ
					  insTerm = payEtt.getInsTerm();	// �ſ�ī�� �Һΰ���
					  sCardNm = payEtt.getCardNm();		// �ſ�ī�� �̸�(������ü �����̸�)
					  merMgmtNo = "";
					  
					  if(insTerm == null || insTerm.equals(""))	insTerm = "00";
					  
					  // �������ܰ� ����
					  if(sPayType.equals("CARD")){
						  sttl_mthd_clss = "0003";
					  }
					  else if(sPayType.equals("ABANK")){
						  sttl_mthd_clss = "0004";
					  }	
					  
					  dataSet.setString("AUTH_NO", sApprovalNo);	// �ſ�ī�� ���ι�ȣ
					  dataSet.setString("STTL_MINS_NM", sCardNm);	// �ſ�ī�� �̸�(������ü �����̸�)						  
				  }

			}
			
			
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("STTL_AMT", sum);
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());
			dataSet.setString("AUTH_NO", payEtt.getUseNo());  
			

			// 04.����ó��	
			if (payResult) {
				addResult = proc.execute(context, dataSet, request);	

				if (addResult == 1) {

					if("mn".equals(payWay)){;
						// ��ȸ���� ��û���̺��� ��û���� �����´�.
						aplc_seq_no = proc.execute_mnSeq(context, dataSet, request);	
						dataSet.setString("STTL_GDS_SEQ_NO", aplc_seq_no+"");  
					}

					// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);

					//�������� �ڷ� ����
					if ( "Y".equals(couponYN))	{
						int couponResult = proc_mem.couponUpExecute(context, dataSet); 
						if (couponResult != 1) {
							debug("�������� ����");
						}

					}
				}
			}
			else{	// �������н� ���� ���� 2009.11.27
				veriResCode = "3";
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);				
				debug("===================�������г���������========> " + result_fail);						
				
			}				  
			
						
	        String script = "";
			
			if (addResult == 2) {
				
				userEtt.setMemGrade(memGrade);
				userEtt.setIntMemGrade((int)intMemGrade);

				// email �߼�
				if (!email_id.equals("")) {

					String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] ��������� ���� ������ ���ϵ帳�ϴ�.";
					emailFileNm = "/email_tpl19.html";
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);
				}
				
				//sms�߼�
				if (!userMobile.equals("")) {

					// SMS ���� ����
					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", userNm);
					smsMap.put("sPhone1", userMobile1);
					smsMap.put("sPhone2", userMobile2);
					smsMap.put("sPhone3", userMobile3);
					
					String smsClss = "674";
					String message = "[BC Golf] "+userNm+"�� "+memGrade+"ȸ�� "+payWayTxt+" "+GolfUtil.comma(sum)+"���� �����Ǿ����ϴ�. �����մϴ�.";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}	
				
				script = "window.top.document.mForm.isLogin.value='1';\n";
				script = script + "window.top.document.mForm.userNm.value='"+userEtt.getName()+"'\n;";
				script = script + "window.top.document.mForm.memGrade.value='"+userEtt.getMemGrade()+"'\n;";
				script = script + "window.top.document.mForm.intMemGrade.value='"+userEtt.getIntMemGrade()+"'\n;";
				script = script + "parent.location.reload();\n";
				//script = script + "window.close();\n";
				
				request.setAttribute("script", script);
				//request.setAttribute("resultMsg", "��� ���׷��̵尡 ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
	        	veriResCode = "3";
				script = "window.close();";	
				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "��� ���׷��̵尡 ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
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
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", ip);
				hmap.put("className", "GolfMtGradeUpdActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
