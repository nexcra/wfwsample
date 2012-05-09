/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPlatinumViewActn
*   �ۼ���    : ������
*   ����      : ������ > ��ŷ > �÷�Ƽ�� ����Ʈ >  �󼼺���(������)
*   �������  : golf
*   �ۼ�����  : 2010-09-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfAdmBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.sky.*;
import com.bccard.golf.jolt.JtProcess;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfadmPlatinumViewActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ ������ ���� ��"; 

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			// 01.��������üũ
			int int_able = 0;
			int int_done = 0;
			int int_can = 0;
			String memGrade = "";
			// 01.��������üũ
			GolfUserEtt ett = new GolfUserEtt();
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String golf_svc_rsvt_no		= parser.getParameter("GOLF_SVC_RSVT_NO", "");
			String cdhd_id 			= parser.getParameter("CDHD_ID","");
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_SVC_RSVT_NO"	,golf_svc_rsvt_no);
			dataSet.setString("CDHD_ID"		,cdhd_id);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");
			DbTaoResult bkView = proc.getPlatinumView(context, dataSet);			//�󼼺��� -����
			
			/// 04-1. �ϴ� ��� ��ȸ
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", 10L);
			dataSet.setString("CDHD_ID", cdhd_id);

			DbTaoResult listResult = (DbTaoResult) proc.executeJuminList(context, request, dataSet);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			else
				request.setAttribute("total_cnt", "0");
			
			request.setAttribute("ListResult", listResult);
			request.setAttribute("BkView", bkView);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
	        
	        /* ī�� ��ȣ �޾ƿ���*/
	        try{
				checkJoltVip(context, request,ett, cdhd_id);
			}catch (Throwable t){
				ArrayList card_list = new ArrayList();
				ett.setCardVipInfoList(card_list);
			}
			List listVip = ett.getCardVipInfoList();
			//System.out.println("## GolfCtrlServ | ȸ�� VIPī��������� ��� ID : "+ listVip.size()+"\n");
			List lgCardList = new ArrayList();	
			
			String select_grade_no = "";
			String newCardNo = "";	//ī�� ��ȣȭ
			String selCardNo="";	//ī�� + ȸ���� +���
			String realCardNo="";	//���� ī�� ��ȣ
			/**/
			if(ett != null)
			{
				select_grade_no = ett.getVipMaxGrade();
				
				debug("## VIPī�� ���� üũ ���� | select_grade_no : "+select_grade_no);
				
								
				if( listVip!=null && listVip.size() > 0 )
				{
					
					if(!"00".equals(select_grade_no))	// �÷�Ƽ�� ȸ���� ���	
					{
						
						for (int i = 0; i < listVip.size(); i++) 
						{
							try { 
							
								CardVipInfoEtt record = (CardVipInfoEtt)listVip.get(i);
								String cardNo 		= StrUtil.isNull((String)record.getCardNo(), ""); 
								String cardType 		= StrUtil.isNull((String)record.getCardType(), "");
								String bankNo 		= StrUtil.isNull((String)record.getBankNo(), "");
								
								CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
								
								try{
								
									
									if(!"".equals(cardNo))
									{
										//newCardNo = cardNo.substring(0, 4)+"-"+cardNo.substring(4, 8)+"-"+cardNo.substring(8, 12)+"-"+cardNo.substring(12, 16);
										newCardNo = cardNo.substring(0, 4)+"-"+cardNo.substring(4, 8)+"-****-"+cardNo.substring(12, 16);
										selCardNo = cardNo + "/" +cardType +"/" + bankNo;
										realCardNo = cardNo;
										
									}
									cardVipInfo.setCardNo(newCardNo);	//ī���ȣ ****
									cardVipInfo.setCardType(realCardNo);	//ī���ȣ 
									cardVipInfo.setCardNm(selCardNo);	//ī���ȣ + / + ��� + / + ȸ����ȣ
									cardVipInfo.setCardAppType(cardNo);
									
									//cardVipInfo.setCardType(cardType);	//ī���ȣ
									//cardVipInfo.setBankNo(bankNo);	//ī���ȣ
								
								}catch(Throwable t){}
								
								lgCardList.add(cardVipInfo);
								
							} catch(Throwable t) {}
							//vipCardYn = "Y";
						}
					}
					else{
						debug("## VIP�÷�Ƽ�� ȸ�� �ƴ�");						
					}
					
				}
				else{
					debug("## VIPī�� ���� ����.");	
				}
			//�˻� �κ� ����
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("realNewCardNo", realCardNo);	//����ī���ȣ
	        request.setAttribute("lgCardList", lgCardList);
			}
	        
	        
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	/**
	 * VIPī�� ���� ���� ��ȸ
	 * @param 	context		WaContext ��ü
	 * @param 	request	HttpServletRequest
	 * @param 	ett			��������� Entity
	 * @return 	void
	 * @TODO	VIPī�� üũ
	 */
	protected void checkJoltVip(WaContext context, HttpServletRequest request,GolfUserEtt ett, String text) throws BaseException {

		java.util.Properties properties = new java.util.Properties();

		properties.setProperty("LOGIN", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
		properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", text); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü
		try {
			/** *****************************************************************
			 *Card������ �о����
			 ***************************************************************** */
			debug("## GolfCtrlServ VIPī�� | 1. VIPī�� Jolt MHL0200R0200 ���� ȣ�� <<<<<<<<<<<<");
						
			String joltFmlTrCode016 = "MHL0200R0200";
			String joltServiceName = "BSNINPT";
			JoltInput jtInput = new JoltInput(joltServiceName);
			jtInput.setServiceName(joltServiceName);
			jtInput.setString("fml_trcode", joltFmlTrCode016);			
			jtInput.setString("fml_arg1",	text);	//1.�ֹ� 2.����� 3.��ü
			//jtInput.setString("fml_arg2",	text);	//�ֹι�ȣ
			//jtInput.setString("fml_arg3",	"");	//
			//jtInput.setString("fml_arg4",	"1");	//����
			
			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult jtResult = jt_pt.call(context, request, jtInput, properties);			
			String retCode = jtResult.getString("fml_ret1").trim();
			
			debug("## retCode : "+retCode+"\n");
			
			String vipGrade 	= "";
			String vipMaxGrade	= ""; 
			String vipCardNo	= "";
			
			
			// �Ϲ�ī�� �Ǵ� ī�� ����
			if( retCode.equals("01")) {
			    //"PTȸ���� �ƴ� ó��";
			}
			else if( !retCode.equals("00")) {
				//��Ÿ �ٸ� �����ϰ�� Skip ó����;	
			}
			else if( retCode.equals("00")) { //PTī�� �������ϰ��
			
				ArrayList vipCardList = new ArrayList();
				
				
				while( jtResult.isNext() ) 
				{
					jtResult.next();
					vipGrade = jtResult.getString("fml_ret9");					
					debug("@@@@@card_no : "+jtResult.getString("fml_ret3")+"  /  " +jtResult.getString("fml_ret9") + "  /  " +jtResult.getString("fml_ret6"));
					
					//�Ϲ�ī��� �����ϰ�
					if(vipGrade.compareTo("00") > 0) {
						
						if(jtResult.getString("fml_ret6").equals("1") || jtResult.getString("fml_ret6").equals("3")) { //1:���� 2:���� 3:����
						
							CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
							
							
							cardVipInfo.setCardNo(jtResult.getString("fml_ret3"));
							cardVipInfo.setCardType(jtResult.getString("fml_ret9"));		//ī�� ���
							cardVipInfo.setBankNo(jtResult.getString("fml_ret5"));			//ȸ�����ȣ
							
							//cardVipInfo.addString("RESULT", "00");
							
							vipCardList.add(cardVipInfo);
							
						}
						
					}
					
				}
				ett.setCardVipInfoList(vipCardList);
				//ett.setVipCardExpDate(vipCardExpDate);
			
			}
				
		} catch (TaoException te) {
			//throw getErrorException("LOGIN_ERROR_0003",new String[]{"TOPī�� ���� ��ȸ ����"},te);     // Jolt ó�� ����
		}

	}	
}
