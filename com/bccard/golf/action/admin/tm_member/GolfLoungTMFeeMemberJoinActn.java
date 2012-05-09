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

************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.03.30    이경희	   TM채널 추가 (리딩아이, 월앤비전)
* 2011.04.12    이경희	   기존회원 가입경로구분코드원복
* 2011.04.20    이경희	   윌앤비젼 가맹점 번호 767445661추가
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;


/**
 * 골프 라운지 TM
 * @version 2009.07.02
 * @author  강선영 [yskkang@bccard.com]
 */
public class GolfLoungTMFeeMemberJoinActn extends AbstractAction  {

	
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
		String  rcru_pl_clss = "";
		String  product_no = "";
		String  mail_clss =  "";
		String  cdhd_ret ="";

		String  disc_clss	= "";
		String  dc_amt		= "";

		String  tb_rslt_clss ="01";

		boolean auth	= false;		

		String action_key = super.getActionKey(context);
		debug(action_key);
		String str = "***";
		String str_hp = "0000";

		boolean  bln_sms = false;
		boolean  bln_email = false;


		try {             	
				info("[골프라운지 TM 유료회원 결제 시작] GolfLoungTMFeeMemberJoinActn START 시작시간:" + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );
 
				GolfLoungTMProc proc = new GolfLoungTMProc();
				
 
				if ("admTmMember".equals(action_key)){
					juminNoList = proc.getListTMLoungApply(context);         //TM 등록대상 조회 : 주민번호List	
				} else if ("admMojib".equals(action_key)) {
					juminNoList = proc.getListMojibLoungApply(context);         //모집인 등록대상 조회 : 주민번호List	
				}
	
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
					// email, sms 발송여부 초기화
					bln_sms = false;
					bln_email = false;
					auth = false;
					hp = "";
					recp_date = "" ; //초기화
					jumin_no = "";

					Hashtable data = new Hashtable();
					data = (Hashtable)juminNoList.get(a);

					jumin_no =(String)data.get("JUMIN_NO"); //주민번호
					debug("초기jumin_no==========>"+jumin_no);
					golf_clss =(String)data.get("GOLF_CDHD_GRD_CLSS"); //등급 1:골드 2: 블루 3:챔피온 4:블랙
					mb_cdhd_no =(String)data.get("MB_CDHD_NO");
					hg_nm =(String)data.get("HG_NM");	//한글명
					email_addr =(String)data.get("EMAIL_ID"); //이메일
					auth_clss =(String)data.get("AUTH_CLSS"); //결제구분 1:카드 2:복합결제 3:포인트
					card_no =(String)data.get("CARD_NO");
					tm_buz =(String)data.get("JOIN_CHNL"); //03:H&C, 10:CIC코리아, 11:TCK, 12:리딩아이, 13:윌앤비젼
					getvald_lim =(String)data.get("VALD_LIM");
					rcru_pl_clss =(String)data.get("RCRU_PL_CLSS"); // 0002:블루 0003:골드 0103:골드(카젠)
					product_no =(String)data.get("PRODUCT_NO"); //1:실속 Gold : 35,000원 2:기존 Gold : 54,000원 3:VIP Gold : 86,000원
										
					
					recp_date	=	(String)data.get("RECP_DATE");

					if	(  "1".equals(golf_clss) ) pay_amt ="25000";
					else if (  "2".equals(golf_clss) ) pay_amt ="50000";
					else if (  "3".equals(golf_clss) ) pay_amt ="200000";
					else if (  "4".equals(golf_clss) ) pay_amt ="120000";
					
					if ("0103".equals(rcru_pl_clss) ){
						if ( product_no ==null ) product_no="";
						if ("1".equals(product_no) ){
							pay_amt ="35000";
						}else if ("2".equals(product_no) ){
							pay_amt ="54000";
						}else if ("3".equals(product_no) ){	
							pay_amt ="86000";
						}
					}
					debug("|유효기간|"+getvald_lim + "|결제금액|"+pay_amt );

					hp_ddd_no	=	(String)data.get("HP_DDD_NO");
					hp_tel_hno	=	(String)data.get("HP_TEL_HNO");
					hp_tel_sno	=	(String)data.get("HP_TEL_SNO");
					 
					disc_clss	=	(String)data.get("DISC_CLSS");
					dc_amt	=	(String)data.get("DC_AMT");
					
					if (hp_ddd_no.length()>=3 && hp_tel_hno.length()>=3 && hp_tel_sno.length()>=4 )	{
						if ( hp_tel_sno.indexOf(str_hp) ==  -1 ) {
							hp = hp_ddd_no + hp_tel_hno + hp_tel_sno;
						}
					}
					debug("disc_clss="+disc_clss+",dc_amt="+dc_amt);

					//모집인 처리 일경우
					if ("admMojib".equals(action_key)) {
					
						//모집인일경우 할인적용
						if ("1".equals(disc_clss))	//할인율
						{
							int sale_amt = Integer.parseInt(dc_amt);
							double div = ((double)(100-(double)sale_amt)/100); //할인율 (0.9)
							int sttl_amt = (int)(Double.parseDouble(pay_amt) * div); //실결제금액
							pay_amt = String.valueOf(sttl_amt) ; //실결제금액
							debug("실결제금액="+pay_amt+",할인율="+div);

						} else if ("3".equals(disc_clss))	//할인금액
						{
							int dc = Integer.parseInt(pay_amt) - Integer.parseInt(dc_amt); //할인금액
							pay_amt = String.valueOf(dc); //할인금액
							debug("실결제금액="+pay_amt+",할인금액="+dc);
						}
					
					// TM일경우 주민번호 *** 마스킹으로 들어와 전문 하나를 더 탄다 2010.03.17 추가
					} else {


						TaoResult taoResult_jumin = null;

						JoltInput entity_jumin = null;

						entity_jumin = new JoltInput();

						entity_jumin.setServiceName("BSNINPT");
						entity_jumin.setString("fml_trcode", "MHL0420R0100"); //회원사회원번호
						entity_jumin.setString("fml_channel", "WEB");
						entity_jumin.setString("fml_sec50", "bcadmin");
						entity_jumin.setString("fml_sec51", hostAddress);
						entity_jumin.setString("fml_arg1", mb_cdhd_no); //회원사회원번호
						entity_jumin.setString("fml_arg2", "0");  //조회키 초기 0
						entity_jumin.setString("fml_arg3", "1"); //1:회원사회원번호 2:고객번호

						java.util.Properties prop = new java.util.Properties();
						prop.setProperty("RETURN_CODE","fml_ret1");

						JtProcess jtproc_jumin = new JtProcess();
						taoResult_jumin = jtproc_jumin.call(context, request, entity_jumin, prop);
						debug(taoResult_jumin.toString());
						String ret_jumin = taoResult_jumin.getString("fml_ret1").trim(); 

						if("00".equals(ret_jumin) || "01".equals(ret_jumin)){	   // resultCode -> 00:정상 01:다음키있음 02:회원사회원번호미존재 99:시스템오류
							// 값 가져올때는...
							jumin_no = taoResult_jumin.getString("fml_ret3").trim(); 
							debug("변경 jumin_no==========>"+jumin_no);

							int upcnt = proc.getUpJuminNo(context, jumin_no,mb_cdhd_no,recp_date);
						} 
						

					}


					memcnt = a + 1;
					info("[골프라운지 TM "+memcnt+" 번] 주민번호 |"+jumin_no+"| 등급(1:골드,2:블루,3:챔피온,4:블랙) |"+golf_clss+"|결제구분|"+auth_clss);

					memkind    = "";					
					ret_code   = "";
					ret_msg    = "";
					ret_code2  = "";
					ret_msg2   = "";
					auth_no    = "";

					int insertgolfcdhd    = 0;
					int insertgolffeectn  = 0;
					int	updatecnt		  = 0;
					
					memkind = proc.getIsFeeFree(context, jumin_no);        // loung 유료 회원여부 조회
					
					info("[골프라운지 TM 유료회원 여부 ] 주민번호 |"+jumin_no+"| memkind (Y:가입불가능고객(현유료회원임) N:가입가능고객) |"+memkind);

					// 회원사 회원번호 
					if ( jumin_no.indexOf(str) >=  0 ) {

						info("[골프라운지 TM 회원사 회원번호 확인] 회원사 회원번호 없음 |" + jumin_no);
						updatecnt = proc.updateIsFree(context, jumin_no,"09","","회원사회원번호미존재",recp_date);
						

					// 현재 유료회원 임 
					} else if( memkind.equals("Y") ) { 

						info("[골프라운지 TM 기가입 유료회원] 주민번호 |" + jumin_no);
						updatecnt = proc.updateIsFree(context, jumin_no,"02","","|기가입고객|",recp_date);
						//수신확인에 기가입처리(TB_RSLT_CLSS = 02 )


					} else if(  memkind.equals("N") ) {
						info("[골프라운지 TM 가입 대상회원] 주민번호 |" + jumin_no);
						
						/*  2010.07.15 제외
						 
						if (memkind.equals("0001"))	{
							//5:챔피온 6:블루 7:골드 8:화이트(무료)
							strCtgo = proc.getGolfClass(context, jumin_no); 
						} else {
							strCtgo="8"; //초기화
						}
						
						if ( !"8".equals(strCtgo) )	{
							info("[골프라운지 TM 기가입 유료회원] 주민번호 |" + jumin_no);
							updatecnt = proc.updateIsFree(context, jumin_no,"02","","|등급코드|"+strCtgo,recp_date); //수신확인에 기가입처리(TB_RSLT_CLSS = 02 )
						} else {
						*/
							
					
							//포인트결제일경우
							if ("3".equals(auth_clss)) {
						
							
								/********************************************
								* MJF6010R0100 - 0000:정상 
								* 탑포인트 조회 전문 :  for COMMIT  *
								*********************************************/	

								TaoResult taoResult = null;

								JoltInput entity0 = null;
		//find ./ -name 'was1_bcext_7201_20090714.log' | xargs grep '5411201400321'

								entity0 = new JoltInput();

								entity0.setServiceName("BSNINPT");
								entity0.setString("fml_trcode", "MJF6010R0100"); //포인트조회
								entity0.setString("fml_channel", "WEB");
								entity0.setString("fml_sec50", "bcadmin");
								entity0.setString("fml_sec51", hostAddress);
							
								entity0.setString("fml_arg1", "1"); //1:주민번호 2:카드번호 3:사업자번호
								entity0.setString("fml_arg2", "99000000"+jumin_no+"   ");  //1.주민번호:99+제휴업체코드(000000)+주민번호(13)+공란(3) 
								entity0.setString("fml_arg3", "1"); //1:개인 2:기업

								java.util.Properties prop = new java.util.Properties();
								prop.setProperty("RETURN_CODE","fml_ret1");

								JtProcess jtproc = new JtProcess();
								taoResult = jtproc.call(context, request, entity0, prop);
								debug(taoResult.toString());

								// 값 가져올때는...
								String top_ret_code = taoResult.getString("fml_ret1").trim(); 
								String top_ret_msg = taoResult.getString("fml_ret2").trim();  
								String top_point = "0";
	//		top_ret_code = "0000"; //개발기전용테스트
								
								if(!"0000".equals(top_ret_code)){
									//포인트 오류
									info("[골프라운지 TM 포인트 조회 실패] 주민번호 |" + jumin_no+"|fml_ret1|"+top_ret_code+"|fml_ret2|"+top_ret_msg);
									updatecnt = proc.updateIsFree(context, jumin_no, "07","" ,top_ret_msg,recp_date); //포인트조회실패
								} else {
									if(taoResult.size() > 0) {
										taoResult.first();
										for(int i=0; i < taoResult.size(); i++) {
											debug(i+"번째 포인트 조회");
											top_point= taoResult.getString("fml_ret23").trim();
											taoResult.next();
										}
									} 
	//		top_point= "300000"; //개발기전용테스트

									//포인트 차감처리
									if ( Integer.parseInt(top_point) >= Integer.parseInt(pay_amt))
									{

										/*********************************************
										* MJF6220I2100 - 0000:정상 
										* 탑포인트 결제 전문승인 :  for COMMIT       *
										**********************************************/	
										JoltInput entity = null;

										entity = new JoltInput();

										entity.setServiceName("BSXINPT");
										entity.setString("fml_trcode", "MJF6220I2100");
										entity.setString("fml_channel", "WEB");
										entity.setString("fml_sec50", "bcadmin");
										entity.setString("fml_sec51", hostAddress);
									
										entity.setString("fml_arg1"  , "1"							 );			// 1.주민번호, 2.카드번호, 3.사업자번호
										entity.setString("fml_arg2"  , "99000000" + jumin_no + "   ");
										entity.setString("fml_arg3"  , "1"			      		 	 );			// 1.개인, 2.기업
										entity.setString("fml_arg4"  , "42"						 );			// 42: 골프라운지 TM
										entity.setString("fml_arg5"  , pay_amt						 );
										entity.setString("fml_arg6"  , ""							 );
				debug("  entity= ["+entity+"]");

										JtTransactionProc pgProc = null;

										pgProc = (JtTransactionProc)context.getProc("MJF6220I2100");

										JoltOutput output = pgProc.execute(context,entity);

										debug(output.toString());
										ret_code   =  output.getString("fml_ret1");
										ret_msg    =  output.getString("fml_ret2");


	 // ret_code="0000"; //개발기 테스트전용
										if(ret_code.equals("0000")) {

											auth_no = output.getString("fml_ret6");                           // Toppoint승인 일련번호
											info("[골프라운지 TM 포인트 차감 성공] 주민번호 |" + jumin_no + "|승인일련번호|" + auth_no);
											
											//골프라운지 회원가입처리(BCDBA.TBGGOLFCDHD)
											String	join_chnl = "0000";
											if ("admTmMember".equals(action_key)){

												if	(  "1".equals(golf_clss) ) {join_chnl ="0003"; golf_class_nm= "골드"; }//골드
												else if (  "2".equals(golf_clss) ) {join_chnl ="0002"; golf_class_nm= "블루";} //블루
												else if (  "3".equals(golf_clss) ) {join_chnl ="0004"; golf_class_nm= "챔피온";} //챔피온
												else if (  "4".equals(golf_clss) ) {join_chnl ="0005"; golf_class_nm= "블랙";} //블랙
												
												//카젠상품 추가 2010.07.15
												if  (  "0103".equals(rcru_pl_clss) ) {join_chnl ="0103"; golf_class_nm= "골드"; }//골드

				
											} else if ("admMojib".equals(action_key)) {

												if	(  "1".equals(golf_clss) ) {join_chnl ="1003"; golf_class_nm= "골드"; }//골드
												else if (  "2".equals(golf_clss) ) {join_chnl ="1002"; golf_class_nm= "블루";} //블루
												else if (  "3".equals(golf_clss) ) {join_chnl ="1001"; golf_class_nm= "챔피온";} //챔피온
												else if (  "4".equals(golf_clss) ) {join_chnl ="1005"; golf_class_nm= "블랙";} //블랙


											}

											tb_rslt_clss ="01";
											
											//회원가입시 insert와 update 처리 2010.07.15 변경(기회원일때 : 00 필요)
											tb_rslt_clss   = proc.updateGolfcdhd(context, jumin_no, join_chnl);  //기존회원으로 가입된 경우엔 00처리함.

											info("[골프라운지 TM 유료회원 테이블 update 완료] 주민번호 |" + jumin_no +"|updateGolfcdhd|"+tb_rslt_clss );

											//주민번호,승인번호,카드번호,승인금액,승인구분코드 1:정상 9:취소,사이트구분,회원사회원번호
											//골프라운지 연회비내역(BCDBA.TBGLUGANLFEECTN)
											
											insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, "point", pay_amt, auth_clss, mb_cdhd_no);  // log insert & mail send
											//insertgolffeectn = 1 ;
											info("[골프라운지 TM 연회비 테이블 insert 완료] 주민번호 |" + jumin_no+"|insertgolffeectn|"+insertgolffeectn );
								
											updatecnt = proc.updateIsFree(context, jumin_no,tb_rslt_clss,"","",recp_date);
											info("[골프라운지 TM 내역 테이블 update 완료] 주민번호 |" + jumin_no);

											if( insertgolffeectn != 0 && updatecnt != 0){       //  DB processed successfully   

												info("[골프라운지 TM 유료회원 정상가입 완료] 주민번호 |" + jumin_no + "|hp|"+hp );
												b++;
												
												

												
												/********************************************
												* SMS 발송 
												******************************************** */
												if (hp.length() > 9)
												{
													bln_sms = true;
												} 


												
												if ("admTmMember".equals(action_key)){ //TM만 영화예매발송

													/********************************************
													* 이메일 발송 
													*********************************************/
													bln_email = true;	

												}


											}else{												              //  DB processed unsuccessfully	

												/********************************************
												* MJF6250I0100 - 0000:정상 
												* 탑포인트 결제 취소전문 승인 :  for COMMIT  *
												*********************************************/	
												info("[골프라운지 TM 포인트 차감 후 DB처리 실패 -> 포인트 취소요청] 주민번호 |" + jumin_no );
												
												JoltInput entity2 = null;

												entity2 = new JoltInput();

												entity2.setServiceName("BSXINPT");		
												
												entity2.setString("fml_channel", "WEB"			           );
												entity2.setString("fml_sec51" , hostAddress            );
												entity2.setString("fml_sec50" , "bcadmin"                    );
												entity2.setString("fml_trcode", "MJF6250I0100"               );
												entity2.setString("fml_arg1"  , jumin_no					   );			
												entity2.setString("fml_arg2"  , auth_no					   );
												entity2.setString("fml_arg3"  , "42"			      		   );			
												
												debug("  entity2= ["+entity2+"]");

												JtTransactionProc pgProc2 = null;

												pgProc2 = (JtTransactionProc)context.getProc("MJF6250I0100");

												JoltOutput output2 = pgProc2.execute(context,entity2);

												debug(output2.toString());

												ret_code2   =  output2.getString("fml_ret1");
												ret_msg2    =  output2.getString("fml_ret2");

												if(output2.getString("fml_ret1").equals("0000")) {
													info("[골프라운지 TM 포인트 취소 성공 ] 주민번호 |" + jumin_no + "|승인일련번호|" + auth_no);
													insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, "point", pay_amt, "9", mb_cdhd_no);  // log insert & mail send
													updatecnt = proc.updateIsFree(context, jumin_no, "05","","",recp_date); //포인트 취소 

												}else{
													info("[골프라운지 TM 포인트 취소 실패 ] 주민번호 |" + jumin_no + "|fml_ret1|" + ret_code2+ "|fml_ret2|" + ret_msg2);
													updatecnt = proc.updateIsFree(context, jumin_no, "06","",ret_msg2,recp_date); //포인트 취소 실패[긴급오류]
												}
											}


										}else{
											info("[골프라운지 TM 포인트 차감전문 처리 실패] 주민번호 |" + jumin_no+"|fml_ret1|"+ret_code+"|fml_ret2|"+ret_msg);
											updatecnt = proc.updateIsFree(context, jumin_no, "04","",ret_msg,recp_date); //포인트 차감 실패
										}	
										/* 포인트 차감처리 끝*/
									

									//포인트 차감 조회후 포인트 조회 부족일경우(승인처리)
									} else {
										info("[골프라운지 TM 포인트 차감 부족] 주민번호 |" + jumin_no+"|잔여포인트|"+top_point);
										updatecnt = proc.updateIsFree(context, jumin_no, "03","","|잔여포인트|"+top_point,recp_date); //잔액부족
										
										
									}

								}	

							//복합결제 혹은 카드결제일경우	
							} else if ("1".equals(auth_clss) || "2".equals(auth_clss)) {

								info("[골프라운지 TM 승인 시작] 주민번호 |" + jumin_no+"|결제구분 1:카드 2:복합결제|"+auth_clss);
								
								if ( "".equals(getvald_lim) )
								{

									//빈번호 조회  
									/********************************************
									* MHA0010R0700 - 00:정상 
									* 빈번호 조회   :  for COMMIT  *
									*********************************************/

									TaoResult binResult = null;

									JoltInput entity_bin = null;

									entity_bin = new JoltInput();

									entity_bin.setServiceName("BSNINPT");
									entity_bin.setString("fml_trcode", "MHA0010R0700"); //빈 번호조회
									entity_bin.setString("fml_channel", "WEB");
									entity_bin.setString("fml_sec50", "bcadmin");
									entity_bin.setString("fml_sec51", hostAddress);
								
									entity_bin.setString("fml_arg1", card_no.substring(0,6) );

									java.util.Properties prop_bin = new java.util.Properties();
									prop_bin.setProperty("RETURN_CODE","fml_ret1");

									JtProcess jtproc_bin = new JtProcess();
									binResult = jtproc_bin.call(context, request, entity_bin, prop_bin);
									debug(binResult.toString());
									String bin_ret_code = binResult.getString("fml_ret1").trim(); 
									String bin_mb_no = "00"; 
									if ("00".equals(bin_ret_code))
									{
										bin_mb_no = binResult.getString("fml_ret2").trim(); 
									}
									info("[골프라운지 TM 빈번호 조회] 주민번호 |" + jumin_no+"|fml_ret1|"+bin_ret_code+"|회원사번호|"+bin_mb_no);

								
									//카드정보 조회  
									/********************************************
									* MHL0260R0100 - 00,01:정상 
									* 카드정보 조회  :  for COMMIT  *
									*********************************************/
			
									TaoResult taoResult = null;

									JoltInput entity0 = null;

									entity0 = new JoltInput();
									
									// 2010년 4월 26일 수정 (농촌사랑카드일 경우 회원사번호가  빈번호에서는 13번이 넘어오나, 카드조회시엔  14번을 넘겨야 함.
									if ( "13".equals(bin_mb_no)) {
										bin_mb_no = "00";
										
									}

									entity0.setServiceName("BSNINPT");
									entity0.setString("fml_trcode", "MHL0260R0100"); //개인카드정보조회
									entity0.setString("fml_channel", "WEB");
									entity0.setString("fml_sec50", "bcadmin");
									entity0.setString("fml_sec51", hostAddress);
								
									entity0.setString("fml_arg1", "1"); //1:주민번호 2:사업자번호 3: 카드번호
									entity0.setString("fml_arg2", jumin_no);  //1.주민번호 
									entity0.setString("fml_arg3", bin_mb_no); //회원사회원번호
									entity0.setString("fml_arg4", "0"); 

									java.util.Properties prop = new java.util.Properties();
									prop.setProperty("RETURN_CODE","fml_ret1");

									JtProcess jtproc = new JtProcess();
									taoResult = jtproc.call(context, request, entity0, prop);
									debug(taoResult.toString());

									// 값 가져올때는...
									String auth_ret_code = taoResult.getString("fml_ret1").trim(); 
									String auth_ret_msg = taoResult.getString("fml_ret2").trim();  

									info("[골프라운지 TM 카드번호 조회] 주민번호 |" + jumin_no+"|카드조회전문 응답|"+auth_ret_code);
									
									String jt_card_no = null;
									String jt_card_no_fmt = null;
									String str_ret2 = null; //연속조회값
									vald_lim ="";
									int icardcnt = 0;
										
									if ("00".equals(auth_ret_code) || "01".equals(auth_ret_code))
									{
										

										if(taoResult.size() > 0) {
											taoResult.first();
											for(int i=0; i < taoResult.size(); i++) {
												jt_card_no = taoResult.getString("fml_ret3").trim();
												str_ret2=taoResult.getString("fml_ret2").trim();
												icardcnt++;
												debug("1번째 호출 전문 "+icardcnt+"번째카드번호 ="+ jt_card_no);
												int istrfind = card_no.indexOf("****");
												if (istrfind != -1){
													jt_card_no_fmt = jt_card_no.substring(0,8)+"****"+jt_card_no.substring(12,16);	
												} else {
													jt_card_no_fmt = jt_card_no;
												}
												if (card_no.equals(jt_card_no_fmt))
												{
													card_no  = jt_card_no;
													vald_lim = taoResult.getString("fml_ret6").trim(); //유효기간
													auth= true;
												}
												taoResult.next();
											}
										}
										//다음카드가 있을경우
										if ( "01".equals(auth_ret_code) )
										{
											int ijoltcallcnt = 1;
											while(	Integer.parseInt(auth_ret_code) > 0 ) {
												ijoltcallcnt++;

												info("[골프라운지 TM 카드번호 조회 "+ijoltcallcnt+"] 주민번호 |" + jumin_no+"|카드조회전문 응답|"+auth_ret_code);
												
												entity0.setString("fml_arg4", str_ret2 );
												debug("▣▣▣ fml_arg4 일때 ▣▣▣" + str_ret2);

												taoResult = jtproc.call(context, request, entity0, prop);
												debug(taoResult.toString());

												auth_ret_code = taoResult.getString("fml_ret1");
												
												//이짓을 하는 이유는 카드가 딱10개인경우 리턴값이 01로 넘어오고,
												//2번째 호출시 fml_ret1,fml_ret2 값 00 으로 넘어와서 fml_ret3 값은 null 에러로 Exception 발생
												String taoRetCode = "";
												try{
													String mTest = taoResult.getString("fml_ret3"); //
													taoRetCode ="OK";   
												} catch (TaoException ex) {
													taoRetCode ="ERROR"; //카드조회 하지말것
												}
												//이짓을 하는 이유 끝

												if ("OK".equals(taoRetCode))
												{
													taoResult.first();
													for(int i=0; i < taoResult.size(); i++) {
														jt_card_no = taoResult.getString("fml_ret3").trim();
														str_ret2=taoResult.getString("fml_ret2").trim();
														icardcnt++;
														debug(ijoltcallcnt+"번째 호출 전문"+icardcnt+"번째카드번호 ="+ jt_card_no);

														int istrfind = card_no.indexOf("****");
														if (istrfind != -1){
															jt_card_no_fmt = jt_card_no.substring(0,8)+"****"+jt_card_no.substring(12,16);	
														} else {
															jt_card_no_fmt = jt_card_no;
														}

														if (card_no.equals(jt_card_no_fmt))
														{
															card_no  = jt_card_no;
															vald_lim = taoResult.getString("fml_ret6").trim(); //유효기간
															auth= true;
														}
														taoResult.next();
													}

												}

											}

										}
										//카드번호를 못 찾았거나, 없을경우 
										if ("".equals(vald_lim))
										{
											info("[골프라운지 TM 카드조회 실패 해당카드없음] 주민번호 |" + jumin_no );
											updatecnt = proc.updateIsFree(context, jumin_no, "11","","카드조회 실패 해당카드없음",recp_date); //카드조회 실패
										}
									
									} else {

										info("[골프라운지 TM 카드조회 실패] 주민번호 |" + jumin_no );
										updatecnt = proc.updateIsFree(context, jumin_no, "11","","카드조회 실패 해당카드없음",recp_date); //카드조회 실패
									}


								//타인카드일경우
								} else {
									vald_lim = getvald_lim;
									auth= true;
								}
								info("[골프라운지 TM 카드번호 조회] 주민번호 |" + jumin_no+"|카드번호|"+card_no+"|유효기간|"+vald_lim);

								////////////////////////////////////////////////////////승인/////////////////////////////

								if ( auth == true )
								{

									debug(vald_lim);
									/********************************************
									* MJF6010R0100 - 01(fml_ret5):정상 
									* 승인 전문 :  for COMMIT  *
									*********************************************/
									GolfPayAuthEtt payEtt = new GolfPayAuthEtt();

									String ins_term ="00"; 
									String merno = "765943401"; //가맹점번호 (연회비)

									if ("admTmMember".equals(action_key)){
										if (tm_buz.equals("03")){
											merno = "767445661"; //H&C네트워크
										} else if (tm_buz.equals("11"))	{
											merno = "767445687"; //트랜스코스모스 코리아
										} else if (tm_buz.equals("12"))	{
											merno = "767445687"; //리딩아이
										} else if (tm_buz.equals("13"))	{
											merno = "767445661"; //윌앤비전
										}
									}
									
									if ( "1".equals(auth_clss) ) ins_term ="00";
									else if	( "2".equals(auth_clss) ) ins_term ="60";

									payEtt.setMerMgmtNo(merno); //가맹점번호
									payEtt.setCardNo(card_no); 
									payEtt.setValid(vald_lim.substring(2,6));			
									payEtt.setAmount(pay_amt);
									payEtt.setInsTerm(ins_term); //할부개월수
									payEtt.setRemoteAddr(hostAddress);

									GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
									boolean payResult = false;

									payResult = payProc.executePayAuth(context, request, payEtt);
									info("[골프라운지 TM 승인] 주민번호 |" + jumin_no+"|결과|"+payResult);

									//성공
									if (payResult) {
										
										auth_no = payEtt.getUseNo();                           // 카드승인 일련번호
										
										info("[골프라운지 TM 카드승인 성공] 주민번호 |" + jumin_no + "|승인일련번호|" + auth_no);
										
										//골프라운지 회원가입처리(BCDBA.TBGGOLFCDHD)
										String	join_chnl = "0000";
										if ("admTmMember".equals(action_key)){

											if	(  "1".equals(golf_clss) ) {join_chnl ="0003"; golf_class_nm= "골드"; }//골드
											else if (  "2".equals(golf_clss) ) {join_chnl ="0002"; golf_class_nm= "블루";} //블루											
											else if (  "3".equals(golf_clss) ) {join_chnl ="0004"; golf_class_nm= "챔피온";} //챔피온
											else if (  "4".equals(golf_clss) ) {join_chnl ="0005"; golf_class_nm= "블랙";} //블랙
											//카젠상품 추가 2010.07.15
											if  (  "0103".equals(rcru_pl_clss) ) {join_chnl ="0103"; golf_class_nm= "골드"; }//골드

			
										} else if ("admMojib".equals(action_key)) {

											if	(  "1".equals(golf_clss) ) {join_chnl ="1003"; golf_class_nm= "골드"; }//골드
											else if (  "2".equals(golf_clss) ) {join_chnl ="1002"; golf_class_nm= "블루";} //블루
											else if (  "3".equals(golf_clss) ) {join_chnl ="1001"; golf_class_nm= "챔피온";} //챔피온
											else if (  "4".equals(golf_clss) ) {join_chnl ="1005"; golf_class_nm= "블랙";} //블랙


										}
										
										tb_rslt_clss ="01";
										
										//회원가입시 insert와 update 처리 2010.07.15 변경(기회원일때 : 00 필요)
										tb_rslt_clss   = proc.updateGolfcdhd(context, jumin_no, join_chnl);  //기존회원으로 가입된 경우엔 00처리함.

										info("[골프라운지 TM 유료회원 테이블 update 완료] 주민번호 |" + jumin_no +"|updateGolfcdhd|"+tb_rslt_clss );
										
										
										
										
										insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, card_no, pay_amt, auth_clss, mb_cdhd_no);  // log insert & mail send
										//insertgolffeectn = 1 ;
										info("[골프라운지 TM 연회비 테이블 insert 완료] 주민번호 |" + jumin_no+"|insertgolffeectn|"+insertgolffeectn );
							
										updatecnt = proc.updateIsFree(context, jumin_no,tb_rslt_clss,vald_lim,"",recp_date);
										info("[골프라운지 TM 내역 테이블 update 완료] 주민번호 |" + jumin_no);

										if( insertgolffeectn != 0 && updatecnt != 0){       //  DB processed successfully   

											info("[골프라운지 TM 유료회원 정상가입 완료] 주민번호 |" + jumin_no + "|hp|" + hp);
											b++;
											/********************************************
											* SMS 발송 
											******************************************** */

											if (hp.length() > 9)
											{
												bln_sms = true;
											} 

											
											if ("admTmMember".equals(action_key)){ // TM만 영화예매 메일 발송

												bln_email=true;

											}

										}else {
											//카드승인취소 전문
											
											/********************************************
											* MGA0030I0800 - 2(fml_ret2):정상 
											* 승인 전문 취소:  for COMMIT  *
											*********************************************/
 
											boolean payCancelResult = false;

											payCancelResult = payProc.executePayAuthCancel(context, payEtt);

											String cancel_ret_code = payEtt.getResCode(); 
											String cancel_ret_msg = payEtt.getResMsg();
											info("[골프라운지 TM 승인취소] 주민번호 |" + jumin_no+"|결과|"+cancel_ret_code+"|사유|"+cancel_ret_msg);
											if(cancel_ret_code.equals("2")) {
												info("[골프라운지 TM 카드 취소 성공 ] 주민번호 |" + jumin_no + "|승인일련번호|" + auth_no);
												insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, card_no, pay_amt, "9", mb_cdhd_no);  // log insert & mail send
												updatecnt = proc.updateIsFree(context, jumin_no, "15","","",recp_date); //카드 취소 

											}else{
												info("[골프라운지 TM 카드 취소 실패 ] 주민번호 |" + jumin_no + "|결과|" + cancel_ret_code+ "|사유|" + cancel_ret_msg);
												updatecnt = proc.updateIsFree(context, jumin_no, "16","",cancel_ret_msg,recp_date); //카드 취소 실패[긴급오류]
											}

 
										} 


									} else {
										//승인실패시
										info("[골프라운지 TM 승인전문 처리 실패] 주민번호 |" + jumin_no+"|fml_ret5|"+payEtt.getResCode()+"|fml_ret6|"+payEtt.getResMsg());
										updatecnt = proc.updateIsFree(context, jumin_no, "12",vald_lim,payEtt.getResMsg(),recp_date); //승인전문 처리 실패

									}
								}



							}/*if ("3".equals(auth_clss)) */

						// } /*8이 아닐때 2010.07.15 제외 */ 

	
					}/* if memkind 처리 */ 
					
					//sms 발송처리
					if (bln_sms==true){
						// SMS 관련 셋팅
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", hg_nm);
						smsMap.put("sPhone1", hp_ddd_no);
						smsMap.put("sPhone2", hp_tel_hno);
						smsMap.put("sPhone3", hp_tel_sno);
						smsMap.put("sCallCenter", "15666578");
						
						String smsClss = "674";

						String message = "[Golf Loun.G]"+hg_nm+"님 골프라운지(www.golfloung.com)회원가입진행해주시기바랍니다" ;
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = "";
						
						//SMS발송
						if (devip.equals(hostAddress)) {  //개발기
							//smsRtn = smsProc.send(smsClss, smsMap, message);
							info("[골프라운지 TM SMS 개발기는 발송안됩니다. ] 핸드폰번호 |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|메세지|" + message);
						} else { //운영기
							smsRtn = smsProc.send(smsClss, smsMap, message);
							info("[골프라운지 TM SMS 발송] 핸드폰번호 |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|메세지|" + message);
						}	
						bln_sms=false;
					}
					 
					//email 발송처리
					if (bln_email==true){
						
						try{
							String[] email = BcUtil.getEmailArray(email_addr);
							int email_cnt = email.length;
	
							if ( !(email_addr=="" || "".equals(email_addr))  && email_cnt == 2  )
							{
								//if ("0103".equals(rcru_pl_clss)){
								//	mail_clss="oil"; //카젠상품은 SK주유권 발송
								//} else {
									mail_clss="movie"; //그외는 영화예매 발송
								//}
								if (devip.equals(hostAddress)) {  //개발기
									//proc.sendMail(email_addr,hg_nm,golf_class_nm,mail_clss);
									info("[골프라운지 TM 메일발송 개발기는 발송안됩니다.] 주민번호 |" + jumin_no + "|email_addr|" + email_addr);
								}else {
									try{
										proc.sendMail(email_addr,hg_nm,golf_class_nm,mail_clss);
										info("[골프라운지 TM 메일발송 완료] 주민번호 |" + jumin_no + "|email_addr|" + email_addr);
									}catch (Exception ex) {
										info("[골프라운지 TM 메일발송 실패] 주민번호 |" + jumin_no + "|email_addr|" + email_addr);
									}
									
									
								}
							}
						} catch(Throwable t) {}
						bln_email=false;
					}
					
								
					a++;
					
				} /*while문*/ 

				info("[골프라운지 TM 회원 GolfLoung 유료가입처리 END : 총" + a + "건 처리 중 " + b + "건 정상가입 종료시간:"  + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );										

				/********************************************
				* 골프라운지 담당자에게 TM 결과 통보
				********************************************/
				if ("admTmMember".equals(action_key)){
					proc.sendMailAdmin(context,a,b); 
				} else if ("admMojib".equals(action_key)) {
					 //모집인 등록대상 조회 : 	
				}
				

        } catch (Throwable ex) {

			warn("GolfLoungTMFeeMemberJoinActn Throwable | jumin_no:" + jumin_no + " | auth_no:" + auth_no + " ... 처리중ERR ", ex);

            rx = new ResultException(ex);			
			rx.setTitleImage("error");			
			rx.addButton(goPage, addButton);
			rx.setKey("SYSTEM_ERR");

			throw rx;
        }
	
//메일만 테스트	
//	try {  	
//		
//		GolfLoungTMProc proc = new GolfLoungTMProc();
//		proc.sendMailAdmin(context,10,10); 
//
//      } catch (Throwable ex) {
//
//		warn("GolfLoungTMFeeMemberJoinActn Throwable | jumin_no:" + jumin_no + " | auth_no:" + auth_no + " ... 처리중ERR ", ex);
//
//        rx = new ResultException(ex);			
//		rx.setTitleImage("error");			
//		rx.addButton(goPage, addButton);
//		rx.setKey("SYSTEM_ERR");
//
//		throw rx;
//    }
        
		return super.getActionResponse(context, responseKey);
		
	}

}
