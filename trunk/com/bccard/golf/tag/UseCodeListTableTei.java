/******************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 : 2004. 12. 24 altair
 * 내용 : 가맹점용 권영만
 * 수정 : 
 * 내용 : 
 ******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.tagext.TagExtraInfo;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class UseCodeListTableTei extends TagExtraInfo {
	/**
	 * @info UseCodeListTableTei
	 * @return
	 */
	public UseCodeListTableTei() {}

	/**
	 * @info getVariableInfo
	 * @param TagData tagdata
	 * @return VariableInfo[] 
	 */
	public VariableInfo[] getVariableInfo(TagData tagdata) {
		VariableInfo avariableinfo[] = new VariableInfo[3];
		String s = "java.util.List";
		String s1 = tagdata.getAttributeString("id");
		avariableinfo[0] = new VariableInfo(s1, s, true, 1);
		String s2 = tagdata.getAttributeString("var");
		String s3 = tagdata.getAttributeString("varStatus");
		int i = 0;
		int j = 1;
		if (s2 != null) {
			avariableinfo[j] = new VariableInfo(s2, "com.bccard.waf.core.Code", true, i);
			j++;
		}
		if (s3 != null) {
			avariableinfo[j] = new VariableInfo(s3, "javax.servlet.jsp.jstl.core.LoopTagStatus",
					true, i);
			j++;
		}
		VariableInfo avariableinfo1[] = new VariableInfo[j];
		System.arraycopy(avariableinfo, 0, avariableinfo1, 0, j);
		return avariableinfo1;
	}

}