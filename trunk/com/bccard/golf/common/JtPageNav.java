/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : JtPageNav
*   �ۼ���    : (��)�̵������ ������
*   ����      : Page Navigation Buffer Class for JOLT System//BEA JOLT Packages for BC ${BC_SITE} under WATRIX FrameWork
*   �������  : golf
*   �ۼ�����  : 2009-04-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*	2008.11.28	2008.12.03	2008.12.03	2008.12.03	hklee	����� ���� ���� ����¡ ���� ����
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

	/* ����/���� ��ȸ ǥ�� ���� �Ǻ��� */
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
	/* ����/���� ��ȸ ǥ�� ���� �Ǻ��� */

	// �� ������� Methods
	
	public String toString() { return this.keySet.toString(); }
	public int getPageNo() { return this.pageId; }
	public int getPointer() { return this.ptr; }
	

}
