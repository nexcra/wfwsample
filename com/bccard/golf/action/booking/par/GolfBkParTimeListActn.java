/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ƼŸ�� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.par;

import java.io.IOException;
import java.util.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPenaltyDaoProc;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreGrViewDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkParTimeListActn extends GolfActn{
	
	public static final String TITLE = "��ŷƼŸ�� ����Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String penalty = "";
		String penalty_start = "";
		String penalty_end = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);	

			// 04-01. ��ŷ ���� ��ȸ
			GolfBkPenaltyDaoProc proc_penalty = (GolfBkPenaltyDaoProc)context.getProc("GolfBkPenaltyDaoProc");
			DbTaoResult penaltyView = proc_penalty.execute(context, dataSet, request);
			
			penaltyView.next();
			if(penaltyView.getString("RESULT").equals("00")){
				penalty = "Y";
				penalty_start = penaltyView.getString("BK_LIMIT_ST");
				penalty_end = penaltyView.getString("BK_LIMIT_ED");
			}else{
				penalty = "N";
			}
			paramMap.put("penalty", penalty);
			paramMap.put("penalty_start", penalty_start);
			paramMap.put("penalty_end", penalty_end);
//			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn ===  penalty => " + penalty);
			
			
			
			// 02. ������ idx ��������
			String affi_GREEN_SEQ_NO = parser.getParameter("AFFI_GREEN_SEQ_NO", "");			
			GolfBkParTimeGrNewViewDaoProc proc3 = (GolfBkParTimeGrNewViewDaoProc)context.getProc("GolfBkParTimeGrNewViewDaoProc");
			DbTaoResult titimeGrNewView = (DbTaoResult) proc3.execute(context, request, dataSet);
			if(affi_GREEN_SEQ_NO.equals("")){
				if (titimeGrNewView != null && titimeGrNewView.isNext()) {
					titimeGrNewView.first();
					titimeGrNewView.next();
					affi_GREEN_SEQ_NO = (String) titimeGrNewView.getObject("MAX_AFFI_GREEN_SEQ_NO");
				}
			}			
			paramMap.put("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);
			
			
			// �ָ���ŷ ��뿩�� �����´�.
			String co_nm = "";	// �ָ���ŷ����
			GolfBkPreGrViewDaoProc proc_weekend = (GolfBkPreGrViewDaoProc)context.getProc("GolfBkPreGrViewDaoProc");
			DbTaoResult grViewResult = proc_weekend.execute_weekend(context, dataSet);
			if (grViewResult != null && grViewResult.isNext()) {
				grViewResult.first();
				grViewResult.next();
				co_nm = (String) grViewResult.getObject("CO_NM");
			}	
			dataSet.setString("co_nm", co_nm);
			

			// 04.���� ���̺�(Proc) ��ȸ - ������
			GolfBkParTimeGrListDaoProc proc2 = (GolfBkParTimeGrListDaoProc)context.getProc("GolfBkParTimeGrListDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);
			
			// 04.���� ���̺�(Proc) ��ȸ - ƼŸ�� ����Ʈ
			GolfBkParTimeListDaoProc proc = (GolfBkParTimeListDaoProc)context.getProc("GolfBkParTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			// 04.���� ���̺�(Proc) ��ȸ - ������
			GolfBkParTimeHolyListDaoProc proc4 = (GolfBkParTimeHolyListDaoProc)context.getProc("GolfBkParTimeHolyListDaoProc");
			DbTaoResult titimeHolyList = (DbTaoResult) proc4.execute(context, request, dataSet);
			request.setAttribute("TitimeHolyList", titimeHolyList);
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
