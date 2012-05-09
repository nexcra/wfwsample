/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkCheckJjListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > ���ֱ׸��� Ȯ��
*   �������  : Golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.check;

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
import com.bccard.golf.dbtao.proc.booking.check.*;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBkCheckNmListActn extends GolfActn{
	
	public static final String TITLE = "���ֱ׸��� Ȯ��";

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
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("LISTTYPE", "");
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
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
						
			

			// 04.���� ���̺�(Proc) ��ȸ - ���� ��ŷ ��ü ����
			GolfBkCheckAllViewDaoProc proc = (GolfBkCheckAllViewDaoProc)context.getProc("GolfBkCheckAllViewDaoProc");
			DbTaoResult allView = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("AllView", allView);
							
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
