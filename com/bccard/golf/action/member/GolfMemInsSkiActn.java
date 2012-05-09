/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsSkiActn
*   �ۼ���    : �̵������ ����
*   ����      : ��Ű�̺�Ʈ�� ���
*   �������  : golf 
*   �ۼ�����  : 2009-12-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemSkiInsDaoProc;
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
* @author	�̵������  
* @version	1.0 
******************************************************************************/
public class GolfMemInsSkiActn extends GolfActn{
	
	public static final String TITLE = "��Ű�̺�Ʈ�� ���";  

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

		// ��ó�� ���� ����
		boolean payResult = false;
		boolean payCancelResult = false;
		int addResult = 0;
		int		sale_amt = 0;

		String returnUrlTrue = ""; 
    	String returnUrlFalse =  "";
    	String script = ""; 
    	String scriptFalse = "";
    	String strMem = "";
		String sum = "0";
		String couponYN = "N";
		ResultException re;
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // ��ư
		String goPage = "/app/card/memberActn.do"; // �̵� �׼�
		int intUsrGrad = 0; //�α��������� ���
		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		
		try {
			// 01.��������üũ 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				strMem 		= (String)usrEntity.getMemGrade();
				
				//�α��� ������ ��� setIntMemGrade  1:è�ǿ� / 2:��� / 3.��� / 4.ȭ��Ʈ 5.ī��
				intUsrGrad 	= usrEntity.getIntMemGrade();
				
				System.out.print("## strMem:"+strMem);
			} else {
				// �������� - ����
				re = new ResultException();
				re.setTitleImage("error");
				re.setTitleText(TITLE);
				re.setKey("USERCERT_ERROR");
				re.addButton(goPage, addButton);
				throw re;
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String payType 				= parser.getParameter("payType", "").trim();	// 1:ī�� 2:ī��+����Ʈ
			String moneyType 			= parser.getParameter("moneyType", "").trim();	
			// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(����)
			String memType 				= parser.getParameter("memType", "").trim();	// ȸ������ - ��ȸ�� : 1 ��ȸ��:2
			String insType				= parser.getParameter("insType", "").trim();	// ���԰�� - TM : 1 �Ϲ� : ""
			String openerType			= parser.getParameter("openerType", "").trim();	// N:���׷��̵� ȸ��
			String realPayAmt			= parser.getParameter("realPayAmt", "").trim();	// �ǰ����ݾ�
			String tmYn					= parser.getParameter("tmYn", "").trim();		// Y:TM ��
			String type 				= parser.getParameter("type", "");
			
			String code					= parser.getParameter("code", "").trim();		//���ޱ����ڵ�
			String joinChnl				= "2302";

			//-- 2009.11.12 �߰� 
			String cupn_type 			= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 		= parser.getParameter("pmgds_pym_yn", "");

			debug("=`==`=`=`=`=`=`=`=` ��Ű�̺�Ʈ�� ���� ���� ���� ");
			debug("===================payType : " + payType);
			debug("===================moneyType : " + moneyType);
			debug("===================memType : " + memType);
			debug("===================insType : " + insType);
			debug("===================openerType : " + openerType);
			debug("===================realPayAmt : " + realPayAmt);
			debug("===================cupn_type : " + cupn_type);
			debug("===================pmgds_pym_yn : " + pmgds_pym_yn);
			debug("===================tmYn : " + tmYn);

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("payType", payType);	
			dataSet.setString("moneyType", moneyType);	
			dataSet.setString("memType", memType);	
			dataSet.setString("insType", insType);	
			
			dataSet.setInt("intUsrGrad", intUsrGrad);			//�α��������� ���
						
			dataSet.setString("CODE", code); 					//�����ڵ�  
			dataSet.setString("SITE_CLSS", "10");				//����Ʈ�����ڵ� 10:���������
			dataSet.setString("EVNT_NO", "111");				//�̺�Ʈ��ȣ1
			dataSet.setString("EVNT_NO2", "112");				//�̺�Ʈ��ȣ2 
			dataSet.setString("CUPN_TYPE", cupn_type);			//�������� 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);	//��ǰ���޿��� 

			// 04.���� ���̺�(Proc) ��ȸ  
			GolfMemSkiInsDaoProc procSky = (GolfMemSkiInsDaoProc)context.getProc("GolfMemSkiInsDaoProc");
			
			// ���� ���̺� ���� ��� ���� 
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");

			System.out.print("## userId : "+userId+" | intUsrGrad "+intUsrGrad+" | moneyType: "+moneyType+"\n");
			 
			// ��� ȸ�� ��� �������� 					
			DbTaoResult gradeView = procSky.gradeExecute(context, dataSet, request);
			debug("===================gradeView : " + gradeView);
			if (gradeView != null && gradeView.isNext()) {
				gradeView.first();
				gradeView.next();
				debug("===================memGrade : " + gradeView.getString("RESULT"));
				if(gradeView.getString("RESULT").equals("00")){
					memGrade = (String) gradeView.getString("memGrade");	
					intMemGrade = (int) gradeView.getInt("intMemGrade");	
				}
				
			}
			debug("=`==`=`=`=`=`=`=`=` ȸ����� ���� ���� ���� ");
			debug("===================memGrade : " + memGrade);
			debug("===================intMemGrade : " + intMemGrade);
			
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
			debug("=`==`=`=`=`=`=`=`=` �����ݾ�");
			debug("===================sum : " + sum);
			debug("===================norm_amt : " + norm_amt);

			dataSet.setString("CODE_NO", "SKI"); 			//���޾�ü�ڵ�
			dataSet.setString("JOIN_CHNL", joinChnl);
			dataSet.setString("CUPN_CTNT", ctnt);
			dataSet.setString("CUPN_NO", code_no); 			//�ſ�ī���ȣ�� �Է��Ұ���.
			dataSet.setString("NORM_AMT", norm_amt); 		//������
			dataSet.setString("DC_AMT", dc_amt);			//���αݾ�
			dataSet.setString("STTL_AMT", sum); 			//�����ݾ�
			dataSet.setString("CUPN_CLSS", cupn_clss); 		//��������
			dataSet.setString("CODE_EVNT_NO", evnt_no); 	//�����̺�Ʈ��ȣ
			

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
			String ip = request.getRemoteAddr();  
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// ������ ��ȣ(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP ������

			String cardNo		= parser.getParameter("card_no", "0");				// ī���ȣ
			String insTerm		= parser.getParameter("ins_term", "00");			// �Һΰ�����
			String siteType		= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
			
			//debug("// STEP 1_2. �Ķ���� �Է�");
			HashMap kvpMap = null;
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}			

