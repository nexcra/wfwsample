/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBsRegFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ������
*   ����      : ������ BC Golf �̺�Ʈ ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSLessonRegDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmBsRegFormActn extends GolfActn{
	
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
		debug("----------------------    in ---------------------------------------");
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
			
			long seq = parser.getLongParameter("seq", 0L);
            String evnt_nm 		= parser.getParameter("evnt_nm", "");
            String img_nm 		= parser.getParameter("evnt_img", "");
            String les_st 		= parser.getParameter("les_st", "");
            String les_en 		= parser.getParameter("les_en", "");
            String les_pay_nor 	= parser.getParameter("les_pay_nor", "");
            String les_pay_dc 	= parser.getParameter("les_pay_dc", "");
            String evnt_st 		= parser.getParameter("evnt_st", "");
            String evnt_en 		= parser.getParameter("evnt_en", "");
            String mo_pe 		= parser.getParameter("mo_pe", "");
            String ctnt 		= parser.getParameter("ctnt", "");
            String disp_yn 		= parser.getParameter("disp_yn", "");
            String evnt_bnf 	= parser.getParameter("evnt_bnf", "");
            String affi_firm 	= parser.getParameter("affi_firm", "");
            
            debug("ctnt : "+ctnt);
            
			if(!"".equals(les_st)){
				if(les_st.indexOf("-") > 0) les_st = les_st.replaceAll("-", "");
			}
			if(!"".equals(les_en)){
				if(les_en.indexOf("-") > 0) les_en = les_en.replaceAll("-", "");
			}
			if(!"".equals(evnt_st)){
				if(evnt_st.indexOf("-") > 0) evnt_st = evnt_st.replaceAll("-", "");
			}
			if(!"".equals(evnt_en)){
				if(evnt_en.indexOf("-") > 0) evnt_en = evnt_en.replaceAll("-", "");
			}
			
			debug("===================================  1===============================================");
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH");
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/event";

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
            dataSet.setLong("EVNT_SEQ_NO", seq);
            dataSet.setString("EVNT_NM", evnt_nm);
            dataSet.setString("EVNT_ST", evnt_st);
            dataSet.setString("EVNT_EN", evnt_en);
            dataSet.setString("LES_ST", les_st);
            dataSet.setString("LES_EN", les_en);
            dataSet.setString("LES_PAY_NOR", les_pay_nor);
            dataSet.setString("LES_PAY_DC", les_pay_dc);
            dataSet.setString("IMG_NM", img_nm);
            dataSet.setString("MO_PE", mo_pe);
            dataSet.setString("CTNT", ctnt);
            dataSet.setString("DISP_YN", disp_yn); 
            dataSet.setString("EVNT_BNF", evnt_bnf);
            dataSet.setString("AFFI_FIRM", affi_firm);
            
            GolfAdmSLessonRegDaoProc proc = (GolfAdmSLessonRegDaoProc)context.getProc("GolfAdmSLessonRegDaoProc");
            
            debug("===================================  2===============================================");
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            int addResult = proc.execute(context,request, dataSet,paramMap);			
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "/app/golf/admEvntBcList.do");
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
				request.setAttribute("returnUrl", "/app/golf/admEvntBcRegForm.do");
				request.setAttribute("resultMsg", "���� BC Golf �̺�Ʈ ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
	        debug("===================================  3===============================================");
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
