/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardUpdActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : �Խ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBoardUpdActn extends GolfActn  {
	
	public static final String TITLE = "�Խ��� ó��";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;
		GolfUserEtt ett = null;

		//debug("==== GolfBoardUpdActn start ===");
		
		try {
			
			GolfUtil cstr = new GolfUtil();
			
			RequestParser	parser	= context.getRequestParser("default", request, response);
					
			//1.��������üũ
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
			
			
			//2. �Ķ��Ÿ �� 
			String search_yn		= parser.getParameter("search_yn", "N");					// �˻�����		
			String search_clss	= "";
			String search_word	= "";
			String sdate 			= "";
			String edate 			= "";
			
			if("Y".equals(search_yn))
			{
				search_clss			= parser.getParameter("search_clss");					// �˻�����
				search_word			= parser.getParameter("search_word");					// �˻���
				sdate 				= parser.getParameter("sdate");							// �˻����۳�¥
				edate 				= parser.getParameter("edate");
			}
			
			long page_no			= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size			= parser.getLongParameter("page_size", 10L);			// ����������¼�	
			
			String subject			= cstr.nl2br(parser.getParameter("subject", ""));			// �� ����
			String eps_yn			= parser.getParameter("eps_yn", "Y");		// ���� ����		
			String mode				= parser.getParameter("mode", "ins");		// ó������
			String p_idx			= parser.getParameter("p_idx", "");			// �� �Ϸù�ȣ
			String boardid			= parser.getParameter("boardid", "");		// �Խ��ǹ�ȣ
			String cate_seq_no 		= parser.getParameter("cate_seq_no","");	// ī�װ� ��ȣ		
			
	

			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = AppConfig.getAppProperty("BK_NOTICE");
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");         
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            if ( contAtcPath == null ) contAtcPath = "";
 			////////////////////////////////////////////////////////////////////////////////////////////////

			String cTNT 			= parser.getParameter("CTNT", "").trim();	// �ڽ��Ұ�
			String mimeData 			= parser.getParameter("mimeData", "").trim();	// �ڽ��Ұ�

 			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������  MIME ���ڵ��ϱ� �ϱ�
			if(!mimeData.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(mimeData);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            cTNT = mime.getBodyContent();        	// ���밡������
			}
            ////////////////////////////////////////////////////////////////////////////////////

			
			
			//2.��ȸ
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
			
			// 3. DB ó�� 
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
