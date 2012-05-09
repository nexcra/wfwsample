/**********************************************************************************************************************
*   Ŭ������  : GetParameterTag
*   �ۼ���    : �̺���
*   ����      : GetParameterTag
*   �ۼ�����  : 2005.12.12
**********************************************************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import com.bccard.waf.action.JspContext;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/*****************************************
* renewal 
* @version 2005 12 12 
* @author �̺���
***********************************************/
public class GetParameterTag extends VarSupport {
	protected String param;
	protected String def;
	protected String factory;


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param param String��ü.
* @return  void 
********************************************************************************** */ 
	public void setParam(String param) { this.param = param; }

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param def String��ü.
* @return  void 
********************************************************************************** */ 
	public void setDef(String def) { this.def = def;  }

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param factory String��ü.
* @return  void 
********************************************************************************** */ 
	public void setFactory(String factory) { this.factory = factory; }


/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  void 
********************************************************************************** */ 
	protected void prepare() throws JspException {
		try {
			RequestParser parser = null;
			try {
				parser = (RequestParser)pageContext.getAttribute("com.bccard.waf.core.RequestParser");
			} catch(Throwable t) {
				parser = null;
			}
			if ( factory == null ) factory = "default";
			if ( parser == null ) {
				HttpServletResponse res = (HttpServletResponse) pageContext.getResponse();
				HttpServletRequest  req = (HttpServletRequest) pageContext.getRequest();
				WaContext waContext = new JspContext(pageContext);
				parser = waContext.getRequestParser(factory, req, res);
				pageContext.setAttribute("com.bccard.waf.core.RequestParser", parser);
			}
			if ( this.def != null ) {
				value = parser.getParameter(this.param,this.def);
			} else {
				value = parser.getParameter(this.param);
			}
		} catch(Throwable t) {
			throw new JspException(t);
		}
	}
    

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @return  void 
********************************************************************************** */ 
	public void release() {
		super.release();
		this.param = null;
		this.def = null;
		this.factory = null;
	}

}
