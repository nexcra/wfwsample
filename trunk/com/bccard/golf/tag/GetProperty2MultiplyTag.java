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

import com.bccard.waf.tag.OutSupport;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class GetProperty2MultiplyTag extends OutSupport {
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
		String ret = "0";

		if (rtnObj != null) {
			if (rtnObj instanceof java.lang.Integer) {
				int src = ((Integer) rtnObj).intValue() * 2;
				ret = Integer.toString(src);
			} else if (rtnObj instanceof java.lang.Float) {
				int src = ((Float) rtnObj).intValue() * 2;
				ret = Integer.toString(src);
			}
		}
		return ret;
	}
}