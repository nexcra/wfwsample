/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPlatinumNameSearchActn
*   �ۼ���    : ������
*   ����      : �̸����� ���� ���
*   �������  : Golf
*   �ۼ�����  : 2010-09-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;


import java.io.IOException; 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.jolt.JtProcess;


/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfadmPlatinumNameSearchActn extends GolfActn{
	
	public static final String TITLE = "�̸����� ���� ������ ��� �ѷ��ֱ�"; 

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ	 	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String sch_type = parser.getParameter("SCH_TYPE","");
			String sch_text = parser.getParameter("SCH_TEXT","");
			//String sch_name = parser.getParameter("SCH_NAME","");
			
			
			debug("sch_text@@@@@@@@@@@@@@@@@@@�� : "+sch_text);
			debug("sch_type@@@@@@@@@@@@@@@@@@@�� : "+sch_type);
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_TYPE", sch_type);
			dataSet.setString("SCH_TEXT", sch_text);
			
			
								
			// 04.���� ���̺�(Proc) ��ȸ - ����Ʈ
			GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");
			DbTaoResult listResult = null;
			
			if(sch_type.equals("SCH_NAME")){
				if(!sch_text.equals(""))
				listResult = (DbTaoResult) proc.searchName(context, request, dataSet);
			}else if(sch_type.equals("SCH_GOLFLOUNG")){
				if(!sch_text.equals(""))
				listResult = (DbTaoResult) proc.searchGolfloung(context, request, dataSet);
			}else if(sch_type.equals("SCH_JUMIN")){
				if(!sch_text.equals("")){}
				//listResult = (DbTaoResult) proc.searchJumin(context, request, dataSet);
					try{
						listResult =checkJoltVip(context, request, sch_text);
					}catch (Throwable t){
						ArrayList card_list = new ArrayList();
					}
				
				
			}
			
			
			//DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result)){
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			}
			else{
				request.setAttribute("total_cnt", "0");
			}
			
			
			//�˻� �κ� ����
			paramMap.put("page_no",Long.toString(page_no));
			paramMap.put("record_size",Long.toString(record_size));
			request.setAttribute("search_type", listResult.getString("TYPE"));
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("ListResult", listResult);
	        
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
	protected DbTaoResult checkJoltVip(WaContext context, HttpServletRequest request, String text) throws BaseException {

		java.util.Properties properties = new java.util.Properties();

		properties.setProperty("GetVIPCARD", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
		properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", text); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü
		DbTaoResult rtnList = null;
		try {

			/** *****************************************************************
			 *Card������ �о����
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ VIPī�� | 1. VIPī�� Jolt MHL0200R0200 ���� ȣ�� <<<<<<<<<<<<");
						
			String joltFmlTrCode016 = "MHL0200R0200";
			String joltServiceName = "BSNINPT";
			JoltInput jtInput = new JoltInput(joltServiceName);
			jtInput.setServiceName(joltServiceName);
			jtInput.setString("fml_trcode", joltFmlTrCode016);			
			jtInput.setString("fml_arg1",	text);	//�ֹι�ȣ ��ȸ
			
			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult jtResult = jt_pt.call(context, request, jtInput, properties);			
			String retCode = jtResult.getString("fml_ret1").trim();
			
			System.out.println("## retCode : "+retCode+"\n");
			
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
														
					//�Ϲ�ī��� �����ϰ�
					if(vipGrade.compareTo("00") > 0) {
					
						System.out.println("## VIPCARD vipGrade : "+vipGrade+" | vipMaxGrade : "+vipMaxGrade+"\n");
						
						//CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
						
						
						rtnList.addString("CARD_NO", jtResult.getString("fml_ret3"));	//ī�� ��ȣ
						rtnList.addString("TYPE", "jumin");
						rtnList.addString("RESULT", "00");
						
//						cardVipInfo.setBankNo(jtResult.getString("fml_ret5"));
//						cardVipInfo.setCardNo(jtResult.getString("fml_ret3"));
//						cardVipInfo.setCardType(jtResult.getString("fml_ret6"));
//						//cardVipInfo.setJoinNo(jtResult.getString("fml_ret8"));
//						//cardVipInfo.setJoinName(jtResult.getString("fml_ret7"));
//						cardVipInfo.setAcctDay(jtResult.getString("fml_ret11"));
//						cardVipInfo.setCardAppType(jtResult.getString("fml_ret17"));
//						cardVipInfo.setExpDate(jtResult.getString("fml_ret13"));
//						cardVipInfo.setAppDate(jtResult.getString("fml_ret14"));
//						cardVipInfo.setLastCardNo(jtResult.getString("fml_ret15"));
//						//cardVipInfo.setSocId(jtResult.getString("fml_ret16"));
//						cardVipInfo.setVipGrade(vipGrade);
						
						//vipCardList.add(cardVipInfo);
						//�ְ��޸�
						/*if( vipMaxGrade.compareTo(vipGrade) < 0) {
							vipMaxGrade = vipGrade;
							vipCardNo		= jtResult.getString("fml_ret3");							
							//info("vipMaxGrade:"+vipMaxGrade);
						}*/
					}
					
				}
				System.out.println("## vipMaxGrade ������ : "+vipMaxGrade+"\n");
				
//				ett.setCardVipInfoList(vipCardList);
//				ett.setVipMaxGrade(vipMaxGrade);
//				ett.setVipCardNo(vipCardNo);
				//ett.setVipCardExpDate(vipCardExpDate);
			
			}
			
						

			
		}  catch (TaoException te) {
			//throw getErrorException("LOGIN_ERROR_0003",new String[]{"TOPī�� ���� ��ȸ ����"},te);     // Jolt ó�� ����
		}
		return rtnList;
	}	
}
