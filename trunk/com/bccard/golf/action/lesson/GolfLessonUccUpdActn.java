/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfLessonUccDetailActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 레슨 > 친절한 ucc 레슨 처리
*   적용범위	: golf
*   작성일자	: 2009-07-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/  
public class GolfLessonUccUpdActn extends GolfActn{
	
	public static final String TITLE = "레슨 > 친절한 ucc 레슨 처리";
 
	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체.  
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try { 
			
			// 01.세션정보체크
			String userIp = ""; 
			String userId = "";
			String email = "";
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				email 		= (String)usrEntity.getEmail1();
			}
			 userIp = request.getRemoteAddr();
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			
			String bbrd_clss 			= "0022";
			String idx 					= parser.getParameter("idx","");
			String mode 				= parser.getParameter("mode","");
			String titl 				= parser.getParameter("titl","");
			String ctnt 				= parser.getParameter("ctnt","");
			String file_nm 				= parser.getParameter("file_nm","");
			String pic_nm 				= parser.getParameter("pic_nm","");
			String mimeData 			= parser.getParameter("mimeData", "").trim();	// 코스소개
			
			//debug("-------------------------strt 1--------------------------");
			
			
			//입력과 수정의 경우에만 나모에디터 코딩
			if("ins".equals(mode) || "upd".equals(mode)){
				//debug("-------------------------strt 2--------------------------");
				//========================== 파일 업로드 Start =============================================================//
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
				//========================== 파일 업로드 End =============================================================//
				
				// 나모 에디터  MIME 디코딩하기 하기///////////////////////////////////////////////////////////////
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
					mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
					mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
		            mime.decode(mimeData);                     		// MIME 디코딩
		            mime.saveFile();                           		// 포함한 파일 저장하기
		            ctnt = mime.getBodyContent();        			// 내용가져오기
				}
	            /////////////////////////////////////////////////////////////////////////////////////////////
			}
			//debug("-------------------------strt 3--------------------------");
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
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
			
			
			// 04.실제 테이블(Proc) 조회
			GolfLessonUccUpdDaoProc proc = (GolfLessonUccUpdDaoProc)context.getProc("GolfLessonUccUpdDaoProc");
			DbTaoResult lessonUccResult = (DbTaoResult)proc.execute(context, request,dataSet);	
			request.setAttribute("lessonUccResult", lessonUccResult);

			
			// 05.모든 파라미터값을 맵에 담아 반환한다.

	        request.setAttribute("paramMap", paramMap); 	
			
			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
