/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComUpdActn
*   �ۼ���     : (��)�̵������ ������	
*   ����        : ������ �Խ��� ���� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.board;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.board.GolfAdmBoardComUpdDaoProc;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.msg.MsgEtt;


/******************************************************************************
* Golf 
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmBoardComUpdActn extends GolfActn  {
	
	public static final String TITLE = "������ �Խ��� ���� ó��";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
 
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		ResultException rx;

		//debug("==== GolfAdmBoardComUpdActn start ===");
		
		try {
			RequestParser parser = context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String search_yn		= parser.getParameter("search_yn", "N");				// ó������
			String search_clss	= "";
			String search_word	= "";
			if("Y".equals(search_yn)){
				search_clss			= parser.getParameter("search_clss");						// �˻�����
				search_word			= parser.getParameter("search_word");						// �˻���
			}
			long page_no			= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size			= parser.getLongParameter("page_size", 10L);			// ����������¼�	

			String idx			= parser.getParameter("idx", "");
			String bbrd_clss			= parser.getParameter("boardid", "");
			String golf_bokg_faq_clss	= parser.getParameter("GOLF_BOKG_FAQ_CLSS", "");
			String titl					= parser.getParameter("TITL", "");
			String eps_yn				= parser.getParameter("EPS_YN", "");
			String annx_file_nm			= parser.getParameter("ANNX_FILE_NM", "");
			String old_annx_file_nm		= parser.getParameter("OLD_ANNX_FILE_NM", "");
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
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			if(!old_annx_file_nm.equals(annx_file_nm)) setFiles(tmpPath, realPath, annx_file_nm);
            ////////////////////////////////////////////////////////////////////////////////////
			
			//2.��ȸ
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				dataSet.setString("search_clss",	search_clss);
				dataSet.setString("search_word",	search_word);
			}
			
			dataSet.setLong("page_no",		page_no);
			dataSet.setLong("page_size",		page_size);	

			dataSet.setString("idx",				idx);
			dataSet.setString("BBRD_CLSS",					bbrd_clss);
			dataSet.setString("GOLF_BOKG_FAQ_CLSS",			golf_bokg_faq_clss);
			dataSet.setString("TITL",						titl);
			dataSet.setString("CTNT",						ctnt);
			dataSet.setString("EPS_YN",						eps_yn);
			dataSet.setString("ANNX_FILE_NM",				annx_file_nm);
			dataSet.setString("golf_clm_clss",				golf_clm_clss);
			
			Map paramMap = parser.getParameterMap();
				
			// 3. DB ó�� 
			GolfAdmBoardComUpdDaoProc proc = (GolfAdmBoardComUpdDaoProc)context.getProc("GolfAdmBoardComUpdDaoProc"); 
			int addResult = proc.execute(context, request, dataSet);

	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        
			if (bbrd_clss.equals("0001")){
	        	returnUrlTrue = "admBkNotiList.do";
	        	returnUrlFalse = "admBkNotiList.do";
	        }else{
	        	returnUrlTrue = "admBkFaqList.do";
	        	returnUrlFalse = "admBkFaqList.do";
	        }

			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }

			paramMap.put("BBRD_CLSS", bbrd_clss);
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmBoardComUpdActn end ===");

		} catch(Throwable t) {
			//debug("==== GolfAdmBoardComUpdActn ���� Error ===");
			return errorHandler(context,request,response,t);
		}finally{
		}
		return super.getActionResponse(context);
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