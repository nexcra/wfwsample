/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemChgFormActn
*   �ۼ���    : �̵������ ������
*   ����      : ȸ�� > ��ȸ�� ��ȯ > ��
*   �������  : golf 
*   �ۼ�����  : 2009-07-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.action.member.MemberChangeConEtt;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemChgActn extends GolfActn{
	
	public static final String TITLE = "ȸ�� > ��ȸ�� ��ȯ ";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/

    public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
                        throws IOException, ServletException, BaseException {

    	String subpage_key = "default";
		String rtnUrl = "GolfMemChgForm.do";
		String script = "";
		String resultMsg = "";
    	RequestParser parser = context.getRequestParser("default",request,response); 
		Map paramMap = BaseAction.getParamToMap(request);
		paramMap.put("title", TITLE);

        // ���� ��ȸ�� ���� �� ���� 
        String argv3 = parser.getParameter("argv3");
        String argv4 = parser.getParameter("argv4");
        request.setAttribute("argv3",argv3);
        request.setAttribute("argv4",argv4);
        
        StringBuffer socidBuff = new StringBuffer();
        socidBuff.append( parser.getParameter("social_id_1","") );
        socidBuff.append( parser.getParameter("social_id_2","") );

        StringBuffer cardNoBuff = new StringBuffer();
        cardNoBuff.append( parser.getParameter("card_no_1","") );
        cardNoBuff.append( parser.getParameter("card_no_2","") );
        cardNoBuff.append( parser.getParameter("card_no_3","") );
        cardNoBuff.append( parser.getParameter("card_no_4","") );

        MemberChangeConEtt conEtt = new MemberChangeConEtt();
        conEtt.setSocid( socidBuff.toString() );
        conEtt.setCardNo( cardNoBuff.toString() );
        conEtt.setCardPass( parser.getParameter("card_pass","") );
		conEtt.setCvc( parser.getParameter("cvc","") );
        
        // UHL017_Ind_Ret ��������(2005.12.15)
        conEtt.setTerm( parser.getParameter("term","") );
		String result = "ok";
		
		debug("=`=`=`=`=`=GolfMemChgActn => argv3: " + argv3);
		debug("=`=`=`=`=`=GolfMemChgActn => argv4: " + argv4);
		debug("=`=`=`=`=`=GolfMemChgActn => social_id_1: " + parser.getParameter("social_id_1",""));
		debug("=`=`=`=`=`=GolfMemChgActn => social_id_2: " + parser.getParameter("social_id_2",""));
		debug("=`=`=`=`=`=GolfMemChgActn => card_no_1: " + parser.getParameter("card_no_1",""));
		debug("=`=`=`=`=`=GolfMemChgActn => card_no_2: " + parser.getParameter("card_no_2",""));
		debug("=`=`=`=`=`=GolfMemChgActn => card_no_3: " + parser.getParameter("card_no_3",""));
		debug("=`=`=`=`=`=GolfMemChgActn => card_no_4: " + parser.getParameter("card_no_4",""));
		debug("=`=`=`=`=`=GolfMemChgActn => card_pass: " + parser.getParameter("card_pass",""));
		debug("=`=`=`=`=`=GolfMemChgActn => cvc: " + parser.getParameter("cvc",""));
		debug("=`=`=`=`=`=GolfMemChgActn => term: " + parser.getParameter("term",""));

		GolfMemChgDaoProc proc = (GolfMemChgDaoProc)context.getProc("GolfMemChgDaoProc");

        try {

            debug("approval pre::::::::::::::::::::::::::::::::: " );
            proc.approval(context, request, conEtt);
            debug("approval::::::::::::::::::::::::::::::::: " );
            proc.executMemberChange(context, conEtt);
            debug("executMemberChange::::::::::::::::::::::::::::::::: " );

        } catch(ResultException re ) {

            String key = StrUtil.isNull(re.getKey(), "");

			debug("?????????????????????????????????????? KEY >> " + key);
			
            if ("MemberChangeProc_1000".equals(key)) {
				debug("MemChange|Success|" + conEtt.getSocid() + "|" + conEtt.getCardNo().substring(0,12) + "****|" + request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss"));        
				rtnUrl = "GolfMemChgEnd.do";
            } else if ( "MemberChangeProc_1100".equals(key) || "MemberChangeProc_9004".equals(key) ) {
                re.setTitleImage("error");
				result = "error";
				rtnUrl = "GolfMemChgForm.do";
            	resultMsg = "ȸ����ȯ�� ���������� ó�� ���� �ʾҽ��ϴ�. \\n\\n�ٽ� �õ��� �ֽñ� �ٶ��ϴ�.";
            }else{
            	debug("MemChange|Fail|" + conEtt.getSocid() + "|" + conEtt.getCardNo().substring(0,12) + "****|" + request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"|"+key);
				result = "error";
                rtnUrl = "";
                script = "parent.document.goodsMoveForm.action='FrontLogout.do'; parent.document.goodsMoveForm.submit();";

                // ���� �˷� ����
    			if ("MemberChangeProc_10010".equals(key)) {
    				resultMsg = "";
                }else if ("MemberChangeProc_10010".equals(key)) {
                	resultMsg = "��ϵǾ� ���� ���� ȸ���Դϴ�.\\n\\n�α����� �ٽ� ���ּ���.";
                }else if ("MemberChangeProc_10020".equals(key)) {
                	resultMsg = "ī�常 ��ϵǾ��ִ� ȸ���Դϴ�.\\n\\n�α����� �ٽ� ���ּ���.";
                }else if ("MemberChangeProc_10030".equals(key)) {
                	resultMsg = "����ȸ������ ��ϵǾ��ִ�  ȸ���Դϴ�.\\n\\n�α����� �ٽ� ���ּ���.";
                }else if ("MemberChangeProc_10040".equals(key)) {
                	resultMsg = "ī��� ����ȸ������ ��ϵǾ��ִ� ȸ���Դϴ�.\\n\\n�α����� �ٽ� ���ּ���.";
                }else{
                	resultMsg = "������ �����Դϴ�.\\n\\n�α����� �ٽ� ���ּ���.";
                }

			}

//			if ( "MemberChangeProc_1100".equals(key) || "MemberChangeProc_9004".equals(key) ) {
//                re.setTitleImage("error");
//				result = "error";
//				rtnUrl = "GolfMemChgForm.do";
//            } else {
//                re.setTitleImage("result");
//				//rtnUrl = "FrontLogout.do"; 
//                rtnUrl = "";
//                script = "parent.document.goodsMoveForm.action='FrontLogout.do'; parent.document.goodsMoveForm.submit();";
//            }

	        debug("============GolfMemChgActn============ rtnUrl : " + rtnUrl);
	        debug("============GolfMemChgActn============ script : " + script);
	        debug("============GolfMemChgActn============ resultMsg : " + resultMsg);
	        
//            re.setTitleText("ȸ�� ��ȯ");
//            re.addButton(rtnUrl, "<img src='/img/bbs/bt1_confirm.gif' border='0'>");
//            throw re;
        }
        
        debug("============GolfMemChgActn============ rtnUrl : " + rtnUrl);

        request.setAttribute("paramMap", paramMap);		
		request.setAttribute("returnUrl", rtnUrl);	
		request.setAttribute("resultMsg", resultMsg);	
		request.setAttribute("script", script);
		request.setAttribute("result", result);

		//return getActionResponse(context, "default");

		return super.getActionResponse(context, subpage_key);
	}
}

