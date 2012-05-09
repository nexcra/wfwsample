/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemExtendInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���������� > ����ȸ�� �Ⱓ ���� ó��
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.14    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;


/******************************************************************************
* Golf
* @author	�̵������  
* @version	1.0 
******************************************************************************/
public class GolfMemExtendInsActn extends GolfActn{
	
	public static final String TITLE = "���������� > ����ȸ�� �Ⱓ ����ó��"; 

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";

		// ��ó�� ���� ����
		boolean payResult = false;
		boolean payCancelResult = false;
		int addResult = 0;

    	String script = ""; 
    	String strMem = "";
    	int intMemGrade = 0;
		int resultPresent = 0;
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)	
		String memName = "";
		String memSocid = "";	
		String ispCardNo = "";	// ispī���ȣ
		String ip = request.getRemoteAddr();		

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

		try {
			// 01.��������üũ 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				strMem 		= (String)usrEntity.getMemGrade();
				intMemGrade = (int)usrEntity.getIntMemGrade();
				memName 	= (String)usrEntity.getName();			//����
				memSocid 	= (String)usrEntity.getSocid();			//�ֹε�Ϲ�ȣ				
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String payType 				= parser.getParameter("payType", "").trim();	// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			String moneyType 			= parser.getParameter("moneyType", "").trim();	
			// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(����)
			String memType 				= parser.getParameter("memType", "").trim();	// ȸ������ - ��ȸ�� : 1 ��ȸ��:2
			String insType				= parser.getParameter("insType", "").trim();	// ���԰�� - TM : 1 �Ϲ� : ""
			String realPayAmt			= parser.getParameter("realPayAmt", "").trim();	// �ǰ����ݾ�
			String tmYn					= parser.getParameter("tmYn", "").trim();		// Y:TM ��
			String payWay				= parser.getParameter("payWay", "").trim();		// yr:��ȸ��, mn:��ȸ��
			String code					= parser.getParameter("code", "").trim();		//���ޱ����ڵ�
			String openerType			= parser.getParameter("openerType", "");		// N:���׷��̵� ȸ��
			String fromUrl				= parser.getParameter("fromUrl", "");		// N:���׷��̵� ȸ��

			//-- 2009.11.12 �߰�  ����, ����ǰ
			String cupn_type 			= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 		= parser.getParameter("pmgds_pym_yn", "");

			String gds_code 			= parser.getParameter("gds_code", "").trim();
			String rcvr_nm 				= parser.getParameter("rcvr_nm", "").trim();
			String zp1 					= parser.getParameter("zp1", "").trim();
			String zp2 					= parser.getParameter("zp2", "").trim();
			String addr 				= parser.getParameter("addr", "").trim();
			String dtl_addr				= parser.getParameter("dtl_addr", "").trim();
			String addr_clss			= parser.getParameter("addr_clss", "").trim();
			String hp_ddd_no 			= parser.getParameter("hp_ddd_no", "").trim();
			String hp_tel_hno 			= parser.getParameter("hp_tel_hno", "").trim();
			String hp_tel_sno 			= parser.getParameter("hp_tel_sno", "").trim();
			
			String sum		 			= parser.getParameter("realPayAmt", "0");	// �����ݾ�
			int	sale_amt 				= 0;			// ���ϱݾ�
			String joinChnl				= "0001";		// ���԰��
			String couponYN 			= "N";			// ������뿩��
			
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

			debug("::golfmemExtendIns:: sum : " + sum + " / payType : " + payType + " / moneyType : " + moneyType + " / memType : " + memType + " / insType : " + insType
					+ " / openerType : " + openerType + " / realPayAmt : " + realPayAmt + " / cupn_type : " + cupn_type
					+ " / pmgds_pym_yn : " + pmgds_pym_yn + " / tmYn : " + tmYn + " / payWay : " + payWay + " / intMemGrade : " + intMemGrade);
			

			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");		// �������� ����
			GolfMemInsDaoProc proc_mem = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");					// ȸ������
			GolfMemExtendInsDaoProc proc = (GolfMemExtendInsDaoProc)context.getProc("GolfMemExtendInsDaoProc");		// ȸ������ ����
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// ���� ���̺� ���� ��� ���� 
			String order_no = parser.getParameter("allat_order_no", "").trim();
			dataSet.setString("ORDER_NO", order_no);	
			//debug("�ֹ���ȣ========> " + order_no);			
			

			dataSet.setString("CODE", code); //�����ڵ�  
			dataSet.setString("SITE_CLSS", "10");//����Ʈ�����ڵ� 10:���������
			dataSet.setString("EVNT_NO", "111");//�̺�Ʈ��ȣ1
			dataSet.setString("EVNT_NO2", "112");//�̺�Ʈ��ȣ2 
			dataSet.setString("CUPN_TYPE", cupn_type);//�������� 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);//��ǰ���޿��� 

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

			// ��������
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
			
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("payWay", payWay);
				
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
				//debug("=========result : " + result);
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
			
			
			// ����
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// ������ ��ȣ(766559864) //topn : 745300778
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
			if("7".equals(moneyType)){
				sttl_gds_clss = "0008";		// ����޿� ���� ���� ��ǰ�����ڵ�, �������� �Ӵ�Ÿ�԰� ��ġ
			}

			//debug("// STEP 1_3. ������������ ���� ��� ��ȿ�� �˻�..");
			
			// 04.���� ���̺�(Proc) ��ȸ  			
			if(intMemGrade==4 || tmYn.equals("Y")){	
				// �Ϲ�ȸ���� ���� ó�� ���� �ʰ� �Ѿ�� �Ѵ�.
				//dataSet.setString("CODE_NO", code_no);
				//dataSet.setString("JOIN_CHNL", joinChnl);
				payResult = true;	
				addResult = 1;
			
			//���������̿��
			}else if( cupn_clss.equals("02")){	
				// ���������̿���� ���� ó�� ���� �ʰ� �Ѿ�� �Ѵ�.
				
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
	
					  dataSet.setString("STTL_MINS_NM", "��ī��");	// �ſ�ī�� �̸�(������ü �����̸�)						  
				
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
				dataSet.setString("STTL_AMT", sum); //�����ݾ�
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
				debug("merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim + " / sum : " + sum);
				debug("insTerm : " + insTerm + " / ip : " + ip);	

			}

			// 04.����ó��	
			// ���� ���� �Ϸ� 
			//payResult = true;
			if (payResult) { 
				addResult = addResult + proc.execute(context, dataSet, request);	// ����ȸ�� �Ⱓ �����ϱ�		

				debug("�����ڷ� �Է� / addResult : " + addResult + " / intMemGrade : " + intMemGrade + " / tmYn : " + tmYn);

				if (addResult == 1) {
					debug("���� ���� ");
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
				debug("�������г���������========> " + result_fail);						
				
			}				 

			// ���� URL ����
			//script = script + "top.opener.location.reload(); top.window.close();";
			script = script + "parent.location.reload();";
			
			debug("script : " + script + " / openerType : " + openerType + " / fromUrl : " + fromUrl);

			if (addResult == 2) {
				// IBK������ ȸ���� ����� ����ǹǷ� ������ �����Ѵ�.
				debug("intMemGrade : " + intMemGrade);
				if(intMemGrade==13){
					usrEntity.setMemGrade("Gold");
					usrEntity.setIntMemGrade(3);
					usrEntity.setIntMemberGrade(3);
					
					script = script + "window.top.document.mForm.isLogin.value='1';\n";
					script = script + "window.top.document.mForm.userNm.value='Gold'\n;";
					script = script + "window.top.document.mForm.memGrade.value='3'\n;";
					script = script + "window.top.document.mForm.intMemGrade.value='3'\n;";
				}

				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "����ȸ�� �Ⱓ ������ ���������� ó�� �Ǿ����ϴ�.");
				
	        } else if (addResult == 9) { //�ѹ� �� üũ��
	        	// DB���� ���н� ������� ����	        	
				debug("=golfmemExtendIns =============DB���� ���н� ������� ���� 1 ");
				
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
					  int result = addPayProc.execute(context, dataSet);
					  debug("=golfmemExtendIns ============= ���� ��� ���� | ���������� : " + result);											
				  }
				  // ���� ��� ����
				  else{
					  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
					  debug("=golfmemExtendIns ============= ���� ��� ���� | ���������� : " + result);											
				  }								  
				}

				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");	
	        } else {
	        	// DB���� ���н� ������� ���� 
				debug("=golfmemExtendIns =============DB���� ���н� ������� ���� 2 ");
				
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
					  int result = addPayProc.execute(context, dataSet);
					  debug("=golfmemExtendIns ============= ���� ��� ���� | ���������� : " + result);											
				  }
				  // ���� ��� ����
				  else{
					  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
					  debug("=golfmemExtendIns ============= ���� ��� ���� | ���������� : " + result);											
				  }								  
				}
				  				
				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����			
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
				hmap.put("memName", memName);
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", ip);
				hmap.put("className", "GolfMemExtendInsActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
