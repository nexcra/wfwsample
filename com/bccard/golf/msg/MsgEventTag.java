/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 메시지 이벤트 등록 커스텀 태그
******************************************************************************/
package com.bccard.golf.msg;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/******************************************************************************
* 메시지 커스텀 태그.
* @author 조용국
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
	 * 태그 재사용을 위한 release 처리.
	 ******************************************************************/
	public void release() {
		super.release();
		this.href = null;
		this.src = null;
	}


	/** *****************************************************************
	 * 태그 시작.
	 ***************************************************************** */
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	/** *****************************************************************
	 * 태그 종료.
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
