/**********************************************************************************************************************
*   Ŭ������  : VarSupport
*   �ۼ���    : �̺���
*   ����      : VarSupport
*   �ۼ�����  : 2005.12.12
**********************************************************************************************************************/

package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/*****************************************
* renewal 
* @version 2005 12 12 
* @author �̺���
********************************************** */

public abstract class VarSupport extends TagSupport {
	protected String var;
	protected String type;
	protected Object value;
	protected String declare;


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  
********************************************************************************** */ 
	public VarSupport() {
		super();
		init();
	}


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  void 
********************************************************************************** */ 
	private void init() {
		var = null;
		type = null;
		value = null;
		declare = "true";
	}


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  void 
********************************************************************************** */ 
	public void release() {
		super.release();
		init();
	}


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param var String��ü.
* @return  void 
********************************************************************************** */ 
	public void setVar(String var) { this.var = var; }

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param type String��ü.
* @return  void 
********************************************************************************** */ 
	public void setType(String type) { this.type = type; }

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param truefalse String��ü.
* @return  void 
********************************************************************************** */ 
	public void setDeclare(String truefalse) { this.declare =  truefalse; }


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  void 
********************************************************************************** */ 
	protected abstract void prepare() throws JspException;


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  int 
********************************************************************************** */ 
	public int doStartTag() throws JspException {
		try {
			prepare();
		} catch (JspException ex) {
			release();
			throw ex;
		}
		return SKIP_BODY;
	}


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  int 
********************************************************************************** */ 
	public int doEndTag() throws JspException {
		try {
			if ( pageContext.getAttribute(var) != null ) {
				setDeclare("false");
			} else {
				setDeclare("true");
			}
			if ( this.var != null ) pageContext.setAttribute( this.var, this.value );
			return EVAL_PAGE;
		} catch (Throwable ex) {
			throw new JspException(ex.getMessage(), ex);
		} finally {
			release();
		}
	}

}
