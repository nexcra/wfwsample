/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkWinChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �����̾� ��ŷ �̺�Ʈ ��÷�� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-27
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBkWinUpdDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkWinChgActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ��÷�� ���� ó��";

	/***************************************************************************************
	* ���� ������ȭ��
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

			String seq_no	= parser.getParameter("p_idx", "");
			String evnt_seq_no = parser.getParameter("evnt_seq_no", "");
			String titl = parser.getParameter("titl", "");	// ����Ʈ ����
			String ctnt = parser.getParameter("ctnt", "");	// ����			
			String disp_yn = parser.getParameter("disp_yn", "");	// �Խÿ���

			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = "/event";
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			mapDir = mapDir.replaceAll("\\.\\.","");
			
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH"); 
            contImgPath = contImgPath.replaceAll("\\.\\.","");
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            contAtcPath = contAtcPath.replaceAll("\\.\\.","");
            if ( contAtcPath == null ) contAtcPath = "";
            
			// ���� ������  MIME ���ڵ��ϱ� �ϱ�
			if(!ctnt.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(ctnt);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            ctnt = mime.getBodyContent();        	// ���밡������
			}
            ////////////////////////////////////////////////////////////////////////////////////

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("EVNT_SEQ_NO", evnt_seq_no);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("DISP_YN", disp_yn);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBkWinUpdDaoProc proc = (GolfAdmEvntBkWinUpdDaoProc)context.getProc("GolfAdmEvntBkWinUpdDaoProc");
			
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);			
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "admEvntBkWinList.do");
				request.setAttribute("resultMsg", "�����̾� ��ŷ �̺�Ʈ ��÷�� ������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", "admEvntBkWinChgForm.do");
				request.setAttribute("resultMsg", "�����̾� ��ŷ �̺�Ʈ ��÷�� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
}
