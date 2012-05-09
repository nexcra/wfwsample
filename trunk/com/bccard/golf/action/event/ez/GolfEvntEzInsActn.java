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
package com.bccard.golf.action.event.ez;

import java.io.IOException;
import java.net.InetAddress;
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

import com.bccard.common.NameCheck;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.ez.GolfEvntEzInsDaoProc;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpDaoProc;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.security.cryptography.*;
import com.initech.util.Base64Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntEzInsActn extends GolfActn{
	
	public static final String TITLE = "������ ó��";
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
						

		try {

			// ��ó��
			String resultMsg = "";
			String script = "";
			
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// �⺻ ��ȸ 
			String idx = (String)parser.getParameter("idx").trim();
			String ur_name = (String)parser.getParameter("ur_name").trim();
			String jumin_no1 = (String)parser.getParameter("jumin_no1").trim();
			String jumin_no2 = (String)parser.getParameter("jumin_no2").trim();
			String jumin_no = jumin_no1 + jumin_no2; 

			paramMap.put("ur_name", ur_name);
			paramMap.put("jumin_no", jumin_no);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm:ss"); 
			GregorianCalendar cal = new GregorianCalendar();


			
			// ������ ����
			String enc_cspCd = "";		// ���޻�(CP) ��ü�ڵ�
			String enc_command = "";	// ��ɾ�����
			String enc_clientCd = "";	// ���� �ڵ�
			String enc_goodsNm = "";	// ��ǰ��
			String enc_goodsCd = "";	// ��ǰ�ڵ�(��޹�ȣ)
			String enc_unitCost = "";	// �Ǹ� �ܰ�
			String enc_buyPrice = "";	// ���� ����
			String enc_orderCount = "";	// �ֹ�����
			String enc_orderTotal = "";	// �ֹ��Ѿ�
			String enc_payMoney = "";	// ����ݾ�
			String enc_orderDd = "";	// �ֹ���
			String enc_orderTm = "";	// �ֹ��ð�
			String enc_orderNm = "";	// �ֹ����̸�(����)
			String enc_userKey = "";	// �ֹ�������Ű
			String enc_orderEmail = "";	// �ֹ����̸���
			String enc_aspOrderNum = "";// ���޻� �ֹ���ȣ
			String enc_goUrl = "";		// ���޻������� URL
			
			String cspCd = (String)parser.getParameter("cspCd");		// ���޻�(CP) ��ü�ڵ�
			String command = "101";	// ��ɾ�����
			String clientCd = (String)parser.getParameter("clientCd");	// ���� �ڵ�
			
			String goodsNm = "";	// ��ǰ��
			String goodsCd = "";	// ��ǰ�ڵ�(��޹�ȣ)
			String unitCost = "";	// �Ǹ� �ܰ�
			String buyPrice = "";	// ���� ����
			String orderCount = "";	// �ֹ�����
			String orderTotal = "";	// �ֹ��Ѿ�
			String payMoney = "";	// ����ݾ�
			
			String orderDd = formatter.format(cal.getTime());	// �ֹ���
			String orderTm = formatter2.format(cal.getTime());	// �ֹ��ð�
			String orderNm = ur_name;							// �ֹ����̸�(����)
			String userKey = (String)parser.getParameter("userKey");	// �ֹ�������Ű
			String orderEmail = (String)parser.getParameter("email");	// �ֹ����̸���
			String aspOrderNum = "";							// ���޻� �ֹ���ȣ
			String goUrl = "";					// ���޻������� URL
			
			
			if(idx.equals("1")){
				goodsNm = "Champion";
				goodsCd = "1";
				unitCost = "170000";
				buyPrice = "170000";
				orderCount = "1";
				orderTotal = "170000";
				payMoney = "170000";
			}else if(idx.equals("2")){
				goodsNm = "Blue";
				goodsCd = "2";
				unitCost = "42500";
				buyPrice = "42500";
				orderCount = "1";
				orderTotal = "42500";
				payMoney = "42500";
			}else if(idx.equals("3")){
				goodsNm = "Gold";
				goodsCd = "3";
				unitCost = "21250";
				buyPrice = "21250";
				orderCount = "1";
				orderTotal = "21250";
				payMoney = "21250";
			}else if(idx.equals("7")){
				goodsNm = "Black";
				goodsCd = "7";
				unitCost = "127500";
				buyPrice = "127500";
				orderCount = "1";
				orderTotal = "127500";
				payMoney = "127500";
			}

			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ur_name", ur_name);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("payMoney", payMoney);
			dataSet.setString("goodsCd", goodsCd);

			String rstCode = "";	// ��� �ڵ�
			String msg = "";		// �ѽ��� ���� �޼���
			String end_date = "";	// ����ȸ�� ������
			GolfEvntEzInsDaoProc proc = (GolfEvntEzInsDaoProc)context.getProc("GolfEvntEzInsDaoProc");

			// �ѽ��� �Ǹ�����
			NameCheck nm = new NameCheck(); 
			nm.setChkName(ur_name);
			String rtn = nm.setJumin(jumin_no + SITEPW);
			nm.setSiteCode(SITEID);

			if("0".equals(rtn)) {
				nm.setSiteCode(SITEID);
				nm.setTimeOut(30000);
				rtn = nm.getRtn().trim(); 
			} 
			debug(">> �ѽ��� �Ǹ����� > Return = " + rtn); 
			
			// �Ǽ�, �¼� ����
			String serverip = InetAddress.getLocalHost().getHostAddress();	// ����������
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");		// ���߱� ip ����
			if(serverip.equals(devip)){
				goUrl = "http://develop.golfloung.com:13300/app/golfloung/";
				rtn = "1";		// �¼������� ���!!
			}else{
				goUrl = "http://www.golfloung.com/app/golfloung/";
				rtn = "1";		// �¼������� ���!!-> �ϴ� �Ǽ��� ���
			}
			goUrl += "GolfEvntReturn.do";
			

			if("1".equals(rtn)) { 
				// �������� - �Ǹ������� ������ ����� �˻��ϵ��� �Ѿ��.

				// �̹� ȸ������, ��û������ �ִ��� Ȯ���Ѵ�.
				// ����ȸ�� �������� �˻�
				end_date = proc.cntMemFunction(context, request, dataSet);
				
				if(GolfUtil.empty(end_date)){
					
					// ��ϳ��� Ȯ��
					int evntCnt = proc.cntEvntFunction(context, request, dataSet);
					
					if(evntCnt==0){
						
						// ��û������ �����Ѵ�.
						int insEvntseq = proc.insEvnt(context, request, dataSet);
						aspOrderNum = insEvntseq+"";
						
						if(insEvntseq>0){
							rstCode = "0";	// �������
						}else{
							rstCode = "1";	// ����
						}
					}else{
						rstCode = "3";	// �̹� ��û
					}
				}else{
					rstCode = "2";	// �̹� ����ȸ��
				}
					
			} else if("2".equals(rtn)) { 
				// ���ξƴ�
				msg = "�Ǹ������� �����߽��ϴ�[���� �ƴ�]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("3".equals(rtn)) {
				// �ڷ� ����
				msg = "�Ǹ������� �����߽��ϴ�[�ڷ� ����]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("4".equals(rtn)) {
				// �ý������ (ũ������ũ �̻�)
				msg = "�Ǹ������� �����߽��ϴ�[�ý������ (ũ������ũ �̻�)]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("5".equals(rtn)) {
				// �ֹι�ȣ ����
				msg = "�Ǹ������� �����߽��ϴ�[�ֹι�ȣ ����]. �ٽ�  �Է��� �ֽʽÿ�";
			} else if("50".equals(rtn)) {
				// �������� ���� ��û �ֹι�ȣ
				msg = "�Ǹ������� �����߽��ϴ�[�������� ���� ��û �ֹι�ȣ]. �ٽ�  �Է��� �ֽʽÿ�";
			} else  {
				// System ERROR
				msg = "�Ǹ������� �����߽��ϴ�[System ERROR]. �ٽ�  �Է��� �ֽʽÿ�";
			} 
			
			

			paramMap.put("end_date", end_date);
			paramMap.put("rstCode", rstCode);
			paramMap.put("rtn", rtn);
			paramMap.put("msg", msg);
			

			if(!GolfUtil.empty(cspCd))		enc_cspCd 		= new String(Base64Encoder.encode(cspCd.getBytes()));
			if(!GolfUtil.empty(command))	enc_command 	= new String(Base64Encoder.encode(command.getBytes()));
			if(!GolfUtil.empty(clientCd))	enc_clientCd 	= new String(Base64Encoder.encode(clientCd.getBytes()));
			if(!GolfUtil.empty(goodsNm))	enc_goodsNm 	= new String(Base64Encoder.encode(goodsNm.getBytes()));
			if(!GolfUtil.empty(goodsCd))	enc_goodsCd 	= new String(Base64Encoder.encode(goodsCd.getBytes()));
			if(!GolfUtil.empty(unitCost))	enc_unitCost 	= new String(Base64Encoder.encode(unitCost.getBytes()));
			if(!GolfUtil.empty(buyPrice))	enc_buyPrice 	= new String(Base64Encoder.encode(buyPrice.getBytes()));
			if(!GolfUtil.empty(orderCount))	enc_orderCount 	= new String(Base64Encoder.encode(orderCount.getBytes()));
			if(!GolfUtil.empty(orderTotal))	enc_orderTotal 	= new String(Base64Encoder.encode(orderTotal.getBytes()));
			if(!GolfUtil.empty(payMoney))	enc_payMoney 	= new String(Base64Encoder.encode(payMoney.getBytes()));
			if(!GolfUtil.empty(orderDd))	enc_orderDd 	= new String(Base64Encoder.encode(orderDd.getBytes()));
			if(!GolfUtil.empty(orderTm))	enc_orderTm 	= new String(Base64Encoder.encode(orderTm.getBytes()));
			if(!GolfUtil.empty(orderNm))	enc_orderNm 	= new String(Base64Encoder.encode(orderNm.getBytes()));
			if(!GolfUtil.empty(userKey))	enc_userKey 	= new String(Base64Encoder.encode(userKey.getBytes()));
			if(!GolfUtil.empty(orderEmail))	enc_orderEmail 	= new String(Base64Encoder.encode(orderEmail.getBytes()));
			if(!GolfUtil.empty(aspOrderNum))enc_aspOrderNum = new String(Base64Encoder.encode(aspOrderNum.getBytes()));
			if(!GolfUtil.empty(goUrl))		enc_goUrl 		= new String(Base64Encoder.encode(goUrl.getBytes()));
			
			paramMap.put("cspCd", cspCd);
			paramMap.put("command", command);
			paramMap.put("clientCd", clientCd);
			paramMap.put("goodsNm", goodsNm);
			paramMap.put("goodsCd", goodsCd);
			paramMap.put("unitCost", unitCost);
			paramMap.put("buyPrice", buyPrice);
			paramMap.put("orderCount", orderCount);
			paramMap.put("orderTotal", orderTotal);
			paramMap.put("payMoney", payMoney);
			paramMap.put("orderDd", orderDd);
			paramMap.put("orderTm", orderTm);
			paramMap.put("orderNm", orderNm);
			paramMap.put("userKey", userKey);
			paramMap.put("orderEmail", orderEmail);
			paramMap.put("aspOrderNum", aspOrderNum);
			paramMap.put("goUrl", goUrl);
			
			debug("cspCd : " + cspCd + " / command : " + command + " / clientCd : " + clientCd + " / goodsNm : " + goodsNm + " / goodsCd : " + goodsCd 
					+ " / unitCost : " + unitCost + " / buyPrice : " + buyPrice + " / orderCount : " + orderCount + " / orderTotal : " + orderTotal 
					+ " / payMoney : " + payMoney + " / orderDd : " + orderDd + " / orderTm : " + orderTm + " / orderNm : " + orderNm + " / userKey : " + userKey + " / orderEmail : " + orderEmail
					+ " / aspOrderNum : " + aspOrderNum + " / goUrl : " + goUrl);

			paramMap.put("enc_cspCd", enc_cspCd);
			paramMap.put("enc_command", enc_command);
			paramMap.put("enc_clientCd", enc_clientCd);
			paramMap.put("enc_goodsNm", enc_goodsNm);
			paramMap.put("enc_goodsCd", enc_goodsCd);
			paramMap.put("enc_unitCost", enc_unitCost);
			paramMap.put("enc_buyPrice", enc_buyPrice);
			paramMap.put("enc_orderCount", enc_orderCount);
			paramMap.put("enc_orderTotal", enc_orderTotal);
			paramMap.put("enc_payMoney", enc_payMoney);
			paramMap.put("enc_orderDd", enc_orderDd);
			paramMap.put("enc_orderTm", enc_orderTm);
			paramMap.put("enc_orderNm", enc_orderNm);
			paramMap.put("enc_userKey", enc_userKey);
			paramMap.put("enc_orderEmail", enc_orderEmail);
			paramMap.put("enc_aspOrderNum", enc_aspOrderNum);
			paramMap.put("enc_goUrl", enc_goUrl);
			
//			debug("enc_cspCd : " + enc_cspCd + " / enc_command : " + enc_command + " / enc_clientCd : " + enc_clientCd + " / enc_goodsNm : " + enc_goodsNm + " / enc_goodsCd : " + enc_goodsCd 
//					+ " / enc_unitCost : " + enc_unitCost + " / enc_buyPrice : " + enc_buyPrice + " / enc_orderCount : " + enc_orderCount + " / enc_orderTotal : " + enc_orderTotal 
//					+ " / enc_payMoney : " + enc_payMoney + " / enc_orderDd : " + enc_orderDd + " / enc_orderTm : " + enc_orderTm + " / enc_orderNm : " + enc_orderNm + " / enc_userKey : " + enc_userKey + " / enc_orderEmail : " + enc_orderEmail
//					+ " / enc_aspOrderNum : " + enc_aspOrderNum + " / enc_goUrl : " + enc_goUrl);


			if(!GolfUtil.empty(enc_cspCd)) 		cspCd 		= new String(Base64Encoder.decode(enc_cspCd));
			if(!GolfUtil.empty(enc_clientCd))	clientCd 	= new String(Base64Encoder.decode(enc_clientCd));
			if(!GolfUtil.empty(enc_userKey)) 	userKey 	= new String(Base64Encoder.decode(enc_userKey));
			if(!GolfUtil.empty(enc_goUrl)) 		goUrl 		= new String(Base64Encoder.decode(enc_goUrl));

//			debug("cspCd : " + cspCd + " / command : " + command + " / clientCd : " + clientCd + " / goodsNm : " + goodsNm + " / goodsCd : " + goodsCd 
//					+ " / unitCost : " + unitCost + " / buyPrice : " + buyPrice + " / orderCount : " + orderCount + " / orderTotal : " + orderTotal 
//					+ " / payMoney : " + payMoney + " / orderDd : " + orderDd + " / orderNm : " + orderNm + " / userKey : " + userKey + " / orderEmail : " + orderEmail
//					+ " / aspOrderNum : " + aspOrderNum + " / goUrl : " + goUrl);


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
