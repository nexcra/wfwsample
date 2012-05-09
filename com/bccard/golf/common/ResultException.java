/*******************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*  클래스명		:   ResultException.java
*  작성자			:   e4net
*  내용			:   예외처리
*  적용범위		:   bccard
*  작성일자		:   2006.12.27
************************** 수정이력 ********************************************
* 일자            수정자         변경사항 
*
*******************************************************************************/

package com.bccard.golf.common;

import java.io.Serializable;
import com.bccard.waf.common.BaseException;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.msg.MsgEtt;

/**
* 예외처리
* @version 2006.12.27
* @author e4net
*/ 
public class ResultException extends BaseException {
	/** 타이틀 이미지 */
	private String titleImage;
	/** 타이틀 텍스트 */
	private String titleText;
	/** 추가기능버튼버퍼 */
	private StringBuffer buttonBuffer;
	
	/** 메시지정보 엔티티 */ 
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
	* 시스템 예외.
	* @param ett 메시지정보를 담은 MsgEtt
	************************************************************************ */
	public ResultException(MsgEtt ett) {
		super(ett.getMessage());
		this.msgEtt = ett;
	}
    
    /** ***********************************************************************
	* 메시지정보 를 통한 예외.
	* @param ett 메시지정보를 담은 MsgEtt
	************************************************************************ */
	public ResultException(MsgEtt ett,Throwable t) {
		this(ett);
		super.setRootCause(t);
	}

	/** ***********************************************************************
	* 메시지정보 엔티티 클래스 반환.
	* @return 메시지정보를 담은 MsgEtt 반환
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

