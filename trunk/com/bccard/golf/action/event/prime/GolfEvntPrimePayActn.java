/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntPrimePay
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > �ؿ������� > ����
*   �������  : Golf
*   �ۼ�����  : 2010-08-16
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.11    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeInsDaoProc;
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
public class GolfEvntPrimePayActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ > �ؿ������� > ����";

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
		String memSocid = "";	
		String ispCardNo = "";	// ispī���ȣ
		String cstIP = request.getRemoteAddr(); //����IP		
		
		try { 
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				memName		= (String)usrEntity.getName(); 
				memSocid 	= (String)usrEntity.getSocid(); 
			}

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// ��ó��
			String script = "";
			boolean payResult = false;
			boolean payCancelResult = false;
			int addResult = 0;
				
			// ��û����
			String cdhd_id		= parser.getParameter("cdhd_id","");		// ȸ�����̵�
			String bkg_pe_num	= parser.getParameter("bkg_pe_num", "");	// ����
			String jumin_no		= parser.getParameter("jumin_no", "");		// �ֹε�Ϲ�ȣ
			String hp_ddd_no	= parser.getParameter("hp_ddd_no","");		// ����ó1
			String hp_tel_hno	= parser.getParameter("hp_tel_hno","");		// ����ó2
			String hp_tel_sno	= parser.getParameter("hp_tel_sno", "");	// ����ó3
			String ddd_no		= parser.getParameter("ddd_no","");			// ����ȭ1
			String tel_hno		= parser.getParameter("tel_hno","");		// ����ȭ2
			String tel_sno		= parser.getParameter("tel_sno", "");		// ����ȭ3
			String dtl_addr		= parser.getParameter("dtl_addr","");		// �ּ�
			String lesn_seq_no	= parser.getParameter("lesn_seq_no","");	// ���Ը����
			String pu_date		= parser.getParameter("pu_date","");		// ȸ��������
			pu_date = GolfUtil.replace(pu_date, ".", "");
			String memo_expl	= parser.getParameter("memo_expl","");		// ��Ÿ ��û ����
			
			// ��������
			String realPayAmt	= parser.getParameter("realPayAmt", "0");		// ���� �ݾ�
			String order_no		= parser.getParameter("order_no", "0");			// �ֹ���ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("bkg_pe_num", bkg_pe_num);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);			
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);			
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("dtl_addr", dtl_addr);
			dataSet.setString("lesn_seq_no", lesn_seq_no);
			dataSet.setString("pu_date", pu_date);
			dataSet.setString("memo_expl", memo_expl);
			
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("order_no", order_no);		

			// 04.���� ���̺�(Proc) ��ȸ
			
			// �ֹ����� ����
			GolfEvntPrimeInsDaoProc proc = (GolfEvntPrimeInsDaoProc)context.getProc("GolfEvntPrimeInsDaoProc");
			
			
			/***�ֹ��������� end **���� start***********************************************/
			
			//debug("// STEP 1. �Է°��� ���� ����üũ+");
			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
			if ( st_s == null ) st_s = "";
			request.getSession().removeAttribute("ParameterManipulationProtectKey");

			String st_p = request.getParameter("ParameterManipulationProtectKey");
			if ( st_p == null ) st_p = "";

			if ( !st_p.equals(st_s) ) {
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
			}				

			// �������� ���� => ����
			String payType 			= parser.getParameter("payType", "").trim();		// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			String ip 				= request.getRemoteAddr();  
			String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD5");				// ������ ��ȣ 765943401-��ȸ��MBCDHD5
			String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP ������

			String cardNo			= parser.getParameter("card_no", "0");				// ī���ȣ
			String insTerm			= parser.getParameter("ins_term", "00");			// �Һΰ�����
			String siteType			= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
			
						
			// ��ī�� debug("// STEP 1_2. �Ķ���� �Է�"); 
			HashMap kvpMap = null;
			String user_r      = StrUtil.isNull(parser.getParameter("cdhd_id"),"");			// ����� ���̵�
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
			String pcg         = "";														// ����/���� ����			
			String valdlim	   = "";														// ���� ����
			String pid = null;																// ���ξ��̵�
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			

			// �������� ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
			String sttl_gds_clss = "1003";
			
			
				
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
			
			
			// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			dataSet.setString("CDHD_ID", cdhd_id);
			dataSet.setString("STTL_AMT", realPayAmt);				// ���� �ݾ�
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());
			dataSet.setString("ORDER_NO", order_no);

			debug("�������� ���� ���� => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip);

			if (payResult) {
				// ���� ����
				dataSet.setString("pgrs_yn", "G");
				dataSet.setString("cslt_yn", "1");
				addResult = proc.execute_upd(context, request, dataSet);
				
				if (addResult>0) {
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntPrimePay = addResult(���� ����) : " + addResult);	
				}
			}else{ 
				veriResCode = "3";
				// ���� ���г��� ���� + ��û���� �������з� ����
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				dataSet.setString("pgrs_yn", "F");
				dataSet.setString("cslt_yn", "");
				result_fail += proc.execute_upd(context, request, dataSet);
				debug("GolfEvntPrimePay = result_fail : " + result_fail);											
			}

			if(addResult == 2 ){
				script = "alert('������ �Ϸ� �Ǿ����ϴ�.'); parent.location.reload();";
			}else{
				veriResCode = "3";		
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
	        	}			
				script = "alert('���������� ������ �־����ϴ�. �ٽ� �õ��� �ֽʽÿ�.'); parent.location.reload();";
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
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfEvntPrimePayActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
