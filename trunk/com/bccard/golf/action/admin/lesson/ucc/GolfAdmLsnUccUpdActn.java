/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfAdmLsnUccDetailActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ������ >  ���� > UCC ���� �󼼺���
*   �������	: golf
*   �ۼ�����	: 2009-07-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson.ucc;

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
import com.bccard.golf.dbtao.proc.admin.lesson.ucc.GolfAdmLsnUccUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmLsnUccUpdActn extends GolfActn{
	
	public static final String TITLE = "������ ���� UCC �󼼺���";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü.  
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		GolfAdminEtt userEtt = null;
		String subpage_key = "default";	
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
		

			// Request �� ����
			String bbrd_clss 	= "0022";
			String idx 			= parser.getParameter("idx","");
			String mode 		= parser.getParameter("mode","");
			String ctnt 		= parser.getParameter("ctnt","");
			String answ_ctnt    = parser.getParameter("answ_ctnt","");
			String hg_nm	    = parser.getParameter("hg_nm","");
			String eps_yn	    = parser.getParameter("eps_yn","");
			String mimeData 	= parser.getParameter("mimeData", "").trim();	// �ڽ��Ұ�
			
			String search_clss 	= parser.getParameter("search_clss","");
			String search_word 	= parser.getParameter("search_word","");
			String search_answ 	= parser.getParameter("search_answ","");
			String page_no		= parser.getParameter("page_no","1");
			
			//�Է°� ������ ��쿡�� ���𿡵��� �ڵ�
			if("upd".equals(mode)){
				// ���� ������  MIME ���ڵ��ϱ� �ϱ�///////////////////////////////////////////////////////////////
				String imgPath 				= AppConfig.getAppProperty("BK_LSN_UCC");
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
			dataSet.setString("bbrd_clss", 	bbrd_clss);
			dataSet.setString("idx", 		idx);
			dataSet.setString("mode", 		mode);
			dataSet.setString("admId", 		admId);
			dataSet.setString("ctnt", 		ctnt);
			dataSet.setString("answ_ctnt", 	answ_ctnt);
			dataSet.setString("hg_nm", 		hg_nm);
			dataSet.setString("eps_yn", 	eps_yn);
			
		
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnUccUpdDaoProc proc = (GolfAdmLsnUccUpdDaoProc)context.getProc("GolfAdmLsnUccUpdDaoProc");
			DbTaoResult lessonUccResult = (DbTaoResult)proc.execute(context,request ,dataSet);
			request.setAttribute("lessonUccResult", lessonUccResult);	
			
			
			// ��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		
			paramMap.put("title", TITLE);
			paramMap.put("idx", idx);	
			paramMap.put("mode", mode);		
			paramMap.put("search_clss", search_clss);
			paramMap.put("search_word", search_word);
			paramMap.put("search_answ", search_answ);
			paramMap.put("page_no", 	page_no);
	        request.setAttribute("paramMap", paramMap); 	
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
