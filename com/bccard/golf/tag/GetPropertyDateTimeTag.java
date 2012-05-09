/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2005. 2. 14 altair
* 내용 : 
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.common.GolfUtil;

/******************************************************************************
* Topn
* @author	(주)미디어포스
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
				// 날짜와 시간일때는 날짜만 가져온다.
				if (value.length()>8) {
					value = value.substring(0, 8);
				}
				// TODO 시간 형식 맞추어야함..!!
				value = GolfUtil.getDateFormat(value,format);
			}

			return value;
		} else {
			return (String) rtnObj;
		}
	}

}
