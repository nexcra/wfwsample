/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2005. 2. 26. tjkang
* ���� : 
* ���� : 
* ���� : 
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
	 * �±� ������ ���� release ó��.
	 ***************************************************************** */
	public void release() {
		super.release();
	}

	/** *****************************************************************
	 * ���� �̸� �Է�.
	 * @param name ���� �̸�
	 ***************************************************************** */
	public void setName(String name) {
		setDatasrc(name);
	}

	/** *****************************************************************
	 * ���� ������Ƽ�� �Է�.
	 * @param property ���� ������Ƽ��
	 ***************************************************************** */
	public void setProperty(String property) {
		setDatafld(property);
	}

	/** *****************************************************************
	 * Entity �� getter �޽�带 �����Ͽ� ��� ���ڿ� ��ȯ.
	 * @return  ��� ���ڿ�
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
