/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 메시지 표시 커스텀 태그
******************************************************************************/
package com.bccard.golf.msg;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgEvntEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.core.Code;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.action.JspContext;


/******************************************************************************
* 메시지 커스텀 태그.
* @author 조용국
* @version 2007.01.04
******************************************************************************/
public class MsgTag extends BodyTagSupport {
	private MsgEtt msgEtt;
	private String title;
	private String type;
	private String key;
	private String arg1;
	private String arg2;
	private String arg3;
	private String arg4;
	private String msgAttrName;
	private String msgHandlerAttrName;

	/**
	 * @info setType
	 * @param String type
	 * @return void
	 */
	public void setType(String type) { this.type = type; }
	/**
	 * @info setTitle
	 * @param String title
	 * @return void
	 */
	public void setTitle(String title) { this.title = title; }
	/**
	 * @info setKey
	 * @param String key
	 * @return void
	 */
	public void setKey(String key) { this.key = key; }
	/**
	 * @info setArg1
	 * @param String arg
	 * @return void
	 */
	public void setArg1(String arg) { this.arg1 = arg; }
	/**
	 * @info setArg2
	 * @param String arg
	 * @return void
	 */
	public void setArg2(String arg) { this.arg2 = arg; }
	/**
	 * @info setArg3
	 * @param String arg
	 * @return void
	 */
	public void setArg3(String arg) { this.arg3 = arg; }
	/**
	 * @info setArg4
	 * @param String arg
	 * @return void
	 */
	public void setArg4(String arg) { this.arg4 = arg; }
	/**
	 * @info setMsgAttrName
	 * @param String name
	 * @return void
	 */
	public void setMsgAttrName(String name) { this.msgAttrName = name; }
	/**
	 * @info setMsgHandlerAttrName
	 * @param String name
	 * @return void
	 */
	public void setMsgHandlerAttrName(String name) { this.msgHandlerAttrName = name; }
	/**
	 * @info setContents
	 * @param String contents
	 * @return void
	 */
	void setContents(String contents) {
		if (this.msgEtt != null ) {
			this.msgEtt.setMessage(contents);
		}
	}
	/**
	 * @info addEvent
	 * @param String href
	 * @param String src
	 * @return void
	 */
	void addEvent(String href,String src) {
		if (this.msgEtt != null ) {
			this.msgEtt.addEvent(href,src);
		}
	}

	/** *****************************************************************
	 * 태그 재사용을 위한 release 처리.
	 ***************************************************************** */
	public void release() {
		super.release();
		this.msgEtt = null;
		this.title = null;
		this.key = null;
		this.arg1 = null;
		this.arg2 = null;
		this.arg3 = null;
		this.arg4 = null;
		this.msgAttrName = null;
		this.msgHandlerAttrName = null;
	}


	/** *****************************************************************
	 * 태그 시작.
	 ***************************************************************** */
	public int doStartTag() throws JspException {
		if ( this.type != null ) {
			if ( this.key != null && this.key.length() > 0 ) {
				this.msgEtt = new MsgEtt(this.type, this.title, this.key, new String[]{arg1,arg2,arg3,arg4} );
			} else {
				this.msgEtt = new MsgEtt(this.type, this.title, "");
			}
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}


	/** *****************************************************************
	 * 태그 종료.
	 ***************************************************************** */
	public int doEndTag() throws JspException {
		try {
			if ( this.msgEtt == null ) {
				if ( this.msgAttrName != null ) {
					Object obj = pageContext.findAttribute(this.msgAttrName);
					if ( obj != null && obj instanceof MsgEtt ) {
						this.msgEtt = (MsgEtt) obj;
					}
				}

				if ( this.msgHandlerAttrName != null ) {
					Object obj = pageContext.findAttribute(this.msgHandlerAttrName);
					if ( obj != null && obj instanceof MsgHandler ) {
						this.msgEtt = ((MsgHandler) obj).getMsgEtt();
					}
				}
			}

			if ( this.msgEtt != null ) {
				JspWriter out = pageContext.getOut();
				printHtml( out );
			}
		} catch(Throwable t) {
			
		}
		return EVAL_PAGE;
	}

	/**
	 * @param printHtml
	 * @param JspWriter out
	 * @return void
	 */
	private void printHtml(JspWriter out) throws IOException, JspException  {
		WaContext waContext = new JspContext(pageContext);
		Code code = waContext.getCode("MSG_CLSS", this.msgEtt.getType() );
		out.println("<div>");
		out.println("<table width=\"565\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin:22 0 0 0\">");
		out.println("<tr height=\"6\">");
		out.println("	<td width=\"6\" class=\"bg_clt\"></td>");
		out.println("	<td width=\"553\"class=\"bg_ct\"></td>");
		out.println("	<td width=\"6\" class=\"bg_crt\"></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("	<td class=\"bg_cl\"></td>");
		out.println("	<td valign=\"top\" align=\"center\" style=\"padding:7 0 7 0\">");
		out.println("		<table width=\"540\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("		<tr>");
		out.println("			<td width=\"133\"><img src=\"" + code.getDetail() + "\"></td>");
		out.println("			<td width=\"407\" style=\"padding:10 0 0 15\" valign=\"top\">");
		out.println("				<div class=\"li02 tem\"><b>" + this.msgEtt.getTitle() + "</b></div>");
		out.println("				<div class=\"li02\">");
		if ( this.msgEtt.isResource() ) {
		    out.println( waContext.getMessage( this.msgEtt.getKey() , this.msgEtt.getArgs() ) );
		} else {
		    out.println(this.msgEtt.getMessage());
		}
		out.println("				</div>");
		out.println("			</td>");
		out.println("		</tr>");
		out.println("		<tr><td colspan=\"2\"><img src=\"/town/img/messagebox/t_cscenter.gif\" style=\"margin:15 0 0 0\"></td></tr>");
		out.println("		</table>");
		out.println("	</td>");
		out.println("	<td class=\"bg_cr\"></td>");
		out.println("</tr>");
		out.println("<tr height=\"6\">");
		out.println("	<td class=\"bg_clb\"></td>");
		out.println("	<td class=\"bg_cb\"></td>");
		out.println("	<td class=\"bg_crb\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</div>");
		out.println("<div align=\"right\" style=\"width:565;padding:6 8 0 8\">");
		if ( this.msgEtt.getEventSize() > 0 ) {
		    for(Iterator it= this.msgEtt.getEvents(); it.hasNext(); ) {
		        MsgEvntEtt evntEtt = (MsgEvntEtt) it.next();
		        out.println( evntEtt.toString() );
		    }
		}
		out.println("</div>"); 
	}

}
