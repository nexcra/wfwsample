/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : AdmGolfTopPointLstInqActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > ����Ʈ ����  > ����Ʈ���� ����Ʈ ��ȸ
*   �������  : Golf  
*   �ۼ�����  : 2010-12-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;
   

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.DateUtil;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/******************************************************************************
* Topn
* @author	 
* @version	1.0  
******************************************************************************/

public class AdmGolfTopPointLstInqActn extends GolfActn {
	
	public static final String TITLE = "������ > ��ŷ > ����Ʈ����  > ����Ʈ���� ����Ʈ ��ȸ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	*********************************** ****************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		debug("***********************************************************************************");
		debug("*************                                                        **************");
		debug("action GolfadmTopPenaltyLstActn.java execute");
		debug("*************                                                        **************");
		debug("***********************************************************************************");
		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
					// 01.��������üũ
			
					debug("action AdmGolfTopPointLstInqActn.java try");
					// 02.�Է°� ��ȸ		
					RequestParser parser = context.getRequestParser(subpage_key, request, response);
					Map paramMap = BaseAction.getParamToMap(request);
					paramMap.put("title", TITLE);
		
					// ��¥ �ʱⰪ ����
					String nowYear      =   DateUtil.currdate("yyyy");             //�ý��� ��¥(year)
					String nowMonth     =   DateUtil.currdate("MM");               //�ý��� ��¥ (month)
					int term            =   DateUtil.getMonthlyDayCount(Integer.parseInt(nowYear),Integer.parseInt(nowMonth));       // ����
					String dateFromFmt  =   nowYear + "." + nowMonth + "." + "01";                  //�⺻ ������
					String dateToFmt    =   nowYear + "." + nowMonth + "." + String.valueOf(term);  //�⺻ ������
					String dateFrom 	=   DateUtil.format(dateFromFmt,"yyyy.MM.dd","yyyyMMdd");   //�⺻ ������ ����
					String dateTo       =   DateUtil.format(dateToFmt,"yyyy.MM.dd","yyyyMMdd");     //�⺻ ������ ����	
		
					String roundDateFrom = parser.getParameter("roundDateFrom",dateFrom);
					String roundDateFromFmt = parser.getParameter("roundDateFromFmt",dateFromFmt);
					String roundDateTo = parser.getParameter("roundDateTo",dateTo);
					String roundDateToFmt = parser.getParameter("roundDateToFmt",dateToFmt);
					String pointDetlCd = parser.getParameter("pointDetlCd", null);
					long pageNo = parser.getLongParameter("pageNo",1L);
					long recordsInPage = 10L;//parser.getLongParameter("recordsInPage",10L);

					// Request �� ����


					// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
					dataSet.setString("roundDateFrom",roundDateFrom);
					dataSet.setString("roundDateTo",roundDateTo);
					dataSet.setString("pointDetlCd",pointDetlCd);
					dataSet.setLong("pageNo",pageNo);
					dataSet.setLong("recordsInPage",recordsInPage);
					// 04.���� ���̺�(Proc) ��ȸ
					debug("before action AdmGolfTopPointLstInqActn.java con xecute before");			
	  	//  TaoResult boardInqLst = con.execute("AdmGolfTopPointLstInqProc", dataSet);
					AdmGolfTopPointLstInqProc proc = (AdmGolfTopPointLstInqProc)context.getProc("AdmGolfTopPointLstInqProc");
					debug("after action AdmGolfTopPointLstInqActn.java con xecute after");	

					paramMap.put("roundDateFrom", roundDateFrom);
					paramMap.put("roundDateFromFmt", roundDateFromFmt);
					paramMap.put("roundDateTo", roundDateTo);
					paramMap.put("roundDateToFmt", roundDateToFmt);
					paramMap.put("pointDetlCd", pointDetlCd);
					paramMap.put("pageNo", String.valueOf(pageNo));
					paramMap.put("recordsInPage", String.valueOf(recordsInPage));
					debug("laction pointDetlCd"+ pointDetlCd);
					debug("laction pageNo"+ pageNo);
					debug("laction recordsInPage"+ recordsInPage);
					DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
					if(listResult.isNext()){
							listResult.next();
							if("00".equals(listResult.getString("result"))){
									paramMap.put("recordCnt", String.valueOf(listResult.getLong("recordCnt")));
							}else{
								paramMap.put("recordCnt", "0");
							}
					}

					request.setAttribute("listResult", listResult);	
					request.setAttribute("paramMap", paramMap);
			
					} catch(Throwable t) {
						debug(TITLE, t);
						throw new GolfException(TITLE, t);
					} 
					
					return super.getActionResponse(context, subpage_key);
					
				}

}
