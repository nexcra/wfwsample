/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkTimeReserveActn
*   �ۼ���    : �̵������ ������
*   ����      : ��ŷ > xgolf > ����
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.security.cryptography.*;

/******************************************************************************
* Golf
* @author	�̵������ 
* @version	1.0  
******************************************************************************/
public class GolfBkTimeReserveActn extends GolfActn{
	
	public static final String TITLE = "��ŷ > xgolf > ����";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("memb_id");
			paramMap.remove("xFA");
			paramMap.remove("xPrc");
			paramMap.remove("xReser");
			
			String key = "adf34alkjdf";   
			String iv = "efef897akdjfkl";   
			  
			// �ν��Ͻ� �����. 
			StringEncrypter encrypter = new StringEncrypter(key, iv);   
			
			// ���ڿ� ��ȣȭ.   
			String memb_id_enc			= parser.getParameter("memb_id", "").trim();		// ��ȣȭ�� ���̵�
			String memb_id 				= encrypter.decrypt(memb_id_enc);					// ���̵�
			
			String xFA 					= parser.getParameter("xFA", "").trim();		// ���� : 1 / �ָ� : 2 / �׸������� : 3
			String xMsg					= parser.getParameter("xMsg", "1").trim();		// 1 : �Ϲݺ�ŷ�޼���  2:�׸��Ǹ޼���
			String xPrc 				= parser.getParameter("xPrc", "2").trim();		// ����Ʈ���� : 1 / Ƚ������ : 2
			String xReser 				= parser.getParameter("xReser", "").trim();		// ���� : 1 / ��� : 2

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			int intBkGrade = 0;		// �������
			if(xFA.equals("1")){
				DbTaoResult nmWkdView = proc_times.getNmWkdBenefit(context, dataSet, request);
				nmWkdView.next();
				intBkGrade = nmWkdView.getInt("intBkGrade");
			}else if(xFA.equals("2")){
				DbTaoResult nmWkeView = proc_times.getNmWkeBenefit(context, dataSet, request);
				nmWkeView.next();
				intBkGrade = nmWkeView.getInt("intBkGrade");
			}

			debug("======================================================================");
			debug("===========GolfBkTimeReserveActn============memb_id_enc => " + memb_id_enc);
			debug("===========GolfBkTimeReserveActn============memb_id => " + memb_id);
			debug("===========GolfBkTimeReserveActn============xFA => " + xFA);
			debug("===========GolfBkTimeReserveActn============xMsg => " + xMsg);
			debug("===========GolfBkTimeReserveActn============xPrc => " + xPrc);
			debug("===========GolfBkTimeReserveActn============xReser => " + xReser);
			debug("======================================================================");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			dataSet.setString("memb_id", memb_id);
			dataSet.setString("xFA", xFA);
			dataSet.setString("xPrc", xPrc);
			dataSet.setString("xReser", xReser);
			dataSet.setInt("intBkGrade", intBkGrade);
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBkTimeReserveDaoProc proc = (GolfBkTimeReserveDaoProc)context.getProc("GolfBkTimeReserveDaoProc");
			int addResult = proc.execute(context, dataSet, request);	
			
			
	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        String resultMsgTrue = "";
	        String resultMsgFalse = "";
	        
	        if (xReser.equals("1")){
	        	
	        	resultMsgTrue = "����� ���������� ó�� �Ǿ����ϴ�.";
	        	resultMsgFalse = "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.";
	        	
				if (xMsg.equals("1")){
		        	returnUrlTrue = "GolfBkNmEnd.do";
		        	returnUrlFalse = "GolfBkNm.do";
		        }else{
		        	returnUrlTrue = "GolfBkWeekEnd.do";
		        	returnUrlFalse = "GolfBkWeek.do";
		        }
	        }else{
	        	// ����� ���
	        	resultMsgTrue = "��Ұ� ���������� ó�� �Ǿ����ϴ�.";
	        	resultMsgFalse = "��Ұ� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.";
	        	
				if (xMsg.equals("1")){
		        	returnUrlTrue = "GolfBkNmEnd.do";
		        	returnUrlFalse = "GolfBkNm.do";
		        }else{
		        	returnUrlTrue = "GolfBkWeekEnd.do";
		        	returnUrlFalse = "GolfBkWeek.do";
		        }
	        }
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", resultMsgTrue);      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", resultMsgFalse);		        		
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
