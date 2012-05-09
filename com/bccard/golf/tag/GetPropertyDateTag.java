/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2005. 1. 29 altair
* ���� : 
* ���� : 
* ���� : 
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.common.GolfUtil;

import com.bccard.waf.tag.OutSupport;


/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class GetPropertyDateTag extends OutSupport{
	protected String format = "-";
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
				// ��¥�� �ð��϶��� ��¥�� �����´�.
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
