/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : FrontAction
*   �ۼ���     : (��)�̵������ ������
*   ����        : �α��� �׼�
*   �������  : Golf
*   �ۼ�����  : 2009-06-11
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100601	������	DM ��ȭ�̺�Ʈ �˾�â ������ ����
* golfloung		20110120 	�̰��� 	NH�йи� �߰�
* golfloung		20110401 	�̰���  TM ���� 12, 13���� �̺�Ʈ â �ּ� ����
* golfloung		20110422 	�̰���  ����ī���� TOP����ī�� �����ڿ� ���� ���Խ� �޼��� ����
***************************************************************************************************/
package com.bccard.golf.action.login;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.DispatchAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.initech.eam.nls.CookieManager;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class FrontAction  extends DispatchAction {
	/**
	 * ���θ� �α���    
	 *  
	 * @param context
	 * @param request
	 * @param response	
	 * @return ActionResponse 
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */

	public static final String TITLE = "FrontAction";
	
	public ActionResponse frontLogin(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		Connection con = null;
		try {
			RequestParser parser = context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();		
			request.setAttribute("paramMap", paramMap);						
		} catch (Throwable t) {
			error(this.getClass().getName(), t);
			throw new BaseException("SYSTEM_ERROR", null, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		return getActionResponse(context, "default");
	}
	/**
	 * Front loginCheck | ����� �α��� üũ 
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @return ActionResponse
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException 
	 * �۾����  
	 * 2009.10.29 | �ǿ��� ������ �߰�
	 */
	public ActionResponse frontLoginCheck(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		TaoConnection 		con 		= null;
		TaoResult rs = null;
		String returnResult  = "";	// ���������� ����
		String end_date = "";		// ����ȸ���Ⱓ ������

		try {			
			
			System.out.print("## frontLoginCheck | ����� �α��� üũ ����  \n");
			
			RequestParser parser = context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();			
			request.setAttribute("paramMap", paramMap);	
			
			con = context.getTaoConnection("dbtao",null);	
			String title = "�����α���";
			String userid = "";
			String golfCardYn = "N";		//����ī�� ����
			String strCardJoinNo	= "";
			String golfCardNhYn = "N";		//���� ����ī�� ����
			String strCardNhType = "";		//���� ����ī�� ����	03:ƼŸ��, 12:�÷�Ƽ��, 48:�йи�ī��
			String tourBlackYn = "N";		//���� ����ȸ�� ����
			String tourJuminNo = "";		//���� ����ȸ�� �ֹι�ȣ
			
			String vipCardYn 		= "N";		//Vipī�� ���� ����
			String topGolfCardYn 	= "N";		//ž����ī�� ���� ����
			String richCardYn 		= "N";		//Richī�� ���� ����
			String topGolfCardNo 	= "";
			String golfCardCoYn 	=  "N";

			String jbCardYn 	=  "N";        //�������� �ñ״���ī�忩�� 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			
			if(usrEntity != null)
			{
				userid=usrEntity.getAccount();								
			}
			else
			{
				returnResult="08";
				debug("## frontLoginCheck | usrEntity null \n");
				
				//sso �α׾ƿ�
				String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
				CookieManager.removeCookie("MEM_CLSS", sso_domain, response);
				CookieManager.removeNexessCookie(sso_domain, response);
				
			}
			
			//����ī�� ���� üũ
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinNo = cardInfo.getJoinNo();	// �����ڵ�
					golfCardYn = "Y";
				}
				
				// ���� ����ī�� ���� üũList cardList = mbr.getCardInfoList(); 
				List cardNhList = mbr.getCardNhInfoList();
				CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
				
				if( cardNhList!=null && cardNhList.size() > 0 )
				{ 
					cardNhInfo = (CardNhInfoEtt)cardNhList.get(0);
				 
					if("02".equals(cardNhInfo.getCardGubun())){ 
						if ( cardNhInfo.getCardType().trim().length() > 0 ){
							strCardNhType = cardNhInfo.getCardType();	// ī������
							golfCardNhYn = "Y";
						}
					}if("04".equals(cardNhInfo.getCardGubun())){ 
						jbCardYn = "Y";
						debug("## �������� ī�� ����.");	
					}else if("01".equals(cardNhInfo.getCardGubun())){
						tourBlackYn = "Y";
						tourJuminNo = cardNhInfo.getJuminNo();
					}else if("03".equals(cardNhInfo.getCardGubun())){
						if ( cardNhInfo.getCardType().trim().length() > 0 ){
							strCardNhType = cardNhInfo.getCardType();	// ī������
							golfCardNhYn = "Y";
						}
						tourBlackYn = "Y";
						tourJuminNo = cardNhInfo.getJuminNo();
					}
				}
				
				
				//VIPī�� �������� üũ 2010.09.14 �ǿ���
				String select_grade_no = StrUtil.isNull(mbr.getVipMaxGrade(), ""); // grade ==>  03:e-PT, 12:PT12, 30:���̾Ƹ��, 91:���Ǵ�Ƽ
				//if (select_grade_no.equals("30")) select_grade_no = "12";	
												
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
				
				//ž����ī�� �������� üũ				
				try {
					List topGolfCardList = mbr.getTopGolfCardInfoList();
					CardInfoEtt cardInfoTopGolfEtt = new CardInfoEtt();
					
					if( topGolfCardList!=null && topGolfCardList.size() > 0 )
					{
						for (int i = 0; i < topGolfCardList.size(); i++) 
						{
							cardInfoTopGolfEtt = (CardInfoEtt)topGolfCardList.get(0);
							topGolfCardNo = cardInfoTopGolfEtt.getCardNo();
							
							topGolfCardYn = "Y";
							debug("## ž����ī�� ���� ȸ�� | topGolfCardNo : "+topGolfCardNo);
						}
						
						golfCardCoYn = mbr.getGolfCardCoYn();
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
			System.out.print("## �ű�����ī�� ���� | ID : "+userid+" | VIPī�� : "+vipCardYn+" | ž����ī�� : "+topGolfCardYn+" | ��ġī�� : "+richCardYn+" \n");														
			System.out.print("## ��������ī�� ���� | ID : "+userid+" | �������ī�� : "+golfCardYn+" | ������޹�ȣ : "+strCardJoinNo+" | ����ī�� : "+golfCardNhYn+"\n");
				
			String join_chnl = "";		// ���԰��
			if(!"".equals(userid))
			{				
				TaoDataSet input = new DbTaoDataSet(title);
				input.setObject("userEtt", usrEntity);
				input.setString("golfCardYn", golfCardYn);
				input.setString("strCardJoinNo", strCardJoinNo);
				input.setString("golfCardNhYn", golfCardNhYn);
				input.setString("strCardNhType", strCardNhType);
				input.setString("tourBlackYn", tourBlackYn);
				input.setString("tourJuminNo", tourJuminNo);
				input.setString("vipCardYn", vipCardYn);
				input.setString("topGolfCardYn", topGolfCardYn);
				input.setString("topGolfCardNo", topGolfCardNo);
				input.setString("richCardYn", richCardYn);
				input.setString("golfCardCoYn", golfCardCoYn);
				input.setString("golfCardCoYn", golfCardCoYn);
				input.setString("jbCardYn", jbCardYn);
				
				String usrId = usrEntity.getAccount();
				if(!"".equals(usrId) && usrId != null)
				{
				/////////////////////////////////////////////
				// �α��� �α����� üũ
				/////////////////////////////////////////////				
				rs = con.execute("login.GolfLoginLogInsProc",input); 

				rs.next();
				returnResult = rs.getString("RESULT");
				end_date = rs.getString("end_date");
				join_chnl = rs.getString("join_chnl");
				System.out.print("## frontLoginCheck | �α��� �α����� üũ | ID : "+userid+" | returnResult : "+returnResult+"\n");
				}
			}
			
	        String returnUrlTrue = "";
	        String resultMsg = "";
	        String script = "";
	        DbTaoResult mkMemberCheck =null;


	    	String conSSO = (String) SessionUtil.getSessionAttribute(request, "conSSO");
	        String jumpUrl = CookieManager.getCookieValue("GOLF_REQ_UURL", request );
	        // ����Խ��ǿ��� �Ѿ���� �Ķ���� ����
	        String bbs = CookieManager.getCookieValue("bbs", request );
	        String slsn_type_cd = CookieManager.getCookieValue("slsn_type_cd", request );
	        String svod_clss = CookieManager.getCookieValue("svod_clss", request );
	        String scoop_cp_cd = CookieManager.getCookieValue("scoop_cp_cd", request );
	        String s_exec_type_cd = CookieManager.getCookieValue("s_exec_type_cd", request );
	        String p_idx = CookieManager.getCookieValue("p_idx", request );
			debug("=`=`=`=`=`=`=`=`=`=` jumpUrl => " + jumpUrl);
			debug("=`=`=`=`=`=`=`=`=`=` returnResult => " + returnResult);
			String orgActionKey = (String) SessionUtil.getSessionAttribute(request, "orgActionKey");
			String orgUURL 		= (String) SessionUtil.getSessionAttribute(request, "UURL");
			
			System.out.print("## frontLoginCheck | ����URL���ϰ� | orgActionKey : "+orgActionKey+" | orgUURL : "+orgUURL+"\n");
			
			// app/golfloung/GolfTopGolfCardList.do :  

			
			/*returnResult join_frame2 
			 * 00 : �α���
			 * 01 : TM ȸ������
			 * 02 : ȸ������
			 * 03 : �簡�� 
			 * 04 : �α��κҰ� | �ֹι�ȣ �������� �ʴ� ���
			 * 05 : �α��κҰ� | ����/���� �� �ƴ� �ٸ������� ���� ȸ���� ������ ���
			 * 06 : ������ȸ�� ���Ե��� ������
			 * 07 : ������ȸ�� ������Ʈ ȸ�� -> SMS �߽�
			 * 08 : ����ȸ���̸鼭 ����ī���̿��� ȸ��
			 * 09 : ����ī��(���Ǿ���)
			 * 10 : ��������ī��ȸ��
			 * 11 : ������ȸ�� �ֹε�Ϲ�ȣ �Է� ������
			 * 12 : ������ȸ��(����)
			 * 13 : ������ȸ�� �Ⱓ���� ȸ�� -> SMS �߽�
			 * */
			
						
			if("".equals(jumpUrl) || jumpUrl == null || "null".equals(jumpUrl))
			{
				jumpUrl = "golfIndex";															
			}
			
			if (returnResult.equals("01")){			// TM ȸ������
	        	returnUrlTrue = "GolfMemJoinTm.do";
	        }else if (returnResult.equals("02")){		// ȸ������
	        	returnUrlTrue = "GolfMemJoinNoCard.do";
	        }else if (returnResult.equals("11")){		// �ֹε�Ϲ�ȣ �Է� ������
	        	returnUrlTrue = "GolfMemBcJoinIpinForm.do?type=golf";
	        }else if (returnResult.equals("06")){		// ������ȸ�� ���Ե��� ������
	        	returnUrlTrue = "GolfMemJoinEvt.do?type=ibkGold";
	        }else if (returnResult.equals("15")){		// �ű԰���/Żȸȸ�� �簡�� ��ȸ����� ���Ե��� ������	        	
	        	returnUrlTrue = "GolfMemMonth.do";	        	
	        }else if (returnResult.equals("08")){		// ����ȸ���̸鼭 ����ī���̿��� ȸ��

	        	resultMsg = "���ȸ���� ����ī��� ȸ�������� �Ͻźи� ����� �����Ͻʴϴ�." +
	        				"\\n����ī�� �����ڴ� ���� ȸ������ �����Ͽ� �ֽñ� �ٶ��ϴ�." +
	        				"\\n(�� TOP����ī�� ���� ����ī�� �����ڴ� ����ȸ������ ���� �����մϴ�.)";	        	
	        	script = "window.top.location.href='/app/golfloung/index.jsp';";
	        	
	        }else if (returnResult.equals("09")){		// ����ī��ȸ������ 2009.08.25 �ǿ��� - ���nO�� �Ѱ��ش�.

	        	if("030698".equals(strCardJoinNo) || "031189".equals(strCardJoinNo) || "031176".equals(strCardJoinNo) )
	        	{
	        		returnUrlTrue = "GolfMemJoinCard.do?code=6";
	        	}
	        	else if("394033".equals(strCardJoinNo) )
	        	{
	        		returnUrlTrue = "GolfMemJoinCard.do?code=14";
	        	}
	        	else if("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo) )
	        	{
	        		returnUrlTrue = "GolfMemJoinCard.do?code=15";
	        	}
	        	else if(strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
						||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
						||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub")) )
	        	{
	        		returnUrlTrue = "GolfMemJoinCard.do?code=20";
	        	}
	        	else
	        	{
	        		returnUrlTrue = "GolfMemJoinCard.do?code=5";
	        	}
	        	
	        }else if (returnResult.equals("10")){		// ��������ī��ȸ������ 2009.11.06 ������
	        	
	        	//03:ƼŸ��, 12:�÷�Ƽ��, 48:�йи�ī��

	        	if("03".equals(strCardNhType)){
	        		
	        		returnUrlTrue = "GolfMemJoinCardNh.do?code=8";	// ƼŸ��
	        		
	        	}else if("12".equals(strCardNhType)){
	        		
	        		returnUrlTrue = "GolfMemJoinCardNh.do?code=9";	// �÷�Ƽ��
	        		
	        	}else if("48".equals(strCardNhType)){ 
	        		
	        		returnUrlTrue = "GolfMemJoinCardNh.do?code=10";	// �йи�ī��
	        		
	        	}
	        	
	        	
	        }else if (returnResult.equals("12")){		// ������ȸ��(����)

	        	returnUrlTrue = "GolfMemJoinCardNh.do?code=10";	// ������ȸ��
	        	
	        }else if (returnResult.equals("14")){ //����ȸ�����̺��� �����ϴ�(Żȸȸ������)  ��ȸ����� ó�� �б� 
	        
	         	returnUrlTrue = orgUURL;
	        	        	
	        }else if (returnResult.equals("00") || returnResult.equals("07") || returnResult.equals("13")){		// �α���

				// ���� ������ - ��������� ȸ�������� �Ϸ�Ǿ����� 00�� 00�ϱ��� ���񽺸� �̿��� �� �ֽ��ϴ�
	        	if(returnResult.equals("07") || returnResult.equals("13")){
					HashMap smsMap = new HashMap();
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", usrEntity.getName());
					smsMap.put("sPhone1", usrEntity.getMobile1());
					smsMap.put("sPhone2", usrEntity.getMobile2());
					smsMap.put("sPhone3", usrEntity.getMobile3());

					SimpleDateFormat fmt = new SimpleDateFormat("MM�� dd��");   
					GregorianCalendar cal = new GregorianCalendar();
			        cal.add(cal.MONTH, 2);
			        Date edDate = cal.getTime();
			        String strEdDate = fmt.format(edDate);	// ����ȸ���Ⱓ ������
			        
					String smsClss = "674";
					//String message = "��������� ȸ�������� �Ϸ�Ǿ����� "+strEdDate+"���� ���񽺸� �̿��� �� �ֽ��ϴ�";

					String message = "";
		        	if(returnResult.equals("07")){
		        		message = "��������� Goldȸ�������� �Ϸ�Ǿ�����, "+strEdDate+"���� ���� �̿� �����մϴ�.";
		        	}else{
		        		message = "IBK�¼��۷��̵� �̺�Ʈ�� ���ᰡ�����ڰ� "+end_date+"���� 2��������Ǿ����ϴ�.";
		        	}

					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
	        	}
	        	if("join_frame2".equals(orgActionKey))
	        	{
	        		returnUrlTrue = orgUURL;
	        	}
	        	else
	        	{
	        		returnUrlTrue = jumpUrl+".do?bbs="+bbs+"&slsn_type_cd="+slsn_type_cd+"&svod_clss="+svod_clss+"&scoop_cp_cd="+scoop_cp_cd+"&s_exec_type_cd="+s_exec_type_cd+"&p_idx="+p_idx;

	        	}
				
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

				// TMȸ�� ��ȭ���ű� ���� �̺�Ʈ ó�� ���� 
				dataSet.setString("tm_evt_no", "119");
				dataSet.setString("userSocid", usrEntity.getSocid());
				dataSet.setString("userAccount", usrEntity.getAccount());
				
				// 1) ������ Ŭ���ϰ� ������ ��� ���� üũ
				String currDate  = DateUtil.currdate("yyyyMMdd");
				String isTmMovie = (String)request.getSession().getAttribute("isTmMovie");
				
				debug("GolfmemInsActn:::isTmMovie : " + isTmMovie + " / userSocid : " + usrEntity.getSocid());
				if(isTmMovie == null){
					isTmMovie = "N";
				}
				
				if(isTmMovie.equals("Y") ){
					
					/*
					if(join_chnl.equals("0103")){
						// ī�� TM SK ������
						script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); location.href='"+returnUrlTrue+"'" + script;
						request.getSession().removeAttribute("isTmMovie");
				
					}else{*/
				
						//TM ��ȭ ���α� �̺�Ʈ START********************************************************************************
						
						// 2) TMȸ������ Ȯ��

						String tm_join_chnl = "";
						String tm_cupn_kind = "";
						

						GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");
						DbTaoResult tmMovieTmCheck = (DbTaoResult) proc_tmMovie.isTmCheckLogin(context, request, dataSet);	
						
						if (tmMovieTmCheck != null && tmMovieTmCheck.isNext()) {
							tmMovieTmCheck.first(); 
							tmMovieTmCheck.next();
							if(tmMovieTmCheck.getString("RESULT").equals("00")){
								tm_join_chnl = tmMovieTmCheck.getString("JOIN_CHNL");
								tm_cupn_kind = tmMovieTmCheck.getString("CUPN_KIND");
							}
							
							if(tm_join_chnl.equals("11")){

								// ī�� TM SK ������
								script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); location.href='"+returnUrlTrue+"'" + script;
								request.getSession().removeAttribute("isTmMovie");
								
							}else{
								
								// 20100731 ���� �����ڴ� SK������ �� ������ ��ȭ ���ű�
								if(tm_cupn_kind.equals("SK")){
									
									// SK ������
									script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); location.href='"+returnUrlTrue+"'" + script;
									request.getSession().removeAttribute("isTmMovie");
									
								}else{
									
									// ��ȭ���α�
									// 3) �̺�Ʈ �Ⱓ üũ
									String from_date = "";
									String to_date   = "";
									
									DbTaoResult tmMovieDateCheck = (DbTaoResult) proc_tmMovie.eventDateCheck(context, request, dataSet);
									
									if (tmMovieDateCheck != null && tmMovieDateCheck.isNext()) {
										tmMovieDateCheck.first(); 
										tmMovieDateCheck.next();
										if(tmMovieDateCheck.getString("RESULT").equals("00")){
											from_date = tmMovieDateCheck.getString("FROM_DATE");
											to_date = tmMovieDateCheck.getString("TO_DATE");
											debug("GolfmemInsActn:::from_date ~ to_date >>>>>>>>>>>>" + from_date + "~" + to_date + ",���糯¥ : "+ currDate);
										}
									}
							
									if((Integer.parseInt(from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(to_date))){
										
										// 4) ����600(����)�� ������ �̺�Ʈ ���� ���� ���� 
										String tmEvtCntYn = (String) proc_tmMovie.eventCountYn(context, request, dataSet);		
										debug("GolfmemInsActn:::tmEvtCntYn : " + tmEvtCntYn );			
										if(tmEvtCntYn.equals("Y")){
											
											// 5) ��ȭ���ű� ���޿��� Ȯ�� - 1�� �̻��ϰ��
											int useEvtCpnCnt = (int) proc_tmMovie.useEvtCpnCnt(context, request, dataSet);	
											debug("GolfmemInsActn:::useEvtCpnCnt : " + useEvtCpnCnt );			
											
											if(useEvtCpnCnt>0){
												// 5-1) 1���̻��̸�  ������ȣ 4�� �ٽ� ������
												script = " window.open('GolfEvntTmMovieCpnPop.do','tmCoupon', 'width=474, height=202'); location.href='"+returnUrlTrue+"'" + script;
												
											}else{
												// 6) ��������� �������� ��� �̺�Ʈ �ȳ� �˾� ���
												script = " window.open('GolfEvntTmMovieInfoPop.do','tmInfo', 'width=539, height=695'); location.href='"+returnUrlTrue+"'" + script;
											}		
											
											debug("GolfmemInsActn:::script : " + script + " / returnUrlTrue : " + returnUrlTrue );	
											
											request.getSession().removeAttribute("isTmMovie");
							
										} else {	// 4) ����600(����)�� ������ �̺�Ʈ ���� ���� ����  
											request.getSession().removeAttribute("isTmMovie");
										}
							
									} else {	// 3) �̺�Ʈ �Ⱓ üũ
										request.getSession().removeAttribute("isTmMovie");
									}
								}
							}
							
						}/*else {	// 2) TMȸ������ Ȯ��
							//request.getSession().removeAttribute("isTmMovie");
							
							// DM �̺�Ʈ ȸ�� ��ȭ ���α� ���� => e-champ ���� ȸ�� 'EVENTGL12345', 'EVENTECHAMP201007', 'EVENTLETTER08'
							if((Integer.parseInt("20100501") <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt("20100930"))){	// �Ⱓ üũ
								int cpMovieCheck = (int) proc_tmMovie.isCpCheckLogin(context, request, dataSet);
								if(cpMovieCheck > 0){	// DM �̺�Ʈ ȸ��

									// 4) ����600(����)�� ������ �̺�Ʈ ���� ���� ���� 
									String tmEvtCntYn = (String) proc_tmMovie.eventCountYn(context, request, dataSet);		
									debug("GolfmemInsActn:::tmEvtCntYn : " + tmEvtCntYn );			
									if(tmEvtCntYn.equals("Y")){
										
										// 5) ��ȭ���ű� ���޿��� Ȯ�� - 1�� �̻��ϰ��
										int useEvtCpnCnt = (int) proc_tmMovie.useEvtCpnCnt(context, request, dataSet);	
										debug("GolfmemInsActn:::useEvtCpnCnt : " + useEvtCpnCnt );			
										
										if(useEvtCpnCnt>0){
											// 5-1) 1���̻��̸�  ������ȣ 4�� �ٽ� ������
											script = " window.open('GolfEvntTmMovieCpnPop.do','tmCoupon', 'width=474, height=202'); location.href='"+returnUrlTrue+"'" + script;
											
										}else{
											// 6) ��������� �������� ��� �̺�Ʈ �ȳ� �˾� ���
											script = " window.open('GolfEvntTmMovieInfoPop.do','tmInfo', 'width=539, height=695'); location.href='"+returnUrlTrue+"'" + script;
										}		
										
										debug("GolfmemInsActn:::script : " + script + " / returnUrlTrue : " + returnUrlTrue + " / Account : " + usrEntity.getAccount() );	
										
										request.getSession().removeAttribute("isTmMovie");
						
									} else {	// 4) ����600(����)�� ������ �̺�Ʈ ���� ���� ����  
										request.getSession().removeAttribute("isTmMovie");
									}
								}else{	
									request.getSession().removeAttribute("isTmMovie");
								}// DM �̺�Ʈ ȸ��
							}else{
								request.getSession().removeAttribute("isTmMovie");
							}// �Ⱓ üũ
							
						}*/	// 1) ������ Ŭ���ϰ� ������ ��� ���� üũ					
					//}	// TMȸ�� ��ȭ���ű� ���� �̺�Ʈ ó�� ����
					
					//TM ��ȭ ���α� �̺�Ʈ END********************************************************************************
				}
				////////////////////////////////////////
				// 2010.08.30 ������ ȸ������------START//
				GolfEvntMkMemberProc proc_mkMember = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
				//1. �̺�Ʈ �Ⱓ üũ 
				DbTaoResult mkMemberDateCheck = (DbTaoResult) proc_mkMember.eventDateCheck(context, request, dataSet);
				String mk_from_date="";
				String mk_to_date="";
				
				if (mkMemberDateCheck != null && mkMemberDateCheck.isNext()) {
					//mkMemberDateCheck.first(); 
					mkMemberDateCheck.next();
					if(mkMemberDateCheck.getString("RESULT").equals("00")){
						mk_from_date = mkMemberDateCheck.getString("FROM_DATE");
						mk_to_date = mkMemberDateCheck.getString("TO_DATE");
						debug("GolfmemInsActn:::from_date ~ to_date >>>>>>>>>>>>" + mk_from_date + "~" + mk_to_date + ",���糯¥ : "+ currDate);
					}
				}
				//�̺�Ʈ �Ⱓ�̸�
				if((Integer.parseInt(mk_from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(mk_to_date))){
					//2. ������ ��� ȸ������ üũ
					dataSet.setString("to_date", mk_to_date);		// ���۳�¥
					dataSet.setString("from_date", mk_from_date);		// ���ᳯ¥
					mkMemberCheck = (DbTaoResult) proc_mkMember.isMkMember(context, request, dataSet);
					
					if (mkMemberCheck != null && mkMemberCheck.isNext()) {
						//������ ��� ȸ���̸�
						mkMemberCheck.first(); 
						mkMemberCheck.next();
						if(mkMemberCheck.getString("RESULT").equals("00")){
							debug("returnUrlTrue = " +returnUrlTrue);
							script = " window.open('GolfEvntMkMemberPop.do','tmInfo', 'width=600, height=580'); location.href='"+returnUrlTrue+"'" + script;
						}
					}
				}
				// 2010.08.30 ������ ȸ������------END//
				//////////////////////////////////////

	        }else if (returnResult.equals("03")){		// Ż���� ����ȸ���� �Ѵ޾ȿ� �簡�� �ȵȴ�.
	        	// �簡�� �κ��� �����ʿ��� ó���Ѵ�.
				//script = "parent.location.href='http://ssodev.golfloung.com:9611/nls3/ssologout_golf.jsp';";
	        	//returnUrlTrue = "GolfBkLoginActn.do";
	        	//resultMsg = "�����Ͻ� �� �ִ� �Ⱓ�� �ƴմϴ�.";

	        	returnUrlTrue = "GolfMemJoinNoCard.do?reJoin=Y";
	        }else if (returnResult.equals("04")){		// �ֹε�Ϲ�ȣ ���� ȸ���� �α��� �� �� ����.
	        	script = "parent.logOut('"+conSSO+"');";
	        	resultMsg = "��������� ���񽺴� ����ȸ���� �̿밡���մϴ�.";
	        }else if (returnResult.equals("05")){		// ���ȸ���� �α��� �� �� ����. || ���ȸ���߿��� BCDBA.TBENTPMEM �� MEM_CLSS�� 5,6 �� �ƴ� ���ȸ��
	        	script = "parent.logOut('"+conSSO+"');";
	        	resultMsg = "��������� ���񽺴� ����ȸ���� �̿밡���մϴ�.";
	        }else if (returnResult.equals("788")){		// VIP ī�� ����������
	        	
	        	// VIPī�� ������������ �̰��������� ž������ŷ������ ���ٽÿ� ���� ȭ�� ���� Skip ó�� 2010.12.10 �ǿ���
	        	debug("## VIPī�� ���� , ȭ��Ʈȸ�������� ����ó�� ������ üũ");
	        	if("/app/golfloung/GolfTopGolfCardList.do".equals(orgUURL) || "/app/golfloung/GolfTopGolfCardStatus.do".equals(orgUURL) || "/app/golfloung/GolfTopGolfCardNoticeList.do".equals(orgUURL) || "/app/golfloung/GolfTopGolfCardGuide.do".equals(orgUURL)  || "/app/golfloung/GolfRoundAfterList.do".equals(orgUURL)  )
	        	{
	        		debug("## VIPī�� ���� , ȭ��Ʈȸ�������� ����ó�� | orgUURL : "+orgUURL);
	        		returnUrlTrue = orgUURL;
	        	}
	        	else
	        	{
	        		returnUrlTrue = "GolfMemVipCardJoin.do";
	        	}
	        	
	        }else if (returnResult.equals("787")){		// VIP ī�� ����������
	        	returnUrlTrue = "GolfMemVipCardAgreeJoin.do";
	        }else if (returnResult.equals("786")){		// topGolf ����������
	        	returnUrlTrue = "GolfMemTopGolfCardJoin.do";
	        }else if (returnResult.equals("785")){		// ��ġī�� ����������
	        	returnUrlTrue = "GolfMemRichCardJoin.do";
	        }
			
			
			if("join_frame2".equals(orgActionKey))
        	{
        		returnUrlTrue = orgUURL;
        	}
        	
			
			debug("## returnUrlTrue : "+returnUrlTrue);
			
			request.setAttribute("script", script);  
			request.setAttribute("returnUrl", returnUrlTrue);
			request.setAttribute("resultMsg", resultMsg); 
		
			

		} catch (Throwable t) {
			error(this.getClass().getName(), t);
			throw new BaseException("SYSTEM_ERROR", null, t);  
		} finally {
			try {
				if (con != null)
					con.close();
				//if (Ccon != null)
				//	Ccon.close();
			} catch (Throwable ignored) {
			}
		}
		return getActionResponse(context, "default");
	}	
	/**
	 * Front logout   
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @return ActionResponse
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse frontLogout(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		RequestParser parser = context.getRequestParser("default", request, response);

		String subpage_key = "default";
		Map paramMap = new HashMap();
		String site = parser.getParameter("site");
		
		String returnUrl = "http://sso1.golfloung.com/nls3/ssologout_golf.jsp";
		String conSSO = (String) SessionUtil.getSessionAttribute(request, "conSSO");
		
		if(!GolfUtil.empty(conSSO)){
			if(conSSO.equals("DEV")){
				returnUrl = "http://ssodev.golfloung.com:9611/nls3/ssologout_golf.jsp";
			} else if (conSSO.equals("SSO1")) {
				returnUrl = "http://sso1.golfloung.com/nls3/ssologout_golf.jsp";
			} else if (conSSO.equals("SSO2")) {
				returnUrl = "http://sso2.golfloung.com/nls3/ssologout_golf.jsp";
			} else {
				returnUrl = "http://sso1.golfloung.com/nls3/ssologout_golf.jsp";
			}
		}
		
		//�α׾ƿ��� ���� ���� 2009.10.31
		HttpSession session = request.getSession(true);	 
		session.setAttribute("actionKey", null); 
		session.setAttribute("actnkey", null);
		session.setAttribute("bbs", null); 
		session.setAttribute("COEVNT_ENTITY", null);
		session.setAttribute("conSSO", null);
		session.setAttribute("FRONT_ENTITY", null);	
		session.setAttribute("GOLF_ENTITY", null);
		session.setAttribute("GOLF_REQ_UURL", null);	
		session.setAttribute("isInterpark", null);	
		session.setAttribute("message", null);	
		session.setAttribute("orgActionKey", null);
		session.setAttribute("p_idx", null);
		session.setAttribute("ParameterManipulationProtectKey", null);	
		session.setAttribute("PARM", null);
		session.setAttribute("requestURI", null);	
		session.setAttribute("s_exec_type_cd", null); 
		session.setAttribute("scoop_cp_cd", null);
		session.setAttribute("SESSION_USER", null);
		session.setAttribute("slsn_type_cd", null);
		session.setAttribute("svod_clss", null);
		session.setAttribute("SYSID", null);
		session.setAttribute("token", null);
		session.setAttribute("UURL", null);   
		//session.invalidate();	
		
		
		debug("===========frontLogout============conSSO : " + conSSO);
		debug("===========frontLogout============returnUrl : " + returnUrl);
		
		request.setAttribute("returnUrl", returnUrl);
        request.setAttribute("paramMap", paramMap);

		if(!"bc".equals(site)){
			paramMap.put("site", site);
			subpage_key = "golfMain";
		}
		return super.getActionResponse(context, subpage_key);
	}	

	/**
	 * Front ���� �ʿ� ȭ�� Ŭ���� �ߴ� �˾� ȭ�� ȭ��  
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @return ActionResponse
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse loginCheckLY(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		Connection con = null;
		try {
			RequestParser parser = context.getRequestParser("default", request, response);
			Map paramMap = parser.getParameterMap();

			String ipAddressInfo = (String)context.getUserDefineObject("ShopConfig", "IPADDRESS_INFO");			
			String serverIp = "";
			try{
				serverIp = java.net.InetAddress.getLocalHost().getHostAddress();
			}catch(Exception e){}

			if(ipAddressInfo.trim().equals(serverIp)) paramMap.put("mainUrl", (String)context.getUserDefineObject("ShopConfig", "URL_DEV")); //���߼��� URL
			else paramMap.put("mainUrl", (String)context.getUserDefineObject("ShopConfig", "URL_REAL"));	
			
			request.setAttribute("paramMap", paramMap);						

		} catch (Throwable t) {
			error(this.getClass().getName(), t);
			throw new BaseException("SYSTEM_ERROR", null, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		return getActionResponse(context, "default");
	}
	/**
	 * �α��� or ��ȸ���ֹ� �����Է�â
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse loginLY(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {	

		RequestParser parser = context.getRequestParser("default", request, response);
		Map paramMap = parser.getParameterMap();
		
		String ipAddressInfo = (String)context.getUserDefineObject("ShopConfig", "IPADDRESS_INFO");			
		String serverIp = "";
		try{
			serverIp = java.net.InetAddress.getLocalHost().getHostAddress();
		}catch(Exception e){}
		if(ipAddressInfo.trim().equals(serverIp)) {			
			paramMap.put("mainUrl", (String)context.getUserDefineObject("ShopConfig", "URL_DEV")); 					//���߼��� URL			
		}else{
			paramMap.put("mainUrl", (String)context.getUserDefineObject("ShopConfig", "URL_REAL"));				//����� URL			
		}

		request.setAttribute("paramTopGnb", paramMap);
		return getActionResponse(context, "default");
	}
	
	/**
	 * �α��� ������� or ��ȸ���ֹ� �����Է�â
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse loginAgreeLY(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {	

		RequestParser parser = context.getRequestParser("default", request, response);
		Map paramMap = parser.getParameterMap();
		
		String ipAddressInfo = (String)context.getUserDefineObject("ShopConfig", "IPADDRESS_INFO");			
		String serverIp = "";
		try{
			serverIp = java.net.InetAddress.getLocalHost().getHostAddress();
		}catch(Exception e){}
		if(ipAddressInfo.trim().equals(serverIp)) {			
			paramMap.put("mainUrl", (String)context.getUserDefineObject("ShopConfig", "URL_DEV")); 					//���߼��� URL			
		}else{
			paramMap.put("mainUrl", (String)context.getUserDefineObject("ShopConfig", "URL_REAL"));				//����� URL			
		}

		request.setAttribute("paramTopGnb", paramMap);
		return getActionResponse(context, "default");
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
