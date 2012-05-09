/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPathActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : ����URL PATH
*   �������  : Golf
*   �ۼ�����  : 2009-05-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfDevActn extends GolfActn {

	public static final String TITLE = "����URL PATH";
	
	/***************************************************************************************
	* ����URL PATH
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
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
