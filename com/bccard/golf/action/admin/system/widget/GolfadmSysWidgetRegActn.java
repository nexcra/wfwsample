/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmSysWidgetRegActn
*   �ۼ���    : �̵������ ������
*   ����      : ������ > �ý��� > ���� ���
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.system.widget;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.system.widget.*;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0
******************************************************************************/
public class GolfadmSysWidgetRegActn extends GolfActn{
	
	public static final String TITLE = "������ > �ý��� > ���� ���";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id	= (String)userEtt.getMemId(); 							
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);			
            

			// ÷������ ���ε�///////////////////////////////////////////////////////////////
			//String imgPath 				= AppConfig.getAppProperty("WIDGET");
			String imgPath 				= "/widget";
			String tmpPath  			= AppConfig.getAppProperty("UPLOAD_TMP_PATH");	
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			realPath = realPath.replaceAll("\\.\\.","");
			String annx_file_nm			= parser.getParameter("ANNX_FILE_NM", "").trim();		// ����̹���
			
			if(annx_file_nm != null && !"".equals(annx_file_nm)) setFiles(tmpPath, realPath, annx_file_nm);
            ////////////////////////////////////////////////////////////////////////////////////
			
			
			String eps_yn 				= parser.getParameter("EPS_YN", "").trim();
			String mvpt_annx_file_path 	= parser.getParameter("MVPT_ANNX_FILE_PATH", "").trim();
			
			//debug("EPS_YN => " + EPS_YN);
			//debug("ANNX_FILE_NM => " + ANNX_FILE_NM);
			//debug("MVPT_ANNX_FILE_PATH => " + MVPT_ANNX_FILE_PATH);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("EPS_YN", eps_yn);
			dataSet.setString("ANNX_FILE_NM", annx_file_nm);
			dataSet.setString("MVPT_ANNX_FILE_PATH", mvpt_annx_file_path);
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmSysWidgetRegDaoProc proc = (GolfadmSysWidgetRegDaoProc)context.getProc("GolfadmSysWidgetRegDaoProc");
			int addResult = proc.execute(context, dataSet);			
			
	        String returnUrlTrue = "admSysWidgetList.do";
	        String returnUrlFalse = "admSysWidgetRegForm.do";
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
	/**
	 * ��������
	 */
	private void setFiles(String tmpPath, String realPath, String imgName)throws BaseException {

		File real_mk = new File(realPath);
		if (!real_mk.exists()) {
			real_mk.mkdirs();
		}

		File tmp = new File(tmpPath, imgName);
		File real = new File(realPath, imgName);
		File tmp_s = new File(tmpPath, "S_"+imgName);
		File real_s = new File(realPath, "S_"+imgName);
			
		// �������� ������丮�� �̵�
		tmp.renameTo(real);
		tmp_s.renameTo(real_s);
	}	

}
