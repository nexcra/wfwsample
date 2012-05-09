/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : EditFileUpActn
*   작성자     : (주)미디어포스 임은혜
*   내용        : 첨부파일 등록시 임시 파일로 업로드. (가맹점용) 에디터용
*   적용범위  : Golf
*   작성일자  : 2009-05-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author 권영만
* @version 2009.4.8
******************************************************************************/
public class EditFileUpActn extends AbstractAction {

	public static final String TITLE ="첨부파일 임시등록";

	/**
	 * @param WaContext context
	 * @param HttpServletRequest    request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		try {
			

			String strDOC_ROOT_PATH		= 	AppConfig.getAppProperty("DOC_ROOT_PATH"); 
			
			//보완검수 2009.9.15
			strDOC_ROOT_PATH = strDOC_ROOT_PATH.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");

			String atcTmpPath 					=	strDOC_ROOT_PATH+"/upload/editor/";
			
			//보완검수 2009.9.15
			atcTmpPath = atcTmpPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			
			// 저장할 디렉토리가 없을 경우 새로 생성한다.
			File createPath  =  new File(atcTmpPath);
			if (!createPath.exists()){
				createPath.mkdirs();
			}
			// 저장할 디렉토리가 없을 경우 새로 생성한다.
			int intMaxSize  = 2*1024*1024; //2MB
			MultipartRequest multi  = new MultipartRequest(request, atcTmpPath, intMaxSize,"euc-kr",new TmpFileRenamePolicy());

			String orgNamePath = multi.getParameter("upFilePath");
			String tmpFileName = multi.getFilesystemName  ("upFile");  // upload 된후의 파일 이름
			String orgFileName = multi.getOriginalFileName("upFile");  // 원래 파일 이름

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

			// 썸네일 이미지 생성
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

