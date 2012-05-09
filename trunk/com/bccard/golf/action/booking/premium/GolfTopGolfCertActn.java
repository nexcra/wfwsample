/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCertCheck
*   �ۼ���    : �̰���
*   ����      : TOP����ī�� ���� ��ŷ> ����ī�� ����
*   �������  : Golf
*   �ۼ�����  : 2010-11-18
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCertPorc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfTopGolfCertActn extends GolfActn{
	

	/**
	 * 
	 */	
	public static final String TITLE = "����ī�� ���� ";

	/***************************************************************************************	
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
								throws IOException, ServletException, BaseException {

		String subpage_key = "default";		

		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		String certId = "", certBizNo = "", certJuminNo = "", updateData = ""; //�������� Ȯ�� �÷���
		
		try {
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			certId = parser.getParameter("hId");
			certBizNo = parser.getParameter("hBizNo");
			certJuminNo = parser.getParameter("hJuminNo");
			
			updateData = parser.getParameter("hConfrim");
			
			if(certId != null && ( certId.equals("notNull") ||  certBizNo.equals("notNull") 
					||  certJuminNo.equals("notNull") ||  updateData.equals("OK") )){
				subpage_key = "certIfr";
			}

			// Proc �� �Ѱ��� �Ķ����
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			// ����ī�� ����
			GolfTopGolfCertPorc proc = (GolfTopGolfCertPorc)context.getProc("GolfTopGolfCertPorc");
			
			//���̵� ����
			if(certId != null && certId.equals("notNull") ){
				
				dataSet.setString("ID", parser.getParameter("id"));				
				dataSet.setString("GUBUN", "ID");
				
				String checkVal = (String)proc.getStateCheck(context, request, dataSet);	
				
				if (checkVal.equals("PRIVATE")){//����ȸ��
					request.setAttribute("certId", "PRIVATE");
				}else if (checkVal.equals("JIJUNG")){//����ī��
					request.setAttribute("certId", "JIJUNG");
				}else { //����ī��
					int cnt = (int)proc.getCount(context, request, dataSet);				
					if(cnt > 0){
						request.setAttribute("certId", "OK");	
					}
				}
			}
			 
			//����ڵ�Ϲ�ȣ ���� -- ���̵�������  ���� (view���� ��Ʈ��)
			if(certBizNo != null && certBizNo.equals("notNull") ){
				
				dataSet.setString("ID", parser.getParameter("hIdV"));
				dataSet.setString("BIZ", parser.getParameter("bizNo1")+parser.getParameter("bizNo2")+parser.getParameter("bizNo3"));		
				dataSet.setString("GUBUN", "BIZ");

				int cnt = (int)proc.getCount(context, request, dataSet);
				
				if(cnt > 0){
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("idV", parser.getParameter("hIdV"));
				}else {//����ڵ�Ϲ�ȣ ������ �ȵǾ ���̵� ������ ������ ��� ���� �־�� ��
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "notNull");
				}
			}	
						
			//�ֹε�Ϲ�ȣ ���� -- ���̵�/����ڵ�Ϲ�ȣ ����  ��  ���� (view���� ��Ʈ��)
			if(certJuminNo != null && certJuminNo.equals("notNull") ){
				
				dataSet.setString("ID", parser.getParameter("hIdV"));
				dataSet.setString("BIZ", parser.getParameter("hBizNoV1")+parser.getParameter("hBizNoV2")+parser.getParameter("hBizNoV3"));
				dataSet.setString("GUBUN", "JUMIN");
			
				int cnt = (int)proc.getCount(context, request, dataSet);
			
				if(cnt > 0){
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("certJumin", "OK");
					
				}else {//�ֹε�� ������ �ȵǾ ���̵�/����ڵ�� ������ ������ ��� ���� �־�� ��
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("certJumin", "notNull");
				}
				
			}		
			
			//Ȯ�ι�ư Ŭ����
			if(updateData != null && updateData.equals("OK") ){
			
				dataSet.setString("ID", parser.getParameter("hIdV"));
				dataSet.setString("JUMIN", parser.getParameter("hJuminNoV1") + parser.getParameter("hJuminNoV2"));
				int cnt = (int) proc.modifyJuminNO(context, request, dataSet);				
				
				if(cnt > 0){
					request.setAttribute("certId", "OK");
					request.setAttribute("certBizNo", "OK");
					request.setAttribute("certJumin", "OK");
					request.setAttribute("conf", "OK");
				}
				
			}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
