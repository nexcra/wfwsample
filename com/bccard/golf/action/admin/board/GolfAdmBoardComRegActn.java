/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComRegActn
*   �ۼ���     : (��)�̵������ ������	
*   ����        : ������ �Խ��� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrRegDaoProc;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.msg.MsgEtt;


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
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.bccard.golf.common.AppConfig;



/******************************************************************************
* Golf
* @author	(��)�̵������   
* @version	1.0
******************************************************************************/
public class GolfAdmBoardComRegActn extends GolfActn  {
	
	public static final String TITLE = "������ �Խ��� ��� ó��";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		ResultException rx;
		GolfAdminEtt userEtt = null;
		String admin_id = "";

		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		//debug("==== GolfAdmBoardComRegActn start ===");
		
		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id	= (String)userEtt.getMemId(); 							
			}
			
			//1. �Ķ��Ÿ �� 
			RequestParser	parser	= context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();	
			
			String bbrd_clss			= parser.getParameter("boardid", "");
			String golf_bokg_faq_clss	= parser.getParameter("GOLF_BOKG_FAQ_CLSS", "");
			String titl					= parser.getParameter("TITL", "");
			String eps_yn				= parser.getParameter("EPS_YN", "");
			String golf_clm_clss		= parser.getParameter("GOLF_CLM_CLSS", "");

			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = AppConfig.getAppProperty("BK_NOTICE");
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			mapDir = mapDir.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");  
            contImgPath = contImgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            contAtcPath = contAtcPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
            if ( contAtcPath == null ) contAtcPath = "";
 			////////////////////////////////////////////////////////////////////////////////////////////////

			String ctnt 				= parser.getParameter("CTNT", "").trim();	
			String mimeData 			= parser.getParameter("mimeData", "").trim();	
			
			/////////////////////////////////////////////////////////////////////////////////////

            
			// ���� ������  MIME ���ڵ��ϱ� �ϱ�
			if(!mimeData.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(mimeData);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            ctnt = mime.getBodyContent();        	// ���밡������
			}
            ////////////////////////////////////////////////////////////////////////////////////

			// ÷������ ���ε�///////////////////////////////////////////////////////////////
			String tmpPath  			= AppConfig.getAppProperty("UPLOAD_TMP_PATH");	
			tmpPath = tmpPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			realPath = realPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			String annx_file_nm 		= parser.getParameter("ANNX_FILE_NM", "").trim();		// ����̹���
			
			if(annx_file_nm != null && !"".equals(annx_file_nm)) setFiles(tmpPath, realPath, annx_file_nm);
            ////////////////////////////////////////////////////////////////////////////////////
            
						
			//2.��ȸ
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("BBRD_CLSS",					bbrd_clss);
			dataSet.setString("GOLF_BOKG_FAQ_CLSS",			golf_bokg_faq_clss);
			dataSet.setString("TITL",						titl);
			dataSet.setString("CTNT",						ctnt);
			dataSet.setString("EPS_YN",						eps_yn);
			dataSet.setString("ANNX_FILE_NM",				annx_file_nm);
			dataSet.setString("golf_clm_clss",				golf_clm_clss);
						
			
			// 3. DB ó�� 
			GolfAdmBoardComRegDaoProc proc = (GolfAdmBoardComRegDaoProc)context.getProc("GolfAdmBoardComRegDaoProc");
			int addResult = proc.execute(context, request, dataSet);

	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        
			debug("bbrd_clss => "+ bbrd_clss);
	        if (bbrd_clss.equals("0001")){
	        	returnUrlTrue = "admBkNotiList.do";
	        	returnUrlFalse = "admBkNotiRegForm.do";
	        }else{
	        	returnUrlTrue = "admBkFaqList.do";
	        	returnUrlFalse = "admBkFaqRegForm.do";
	        }

			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			debug("returnUrl => "+ returnUrlTrue);

			paramMap.put("BBRD_CLSS", bbrd_clss);
			paramMap.put("addResult", String.valueOf(addResult));
			request.setAttribute("paramMap", paramMap);
			

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
