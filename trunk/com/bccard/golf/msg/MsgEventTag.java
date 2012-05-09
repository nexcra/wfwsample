/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [skyking@kcp.co.kr]
* ���� : �޽��� �̺�Ʈ ��� Ŀ���� �±�
******************************************************************************/
package com.bccard.golf.msg;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/******************************************************************************
* �޽��� Ŀ���� �±�.
* @author ���뱹
* @version 2007.01.04
******************************************************************************/
public class MsgEventTag extends TagSupport {
	private String href;
	private String src;

	/**
	 * @info setHref
	 * @param String href
	 * @return void
	 */
	public void setHref(String href) { this.href = href; }
	/**
	 * @info setSrc
	 * @param String src
	 * @return void
	 */
	public void setSrc (String src ) { this.src  = src; }


	/** *****************************************************************
	 * �±� ������ ���� release ó��.
	 ******************************************************************/
	public void release() {
		super.release();
		this.href = null;
		this.src = null;
	}


	/** *****************************************************************
	 * �±� ����.
	 ***************************************************************** */
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	/** *****************************************************************
	 * �±� ����.
	 ***************************************************************** */
	public int doEndTag() throws JspException {
		try {
			MsgTag tag = (MsgTag)findAncestorWithClass(this,MsgTag.class);
			if ( tag != null ) {
				tag.addEvent(this.href,this.src);
			}
		} catch(Throwable t) {
			//t.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
