/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [skyking@kcp.co.kr]
* ���� : Ʈ���� �����͸� ǥ���ϱ� ���� Ŀ���� �±�
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;

import com.bccard.waf.tag.GetValueSupport;

/** ****************************************************************************
 * @version   1.0
 * @author    2003 10 <A href="mailto:ykcho@e4net.net">yongkook cho</A>
 **************************************************************************** */
public class PagingMerParamTag extends GetValueSupport {
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
			PagingMerTag pagingMerTag = (PagingMerTag)findAncestorWithClass(this,PagingMerTag.class);
			if ( pagingMerTag != null ) {
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
					pagingMerTag.setTotal(val);
				} else if ( "currpage".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingMerTag.setCurrpage(val);
				} else if ( "recordSize".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingMerTag.setRecordSize(val);
				} else if ( "blockSize".equals(this.param) ) {
					int val = Integer.parseInt( this.value.toString() );
					pagingMerTag.setBlockSize(val);
				} else if ( "contents".equals(this.param) ) {
					pagingMerTag.setContents(this.value.toString());
				} else if ( "first".equals(this.param) )    {
					pagingMerTag.setFirst(this.value.toString());
				} else if ( "last".equals(this.param) ) {
					pagingMerTag.setLast(this.value.toString());
				} else if ( "back".equals(this.param) ) {
					pagingMerTag.setBack(this.value.toString());
				} else if ( "next".equals(this.param) ) {
					pagingMerTag.setNext(this.value.toString());
				}
			}
		} catch(Throwable t) {
			throw new JspException(t);
		}
		return EVAL_PAGE;
	}

}