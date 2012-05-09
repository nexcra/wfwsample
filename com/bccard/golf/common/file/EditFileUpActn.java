/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : EditFileUpActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ÷������ ��Ͻ� �ӽ� ���Ϸ� ���ε�. (��������) �����Ϳ�
*   �������  : Golf
*   �ۼ�����  : 2009-05-13
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package	com.bccard.golf.common.file;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.common.file.TmpFileRenamePolicy;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* m4 
* @author �ǿ���
* @version 2009.4.8
******************************************************************************/
public class EditFileUpActn extends AbstractAction {

	public static final String TITLE ="÷������ �ӽõ��";

	/**
	 * @param WaContext context
	 * @param HttpServletRequest    request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		try {
			

			String strDOC_ROOT_PATH		= 	AppConfig.getAppProperty("DOC_ROOT_PATH"); 
			
			//���ϰ˼� 2009.9.15
			strDOC_ROOT_PATH = strDOC_ROOT_PATH.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");

			String atcTmpPath 					=	strDOC_ROOT_PATH+"/upload/editor/";
			
			//���ϰ˼� 2009.9.15
			atcTmpPath = atcTmpPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			
			// ������ ���丮�� ���� ��� ���� �����Ѵ�.
			File createPath  =  new File(atcTmpPath);
			if (!createPath.exists()){
				createPath.mkdirs();
			}
			// ������ ���丮�� ���� ��� ���� �����Ѵ�.
			int intMaxSize  = 2*1024*1024; //2MB
			MultipartRequest multi  = new MultipartRequest(request, atcTmpPath, intMaxSize,"euc-kr",new TmpFileRenamePolicy());

			String orgNamePath = multi.getParameter("upFilePath");
			String tmpFileName = multi.getFilesystemName  ("upFile");  // upload ������ ���� �̸�
			String orgFileName = multi.getOriginalFileName("upFile");  // ���� ���� �̸�

			File file = new File(atcTmpPath,tmpFileName);
			long fsize = file.length();

			String fmt = "";
			DecimalFormat format = new DecimalFormat("#,##0.##");
			if ( fsize > 1024 ) {
				BigDecimal blen = new BigDecimal(fsize);
				blen = blen.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
				if ( blen.doubleValue() > 1024 ) {
					blen = blen.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP );
					fmt = format.format( blen.doubleValue() ) + "MB";
				} else {
					fmt = format.format( blen.doubleValue() ) + "KB";
				}
			} else {
				fmt = format.format(fsize) + "Byte";
			}
   
			request.setAttribute("tmpFileName"   ,tmpFileName);
			request.setAttribute("tmpFilePath"   ,atcTmpPath);
			request.setAttribute("tmpFileSize"   ,String.valueOf(fsize) );
			request.setAttribute("tmpFileSizeFmt",fmt);
			request.setAttribute("orgFileName"   ,orgFileName);
			request.setAttribute("orgNamePath"   ,orgNamePath);
			request.setAttribute("varNm"         ,multi.getParameter("varNm"));
			request.setAttribute("varSize"       ,multi.getParameter("varSize"));
			request.setAttribute("varPoint"      ,multi.getParameter("varPoint"));

			// ����� �̹��� ����
			String imgType = StrUtil.isNull(multi.getParameter("imgType"),"") ;
			if (!"".equals(imgType)){
				Thumbnail thumb = new Thumbnail();
				thumb.createThumbnail(atcTmpPath+tmpFileName, atcTmpPath+"S_"+tmpFileName,175,131);
			}
			
			//debug("==== EditFileUpActn End ===");
			
		} catch(Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler ) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, t.getMessage());
			}
			//throw new CpnException(ett, t);
		}
		return getActionResponse(context); // response key
	}
}