			//debug("// STEP 1_3. ������������ ���� ��� ��ȿ�� �˻�..");
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// ����� ���̵�
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
			String pcg         = "";														// ����/���� ����
			String ispCardNo   = "";														// ispī���ȣ
			String valdlim	   = "";														// ���� ����
			String pid = null;																// ���ξ��̵�

			if(kvpMap != null) {
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
			//debug("// STEP 5. ����ó��");
			payEtt.setMerMgmtNo(merMgmtNo);
			payEtt.setCardNo(ispCardNo);
			payEtt.setValid(valdlim);			
			payEtt.setAmount(sum);
			payEtt.setInsTerm(insTerm);
			payEtt.setRemoteAddr(ip);				 

			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			if( "211.181.255.40".equals(host_ip)) {
				payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
				
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
				//payResult=true;
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

			} else {
				payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
			}
			// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = "000"+payType;
			String sttl_gds_clss = "000"+moneyType;
			
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			if("211.181.255.40".equals(host_ip)) {
			//	dataSet.setString("AUTH_NO", "");
				dataSet.setString("AUTH_NO", payEtt.getUseNo());

			} else {
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
			}

			debug("=`==`=`=`=`=`=`=`=` �������� ���� ���� ");
			debug("===================merMgmtNo : " + merMgmtNo);
			debug("===================ispCardNo : " + ispCardNo);
			debug("===================valdlim : " + valdlim);
			debug("===================sum : " + sum);
			debug("===================insTerm : " + insTerm);
			debug("===================ip : " + ip);


			// 04.����ó��	

			// ���� ���� �Ϸ� 
			//payResult = true;
			if (payResult) { // ��~~ �����ؾ� �� ����
				
				addResult = addResult + procSky.executeSky(context, dataSet, request);		

				debug("�����ڷ� �Է�");
				debug("===================addResult : " + addResult);
				debug("===================intMemGrade : " + intMemGrade);
				debug("===================tmYn : " + tmYn);

				if (addResult == 1 && (intMemGrade<4 || intMemGrade==7) && !tmYn.equals("Y")) {
					debug("===================�Ϸ� ");
					// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);
				}
			}			

			returnUrlTrue = "/app/golfloung/golfMemSkiPop.do";
        	returnUrlFalse =  "/app/golfloung/golfMemSkiPop.do";
        	//script = "alert('ó���� �Ϸ�Ǿ����ϴ�.'); self.close();";
        	//script = "if(opener.opener) { opener.opener.golfMemSkiPop(); } else { self.close(); }";
        	scriptFalse = "";
        	if ("3".equals(type)) {
        		script = "opener.golfMemSkiPop(); opener.parent.document.location.href='/'; self.close();";
        	}

			if (addResult == 2) {
				//ī��ȸ������ �����ȸ������
				if("���Ǿ����÷�Ƽ��".equals(strMem)) {
					usrEntity.setIntMemberGrade((int)intMemGrade);
					if((int)intMemGrade < 2) {				
						usrEntity.setIntMemGrade((int)intMemGrade);
					}
					usrEntity.setCyberMoney(0);
				} else {
					
					// è�ǿµ���� �ƴ϶�� �ٽ� ��޼��Ǳ���
					if( intUsrGrad != 1)
					{
					usrEntity.setMemGrade(memGrade);
					usrEntity.setIntMemberGrade((int)intMemGrade);
					usrEntity.setIntMemGrade((int)intMemGrade);
					usrEntity.setCyberMoney(0);
					}
				}

				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);

	        } else if (addResult == 9) { //�ѹ� �� üũ��
	        	// DB���� ���н� ������� ����	        	
				debug("====================GolfMemInsSkiActn =============DB���� ���н� ������� ���� 1 ");
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
	        	}

				request.setAttribute("script", scriptFalse);
	        	request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");	
	        } else {
	        	// DB���� ���н� ������� ���� 
				debug("====================GolfMemInsSkiActn =============DB���� ���н� ������� ���� 2 ");
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
	        	}

				request.setAttribute("script", scriptFalse);
	        	request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	

			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
	}
}
