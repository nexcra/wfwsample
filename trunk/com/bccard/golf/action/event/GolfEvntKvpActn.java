/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntKvpActn 
*   �ۼ���	: (��)�̵������ ������
*   ����		: KVP ó��
*   �������	: golf
*   �ۼ�����	: 2010-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.GolfEvntKvpDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntKvpActn extends GolfActn{
	
	public static final String TITLE = "KVP ó��";
	private static final String SITEID = "I829";		// �ѽ��� �ڵ�
	private static final String SITEPW = "44463742";	// �ѽ��� PASSWORD

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();											// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		
		GolfEvntKvpDaoProc proc = (GolfEvntKvpDaoProc)context.getProc("GolfEvntKvpDaoProc");	// KVP ����
				

		try {

			String socid = ""; 			// �ֹι�ȣ
			String social_id_1 = "";
			String social_id_2 = ""; 
			String name = ""; 
			String ddd_no = ""; 
			String tel_hno = ""; 
			String tel_sno = ""; 
			String hp_ddd_no = ""; 
			String hp_tel_hno = ""; 
			String hp_tel_sno = ""; 
			String email = "";  
			String idx = ""; 
			String realPayAmt = "";

			// ��ó��
			String resultMsg = "";
			String script = "";
			boolean payResult = false;
			boolean payCancelResult = false;
			int addResult = 0;
			
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// �⺻ ��ȸ 
			social_id_1 = (String)parser.getParameter("social_id_1").trim();
			social_id_2 = (String)parser.getParameter("social_id_2").trim();
			socid = social_id_1 + social_id_2; 
			name = (String)parser.getParameter("name","").trim(); 
			
			ddd_no = (String)parser.getParameter("ddd_no",""); 
			tel_hno = (String)parser.getParameter("tel_hno",""); 
			tel_sno = (String)parser.getParameter("tel_sno",""); 
			hp_ddd_no = (String)parser.getParameter("hp_ddd_no",""); 
			hp_tel_hno = (String)parser.getParameter("hp_tel_hno",""); 
			hp_tel_sno = (String)parser.getParameter("hp_tel_sno",""); 
			email = (String)parser.getParameter("email",""); 
			idx = (String)parser.getParameter("idx",""); 
			realPayAmt = (String)parser.getParameter("realPayAmt",""); 


			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			String order_no = addPayProc.getOrderNo(context, dataSet);
			debug("order_no : " + order_no);
			
			dataSet.setString("socid", socid);
			dataSet.setString("name", name);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("email", email);
			dataSet.setString("idx", idx);
			dataSet.setString("realPayAmt", realPayAmt);

			
//
//			/***�ֹ��������� end **���� start***********************************************/
//			
//			//debug("// STEP 1. �Է°��� ���� ����üũ+");
//			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
//			if ( st_s == null ) st_s = "";
//			request.getSession().removeAttribute("ParameterManipulationProtectKey");
//
//			String st_p = request.getParameter("ParameterManipulationProtectKey");
//			if ( st_p == null ) st_p = "";
//
//			if ( !st_p.equals(st_s) ) {
//				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
//			//	throw new EtaxException(msgEtt);
//			}				
//
			// �������� ���� => ����
			String payType 			= "1";												// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			String ip 				= request.getRemoteAddr();  
			String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD");				// ������ ��ȣ ��������� ������Ű��	770119761
			String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP ������

			String cardNo			= parser.getParameter("card_no", "0");				// ī���ȣ
			String insTerm			= parser.getParameter("ins_term", "00");			// �Һΰ�����
			String siteType			= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
			
						
			// ��ī�� debug("// STEP 1_2. �Ķ���� �Է�"); 
			HashMap kvpMap = null;
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// ����� ���̵�
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
			String pcg         = "";														// ����/���� ����
			String ispCardNo   = "";														// ispī���ȣ
			String valdlim	   = "";														// ���� ����
			String pid = null;																// ���ξ��̵�
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			
			// �������� ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType, 4, "0");
			String sttl_gds_clss = "9"+GolfUtil.lpad(idx, 3, "0");
//			
//			
//			if(iniplug !=null && !"".equals(iniplug)) {
//				kvpMap = payProc.getKvpParameter( iniplug );
//			}	
//			
//			if(kvpMap != null) {
//				pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
//				ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
//				valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
//				if ( "2".equals(pcg) ) {
//					pid = (String)kvpMap.get("BizId");								// ����ڹ�ȣ
//				} else {
//					pid = (String)kvpMap.get("Pid");									// ���� �ֹι�ȣ
//				}
//			} else {
//				ispCardNo = parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
//			}
//			
//			if ( valdlim.length() == 6 ) {
//				valdlim = valdlim.substring(2);											
//			}					
//
//			//debug("// STEP 5. ����ó��");
//			payEtt.setMerMgmtNo(merMgmtNo);
//			payEtt.setCardNo(ispCardNo);
//			payEtt.setValid(valdlim);			
//			payEtt.setAmount(realPayAmt);
//			payEtt.setInsTerm(insTerm);
//			payEtt.setRemoteAddr(ip);				 
//
//			payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
			dataSet.setString("AUTH_NO", payEtt.getUseNo());
			dataSet.setString("STTL_MINS_NM", "��ī��");	// �ſ�ī�� �̸�(������ü �����̸�)						  

			
			// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			dataSet.setString("ORDER_NO", order_no);
			dataSet.setString("CDHD_ID", socid);
			dataSet.setString("STTL_AMT", realPayAmt);				// ���� �ݾ�
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_GDS_SEQ_NO", idx);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			debug("�������� ���� ���� => merMgmtNo(��������ȣ) : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip + " / idx : " + idx);

			payResult = true;	// �׽�Ʈ
			if (payResult) {
				addResult = proc.execute(context, request, dataSet);		
				debug("GolfEvntBnstPayActn : �ֹ� ���̺� ������Ʈ = addResult2 : " + addResult);	

				if (addResult > 0) {
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntBnstPayActn : ���� ���� = addResult3 : " + addResult);	
				}
			}else{	// �������н� ���� ���� 2009.11.27 
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				debug("GolfEvntBnstPayActn = result_fail : " + result_fail);
			}

			if(addResult > 1 ){
				resultMsg = "������ �Ϸ� �Ǿ����ϴ�.";
			}else{	
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
	        	}
	        	resultMsg = "������ �����߽��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.";
			}		

			script = "top.window.close();";
			request.setAttribute("resultMsg", resultMsg);
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
