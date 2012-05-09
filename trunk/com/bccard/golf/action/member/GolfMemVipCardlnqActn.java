/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemVipCardlnqActn
*   �ۼ���    : �̵������ �ǿ���
*   ����      : ���� > VIPī��
*   �������  : golf 
*   �ۼ�����  : 2010-09-14
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemVipCardlnqActn extends GolfActn{
	
	public static final String TITLE = "���� > VIPī��";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		
		try {
			// 01.��������üũ
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);			
			
			String select_grade_no = "";
			String toDate  = DateUtil.currdate("yyyyMMdd");			
			String fromDate =  DateUtil.dateAdd('M', -3, toDate, "yyyyMMdd");
			fromDate = fromDate.substring(0,6) + "01";
			double sum_money = 0;
			double select_sum_money = 0;
			double temp_sum_money = 0;
			String select_card_no = "";
			//String select_bank_no = "";
			String vipCardPayAmt	= "0";
			//String vipCardYn 		= "N";
			
			List cardVipList = mbr.getCardVipInfoList();
			List lgCardList = new ArrayList();	
			if(mbr != null)
			{
				select_grade_no = mbr.getVipMaxGrade();
				
				debug("## VIPī�� ���� üũ ���� | ID : "+usrEntity.getAccount()+" | select_grade_no : "+select_grade_no);
				
				// SBS��������� ȸ������ 5õ�� �������� ȸ���� 2���� ���� 
				// �ڷᰡ �ִٸ� 5õ�� ���� ���� => 2���� ����(����üũ OK �Ǵ��� 2���� ����)
				// �ڷᰡ ���ٸ� ���� ���� ó�� (1��5õ�� ����, ����üũ�� ���� ����)
				int resultCk = 0;
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("socid", usrEntity.getSocid());	
				GolfMemCardInsDaoProc proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
				
				try{
					resultCk = proc.sbsMemberCk(context, dataSet, request);
				}catch(Throwable t){}
								
				if( cardVipList!=null && cardVipList.size() > 0 )
				{
					
					if(!"00".equals(select_grade_no))	// �÷�Ƽ�� ȸ���� ���	
					{
						
						for (int i = 0; i < cardVipList.size(); i++) 
						{
							try { 
								
								vipCardPayAmt = "";
								sum_money = 0;
								int usedMoney = 0;
								CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
								String grade 		= (String)record.getVipGrade();
								String expDate 		= (String)record.getExpDate();
								String cardType 	= (String)record.getCardType();
								String cardNo 		= StrUtil.isNull((String)record.getCardNo(), ""); 
								String last_cardApp = (String)record.getCardAppType();
								String last_cardNo 	= (String)record.getLastCardNo();
								String bankNo 		= (String)record.getBankNo();
								String reg_date 	= StrUtil.isNull((String)record.getAppDate(), "");
								String cardNm	 	= StrUtil.isNull((String)record.getJoinName(), "");
								String cardJoinDate	= StrUtil.isNull((String)record.getCardJoinDate(), "");
								
								CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
								
								try{
								
									cardVipInfo.setJoinName(cardNm);	//ī���
									String newRegDate = "";
									if(!"".equals(reg_date))
									{
										newRegDate =  reg_date.substring(0, 4)+"."+reg_date.substring(4, 6)+"."+reg_date.substring(6, 8);
									}
									cardVipInfo.setAppDate(newRegDate);	//��ϳ�¥
									
									String newCardNo = "";
									if(!"".equals(cardNo))
									{
										newCardNo = cardNo.substring(0, 4)+"-"+cardNo.substring(4, 8)+"-****-"+cardNo.substring(12, 16);
									}
									cardVipInfo.setCardNo(newCardNo);	//ī���ȣ
									cardVipInfo.setCardAppType(cardNo);
																		
								
								}catch(Throwable t){}
								
								
								
								//����üũ
								/*
								CardAppType
								11:�ű�, 12:�߰��ű�, 21:�Ѽ���߱�, 22:��޺�����߱�, 
								24:�н���߱�, 25:��޺���н���߱�, 31:�Ϲݰ���, 32:��޺����Ϲݰ���, 
								33:�ڵ�����, 34:��޺����ڵ�����, 35:���ⰻ��, 36:��޺������ⰻ��, 
								37:��������, 38:��޺�����������, 41:���Ű�
								CardType
								1:����,2:����,3:����,4:����
								*/
								
								
								// PTī�尡  03,12,30,91 ���ϰ�� 1��5õ�� ����
								if( "03".equals(grade) || "12".equals(grade) || "30".equals(grade) || "91".equals(grade)  )
								{
									vipCardPayAmt = "15000";
								
								}
								
								if( "03".equals(grade) || "12".equals(grade) )
								{
									
									if("00".equals(expDate)){
										
										// 1:����,2:����,3:����,4:����
										if("1".equals(cardType) || "3".equals(cardType))
										{
											sum_money = getSumMoney(cardNo,cardType,sum_money,context,request,response);
											usedMoney = (int) sum_money;
											debug("## GolfMemVipCardlnqActn | ���� ī���ȣ : "+cardNo+" | �����ݾ� : "+usedMoney+ " | cardJoinDate : "+cardJoinDate);
											
											
											
											
											if(last_cardApp.equals("21") || last_cardApp.equals("24") || last_cardApp.equals("31") || last_cardApp.equals("33") || last_cardApp.equals("35") || last_cardApp.equals("37")){
												sum_money = getSumMoney(last_cardNo,cardType,sum_money,context,request,response);
												usedMoney = (int) sum_money;
												debug("## GolfMemVipCardlnqActn | ���� ī���ȣ : "+last_cardNo+" | �����ݾ� : "+usedMoney);
											}
											
										}
										
										
									}
									
									
									//ī�尡 �߱޳�¥�� 3���� �̳��� ��� üũ
									String ckDate =  DateUtil.dateAdd('M', 3, cardJoinDate, "yyyyMMdd");											
									debug("## GolfMemVipCardlnqActn | ī��߱޳�¥ �� | ���ó�¥ : "+toDate+" | ī��߱޳�¥ : "+cardJoinDate+ " | ī��3�����񱳳�¥ : "+ckDate);
									
									if( Integer.parseInt(toDate) < Integer.parseInt(ckDate) )
									{
										debug("## GolfMemVipCardlnqActn | 3�����̳� �߱�ī���Դϴ�. last_cardApp : "+last_cardApp+" | ī��߱� 3���� �̳� ��");
										sum_money = 10000000; //�űԸ� ���� õ���� �־� �켱�ο�.....
										usedMoney = (int) sum_money;
									}
									
									
									
									/*
									if(!(Integer.parseInt(fromDate) <= Integer.parseInt(reg_date))){
	
									} else {
										//��, 30���� �̸��ΰ�� �ֱ� ���� ī��߱����ڷ� 3���� �̸� ���� 1�� 5õ�� ����. 3���� ��� ȸ���� 2���� ����
										//��, �ű� 3���� �̳��� �Ѽ���߱�, �н���߱� �� ī�带 ��߱� �纯��õ� ����üũ���� ����ó�� �Ѵ�.(1��5õ�� ���� ����)
										
										
										
																				
										if (last_cardApp.equals("11") || last_cardApp.equals("12") || last_cardApp.equals("21") || last_cardApp.equals("22") || last_cardApp.equals("24") || last_cardApp.equals("25")   ) {
											debug(" last_cardApp : "+last_cardApp+" | �ű� 3���� �̳��� �Ѽ���߱�, �н���߱� ��");
											sum_money = 10000000; //�űԸ� ���� õ���� �־� �켱�ο�.....
											usedMoney = (int) sum_money;
										}
										
										
										
									}
									*/	
									temp_sum_money = usedMoney;
									
									// ���� ���� ī��,ȸ����,������ �켱
									if ( select_sum_money < temp_sum_money )	{
										select_sum_money = temp_sum_money;
										select_card_no = cardNo;
										//select_bank_no = bankNo;
									}
									
									if (usedMoney >= 300000) {
										
										vipCardPayAmt = "15000";
										
									}else if (usedMoney < 300000) {
										
										vipCardPayAmt = "20000";
										
									}
								
								}
																
								
								//SBSȸ���ϰ�� ������ 2���� 
								if(resultCk>0)
								{
									vipCardPayAmt = "20000";
								}
								
								
								
								
								cardVipInfo.setUsedAmt(usedMoney+"");
								cardVipInfo.setPayAmt(vipCardPayAmt+"");														
								
								lgCardList.add(cardVipInfo);
								
								debug("## "+i+"��° | ID : "+usrEntity.getAccount()+" | grade : "+grade+" | cardNo : "+cardNo+" | ���� : "+sum_money+" | �����ݾ� : "+vipCardPayAmt+" | bankNo : "+bankNo+" | reg_date : "+reg_date+" | last_cardApp : "+last_cardApp+" | cardType : "+cardType);												
								
								
							} catch(Throwable t) {}
							
						
						}
						
									
						
						
												
						debug("## �����ݾ� ���� �������� ī�� ID : "+usrEntity.getAccount()+" | select_sum_money : " + select_sum_money+" | sum_money : "+sum_money);
						
						
						
						
						
					}
					else
					{
						debug("## VIP�÷�Ƽ�� ȸ�� �ƴ�");						
					}
					
					
				
				}
				else
				{
					debug("## VIPī�� ���� ����.");	
				}
				
				
			}
			System.out.print("## VIPī�� ID : "+usrEntity.getAccount()+" | ���� �����ݾ� : vipCardPayAmt : "+vipCardPayAmt+" | select_card_no : "+select_card_no+" \n");
			
			
			
			
			
			// 05. Return �� ����					
			//paramMap.put("join_chnl", join_chnl);
			paramMap.put("vipCardPayAmt", vipCardPayAmt);
	        request.setAttribute("paramMap", paramMap); 
	        request.setAttribute("vipCardPayAmt", vipCardPayAmt);
			request.setAttribute("select_card_no", select_card_no);
			request.setAttribute("lgCardList", lgCardList); 
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	/***********************************************************************
	 * ����üũ ����
	 **********************************************************************/
	public double getSumMoney(String cardNo, String cardType, double sum,WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, BaseException{

		
		JtProcess process = new JtProcess();
		String joltServiceName = "BSNINPT";
		String toDay  = DateUtil.currdate("yyyyMMdd");
		
		String toDate  = DateUtil.dateAdd('M', -1, toDay, "yyyyMMdd");
		int datcount = DateUtil.getMonthlyDayCount(
						Integer.parseInt(toDate.substring(0,4)),
						Integer.parseInt(toDate.substring(4,6))); // �ش���� ����
		toDate = toDate.substring(0,6) + Integer.toString(datcount);
		debug("toDate : " + toDate);
		String fromDate =  DateUtil.dateAdd('M', -3, toDay, "yyyyMMdd");
		fromDate = fromDate.substring(0,6) + "01";		

		if(cardType.equals("1") || cardType.equals("3")){  //1:���� 3:��������
			// 2008-10-13 ����
			JoltInput entity = new JoltInput(joltServiceName);					
			entity.setServiceName(joltServiceName);
			entity.setString("fml_trcode", "MGA0100R1600");

			TaoResult jout = null;

			entity.setString("fml_arg1", cardNo);		// ��ȣ: ����:�ֹι�ȣ/���:ȸ����ȸ����ȣ/���:ī���ȣ				
			entity.setString("fml_arg2", "3");			// ����,�������: '1':�ֹι�ȣ(����),'2':ȸ����ȸ����ȣ(���)'3':ī���ȣ(���)
			entity.setString("fml_arg3", fromDate);		// �̿���ȸ��_FROM, YYYYMMDD(����Ѵ�)
			entity.setString("fml_arg4", toDate);		// �̿���ȸ��_TO, YYYYMMDD(����Ѵ�)
			entity.setString("fml_arg5", " ");			// ISP����(1.ISP, ������ : SPACE)
			entity.setString("fml_arg7", "1");			// ����ī���������

			jout = process.call(context, request, entity);

			String rescode = jout.getString("fml_ret1");
			
				if ("0".equals(rescode)) {
					sum = sum + jout.getDouble("fml_retd3") + jout.getDouble("fml_retd5");
					debug("sum_money### >> " + sum);
			} 
		}
		return sum;
	}
}
