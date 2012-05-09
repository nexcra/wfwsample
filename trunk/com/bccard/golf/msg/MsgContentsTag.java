/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� :2007.01.04 [skyking@kcp.co.kr]
* ���� : �޽��� ������ ��� Ŀ���� �±�
******************************************************************************/
package com.bccard.golf.msg;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/******************************************************************************
* �޽��� Ŀ���� �±�.
* @author ���뱹
* @version 2007.01.04 
******************************************************************************/
public class MsgContentsTag extends BodyTagSupport {
	/** *****************************************************************
	 * �±� ������ ���� release ó��.
	 ******************************************************************/
	public void release() {
		super.release();
	}


	/** *****************************************************************
	 * �±� ����.
	 ***************************************************************** */
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	/** *****************************************************************
	 * �±� ����.
	 ***************************************************************** */
	public int doEndTag() throws JspException {
		try {
			MsgTag tag = (MsgTag)findAncestorWithClass(this,MsgTag.class);
			if ( tag != null ) {
				tag.setContents( bodyContent.getString() );
			}
		} catch(Throwable t) {
			//t.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
