/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [skyking@kcp.co.kr]
* ���� : Ʈ���� �����͸� ǥ���ϱ� ���� Ŀ���� �±� | �������� ����¡
******************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.bccard.waf.common.StrUtil;

/** ****************************************************************************
 * @version   1.0
 * @author   2007.01.04<A href="mailto:ykcho@e4net.net">yongkook cho</A>
 **************************************************************************** */
public class PagingMerTag extends BodyTagSupport {
	
	private String onclick;     /** ����¡ ��ũ��Ʈ  */
	private int    blockSize;   /** ����¡ ũ��      */
	private int    recordSize;  /** ���ڵ� ũ��      */
	private int    total;       /** ��ü ���ڵ� ũ�� */
	private int    currpage;    /** ���� ������      */
	private String contents;    /** ������           */

	//private String first ="<img src='/topn/img/aocstp/mall/icon/ico_page_prev.gif' align='absmiddle' class='mb2 mr15'>";  /** ó���̹���       */
	//private String last ="<img src='/topn/img/aocstp/mall/icon/ico_page_next.gif' align='absmiddle' class='mb2 ml15'>";   /** ���̹���         */
	//private String back ="<img src='/topn/img/aocstp/mall/icon/ico_page_prev.gif' align='absmiddle' class='mb2 mr15'>";    /** �����̹���       */
	//private String next ="<img src='/topn/img/aocstp/mall/icon/ico_page_next.gif' align='absmiddle' class='mb2 ml15'>";    /** �����̹���       */
	//private String backblock ="<img src='/topn/img/aocstp/mall/icon/ico_page_prev.gif' align='absmiddle' class='mb2 mr15'>";    /** �������̵��̹���       */
	//private String nextblock ="<img src='/topn/img/aocstp/mall/icon/ico_page_next.gif' align='absmiddle' class='mb2 ml15'>";    /** �������̵��̹���       */
	
	private String first ="<img src='/topn/img/btn/bt_first.gif' alt='ó������' />";  /** ó���̹���       */
	private String last ="<img src='/topn/img/btn/bt_last.gif' alt='������' />";   /** ���̹���         */
	private String back ="<img src='/topn/img/btn/bt_prev.gif' alt='����' />";    /** �����̹���       */
	private String next ="<img src='/topn/img/btn/bt_next.gif' alt='����'  />";    /** �����̹���       */
	private String backblock ="<img src='/topn/img/btn/bt_prev.gif' alt='����' />";    /** �������̵��̹���       */
	private String nextblock ="<img src='/topn/img/btn/bt_next.gif' alt='����'  />";    /** �������̵��̹���       */
  
	/**
	 * @info setOnclick
	 * @param String param
	 * @return void
	 */
	public void setOnclick  (String param)  { this.onclick = param; }

	/**
	 * @info setTotal
	 * @param int param
	 * @return void
	 */
	public void setTotal    (int param)     { this.total = param; }

	/**
	 * @info setCurrpage
	 * @param int param
	 * @return void
	 */
	public void setCurrpage (int param)     { this.currpage = param; }

	/**
	 * @info setBlockSize
	 * @param int param
	 * @return void
	 */
	public void setBlockSize(int param)     { 
		this.blockSize    = param; 
	}

	/**
	 * @info setRecordSize
	 * @param int param
	 * @return void
	 */
	public void setRecordSize(int param)    { this.recordSize = param; }

	/**
	 * @info setContents
	 * @param String param
	 * @return void
	 */
	public void setContents(String param)   { this.contents = param; }

	/**
	 * @info setFirst
	 * @param String param
	 * @return void
	 */
	public void setFirst(String param)      { this.first = param; }

	/**
	 * @info setLast
	 * @param String param
	 * @return void
	 */
	public void setLast(String param)       { this.last = param; }

	/**
	 * @info setBack
	 * @param String param
	 * @return void
	 */
	public void setBack(String param)       { this.back = param; }

	/**
	 * @info setNext
	 * @param String param
	 * @return void
	 */
	public void setNext(String param)       { this.next = param; }

