/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.11    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemMonthJoinDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;

/******************************************************************************
* Golf
* @author	�̵������  
* @version	1.0 
******************************************************************************/
public class GolfMemInsActn extends GolfActn{
	
	public static final String TITLE = "���� > ���";

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
		String memGrade = "White"; 
		int intMemGrade = 4;  
		String email_id = ""; 
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String userSocid = "";	// 20100319 TM��ȭ���űǱ��� �̺�Ʈ ���� �߰�
		int anl_fee = 0;	// ��� ���� ���̺��� �ش� ����� ���� �ݾ�

		// ��ó�� ���� ����
		boolean payResult = false;
		boolean payCancelResult = false;
		int addResult = 0;
		int	sale_amt = 0;

		int result = 0;
		String returnUrlTrue = ""; 
    	String returnUrlFalse =  "";
    	String script = ""; 
    	String scriptFalse = "";
    	String strMem = "";
		String sum = "0";
		String couponYN = "N";
		int resultPresent = 0;
		

		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
    	String userIP = (String) request.getRemoteAddr();
    	String uurl = (String) request.getParameter("UURL");
    	String userAcount = "";
		Connection con = null;

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)		
		String ispCardNo   = "";  // ispī���ȣ
		boolean monPaySuccYn = false;		
		boolean yearPaySuccYn = false;
		String payType = "";
		ResultException rx = null;

		try {
			con = context.getDbConnection("default", null);
			
			// 01.��������üũ 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				strMem 		= (String)usrEntity.getMemGrade();
				userSocid 	= (String)usrEntity.getSocid();
				System.out.print("## strMem:"+strMem);
				
			}else{
				userId = parser.getParameter("userId", "");
				
				HttpSession session = request.getSession();
		    	session.setAttribute("userID", userId);	
		    	session.setAttribute("userIP", userIP);	
		    	session.setAttribute("UURL", uurl);	
		    	session.setAttribute("SYSID", userId);

				UcusrinfoDaoProc proc_user = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	
				UcusrinfoEntity ucusrinfo2 = proc_user.selectByAccount(con, userId);
				session.setAttribute("FRONT_ENTITY", ucusrinfo2);
				session.setAttribute("SESSION_USER", ucusrinfo2);
						
	
		        UcusrinfoEntity usrEntity2 = SessionUtil.getFrontUserInfo(request);
				
		        if(usrEntity2 == null) {
		        	
		        	usrEntity = new UcusrinfoEntity(); 
		        	debug("## GolfCtrlServ | usrEntity null --> ���� �۾� \n");
		        }else{
		        	
		        	userAcount = usrEntity.getAccount();
		        	debug("## GolfCtrlServ | usrEntity not null  \n");
		        } 

		        CookieManager.addCookie(SECode.USER_ID, userId, sso_domain, response);
		        String sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);

				debug("## GolfMemBcJoinEndActn | userId ��ȣȭ�Ȱ� : " + userId + " / sso_domain : " + sso_domain 
						+ " / userAcount : " + userAcount + " / ucusrinfo : " + ucusrinfo2 + "sso_id : " + sso_id);	
			}
			
			
			payType 					= parser.getParameter("payType", "").trim();	// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			String moneyType 			= parser.getParameter("moneyType", "").trim();	// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(����)
			String memType 				= parser.getParameter("memType", "").trim();	// ȸ������ - ��ȸ�� : 1 ��ȸ��:2
			String insType				= parser.getParameter("insType", "").trim();	// ���԰�� - TM : 1 �Ϲ� : ""
			String openerType			= parser.getParameter("openerType", "").trim();	// N:���׷��̵� ȸ��
			String realPayAmt			= parser.getParameter("realPayAmt", "").trim();	// �ǰ����ݾ�
			String tmYn					= parser.getParameter("tmYn", "").trim();		// Y:TM ��
			String payWay				= parser.getParameter("payWay", "").trim();		// yr:��ȸ��, mn:��ȸ��
			String payWayTxt			= "��ȸ��";
			if("mn".equals(payWay)){
				payWayTxt = "��ȸ��";
			}
			String code					= parser.getParameter("code", "").trim();		//���ޱ����ڵ�
			String joinChnl				= "0001";
			String formtarget			= parser.getParameter("formtarget", "");		//���ޱ����ڵ�
			
			//-- 2009.11.12 �߰� 
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 				= parser.getParameter("pmgds_pym_yn", "");

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
			
			//VIPī�� ����
			String vipCardYn 				= parser.getParameter("vipCardYn", "N");

			String call_actionKey 			= parser.getParameter("call_actionKey", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");

			debug("GolfMemInsActn :: vipCardYn : "+vipCardYn+" / payType : " + payType + " / moneyType : " + moneyType + " / memType : " + memType + " / insType : " + insType 
					+ " / openerType : " + openerType + " / realPayAmt : " + realPayAmt + " / cupn_type : " + cupn_type
					+ " / pmgds_pym_yn : " + pmgds_pym_yn + " / tmYn : " + tmYn + " / payWay : " + payWay + " / code : " + code + " / formtarget : " + formtarget);			

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			//zp  addr dtl_addr
			if(!"".equals(gds_code) && "1".equals(moneyType)) {
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
				GolfMemPresentDaoProc procPresent = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// ����ǰ��û�ϱ����μ���
				resultPresent = procPresent.execute(context, dataSet, request);
			}
			
			dataSet.setString("payType", payType);	
			dataSet.setString("moneyType", moneyType);	
			dataSet.setString("memType", memType);	
			dataSet.setString("insType", insType);	
			dataSet.setString("CODE", code); //�����ڵ�  
			dataSet.setString("SITE_CLSS", "10");//����Ʈ�����ڵ� 10:���������
			dataSet.setString("EVNT_NO", "111");//�̺�Ʈ��ȣ1
			dataSet.setString("EVNT_NO2", "112");//�̺�Ʈ��ȣ2 
			dataSet.setString("CUPN_TYPE", cupn_type);//�������� 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);//��ǰ���޿��� 
			dataSet.setString("payWay", payWay);// yr:��ȸ��, mn:��ȸ��
			dataSet.setString("vipCardYn", vipCardYn);			
			
			// 04.���� ���̺�(Proc) ��ȸ  
			GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
			
			// ���� ���̺� ���� ��� ���� 
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			String order_no = parser.getParameter("allat_order_no", "").trim();
			dataSet.setString("ORDER_NO", order_no);	
			debug("===================�ֹ���ȣ========> " + order_no);						

			// ȸ�� ��� ��������  
			DbTaoResult gradeView = proc.gradeExecute(context, dataSet, request);
			debug("===================gradeView : " + gradeView);
			if (gradeView != null && gradeView.isNext()) {
				gradeView.first();
				gradeView.next();
				debug("===================memGrade : " + gradeView.getString("RESULT"));
				if(gradeView.getString("RESULT").equals("00")){
					memGrade = (String) gradeView.getString("memGrade").trim();	
					intMemGrade = (int) gradeView.getInt("intMemGrade");	
					anl_fee = (int) gradeView.getInt("anl_fee");
				}
			} 
			
			//�����̿��ڵ� üũ ����
			String ctnt	 = "";
			String evnt_no = "";
			String cupn_clss = "";
			String code_no = "";
			String norm_amt ="0";
			String dc_amt = "0"; //���αݾ�

			sum		 = parser.getParameter("realPayAmt", "0");	// �����ݾ�(����ݾ�)
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
				norm_amt = sum;  //����ݾ�
			}   

			String vipCardCheckYn = "N";
			
			debug("GolfMemInsActn ȸ����� ���� ���� ���� / memGrade : " + memGrade + " / intMemGrade : " + intMemGrade + " / anl_fee : " + anl_fee 
					+ " / sum : " + sum + " / norm_amt : " + norm_amt);

			//�����̿��ڵ�� ���� 10% �Է�
			if (!"".equals(code))
			{
				DbTaoResult codeCheck = proc.codeExecute(context, dataSet, request);
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
							
							if("Y".equals(vipCardYn) && sale_amt == 25000 && "15000".equals(norm_amt))
							{
								sttl_amt = 0;
								vipCardCheckYn = "Y";
							}	
							
							sum = String.valueOf(sttl_amt) ; //�ǰ����ݾ�
							debug("��������ݾ�="+String.valueOf(sale_amt)+",�ǰ����ݾ�="+sum+",���αݾ�="+dc_amt);
						}
						couponYN= "Y";

					}
					
				}

			}

			dataSet.setString("CODE_NO", code_no); 		//������ȣ
			dataSet.setString("JOIN_CHNL", joinChnl);

			debug("332 : join_chnl : " + joinChnl);
			dataSet.setString("CUPN_CTNT", ctnt);
			dataSet.setString("CUPN_NO", code_no); 		//�ſ�ī���ȣ�� �Է��Ұ���.
			dataSet.setString("NORM_AMT", norm_amt); 	//������ 
			dataSet.setString("DC_AMT", dc_amt);		//���αݾ�
			dataSet.setString("STTL_AMT", sum); 		//�����ݾ�
			dataSet.setString("CUPN_CLSS", cupn_clss); 	//��������
			dataSet.setString("CODE_EVNT_NO", evnt_no); //�����̺�Ʈ��ȣ

			

			//�����̿��ڵ� üũ ����
			
			if(intMemGrade==4 || tmYn.equals("Y")){	
				// �Ϲ�ȸ���� ���� ó�� ���� �ʰ� �Ѿ�� �Ѵ�.
				//dataSet.setString("CODE_NO", code_no);
				//dataSet.setString("JOIN_CHNL", joinChnl);
				payResult = true;	
				addResult = 1;
			
			//���������̿��
			}else if( cupn_clss.equals("02")){	
				// ���������̿���� ���� ó�� ���� �ʰ� �Ѿ�� �Ѵ�.
				String sttl_gds_clss = GolfUtil.lpad(moneyType+"", 4, "0"); 
				
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_MTHD_CLSS", "1001");
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", "");
				dataSet.setString("CARD_NO", code_no); //������ȣ
				dataSet.setString("VALD_DATE", "");
				dataSet.setString("INS_MCNT", "");
				dataSet.setString("AUTH_NO","");

				payResult = true;	
				addResult = 0;
			}else{
				
				//debug("// STEP 1. �Է°��� ���� ����üũ+");
				String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
				if ( st_s == null ) st_s = "";
				request.getSession().removeAttribute("ParameterManipulationProtectKey");
	
				String st_p = request.getParameter("ParameterManipulationProtectKey");
				if ( st_p == null ) st_p = "";
	
				if ( !st_p.equals(st_s) ) {
					MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
				//	throw new EtaxException(msgEtt);
				}				
				
				String merMgmtNo = "";
				
				if ( moneyType.equals(AppConfig.getDataCodeProp("0005CODE11")) ){
					merMgmtNo = AppConfig.getDataCodeProp("MBCDHD6");//����Ʈ ��ȸ�� ��������ȣ				
				}else {
					merMgmtNo = AppConfig.getAppProperty("MBCDHD");; //����� ��ȸ�� ��������ȣ
				}
				
				// ���� 
				String ip = request.getRemoteAddr();
				String iniplug = parser.getParameter("KVPpluginData", "");	// ISP ������
	
				String cardNo		= parser.getParameter("card_no", "0");				// ī���ȣ
				String insTerm		= parser.getParameter("ins_term", "00");			// �Һΰ�����
				String siteType		= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
				
				//debug("// STEP 1_2. �Ķ���� �Է�");
				HashMap kvpMap = null;
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
				
				
				//VIPī�� ��������
				debug("## GolfMemInsActn | VIP���� �������� ���� | userId : "+userId+" | vipCardYn : "+vipCardYn+" | ����ȸ������ : "+moneyType);
				
				
				// ��ī�� �Ǵ� ��ī��+����Ʈ ������ ��� 
				if(payType.equals("1") || payType.equals("2")){
					
					if(iniplug !=null && !"".equals(iniplug)) {
						kvpMap = payProc.getKvpParameter( iniplug );
					}	
					
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
						ispCardNo = parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
					}
					
					if ( valdlim.length() == 6 ) {
						valdlim = valdlim.substring(2);											
					}			
					
					//vipCardYn = "Y";
					//moneyType = "3";
					
					//VIPī�� ����
										
					if("Y".equals(vipCardYn) && "3".equals(moneyType))
					{
						debug("## GolfMemInsActn | VIPī�� �������̸鼭 Gold �����õ� | VIPī�����ù������� �õ� | userId : "+userId+" | ispCardNo : "+ispCardNo);
						
						
						GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);	
						if(mbr != null)
						{
							
							List cardVipList = mbr.getCardVipInfoList();
							
							if( cardVipList!=null && cardVipList.size() > 0 )
							{
								for (int i = 0; i < cardVipList.size(); i++) 
								{
									CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
									
									String userVipCardNo 		= (String)record.getCardNo(); 
									
									debug("## VIPī�� �� | userVipCardNo : "+userVipCardNo+" = ispCardNo : "+ispCardNo);
									
									if(userVipCardNo.equals(ispCardNo))
									{
										vipCardCheckYn = "Y";
									}
									
									
								}
								
								//���� �׽�Ʈ
								//vipCardCheckYn = "Y";
								
								debug("## vipCardCheckYn : "+vipCardCheckYn);
								
								if("Y".equals(vipCardCheckYn))
								{
									debug("## GolfMemInsActn | VIPī�� �������̸鼭 Gold �����õ� | �����õ�");
									//debug("// STEP 5. ����ó��");
									payEtt.setMerMgmtNo(merMgmtNo);
									payEtt.setCardNo(ispCardNo);
									payEtt.setValid(valdlim);			
									payEtt.setAmount(sum);
									payEtt.setInsTerm(insTerm);
									payEtt.setRemoteAddr(ip);				 
						
									if( "211.181.255.40".equals(host_ip)) {
										payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
									} else {
										payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
									}
									
									if("211.181.255.40".equals(host_ip)) {
										dataSet.setString("AUTH_NO", payEtt.getUseNo());
									} else {
										dataSet.setString("AUTH_NO", payEtt.getUseNo());
									}

									debug("## STTL_MINS_NM : BCPTī��");
									dataSet.setString("STTL_MINS_NM", "BCPTī��");	// �ſ�ī�� �̸�(������ü �����̸�)		
									
									
								}
								else
								{
									debug("## GolfMemInsActn | VIPī�� �������̸鼭 Gold �����õ������� VIPī�尡 �ƴ� | ��������");
									payResult = false;
									payEtt.setMerMgmtNo(merMgmtNo);
									payEtt.setCardNo(ispCardNo);
									payEtt.setValid(valdlim);			
									payEtt.setAmount(sum);
									payEtt.setInsTerm(insTerm);
									payEtt.setRemoteAddr(ip);	
									dataSet.setString("AUTH_NO", "");
									debug("## STTL_MINS_NM : BCPTī��");
									dataSet.setString("STTL_MINS_NM", "BCPTī��");
									
									veriResCode = "3";
									
								}
								
								
							}
							else
							{
								debug("## GolfMemInsActn | VIPī�� �������̸鼭 Gold �����õ������� VIPī�峻���� ���� | ��������");
								payResult = false;
								payEtt.setMerMgmtNo(merMgmtNo);
								payEtt.setCardNo(ispCardNo);
								payEtt.setValid(valdlim);			
								payEtt.setAmount(sum);
								payEtt.setInsTerm(insTerm);
								payEtt.setRemoteAddr(ip);	
								dataSet.setString("AUTH_NO", "");
								debug("## STTL_MINS_NM : BCPTī��");
								dataSet.setString("STTL_MINS_NM", "BCPTī��");
								
								veriResCode = "3";
							}
							
							
							
						}
						else
						{
							debug("## GolfMemInsActn | VIPī�� �������̸鼭 Gold �����õ������� VIPī�峻���� ���� | ��������");
							payResult = false;
							payEtt.setMerMgmtNo(merMgmtNo);
							payEtt.setCardNo(ispCardNo);
							payEtt.setValid(valdlim);			
							payEtt.setAmount(sum);
							payEtt.setInsTerm(insTerm);
							payEtt.setRemoteAddr(ip);	
							dataSet.setString("AUTH_NO", "");
							debug("## STTL_MINS_NM : BCPTī��");
							dataSet.setString("STTL_MINS_NM", "BCPTī��");
							
							veriResCode = "3";
						}			
						
											
						
					}
					else
					{
						//debug("// STEP 5. ����ó��");
						payEtt.setMerMgmtNo(merMgmtNo);
						payEtt.setCardNo(ispCardNo);
						payEtt.setValid(valdlim);			
						payEtt.setAmount(sum);
						payEtt.setInsTerm(insTerm);
						payEtt.setRemoteAddr(ip);				 
			
						if( "211.181.255.40".equals(host_ip)) {
							payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
						} else {
							payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
						}
						
						if("211.181.255.40".equals(host_ip)) {
							dataSet.setString("AUTH_NO", payEtt.getUseNo());
						} else {
							dataSet.setString("AUTH_NO", payEtt.getUseNo());
						}

						debug("## STTL_MINS_NM : ��ī��");
						dataSet.setString("STTL_MINS_NM", "��ī��");	// �ſ�ī�� �̸�(������ü �����̸�)					
						
					}
					

					
					
					  	  
				
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
				
				// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::				
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());

				debug("�������� ���� ���� => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
						 + " / sum : " + sum + " / insTerm : " + insTerm + " / ip : " + ip);
	
			}
			
			int sttlGdsSeq = 0;

			// 04.����ó�� - ���� ���� �Ϸ�
			if (payResult) {				
				
				if ( moneyType.equals(AppConfig.getDataCodeProp("0005CODE11")) ){ //��ȸ�� (����Ʈ���) �ش�
					
					GolfMemMonthJoinDaoProc monthProc = (GolfMemMonthJoinDaoProc)context.getProc("GolfMemMonthJoinDaoProc");
					addResult = addResult + monthProc.execute(context, dataSet, request);
										
					sttlGdsSeq = monthProc.getSeq(context, usrEntity.getAccount());				    
					
					debug(" ## sttlGdsSeq : "+sttlGdsSeq);
					
				    dataSet.setString("STTL_GDS_SEQ_NO", Integer.toString(sttlGdsSeq)); 					
					
					if ( addResult > 0){
						monPaySuccYn = true;
						payWayTxt = "��ȸ��";
					}
					
				}else {//����� �ش�				
					
					addResult = addResult + proc.execute(context, dataSet, request);
					yearPaySuccYn = true;
					
				}

				debug("�����ڷ� �Է� / addResult : " + addResult + " / intMemGrade : " + intMemGrade + " / tmYn : " + tmYn +"/ anl_fee : " + anl_fee);
				
				if (addResult == 1 && (anl_fee>0 || monPaySuccYn ) && !tmYn.equals("Y")) {
					addResult = addResult + addPayProc.execute(context, dataSet);
					
					debug("addResult : " + addResult);

					//�������� �ڷ� ����
					if ( "Y".equals(couponYN))	{
						int couponResult = proc.couponUpExecute(context, dataSet); 
						if (couponResult != 1) {
							debug("�������� ����");
						}

					}

				}
				
				debug("����ó�� - ���� ���� �Ϸ� /monPaySuccYn : " + monPaySuccYn + " / yearPaySuccYn : " + yearPaySuccYn +" / moneyType : " + moneyType + " / addResult : " + addResult );
				
			}else{	// �������н� ���� ���� 2009.11.27 
				
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);
				veriResCode = "3";
				debug("�������г���������  result_fail : " + result_fail);						
				
			}
   	
	        if(openerType.equals("N")){
	        	// ����ȸ������ ���巹�̵� �ϴ� �˾����� ���� 
	        	returnUrlTrue = "GolfMemJoinEndPop.do";
	        	//returnUrlFalse =  "GolfMemJoinEndPop.do";
	        	returnUrlFalse =  "";
	        	if(payType.equals("3")){ // Ÿ��ī����� 
		        	script = "parent.location.href='/app/golfloung/html/common/bcgolf_service_join.jsp'; window.close();";	        		
	        	}
	        	else{	// ��ī�� �Ǵ� ���հ��� // ���������� > ����ȸ����ȯ  
	        		if(formtarget.equals("White")){
	        			script = "opener.parent.location.href='/app/golfloung/html/common/bcgolf_service_join.jsp'; window.close();";	 
	        		}else{
	        			script = "opener.parent.location.href='/app/golfloung/html/common/bcgolf_service_join.jsp'; window.close();";
	        		}
	        	}
	        	scriptFalse = "window.close();";    	

	        }else{
	        	returnUrlTrue = "GolfMemJoinEnd.do";
	        	returnUrlFalse =  "GolfMemJoinNoCard.do";
	        	script = "parent.location.href='/app/golfloung/html/common/member_join_finish.jsp'";
	        	scriptFalse = "";   
	        }

	        //VIPī�� ��ȣ üũ
	        debug("script: " + script + " / openerType : " + openerType + " / returnUrlTrue : " + returnUrlTrue);    
	        
	        if("N".equals(vipCardCheckYn) && "Y".equals(vipCardYn) && "3".equals(moneyType))
	        {
	        	debug("## VIP���� ���");
	        	veriResCode = "3";
	        	request.setAttribute("script", "");
	        	if(openerType.equals("N")){
	        		request.setAttribute("returnUrl", "GolfMemJoinPop.do");
	        	}
	        	else
	        	{
	        		script = "parent.location.href='/app/golfloung/html/member/membership_guide/member_gold.jsp'";
	        		request.setAttribute("returnUrl", "GolfMemJoinNoCard.do");
	        		request.setAttribute("script", script);
	        	}
	        	
	        		        	
	        	request.setAttribute("resultMsg", "VIP������ BC VIPī��� �����ϼž� �մϴ�. �÷�Ƽ��ī��� �ٽ� �õ��Ͽ��ֽʽÿ�. ");	
	        	
	        }
	        else
	        { 
	        	if (addResult == 2) {
					
	        		/*����� �����ÿ��� ���� ���� ����   
	        		 * ������ ���� ȸ�� �з��� ������� �׻� �켱 �����̹Ƿ� ��ȸ�� �����ÿ��� ���� ���� ���� �ʴ´�.
	        		 */
	        		if (yearPaySuccYn){ 
	        			
						//ī��ȸ������ �����ȸ������
						if("���Ǿ����÷�Ƽ��".equals(strMem))
						{
							usrEntity.setIntMemberGrade((int)intMemGrade);
							if((int)intMemGrade < 2)
							{				
								usrEntity.setIntMemGrade((int)intMemGrade);
							}
							usrEntity.setCyberMoney(0);
						}
						else
						{
							usrEntity.setMemGrade(memGrade);
							usrEntity.setIntMemberGrade((int)intMemGrade);
							usrEntity.setIntMemGrade((int)intMemGrade);
							usrEntity.setCyberMoney(0);
						}
						
	        		}	        		

					if (email_id != null && !email_id.equals("")) {

						String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						try {
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailTitle = "[Golf Loun.G] ��������� ���� ������ ���ϵ帳�ϴ�.";
							emailFileNm = "/email_tpl19.html";
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							//sender.send(emailEtt);
						} catch(Throwable t) {}
					}
					
					//sms�߼�
					if (!userMobile.equals("")
							&& (intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE1")) 
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE2")) 
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE3"))  
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE5")) 
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE6"))  
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE11")) ) 							
							&& !tmYn.equals("Y")) {

						// SMS ���� ����
						try {
							HashMap smsMap = new HashMap();
							
							smsMap.put("ip", request.getRemoteAddr());
							smsMap.put("sName", userNm);
							smsMap.put("sPhone1", userMobile1);
							smsMap.put("sPhone2", userMobile2);
							smsMap.put("sPhone3", userMobile3);
							
	
							//debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
							String smsClss = "674";
							String message = "[Golf Loun.G] "+userNm+"�� "+memGrade+"ȸ�� " + payWayTxt + " " +GolfUtil.comma(sum)+"�� �����Ǿ����ϴ�. �����մϴ�.";
	
							if (joinChnl.equals("2000")) {
								message = "[Golf Loun.G] "+userNm+"�� "+memGrade+"ȸ�� �����̿����� ���ԵǼ̽��ϴ�.�����մϴ�.";
							} else if (joinChnl.equals("1000")) {
								if (sale_amt > 0 )
								{
									message = "[Golf Loun.G] "+userNm+"�� "+memGrade+"ȸ�� " + payWayTxt + " " +GolfUtil.comma(sum)+"�� "+String.valueOf(sale_amt)+"% ���� �����Ǿ����ϴ�.�����մϴ�.";
								}
								
							} else {
								message = "[Golf Loun.G] "+userNm+"�� "+memGrade+"ȸ�� " + payWayTxt + " " +GolfUtil.comma(sum)+"�� �����Ǿ����ϴ�.�����մϴ�.";								
							}
							
							SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
							String smsRtn = "";
							smsRtn = smsProc.send(smsClss, smsMap, message);
							
						} catch(Throwable t) {}
					}	
					
					//interpark�̺�Ʈ ó��
					String isInterpark = (String)request.getSession().getAttribute("isInterpark");
					String currDate  = DateUtil.currdate("yyyyMMdd");
					debug("isInterpark>>>>>>>>>>>>>>>>>>>>>>>" + isInterpark);
					if(isInterpark == null){
						isInterpark = "N";
					}
		
					if(isInterpark.equals("Y") ){	
						if( intMemGrade<4 ){		//����ȸ���ϰ�츸		
							debug("##################################������ũ �̺�Ʈ ó�� ����##############################");

							String from_date = "";
							String to_date   = "";
							String cupn      = ""; 

							GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
							DbTaoResult evntInterpark = (DbTaoResult) inter.eventDateCheck(context, request, dataSet);
							
							if (evntInterpark != null && evntInterpark.isNext()) {
								evntInterpark.first(); 
								evntInterpark.next();
								if(evntInterpark.getString("RESULT").equals("00")){
									from_date = evntInterpark.getString("FROM_DATE");
									to_date = evntInterpark.getString("TO_DATE");
									debug("from_date ~ to_date >>>>>>>>>>>>>>>" + from_date + "~" + to_date + ",���糯¥ : "+ currDate);
								}
							}

							if((Integer.parseInt(from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(to_date))){
								debug("�Ⱓ���� ���� �̺�Ʈ ������");
								boolean doUpdate = false;
								int cnt = 1;
								synchronized(this) {	// ���� ���� �߻��� ���� max �� �����°� ����
									DbTaoResult cupnInterpark = (DbTaoResult) inter.cupnNumber(context, request, dataSet);
									if (cupnInterpark != null && cupnInterpark.isNext()) {
										cupnInterpark.first();
										cupnInterpark.next();
										if(cupnInterpark.getString("RESULT").equals("00")){
											cupn = cupnInterpark.getString("CUPN");
											debug("������ȣ >>>>>>>>>>>>>>>>>>>>>>>>>" + cupn);
										}else if(cupnInterpark.getString("RESULT").equals("01")){
											request.getSession().removeAttribute("isInterpark");
											request.setAttribute("script", script);
											request.setAttribute("returnUrl", returnUrlTrue);
											return super.getActionResponse(context, subpage_key);
										}
									}
									dataSet.setString("email"	, email_id);
									dataSet.setString("socid"	, usrEntity.getSocid());
									dataSet.setString("cupn"	, cupn);
									dataSet.setString("userNm"	, userNm);
									dataSet.setString("ea_info"	, "");

									cnt = (int)inter.getDplCheck(context, request, dataSet);

									//Thread.sleep(5000);

									if(cnt == 0){
										debug("������ũ �̺�Ʈ���� �μ�Ʈ or ������Ʈ");
										doUpdate = (boolean) inter.insertCupnNumber(context, request, dataSet);
									}else{
										request.getSession().removeAttribute("isInterpark");
									}
								}	// synchronized

								if(cnt == 0){
									try {
										EmailSend sender = new EmailSend();
										EmailEntity emailEtt = new EmailEntity("EUC_KR");
										String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
										String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										String emailTitle = "";
										String emailFileNm = "";
										String useYN = "N";
	
										if(doUpdate == true){	
											useYN = (String) inter.getUseYN(context, request, dataSet);
	
											request.setAttribute("useYN", useYN);
											debug("useYN>>>>>>>>>>>>>>>>>>>>>" + useYN);
											emailTitle = "��������� ȸ������ ������ũ ��������";
	
											if(useYN.equals("Y")){
												emailFileNm = "/email_interpark1.html";
											}else{
												emailFileNm = "/email_interpark.html";
											}
					
											emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
											emailEtt.setFrom(emailAdmin);
											emailEtt.setSubject(emailTitle);
											emailEtt.setTo(email_id);
											sender.send(emailEtt);
										}
									} catch(Throwable t) {}
								}

							} else {	// if
								request.getSession().removeAttribute("isInterpark");
							}
						} else {
							request.getSession().removeAttribute("isInterpark");
						}
					}
					// interpark �̺�Ʈ ó�� ����

					debug("746. joinChnl : " + joinChnl + " / code : " + code + " / email_id : " + email_id + " / currDate : " + currDate); 
					if((Integer.parseInt("20100501") <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt("20100930"))){
						if(joinChnl.equals("1000") && ("EVENTGL12345".equals(code.toString().toUpperCase()) || "EVENTECHAMP201007".equals(code.toString().toUpperCase()) || "EVENTLETTER08".equals(code.toString().toUpperCase()))){
							debug("### DM �̺�Ʈ ȸ������ ��ȭ�����ٿ�ε� ���� �߼� ##" + currDate);
		
							try{
								EmailSend sender = new EmailSend();
								EmailEntity emailEtt = new EmailEntity("EUC_KR");
								String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
								String imgPath = "<img src=\"";
								String hrefPath = "<a href=\"";
								String emailTitle = "";
								String emailFileNm = "";
		
								emailTitle = "[Golf Loun.G] ��������� ��ȭ���ű�";
								emailFileNm = "/eamil_tm_movie.html";
								emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, "");
								
								emailEtt.setFrom(emailAdmin);
								emailEtt.setSubject(emailTitle);
								emailEtt.setTo(email_id);
								sender.send(emailEtt);
								
								debug("�������� ���� : emailTitle : " + emailTitle + " / email_id : " + email_id);
							}catch(Exception e){
								debug("���� ���� ���� : email_id : " + email_id);
							}
						}
					}
				
					request.setAttribute("script", script);
					request.setAttribute("returnUrl", returnUrlTrue);
					//request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�."); 
					
					
		        } else if (addResult == 9) { //�ѹ� �� üũ��
		        	// DB���� ���н� ������� ����	        	
					debug("====================GolfMemInsActn =============DB���� ���н� ������� ���� 1 ");
					
					veriResCode = "3";					
					
					// ��ī�� �Ǵ� ���հ����� ���
					if(payType.equals("1") || payType.equals("2")){
			        	if(!GolfUtil.empty(payEtt.getUseNo())){
			        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
			        	}					
					}
					// Ÿ��ī��(�þ�����)�� ���
					else if(payType.equals("3") && payResult){
					  payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);	
					  
					  dataSet.setString("STTL_STAT_CLSS", "Y");	// Y:�������, N:�������ο�û
						  
					  // ���� ��� ����
					  if (payCancelResult) {
						  result = addPayProc.execute(context, dataSet);											
					  }else{	// ���� ��� ����
						  result = addPayProc.failExecute(context, dataSet, request, payEtt);													
					  }								  
					}

					request.setAttribute("script", scriptFalse);
		        	request.setAttribute("returnUrl", returnUrlFalse);
					request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");	
		        } else {
		        	// DB���� ���н� ������� ���� 
					debug("====================GolfMemInsActn =============DB���� ���н� ������� ���� 2 ");
					
					veriResCode = "3";
					
					// ��ī�� �Ǵ� ���հ����� ���
					if(payType.equals("1") || payType.equals("2")){									
			        	if(!GolfUtil.empty(payEtt.getUseNo())){
			        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
			        	}
					}
					// Ÿ��ī��(�þ�����)�� ���
					else if(payType.equals("3") && payResult){
					  payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		
					  
					  dataSet.setString("STTL_STAT_CLSS", "Y");	// Y:�������, N:�������ο�û
					  
					  if (payCancelResult) {	// ���� ��� ����
						  result = addPayProc.execute(context, dataSet);										
					  }else{	// ���� ��� ����
						  result = addPayProc.failExecute(context, dataSet, request, payEtt);															
					  }								  
					}
					
					debug("������Ҽ������� result : " + result);					
					  				
					request.setAttribute("script", scriptFalse);
		        	request.setAttribute("returnUrl", returnUrlFalse);

		        	if (!yearPaySuccYn){
		        		if (!monPaySuccYn){						
							request.setAttribute("resultMsg", "�̹� ��ȸ���� ���� �Ǽ̽��ϴ�.");
		        		}
					}else{
						request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");
					}
							        		
		        }	
	        }
	        			
			// 05. Return �� ����			
			paramMap.remove("moneyNo");
			paramMap.remove("YR");
			paramMap.remove("MN");
			paramMap.remove("GRD_NM");
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
			
		} finally {
			
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
			
			/*����� �������� �ش� �ȵ�; ��, ��ȸ�� ������������ �ش�
			 * ���� �� ��������� ����Ͻ��� DAOó���� Exception�� �߻��ϸ� �� ������� ������ ��ġ�� ����
			 * ������� ���������ϰ� ������, �켱 ���� �߰��Ǵ� ��ȸ���� ���� Exception�� ó��
			 * �ٺ������� �ذ��ϱ� ���ؼ� ������ ���� �����ؾ���
			 */
			if (!yearPaySuccYn){
				
				if (!monPaySuccYn){
					
					veriResCode = "3";
					
					// ��ī�� ; ��ȸ�� ������ ��ī�� �������ܸ� ����
					if(payType.equals("1") ){
			        	if(!GolfUtil.empty(payEtt.getUseNo())){
			        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
			        	}				
					}
					
					request.setAttribute("script", scriptFalse);
		        	request.setAttribute("returnUrl", returnUrlFalse);
					request.setAttribute("resultMsg", "��ȸ�� ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");
					
				}
				
			}
			
			if(ispAccessYn.equals("Y")){
			
				//ISP���� �α� ���
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", userNm);
				hmap.put("memSocid", userSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", userIP);
				hmap.put("className", "GolfMemInsActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
			
			}
			
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
