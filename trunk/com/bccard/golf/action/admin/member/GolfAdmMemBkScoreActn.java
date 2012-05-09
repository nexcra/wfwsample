/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemBkScoreActn
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������ >ȸ������>ȸ������Ʈ>��(��ŷ����)
*   �������  : Golf
*   �ۼ�����  : 2011-05-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemBkScoreDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmMemBkScoreActn extends GolfActn{
	
	public static final String TITLE = "������ >ȸ������>ȸ������Ʈ>��(��ŷ����)";

	/***************************************************************************************
	* 
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);		
		String topGolfCardYn 	= "N";		//ž����ī�� ���� ����
		
		boolean result = false ;
		
		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	
			
			/*
			 * top���� ī�� ȸ������ üũ
			 * */			
			String cdhd_ID		= parser.getParameter("CDHD_ID", "");
			String name		= parser.getParameter("NAME", "");
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);		
			dataSet.setString("CDHD_ID", cdhd_ID);			
			
			//ž����ȸ������ ��ȸ 
			GolfAdmMemBkScoreDaoProc proc = (GolfAdmMemBkScoreDaoProc)context.getProc("GolfAdmMemBkScoreDaoProc");			
			result = proc.execute(context, request, dataSet);

			HashMap	getPrveInfo = proc.getMemberInfo(context, dataSet);
			
			if(result){ 
				
				topGolfCardYn = "Y";
				
				// 02.�Է°� ��ȸ		
				int defaultDate			= parser.getIntParameter("defaultDate", 5);
				paramMap.put("defaultDate", String.valueOf(defaultDate));
			      
		        GolfTopGolfCardListDaoProc proc1 = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
				
				dataSet.setString("memId", cdhd_ID);												//ȸ�����̵� 
				dataSet.setInt("memNo", Integer.parseInt(getPrveInfo.get("MEMID").toString()));		//ȸ��������ȣ 10832273
				dataSet.setString("memSocId", getPrveInfo.get("SOCID").toString());					//�ֹ�
				dataSet.setString("memberClss", getPrveInfo.get("MEMBERCLSS").toString());			//MEMBERCLSS			
				dataSet.setString("roundDate", "");
				
				DbTaoResult	getScore = (DbTaoResult) proc1.get_score(context, request, dataSet);

				request.setAttribute("topGolfCardYn", topGolfCardYn);				
				request.setAttribute("getScore", getScore);
				request.setAttribute("taoResult0", getScore);
												
			}
			
			paramMap.put("CDHD_ID", cdhd_ID);
			paramMap.put("NAME", name);
			paramMap.put("topGolfCardYn", topGolfCardYn);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}	
}
