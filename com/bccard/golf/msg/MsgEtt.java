/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [skyking@kcp.co.kr]
* ���� : �޽��� ó�� �⺻������
* ���� : 2007.01.04 [skyking@kcp.co.kr]
* ���� : �޽��� ���ҽ��� Ű�� �ƱԸ�Ʈ �κ� �߰�
******************************************************************************/
package com.bccard.golf.msg;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.bccard.waf.core.AbstractEntity;

/******************************************************************************
* �޽��� Ŀ���� �±�.
* @author ���뱹
* @version 2007.01.04
******************************************************************************/
public class MsgEtt extends AbstractEntity {
	public static final String TYPE_NONE   = "NONE";   /** NONE   �ڵ� ��� */
	public static final String TYPE_INFO   = "INFO";   /** INFO   �ڵ� ��� */
	public static final String TYPE_WARN   = "WARN";   /** WAqRN   �ڵ� ��� */
	public static final String TYPE_ERROR  = "ERROR";  /** ERROR  �ڵ� ��� */
	public static final String TYPE_RESULT = "RESULT"; /** RESULT �ڵ� ��� */
	private  String type;                              /** Message Type �⺻�� NONE */
	private  String title;                             /** �޽��� ����              */
	private  String message;                           /** �޽��� ����              */
	private  List eventList;                           /** ��ư �̺�Ʈ�� ���       */
	private String key;                                /** �޽������ҽ�Ű           */
	private Object[] args;                             /** �޽��������� �ƱԸ�Ʈ    */
	private boolean resource;                          /** �޽���Ű �Է� ����      */

	/** ***********************************************************************
	* �޽�����ƼƼ.
	************************************************************************ */
	public MsgEtt() {
		this.type=TYPE_NONE;
		this.title="";
		this.message="";
		this.eventList= new ArrayList();
		this.key  = null;
		this.args = null;
		this.resource = false;
	}

	/** ***********************************************************************
	* �޽������� �޽�����ƼƼ.
	* @param type �޽���Ÿ��
	* @param title �޽�������
	* @param message �޽�������
	************************************************************************ */
	public MsgEtt(String type,String title,String message) {
		this();
		this.setType(type);
		this.setTitle(title);
		this.setMessage(message);
		this.resource = false;
	}

	/** ***********************************************************************
	* �޽������ҽ�Ű �޽�����ƼƼ.
	* @param type �޽���Ÿ��
	* @param title �޽�������
	* @param key �޽������ҽ�Ű
	* @param args �޽������ҽ��ƱԸ�Ʈ
	************************************************************************ */
	public MsgEtt(String type,String title,String key, Object[] args) {
		this();
		this.setType(type);
		this.setKey(key,args);
		this.setTitle(title);
		this.resource = true;
	}

	/** ***********************************************************************
	* �޽��� Ÿ�� ��ȯ.
	* @return �޽��� Ÿ��
	************************************************************************ */
	public String getType() {
		return type;
	}

	/** ***********************************************************************
	* �޽��� ���� ��ȯ.
	* @return �޽��� ����
	************************************************************************ */
	public String getTitle() {
		return title;
	}

	/** ***********************************************************************
	* �޽��� ��ȯ.
	* @return �޽���
	************************************************************************ */
	public String getMessage() {
		return message;
	}

	/** ***********************************************************************
	* ��ư �̺�Ʈ Enumeration ��ȯ.
	* @return ��ư �̺�Ʈ Enumeration
	************************************************************************ */
	public Iterator getEvents() {
		return eventList.iterator();
	}

	/** ***********************************************************************
	* ��ư �̺�Ʈ�� ���� ��ȯ.
	* @return ��ư �̺�Ʈ ����
	************************************************************************ */
	public int getEventSize() {
		return eventList.size();
	}

	/** ***********************************************************************
	* �޽��� ���� �Է�.
	* @param string �޽��� ����
	************************************************************************ */
	public void setType(String string) {
		if ( TYPE_INFO.equals(string)
		  || TYPE_WARN.equals(string)
		  || TYPE_ERROR.equals(string)
		  || TYPE_RESULT.equals(string) ) {
			type = string;
		} else {
			type = TYPE_NONE;
		}
	}

	/** ***********************************************************************
	* �޽��� ���� �Է�.
	* @param string �޽��� ����
	************************************************************************ */
	public void setTitle(String string) {
		this.title = string;
	}

	/** ***********************************************************************
	* �޽��� �Է�.
	* @param string �޽���
	************************************************************************ */
	public void setMessage(String string) {
		this.resource = false;
		this.message = string;
	}

	/** ***********************************************************************
	* ��ư �̺�Ʈ �߰�.
	* @param ett msgEvntEtt
	************************************************************************ */
	public void addEvent(MsgEvntEtt ett) {
		eventList.add(ett);
	}

	/** ***********************************************************************
	* ��ư �̺�Ʈ �߰�.
	* @param href ��ư ���� url
	* @param href ��ư �̹��� src
	************************************************************************ */
	public void addEvent(String href, String src) {
		MsgEvntEtt ett = new MsgEvntEtt(href,src);
		eventList.add(ett);
	}

	/** ***********************************************************************
	* �޽������ҽ� Ű ��ȯ.
	* @return �޽������ҽ� Ű
	************************************************************************ */
	public String getKey() {
		return this.key;
	}

	/** ***********************************************************************
	* �޽������ҽ� �ƱԸ�Ʈ ��ȯ.
	* @return �޽������ҽ� �ƱԸ�Ʈ
	************************************************************************ */
	public Object[] getArgs() {
		return this.args;
	}

	/** ***********************************************************************
	* �޽������ҽ� ����.
	* @param key �޽������ҽ� Ű
	************************************************************************ */
	public void setKey(String key) {
		this.resource = true;
		this.key = key;
		this.args = null;
	}

	/** ***********************************************************************
	* �޽������ҽ� ����.
	* @param key �޽������ҽ� Ű
	* @param args �޽������ҽ� �ƱԸ�Ʈ
	************************************************************************ */
	public void setKey(String key, Object[] args) {
		this.resource = true;
		this.key = key;
		this.args = args;
	}

	/** ***********************************************************************
	* �޽������ҽ� Ű ���� ����.
	* @return �޽������ҽ� Ű ���� ����
	************************************************************************ */
	public boolean isResource() {
		return this.resource;
	}

}
