/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBcRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ BC Golf �̺�Ʈ ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBcInsDaoProc;

import java.io.File;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBcRegActn extends GolfActn{
	
	public static final String TITLE = "������ BC Golf �̺�Ʈ ��� ó��";

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
		String admin_no = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String evnt_nm = parser.getParameter("evnt_nm", "");	// �̺�Ʈ��
			String evnt_from = parser.getParameter("evnt_from", "");	// �̺�Ʈ��������
			String evnt_to = parser.getParameter("evnt_to", "");	// �̺�Ʈ��������
			String img_nm = parser.getParameter("img_nm", "");	// �����
			String titl = parser.getParameter("titl", "");	// ����Ʈ ����
			String ctnt = parser.getParameter("ctnt", "");	// ����			
			String disp_yn = parser.getParameter("disp_yn", "");	// �Խÿ���
			String bltn_strt_date = parser.getParameter("bltn_strt_date", "");	// �̺�Ʈ�ԽñⰣ
			String bltn_end_date = parser.getParameter("bltn_end_date", "");	// �̺�Ʈ�ԽñⰣ
			
			

			/*
			if (!GolfUtil.isNull(ctnt)) {
				MimeData mime = new MimeData();
				mime.decode(ctnt); // MIME ���ڵ�
				ctnt = mime.getBodyContent();    // ���밡������
			}
			*/ 
			
			evnt_from = evnt_from.length() == 10 ? DateUtil.format(evnt_from, "yyyy-MM-dd", "yyyyMMdd"): "";
			evnt_to = evnt_to.length() == 10 ? DateUtil.format(evnt_to, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			bltn_strt_date = bltn_strt_date.length() == 10 ? DateUtil.format(bltn_strt_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			bltn_end_date = bltn_end_date.length() == 10 ? DateUtil.format(bltn_end_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH");
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "event";

			if ( !GolfUtil.isNull(img_nm)) {
                File tmp = new File(tmpPath,img_nm);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = img_nm.substring(0, img_nm.lastIndexOf('.'));
                    String ext = img_nm.substring(img_nm.lastIndexOf('.'));

                    File listAttch = new File(createPath, img_nm);
                    int i=0;
                    while ( listAttch.exists() ) {
                    	listAttch = null;
                    	listAttch = new File(createPath, name + String.valueOf(i) + ext );
                        i++;
                    }

                    if ( tmp.renameTo(listAttch) ) {
            			tmp.delete();
                    }
                }				
			}
			//========================== ���� ���ε� End =============================================================//
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_no);		
			dataSet.setString("EVNT_NM", evnt_nm);	
			dataSet.setString("EVNT_FROM", evnt_from);
			dataSet.setString("EVNT_TO", evnt_to);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("DISP_YN", disp_yn);
			dataSet.setString("BLTN_STRT_DATE", bltn_strt_date);
			dataSet.setString("BLTN_END_DATE", bltn_end_date);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntBcInsDaoProc proc = (GolfAdmEvntBcInsDaoProc)context.getProc("GolfAdmEvntBcInsDaoProc");
			
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);			
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "/app/golfloung/admEvntBcList.do");
				request.setAttribute("resultMsg", "���� BC Golf �̺�Ʈ ����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				//========================== ���� ���ε� Start =============================================================//
				if ( !GolfUtil.isNull(img_nm)) {
                    File tmpAttch = new File(tmpPath, img_nm);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, img_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "/app/golfloung/admEvntBcRegForm.do");
				request.setAttribute("resultMsg", "���� BC Golf �̺�Ʈ ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
