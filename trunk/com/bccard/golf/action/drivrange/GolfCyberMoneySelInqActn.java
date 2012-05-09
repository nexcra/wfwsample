/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfCyberMoneySelInqActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      :  SKY72�帲���������� ���̹��Ӵ� Ȯ��
*   �������  : golf
*   �ۼ�����  : 2009-07-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat; 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
//import com.bccard.golf.dbtao.proc.drivrange.GolfCyberMoneySelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfCyberMoneySelInqActn extends GolfActn{
	
	public static final String TITLE = "SKY72�帲���������� ���̹��Ӵ� Ȯ��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); //�̸�
				memClss		= (String)usrEntity.getMemberClss(); //��޹�ȣ
				userId		= (String)usrEntity.getAccount(); //���̵�
				juminno 	= (String)usrEntity.getSocid(); //�ֹι�ȣ
				memGrade 	= (String)usrEntity.getMemGrade(); //���
				intMemGrade	= (int)usrEntity.getIntMemGrade(); //��޹�ȣ
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //���̹��Ӵ�
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("INTCYBERMONEY", intCyberMoney);
			
				
			// 04.���� ���̺�(Proc) ��ȸ
			//GolfCyberMoneySelDaoProc proc = (GolfCyberMoneySelDaoProc)context.getProc("GolfCyberMoneySelDaoProc");
			
			// ���α׷� ����ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			//DbTaoResult result = proc.execute(context, dataSet);
			
			// 05. Return �� ����			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			//request.setAttribute("result",result);	
		    request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
		
			    
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
