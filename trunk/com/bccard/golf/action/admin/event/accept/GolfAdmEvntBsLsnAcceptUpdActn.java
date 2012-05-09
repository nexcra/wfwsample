/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmEvntBsLsnAcceptUpdActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ > �̺�Ʈ >Ư������ �̺�Ʈ >��÷�ڰԽ��ǰ��� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.accept;

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
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.accept.GolfAdmEvntBsLsnAcceptUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBsLsnAcceptUpdActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ >Ư������ �̺�Ʈ >��÷�ڰԽ��ǰ��� ó��";

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
		String admId = "";
		 
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admId	= (String)userEtt.getMemId(); 							
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String evnt_clss 	= "0003";
			String golf_svc_aplc_clss = "0005";
			String p_idx 		= parser.getParameter("p_idx","");
			String mode 		= parser.getParameter("mode","");
			String bltn_yn 		= parser.getParameter("bltn_yn","");
			String search_evnt 	= parser.getParameter("search_evnt","");
			String search_word 	= parser.getParameter("search_word","");
			String search_clss 	= parser.getParameter("search_clss","");
			String search_eps 	= parser.getParameter("search_eps","");
			String mimeData 	= parser.getParameter("mimeData","");
			String ctnt 		= parser.getParameter("ctnt","");
			String titl 		= parser.getParameter("titl","");
			String evnt_seq_no 	= parser.getParameter("search_evnt","");
			
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
		
			//�Է°� ������ ��쿡�� ���𿡵��� �ڵ�
			if("ins".equals(mode) || "upd".equals(mode)){
				// ���� ������  MIME ���ڵ��ϱ� �ϱ�///////////////////////////////////////////////////////////////
				String imgPath 				= AppConfig.getAppProperty("EVNT_ACCEPT");
				imgPath = imgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
				String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");		
				mapDir = mapDir.replaceAll("\\.\\.","");
				if ( mapDir == null )  mapDir = "/";
	            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
	            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");
	            contImgPath = contImgPath.replaceAll("\\.\\.","");
	            if ( contImgPath == null ) contImgPath = "";   
	            
	            if(!mimeData.equals("")){
		            MimeData mime = new MimeData();
					mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
					mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
		            mime.decode(mimeData);                     		// MIME ���ڵ�
		            mime.saveFile();                           		// ������ ���� �����ϱ�
		            ctnt = mime.getBodyContent();        			// ���밡������
				} 
	            /////////////////////////////////////////////////////////////////////////////////////////////
			}
		
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("evnt_clss", 			evnt_clss);
			dataSet.setString("golf_svc_aplc_clss",	golf_svc_aplc_clss);
			dataSet.setString("p_idx",				p_idx);
			dataSet.setString("mode",				mode);
			dataSet.setString("bltn_yn",			bltn_yn);
			dataSet.setString("ctnt",				ctnt);
			dataSet.setString("titl",				titl);
			dataSet.setString("evnt_seq_no",		evnt_seq_no);
			dataSet.setString("admId",				admId);
			
		
			// 04.���� ���̺�(Proc) ó��
			GolfAdmEvntBsLsnAcceptUpdDaoProc proc = (GolfAdmEvntBsLsnAcceptUpdDaoProc)context.getProc("GolfAdmEvntBsLsnAcceptUpdDaoProc");
			DbTaoResult boardResult = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("boardResult", boardResult);	
			
			
			//��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
			paramMap.put("mode", 		mode);
			paramMap.put("search_word", search_word);
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_eps",  search_eps); 
			paramMap.put("search_evnt", search_evnt);
			paramMap.put("page_no", 	Long.toString(page_no));
	        request.setAttribute("paramMap", paramMap); 		
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}

}
