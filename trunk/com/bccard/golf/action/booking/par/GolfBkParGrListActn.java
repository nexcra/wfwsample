/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkParGrListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.par;

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
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkParGrListActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ���������α׷� ����Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		String permission = "";
		String memb_id = "";
		String isLogin = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null) {
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
			}

			if(memb_id != null && !"".equals(memb_id)){
				isLogin = "1";
			} else {
				isLogin = "0";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("intMemGrade",		intMemGrade);
			
			/*
			// 04. ���ٱ��� ��ȸ	
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("PAR_3_BOKG_APO_YN");
			}else{
				permission = "N";
			}
			*/
			paramMap.put("permission", permission);

			// 04.���� ���̺�(Proc) ��ȸ
			//if(permission.equals("Y")){
				GolfBkParGrListDaoProc proc = (GolfBkParGrListDaoProc)context.getProc("GolfBkParGrListDaoProc");
				DbTaoResult grListResult = (DbTaoResult) proc.execute(context, request, dataSet);
	
				paramMap.put("resultSize", String.valueOf(grListResult.size()));
				request.setAttribute("GrListResult", grListResult);
			//}
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
