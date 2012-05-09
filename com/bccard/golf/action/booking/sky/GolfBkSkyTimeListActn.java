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
package com.bccard.golf.action.booking.sky;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.sky.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfBkSkyTimeListActn extends GolfActn{
	
	public static final String TITLE = "��ŷƼŸ�� ����Ʈ";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		String penalty = "";
		String penalty_start = "";
		String penalty_end = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				intMemGrade = userEtt.getIntMemGrade();
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	
			String click_DATE		= parser.getParameter("CLICK_DATE", "");
			String hole				= parser.getParameter("HOLE", "");			

	        int nYoil = 0;	        
	        GregorianCalendar today = new GregorianCalendar ( );
	        nYoil = today.get ( today.DAY_OF_WEEK );	       
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("intMemGrade",		intMemGrade);
			dataSet.setString("CLICK_DATE", click_DATE);
			dataSet.setString("HOLE", hole);
			dataSet.setInt("nYoil", nYoil);
			dataSet.setInt("intMemGrade", intMemGrade);

			// 04-01. ��ŷ ���� ��ȸ : �г�Ƽ
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

			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBkSkyTimeListDaoProc proc = (GolfBkSkyTimeListDaoProc)context.getProc("GolfBkSkyTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			if(!click_DATE.equals("")){
				GolfBkSkyTimeViewDaoProc proc2 = (GolfBkSkyTimeViewDaoProc)context.getProc("GolfBkSkyTimeViewDaoProc");
				DbTaoResult viewResult = (DbTaoResult) proc2.execute(context, request, dataSet);
				request.setAttribute("ViewResult", viewResult);
			}
	        request.setAttribute("CLICK_DATE", click_DATE);
	        request.setAttribute("paramMap", paramMap);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
