/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmCtmNtsMutiUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


/******************************************************************************
* Topn
* @author (��)����Ŀ�´����̼�
* @version 1.0
******************************************************************************/
public class GolfAdmCtmNtsMutiChgActn extends GolfActn{
 
 public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

 /***************************************************************************************
  * ��ž����Ʈ ������ȭ��
  * @param context  WaContext ��ü. 
  * @param request  HttpServletRequest ��ü. 
  * @param response  HttpServletResponse ��ü. 
  * @return ActionResponse Action ó���� ȭ�鿡 ���÷����� ����. 
  ***************************************************************************************/
 
 public ActionResponse execute( WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

  String subpage_key = "default"; 
  GolfAdminEtt userEtt = null;
  String admin_no = "";
  
  // 00.���̾ƿ� URL ����
  String layout = super.getActionParam(context, "layout");
  request.setAttribute("layout", layout);

  try {
   // 01.��������üũ
     HttpSession session = request.getSession(true);
     userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
     if(userEtt != null && !"".equals(userEtt.getMemId())){    
      admin_no = (String)userEtt.getMemNo();        
     }
    
     
     // 02.�Է°� ��ȸ  
     RequestParser parser = context.getRequestParser(subpage_key, request, response);
     Map paramMap = BaseAction.getParamToMap(request);
     paramMap.put("title", TITLE);
     paramMap.remove("cidx");

     String subkey   	= parser.getParameter("subkey", "");  		// ����޴�����Ű
     String prize_yn   	= parser.getParameter("prize_yn", "");  	// �������࿩��
     String[] seq_no    = parser.getParameterValues("cidx", ""); 	// �Ϸù�ȣ
     String bbs   		= parser.getParameter("bbs", "");
     
   
     // 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
     DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
     dataSet.setString("ADMIN_NO", admin_no);
     dataSet.setString("PRIZE_YN", prize_yn);
     dataSet.setString("SUBKEY", subkey); 


     // 04.���� ���̺�(Proc) ��ȸ
     GolfAdmCtmNtsMutiUpdDaoProc proc = (GolfAdmCtmNtsMutiUpdDaoProc)context.getProc("GolfAdmCtmNtsMutiUpdDaoProc");
   
     // ���������� ��û ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     int editResult = 0;
   
     if (seq_no != null && seq_no.length > 0) {
      editResult = proc.execute(context, dataSet, seq_no); 
     }   
    
     // ����ó���Ǿ����� (ó���Ǽ��� ���ðǼ�(seq_no.length)�� ����)
   if (editResult == seq_no.length) {
	   if("0062".equals(bbs)) request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0062");
	   else request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0035");
       request.setAttribute("resultMsg", "���������� ó�� �Ǿ����ϴ�."); 

   // ����ó�� ���� �ʾ�����
   }else{
	   if("0062".equals(bbs)) request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0062");
	   else  request.setAttribute("returnUrl", "admCtmNtsList.do?bbs=0035");
	  
       request.setAttribute("resultMsg", "���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");          
   }

   // 05. Return �� ����   
   //paramMap.put("editResult", String.valueOf(editResult));   
   request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.   
   
  } catch(Throwable t) {

   throw new GolfException(TITLE, t);
  }
  
  return super.getActionResponse(context, subpage_key);
  
 }
}

