/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfMemDelOutActn
*   �ۼ���	: �̵������ 
*   ����		: ���� ���� > �� �йи� ����Ʈ ȸ��Ż��� ��������� �ڵ�Ż�� ����
*   �������	: golf 
*   �ۼ�����	: 2009-11-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemDelDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemDelFormDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemDelOutActn extends GolfActn{
	
	public static final String TITLE = "���� ���� > �� �йи� ����Ʈ ȸ�� Ż��� ��������� �ڵ�Ż�� ����";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default"; 
		String userId = "";
		String strMemGrd = ""; 	
		String isDelAble = "Y";	// ��������� �������ɿ��� (Y:��������, N:�����Ұ�)
		
		String one_month_later = "Y";	// ����ȸ�� ���� �Ѵ� ��� ����
		int money_cnt = 0;	// ���Ǽ� 
		int payCancel = 0;	// ������� ���
		int memCancelResult = 0;	// ȸ�� ���� ���� ��Ұ�� 
		
		try {
			
			// ��������üũ����
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			
			// �Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			userId = parser.getParameter("memAccount", "");	// ȸ�����̵�
			
			debug("## GolfMemDelOutActn | ���� ");
			debug("## GolfMemDelOutActn | userId ��ȣȭ�Ȱ� : " + userId);
			
			if(!(userId == null || userId.equals(""))){
							
				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
				userId = receiver.decrypt(userId); 

				debug("## GolfMemDelOutActn | userId : " + userId);
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("userId", userId);	

				GolfMemDelFormDaoProc chkProc = (GolfMemDelFormDaoProc)context.getProc("GolfMemDelFormDaoProc");

				// ����� ��� ��������
				DbTaoResult memGrade = chkProc.getMemGrd(context, dataSet, request);
				if (memGrade != null && memGrade.isNext()) {
					memGrade.first();
					memGrade.next();
					strMemGrd = (String) memGrade.getString("CDHD_SQ2_CTGO");					
				}	
				else{
					isDelAble = "N";	
				}
				debug("## GolfMemDelOutActn | strMemGrd : " + strMemGrd);				

				/*
				// TM ȸ�� ���� �Ұ�
				DbTaoResult addResult = chkProc.execute(context, dataSet, request);			
				if (addResult != null && addResult.isNext()) {
					addResult.first();
					addResult.next();
					String join_chnl = (String) addResult.getString("JOIN_CHNL");
					
					if(  !(join_chnl.equals("0001") || join_chnl.equals("1000") || join_chnl.equals("2000")) ){
						isDelAble = "N";
						debug("## GolfMemDelOutActn | TM ȸ�� - ���� �Ұ�");
					}
				}	
				
				// ����ī��ȸ���� ���� �Ұ�
				DbTaoResult cardUser = chkProc.getCardMem(context, dataSet, request);
				if (cardUser != null && cardUser.isNext()) {
					cardUser.first();
					cardUser.next();
					String strMemCard = (String) cardUser.getString("CDHD_SQ2_CTGO");
					
					debug("## GolfMemDelOutActn | strMemCard : "+strMemCard);
									
					if("0005".equals(strMemCard) || "0006".equals(strMemCard)){	// ����ī��ȸ���� ��� - ���� �Ұ�
						isDelAble = "N";
						debug("## GolfMemDelOutActn | ���� ī��  ȸ�� - ���� �Ұ�");
					}
					
				}
				
				// è�ǿ� ȸ���� ��� - ����ǰ �� ȸ���� ���� �Ұ�
				if("0001".equals(strMemGrd)){	
					DbTaoResult addChampion = chkProc.getChamp(context, dataSet, request);
						
					if (addChampion != null && addChampion.isNext()) {
						addChampion.first();
						addChampion.next();
						String champ_seq_no = (String) addChampion.getString("SEQ_NO");
							
						if(!GolfUtil.empty(champ_seq_no) && !champ_seq_no.equals("")){
							isDelAble = "N";
							debug("## GolfMemDelOutActn | ����ǰ ���� è�ǿ� ȸ�� - ���� �Ұ�");
						}
					}
				}							
				*/		
						
				// ���� ������ ȸ�� - Ż�� ó��
				if(isDelAble.equals("Y")){

					GolfMemDelDaoProc proc = (GolfMemDelDaoProc)context.getProc("GolfMemDelDaoProc");			// ȸ��Ż�� ���μ���

					if(strMemGrd.equals("0004")){
						debug("## GolfMemDelOutActn | ����ȸ�� Ż�� => Ż��ó��");
						memCancelResult = proc.execute(context, dataSet, request);
						payCancel = 1;
						debug("## GolfMemDelOutActn | ����ȸ�� Ż�� ��� : " + memCancelResult);
					}else{
						debug("## GolfMemDelOutActn | ����ȸ�� ����");
						DbTaoResult periodResult = proc.execute_period(context, dataSet, request);

						if (periodResult != null && periodResult.isNext()) {
							periodResult.first();
							periodResult.next();
							
							if(periodResult.getString("RESULT").equals("00")){
								one_month_later = (String) periodResult.getString("ONE_MONTH_LATER");
								
								if(one_month_later.equals("N")){
									debug("## GolfMemDelOutActn | �Ѵ��� ������ ���� ��� ����");
									DbTaoResult moneyCntResult = proc.execute_money_cnt(context, dataSet, request);
									payCancel = 1;
									
									if (moneyCntResult != null && moneyCntResult.isNext()) {
										moneyCntResult.first();
										moneyCntResult.next();

										if(moneyCntResult.getString("RESULT").equals("00")){
											money_cnt = (int) moneyCntResult.getInt("MONEY_CNT");
											
											if(money_cnt==0){
												debug("## GolfMemDelOutActn | ��� ������ ���� ��� ����");
												
												payCancel = proc.execute_payCancel(context, dataSet, request);
												debug("## GolfMemDelOutActn | ��� ���� ��� : " + payCancel);
												
												if(payCancel>0){
													memCancelResult = proc.execute(context, dataSet, request);
													debug("## GolfMemDelOutActn | ��� ������ ���� ��� Ż�� ��� : " + memCancelResult);
												}
												
												debug("## GolfMemDelOutActn | ��� ������ ���� ��� ����");
											}else{
												debug("## GolfMemDelOutActn | ��� ������ �ִ°�� => Ż��ó��");
												memCancelResult = proc.execute(context, dataSet, request);
												payCancel = 1;
												debug("## GolfMemDelOutActn | ��� ������ �ִ°�� Ż�� ��� : " + memCancelResult);
											}
										}
										
									}	//if (moneyCntResult != null && moneyCntResult.isNext()) {
									debug("## GolfMemDelOutActn | �Ѵ��� ������ ���� ��� ����");
									
								}else{	//if(one_month_later.equals("N")){
									debug("## GolfMemDelOutActn | �Ѵ��� ���� ��� => Ż��ó��");
									memCancelResult = proc.execute(context, dataSet, request);
									payCancel = 1;
									debug("## GolfMemDelOutActn | ����ȸ�� Ż�� ��� : " + memCancelResult);
									
								}	//if(one_month_later.equals("N")){
							}
						}
						
						debug("## GolfMemDelOutActn | ����ȸ�� ����");
					}			
					
				}
				
			}			
						
			String returnUrlTrue = "";
			String returnUrlFalse = "/app/golfloung/index.jsp";
			
	        if(strMemGrd.equals("0004")){
	        	returnUrlTrue = "GolfMemCcGeneralEnd.do";
	        }else{
	        	returnUrlTrue = "GolfMemCcChargeEnd.do";
	        }      

			debug("## GolfMemDelOutActn | payCancel : " + payCancel);
			debug("## GolfMemDelOutActn | memCancelResult : " + memCancelResult);
	        
			if (payCancel>0 && memCancelResult>0) {
				
				// ���� ������ 
				if(usrEntity != null){
					usrEntity.setMemGrade("");
					usrEntity.setIntMemGrade(0);
					usrEntity.setCyberMoney(0);					
				}
				
				request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "������ ó�� �Ǿ����ϴ�.");      
	       
	        } else {
				request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(memCancelResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
