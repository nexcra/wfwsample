/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntKvpPopActn 
*   �ۼ���	: (��)�̵������ ������
*   ����		: KVP event
*   �������	: golf
*   �ۼ�����	: 2010-05-20
*   note :  http://www.golfloung.com/app/golfloung/view/golf/member/ktOlleh/kt_olleh.jsp?serviceFlag=ollehClubGolf
*   		http://develop.golfloung.com:13300/app/golfloung/view/golf/member/ktOlleh/kt_olleh.jsp?serviceFlag=ollehClubGolf
*           �׽�Ʈ�� serviceFlag �� �ƹ����̳� �Ҵ�
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.kvp;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntGolfShowPopDaoProc;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpPopDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntKvpPopActn extends GolfActn{
	
	public static final String TITLE = "KVP event";

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
		
		boolean ktTrue = false;
				
		try {
				
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String idx = parser.getParameter("idx", "");
			String flag = parser.getParameter("flag", "");
			String serviceFlag = parser.getParameter("serviceFlag", "");
			String ollehKtValue = parser.getParameter("ollehKtValue", "");
			String currentBirthDate = parser.getParameter("currentBirthDate", "");
			String firstFlag = parser.getParameter("firstFlag", "");
			String pay = parser.getParameter("pay", "");
			String order_no = parser.getParameter("allat_order_no", "");

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			if (pay.equals("Y")){				
				GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
				order_no = addPayProc.getOrderNo(context, dataSet);				
			}
			
			paramMap.put("order_no", order_no); 	
			
			debug (" #### firstFlag : " + firstFlag + ", serviceFlag : " + serviceFlag + ", ollehKtValue : " + ollehKtValue + ", currentBirthDate : " + currentBirthDate + ", len : " + currentBirthDate.trim().length());
			
			if (firstFlag.equals("Y")){
				
				//���� �÷�Ŭ��ȸ�� 
				if ( serviceFlag != null && !serviceFlag.equals("null")){
				
					if (serviceFlag.trim().equals("ollehClubGolf")){						
						ktTrue  = true;
					}
					
				}
					
				//������(superstar ȸ��)����				
				if ( ollehKtValue != null && !ollehKtValue.equals("null")){	
	
					// R : �÷�Ŭ�� ȸ�� + ������(���۽�Ÿ), M : �÷�Ŭ�� ��ȸ�� + ������(���۽�Ÿ)
					if ( currentBirthDate.trim().length() == 8 && (ollehKtValue.trim().equals("R") || ollehKtValue.trim().equals("M")) ){
						ktTrue  = true; 
					}			
					
				}
				
			}else {
				ktTrue  = true;
			}	
			
			if (ktTrue){				
			
				if (firstFlag.equals("Y")){
					subpage_key = "memdis";		
				}				
	
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)					
				dataSet.setString("idx", idx);
				
				GolfEvntKvpPopDaoProc proc = (GolfEvntKvpPopDaoProc)context.getProc("GolfEvntKvpPopDaoProc");
				DbTaoResult resultKvpPop = proc.execute(context, request, dataSet);	
				
				paramMap.put("idx", idx);
			
				String gds_code 				= parser.getParameter("gds_code", "");
				String name 					= parser.getParameter("name", "");
				String zp1 						= parser.getParameter("zp1", "");
				String zp2 						= parser.getParameter("zp2", "");
				String zipaddr 					= parser.getParameter("addr", "");
				String detailaddr 				= parser.getParameter("dtl_addr", "");
				String addr_clss 				= parser.getParameter("addr_clss", "");
				String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
				String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
				String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
				String gds_code_name 			= parser.getParameter("gds_code_name", "");
				String formtarget 				= parser.getParameter("formtarget", "");
						
				String realPayAmt 				= parser.getParameter("realPayAmt", "");
				String social_id_1 				= parser.getParameter("social_id_1", "");
				String social_id_2 				= parser.getParameter("social_id_2", "");			
				String email 					= parser.getParameter("email", "");
				String ddd_no 					= parser.getParameter("ddd_no", "");
				String tel_hno 					= parser.getParameter("tel_hno", "");
				String tel_sno 					= parser.getParameter("tel_sno", "");
	
				paramMap.put("gds_code", gds_code);
				paramMap.put("name", name);
				paramMap.put("zp1", zp1);
				paramMap.put("zp2", zp2);
				paramMap.put("zipaddr", zipaddr);
				paramMap.put("detailaddr", detailaddr);
				paramMap.put("addr_clss", addr_clss);
				paramMap.put("hp_ddd_no", hp_ddd_no);
				paramMap.put("hp_tel_hno", hp_tel_hno);
				paramMap.put("hp_tel_sno", hp_tel_sno);
				paramMap.put("gds_code_name", gds_code_name);				
				paramMap.put("formtarget", formtarget);
				
				paramMap.put("realPayAmt", realPayAmt);
				paramMap.put("social_id_1", social_id_1);
				paramMap.put("social_id_2", social_id_2);
				paramMap.put("socid", social_id_1 + social_id_2);
				paramMap.put("email", email);
				paramMap.put("ddd_no", ddd_no);
				paramMap.put("tel_hno", tel_hno);
				paramMap.put("tel_sno", tel_sno);
				paramMap.put("serviceFlag", serviceFlag);
				paramMap.put("ollehKtValue", ollehKtValue);
				paramMap.put("currentBirthDate", currentBirthDate);
								
		        request.setAttribute("resultKvpPop", resultKvpPop);		
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
		        
			}else {
				
				subpage_key = "fault";				
				String script = "";
				
				if ( !serviceFlag.trim().equals("ollehClubGolf")
						&& (ollehKtValue.trim().length() == 0) ) {
					script += "alert('KT Olleh Club ȸ���� �ƴմϴ�.');";				
					script += "parent.top.window.close();";
				}				

				
				if ( serviceFlag.trim().length() == 0
						&& ( currentBirthDate.trim().length() != 8 || !(ollehKtValue.trim().equals("R") || ollehKtValue.trim().equals("M") ) ) )
				{
					script += "alert('SuperStar ȸ���� �ƴմϴ�.');";				
					script += "parent.top.window.close();";
				}						
								
				request.setAttribute("script", script);				
		        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.				
		        
			}
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
