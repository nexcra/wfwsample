/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntCouponOrdActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : �̺�Ʈ�����/����������̺�Ʈ/���������̺�Ʈ/�׸�����������->����
*   �������  : Golf
*   �ۼ�����  : 2011-04-12
************************** �����̷� ****************************************************************
*    ����  �ۼ���   �������
20110419  �̰���   ���հ����� �Һα�� ���� �� ���հ��� �Ͻúҽ� 60ó��
***************************************************************************************************/
package com.bccard.golf.action.event.coupon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.coupon.GolfEvntCouponOrdProc;
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
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntCouponOrdActn extends GolfActn{
	
	public static final String TITLE = "���� �ֹ� ����";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)		
		String memName = "";			
		String ispCardNo = "";	// ispī���ȣ
		String cstIP = request.getRemoteAddr(); //����IP
		String pid = null;						// ���ξ��̵�		
		
		try { 

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);			

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				memName		= (String)usrEntity.getName();				
			}else {
				memName = (String)parser.getParameter("userNm","").trim();				
			}
			
			// ��ó��
			String script = "";
			boolean payResult = false;
			boolean payCancelResult = false;
			int addResult = 0;

			// ��ǰ ����
			//String order_no			= parser.getParameter("order_no", "");			// �ֹ��ڵ�
			String qty				= parser.getParameter("qty","");				// ����
			String int_atm			= parser.getParameter("int_atm","");			// ��ǰ�ݾ�
			String realPayAmt		= parser.getParameter("realPayAmt", "0");		// ���� �ݾ�
			
			// ������ - ������
			String juminno1			= parser.getParameter("juminno1","");	
			String juminno2			= parser.getParameter("juminno2","");	
			String userNm			= parser.getParameter("userNm","");	
			String userId			= parser.getParameter("userId","");	
			String mobile1			= parser.getParameter("mobile1","");	
			String mobile2			= parser.getParameter("mobile2","");	
			String mobile3			= parser.getParameter("mobile3","");						
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			String order_no = addPayProc.getOrderNo(context, dataSet); //�ֹ��ڵ� ��������

			dataSet.setString("ORDER_NO", order_no);
			dataSet.setString("qty", qty);
			dataSet.setString("int_atm", int_atm);
			dataSet.setString("realPayAmt", realPayAmt);
			
			dataSet.setString("juminno1", juminno1);
			dataSet.setString("juminno2", juminno2);
			dataSet.setString("socid", juminno1+juminno2);
			dataSet.setString("userId", userId);
			dataSet.setString("userNm", userNm);
			dataSet.setString("mobile1", mobile1);
			dataSet.setString("mobile2", mobile2);
			dataSet.setString("mobile3", mobile3);
			
			/***�ֹ��������� end **���� start***********************************************/
			
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

			// �������� ���� => ����
			String payType 			= parser.getParameter("payType", "").trim();		// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			String ip 				= request.getRemoteAddr();  
			//String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD3");				// ������ ��ȣ 769835680-�����������ǰ
			String merMgmtNo 		= "770119761";										// ������ ��ȣ 770119761 - �׸��� ��������  ���� ��������ȣ - ���������� �Ҵ�
			String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP ������			
			String insTerm			= parser.getParameter("ins_term", "00");			// �Һΰ�����
						
			// ��ī�� debug("// STEP 1_2. �Ķ���� �Է�"); 
			HashMap kvpMap = null;
			String pcg         = "";														// ����/���� ����			
			String valdlim	   = "";														// ���� ����
			
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();


			// �������� ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
			String sttl_gds_clss = "0020";
			
			if (payType.equals("2")){
				insTerm = "60";
			}		
			
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
				payEtt.setAmount(realPayAmt);
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
			
			// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_AMT", realPayAmt);				// ���� �ݾ�
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			debug("�������� ���� ���� => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip);
			
			
			HashMap valMap = new HashMap();
			GolfEvntCouponOrdProc proc = (GolfEvntCouponOrdProc)context.getProc("GolfEvntCouponOrdProc");

			if (payResult) {

				//�����Ҵ�				
				valMap = proc.cupnAlloc(context, request, dataSet);	
				debug("GolfEvntCouponOrdProc = valMap : " + valMap);
				
				addResult = Integer.parseInt(valMap.get("resultCnt").toString());
				debug("GolfEvntCouponOrdProc = addResult : " + addResult);
				
				if (addResult == 1) {
					// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntCouponOrdProc = addResult3 : " + addResult);	
				}					
				
			}else{	// ���� ���� ���н� ���� ����
				
				veriResCode = "3";
				
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				debug("GolfEvntCouponOrdProc = result_fail : " + result_fail);

			}
				
			if(addResult == 2 ){ // �������� ok  &  �����Ҵ� ok &  �����������̺� ���� ok
				
				String str_hp = "0000";
				String hp	  = "";				
				Vector cupnV = new Vector();
				
				if (mobile1.length()>=3 && mobile2.length()>=3 && mobile2.length()>=4 )	{
					if ( mobile2.indexOf(str_hp) ==  -1 ) {
						hp = mobile1 + mobile2 + mobile2;
					}
				}
				
				cupnV = (Vector)valMap.get("couponNo");				

				// SMS ���� ����
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", mobile1);
				smsMap.put("sPhone2", mobile2);
				smsMap.put("sPhone3", mobile3);
				smsMap.put("sCallCenter", "15666578");
				
				String smsClss = "674";				
				String message = "";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = "";
				
				if (hp.length() > 9) {
					
					for (int i=0; i<cupnV.size(); i++){	
						
						message = "[BC���������] �����׸� ��������  ��ȣ : " + cupnV.elementAt(i).toString();
						
						//SMS�߼�
						smsRtn = smsProc.send(smsClss, smsMap, message);
						info("[�����׸� �������� SMS �߼�] �ڵ�����ȣ |" + mobile1 +"-"+  mobile2 +"-"+ mobile3 + "|�޼���|" + message);
						message = "";
						
					}
				}

				info ("�����ֹ���ȣ : " + order_no + "/ �������� ok  &  �����Ҵ� ok &  �����������̺� ���� ok");
				script = "alert('���Ű� �Ϸ� �Ǿ����ϴ�.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
			
				
			}else if(addResult == 1 ){ // �������� ok & �����Ҵ� ok & �����������̺� ���� ����
				
				veriResCode = "3";		
	
				//�����Ҵ� ���	
				proc.cupnAlloCancel(context, request, dataSet, valMap);
				debug("GolfEvntCouponOrdProc = valMap : " + valMap);		

				// ��ī�� �Ǵ� ���հ����� ���
				if(payType.equals("1") || payType.equals("2")){									
		        	if(!GolfUtil.empty(payEtt.getUseNo())){
		        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);	// ������� ȣ��
		        	}
				}
				
				info ("�����ֹ���ȣ : " + order_no + "/ �������� ok & �����Ҵ� ok & �����������̺� ���� ����");
				script = "alert('���� ������ ���� ���ſ� �����߽��ϴ�.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
				
							
			}else{ // �������� ���� 
				
				veriResCode = "3";

				// ��ī�� �Ǵ� ���հ����� ���
				if(payType.equals("1") || payType.equals("2")){									
		        	if(!GolfUtil.empty(payEtt.getUseNo())){
		        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);	// ������� ȣ��
		        	}
				}
				info ("�����ֹ���ȣ : " + order_no + "/ �������� ���� ");
				script = "alert('���� ���� ������ ���� ���ſ� �����߽��ϴ�.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
				
			}			
			
			request.setAttribute("script", script);
			request.setAttribute("paramMap", paramMap);
	        
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
				hmap.put("memSocid", pid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfEvntKvpActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}
		
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
