/*******************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*  Ŭ������		:   ResultException.java
*  �ۼ���			:   e4net
*  ����			:   ����ó��
*  �������		:   bccard
*  �ۼ�����		:   2006.12.27
************************** �����̷� ********************************************
* ����            ������         ������� 
*
*******************************************************************************/

package com.bccard.golf.common;

import java.io.Serializable;
import com.bccard.waf.common.BaseException;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.msg.MsgEtt;

/**
* ����ó��
* @version 2006.12.27
* @author e4net
*/ 
public class ResultException extends BaseException {
	/** Ÿ��Ʋ �̹��� */
	private String titleImage;
	/** Ÿ��Ʋ �ؽ�Ʈ */
	private String titleText;
	/** �߰���ɹ�ư���� */
	private StringBuffer buttonBuffer;
	
	/** �޽������� ��ƼƼ */ 
	private MsgEtt msgEtt;
	
	/**
    * ResultException
    * @param N/A
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
    public ResultException() {
        super();
    }

	/**
    * ResultException
    * @param s String Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
    public ResultException(String s) {
        super(s);
    }

	/**
    * ResultException
    * @param s String Object
    * @param rootCause Throwable Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
    public ResultException(String s, Throwable rootCause) {
        super(s,rootCause);
    }

	/**
    * ResultException
    * @param key String Object
    * @param values Object[] Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
    public ResultException(String key, Object[] values) {
        super(key,values);
    }

	/**
    * ResultException
    * @param key String Object
    * @param values Object[] Object
    * @param rootCause Throwable Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
    public ResultException(String key, Object[] values, Throwable rootCause) {
        super(key,values,rootCause);
    }

	/**
    * ResultException
    * @param rootCause Throwable Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
    public ResultException(Throwable rootCause) {
        super(rootCause);
    }
    
    /** ***********************************************************************
	* �ý��� ����.
	* @param ett �޽��������� ���� MsgEtt
	************************************************************************ */
	public ResultException(MsgEtt ett) {
		super(ett.getMessage());
		this.msgEtt = ett;
	}
    
    /** ***********************************************************************
	* �޽������� �� ���� ����.
	* @param ett �޽��������� ���� MsgEtt
	************************************************************************ */
	public ResultException(MsgEtt ett,Throwable t) {
		this(ett);
		super.setRootCause(t);
	}

	/** ***********************************************************************
	* �޽������� ��ƼƼ Ŭ���� ��ȯ.
	* @return �޽��������� ���� MsgEtt ��ȯ
	************************************************************************ */
	public MsgEtt getMsgEtt() {
		if ( this.msgEtt == null ) {
			this.msgEtt = new MsgEtt();
			msgEtt.setType( MsgEtt.TYPE_ERROR );
			msgEtt.setTitle("");
			msgEtt.setMessage( getMessage() );
		}
		return this.msgEtt;
	}
	 
	
	/**
    * setTitleImage
    * @param titleImage String Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	/**
    * getTitleImage
    * @param N/A
    * @version 2006.12.27
 	* @author e4net
    * @return String
    */
	public String getTitleImage() {
		return this.titleImage;
	}

	/**
    * setTitleText
    * @param titleText String Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	/**
    * getTitleText
    * @param N/A
    * @version 2006.12.27
 	* @author e4net
    * @return String
    */
	public String getTitleText() {
		return this.titleText;
	}

	/**
    * addButton
    * @param href String Object
    * @param buttonhtml String Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
	public void addButton(String href, String buttonhtml) {
		addButton(href,null,buttonhtml);
	}

	/**
    * addButton
    * @param href String Object
    * @param target String Object
    * @param buttonhtml String Object
    * @version 2006.12.27
 	* @author e4net
    * @return N/A
    */
	public void addButton(String href, String target, String buttonhtml) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<a href=\"").append(href).append("\"");
		if ( target != null ) strBuff.append(" target=\"").append(target).append("\"");
		strBuff.append(">");
		strBuff.append( buttonhtml );
		strBuff.append("</a>");
		if ( buttonBuffer == null ) this.buttonBuffer = new StringBuffer();
		buttonBuffer.append(strBuff.toString());
	}

	/**
    * getButtonHtml
    * @param N/A
    * @version 2006.12.27
 	* @author e4net
    * @return String
    */
	public String getButtonHtml() {
		if ( buttonBuffer != null ) {
			return buttonBuffer.toString();
		} else {
			return "";
		}
	}

}

