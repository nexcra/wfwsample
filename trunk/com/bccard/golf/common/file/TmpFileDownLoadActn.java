/*******************************************************************************
*   클 래 스 명	: FileDownLoad
*   작   성   자	: Kim Tae Wan
*   내          용	: 파일 다운로드
*   업   무   명	:
*   작   성   일	: 2009.03.20
************************** 수정이력 ********************************************
*    일자      버전   작성자   변경사항
*
*******************************************************************************/
package com.bccard.golf.common.file;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.GolfActn;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class TmpFileDownLoadActn extends GolfActn {

	private static final String TITLE ="파일 다운로드";	
	/**
	 * 
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException, BaseException {
		
		ResultException bx 	= null;
		FileInputStream fin = null;
		ServletOutputStream out = null;

		try {

				RequestParser parser = context.getRequestParser("default",request, response);

				String file_down = AppConfig.getAppProperty("UPLOAD_REAL_PATH");
				file_down = file_down.replaceAll("\\.\\.","");
				String filepath = (String)parser.getParameter("filepath");
				String filename = (String)parser.getParameter("filename");

				File file = new File(file_down + filepath + "/" + filename);
				fin=new FileInputStream( file );
				byte b[] = new byte[(int)file.length()];

				response.setContentType("application/smnet;");
				response.setHeader("Content-Disposition","attachment;filename="+new String(filename.getBytes("euc-kr"), "8859_1")+";");

				out = response.getOutputStream();

				int numRead = fin.read(b);
				while (numRead != -1) {
					out.write(b, 0, numRead);
					numRead = fin.read(b);
				}

				fin.close();
				out.close();

		}catch ( ResultException re ) {
			throw re;
		} catch ( Throwable t) {
			debug("t.getMessage() :: " + t.getMessage());
			bx = new ResultException(t);
			bx.setTitleImage("error");
			bx.setKey("SYSTEM_ERROR");
			throw bx;
		} finally {
			try { fin.close(); } catch(Throwable t) {}
			try { out.close(); } catch(Throwable t) {}
 		}
		
		ActionResponse actRes = new ActionResponse();
		actRes.setType( ActionResponse.TYPE_OUT );
		return actRes;
		
	}
}
