/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBnstBaseFormActn
*   �ۼ���    : ������
*   ����      : �̺�Ʈ ����� > ���� ����� �̺�Ʈ > �������� �̺�Ʈ > ������û
*   �������  : Golf
*   �ۼ�����  : 2010-10-05
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100524	������	6�� �̺�Ʈ
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntMngBaseDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntBnstBaseFormActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ ����� > ���� ����� �̺�Ʈ > �������� �̺�Ʈ > ������û ��";

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

		// ���� ����
		String script = "";
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String mobile = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		//String chdh_non_cdhd_clss = "2";	// 1:ȸ��, 2:��ȸ��
		
		// ����ȸ ����
		/*String green_nm = "���򺣳׽�Ʈ ����Ŭ��";		// �������
		String actnKey = super.getActionKey(context);
		String green_id = "bn";
		String evt_type = "A";					// A: ���������� ��� �̺�Ʈ, B:6�� ȸ���ǳ� �̺�Ʈ 
		String evt_month = "05";*/
		
		
		try { 
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/benest");
			
			
			
			// param�������� SEQ_NO
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			String seq_no			= parser.getParameter("seq_no", "");	//����ȸ ��ȣ
			dataSet.setString("seq_no", seq_no);
			debug("@@@@@@@seq_no : "+seq_no);
			// 03.���� ���̺�(Proc) ��ȸ 
			GolfEvntMngBaseDaoProc proc = (GolfEvntMngBaseDaoProc)context.getProc("GolfEvntMngBaseDaoProc");
			
			// 04. ��û�� �󼼺���
			DbTaoResult appResult = (DbTaoResult) proc.execute_app_view(context, request, dataSet);
			
			// 04-01 ��û��¥ ����Ʈ�� ����
			DbTaoResult dateResult = (DbTaoResult) proc.execute_datelist(context, request, dataSet);
			
			
			request.setAttribute("dateResult" , dateResult);
			request.setAttribute("appResult", appResult);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("seq_no", seq_no);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
			/*if(actnKey.equals("GolfEvntBnst2RegForm")){
				green_nm = "�Ŷ� ����Ŭ��";
				green_id = "sr";
				evt_month = "05";
			}else if(actnKey.equals("GolfEvntMonthly1006ARegForm")){
				green_nm = "�Ŷ� ����Ŭ��";
				green_id = "evt";
				evt_type = "A";
				evt_month = "06";
			}else if(actnKey.equals("GolfEvntMonthly1006BRegForm")){
				green_nm = "�Ŷ� ����Ŭ��";
				green_id = "evt";
				evt_type = "B";
				evt_month = "06";
			}else if(actnKey.equals("GolfEvntMonthly1006CRegForm")){
				green_nm = "������Ʈ��Ŭ��";
				green_id = "evt";
				evt_type = "C";
				evt_month = "06";
			}else if(actnKey.equals("GolfEvntMonthly1006DRegForm")){
				green_nm = "����þ�";
				green_id = "evt";
				evt_type = "D";
				evt_month = "06";
			}else if(actnKey.equals("GolfEvntMonthly1007ARegForm")){
				green_nm = "�Ŷ� ����Ŭ��";
				green_id = "sr";
				evt_month = "07";
			}else if(actnKey.equals("GolfEvntMonthly1008ARegForm")){
				green_nm = "�̺쵥��";
				green_id = "even";
				evt_month = "08";
				evt_type = "B";
			}else if(actnKey.equals("GolfEvntMonthly1007BRegForm")){
				green_nm = "�Ŷ� ����Ŭ��";
				green_id = "sr";
				evt_month = "09";
				evt_type = "A";
			}else if(actnKey.equals("GolfEvntMonthly1008BRegForm")){
				green_nm = "�̺쵥��";
				green_id = "even";
				evt_month = "09";
				evt_type = "B";
			}else if(actnKey.equals("GolfEvntMonthly1009ARegForm")){
				green_nm = "��ũ����"; 
				green_id = "oak";
				evt_month = "09";
				evt_type = "C";
			}else if(actnKey.equals("GolfEvntMonthly1009BRegForm")){ 
				green_nm = "��ũ����(����)"; 
				green_id = "oak2";
				evt_month = "09";
				evt_type = "D"; 
			}else if(actnKey.equals("GolfEvntMonthly1008CRegForm")){
				green_nm = "�̺쵥��"; 
				green_id = "even";
				evt_month = "10";
				evt_type = "B";
			}else if(actnKey.equals("GolfEvntMonthly1007CRegForm")){
				green_nm = "�Ŷ����"; 
				green_id = "sr";
				evt_month = "10";
				evt_type = "A";
			}
			
			debug("green_nm : " + green_nm + " / actnKey : " + actnKey + " / green_id : " + green_id + " / evt_type : " + evt_type + " / evt_month : " + evt_month);

			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);

				mobile 	= (String)usrEntity.getMobile(); 
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				chdh_non_cdhd_clss = "1";
				
			}
			
			paramMap.put("userNm", userNm); 
			paramMap.put("userId", userId);
			paramMap.put("juminno1", juminno1);
			paramMap.put("juminno2", juminno2);
			paramMap.put("mobile1", mobile1);
			paramMap.put("mobile2", mobile2);
			paramMap.put("mobile3", mobile3);
			paramMap.put("chdh_non_cdhd_clss", chdh_non_cdhd_clss);
			paramMap.put("green_nm", green_nm);
			paramMap.put("green_id", green_id);
			paramMap.put("evt_type", evt_type);
			paramMap.put("evt_month", evt_month);
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}*/
