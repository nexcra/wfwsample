/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentViewDaoProc;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtInfoViewActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ������ ����";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String gds_code 				= parser.getParameter("gds_code", "");
			String name 					= parser.getParameter("rcvr_nm", "");
			String zp1 						= parser.getParameter("zp1", "");
			String zp2 						= parser.getParameter("zp2", "");
			String zipaddr 					= parser.getParameter("addr", "");
			String detailaddr 				= parser.getParameter("dtl_addr", "");
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "");
			String gds_code_name 			= parser.getParameter("gds_code_name", "");
			String formtarget 				= parser.getParameter("formtarget", "");
			String openerType 				= parser.getParameter("openerType", "");
			if(GolfUtil.empty(openerType)){
				openerType = "U";
			}
			//debug("openerType : " + openerType);
			
			Map paramMap = parser.getParameterMap();
			paramMap.put("title", TITLE);

			paramMap.put("gds_code", gds_code);
			paramMap.put("name", name);
			paramMap.put("zp1", zp1);
			paramMap.put("zp2", zp2);
			paramMap.put("zipaddr", zipaddr);
			paramMap.put("detailaddr", detailaddr);
			paramMap.put("hp_ddd_no", hp_ddd_no);
			paramMap.put("hp_tel_hno", hp_tel_hno);
			paramMap.put("hp_tel_sno", hp_tel_sno);
			paramMap.put("gds_code_name", gds_code_name);

			paramMap.put("formtarget", formtarget);
			paramMap.put("openerType", openerType);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMtInfoViewDaoProc proc = (GolfMtInfoViewDaoProc)context.getProc("GolfMtInfoViewDaoProc");
			
			DbTaoResult myInfo = proc.execute(context, dataSet, request);
			String payWay = "";		// ���� ���  yr:��ȸ��, mn:��ȸ��
			try {
				if(myInfo!=null && myInfo.isNext()){
					myInfo.next();
					payWay = myInfo.getString("payWay");
				}
			}catch(Throwable t){}
			
			paramMap.put("payWay", payWay);
			dataSet.setString("payWay", payWay); 
			
			DbTaoResult myCard = proc.execute_card(context, dataSet, request);
			DbTaoResult myList = proc.execute_list(context, dataSet, request);
			
			

			// ����ī�� ���� ��������, ����ī�� �������� �߰� 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			
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

debug("intMemGrade :" + usrEntity.getIntMemGrade());
debug("intCardGrade :" + usrEntity.getIntCardGrade());

				if("Y".equals(vipCardYn))
				{
					GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
					dataSet.setString("intMemGrade",  usrEntity.getIntMemGrade()+""); 
					dataSet.setString("intCardGrade", usrEntity.getIntCardGrade()+""); 
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
			
			boolean smartYn = false;
			String smartGrd = "";
			
			
			//����Ʈ ����� �����ϰ� �ִ���
			for (int i=0; i < myCard.size(); i++){	
				
				myCard.next();
				
				if ( myCard.getString("RESULT").equals("00")){						
					
					smartGrd = myCard.getString("CARD_SEQ");
					
					if ( smartGrd.equals(AppConfig.getDataCodeProp("0052CODE7"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE8"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE9"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE10"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE11"))
							|| smartGrd.equals(AppConfig.getDataCodeProp("0052CODE19"))
					){	
						smartYn = true;
					}
				}
					
			}
			
			
			/*����Ʈ��޿� �ش�Ǹ鼭 VIPī�� ������ ���
			 *���ΰ��� ������ ��ŵ�ϵ��� ��
			 *2011.06.30 Loun.G ������ ���� Ȯ�� 
			 */
			if (vipCardYn.equals("Y")){
				
				String strCardJoinNo ="";
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 ){				
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinNo = cardInfo.getJoinNo();	// �����ڵ�
					
					//Smart300�� �ش� (�������)
					if (strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))){					
						vipCardYn = "N";
					}
				}	
				
				//��� ����Ʈ ���(���۵� ��ȸ�� �������)
				if (smartYn){
					vipCardYn = "N";
				}
				
			}
			
			System.out.print("### strGolfCardNhYn:"+strGolfCardNhYn+"\n");
			
			

			
			// ������ ����ǰ ��������
			GolfMemPresentViewDaoProc present_proc = (GolfMemPresentViewDaoProc)context.getProc("GolfMemPresentViewDaoProc");
			DbTaoResult presentView = present_proc.execute(context, dataSet, request);
	        request.setAttribute("presentView", presentView);
			
			
			// 05. Return �� ����			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			request.setAttribute("myInfo", myInfo);	
			request.setAttribute("myCard", myCard);		// ī�� ����
			request.setAttribute("myList", myList);		// ���׷��̵� ���
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
	        
	        request.setAttribute("strGolfCardYn", strGolfCardYn); 		//����ī������
	        request.setAttribute("strCardJoinDate", strCardJoinDate); 	//ī�������
	        request.setAttribute("strGolfCardNhYn", strGolfCardNhYn); 	//����ī������
	        request.setAttribute("topGolfCardYn", topGolfCardYn);		//ž����ī�� ����
	        
	        request.setAttribute("vipCardYn", vipCardYn);
	        request.setAttribute("richCardYn", richCardYn);
	        request.setAttribute("CardGrade", usrEntity.getIntCardGrade()+"");   //ī���� 
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
