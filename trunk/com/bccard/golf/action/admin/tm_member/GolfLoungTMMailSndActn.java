/**************************************************************************************************
*  클래스명	: GolfLoungTMFeeMemberJoinActn
*  작 성 자	: 강선영 [yskkang@bccard.com]
*  내    용	: 골프 라운지 TM 고객대상 포인트 결제 유료회원가입 처리
*  적용범위	: golfloung
*  작성일자	: 2009.07.02
* http://develop.bccard.com:13300/app/golfloung/admTmMember.do

1. 골프라운지 TM성공 내역 조회(대상자 테이블- bcdba.TBLUGTMCSTMR) - List
2. 대상회원 가입유무 확인 (유료회원 테이블 - bcdba.TBGGOLFCDHD) JONN_CHNL_CLSS='03'  블루고객이면 JOIN_CHNL : 0002  골드고객이면 JOIN_CHNL : 0003
3. 대상회원 포인트 조회  
4. 포인트 있을경우 포인트 차감 (MJF6220I2100)
	- 대상회원 가입(유료회원 테이블 insert)
	- 연회비 결제내역 테이블 insert
		* BCDBA.TBGGOLFCDHD		(골프라운지회원    table)
		* bcdba.TBGLUGANLFEECTNT (유료승인내역  table)
5. 포인트 없을경우 
	- 내역테이블에 거절 내용 기록
	- 
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;
import java.net.InetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.ResultException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.common.DateUtil;

import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.jolt.JtProcess;

import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput; 

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.AppConfig;


/**
 * 골프 라운지 TM
 * @version 2009.07.02
 * @author  강선영 [yskkang@bccard.com]
 */
public class GolfLoungTMMailSndActn extends AbstractAction  {

	
	/** *****************************************************************
	 * Action excecution method
	 * @param context		WaContext Object
	 * @param request		HttpServletRequest Object
	 * @param response		HttpServletResponse Object
	 * @return				ActionResponse Object
	 * @exception IOException, ServletException, BaseException if errors occur
	 ***************************************************************** */

