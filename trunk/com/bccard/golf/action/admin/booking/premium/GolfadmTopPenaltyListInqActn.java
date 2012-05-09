/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopPenaltyListActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > �г�Ƽ����  > �г�Ƽ���� ����Ʈ ��ȸ
*   �������  : Golf
*   �ۼ�����  : 2010-11-19
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
public class GolfadmTopPenaltyListInqActn extends GolfActn{
	
	public static final String TITLE = "������ > ��ŷ > �г�Ƽ����  > �г�Ƽ���� ����Ʈ ��ȸ";

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
		debug("action GolfadmTopPenaltyListActn.java execute");
		debug("*************                                                        **************");
		debug("***********************************************************************************");
		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
					// 01.��������üũ
			
					debug("action GolfadmTopPenaltyListActn.java try");
					// 02.�Է°� ��ȸ		
					RequestParser parser = context.getRequestParser(subpage_key, request, response);
					Map paramMap = BaseAction.getParamToMap(request);
					paramMap.put("title", TITLE);
		
					// ��¥ �ʱⰪ ����
					String nowYear      =   DateUtil.currdate("yyyy");//�ý��� ��¥(year)
					String nowMonth     =   DateUtil.currdate("MM");//�ý��� ��¥(month)
					int term            =   DateUtil.getMonthlyDayCount(Integer.parseInt(nowYear),Integer.parseInt(nowMonth));// ����
					String dateFromFmt  =   nowYear + "." + nowMonth + "." + "01";//�⺻ ������
					String dateToFmt    =   nowYear + "." + nowMonth + "." + String.valueOf(term);  //�⺻ ������
					String dateFrom     =   DateUtil.format(dateFromFmt,"yyyy.MM.dd","yyyyMMdd");   //�⺻ ������ ����
					String dateTo       =   DateUtil.format(dateToFmt,"yyyy.MM.dd","yyyyMMdd");     //�⺻ ������ ����
					String bbs          = "0035";
					
					String penaltyApplyClss ="";
					String roundDateFrom    = parser.getParameter("roundDateFrom",dateFrom);//����Ⱓ
					String roundDateFromFmt = parser.getParameter("roundDateFromFmt",dateFromFmt);
					String roundDateTo      = parser.getParameter("roundDateTo",dateTo);
					String roundDateToFmt   = parser.getParameter("roundDateToFmt",dateToFmt);
					String setFrom          = parser.getParameter("setFrom",dateFrom); //����Ⱓ
					String setFromFmt       = parser.getParameter("setFromFmt",dateFromFmt);
					String setTo            = parser.getParameter("setTo",dateTo);
					String setToFmt         = parser.getParameter("setToFmt",dateToFmt);

					// Request �� ����
					long affiGreenSeqNo     = parser.getLongParameter("affiGreenSeqNo",0L);
					debug("action GolfadmTopPenaltyListActn.java affiGreenSeqNo "+affiGreenSeqNo+"]");
					String pointDetlCd      = parser.getParameter("pointDetlCd");
					String greenNm          = parser.getParameter("greenNm");
					long   recordCnt        = parser.getLongParameter("recortCnt",0L);

					// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
					dataSet.setString("roundDateFrom", roundDateFrom); 
					dataSet.setString("roundDateTo", roundDateTo);
					dataSet.setString("setFrom",setFrom);
					dataSet.setString("setTo",setTo); 
					dataSet.setLong("affiGreenSeqNo", affiGreenSeqNo);
					debug("action GolfadmTopPenaltyListActn.java data.Set affiGreenSeqNo "+affiGreenSeqNo+"]");
					dataSet.setString("pointDetlCd",pointDetlCd);
					// 04.���� ���̺�(Proc) ��ȸ
					GolfadmTopPenaltyListDaoProc proc = (GolfadmTopPenaltyListDaoProc)context.getProc("GolfadmTopPenaltyListDaoProc");
					// 05. ������ ����Ʈ (Sel_Proc) ��ȸramMap.put("greenNm",greenNm);
					GolfadmTopCodeSelDaoProc coodSelProc = (GolfadmTopCodeSelDaoProc)context.getProc("GolfadmTopCodeSelDaoProc");
					DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs); //�Խ��� ����
					request.setAttribute("codeSelResult", codeSel);
					paramMap.put("penaltyApplyClss",penaltyApplyClss);
					paramMap.put("roundDateFrom",roundDateFrom);
					paramMap.put("roundDateFromFmt", roundDateFromFmt);
					paramMap.put("roundDateTo",roundDateTo);
					paramMap.put("roundDateToFmt", roundDateToFmt);
					paramMap.put("setFrom",setFrom);
					paramMap.put("setFromFmt", setFromFmt);
					paramMap.put("setTo",setTo); 
					paramMap.put("setToFmt", setToFmt);
					paramMap.put("affiGreenSeqNo",String.valueOf(affiGreenSeqNo));
					paramMap.put("pointDetlCd",pointDetlCd);
					paramMap.put("greenNm",greenNm);
					paramMap.put("recordCnt",String.valueOf(recordCnt));
					debug("action GolfadmTopPenaltyListActn.java recordCnt "+String.valueOf(recordCnt)); 
					DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
					request.setAttribute("listResult", listResult);	
					request.setAttribute("paramMap", paramMap);
			
					} catch(Throwable t) {
						debug(TITLE, t);
						throw new GolfException(TITLE, t);
					} 
					
					return super.getActionResponse(context, subpage_key);
					
				}

}
