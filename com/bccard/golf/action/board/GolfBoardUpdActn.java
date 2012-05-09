/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardUpdActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 게시판 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.board;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.board.GolfBoardUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfBoardUpdActn extends GolfActn  {
	
	public static final String TITLE = "게시판 처리";
	
	/***************************************************************************************
	* 비씨골프 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		GolfUserEtt ett = null;

		//debug("==== GolfBoardUpdActn start ===");
		
		try {
			
			GolfUtil cstr = new GolfUtil();
			
			RequestParser	parser	= context.getRequestParser("default", request, response);
					
			//1.세션정보체크
			HttpSession session = request.getSession(false);
			if(session != null ) {
				Object obj = session.getAttribute("SESSION_USER");
				if(obj !=null) 	{
					ett =(GolfUserEtt)obj;
				}

			}
			String board_writer_nm = "";
			String board_input_id = "";
			
			if(ett != null) {
				board_writer_nm  = ett.getMemNm();
				board_input_id	 = ett.getMemId();
			}
			
			String ip_addr = request.getRemoteAddr();
			
			
			//2. 파라메타 값 
			String search_yn		= parser.getParameter("search_yn", "N");					// 검색여부		
			String search_clss	= "";
			String search_word	= "";
			String sdate 			= "";
			String edate 			= "";
			
			if("Y".equals(search_yn))
			{
				search_clss			= parser.getParameter("search_clss");					// 검색구분
				search_word			= parser.getParameter("search_word");					// 검색어
				sdate 				= parser.getParameter("sdate");							// 검색시작날짜
				edate 				= parser.getParameter("edate");
			}
			
			long page_no			= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size			= parser.getLongParameter("page_size", 10L);			// 페이지당출력수	
			
			String subject			= cstr.nl2br(parser.getParameter("subject", ""));			// 글 제목
			String eps_yn			= parser.getParameter("eps_yn", "Y");		// 공개 여부		
			String mode				= parser.getParameter("mode", "ins");		// 처리구분
			String p_idx			= parser.getParameter("p_idx", "");			// 글 일련번호
			String boardid			= parser.getParameter("boardid", "");		// 게시판번호
			String cate_seq_no 		= parser.getParameter("cate_seq_no","");	// 카테고리 번호		
			
	

			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터 이미지용 설정					
			// imgPath : 실제 컨텐츠 안의 이미지가 저장되는 폴더이름
			// 게시판 마다 업로드될 이미지 폴더 imgPath 설정은 : /WEB-INF/config/config.xml 에서 추가하세요.
			String imgPath = AppConfig.getAppProperty("BK_NOTICE");
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");         
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            if ( contAtcPath == null ) contAtcPath = "";
 			////////////////////////////////////////////////////////////////////////////////////////////////

			String cTNT 			= parser.getParameter("CTNT", "").trim();	// 코스소개
			String mimeData 			= parser.getParameter("mimeData", "").trim();	// 코스소개

 			////////////////////////////////////////////////////////////////////////////////////////////////
			// 나모 에디터  MIME 디코딩하기 하기
			if(!mimeData.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// 이미지조회URL 셋팅
				mime.setSavePath( contImgPath + imgPath );		// 파일 저장 경로 셋팅			
	            mime.decode(mimeData);                     		// MIME 디코딩
	            mime.saveFile();                           		// 포함한 파일 저장하기
	            cTNT = mime.getBodyContent();        	// 내용가져오기
			}
            ////////////////////////////////////////////////////////////////////////////////////

			
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn))
			{
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
				input.setString("sdate",	sdate);
				input.setString("edate",	edate);
			}
			input.setString("mode",		mode);
			
			input.setString("subject",		subject);
			input.setString("CTNT",			cTNT);
			input.setString("eps_yn",		eps_yn);			
			input.setString("board_writer_nm",		board_writer_nm);
			input.setString("board_input_id",		board_input_id);
			input.setString("ip_addr",		ip_addr);
			
			input.setString("p_idx",			p_idx);
			input.setString("boardid",		boardid);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			input.setString("cate_seq_no",  cate_seq_no);
			
			
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB 처리 
			GolfBoardUpdDaoProc proc = (GolfBoardUpdDaoProc)context.getProc("GolfBoardUpdDaoProc");
			DbTaoResult boardInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("boardInq", boardInq);						
			request.setAttribute("boardid", boardid);		
			request.setAttribute("mode", mode);	
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfBoardUpdActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return super.getActionResponse(context);
		
	}
}
