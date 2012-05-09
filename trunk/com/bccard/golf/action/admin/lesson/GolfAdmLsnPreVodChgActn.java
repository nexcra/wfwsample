/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmLsnPreVodChgActn
*   �ۼ���	: (��)�̵������
*   ����		: ������ > ���� > �����̾� ������ > ���� ó��
*   �������	: golf
*   �ۼ�����	: 2009-12-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnVodUpdDaoProc;

import java.io.File;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnPreVodChgActn extends GolfActn{
	
	public static final String TITLE = "������  > ���� > �����̾� ������ > ���� ó��";

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

			// Request �� ����
			String seq_no = parser.getParameter("p_idx", "");			
			String vod_clss = parser.getParameter("vod_clss", "");	// �����󱸺��ڵ�
			String vod_lsn_clss = parser.getParameter("vod_lsn_clss", "");	// �����󷹽������ڵ�
			String titl = parser.getParameter("titl", "");	// ����
			String ctnt = parser.getParameter("ctnt", "");	// ����
			String best_yn = parser.getParameter("best_yn", "");	// ����Ʈ����
			String new_yn = parser.getParameter("new_yn", "");	// ���ۿ���
			String vod_nm = parser.getParameter("vod_nm", "");	// ����������
			String img_nm = parser.getParameter("img_nm", "");	// ����� �̹���
			String orgVod_nm = parser.getParameter("orgVod_nm", "");	// ���� ����������
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// ���� ����� �̹���
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH");
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/lesson";		

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
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("VOD_CLSS", vod_clss);	
			dataSet.setString("VOD_LSN_CLSS", vod_lsn_clss);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("BEST_YN", best_yn);
			dataSet.setString("NEW_YN", new_yn);
			dataSet.setString("VOD_NM", vod_nm);
			dataSet.setString("IMG_NM", img_nm);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnVodUpdDaoProc proc = (GolfAdmLsnVodUpdDaoProc)context.getProc("GolfAdmLsnVodUpdDaoProc");
			
			// ���� ������ ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				//========================== ���� ���ε� Start =============================================================//
				if ( !GolfUtil.isNull(vod_nm) && !GolfUtil.isNull(orgVod_nm)) {
                    File realAttch = new File(realPath + subDir, orgVod_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(img_nm) && !GolfUtil.isNull(orgImg_nm)) {
                    File realAttch = new File(realPath + subDir, orgImg_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admLsnPreVodList.do");
				request.setAttribute("resultMsg", "���� �����̾� ������ ������ ���������� ó�� �Ǿ����ϴ�.");      	
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
				request.setAttribute("returnUrl", "admLsnPreVodChgForm.do");
				request.setAttribute("resultMsg", "���� �����̾� ������ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
