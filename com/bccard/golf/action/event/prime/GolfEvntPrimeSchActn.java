/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntPrimeIns
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > �ؿ������� > �˻����
*   �������  : Golf
*   �ۼ�����  : 2010-08-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeSchDaoProc;
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
public class GolfEvntPrimeSchActn extends GolfActn{ 
	
	public static final String TITLE = "�̺�Ʈ > �ؿ������� > �˻����";

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
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String userId = "";
			if(usrEntity != null) { 
				userId		= (String)usrEntity.getAccount(); 
			}
							

			// �����̺�Ʈ�˻�
			String bkg_pe_nm					= parser.getParameter("bkg_pe_nm", "");
			String jumin_no1					= parser.getParameter("jumin_no1", "");
			String jumin_no2					= parser.getParameter("jumin_no2", "");
			
			
			String modeType						= parser.getParameter("modeType", "");
			
			
			if("memberCk".equals(modeType))
			{
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.) 
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

				dataSet.setString("bkg_pe_nm", bkg_pe_nm);
				dataSet.setString("jumin_no1", jumin_no1);
				dataSet.setString("jumin_no2", jumin_no2);
				
				GolfEvntPrimeSchDaoProc proc = (GolfEvntPrimeSchDaoProc)context.getProc("GolfEvntPrimeSchDaoProc");
				DbTaoResult aplResult = (DbTaoResult) proc.memberCk(context, request, dataSet);
		        request.setAttribute("aplResult", aplResult);
				
				
			}
			else
			{

				request.getSession().removeAttribute("primeName");
				request.getSession().removeAttribute("primeJumin1");
				request.getSession().removeAttribute("primeJumin2");
				session.setAttribute("primeName",bkg_pe_nm);
				session.setAttribute("primeJumin1",jumin_no1);
				session.setAttribute("primeJumin2",jumin_no2);

		        paramMap.put("bkg_pe_nm", bkg_pe_nm);
		        paramMap.put("jumin_no1", jumin_no1);
		        paramMap.put("jumin_no2", jumin_no2);
		        paramMap.put("jumin_no", jumin_no1+""+jumin_no2);
		        paramMap.put("userId", userId);
				
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.) 
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

				dataSet.setString("bkg_pe_nm", bkg_pe_nm);
				dataSet.setString("jumin_no1", jumin_no1);
				dataSet.setString("jumin_no2", jumin_no2);
				
				// 04.���� ���̺�(Proc) ��ȸ

				
				// �ֹ�����
				String aplc_seq_no = "";
				GolfEvntPrimeSchDaoProc proc = (GolfEvntPrimeSchDaoProc)context.getProc("GolfEvntPrimeSchDaoProc");
				DbTaoResult aplResult = (DbTaoResult) proc.execute(context, request, dataSet);
		        request.setAttribute("aplResult", aplResult);
		        						
				
				// ������ ����	        
		        String rsvt_date = "";		// �����
		        String hadc_num = "";		// �����
		        String rsvt_date2 = "";		// ������
		        String rsv_yn = "N";
		        
				DbTaoResult rsvResult = (DbTaoResult) proc.execute_rsv(context, request, dataSet);	
		        request.setAttribute("rsvResult", rsvResult);
		        
		        if(rsvResult.isNext()){
		        	rsvResult.first();
		        	rsvResult.next();
		        	if(rsvResult.getString("RESULT").equals("00")){
			        	rsvt_date = rsvResult.getString("rsvt_date");
			        	hadc_num = rsvResult.getString("hadc_num");
			        	rsvt_date2 = rsvResult.getString("rsvt_date2");
			        	rsv_yn = "Y";
		        	}
		        }
		        
		        paramMap.put("rsvt_date", rsvt_date);
		        paramMap.put("hadc_num", hadc_num);
		        paramMap.put("rsvt_date2", rsvt_date2);
		        paramMap.put("rsv_yn", rsv_yn);
			}
			
			
	        
	        request.setAttribute("modeType", modeType);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
