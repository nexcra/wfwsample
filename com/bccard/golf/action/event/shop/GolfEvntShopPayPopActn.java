/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopPayPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ���� �˾� 
*   �������  : Golf
*   �ۼ�����  : 2010-03-08
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
*20110323  �̰��� 	���̽�ĳ�� ����
*20110425  �̰��� 	��������3Ȧ�� + �������ø�Ʈ��Ʈ
*20120307 SHIN CHEONG GWI �������� : ���̽�ĳ�� ����--> ���������̽��� ����
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntShopPayPopActn extends GolfActn{
	
	public static final String TITLE = "���� ����Ʈ";

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

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		
		try { 

			// 02.�Է°� ��ȸ	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = parser.getParameterMap();
			paramMap.put("title", TITLE);
			
			String gds_code			= "";												// ��ǰ�ڵ�
			//String gds_code			= parser.getParameter("gds_code", "");			// ��ǰ�ڵ�
			//String sgl_lst_itm_code	= parser.getParameter("sgl_lst_itm_code", "");	// �ɼ�
			int int_atm				= 0;											// ��ǰ�ݾ�
			int total_atm			= 0;											// �����ݾ�
			String gds_nm			= "";											// ��ǰ��

			int productPrice		= parser.getIntParameter("productPrice",0);		// ��ǰ����
			int qty					= parser.getIntParameter("qty",0);				// ����
			String userNm 			= parser.getParameter("userNm", "");			// ȸ���̸�
			String flag				= parser.getParameter("flag","");				// ����

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			//dataSet.setString("gds_code", gds_code);

			// 04.���� ���̺�(Proc) ��ȸ
			// ��ǰ ���� ��������
			/*
			GolfEvntShopViewDaoProc proc = (GolfEvntShopViewDaoProc)context.getProc("GolfEvntShopViewDaoProc");
			DbTaoResult goodsEtt = (DbTaoResult) proc.execute(context, request, dataSet);
			if (goodsEtt !=null && goodsEtt.isNext() && goodsEtt.size() > 0) {
				goodsEtt.next();		
				int_atm = goodsEtt.getInt("INT_AMT");
				gds_nm = goodsEtt.getString("GDS_NM");
				total_atm = int_atm*qty;
			}
			
			paramMap.put("gds_code", gds_code);
			paramMap.put("gds_nm", gds_nm);
			paramMap.put("sgl_lst_itm_code", sgl_lst_itm_code);
			paramMap.put("qty", qty+"");
			paramMap.put("total_atm", total_atm+"");*/
			
			/*��ü���� ���� ���μ��� ���� �ӽ� �������� ����
			 * ���� Ȯ��� �ٽ� ��üȭ�ؾ���
			 * ���� �Ʒ� ��ǰ �ϳ��� �ϵ��ڵ���
			*/
			if (!flag.equals("B")){ 
				/* 2012.03.07 �ּ�
				gds_nm = "���̽�ĳ��";
				gds_code = "2011040101";
				*/
				gds_nm = "���������̽�";		// 2012.03.07 �߰�
				gds_code = "2011040103";
				
				paramMap.put("userNm", userNm);
				paramMap.put("gds_code", gds_code);			
			}else {
				gds_nm = "��������3Ȧ�� + �������ø�Ʈ��Ʈ";
				gds_code = "2011040102";
				paramMap.put("userNm", userNm);
				paramMap.put("gds_code", gds_code);	
			}
			
			total_atm = productPrice * qty;
			
			paramMap.put("gds_nm", gds_nm);
			paramMap.put("total_atm", total_atm+"");
			paramMap.put("userNm", userNm);
			
			// �ֹ��ڵ� ��������
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			String order_no = addPayProc.getOrderNo(context, dataSet);
			paramMap.put("order_no", order_no);
			
			debug("GolfEvntShopPayPopActn :: order_no : " + order_no + " / int_atm : " + int_atm+ " / qty : " + qty+ " / total_atm : " + total_atm 
					+ " / gds_code : " + gds_code + " / gds_nm : " + gds_nm);

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