	public ActionResponse execute(WaContext context
						,HttpServletRequest request
						,HttpServletResponse response)
	throws IOException, ServletException, BaseException {

		ResultException rx = null;
		HttpSession session	= request.getSession(false);		
		RequestParser parser = context.getRequestParser("default", request, response);

		String goPage	 = "javascript:window.open('history.go(-1)', '_blank', '');self.close();";
		String title	 = "골프 라운지 TM 유료가입";
		String addButton = "<img src='/golf/img/common/btn/btn_definite.gif' border='0'>";//확인버튼
		String responseKey = "default";
		

		Vector juminNoList = new Vector();			

		String  jumin_no		= "";
		String  auth_no			= "";
		String  mb_cdhd_no		= "";
		String	golf_clss		= "";  //등급 1:골드 2: 블루 3:챔피온
		String  hg_nm			= "";
		String  email_addr		= "";
		String	pay_amt			= "0";
		String	memkind			= "";					
		String	ret_code		= "";
		String	ret_msg			= "";
		String	ret_code2		= "";
		String	ret_msg2		= "";
		String	golf_class_nm	= "";
		String  strCtgo			= "8"; //5:챔피온 6:블루 7:골드 8:화이트

		String	auth_clss		= "";
		String	card_no			= "";
		String	vald_lim		= "";
		String	tm_buz			= "";
		String	hp_ddd_no		= "";
		String	hp_tel_hno		= "";
		String	hp_tel_sno		= "";
		String	hp			= "";
		String  recp_date	= "";
		String  getvald_lim	= "";

		String  disc_clss	= "";
		String  dc_amt		= "";

		String  tb_rslt_clss ="01";

		boolean auth	= false;		

		String action_key = super.getActionKey(context);
		debug(action_key);
		String str = "***";
		String str_hp = "0000";



		try {             	
				info("[골프라운지 TM 유료회원 결제 시작] GolfLoungTMMailSndActn START 시작시간:" + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );
 
				GolfLoungTMProc proc = new GolfLoungTMProc();
				
				juminNoList = proc.getMailSend(context,"일자yyyymmdd");         //TM 등록대상 조회 : 주민번호List	
	
				String hostAddress = InetAddress.getLocalHost().getHostAddress(); 
				String devip = "";
				
				try {
					devip = AppConfig.getAppProperty("DV_WAS_1ST");
				} catch(Throwable t) {}


				info("[골프라운지 TM 서버IP="  + hostAddress );	
				info("[골프라운지 TM 개발IP="  + devip );

				int a = 0;        //   총 처리건수
				int b = 0;        // 정상 처리건수
				int memcnt = 0;   // 
				info("[골프라운지 TM 등록대상 조회 수] "+juminNoList.size());

				while(a < juminNoList.size())	{   

					Hashtable data = new Hashtable();
					data = (Hashtable)juminNoList.get(a);

					jumin_no =(String)data.get("JUMIN_NO"); //주민번호
					golf_clss =(String)data.get("GOLF_CDHD_GRD_CLSS"); //등급 1:골드 2: 블루 3:챔피온 4:블랙
					hg_nm =(String)data.get("HG_NM");	//한글명
					email_addr =(String)data.get("EMAIL_ID"); //이메일

					if	(  "1".equals(golf_clss) ) pay_amt ="25000";
					else if (  "2".equals(golf_clss) ) pay_amt ="50000";
					else if (  "3".equals(golf_clss) ) pay_amt ="200000";
					else if (  "4".equals(golf_clss) ) pay_amt ="120000";

					hp_ddd_no	=	(String)data.get("HP_DDD_NO");
					hp_tel_hno	=	(String)data.get("HP_TEL_HNO");
					hp_tel_sno	=	(String)data.get("HP_TEL_SNO");
					 
					disc_clss	=	(String)data.get("DISC_CLSS");
					dc_amt	=	(String)data.get("DC_AMT");

					debug("hphp  시작 hp_ddd_no + hp_tel_hno + hp_tel_sno=>>"+ hp_ddd_no + hp_tel_hno + hp_tel_sno );

					hp = "";
					if (hp_ddd_no.length()>=3 && hp_tel_hno.length()>=3 && hp_tel_sno.length()>=4 )	{
						debug("hphp   hp_ddd_no + hp_tel_hno + hp_tel_sno=>>"+ hp_ddd_no + hp_tel_hno + hp_tel_sno );
						debug("hphp   hp_tel_sno.indexOf(str_hp)=>>"+ hp_tel_sno.indexOf(str_hp));

						if ( hp_tel_sno.indexOf(str_hp) ==  -1 ) {
							hp = hp_ddd_no + hp_tel_hno + hp_tel_sno;
						}
					}
					debug("hphphphphphphphphphphphphphp=>>"+hp);
					debug("disc_clss="+disc_clss+",dc_amt="+dc_amt);

												
					/********************************************
					* SMS 발송 
					******************************************** 

					if (hp.length() > 9)
					{
						// SMS 관련 셋팅
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", hg_nm);
						smsMap.put("sPhone1", hp_ddd_no);
						smsMap.put("sPhone2", hp_tel_hno);
						smsMap.put("sPhone3", hp_tel_sno);
						smsMap.put("sCallCenter", "15666578");
						
						debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "674";

						String message = "[Golf Loun.G]"+hg_nm+"님 골프라운지(www.golfloung.com)회원가입진행해주시기바랍니다" ;
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = "";
						
						//SMS발송
						if (devip.equals(hostAddress)) {  //개발기
							//smsRtn = smsProc.send(smsClss, smsMap, message);
							debug("개발기 SMS>>>>>>>>>>>"+ hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno+">>>>>>>>>>>>>>>>>>>>>.."+message) ;
							info("[골프라운지 SMS 개발기는 발송안됩니다. ] 핸드폰번호 |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|메세지|" + message);

						} else { //운영기
							smsRtn = smsProc.send(smsClss, smsMap, message);
							info("[골프라운지 SMS 발송] 핸드폰번호 |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|메세지|" + message);
							//debug("운영기 SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
						}	

					} */



					/********************************************
					* 이메일 발송 
					*********************************************/
					String[] email = BcUtil.getEmailArray(email_addr);
					int email_cnt = email.length;
					String mail_clss="oil";
		

					if ( !(email_addr=="" || "".equals(email_addr))  && email_cnt == 2  )
					{
						info("[골프라운지 TM 메일발송 시작] 주민번호 |" + jumin_no + "|성명]"+hg_nm+"|email_addr|" + email_addr);
						proc.sendMail(email_addr,hg_nm,golf_class_nm,mail_clss);
						info("[골프라운지 TM 메일발송 완료] 주민번호 |" + jumin_no + "|성명]"+hg_nm+"|email_addr|" + email_addr);
					}


	
								
					a++;
				} /*while문*/ 

				info("[골프라운지 TM 회원 GolfLoung 유료가입처리 END : 총" + a + "건 처리 중 " + b + "건 정상가입 종료시간:"  + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );										

				

        } catch (Throwable ex) {

			warn("GolfLoungTMFeeMemberJoinActn Throwable | jumin_no:" + jumin_no + " | auth_no:" + auth_no + " ... 처리중ERR ", ex);

            rx = new ResultException(ex);			
			rx.setTitleImage("error");			
			rx.addButton(goPage, addButton);
			rx.setKey("SYSTEM_ERR");

			throw rx;
        }
		return super.getActionResponse(context, responseKey);
	}

}
