/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2005. 2. 14 altair
* ���� : 
* ���� : 
* ���� : 
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.common.GolfUtil;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class GetPropertyDateTimeTag extends GetPropertyDateTag {

	/**
	 * @info getPrintText
	 * @return String
	 */
	protected String getPrintText() throws JspException {
		Object rtnObj = getEntityValue();
		String value = "";

		if (rtnObj != null) {
			if (rtnObj instanceof java.lang.String) {
				value = (String) rtnObj;
				// ��¥�� �ð��϶��� ��¥�� �����´�.
				if (value.length()>8) {
					value = value.substring(0, 8);
				}
				// TODO �ð� ���� ���߾����..!!
				value = GolfUtil.getDateFormat(value,format);
			}

			return value;
		} else {
			return (String) rtnObj;
		}
	}

}
