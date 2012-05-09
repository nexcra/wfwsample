/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 메시지 처리 기본데이터
* 수정 : 2007.01.04 [skyking@kcp.co.kr]
* 내용 : 메시지 리소스의 키와 아규먼트 부분 추가
******************************************************************************/
package com.bccard.golf.msg;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.bccard.waf.core.AbstractEntity;

/******************************************************************************
* 메시지 커스텀 태그.
* @author 조용국
* @version 2007.01.04
******************************************************************************/
public class MsgEtt extends AbstractEntity {
	public static final String TYPE_NONE   = "NONE";   /** NONE   코드 상수 */
	public static final String TYPE_INFO   = "INFO";   /** INFO   코드 상수 */
	public static final String TYPE_WARN   = "WARN";   /** WAqRN   코드 상수 */
	public static final String TYPE_ERROR  = "ERROR";  /** ERROR  코드 상수 */
	public static final String TYPE_RESULT = "RESULT"; /** RESULT 코드 상수 */
	private  String type;                              /** Message Type 기본값 NONE */
	private  String title;                             /** 메시지 제목              */
	private  String message;                           /** 메시지 내용              */
	private  List eventList;                           /** 버튼 이벤트의 목록       */
	private String key;                                /** 메시지리소스키           */
	private Object[] args;                             /** 메시지리스스 아규먼트    */
	private boolean resource;                          /** 메시지키 입력 여부      */

	/** ***********************************************************************
	* 메시지엔티티.
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
	* 메시지내용 메시지엔티티.
	* @param type 메시지타입
	* @param title 메시지제목
	* @param message 메시지내용
	************************************************************************ */
	public MsgEtt(String type,String title,String message) {
		this();
		this.setType(type);
		this.setTitle(title);
		this.setMessage(message);
		this.resource = false;
	}

	/** ***********************************************************************
	* 메시지리소스키 메시지엔티티.
	* @param type 메시지타입
	* @param title 메시지제목
	* @param key 메시지리소스키
	* @param args 메시지리소스아규먼트
	************************************************************************ */
	public MsgEtt(String type,String title,String key, Object[] args) {
		this();
		this.setType(type);
		this.setKey(key,args);
		this.setTitle(title);
		this.resource = true;
	}

	/** ***********************************************************************
	* 메시지 타입 반환.
	* @return 메시지 타입
	************************************************************************ */
	public String getType() {
		return type;
	}

	/** ***********************************************************************
	* 메시지 제목 반환.
	* @return 메시지 제목
	************************************************************************ */
	public String getTitle() {
		return title;
	}

	/** ***********************************************************************
	* 메시지 반환.
	* @return 메시지
	************************************************************************ */
	public String getMessage() {
		return message;
	}

	/** ***********************************************************************
	* 버튼 이벤트 Enumeration 반환.
	* @return 버튼 이벤트 Enumeration
	************************************************************************ */
	public Iterator getEvents() {
		return eventList.iterator();
	}

	/** ***********************************************************************
	* 버튼 이벤트의 수량 반환.
	* @return 버튼 이벤트 수량
	************************************************************************ */
	public int getEventSize() {
		return eventList.size();
	}

	/** ***********************************************************************
	* 메시지 종류 입력.
	* @param string 메시지 종류
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
	* 메시지 제목 입력.
	* @param string 메시지 제목
	************************************************************************ */
	public void setTitle(String string) {
		this.title = string;
	}

	/** ***********************************************************************
	* 메시지 입력.
	* @param string 메시지
	************************************************************************ */
	public void setMessage(String string) {
		this.resource = false;
		this.message = string;
	}

	/** ***********************************************************************
	* 버튼 이벤트 추가.
	* @param ett msgEvntEtt
	************************************************************************ */
	public void addEvent(MsgEvntEtt ett) {
		eventList.add(ett);
	}

	/** ***********************************************************************
	* 버튼 이벤트 추가.
	* @param href 버튼 동작 url
	* @param href 버튼 이미지 src
	************************************************************************ */
	public void addEvent(String href, String src) {
		MsgEvntEtt ett = new MsgEvntEtt(href,src);
		eventList.add(ett);
	}

	/** ***********************************************************************
	* 메시지리소스 키 반환.
	* @return 메시지리소스 키
	************************************************************************ */
	public String getKey() {
		return this.key;
	}

	/** ***********************************************************************
	* 메시지리소스 아규먼트 반환.
	* @return 메시지리소스 아규먼트
	************************************************************************ */
	public Object[] getArgs() {
		return this.args;
	}

	/** ***********************************************************************
	* 메시지리소스 설정.
	* @param key 메시지리소스 키
	************************************************************************ */
	public void setKey(String key) {
		this.resource = true;
		this.key = key;
		this.args = null;
	}

	/** ***********************************************************************
	* 메시지리소스 설정.
	* @param key 메시지리소스 키
	* @param args 메시지리소스 아규먼트
	************************************************************************ */
	public void setKey(String key, Object[] args) {
		this.resource = true;
		this.key = key;
		this.args = args;
	}

	/** ***********************************************************************
	* 메시지리소스 키 설정 여부.
	* @return 메시지리소스 키 설정 여부
	************************************************************************ */
	public boolean isResource() {
		return this.resource;
	}

}
