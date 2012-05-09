/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : JunMoonFactory
*   작성자    : (주)미디어포스 이경희
*   내용      : 전문 Factory
*   적용범위  : golf
*   작성일자  : 2011-01-12
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.factory;

import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;

public abstract class JunMoonFactory {
	
	public abstract HashMap procss(WaContext context, HttpServletRequest request
								, Properties prop, HashMap hmap);

}
