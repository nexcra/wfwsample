/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCouponRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ���� ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.File;
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmCouponInsDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmCouponRegActn extends GolfActn{
	
	public static final String TITLE = "������ ���� ��� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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

			String cupn_clss = parser.getParameter("cupn_clss", "");	// �����ڵ�
			String cupn_nm = parser.getParameter("cupn_nm", "");	// ������
			long cupn_dc_rt = parser.getLongParameter("cupn_dc_rt", 0L);	// ������
			String img_nm = parser.getParameter("img_nm", "");	// �����̹���
			
			/*
			MimeData mime = new MimeData();
			mime.decode(care_mttr); // MIME ���ڵ�
			care_mttr = mime.getBodyContent();    // ���밡������
			*/
			//debug("care_mttr :: >> " + care_mttr);
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String subDir = "/drivrange";

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
			dataSet.setString("CUPN_CLSS", cupn_clss);
			dataSet.setString("CUPN_NM", cupn_nm);
			dataSet.setLong("CUPN_DC_RT", cupn_dc_rt);
			dataSet.setString("IMG_NM", img_nm);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmCouponInsDaoProc proc = (GolfAdmCouponInsDaoProc)context.getProc("GolfAdmCouponInsDaoProc");
			
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);
			
			//debug("addResult :::: >>>> " + addResult);
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "admCouponList.do");
				request.setAttribute("resultMsg", "���� ����� ���������� ó�� �Ǿ����ϴ�.");      	
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
				request.setAttribute("returnUrl", "admCouponRegForm.do");
				request.setAttribute("resultMsg", "���� ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
