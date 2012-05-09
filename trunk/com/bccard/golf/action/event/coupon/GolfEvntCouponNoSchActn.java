/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntCouponNoSchActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : �̺�Ʈ�����/����������̺�Ʈ/���������̺�Ʈ/�׸�����������->�������� ��ȣ Ȯ�� �˻�
*   �������  : Golf
*   �ۼ�����  : 2011-04-13
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
***************************************************************************************************/
package com.bccard.golf.action.event.coupon;


import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.coupon.GolfEvntCouponNoSchProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntCouponNoSchActn extends GolfActn{
	
	public static final String TITLE = "�������� ��ȣ Ȯ�� �˻�";

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
		
		try { 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2","");
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");			
			
			debug(" / jumin_no1 : " + jumin_no1 + " / jumin_no2 : " + jumin_no2 + " / hp_ddd_no : " + hp_ddd_no
					 + " / hp_tel_hno : " + hp_tel_hno + " / hp_tel_sno : " + hp_tel_sno);
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);

			GolfEvntCouponNoSchProc proc = (GolfEvntCouponNoSchProc)context.getProc("GolfEvntCouponNoSchProc");
			DbTaoResult couponNoList = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("juminno1", jumin_no1);
			paramMap.put("juminno2", jumin_no2);
			paramMap.put("mobile1", hp_ddd_no);
			paramMap.put("mobile2", hp_tel_hno);
			paramMap.put("mobile3", hp_tel_sno);
	        request.setAttribute("couponNoList", couponNoList);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
