/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : MerInfoBaseChgProc
*   작성자     : (주)미디어포스 권영만
*   내용        : 비씨탑 시스템 설정 정보. (가맹점용)
*   적용범위  : Golf
*   작성일자  : 2009-04-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* 
***************************************************************************************************/
package com.bccard.golf.common.config;

import java.util.Properties;
import com.bccard.waf.resource.ResourceFactory;
import com.bccard.waf.resource.Resource;

import com.bccard.golf.common.config.ConfigResource;




/******************************************************************************
* Golf : ConfigResourceFactory
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class ConfigResourceFactory extends ResourceFactory {
	/**
	 * @param Properties properties 프로퍼티
	 * @return Resource 
	 */
    public Resource getResource(Properties properties) {
        return new ConfigResource(this,properties);
    }
}
 