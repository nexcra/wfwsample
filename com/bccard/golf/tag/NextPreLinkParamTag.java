/*****************************************************************************
 * 클래스명 : NextPreLinkParamTag
 * 작성자	: 이은호
 * 내용		: 이전, 다음 버튼 태그 파라메터 처리
 * 적용범위 : bccard 
 * 작성일자 : 2005.08.03 
********************************수정이력***************************************
 * 일자			수정자		변경사항 
 * 2005.08.03	이은호		이전, 다음 버튼 태그 파라메터 처리
 ******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.tag.GetValueSupport;

/*******************************************************************************
 * 이전, 다음 버튼 태그 파라메터 처리
 * 
 * @author 이은호
 * @version 2005.08.03
 *  
 ******************************************************************************/ 
public class NextPreLinkParamTag extends GetValueSupport {

	private String param;
	private Object value;
	
	/*************************************************************************
    * 파라메터 입력
    * @param param 파라메터
    *************************************************************************/  
	public void setParam(String param) { this.param = param; }

	/*************************************************************************
    * 값 입력
    * @param value 값
    *************************************************************************/  
	public void setValue(String value) { this.value = value; }

	/** *****************************************************************
	 * 태그 재사용을 위한 release 처리.
	 ***************************************************************** */
	public void release() {
		super.release();
		this.param = null;
		this.value = null;
	}

	/** *****************************************************************
	 * 태그 시작.
	 ***************************************************************** */
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	/** *****************************************************************
	 * 태그 종료.
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
