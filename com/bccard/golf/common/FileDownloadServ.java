/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : FileDownloadServ
*   �ۼ���    :
*   ����      : ���ϴٿ�ε� ����
*   �������  : etax
*   �ۼ�����  : 2007.01.10
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/*************************************************************
 * ���ϴٿ�ε� ����
 * @author
 * @version 2007.01.10
 ************************************************************/
public class FileDownloadServ extends HttpServlet{
	/************************************************************
	* GET ��û ó��
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
	* POST ��û ó��
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