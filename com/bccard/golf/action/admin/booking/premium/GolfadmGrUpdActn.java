/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

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
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfadmGrUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹골프장 수정 처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면 
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id	= (String)userEtt.getMemId(); 							
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String sort 				= parser.getParameter("SORT", "").trim();		// 부킹골프장구분코드
			long seq_no					= parser.getLongParameter("SEQ_NO", 0L);
			long page_no				= parser.getLongParameter("page_no", 0L);
			String gr_mn 				= parser.getParameter("GR_NM", "").trim();		// 골프장명
			String rl_green_nm			= parser.getParameter("RL_GREEN_NM", "").trim();// 실골프장명
			String gr_url 				= parser.getParameter("GR_URL", "").trim();		// 골프장URL
			String gr_addr 				= parser.getParameter("GR_ADDR", "").trim();	// 골프장주소
			String course 				= parser.getParameter("COURSE", "").trim();		// 코스/규모
			String subequip 			= parser.getParameter("SUBEQUIP", "");			// 부대시설
			int del_limit 				= parser.getIntParameter("DEL_LIMIT", 0);		// 부킹취소가능기간
			int per_limit 				= parser.getIntParameter("PER_LIMIT", 0);		// 최대접수인원
			String corr_mgr_seq_no 		= admin_id;
			String pic1_DESC			= parser.getParameter("PIC1_DESC", "").trim();	// 갤러리1파일 설명
			String pic2_DESC			= parser.getParameter("PIC2_DESC", "").trim();	// 갤러리2파일 설명
			String pic3_DESC			= parser.getParameter("PIC3_DESC", "").trim();	// 갤러리3파일 설명
			String pic4_DESC			= parser.getParameter("PIC4_DESC", "").trim();	// 갤러리4파일 설명
			String pic5_DESC			= parser.getParameter("PIC5_DESC", "").trim();	// 갤러리5파일 설명
			String pic6_DESC			= parser.getParameter("PIC6_DESC", "").trim();	// 갤러리6파일 설명
			String gr_INFO 				= parser.getParameter("GR_INFO", "").trim();	// 골프장설명
			String gr_NOTI 				= parser.getParameter("GR_NOTI", "").trim();	// 골프장 공지사항
			String max_ACPT_PNUM 		= parser.getParameter("MAX_ACPT_PNUM", "").trim();	// 최대접대인원
			
			String banner 				= parser.getParameter("BANNER", "").trim();		// 배너이미지
			String map_nm 				= parser.getParameter("MAP_NM", "").trim();		// 약도이미지
			String pic1 				= parser.getParameter("PIC1", "").trim();	// 갤러리1파일
			String pic2					= parser.getParameter("PIC2", "").trim();	// 갤러리2파일
			String pic3 				= parser.getParameter("PIC3", "").trim();	// 갤러리3파일
			String pic4 				= parser.getParameter("PIC4", "").trim();	// 갤러리4파일
			String pic5 				= parser.getParameter("PIC5", "").trim();	// 갤러리5파일
			String pic6 				= parser.getParameter("PIC6", "").trim();	// 갤러리6파일
			String main_BANNER_IMG		= parser.getParameter("MAIN_BANNER_IMG", "").trim();	// 메인배너이미지
			String main_EPS_YN 			= parser.getParameter("MAIN_EPS_YN", "").trim();	// 메인노출여부
			String co_nm				= parser.getParameter("CO_NM", "");					// 주말부킹 사용여부
			
			String old_BANNER 				= parser.getParameter("OLD_BANNER", "").trim();		// 배너이미지
			String old_MAP_NM 				= parser.getParameter("OLD_MAP_NM", "").trim();		// 약도이미지
			String old_PIC1 				= parser.getParameter("OLD_PIC1", "").trim();	// 갤러리1파일
			String old_PIC2					= parser.getParameter("OLD_PIC2", "").trim();	// 갤러리2파일
			String old_PIC3 				= parser.getParameter("OLD_PIC3", "").trim();	// 갤러리3파일
			String old_PIC4 				= parser.getParameter("OLD_PIC4", "").trim();	// 갤러리4파일
			String old_PIC5 				= parser.getParameter("OLD_PIC5", "").trim();	// 갤러리5파일
			String old_PIC6 				= parser.getParameter("OLD_PIC6", "").trim();	// 갤러리6파일
			String old_MAIN_BANNER_IMG		= parser.getParameter("OLD_MAIN_BANNER_IMG", "").trim();	// 메인배너이미지
			

			debug("pic6 -> " + pic6 + " / old_PIC6 -> " + old_PIC6); 
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터 이미지용 설정					
			// imgPath : 실제 컨텐츠 안의 이미지가 저장되는 폴더이름
			// 게시판 마다 업로드될 이미지 폴더 imgPath 설정은 : /WEB-INF/config/config.xml 에서 추가하세요.
			String imgPath = AppConfig.getAppProperty("BK_GREEN");
			imgPath = imgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
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

			String course_INFO 			= parser.getParameter("COURSE_INFO", "").trim();	// 코스소개
			String mimeData 			= parser.getParameter("mimeData", "").trim();	// 코스소개
			String ch_INFO 				= parser.getParameter("CH_INFO", "").trim();	// 골프장 요금안내
			String mimeData2 			= parser.getParameter("mimeData2", "").trim();	// 골프장 요금안내
			
			/////////////////////////////////////////////////////////////////////////////////////

			// 나모 에디터  MIME 디코딩하기 하기
			if(!mimeData.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
				mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
	            mime.decode(mimeData);                     		// MIME 디코딩
	            mime.saveFile();                           		// 포함한 파일 저장하기
	            course_INFO = mime.getBodyContent();        	// 내용가져오기
			}

			if(!mimeData2.equals("")){
	            MimeData mime2 = new MimeData();
	            mime2.setSaveURL ( mapDir + imgPath );				// 이미지조회URL 셋팅
	            mime2.setSavePath( contImgPath + imgPath );			// 파일 저장 경로 셋팅			
	            mime2.decode(mimeData2);                     		// MIME 디코딩
	            mime2.saveFile();                           		// 포함한 파일 저장하기
	            ch_INFO = mime2.getBodyContent();        			// 내용가져오기
			}
            ////////////////////////////////////////////////////////////////////////////////////

			// 첨부파일 업로드///////////////////////////////////////////////////////////////
			String tmpPath  			= AppConfig.getAppProperty("UPLOAD_TMP_PATH");	
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			if(!old_BANNER.equals(banner)) setFiles(tmpPath, realPath, banner);
			if(!old_MAP_NM.equals(map_nm)) setFiles(tmpPath, realPath, map_nm);
			if(!old_PIC1.equals(pic1)) setFiles(tmpPath, realPath, pic1);
			if(!old_PIC2.equals(pic2)) setFiles(tmpPath, realPath, pic2);
			if(!old_PIC3.equals(pic3)) setFiles(tmpPath, realPath, pic3);
			if(!old_PIC4.equals(pic4)) setFiles(tmpPath, realPath, pic4);
			if(!old_PIC5.equals(pic5)) setFiles(tmpPath, realPath, pic5);
			if(!old_PIC6.equals(pic6)) setFiles(tmpPath, realPath, pic6);
			if(!old_MAIN_BANNER_IMG.equals(main_BANNER_IMG)) setFiles(tmpPath, realPath, main_BANNER_IMG);
            ////////////////////////////////////////////////////////////////////////////////////

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("SEQ_NO", seq_no);
			dataSet.setString("GR_NM", gr_mn);				// 골프장명
			dataSet.setString("RL_GREEN_NM", rl_green_nm);	// 실골프장명
			dataSet.setString("GR_URL", gr_url);	// 골프장URL
			dataSet.setString("GR_INFO", gr_INFO);	// 골프장설명
			dataSet.setString("GR_ADDR", gr_addr);	// 골프장주소
			dataSet.setString("COURSE", course);	// 코스/규모
			dataSet.setString("COURSE_INFO", course_INFO);	// 코스소개
			dataSet.setString("SUBEQUIP", subequip);	// 부대시설
			dataSet.setInt("DEL_LIMIT", del_limit);	// 부킹취소가능기간
			dataSet.setInt("PER_LIMIT", per_limit);	// 최대접수인원
			dataSet.setString("corr_mgr_seq_no", corr_mgr_seq_no);	// 수정관리자일련번호
			dataSet.setString("PIC1_DESC", pic1_DESC);	// 갤러리1파일 설명
			dataSet.setString("PIC2_DESC", pic2_DESC);	// 갤러리2파일 설명
			dataSet.setString("PIC3_DESC", pic3_DESC);	// 갤러리3파일 설명
			dataSet.setString("PIC4_DESC", pic4_DESC);	// 갤러리4파일 설명
			dataSet.setString("PIC5_DESC", pic5_DESC);	// 갤러리5파일 설명
			dataSet.setString("PIC6_DESC", pic6_DESC);	// 갤러리6파일 설명
			dataSet.setString("GR_NOTI", gr_NOTI);	// 골프장 공지사항
			dataSet.setString("CH_INFO", ch_INFO);	// 골프장 요금안내
			dataSet.setString("MAX_ACPT_PNUM", max_ACPT_PNUM);	// 일일최대접대인원
			
			dataSet.setString("BANNER", banner);	// 배너이미지
			dataSet.setString("MAP_NM", map_nm);	// 약도이미지
			dataSet.setString("PIC1", pic1);	// 갤러리1파일
			dataSet.setString("PIC2", pic2);	// 갤러리2파일
			dataSet.setString("PIC3", pic3);	// 갤러리3파일
			dataSet.setString("PIC4", pic4);	// 갤러리4파일
			dataSet.setString("PIC5", pic5);	// 갤러리5파일
			dataSet.setString("PIC6", pic6);	// 갤러리6파일
			dataSet.setString("MAIN_BANNER_IMG", main_BANNER_IMG);	// 메인배너이미지
			dataSet.setString("MAIN_EPS_YN", main_EPS_YN);	// 메인노출여부
			dataSet.setString("CO_NM", co_nm);	// 주말부킹 사용여부
			
			// 04.실제 테이블(Proc) 조회
			GolfadmGrUpdDaoProc proc = (GolfadmGrUpdDaoProc)context.getProc("admGrUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);		        

	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        
			if (sort.equals("0001") || sort.equals("1000")){
	        	returnUrlTrue = "admGrList.do";
	        	returnUrlFalse = "admGrList.do";
	        }else{
	        	returnUrlTrue = "admParList.do";
	        	returnUrlFalse = "admParList.do";
	        }
			
			if (editResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
	/**
	 * 파일저장
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
			
		// 템프에서 리얼디렉토리로 이동
		tmp.renameTo(real);
		tmp_s.renameTo(real_s);
	}	

}
