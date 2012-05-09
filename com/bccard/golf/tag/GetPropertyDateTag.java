/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2005. 1. 29 altair
* 내용 : 
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.common.GolfUtil;

import com.bccard.waf.tag.OutSupport;


/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class GetPropertyDateTag extends OutSupport{
	protected String format = "-";
	/** *****************************************************************
	 * 태그 재사용을 위한 release 처리.
	 ***************************************************************** */
	public void release() {
		super.release();
	}

	/** *****************************************************************
	 * 빈의 이름 입력.
	 * @param name 빈의 이름
	 ***************************************************************** */
	public void setName(String name) {
		setDatasrc(name);
	}

	/** *****************************************************************
	 * 빈의 프로퍼티명 입력.
	 * @param property 빈의 프로퍼티명
	 ***************************************************************** */
	public void setProperty(String property) {
		setDatafld(property);
	}

	/** *****************************************************************
	 * Entity 의 getter 메쏘드를 실행하여 출력 문자열 반환.
	 * @return  출력 문자열
	 ***************************************************************** */
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
				value = GolfUtil.getDateFormat(value,format);
			}

			return value;
		} else {
			return (String) rtnObj;
		}
	}
	
	/**
	 * @info setFormat
	 * @param String format
	 * @return void
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}
