/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLessonUccDetailActn
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ���� > ģ���� ucc ���� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.lesson.GolfLessonUccUpdDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/  
public class GolfLessonUccUpdActn extends GolfActn{
	
	public static final String TITLE = "���� > ģ���� ucc ���� ó��";
 
	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try { 
			
			// 01.��������üũ
			String userIp = ""; 
			String userId = "";
			String email = "";
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				email 		= (String)usrEntity.getEmail1();
			}
			 userIp = request.getRemoteAddr();
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			
			String bbrd_clss 			= "0022";
			String idx 					= parser.getParameter("idx","");
			String mode 				= parser.getParameter("mode","");
			String titl 				= parser.getParameter("titl","");
			String ctnt 				= parser.getParameter("ctnt","");
			String file_nm 				= parser.getParameter("file_nm","");
			String pic_nm 				= parser.getParameter("pic_nm","");
			String mimeData 			= parser.getParameter("mimeData", "").trim();	// �ڽ��Ұ�
			
			//debug("-------------------------strt 1--------------------------");
			
			
			//�Է°� ������ ��쿡�� ���𿡵��� �ڵ�
			if("ins".equals(mode) || "upd".equals(mode)){
				//debug("-------------------------strt 2--------------------------");
				//========================== ���� ���ε� Start =============================================================//
				String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
				realPath = realPath.replaceAll("\\.\\.","");
				String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH");
				tmpPath = tmpPath.replaceAll("\\.\\.","");
				String subDir = "/lesson";

				if ( !GolfUtil.isNull(file_nm)) {
	                File tmp = new File(tmpPath,file_nm); 
	                if ( tmp.exists() ) {

	        			File createPath  =	new	File(realPath + subDir);
	        			if (!createPath.exists()){
	        				createPath.mkdirs();
	        			}

	                    String name = file_nm.substring(0, file_nm.lastIndexOf('.'));
	                    String ext = file_nm.substring(file_nm.lastIndexOf('.'));

	                    File listAttch = new File(createPath, file_nm);
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
				//debug("-------------------------strt 3--------------------------");
				//========================== ���� ���ε� End =============================================================//
				
				// ���� ������  MIME ���ڵ��ϱ� �ϱ�///////////////////////////////////////////////////////////////
				String imgPath 	="/lesson";
				String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");	
				mapDir = mapDir.replaceAll("\\.\\.","");
				if ( mapDir == null )  mapDir = "/";
	            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
	            
	            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");    
	            contImgPath = contImgPath.replaceAll("\\.\\.","");
	            if ( contImgPath == null ) contImgPath = "";   
	            
	            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
	            contAtcPath = contAtcPath.replaceAll("\\.\\.","");
	            if ( contAtcPath == null ) contAtcPath = "";   
	            
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
			//debug("-------------------------strt 3--------------------------");
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("idx",    				idx);
			dataSet.setString("bbrd_clss",  			bbrd_clss);
			dataSet.setString("mode", 		 			mode);
			dataSet.setString("titl",  					titl);
			dataSet.setString("ctnt",  					ctnt);
			dataSet.setString("userId",  				userId);
			dataSet.setString("userIp",  				userIp);
			dataSet.setString("email",  				email);
			dataSet.setString("mvpt_annx_file_path",	pic_nm);
			dataSet.setString("annx_file_nm",  			file_nm);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfLessonUccUpdDaoProc proc = (GolfLessonUccUpdDaoProc)context.getProc("GolfLessonUccUpdDaoProc");
			DbTaoResult lessonUccResult = (DbTaoResult)proc.execute(context, request,dataSet);	
			request.setAttribute("lessonUccResult", lessonUccResult);

			
			// 05.��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.

	        request.setAttribute("paramMap", paramMap); 	
			
			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
