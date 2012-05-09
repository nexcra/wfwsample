/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��û ���
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.sky;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.sky.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfBkSkyTimeRegFormActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ��û ���� Ȯ��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String permission = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			int cyberMoney = 0;
			String userNm = "";
			String userId = "";
			int intMemGrade = 0;
			
			
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				cyberMoney = userEtt.getCyberMoney();
				userNm = userEtt.getName();
				userId = (String)userEtt.getAccount();
				intMemGrade = userEtt.getIntMemGrade();
			}

			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String time_SEQ_NO			= parser.getParameter("TIME_SEQ_NO", "");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("TIME_SEQ_NO", time_SEQ_NO);					

			// 04.���� ���̺�(Proc)
			GolfBkSkyTimeRegFormDaoProc proc = (GolfBkSkyTimeRegFormDaoProc)context.getProc("GolfBkSkyTimeRegFormDaoProc");
			DbTaoResult rsView = proc.execute(context, dataSet);		

			// 05. ��ŷ Ƚ�� ��ȸ - ���̹� �Ӵ� ��볻���� ȸ������ �����ȴ�.
			int drds_BOKG_YR = 0;	// �Ϲ����ߺ�ŷȽ��
			int drds_BOKG_MO = 0;	// �Ϲ��ָ���ŷȽ��
			
//			GolfBkSkyTimesDaoProc proc_times = (GolfBkSkyTimesDaoProc)context.getProc("GolfBkSkyTimesDaoProc");
//			DbTaoResult timesView = proc_times.execute(context, dataSet, request);
//			timesView.next();
//			if(timesView.getString("RESULT").equals("00")){
//				drds_BOKG_YR = timesView.getString("DRDS_BOKG_YR");
//				drds_BOKG_MO = timesView.getString("DRDS_BOKG_MO");
//			}

			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult skyView = proc_times.getSkyBenefit(context, dataSet, request);
			skyView.next();
			drds_BOKG_YR = skyView.getInt("DRDS_BOKG_YR");
			drds_BOKG_MO = skyView.getInt("DRDS_BOKG_MO");
			cyberMoney = skyView.getInt("CY_MONEY");

			//���ٱ��� üũ	
			String permissionColum = "DRDS_BOKG_LIMT_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				
			}else{
				permission = "N";
			}	
			debug(" >>>>>>> permission : "+permission);
			paramMap.put("permission", permission);	
			paramMap.put("cyberMoney", cyberMoney+"");	
			paramMap.put("userNm", userNm);	
			paramMap.put("intMemGrade", intMemGrade+"");	
			paramMap.put("DRDS_BOKG_YR", drds_BOKG_YR+"");	
			paramMap.put("DRDS_BOKG_MO", drds_BOKG_MO+"");
	        
			
			// 05. Return �� ����
	        request.setAttribute("TIME_SEQ_NO", time_SEQ_NO);
	        request.setAttribute("RsView", rsView); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
