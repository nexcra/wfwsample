/**********************************************************************************************************************
*   클래스명  : VarSupport
*   작성자    : 이보아
*   내용      : VarSupport
*   작성일자  : 2005.12.12
**********************************************************************************************************************/

package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/*****************************************
* renewal 
* @version 2005 12 12 
* @author 이보아
********************************************** */

public abstract class VarSupport extends TagSupport {
	protected String var;
	protected String type;
	protected Object value;
	protected String declare;


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @return  
********************************************************************************** */ 
	public VarSupport() {
		super();
		init();
	}


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @return  void 
********************************************************************************** */ 
	private void init() {
		var = null;
		type = null;
		value = null;
		declare = "true";
	}


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @return  void 
********************************************************************************** */ 
	public void release() {
		super.release();
		init();
	}


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @param var String객체.
* @return  void 
********************************************************************************** */ 
	public void setVar(String var) { this.var = var; }

/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @param type String객체.
* @return  void 
********************************************************************************** */ 
	public void setType(String type) { this.type = type; }

/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @param truefalse String객체.
* @return  void 
********************************************************************************** */ 
	public void setDeclare(String truefalse) { this.declare =  truefalse; }


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
* @return  void 
********************************************************************************** */ 
	protected abstract void prepare() throws JspException;


/** ******************************************************************************** 
* 기업
* @version 2005 12 12 
* @author 이보아
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
* 기업
* @version 2005 12 12 
* @author 이보아
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
