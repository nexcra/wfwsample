/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : FileDownloadServ
*   작성자    :
*   내용      : 파일다운로드 서블릿
*   적용범위  : etax
*   작성일자  : 2007.01.10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/*************************************************************
 * 파일다운로드 서블릿
 * @author
 * @version 2007.01.10
 ************************************************************/
public class FileDownloadServ extends HttpServlet{
	/************************************************************
	* GET 요청 처리
	* @param request HttpServletRequest
	* @param response HttpServletResponse
	* @version 2007.01.10
	* @exception ServletException, IOException
	*************************************************************/
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			HttpSession sess	= request.getSession(false);

			String fileobj = (String)sess.getAttribute("fileobj");
			String fname = (String)sess.getAttribute("fname");
			String fdname = (String)sess.getAttribute("fdname");
			sess.removeAttribute("fileobj");
			sess.removeAttribute("fname");
			sess.removeAttribute("fdname");

			if (fname != null && fname.length() > 0) {
				InputStream file_data = new FileInputStream(new File(fileobj) );

				response.setContentType("application/x-msdownload");
				response.setHeader("Content-Disposition","attachment; filename=" + new String(fdname.getBytes(),"ISO8859_1"));
				ServletOutputStream os = response.getOutputStream();

				byte[] buffer = new byte[1024*100];
				int numberRead;
				while((numberRead = file_data.read(buffer)) > 0) {
					os.write(buffer, 0, numberRead);
				}
				os.close();
				file_data.close();
			}
		}catch(Throwable t) {
			
		}
	}

	/************************************************************
	* POST 요청 처리
	* @param request HttpServletRequest
	* @param response HttpServletResponse
	* @version 2007.01.10
	* @exception ServletException, IOException
	*************************************************************/
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			doGet(request, response);
		
		}catch (Throwable t) {
			
		}
	}
}