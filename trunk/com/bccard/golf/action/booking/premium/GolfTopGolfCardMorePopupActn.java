/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCardTop
*   �ۼ���    : ������
*   ����      : Top����ī�� ��ŷTop
*   �������  : Golf
*   �ۼ�����  : 2010-11-04
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException; 
import java.util.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPenaltyDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardMorePopupActn extends GolfActn{
	
	public static final String TITLE = "Top����ī�� �����ŷ > ȸ����ŷ > TOP";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü.  
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		String memSocId ="";
		String golfJoinDate = "";
		String roundDate = "";
		String memberClss = "";
		String memId = "";
		
		int memNo =  0;
		
		String strMemChkNum = "";		//ȸ������ 1:��ȸ�� / 4: ��ȸ�� / 5:����ȸ��
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String topGolfCardNo 	= "";
		String topGolfCardYn 	= "N";		//ž����ī�� ���� ����
		String getPassId = "N";		//������Ͼ��̵� 
		String coMemType ="" ;				//ī��
		
		try {
			
			// 01.��������üũ 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				
				memId = userEtt.getAccount();				// ȸ�� ���̵�
				memNo = userEtt.getMemid();					//��� ������ȣ
				intMemGrade = userEtt.getIntMemGrade();	
				memberClss= userEtt.getStrMemChkNum();		// 1:��ȸ�� / 4: ��ȸ�� / 5:����ȸ��
				coMemType = userEtt.getStrCoMemType();		// 2:ȸ������(����) 6:����ī��(����)
				if("5".equals(memberClss)){
					if("6".equals(coMemType))
						memSocId = userEtt.getSocid();
						
					else{
						memSocId = userEtt.getStrCoNum();		//����ī��(����) - ����� ��Ϲ�ȣ
					}
					
					
				}else{
					memSocId = userEtt.getSocid();			//ī�����ī��(����) -  �ֹε�Ϲ�ȣ
				}
				roundDate = DateUtil.currdate("yyyyMMdd");
				
			}
			
			/*
			 * top���� ī�� ȸ������ üũ
			 * */
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			//ȸ�����̵�
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			try {
				List topGolfCardList = mbr.getTopGolfCardInfoList();
				CardInfoEtt cardInfoTopGolfEtt = new CardInfoEtt();
				
				if( topGolfCardList!=null && topGolfCardList.size() > 0 )
				{
					for (int i = 0; i < topGolfCardList.size(); i++) 
					{
						cardInfoTopGolfEtt = (CardInfoEtt)topGolfCardList.get(0);
						topGolfCardNo = cardInfoTopGolfEtt.getCardNo();
						golfJoinDate = cardInfoTopGolfEtt.getAcctDay();		//join
						
						topGolfCardYn = "Y";
						debug("## ž����ī�� ���� ȸ�� | topGolfCardNo : "+topGolfCardNo); 
						
						
						
					}
					if("Y".equals(topGolfCardYn)){
						/**golfloung���� ���̵� �ִ��� Ȯ�� ������ ��� ���� �������� �̵�*/
						
						GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
						dataSet.setString("memId", memId);					//ȸ�����̵�
						int isYn = (int)proc.is_topMember(context, request, dataSet);		//ƼŸ�� ��û�ڼ�
						debug("@@@@@@isYn : "+isYn);
						if(isYn < 1 ){
							return super.getActionResponse(context, "join");		//������� �������� �̵�
						}
					}
					//golfCardCoYn = mbr.getGolfCardCoYn();
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
			/////////////////////////////////////////////////////////////////////////////////////
			/*
			 * ���� ���� ���̵�
			 */
			if( memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ){
				topGolfCardYn 	= "Y";	
				getPassId = "Y";
			}
			if(memId.equals("altec16") || memId.equals("bcgolf2")){
				topGolfCardYn 	= "Y";	
			} 
			
			/////////////////////////////////////////////////////////////////////////////////////
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	 
			if(topGolfCardYn.equals("Y")){
				// 02.�Է°� ��ȸ		
				int defaultDate			= parser.getIntParameter("defaultDate", 5);
				paramMap.put("defaultDate", String.valueOf(defaultDate));
							
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				dataSet.setInt("defaultDate", defaultDate);
				dataSet.setInt("intMemGrade",	intMemGrade);
			      
			        GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
					
					dataSet.setString("memId", memId);					//ȸ�����̵�
					dataSet.setInt("memNo", memNo);						//ȸ��������ȣ
					dataSet.setString("memSocId", memSocId);			//�ֹ�,�����ȣ
					dataSet.setString("golfJoinDate", golfJoinDate);	//topī�� �߱���
					dataSet.setString("roundDate", roundDate);			//���ó�¥
					dataSet.setString("memberClss", memberClss);
					dataSet.setString("getPassId",getPassId );
					DbTaoResult taoResult0 = null;
					if(getPassId.equals("N")){ 
						taoResult0 = proc.get_score(context, request, dataSet);
					}
					paramMap.put("topGolfCardYn", topGolfCardYn);
					
					request.setAttribute("getPassId", getPassId);
					request.setAttribute("topGolfCardYn", topGolfCardYn);
					if(getPassId.equals("N")){
						request.setAttribute("taoResult0", taoResult0);
					}
			}
			paramMap.put("topGolfCardYn", topGolfCardYn);
	        request.setAttribute("paramMap", paramMap);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
