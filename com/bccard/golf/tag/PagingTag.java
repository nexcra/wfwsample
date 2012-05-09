/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 트리형 데이터를 표시하기 위한 커스텀 태그
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
public class PagingTag extends BodyTagSupport {
	
	private String onclick;     /** 페이징 스크립트  */
	private int    blockSize;   /** 페이징 크기      */
	private int    recordSize;  /** 레코드 크기      */
	private int    total;       /** 전체 레코드 크기 */
	private int    currpage;    /** 현재 페이지      */
	private String contents;    /** 컨텐츠           */

	//private String first ="<img src='/topn/img/aocstp/mall/icon/ico_page_prev.gif' align='absmiddle' class='mb2 mr15'>";  /** 처음이미지       */
	//private String last ="<img src='/topn/img/aocstp/mall/icon/ico_page_next.gif' align='absmiddle' class='mb2 ml15'>";   /** 끝이미지         */
	//private String back ="<img src='/topn/img/aocstp/mall/icon/ico_page_prev.gif' align='absmiddle' class='mb2 mr15'>";    /** 이전이미지       */
	//private String next ="<img src='/topn/img/aocstp/mall/icon/ico_page_next.gif' align='absmiddle' class='mb2 ml15'>";    /** 다음이미지       */
	//private String backblock ="<img src='/topn/img/aocstp/mall/icon/ico_page_prev.gif' align='absmiddle' class='mb2 mr15'>";    /** 이전블럭이동이미지       */
	//private String nextblock ="<img src='/topn/img/aocstp/mall/icon/ico_page_next.gif' align='absmiddle' class='mb2 ml15'>";    /** 다음블럭이동이미지       */
	
	private String first ="[처음]";  /** 처음이미지       */
	private String last ="[끝]";   /** 끝이미지         */
	private String back ="[이전]";    /** 이전이미지       */
	private String next ="[다음]";    /** 다음이미지       */
	private String backblock ="[이전블럭]";    /** 이전블럭이동이미지       */
	private String nextblock ="[다음블럭]";    /** 다음블럭이동이미지       */
  
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
	public void setBlockSize(int param)     { this.blockSize    = param; }

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

	/**
	 * @info setBack
	 * @param String param
	 * @return void
	 */
	public void setBackBlock(String param)       { this.backblock = param; }

	/**
	 * @info setNext
	 * @param String param
	 * @return void
	 */
	public void setNextBlock(String param)       { this.nextblock = param; }

