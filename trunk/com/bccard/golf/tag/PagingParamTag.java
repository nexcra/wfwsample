/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 트리형 데이터를 표시하기 위한 커스텀 태그
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.waf.tag.GetValueSupport;

/** ****************************************************************************
 * @version   1.0
 * @author    2003 10 <A href="mailto:ykcho@e4net.net">yongkook cho</A>
 **************************************************************************** */
public class PagingParamTag extends GetValueSupport {
	private String param;
	private Object value;

	/**
	 * @info setParam
	 * @param String param
	 * @return void
	 */
	public void setParam(String param) { this.param = param; }

	/**
	 * @info setValue
	 * @param String value
	 * @return void
	 */
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
			PagingTag pagingTag = (PagingTag)findAncestorWithClass(this,PagingTag.class);
			if ( pagingTag != null ) {
				if ( this.value == null ) {
					if ( datasrc != null ) {
						this.value = getValue();
					} else {
						this.value = bodyContent.getString();
					}
				}
				if ( this.value == null ) this.value = "0";

				if ( "total".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingTag.setTotal(val);
				} else if ( "currpage".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingTag.setCurrpage(val);
				} else if ( "recordSize".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingTag.setRecordSize(val);
				} else if ( "blockSize".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingTag.setBlockSize(val);
				} else if ( "contents".equals(this.param) ) {
					pagingTag.setContents(this.value.toString());
				} else if ( "first".equals(this.param) )    {
					pagingTag.setFirst(this.value.toString());
				} else if ( "last".equals(this.param) ) {
					pagingTag.setLast(this.value.toString());
				} else if ( "back".equals(this.param) ) {
					pagingTag.setBack(this.value.toString());
				} else if ( "next".equals(this.param) ) {
					pagingTag.setNext(this.value.toString());
				} else if ( "backblock".equals(this.param) ) {
					pagingTag.setBackBlock(this.value.toString());
				} else if ( "nextblock".equals(this.param) ) {
					pagingTag.setNextBlock(this.value.toString());
				}
			}
		} catch(Throwable t) {
			throw new JspException(t);
		}
		return EVAL_PAGE;
	}

}