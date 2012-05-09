/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBkListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ŷ �̺�Ʈ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-06-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfEvntBkListActn extends GolfActn{
	
	public static final String TITLE = "��ŷ �̺�Ʈ ����Ʈ";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}
			 
			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String sgr_nm			= parser.getParameter("sgr_nm", "");
			String sevent_yn		= parser.getParameter("sevent_yn", "");

			String sort			= parser.getParameter("SORT", "0001"); //0001:�����̾� 0002:��3��ŷ
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SGR_NM", sgr_nm);
			dataSet.setString("SEVENT_YN", sevent_yn);
			dataSet.setString("SORT", sort);
			
			// �̿����� üũ
			if (isLogin.equals("1") && intMemGrade < 4) {
			
				// 04.���� ���̺�(Proc) ��ȸ
				GolfEvntBkListDaoProc proc = (GolfEvntBkListDaoProc)context.getProc("GolfEvntBkListDaoProc");
				GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
				DbTaoResult evntPreBkTimeListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				DbTaoResult preBkEvntDateResult = (DbTaoResult) proc.getPreBkEvntDate(context, request, dataSet);
				DbTaoResult titimeGreenListResult = (DbTaoResult) proc2.execute(context, request, dataSet);
	
				if (preBkEvntDateResult != null && preBkEvntDateResult.isNext()) {
					preBkEvntDateResult.first();
					preBkEvntDateResult.next();
					
					if (preBkEvntDateResult.getObject("EVNT_STRT_DATE").equals("")) {
						request.setAttribute("returnUrl", "golfEvntBkDateEnd.do");
						request.setAttribute("resultMsg", "");      
						subpage_key = "errorUrl";
					}
				}
				
				paramMap.put("resultSize", String.valueOf(evntPreBkTimeListResult.size()));
				
				request.setAttribute("evntPreBkTimeListResult", evntPreBkTimeListResult);
				request.setAttribute("preBkEvntDateResult", preBkEvntDateResult);
				request.setAttribute("titimeGreenListResult", titimeGreenListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
			} else {
				subpage_key = "limitReUrl";
			}
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
