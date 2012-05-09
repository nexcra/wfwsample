/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMagazineRegFormActn
*   �ۼ���    : (��)�̵������ õ����
*   ����      : �������� ������û��
*   �������  : golf
*   �ۼ�����  : 2009-10-30
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.mania.GolfLimousineDaoProc;
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
public class GolfMagazineRegFormActn extends GolfActn{
	public static final String TITLE = "�������� ������û��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		String permission = "";
		int intMemGrade = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
//			 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}

			
			// 02.�Է°� ��ȸ	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			//03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			debug("---------------------------------------intMemGrade() ::> " + intMemGrade);

			// �̿����� üũ
			if (isLogin.equals("1") ) { // �̰���ȸ�� ���ٱ���
				
				// ���ٱ��� ��ȸ	
				String permissionColum = "MGZ_SBSC_APO_YN";
				GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
				DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

				permissionView.next();
				if(permissionView.getString("RESULT").equals("00")){
					permission = permissionView.getString("LIMT_YN");
					
				}else{
					permission = "N";
				}
				
				
				//���ٱ��� üũ
				if (permission.equals("N")) { // 	
					
					subpage_key = "limitReUrl";
				}
				else
				{
			
					//04.���� ���̺�(Proc) ��ȸ
					GolfLimousineDaoProc coopCpSelProc = (GolfLimousineDaoProc)context.getProc("GolfLimousineDaoProc");
					//DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0012", "Y"); //���޾�ü
					DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet); //���޾�ü
					
					// 05. Return �� ����	
					//@@@@@@@ ���̺� ��û ó���Ϸ� �ɶ����� �ּ�ó��-----------------@request.setAttribute("coopCpSel", coopCpSel);
			        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			        request.setAttribute("coopCpSel", coopCpSel);
			        paramMap.put("userNm", userNm);
			        paramMap.put("memGrade", memGrade);

				} 
			
			
			
			
			}
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
