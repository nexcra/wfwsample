/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 메시지 이벤트 엔티티
******************************************************************************/
package com.bccard.golf.msg;

import com.bccard.waf.core.AbstractEntity;

/******************************************************************************
* 메시지 커스텀 태그.
* @author 조용국
* @version 2007.01.04
***************************************************************************** */
public class MsgEvntEtt extends AbstractEntity {

	private String href;
	private String src;
	private String title;

	/**
	 * @info MsgEvntEtt
	 * @return
	 */
	public MsgEvntEtt() {
		this.href="";
		this.src = "";
		this.title = "";
	}

	/**
	 * @info MsgEvntEtt
	 * @param String href
	 * @param String src
	 * @return
	 */
	public MsgEvntEtt(String href, String src) {
		this.href=href;
		this.src = src;
		this.title = "";
	}

	/**
	 * @info getHref
	 * @return String
	 */
	public  String getHref() {
		return href;
	}

	/**
	 * @info getSrc
	 * @return String
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @info setHref
	 * @param String string
	 * @return void
	 */
	public void setHref(String string){
		href = string;
	}

	/**
	 * @info setSrc
	 * @param String string
	 * @return void
	 */
	public void setSrc(String string) {
		src = string;
	}

	/**
	 * @info setTitle
	 * @param String string
	 * @return void
	 */
	public void setTitle(String string) {
		title = string;
	}

	/**
	 * @info getTitle
	 * @return String
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @info toString
	 * @return String
	 */
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<a href=\"");
		if ( this.href != null && this.href.trim().length() > 0 ) {
			strBuff.append(this.href);
		} else {
			strBuff.append("#");
		}
		strBuff.append("\">");
		if ( this.src != null && this.src.trim().length() > 0 ) {
			strBuff.append("<img src=\"").append(this.src).append("\" border=\"0\">");
		} else {
			if ( this.title != null && this.title.trim().length() > 0 ) {
				strBuff.append( this.title );
			} else {
				strBuff.append("--");
			}
		}
		strBuff.append("</a>");
		return strBuff.toString();
	}
}
