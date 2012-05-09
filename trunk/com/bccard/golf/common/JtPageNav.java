/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : JtPageNav
*   작성자    : (주)미디어포스 진현구
*   내용      : Page Navigation Buffer Class for JOLT System//BEA JOLT Packages for BC ${BC_SITE} under WATRIX FrameWork
*   적용범위  : golf
*   작성일자  : 2009-04-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*	2008.11.28	2008.12.03	2008.12.03	2008.12.03	hklee	모바일 적용 관련 페이징 변경 적용
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.ArrayList;
import com.bccard.waf.core.AbstractEntity;

public class JtPageNav extends AbstractEntity {

	private ArrayList keySet;
	private int       pageId;
	private int       ptr;
	private boolean   nextFlag;
    /** 
     * JtPageNav
    */
	private JtPageNav() {
		this.pageId = 0;
		this.ptr    = -1;
		this.keySet = new ArrayList();
	}

	// ONLY THIS CONSTRUCTOR CAN BE USED
    /** 
     * JtPageNav
    */
	public JtPageNav(String initVal) {
		this();
		this.keySet.add(initVal);
		this.pageId++;
		this.ptr++;
	}

	// FALSE CONSTRUCTOR
    /** 
     * JtPageNav
    */
	public JtPageNav(String initVal, String next) {
		this();
		this.keySet.add(initVal);
		this.pageId++;
		this.ptr++;
	}

	// adds CURRENT PAGE KEY into ArrayList
    /** 
     * setKey
    */
	public void setKey(String key) {
		this.keySet.add(key);
		this.pageId++;
	}

	// get PREVIOUS PAGE KEY from ArrayList.
    /** 
     * getKey
    */
	public String getKey() {
		this.ptr = this.keySet.size() - 1;
		this.keySet.remove(this.ptr);	this.ptr--;
		this.pageId--;
		return (String)this.keySet.get(this.ptr);
	}

	// New, Simple Method.
    /** 
     * setNext
    */
	public void setNext(boolean flag) { this.nextFlag = flag; }

	/* 이전/다음 조회 표시 여부 판별용 */
    /** 
     * isNext
    */
	public boolean isNext() { return this.nextFlag; }

    /** 
     * isPrev
    */
	public boolean isPrev() {
		if (this.keySet.size() <= 1)	{ return false; }
		else return true;
	}
	/* 이전/다음 조회 표시 여부 판별용 */

	// 별 쓸모없는 Methods
	
	public String toString() { return this.keySet.toString(); }
	public int getPageNo() { return this.pageId; }
	public int getPointer() { return this.ptr; }
	

}