	/** *****************************************************************
	 * 태그 재사용을 위한 release 처리.
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

			int startBlock = (start * this.blockSize+1);		//현재 페이지의 시작 블락 번호
			
			String src = this.contents;
			// 전체 수량 표시
			src = StrUtil.replace(src, "${total}", String.valueOf(this.total) );

			// 현재 페이지 표시
			if ( this.total > 0 ) {
				src = StrUtil.replace(src, "${currpage}" , String.valueOf(this.currpage) );
			} else {
				src = StrUtil.replace(src, "${currpage}" , "0" );
			}

			// 전체 페이지 표시
			src = StrUtil.replace(src, "${totalpage}", String.valueOf(totalpage) );

			// 처음으로 버튼 넣기
			String  tmp = "";
			if ( src.indexOf("${first}") >= 0 && this.first != null && this.first.length()    > 0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage > 1 ) {
						tmp = getLink( this.first, 1    , true );
					} else {
						tmp = getLink( this.first, 1    , false );
					}
				}
				src = StrUtil.replace( src, "${first}", tmp );
			}

			// 이전 n개 버튼 넣기
			tmp = "";
			if ( src.indexOf("${backblock}") >= 0 && backblock.length() > 0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage-this.blockSize > 0 ) {
						
						tmp = getLink( backblock, startBlock-1, true );
					} else {
						tmp = getLink( backblock, startBlock-1, false );
					}
				}
				src = StrUtil.replace( src, "${backblock}", tmp );
			}

			// 이전 버튼 넣기
			tmp = "";
			if ( src.indexOf("${back}") >= 0 && this.back.length() >    0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage-1 > 0 ) {
						tmp = getLink( this.back, this.currpage-1, true );
					} else {
						tmp = getLink( this.back, this.currpage-1, false    );
					}
				}
				src = StrUtil.replace( src, "${back}", tmp );
			}

			// 이후로 버튼 넣기
			tmp = "";
			if ( src.indexOf("${next}") >= 0 && this.next.length() >    0 ) {
				if ( this.total > 0 ) {
					if ( totalpage >= this.currpage+1 ) {
						tmp = getLink( this.next, this.currpage+1, true );
					} else {
						tmp = getLink( this.next, this.currpage+1, false    );
					}
				}
				src = StrUtil.replace( src, "${next}", tmp );
			}

			// 이후n개 버튼 넣기
			tmp = "";
			if ( src.indexOf("${nextblock}") >= 0 && nextblock.length() > 0 ) {
				if ( this.total > 0 ) {
					if ( totalpage > (start+1)*this.blockSize ) {
						tmp = getLink( nextblock, (start+1)*this.blockSize+1, true );
					} else {
						tmp = getLink( nextblock, (start+1)*this.blockSize+1, false );
					}
				}
				src = StrUtil.replace( src, "${nextblock}", tmp );
			}

			// 맨끝으로 버튼 넣기
			tmp = "";
			if ( src.indexOf("${last}") >= 0 && this.last.length() >    0 ) {
				if ( this.total > 0 ) {
					if ( this.currpage < totalpage ) {
						tmp = getLink( this.last, totalpage,    true );
					} else {
						tmp = getLink( this.last, totalpage,    false );
					}
				}
				src = StrUtil.replace( src, "${last}", tmp );
			}

			// 페이지 넣기
			tmp = "";
			if ( src.indexOf("${pages}") >= 0 ) {
				StringBuffer tmpBuff = new StringBuffer();
				if ( this.total > 0 ) {
					for(int i = start*this.blockSize+1; i < (start+1)*this.blockSize+1; i++) {
						if( i == this.currpage ) {
							tmp = getTextLink( " <b>" + i + "</b> ", i, false );
						} else {
							tmp = getTextLink( " " +  i + " " , i, true );
						}
						tmpBuff.append( tmp );
						if ( i >= totalpage ) {
							break;
						}

						if ( i < (start+1)*this.blockSize ) {
							tmp = "|";
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

	// 앤커 작성
	/**
	 * @info getLink
	 * @param String str
	 * @param int gotopage
	 * @param boolean isA
	 * @return String
	 */
	private String getLink(String str, int gotopage, boolean isA) {
		StringBuffer tmpBuff = new StringBuffer();
		String script  = StrUtil.replace(this.onclick,"${page}",String.valueOf(gotopage) );
		if ( isA ) {
			tmpBuff.append("<a href=\"javascript:").append( script ).append("\">");
			tmpBuff.append( str ).append("</a>");
		} else {
			tmpBuff.append( str );
		}
		return tmpBuff.toString();
	}

	// 앤커 작성
	/**
	 * @info getLink
	 * @param String str
	 * @param int gotopage
	 * @param boolean isA
	 * @return String
	 */
	private String getTextLink(String str, int gotopage, boolean isA) {
		StringBuffer tmpBuff = new StringBuffer();
		String script  = StrUtil.replace(this.onclick,"${page}",String.valueOf(gotopage) );
		if ( isA ) {
			tmpBuff.append("<a href=\"javascript:").append( script ).append("\" class=\"ml5 mr5 t11\">");
			tmpBuff.append( str ).append("</a>");
		} else {
			tmpBuff.append("<span class=\"ml5 mr5 t11\">");
			tmpBuff.append( str ).append("</span>");
		}
		return tmpBuff.toString();
	}
}
