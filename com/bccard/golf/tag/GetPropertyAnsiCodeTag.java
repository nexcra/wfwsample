/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2005. 2. 26. tjkang
* 내용 : 
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.common.GolfUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.tag.OutSupport;

/** ****************************************************************************
 * @version   1.0
 * @author    2003 10 <A href="mailto:ykcho@e4net.net">yongkook cho</A>
 **************************************************************************** */
public class GetPropertyAnsiCodeTag extends OutSupport {
	private int len = 0;
	
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
				
				if (len > 0) {
					value = StrUtil.replace(value, "&", "&#38;");
					value = StrUtil.replace(value, "'", "&#39;");
					value = StrUtil.replace(value, "\"", "&#34;");
					value = StrUtil.replace(value, "<", "&#60;");
					value = StrUtil.replace(value, ">", "&#62;");
					value = GolfUtil.left(value, len);
				} else {
					value = StrUtil.replace(value, "&", "&#38;");
					value = StrUtil.replace(value, "'", "&#39;");
					value = StrUtil.replace(value, "\"", "&#34;");
					value = StrUtil.replace(value, "<", "&#60;");
					value = StrUtil.replace(value, ">", "&#62;");
					value = StrUtil.replace(value, "\r\n", "<br>");
				}
			}

			return value;
		} else {
			return (String) rtnObj;
		}
	}

	/**
	 * @param len The len to set.
	 */
	public void setLen(int len) {
		this.len = len;
	}
}
