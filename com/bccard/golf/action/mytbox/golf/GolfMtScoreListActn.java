/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtScoreListAtcn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����Ƽ�ڽ� > ���ھ� > ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.dbtao.proc.mytbox.golf.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtScoreListActn extends GolfActn{
	
	public static final String TITLE = "���ھ� > ����Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
            String userId	= "";
            
            if(usrEntity != null) 
        	{
        		userId		= (String)usrEntity.getAccount(); 
        	}
            
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String sch_DATE_ST	= parser.getParameter("SCH_DATE_ST", "");
			String sch_DATE_ED	= parser.getParameter("SCH_DATE_ED", "");
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_DATE_ST", sch_DATE_ST);
			dataSet.setString("SCH_DATE_ED", sch_DATE_ED);
			dataSet.setString("CDHD_ID", userId);
			
			
			
			
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			// ����ī�� ���� ��������, ����ī�� �������� �߰� 
			String strCardJoinDate = "";
			String strGolfCardYn = "N";
			String strGolfCardNhYn = "N";
			String topGolfCardYn	= "N";
			String vipCardYn = "N";
			String richCardYn = "N";
			
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinDate = cardInfo.getAcctDay();	// ī�尡����
					strGolfCardYn	= "Y";
				}
				
				List cardNhList = mbr.getCardNhInfoList();
				CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
				
				if( cardNhList!=null && cardNhList.size() > 0 )
				{
					cardNhInfo = (CardNhInfoEtt)cardNhList.get(0);
					strGolfCardNhYn	= "Y";
				}
				
				//ž����ī�� �������� üũ
				try {
					List topGolfCardList = mbr.getTopGolfCardInfoList();
					if( topGolfCardList!=null && topGolfCardList.size() > 0 )
					{
						for (int i = 0; i < topGolfCardList.size(); i++) 
						{
							
							topGolfCardYn = "Y";
							debug("## ž����ī�� ���� ȸ��");
						}
					}
					else
					{
						topGolfCardYn = "N";
						debug("## ž����ī�� �̼���");					
					}
				} catch(Throwable t) 
				{
					topGolfCardYn = "N";
					debug("## ž����ī�� üũ ����");	
				}
				
				//VIPī�� �������� üũ 2010.09.14 �ǿ���
				String select_grade_no = StrUtil.isNull(mbr.getVipMaxGrade(), ""); // grade ==>  03:e-PT, 12:PT12, 30:���̾Ƹ��, 91:���Ǵ�Ƽ
				//if (select_grade_no.equals("30")) select_grade_no = "12";	
								
				debug("## VIPī�� ���� üũ ���� | select_grade_no : "+select_grade_no);
				try {
					List cardVipList = mbr.getCardVipInfoList();								
					if( cardVipList!=null && cardVipList.size() > 0 )
					{
						
						if(!"00".equals(select_grade_no))	// �÷�Ƽ�� ȸ���� ���	
						{
							
							for (int i = 0; i < cardVipList.size(); i++) 
							{
								
								vipCardYn = "Y";
								debug("## VIPī�� ����");	
							}
							
							
						}
						else
						{
							vipCardYn = "N";
							debug("## VIP�÷�Ƽ�� ȸ�� �ƴ�");						
						}
						
						
					
					}
					else
					{
						vipCardYn = "N";
						debug("## VIPī�� ���� ����.");	
					}
				} catch(Throwable t) 
				{
					vipCardYn = "N";
					debug("## VIPī�� üũ ����");	
				}
				
				//���� ȸ���� ����ȸ������ üũ
				String memCk = "N";
				if("Y".equals(vipCardYn))
				{
					GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
					dataSet.setString("intMemGrade", usrEntity.getIntMemGrade()+""); 
					memCk = mem_proc.memCk(context, dataSet, request);											
					
				}
				request.setAttribute("vipMemCk", memCk);	
				
				//��ġī�� �������� üũ
				try {
					List richCardList = mbr.getRichCardInfoList();
					if( richCardList!=null && richCardList.size() > 0 )
					{
						for (int i = 0; i < richCardList.size(); i++) 
						{
							
							richCardYn = "Y";
							debug("## ��ġī�� ���� ȸ��");
						}
					}
					else
					{
						richCardYn = "N";
						debug("## ��ġī�� �̼���");					
					}
				} catch(Throwable t) 
				{
					richCardYn = "N";
					debug("## ��ġī�� üũ ����");	
				}

			}
			request.setAttribute("strGolfCardYn", strGolfCardYn); 		//����ī������
		    request.setAttribute("strCardJoinDate", strCardJoinDate); 	//ī�������
	        request.setAttribute("strGolfCardNhYn", strGolfCardNhYn); 	//����ī������
	        request.setAttribute("topGolfCardYn", topGolfCardYn);		//ž����ī�� ����	        
	        request.setAttribute("vipCardYn", vipCardYn);
	        request.setAttribute("richCardYn", richCardYn);
		
			
			
			

			// 04.���� ���̺�(Proc) ��ȸ - ��ձ��ϱ�
			GolfMtScoreAvgViewDaoProc proc2 = (GolfMtScoreAvgViewDaoProc)context.getProc("GolfMtScoreAvgViewDaoProc");
			DbTaoResult scoreAvg = (DbTaoResult) proc2.execute(context, dataSet, request);
			request.setAttribute("ScoreAvg", scoreAvg);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfMtScoreListDaoProc proc = (GolfMtScoreListDaoProc)context.getProc("GolfMtScoreListDaoProc");
			DbTaoResult scoreListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ScoreListResult", scoreListResult);
		
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
