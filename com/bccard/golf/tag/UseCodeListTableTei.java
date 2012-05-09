/******************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� : 2004. 12. 24 altair
 * ���� : �������� �ǿ���
 * ���� : 
 * ���� : 
 ******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.tagext.TagExtraInfo;

/******************************************************************************
* Topn
* @author	(��)�̵������
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