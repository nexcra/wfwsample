/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPreTimeListDaoProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������ �����̾� ƼŸ�� ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2010-12-29
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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopBkngStatisDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfadmTopBkngApplyStatisActn extends GolfActn{
 

	public static final String TITLE = "��ŷ��û�����";
	
	/***************************************************************************************
	 * ��ž����Ʈ ������ȭ�� 
	 * @param context  WaContext ��ü. 
	 * @param request  HttpServletRequest ��ü. 
	 * @param response  HttpServletResponse ��ü. 
	 * @return ActionResponse Action ó���� ȭ�鿡 ���÷����� ����. 
	 ***************************************************************************************/	 
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
	
		String subpage_key = "default"; 
		  
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		  
		try {
			// 01.��������üũ
			   
			// 02.�Է°� ��ȸ  
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			String mode = parser.getParameter("mode", "INIT");
			String diff = parser.getParameter("diff", "0");
			String yyyy = parser.getParameter("yyyy");
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");
			String objClss = parser.getParameter("bkngObjClss", "0");
			String repMbNo = parser.getParameter("repMbNo", "00");
			String bkngObjClssNm = parser.getParameter("bkngObjClssNm" );
			String repMbNoNm = parser.getParameter("repMbNoNm");
			
			String memberClss = parser.getParameter("parm1");
			String bkngStat   = parser.getParameter("parm2");
			   
			paramMap.put("diff", diff);
			paramMap.put("yyyy", yyyy);
			paramMap.put("from", from);
			paramMap.put("to", to);
			paramMap.put("bkngObjClss", objClss);
			paramMap.put("repMbNo", repMbNo);
			paramMap.put("bkngObjClssNm", bkngObjClssNm);
			paramMap.put("repMbNoNm", repMbNoNm);
			paramMap.put("memberClss", memberClss);
			paramMap.put("bkngStat", bkngStat);
			
			debug (" test----------------diff[" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"],objClss[" + objClss +"],repMbNo[" + repMbNo +"]");
			debug (" test----------------bkngObjClssNm[" + bkngObjClssNm +"],repMbNoNm[" + repMbNoNm +"],memberClss[" + memberClss +"],bkngStat[" + bkngStat +"]");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);   
			debug (" @@@@@@@@@@====01 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
//			if (mode.equals("EXCELDETAIL")){
//				dataSet.setString("memberClss", memberClss);
//				dataSet.setString("bkngStat", bkngStat);	  
//			}else {
//				dataSet.setString("diff", diff);
//				dataSet.setString("yyyy", yyyy);
//				dataSet.setString("from", from);
//				dataSet.setString("to", to);
//			}
//			
//			dataSet.setString("bkngObjClss", objClss);     
//			dataSet.setString("repMbNo", repMbNo);
			
			
			dataSet.setString("memberClss", memberClss);
			dataSet.setString("bkngStat", bkngStat);	  
		
			dataSet.setString("diff", diff);
			dataSet.setString("yyyy", yyyy);
			dataSet.setString("from", from);
			dataSet.setString("to", to);
			
			
			dataSet.setString("bkngObjClss", objClss);     
			dataSet.setString("repMbNo", repMbNo);
			debug (" @@@@@@@@@@====02 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
			
			GolfadmTopBkngStatisDaoProc instance = GolfadmTopBkngStatisDaoProc.getInstance();
			   
			DbTaoResult listResult = null;
			DbTaoResult listResult2 = null;
			debug (" @@@@@@@@@@====03 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
			debug (" test----------------mode : " + mode);
			   
			if (!"INIT".equals(mode)) {
			    
				// 04.���� ���̺�(Proc) ��ȸ - ����Ʈ
				if (mode.equals("EXCELDETAIL")){
					debug (" @@@@@@@@@@====1 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");	
					listResult = instance.excelDetail(context, request, dataSet);
				}else {					
				    listResult = instance.execute(context, request, dataSet);
				}
				
				request.setAttribute("BkngApplyStatis", listResult);
			    
			}
				
			//ȸ���� ��ȸ
			listResult2 = instance.getGreenList(context, request, dataSet);
			
			request.setAttribute("TitimeGreenList", listResult2);
			request.setAttribute("paramMap", paramMap);
			   
			if (mode.equals("EXCEL")) { subpage_key = "excel"; }
			if (mode.equals("EXCELDETAIL")) { subpage_key = "excelDetail"; }
			if (mode.equals("PRINT")) { subpage_key = "print"; } 
		         
		} catch(Throwable t) {
			debug(TITLE, t);
			t.printStackTrace(); 
		    throw new GolfException(TITLE, t);
		} 
		  
		return super.getActionResponse(context, subpage_key);

	
	} 
  

}