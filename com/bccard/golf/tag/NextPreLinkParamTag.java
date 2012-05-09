/*****************************************************************************
 * Ŭ������ : NextPreLinkParamTag
 * �ۼ���	: ����ȣ
 * ����		: ����, ���� ��ư �±� �Ķ���� ó��
 * ������� : bccard 
 * �ۼ����� : 2005.08.03 
********************************�����̷�***************************************
 * ����			������		������� 
 * 2005.08.03	����ȣ		����, ���� ��ư �±� �Ķ���� ó��
 ******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.tag.GetValueSupport;

/*******************************************************************************
 * ����, ���� ��ư �±� �Ķ���� ó��
 * 
 * @author ����ȣ
 * @version 2005.08.03
 *  
 ******************************************************************************/ 
public class NextPreLinkParamTag extends GetValueSupport {

	private String param;
	private Object value;
	
	/*************************************************************************
    * �Ķ���� �Է�
    * @param param �Ķ����
    *************************************************************************/  
	public void setParam(String param) { this.param = param; }

	/*************************************************************************
    * �� �Է�
    * @param value ��
    *************************************************************************/  
	public void setValue(String value) { this.value = value; }

	/** *****************************************************************
	 * �±� ������ ���� release ó��.
	 ***************************************************************** */
	public void release() {
		super.release();
		this.param = null;
		this.value = null;
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
			NextPreLinkTag pagingTag = (NextPreLinkTag)findAncestorWithClass(this,NextPreLinkTag.class);
			if ( pagingTag != null ) {
				if ( this.value == null ) {
					if ( datasrc != null ) {
						this.value = getValue();
					} else {
						this.value = bodyContent.getString();
					}
				}
				if ( this.value == null ) this.value = "";

				if ( "preOnclick".equals(this.param) ) {
					pagingTag.setPreOnclick(this.value.toString());
				} else if ( "preContent".equals(this.param) ) {
					pagingTag.setPreContent(this.value.toString());
				} else if ( "nextOnclick".equals(this.param) ) {
					pagingTag.setNextOnclick(this.value.toString());
				} else if ( "nextContent".equals(this.param) ) {
					pagingTag.setNextContent(this.value.toString());
				} else if ( "dbTao".equals(this.param) ) {
					pagingTag.setDbTao((DbTaoResult) this.value);
				}
			}
		} catch(Throwable t) {
			throw new JspException(t);
		}
		return EVAL_PAGE;
	}

}
