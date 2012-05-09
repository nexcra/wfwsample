/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntSpRecvPayActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: �̺�Ʈ����� > Ư���� �����̺�Ʈ > ���Ǵ�÷���� ����ȳ�
*   �������	: golf
*   �ۼ�����	: 2009-07-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.special.myprize;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;
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
public class GolfEvntSpRecvPayActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ����� > Ư���� �����̺�Ʈ > ���Ǵ�÷���� ����ȳ�";
 
	/***************************************************************************************
	* ��ž����Ʈ ������ȭ�� 
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String juminno = "";  
		String memGrade = ""; 
		String userSex = "";
		
		int intMemGrade = 0; 
		int myPointResult =  0;
		
		// 00.���̾ƿ� URL ���� 
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try { 
			// 01.��������üũ
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				userSex		= (String)usrEntity.getSex();
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
				
			}
			 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			String realPayAmt	= parser.getParameter("realPayAmt","0");
			String p_idx		= parser.getParameter("p_idx","");
			String evnt_seq_no	= parser.getParameter("evnt_seq_no","");
			String evnt_nm		= parser.getParameter("evnt_nm","");
			String reg_aton		= parser.getParameter("reg_aton","");
			String status		= parser.getParameter("status","");
			String email		= parser.getParameter("email","");
			String hp_ddd_no	= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno	= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno	= parser.getParameter("hp_tel_sno","");

			//�ݾ��� ,�κ� ����
			realPayAmt = realPayAmt.trim();
			
			// ���� jsp ���� ���
			Random rand = new Random();
		    String st = String.valueOf( rand.nextInt(99999999) );
		    session.setAttribute("ParameterManipulationProtectKey",st);
			
		    // 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SESS_CSTMR_ID", userId);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfPaymentInqDaoProc proc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
		    
			//���� ����Ʈ���� �������� jolt
			if(juminno != null && !"".equals(juminno)) {
				myPointResult = (int)proc.getMyPointInfo(context, request, juminno);
			}
			
			//��¥����
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDate = String.valueOf(cal.get(Calendar.DATE));
			
			
			// 05.��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("p_idx", p_idx);
			paramMap.put("evnt_seq_no", evnt_seq_no);
			paramMap.put("reg_aton", reg_aton);
			paramMap.put("status", status);
			paramMap.put("evnt_nm", evnt_nm);
			paramMap.put("email", email);
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("userSex", userSex);
			paramMap.put("nowMonth", nowMonth);
			paramMap.put("nowDate", nowDate);
			paramMap.put("myPoint", String.valueOf(myPointResult));
			paramMap.put("realPayAmt", realPayAmt);
			paramMap.put("ParameterManipulationProtectKey", st);
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
