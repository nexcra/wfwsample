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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.ez.GolfEvntEzReturnDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.golf.common.loginAction.SessionUtil;
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
public class GolfEvntEzReturnActn extends GolfActn{
	
	public static final String TITLE = "������ ���� ó��";

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
			String rstCode = "";
			int updEvnt			= 0;	// �������� ������

			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
						
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String result 		= "";
			String aspOrderNum 	= "";
			String orderNum 	= "";
			String paySummary 	= "";
			String errDesc 		= "";
			String goUrl 		= "";
			String command 		= "";
			
			// �⺻ ��ȸ 
			String enc_result 		= (String)parser.getParameter("result");		// �ֹ�ó�� ��� : yes:����, no:����
			String enc_aspOrderNum 	= (String)parser.getParameter("aspOrderNum");	// �ֹ���ȣ (���޻���) -> ��������� �ֹ���ȣ
			String enc_orderNum 	= (String)parser.getParameter("orderNum");		// �ֹ���ȣ (��������)
			String enc_paySummary 	= (String)parser.getParameter("paySummary");	// ������� ���� : ����Ʈ:10000, ī��:50000
			String enc_errDesc 		= (String)parser.getParameter("errDesc");		// ��������
			String enc_goUrl 		= (String)parser.getParameter("goUrl");			// ����URL
			String enc_command 		= "";
			

			if(!GolfUtil.empty(enc_result)) 		result 		= new String(Base64Encoder.decode(enc_result));
			if(!GolfUtil.empty(enc_aspOrderNum))	aspOrderNum = new String(Base64Encoder.decode(enc_aspOrderNum));
			if(!GolfUtil.empty(enc_orderNum)) 		orderNum 	= new String(Base64Encoder.decode(enc_orderNum));
			if(!GolfUtil.empty(enc_paySummary)) 	paySummary 	= new String(Base64Encoder.decode(enc_paySummary));
			if(!GolfUtil.empty(enc_errDesc)) 		errDesc 	= new String(Base64Encoder.decode(enc_errDesc));
			if(!GolfUtil.empty(enc_goUrl)) 			goUrl 		= new String(Base64Encoder.decode(enc_goUrl));
			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("result", result);
			dataSet.setString("aspOrderNum", aspOrderNum);
			dataSet.setString("orderNum", orderNum);
			dataSet.setString("paySummary", paySummary);
			dataSet.setString("errDesc", errDesc);			

			
			GolfEvntEzReturnDaoProc proc = (GolfEvntEzReturnDaoProc)context.getProc("GolfEvntEzReturnDaoProc");
			
			
			if(GolfUtil.empty(result)){
				// ���������� ���� ���� �Ѿ�°��
				rstCode = "1";
				command = "102";
			}else{
				rstCode = "0";
				
				// ���� ����
				updEvnt = proc.updEvntFunction(context, request, dataSet);
				
				if(updEvnt>0){
					if(result.equals("yes")){
						command = "103";
					}else{
						command = "102";
					}
				}else{
					command = "102";
				}
			}

			paramMap.put("updEvnt", updEvnt+"");
			paramMap.put("rstCode", rstCode);
			
			if(!GolfUtil.empty(result))			enc_result 		= new String(Base64Util.encode(result.getBytes()));
			if(!GolfUtil.empty(aspOrderNum))	enc_aspOrderNum	= new String(Base64Util.encode(aspOrderNum.getBytes()));
			if(!GolfUtil.empty(orderNum))		enc_orderNum 	= new String(Base64Util.encode(orderNum.getBytes()));
			if(!GolfUtil.empty(paySummary))		enc_paySummary 	= new String(Base64Util.encode(paySummary.getBytes()));
			if(!GolfUtil.empty(errDesc))		enc_errDesc 	= new String(Base64Util.encode(errDesc.getBytes()));
			if(!GolfUtil.empty(command))		enc_command 	= new String(Base64Util.encode(command.getBytes()));
			
			paramMap.put("result", result);
			paramMap.put("aspOrderNum", aspOrderNum);
			paramMap.put("orderNum", orderNum);
			paramMap.put("paySummary", paySummary);
			paramMap.put("errDesc", errDesc);
			paramMap.put("goUrl", goUrl);
			paramMap.put("command", command);

			
			paramMap.put("enc_result", enc_result);
			paramMap.put("enc_aspOrderNum", enc_aspOrderNum);
			paramMap.put("enc_orderNum", enc_orderNum);
			paramMap.put("enc_paySummary", enc_paySummary);
			paramMap.put("enc_errDesc", enc_errDesc);
			paramMap.put("enc_command", enc_command);
			paramMap.put("enc_goUrl", enc_goUrl);
			
			String cspCd = (String)request.getSession().getAttribute("ezCspCd");
			String enc_cspCd = "";
			if(!GolfUtil.empty(cspCd))			enc_cspCd 		= new String(Base64Util.encode(cspCd.getBytes()));
			paramMap.put("cspCd", cspCd);
			paramMap.put("enc_cspCd", enc_cspCd);
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
