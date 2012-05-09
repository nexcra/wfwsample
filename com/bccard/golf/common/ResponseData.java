/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : ResponseData
*   작성자     : (주)미디어포스 권영만
*   내용        : XML 통신
*   적용범위  : Golf
*   작성일자  : 2009-04-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public final class ResponseData extends HashMap implements Map, Serializable {
	
	private String title;
	private Status status;
	
    /** ************************************************************************
     * 응답 정보 전달 데이터 집합 객체.
     * @param actionKey 액션키
     ************************************************************************ */
	ResponseData(String title) {
        super();
        this.title = title;
    }

    /**
     * @param status
     */
    public void setStatus(Status status) {
  		this.status = status;
    }
    
    /**
     * @return
     */
    public Status getStatus() {
    	if ( this.status == null ) {
    		this.status = new RuntimeStatus(this.title);
    	}
    	return this.status;
    }

}