/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2005. 2. 26. tjkang
* 내용 : 가맹점용
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.waf.tag.OutSupport;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class GetProperty5MultiplyTag extends OutSupport {
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
		String ret = "0";

		if (rtnObj != null) {
			if (rtnObj instanceof java.lang.Long) {
				long src = ((Long) rtnObj).longValue() * 5;
				ret = Long.toString(src);
			}
		}
		return ret;
	}
}