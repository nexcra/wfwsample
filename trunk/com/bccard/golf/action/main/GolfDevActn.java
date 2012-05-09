/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPathActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 개발URL PATH
*   적용범위  : Golf
*   작성일자  : 2009-05-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.main;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfDevActn extends GolfActn {

	public static final String TITLE = "개발URL PATH";
	
	/***************************************************************************************
	* 개발URL PATH
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String rtnCode = "default";

        try{
        	//debug("==== GolfPathActn start ===");
        	
        	int x = 3;
        	
        	if(x>2){
        		System.out.print("a");
        	}
        	
        	while(x>0){
        		x = x-1;
        	}
        	
        	
        	


		    //debug("==== GolfPathActn End ===");
        }catch(Throwable t){
        	//debug("==== GolfPathActn Error ===" + t);
        	return errorHandler(context,request,response,t);
	    }
        return super.getActionResponse(context, rtnCode);
		
		
    }
		
	
}
