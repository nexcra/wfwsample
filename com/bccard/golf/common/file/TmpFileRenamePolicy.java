/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : TmpFileRenamePolicy
*   작성자     : (주)미디어포스 임은혜
*   내용        : 첨부파일 등록시 임시 파일 이름 생성기. (가맹점용)
*   적용범위  : Golf
*   작성일자  : 2009-05-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common.file;

import java.io.File;
import java.util.*;
import com.bccard.waf.common.DateUtil;
import com.oreilly.servlet.multipart.FileRenamePolicy;

/******************************************************************************
* m4
* @author 권영만 
* @version 2009.4.8
******************************************************************************/
public class TmpFileRenamePolicy implements FileRenamePolicy {
	/** 세션키 */
	//private String sid;

	/*************************************************************
	 * 기본.
	 ************************************************************/
	public TmpFileRenamePolicy() {
	}

	/*************************************************************
	 * 이름변경처리.
	 * @param file 원본파일
	 ************************************************************/
	public File rename(File file) {
		
		String currdate = DateUtil.currdate("yyyyMMddHHmmss");

		StringTokenizer st = new StringTokenizer(file.getName(),".");
		String[] temp = new String[st.countTokens()];
		
		int count = 0;
		while(st.hasMoreTokens()){
			temp[count] = st.nextToken();
			count++;
		}

		return new File(file.getParent(), currdate+"."+temp[count-1]);
	}
}