	/** *****************************************************************
	 * �±� ������ ���� release ó��.
	 ***************************************************************** */
	public void release() {
		super.release();
		this.onclick = null;
		this.blockSize = 0;
		this.recordSize = 0;
		this.total = 0;
		this.currpage = 0;
		this.contents = null;
		/*
		this.first =    null;
		this.last = null;
		this.back = null;
		this.next = null;
		*/
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
			if ( this.contents == null ) return EVAL_PAGE;

			int start = 0;
			int totalpage = 0;

			if ( this.total != 0 ) {
				totalpage = this.total % this.recordSize;
				if ( totalpage == 0 ) {
					totalpage = this.total / this.recordSize;
				} else {
					totalpage = this.total / this.recordSize + 1;
				}
				start = (this.currpage-1) / this.blockSize;
			}

			String src = this.contents;
			// ��ü ���� ǥ��
			src = StrUtil.replace(src, "${total}", String.valueOf(this.total) );

			// ���� ������ ǥ��
			if ( this.total > 0 ) {
				src = StrUtil.replace(src, "${currpage}" , String.valueOf(this.currpage) );
			} else {
				src = StrUtil.replace(src, "${currpage}" , "0" );
			}

			// ��ü ������ ǥ��
			src = StrUtil.replace(src, "${totalpage}", String.valueOf(totalpage) );

			// ó������ ��ư �ֱ�
			String  tmp = "";
			if ( src.indexOf("${first}") >= 0 && this.first != null && this.first.length()    > 0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage > 1 ) {
						tmp = getLink( this.first, 1    , true, "bgnone" );
					} else {
						tmp = getLink( this.first, 1    , false, "bgnone" );
					}
				}
				src = StrUtil.replace( src, "${first}", tmp );
			}

			// ���� n�� ��ư �ֱ�
			tmp = "";
			if ( src.indexOf("${backblock}") >= 0 && backblock.length() > 0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage-this.blockSize > 0 ) {
						tmp = getLink( backblock, this.currpage-this.blockSize, true, "prev" );
					} else {
						tmp = getLink( backblock, this.currpage-this.blockSize, false, "prev" );
					}
				}
				src = StrUtil.replace( src, "${backblock}", tmp );
			}

			// ���� ��ư �ֱ�
			tmp = "";
			if ( src.indexOf("${back}") >= 0 && this.back.length() >    0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage-1 > 0 ) {
						tmp = getLink( this.back, this.currpage-1, true, "prev" );
					} else {
						tmp = getLink( this.back, this.currpage-1, false, "prev"    );
					}
				}
				src = StrUtil.replace( src, "${back}", tmp );
			}

			// ���ķ� ��ư �ֱ�
			tmp = "";
			if ( src.indexOf("${next}") >= 0 && this.next.length() >    0 ) {
				if ( this.total > 0 ) {
					if ( totalpage >= this.currpage+1 ) {
						tmp = getLink( this.next, this.currpage+1, true, "next" );
					} else {
						tmp = getLink( this.next, this.currpage+1, false, "next"    );
					}
				}
				src = StrUtil.replace( src, "${next}", tmp );
			}

			// ����n�� ��ư �ֱ�
			tmp = "";
			if ( src.indexOf("${nextblock}") >= 0 && backblock.length() > 0 ) {
				if ( this.total > 0 ) {
					if ( totalpage > (start+1)*this.blockSize ) {
						tmp = getLink( nextblock, (start+1)*this.blockSize+1, true, "next" );
					} else {
						tmp = getLink( nextblock, (start+1)*this.blockSize+1, false, "next" );
					}
				}
				src = StrUtil.replace( src, "${nextblock}", tmp );
			}

			// �ǳ����� ��ư �ֱ�
			tmp = "";
			if ( src.indexOf("${last}") >= 0 && this.last.length() >    0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage < totalpage ) {
						tmp = getLink( this.last, totalpage,    true, "bgnone" );
					} else {
						tmp = getLink( this.last, totalpage,    false, "bgnone" );
					}
				}
				src = StrUtil.replace( src, "${last}", tmp );
			}

			// ������ �ֱ�
			tmp = "";
			if ( src.indexOf("${pages}") >= 0 ) {
				StringBuffer tmpBuff = new StringBuffer();
				if ( this.total > 0 ) {
					for(int i = start*this.blockSize+1; i < (start+1)*this.blockSize+1; i++) {
						if( i == this.currpage ) {
							tmp = getTextLink( " <strong>" + i + "</strong> ", i, false, this.total, this.currpage );
						} else {
							tmp = getTextLink( " " +  i + " " , i, true, this.total, this.currpage );
						}
						tmpBuff.append( tmp );
						if ( i >= totalpage ) {
							break;
						}

						if ( i == (start+1)*this.blockSize ) {
							tmp = "";
							tmpBuff.append( tmp );
						}
					}
				}
				src = StrUtil.replace( src, "${pages}", tmpBuff.toString() );
			}
			JspWriter out = getPreviousOut();
			out.print(src);  
		} catch(Throwable t) {
			throw new JspException(t);
		}
		return EVAL_PAGE;
	}

	// ��Ŀ �ۼ�
	/**
	 * @info getLink
	 * @param String str
	 * @param int gotopage
	 * @param boolean isA
	 * @return String
	 */
	private String getLink(String str, int gotopage, boolean isA, String step) {
		StringBuffer tmpBuff = new StringBuffer();
		String script  = StrUtil.replace(this.onclick,"${page}",String.valueOf(gotopage) );
		if ( isA ) {
			if(!"".equals(step))
			{
				tmpBuff.append("<a href=\"javascript:").append( script ).append("\" class=\"").append( step ).append(" \")>");
			}
			else
			{
				tmpBuff.append("<a href=\"javascript:").append( script ).append("\">");
			}
			
			tmpBuff.append( str ).append("</a>");
		} else {
			tmpBuff.append( str );
		}
		return tmpBuff.toString();
	}

	// ��Ŀ �ۼ�
	/**
	 * @info getLink
	 * @param String str
	 * @param int gotopage
	 * @param boolean isA
	 * @return String
	 */
	private String getTextLink(String str, int gotopage, boolean isA, int tblockSize, int currpage) {
		StringBuffer tmpBuff = new StringBuffer();
		String script  = StrUtil.replace(this.onclick,"${page}",String.valueOf(gotopage) );
		if ( isA ) {
			if(tblockSize == currpage)
			{
				tmpBuff.append("<a href=\"javascript:").append( script ).append("\" class=\"bg_none pr0 \">");
			}
			else
			{
				tmpBuff.append("<a href=\"javascript:").append( script ).append(" \">");
				
			}
			
			tmpBuff.append( str ).append("</a>");
		} else {
			tmpBuff.append("");
			tmpBuff.append( str ).append("");
		}
		return tmpBuff.toString();
	}
}
