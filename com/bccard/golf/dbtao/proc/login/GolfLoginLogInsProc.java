/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoginLogInsProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 로그인 로그 저장
*   적용범위  : Golf
*   작성일자  : 2009-06-11
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*  2009.8.25          권영만   골프카드 추가작업
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemMonthInsDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  golf
 * @author	Media4th 
 * @version 1.0  
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		2010-02-25	임은혜	골프투어멤버스->블랙회원 수정	
* golfloung		2010-02-26	임은혜	NH 회원 가입채널 3000으로 들어오는것 수정	
* golfloung		2011-01-20 	이경희 	NH패밀리 추가
 **************************************************************************** */
public class GolfLoginLogInsProc extends DbTaoProc {
	
	/** 
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */ 
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		ResultSet rs2 				= null;
		ResultSet rs3 				= null;
		ResultSet rs4 				= null;
		ResultSet rs_grd			= null;		// 등급가져오기 
		ResultSet rs_end_date		= null;		// 유료회원 종료일 
		ResultSet rs_membership		= null;		// 멤버십 등급 가져오기
		ResultSet rs_memInfo		= null;		// 회원정보
		String title				= dataSet.getString("title");
		String actnKey 				= null;
		DbTaoResult result			= new DbTaoResult(title);
		String reResult				= "";
		String strMESSAGE_KEY 		= "GolfTao_Common_reg";
		String benefit_no			= ""; 		// 혜택관리 일련번호
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;
		int idx						= 0;
		String cdhd_ctgo_seq_no		= "";		// 골프회원 테이블의 회원분류일련번호 : 대표등급번호
		String acrg_cdhd_end_date	= "";		// 유료회원 혜택 종료일
		int isIbkGold				= 0; 		// 기업골드 회원여부
		int isSmart					= 0; 		// 스마트 회원여부
		int isMembershiop			= 0;		// 멤버십 회원여부		
		
		int intMemGrade = 0;		//공통등급
		int intMemberGrade = 0;		//멤버쉽등급처리
		int intCardGrade = 0;		//카드등급처리
		String memGrade = "";		//등급명 
		//WaContext context = null;
		//int result_upd = 0;			// 기업은행 골드 회원 기간연장 결과
		String end_date = "";		// 유료기간 종료일
						
		try {
			
			System.out.print("## GolfLoginLogInsProc | sso id를 통해 골프회원 조회 시작  \n");
			
			actnKey 						= dataSet.getString("actnKey");
			UcusrinfoEntity userEtt 		= (UcusrinfoEntity)dataSet.getObject("userEtt");
									
			String sece_aton = "";				// 탈퇴일자
			String charge_mem = "";				// 유료회원 여부 => Y: 유료회원
			String join_re = "";				// 재가입 가능 여부 => N;재가입 불가
			String jumin_no = "";				// 주민등록번호
			String member_clss = "1";			// 비씨카드 회원 테이블 멤버클래스 5-> 법인회원, 1-> 일반회원
			boolean goLogin = false;			// 로그인 처리 여부

			String vtl_jumin_no = "";			// (BC)가상주민등록번호
			//String socid = "";					// (BC)주민등록번호

			String golfCardYn		= StrUtil.isNull(dataSet.getString("golfCardYn"),"N");			//골프카드유무
			String strCardJoinNo	= StrUtil.isNull(dataSet.getString("strCardJoinNo"),"");		//제휴카드코드	
			String golfCardNhYn		= StrUtil.isNull(dataSet.getString("golfCardNhYn"),"N");		//농협골프카드유무
			String strCardNhType	= StrUtil.isNull(dataSet.getString("strCardNhType"),"");		//농협카드종류		
			String tourBlackYn		= StrUtil.isNull(dataSet.getString("tourBlackYn"),"");			//투어 블랙회원 여부	
			String tourJuminNo		= StrUtil.isNull(dataSet.getString("tourJuminNo"),"");			//투어 블랙회원 주민번호
			String vipCardYn		= StrUtil.isNull(dataSet.getString("vipCardYn"),"N");			//VIP카드 유무
			String topGolfCardYn	= StrUtil.isNull(dataSet.getString("topGolfCardYn"),"N");		//탑골프카드 유무
			//String topGolfCardNo	= StrUtil.isNull(dataSet.getString("topGolfCardNo"),"");
			String golfCardCoYn		= StrUtil.isNull(dataSet.getString("golfCardCoYn"),"N");		//탑골프법인회원 유무			
			String richCardYn		= StrUtil.isNull(dataSet.getString("richCardYn"),"N");			//리치카드 유무
			String jbCardYn		= StrUtil.isNull(dataSet.getString("jbCardYn"),"N");			//전북시그니쳐카드 유무
			
			debug("## GolfLoginLogInsProc | ID : "+userEtt.getAccount()+" | 등급 : "+userEtt.getIntMemGrade()+" | golfCardYn : " + golfCardYn + "  | strCardJoinNo : " + strCardJoinNo + " | golfCardNhYn : " + golfCardNhYn + " | strCardNhType : " + strCardNhType + " | tourBlackYn : " + tourBlackYn + " | tourJuminNo : " + tourJuminNo + " | vipCardYn : "+vipCardYn+" | richCardYn : "+richCardYn+" | topGolfCardYn : "+topGolfCardYn+"| jbCardYn :"+jbCardYn +"\n");
			
			//월회원 프로세스
			GolfMemMonthInsDaoProc monthProc = new GolfMemMonthInsDaoProc();
			

			/**개인/법인 회원 구분****개인회원과 법인회원(6)만 로그인 가능하게 **********************************************************/			
			//골프회원(법인) TBENTPUSER 테이블  체크 
			// member_clss 조건 검색 1:정회원 / 4:비회원 / 5:법인회원
			// 법인회원은 BCDBA.TBENTPUSER , BCDBA.UCUSRINFO 존재  / 개인회원은 BCDBA.UCUSRINFO 존재한다.
			// 해당아이디로 골프회원 테이블을 검색한다.	 
			pstmt = con.prepareStatement(getMemCoYnQuery());
			pstmt.setString(1, userEtt.getAccount());	
			rs3 = pstmt.executeQuery();
			String memCoChk2 = "";
			if(rs3 != null) {			 
				
				while(rs3.next())  {	
					
					if("6".equals(rs3.getString("MEM_CLSS")))
					{
						memCoChk2 = rs3.getString("MEM_CLSS");
					}
					else
					{
						if(!"6".equals(memCoChk2)) memCoChk2 = rs3.getString("MEM_CLSS");
					}														
											
				}
			}
			
			if(!"".equals(memCoChk2)) //로그인 가능한 법인회원
			{
				if("6".equals(memCoChk2))	// 지정카드 담당자인 경우
				{
					System.out.print("## GolfLoginLogInsProc  | setStrMemChkNum(5) 굽기 | ID : "+userEtt.getAccount()+" | memCoChk2 : "+memCoChk2+" \n");	
					member_clss = "5";
					userEtt.setStrMemChkNum("5");		//개인/법인 (  1,4:개인 / 5:법인 ) 
				}
				else{ // 지정카드담당자 외인 경우 
					
					// 탑골프카드 소지자 다시 한번 법인카드원장 확인
					if("Y".equals(topGolfCardYn))
					{											
						
						
						if("Y".equals(golfCardCoYn))
						{
							System.out.print("## GolfLoginLogInsProc | 탑골프카드 소지 법인원장 존재 | ID : "+userEtt.getAccount()+" | memCoChk2 : "+memCoChk2+" \n");	
							member_clss = "5";
							userEtt.setStrMemChkNum("5");		//개인/법인 (  1,4:개인 / 5:법인 ) 
						}																		
						else
						{
							member_clss = "7";
						}
					}
					else
					{
						member_clss = "7";  //불가능 법인
					}
					
				}
			}				
			else
			{
				
				//회원 TBENTPUSER  테이블에 없을경우 회원테이블 UCUSRINFO 조회 일반회원이라면
				pstmt = con.prepareStatement(getCountMemOnlyClssQuery());
				pstmt.setString(1, userEtt.getAccount());	
				rs4 = pstmt.executeQuery();
				if(rs4.next())  //로그인 가능한 개인회원
				{	
					member_clss = rs4.getString("MEMBER_CLSS");
				}
			
			}
			System.out.print("## GolfLoginLogInsProc  | member_clss 체크 | ID : "+userEtt.getAccount()+" | member_clss : "+member_clss+" \n");
			/////////////////////////////////////////////////////////////
			// 개인인지 법인인지 구분값 ett저장
			userEtt.setStrMemChkNum(member_clss);		//개인/법인 (  1,4:개인 / 5:법인 ) 
			/////////////////////////////////////////////////////////////
			
			
			
			 
			/**회원기본정보 가져오기**************************************************************/
			String memEmail				= "";	// 이메일
			String memZipCode			= "";	// 우편번호
			String memZipAddr			= "";	// 주소
			String memDetailAddr		= "";	// 상세주소
			String memMobile			= "";	// 핸드폰번호
			String memPhone				= "";	// 전화번호
			
			sql = this.getUserInfoQuery(member_clss);  	// 회원등급번호 1:개인 / 5:법인
            userInfoPstmt = con.prepareStatement(sql);
            userInfoPstmt.setString(1, userEtt.getAccount() );
            userInfoRs = userInfoPstmt.executeQuery();	
			if(userInfoRs.next()){
				memEmail			= userInfoRs.getString("EMAIL");	// 이메일
				memZipCode			= userInfoRs.getString("ZIPCODE");	// 우편번호
				memZipAddr			= userInfoRs.getString("ZIPADDR");	// 주소
				memDetailAddr		= userInfoRs.getString("DETAILADDR");	// 상세주소
				memMobile			= userInfoRs.getString("MOBILE");	// 핸드폰번호
				memPhone			= userInfoRs.getString("PHONE");	// 전화번호
			}
			if(userInfoRs != null) userInfoRs.close();
            if(userInfoPstmt != null) userInfoPstmt.close();
            

//			if("simijoa81".equals(userEtt.getAccount())){
//				tourBlackYn = "Y";
//				tourJuminNo = "8108212622793";
//				golfCardYn = "Y";			//9, 10 - 나의 알파
//				strCardJoinNo = "030698";	//10 
//				strCardJoinNo = "394033";	//19 - 경남은행
//				strCardJoinNo = "740276";	//20 - IBK APT 프리미엄카드
//				golfCardNhYn = "Y";			//12, 13 - NH  
//				strCardNhType = "03";		//12 - 티타늄
//				strCardNhType = "12";		//13 - 플래티늄
//			} 
/*			
//			if("khlplus01".equals(userEtt.getAccount())){
//				tourBlackYn = "Y";
//				tourJuminNo = "8108212622793";
				golfCardYn = "Y";			//9, 10 - 나의 알파
				strCardJoinNo = "036948";	//10 
//				strCardJoinNo = "394033";	//19 - 경남은행
//				strCardJoinNo = "740276";	//20 - IBK APT 프리미엄카드
//				golfCardNhYn = "Y";			//12, 13 - NH  
//				strCardNhType = "03";		//12 - 티타늄
//				strCardNhType = "12";		//13 - 플래티늄
				
//				debug("#---------------khlplus01");
//			} 			
*/			

			/*
			//테스트시
			if("ymkwun".equals(userEtt.getAccount())){
				golfCardYn = "Y";
				strCardJoinNo = "740276";	//20 - IBK APT 프리미엄카드
			} 
			*/
			
            //CtrlServ 에서 구운 세션이 있을때에만 
			if(userEtt != null)
			{	
							
				/**골프투어 멤버스->블랙수정 처리**************************************************************/
				String tour_grade_chage = "N";		// 골프투어멤버스 등급으로 변경여부
				String tour_acrg_upd = "N";			// 유료회원기간 변경 여부
				if("Y".equals(tourBlackYn)){		// 투어 블랙 회원일경우 자동으로 로그인 되도록한다.	
					System.out.print("## GolfLoginLogInsProc | 골프투어멤버스->블랙수정 처리 \n");	
					pstmt = con.prepareStatement(getChkTourQuery());
					pstmt.setString(1, userEtt.getAccount());
					rs= pstmt.executeQuery();
					
					if(rs.next()){
						if("Y".equals(rs.getString("SECE_YN"))){	
							pstmt = con.prepareStatement(getReInsertMemQuery());
							pstmt.setString(1, userEtt.getAccount());
							pstmt.executeQuery();
							
							tour_grade_chage = "Y";
							System.out.print("## GolfLoginLogInsProc | 골프투어멤버스->블랙수정 : 탈퇴회원 재가입처리  \n");	
						}

						if("29".equals(rs.getString("CDHD_CTGO_SEQ_NO"))){
							tour_acrg_upd = "Y";
							System.out.print("## GolfLoginLogInsProc | 골프투어멤버스->전북은행시그티처 : 유료기간  변경\n");
                                                }
						
						if("8".equals(rs.getString("CDHD_CTGO_SEQ_NO")) || GolfUtil.empty(rs.getString("CDHD_CTGO_SEQ_NO"))){
							tour_acrg_upd = "Y";
							tour_grade_chage = "Y";
							System.out.print("## GolfLoginLogInsProc | 골프투어멤버스->블랙수정 : 화이트회원 등급 업그레이드 \n");
						}else{
							if("N".equals(rs.getString("ACRG_ABLE"))){
								tour_acrg_upd = "Y";
								tour_grade_chage = "Y";
								System.out.print("## GolfLoginLogInsProc | 골프투어멤버스->블랙수정 : 유료기간 지난 회원 등급 변경  \n");
							}
						}
						
						System.out.print("## GolfLoginLogInsProc | 골프투어멤버스->블랙수정 | tour_acrg_upd : "+tour_acrg_upd+" | tour_grade_chage : "+tour_grade_chage+"  \n");
						
						if("Y".equals(tour_acrg_upd)){
							pstmt = con.prepareStatement(getAcrgUpdQuery());
							pstmt.setString(1, userEtt.getAccount());
							pstmt.executeQuery();
							System.out.print("## GolfLoginLogInsProc | 유료기간 업데이트  \n");
						}
						
						if("Y".equals(tour_grade_chage)){
							//골프회원등급관리 테이블 기존 데이터 삭제
							pstmt = con.prepareStatement(getMemGradeDelQuery());
							pstmt.setString(1, userEtt.getAccount());
							pstmt.executeQuery();

							//골프회원등급관리인서트
							sql = this.getNextValQuery(); 
				            pstmt = con.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long max_seq_no = 0L;
							if(rs.next()){
								max_seq_no = rs.getLong("SEQ_NO");
							}
							if(rs != null) rs.close();
				            if(pstmt != null) pstmt.close();
				            
				            sql = this.getInsertGradeQuery();
							pstmt = con.prepareStatement(sql);
							
				        	pstmt.setLong(1, max_seq_no ); 
				        	pstmt.setString(2, userEtt.getAccount() ); 
				        	pstmt.setString(3, "11" );
				        	
							pstmt.executeUpdate();
							System.out.print("## GolfLoginLogInsProc | 골프투어멤버스로 등급변경->블랙수정  \n");
							
							//골프회원테이블 대표등급, 가입경로(3000) 업데이트 getUpdGradeQuery
				            sql = this.getUpdGradeTourQuery();
							pstmt = con.prepareStatement(sql);
							
				        	pstmt.setString(1, "11" );
				        	pstmt.setString(2, userEtt.getAccount() ); 
				        	
							pstmt.executeUpdate();
						}
						
					}else{
			            
						//골프회원테이블 인서트
						pstmt = con.prepareStatement(getInsertMemQuery());
						idx = 0;
						pstmt.setString(++idx, userEtt.getAccount());
			        	pstmt.setString(++idx, userEtt.getName() );
			        	pstmt.setString(++idx, tourJuminNo );
						pstmt.setString(++idx, "11" );
						pstmt.setString(++idx, memMobile );
						pstmt.setString(++idx, memPhone );
						pstmt.setString(++idx, memEmail );
						pstmt.setString(++idx, memZipCode );
						pstmt.setString(++idx, memZipAddr );
						pstmt.setString(++idx, memDetailAddr );
						pstmt.executeQuery();

						//골프회원등급관리인서트
						sql = this.getNextValQuery(); 
			            pstmt = con.prepareStatement(sql);
			            rs = pstmt.executeQuery();			
						long max_seq_no = 0L;
						if(rs.next()){
							max_seq_no = rs.getLong("SEQ_NO");
						}
						if(rs != null) rs.close();
			            if(pstmt != null) pstmt.close();
			            
			            sql = this.getInsertGradeQuery();
						pstmt = con.prepareStatement(sql);
						
			        	pstmt.setLong(1, max_seq_no ); 
			        	pstmt.setString(2, userEtt.getAccount() ); 
			        	pstmt.setString(3, "11" );
			        	
						pstmt.executeUpdate();
						
						System.out.print("## GolfLoginLogInsProc | 골프투어멤버스로 가입처리->블랙수정  \n");
					}
					
				}	// 투어 블랙 회원일경우 자동으로 로그인 되도록한다.
				else	// 투어회원이 아니지만 3000 블랙회원이면 유료회원기간을 어제로 변경한다.
				{
					pstmt = con.prepareStatement(getFreeBlackQuery());
					pstmt.setString(1, userEtt.getAccount());
					rs= pstmt.executeQuery();
					
					if(rs.next()){

			            sql = this.getUpdEndDateQuery();
						pstmt = con.prepareStatement(sql);
			        	pstmt.setString(1, userEtt.getAccount() );
			        	
						pstmt.executeUpdate();
						System.out.print("## GolfLoginLogInsProc | 무료블랙 -> 해지 -> 유료회원종료일자 수정  \n");
					}

					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
				}
				

				System.out.print("## GolfLoginLogInsProc | 기업은행카드 확인 시작 (경남)================================ \n");
				
				debug("## 1:"+AppConfig.getDataCodeProp("Basic")
						+ ", "+ AppConfig.getDataCodeProp("Skypass") 
						+ ", "+ AppConfig.getDataCodeProp("AsianaClub")
						+ ", "+ AppConfig.getDataCodeProp("0052CODE9")
						+ ", strCardJoinNo : "+ strCardJoinNo
						+ ", vipCardYn: "+ vipCardYn);		

				
				if("Y".equals(golfCardYn)){	// 혜택번호를 보내준다.
					
					if("030698".equals(strCardJoinNo) || "031189".equals(strCardJoinNo) || "031176".equals(strCardJoinNo) ){
		        		benefit_no = "10";
					}else if("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo) ){		// IBK APT 프리미엄카드 
		        		benefit_no = "20";
					}else if (strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))){  
						benefit_no = AppConfig.getDataCodeProp("0052CODE9");
					}else if("394033".equals(strCardJoinNo)){	// 경남은행 Family
		        		benefit_no = "19";
		        	}else{
		        		benefit_no = "9";
		        	}
				}
				execute_card(con, userEtt.getAccount(), golfCardYn, benefit_no, "ibk");

				
				System.out.print("## GolfLoginLogInsProc | 농협채움카드 확인 시작 ================================ \n");
				if("Y".equals(golfCardNhYn)){	
					
					// 카드종류 : 03:티타늄 , 12:플래티늄, 48:패밀리카드
					if(strCardNhType.equals("03")){	
						benefit_no = "12";
					}else if(strCardNhType.equals("12")) {
						benefit_no = "13";
					}else if(strCardNhType.equals("48")) {
						benefit_no = "14";
					}		
				}
				execute_card(con, userEtt.getAccount(), golfCardNhYn, benefit_no, "nh");
				
				/*
				System.out.print("## GolfLoginLogInsProc | VIP카드 확인 시작 ================================ \n");
				if("Y".equals(vipCardYn)){	
					benefit_no = "7";	
				}
				execute_card(con, userEtt.getAccount(), vipCardYn, benefit_no, "vip");
				*/
				
				System.out.print("## GolfLoginLogInsProc | 탑골프카드 확인 시작 ================================ \n");
				if("Y".equals(topGolfCardYn)){	
					benefit_no = "21";	
				}
				execute_card(con, userEtt.getAccount(), topGolfCardYn, benefit_no, "topGolf");
				
				System.out.print("## GolfLoginLogInsProc | 리치카드 확인 시작 ================================ \n");
				if("Y".equals(richCardYn)){	
					benefit_no = "22";	
				}
				execute_card(con, userEtt.getAccount(), richCardYn, benefit_no, "rich");
				
				System.out.print("## GolfLoginLogInsProc | 전북시그니처카드 확인 시작 ================================ \n");
				if("Y".equals(jbCardYn)){	
					benefit_no = "29";	
				}
				execute_card(con, userEtt.getAccount(), jbCardYn, benefit_no, "jb");
				
				
				//오퍼 월회원 등급은 유무 체크
				boolean offerResult = monthProc.execute_newJoinMemYN(con, userEtt.getSocid());				

				System.out.print("## GolfLoginLogInsProc | 리턴페이지 지정 ================================ \n");
				//sso id 로 순수 골프회원 테이블(TBGGOLFCDHD) 존재 유무 체크
				pstmt = con.prepareStatement(getCountCkQuery());
				pstmt.setString(1, userEtt.getAccount());	
				rs = pstmt.executeQuery();
								
				if ( rs.next() ){	//골프회원테이블에 존재
					
					
					sece_aton 			= StrUtil.isNull(rs.getString("SECE_ATON"), "");	// 탈퇴일자
					charge_mem 			= StrUtil.isNull(rs.getString("CHARGE_MEM"), "");	// 유료회원 여부
					join_re 			= StrUtil.isNull(rs.getString("JOIN_RE"), "");		// 재가입 가능기간 
					jumin_no 			= StrUtil.isNull(rs.getString("JUMIN_NO"), "");		// 주민등록번호
					vtl_jumin_no		= StrUtil.isNull(rs.getString("VRTL_JUMIN_NO"), "");// (BC)가상주민등록번호
					if(GolfUtil.empty(vtl_jumin_no)) vtl_jumin_no="";
					//socid	 		= rs.getString("SOCID");		// (BC)주민등록번호
					cdhd_ctgo_seq_no	= StrUtil.isNull(rs.getString("CDHD_CTGO_SEQ_NO"), "");			// 골프회원테이블의 대표 등급번호
					acrg_cdhd_end_date 	= StrUtil.isNull(rs.getString("ACRG_CDHD_END_DATE"), "");	// 유료회원 혜택 종료일					
					
					
					
					System.out.print("## GolfLoginLogInsProc | 회원 | TBGGOLFCDHD 존재 골프회원일 경우 처리시작 | ID : "+userEtt.getAccount()+" | jumin_no : "+jumin_no+" | sece_aton : "+sece_aton+" | charge_mem : "+charge_mem+" | join_re : "+join_re+" \n");
										
					if(!GolfUtil.empty(sece_aton)){	// 탈퇴일자가 있는경우
			
					//가입하지 않은 TM 내역이 있을 경우 TM가입 페이지로 돌린다. -> 결제 후 1년이 지나면 스킵
					pstmt = con.prepareStatement(getMemTmQuery());
					pstmt.setString(1, userEtt.getSocid());	
					pstmt.setString(2, userEtt.getSocid());
					rs2 = pstmt.executeQuery();
										
					if(rs2.next()){						
						//1.티엠등록 -> 이미 결제한 상태이므로 동의만 있는 페이지로 돌린다.
						reResult = "01";
						System.out.print("## GolfLoginLogInsProc | 로그인회원 | TM회원 | TM 가입으로 보내기 (업데이트)1 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
					}else {							
						
						if(charge_mem.equals("Y") && join_re.equals("N"))
						{					
							reResult = "03";
							System.out.print("## GolfLoginLogInsProc | 회원 | 로그인불가 | 한달이내에 탈퇴한 유료회원일 경우 재가입불가 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
						}
						else	// 재가입 : 골프카드가 있을경우 재가입은 카드가입페이지로 2009.08.25 권영만 농협추가 2009.11.06 임은혜
						{	
							
							//가입하지 않은 TM 내역이 있을 경우 TM가입 페이지로 돌린다. -> 결제 후 1년이 지나면 스킵
							pstmt = con.prepareStatement(getMemTmQuery());
							pstmt.setString(1, userEtt.getSocid());	
							pstmt.setString(2, userEtt.getSocid());
							rs2 = pstmt.executeQuery();
												
							if(rs2.next()){						
								//1.티엠등록 -> 이미 결제한 상태이므로 동의만 있는 페이지로 돌린다.
								reResult = "01";
								System.out.print("## GolfLoginLogInsProc | 로그인회원 | TM회원 | TM 가입으로 보내기 (업데이트)2 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
							}else {
							
								//우선순위에 따른 가입처리							
								// 기업IBK카드 제휴업체코드 (스마트 등급에 해당되는 제휴코드)-기본 ,스카이패스,아시아나클럽  -> 법인지정만 해당됨
								// 모든 스마트 등급은 VIP카드 소지 회원 유로멤버쉽 가입유도페이지 생략 - 최종욱 차장 요청
								if("Y".equals(golfCardYn) && (
																strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))
																
																))
								{											
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 기업IBK카드 제휴업체코드 (Smart300  등급에 해당되는 제휴코드)-[기본 , 스카이패스, 아시아나클럽] strCardJoinNo : "
													+strCardJoinNo+"/"+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}
								// 모든 스마트 등급은 VIP카드 소지 회원 유로멤버쉽 가입유도페이지 생략 - 최종욱 차장 요청
								else if("Y".equals(vipCardYn) && offerResult  ){
									
									reResult = "02";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | Smart등급  & vip카드 소유  : "+userEtt.getAccount()+" | offerResult  : " + offerResult  +" | reResult : "+reResult+" \n");									
									
								}	
								// 1순위 IBK APT프리미엄 카드
								else if("Y".equals(golfCardYn) && ("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo)) )
								{											
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | IBK APT 프리미엄카드 있는 유저 재가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");															
								}
								// 2순위 탑골프 카드
								else if("Y".equals(topGolfCardYn))
								{								
									reResult = "786";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 탑골프카드 있는 유저 재가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}
								
								// 3순위 농협NH카드
								else if("Y".equals(golfCardNhYn))
								{
									reResult = "10";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 농협NH카드 있는 유저 재가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}	
								// 4순위 경남은행카드	
								else if("Y".equals(golfCardYn) && "394033".equals(strCardJoinNo) )
								{									
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 경남은행카드 있는 유저 재가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");									
															
								}										
								// 5순위 리치카드
								else if("Y".equals(richCardYn))
								{								
									reResult = "785";
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 리치카드 있는 유저 재가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}
								// 6순위 VIP카드 			
								else if("Y".equals(vipCardYn))
								{
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | VIP카드 소지회원 처리 시작  \n");	
									
									String memCk = "N";
									memCk = memCk(con,cdhd_ctgo_seq_no);
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | VIP카드 소지회원 유료회원인지 확인 | ID : "+userEtt.getAccount()+" | memCk : "+memCk+"\n");	
									
									//VIP카드 소지자가 유료등급이 아닐경우 결제동의페이지로 이동
									if("N".equals(memCk))
									{
										reResult = "788";
									
									}
									
									//유료회원날짜가 종료일경우 1년 경과 결제동의페이지로 이동
									if(!"".equals(acrg_cdhd_end_date) && acrg_cdhd_end_date != null)
									{
										String toDate 		= DateUtil.currdate("yyyyMMdd");		//오늘날짜
										if( Integer.parseInt(toDate) > Integer.parseInt(acrg_cdhd_end_date) )
										{
											reResult = "788";																				
										}
									}	
									
									
									System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | VIP카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}														
																					
								else
								{
									if("Y".equals(golfCardYn))
									{								
										reResult = "09";
										System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 나머지 기업은행카드 있는 유저 재가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
									}
									else
									{
										reResult = "02";
										System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 일반 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
									}
								}
								if("".equals(reResult)) reResult = "02";
								System.out.print("## GolfLoginLogInsProc | 이미탈퇴 재가입 회원 | 가입분류처리결과 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								
								
							}
							
						}
					}
						
					}else{	// 탈퇴일자가 없는 경우 => 정상 로그인으로 본다.

						/* 유료회원 기간연장 - TM START */
						if(!GolfUtil.empty(acrg_cdhd_end_date) && charge_mem.equals("N")){
							debug("유료회원 기간연장 : acrg_cdhd_end_date : " + acrg_cdhd_end_date + " / charge_mem : " + charge_mem + " / memId : " + userEtt.getAccount());
							execute_exPeriod(con, userEtt.getSocid(), userEtt.getAccount());
						}
						/* 유료회원 기간연장 - TM END */
						
						
						//가입하지 않은 TM 내역이 있을 경우 TM가입 페이지로 돌린다. -> 결제 후 1년이 지나면 스킵
						pstmt = con.prepareStatement(getMemTmQuery());
						pstmt.setString(1, userEtt.getSocid());	
						pstmt.setString(2, userEtt.getSocid());
						rs2 = pstmt.executeQuery();
											
						if(rs2.next()){						
							//1.티엠등록 -> 이미 결제한 상태이므로 동의만 있는 페이지로 돌린다.
							reResult = "01";
							System.out.print("## GolfLoginLogInsProc | 로그인회원 | TM회원 | TM 가입으로 보내기 (업데이트)3 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
						}else{
							
							// BC 회원테이블의 가상 주민등록번호와 골프 회원 테이블의 주민등록번호가 같을 경우 주민등록번호 입력페이지로 이동
							if(vtl_jumin_no.equals(jumin_no)){
								reResult = "11";
								goLogin = true;
								System.out.print("## GolfLoginLogInsProc | 로그인회원 | 주민등록번호 입력으로 : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}else{
								
								// 기업IBK카드 제휴업체코드 (스마트 등급에 해당되는 제휴코드)-기본 ,스카이패스,아시아나클럽  -> 법인지정만 해당됨
								// 모든 스마트 등급은 VIP카드 소지 회원 유로멤버쉽 가입유도페이지 생략 - 최종욱 차장 요청
								if("Y".equals(golfCardYn) && (
																strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))
																
																))
								{											
									reResult = "00";
									goLogin = true;
									System.out.print("## 기업IBK카드 제휴업체코드 (Smart300  등급에 해당되는 제휴코드)-[기본 , 스카이패스, 아시아나클럽] strCardJoinNo : "+strCardJoinNo+"/"+userEtt.getAccount()+" | reResult : "+reResult+" \n");

								}										
								// 모든 스마트 등급은 VIP카드 소지 회원 유로멤버쉽 가입유도페이지 생략 - 최종욱 차장 요청
								else if("Y".equals(vipCardYn) && offerResult ){
									
									reResult = "00";
									goLogin = true;
									
									monthProc.execute_MonthMember(con, userEtt.getSocid(), userEtt.getAccount());		
									
									System.out.print("## 정상 로그인 Smart등급  & vip카드 소유  : "+userEtt.getAccount()+" | offerResult : " + offerResult +" | reResult : "+reResult+" \n");									
									
								}
								// VIP카드 소지회원 처리 2010.09.14 권영만
								else if("Y".equals(vipCardYn))
								{
									System.out.print("## GolfLoginLogInsProc | 로그인회원 | VIP카드 소지회원 처리 시작 | ID : "+userEtt.getAccount()+" | cdhd_ctgo_seq_no : "+cdhd_ctgo_seq_no+" | acrg_cdhd_end_date : "+acrg_cdhd_end_date+"\n");	 
									
									String memCk = "N";
									memCk = memCk(con,cdhd_ctgo_seq_no);
									System.out.print("## GolfLoginLogInsProc | 로그인회원 | VIP카드 소지회원 유료회원인지 확인 | ID : "+userEtt.getAccount()+" | memCk : "+memCk+"\n");	
									
									//VIP카드 소지자가 유료등급이 아닐경우 결제동의페이지로 이동
									if("N".equals(memCk))
									{
										reResult = "788";
										goLogin = true;
									}
									
									//유료회원날짜가 종료일경우 1년 경과 결제동의페이지로 이동
									else if(!"".equals(acrg_cdhd_end_date) && acrg_cdhd_end_date != null)
									{


System.out.println("유료회원날짜가 종료일경우 1년 경과 결제동의페이지로 이동");
										String toDate 		= DateUtil.currdate("yyyyMMdd");		//오늘날짜
										if( Integer.parseInt(toDate) > Integer.parseInt(acrg_cdhd_end_date) ){
											
											reResult = "788";	
											goLogin = true;
System.out.println("reResult : " + reResult);
System.out.println("goLogin : " + goLogin);
											
										}else{
											
											// 정상 로그인 처리
											reResult = "00";
											goLogin = true;
											System.out.print("## GolfLoginLogInsProc | 로그인회원 | VIP카드 정상 로그인 처리 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
											
										}
										
									}else{
										
										// 정상 로그인 처리
										reResult = "00";
										goLogin = true;
										System.out.print("## GolfLoginLogInsProc | 로그인회원 | VIP카드 정상 로그인 처리 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
										
									}
									
									System.out.print("## GolfLoginLogInsProc | 로그인회원 | VIP카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
									
								}else{
								
									// 정상 로그인 처리
									reResult = "00";
									goLogin = true;
									System.out.print("## GolfLoginLogInsProc  | 정상 로그인 처리 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
									
									/* 기업 골드 회원관련 START */
									// 화이트 회원이 기업골드 이벤트 회원일 경우 기업골드 이벤트 회원으로 업데이트 시킨다. // 20100408
									// 멤버십 유료회원은 기업골드 이벤트 회원일 경우 유료회원기간을 2달 늘려준다.		// 20100506
									isIbkGold 		= execute_isIbkGold(con, userEtt.getSocid());		// 기업골드 회원 여부 
									isMembershiop 	= execute_isMembership(con, userEtt.getAccount());	// 멤버십 회원 여부
									debug("isIbkGold : " + isIbkGold + " / isMembershiop : " + isMembershiop);
									
									if(isMembershiop>0 && isIbkGold>0){
										if(isMembershiop==8){	// white 회원은 등급 업데이트
											int updIbkGold = execute_updIbkGold(con, userEtt.getSocid(), userEtt.getAccount());
											if(updIbkGold>0){
												reResult = "07";
											}
										}else{					// 유료회원은 2달 기간연장
											end_date = execute_updPeriodIbkGold(con, userEtt.getSocid(), userEtt.getAccount());
											if(!GolfUtil.empty(end_date)){
												reResult = "13";
											}
										}
									}									
									/* 기업골드 회원관련 END */
									
									
									/* TM 오퍼 월회원 등급 관련 Start */												
									int isResult2 = monthProc.execute_MonthMember(con, userEtt.getSocid(), userEtt.getAccount());
									if(isResult2>0){
										reResult = "14";
									}
									/* 월회원 등급 관련 End */
									
								}
							}
						}
					}

					if(GolfUtil.empty(jumin_no)){
						reResult = "04";
						System.out.print("## GolfLoginLogInsProc  | 로그인불가 | 주민번호 존재하지 않는 경우 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");								
					}							
										
					if(!( member_clss.equals("1") || member_clss.equals("4")  || member_clss.equals("5")  )){	// 1:정회원 / 4:비회원 / 5:법인회원
						reResult = "05";
						System.out.print("## GolfLoginLogInsProc  | 로그인불가 | 개인/법인 이 아닌 다른권한을 가진 회원이 접속한 경우  | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");		
					}
					
				//골프회원이 아닌 경우
				}else{
					
					System.out.print("## GolfLoginLogInsProc | 비회원 | TBGGOLFCDHD 존재하지 않음 | 골프회원이 아닌 경우 처리시작 | ID : "+userEtt.getAccount()+" | socid : "+userEtt.getSocid()+" \n");					

					// 회원분류가 6(지정카드)인 법인회원인지 체크 
					pstmt = con.prepareStatement(getMemCoYnQuery());
					pstmt.setString(1, userEtt.getAccount());	
					rs4 = pstmt.executeQuery();
					
					String memCoChk = "";
					
					if(rs4 != null) {			 
						
						while(rs4.next())  {	
							
							if("6".equals(rs4.getString("MEM_CLSS")))
							{
								memCoChk = rs4.getString("MEM_CLSS");
							}
							else
							{
								if(!"6".equals(memCoChk)) memCoChk = rs4.getString("MEM_CLSS");
							}														
													
						}
					}					
					
					System.out.print("## GolfLoginLogInsProc | 비회원 | 법인회원이며 지정카드(6)인지 체크 | ID : "+userEtt.getAccount()+" | memCoChk : "+memCoChk+" \n");
					
					if(!"".equals(memCoChk))  //로그인 가능한 법인회원
					{			
						if ("6".equals(memCoChk)) 
						{
							//프로세스 다시 변경 일반 가입페이지로 이동시켜라.
							//reResult = "00";
							//goLogin = true;
							//userEtt.setStrMemChkNum("5");		//개인/법인 (  1,4:개인 / 5:법인 )
														
							// 기업IBK카드 제휴업체코드 (Smart300  등급에 해당되는 제휴코드)-기본 ,스카이패스,아시아나클럽  -> 법인지정만 해당됨
							if("Y".equals(golfCardYn) && (strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
															||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
															||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub")) )){ 
																		
								reResult = "09";
								System.out.print("## GolfLoginLogInsProc | 비회원 | 지정카드인 법인회원 | Smart300 가입 페이지 이동 |  ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");															
							}	
							// 모든 스마트 등급은 VIP카드 소지 회원 유로멤버쉽 가입유도페이지 생략 - 최종욱 차장 요청
							else if("Y".equals(vipCardYn) && offerResult  ){
								
								reResult = "15" ;
								System.out.print("## GolfLoginLogInsProc | 비회원 | 지정카드인 법인회원 | Smart등급  & vip카드 소유  : "+userEtt.getAccount()+" | offerResult  : " + offerResult  +" | reResult : "+reResult+" \n");									
							}								
							
							// 2순위 탑골프 카드
							else if("Y".equals(topGolfCardYn))
							{																								
								reResult = "786";																						
								System.out.print("## GolfLoginLogInsProc | 비회원 | 지정카드인 법인회원 | 탑골프카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");																							
							}
							// 5순위 리치카드
							else if("Y".equals(richCardYn))
							{																
								reResult = "785";																						
								System.out.print("## GolfLoginLogInsProc | 비회원 | 지정카드인 법인회원 | 리치카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							// 6순위 VIP카드 			
							else if("Y".equals(vipCardYn))
							{								
								reResult = "787";																						
								System.out.print("## GolfLoginLogInsProc | 비회원 | 지정카드인 법인회원 | VIP카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							else
							{														
								reResult = "02";
								System.out.print("## GolfLoginLogInsProc | 비회원 | 지정카드인 법인회원 | 회원분류가 6(지정카드)인 법인회원. 일반가입페이지로 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
							
							}
						}
						else
						{
							// 탑골프카드 소지자 다시 한번 법인카드원장 확인
							if("Y".equals(topGolfCardYn))
							{											
								
								
								if("Y".equals(golfCardCoYn))
								{
									System.out.print("## GolfLoginLogInsProc | 비회원 | 법인회원 | 탑골프카드 소지회원 처리 시작 \n");									
									reResult = "786";																						
									System.out.print("## GolfLoginLogInsProc | 비회원 | 법인회원 | 탑골프카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}																		
								else
								{
									reResult = "08";
									System.out.print("## GolfLoginLogInsProc | 비회원 | 법인회원 | 회원분류가 6(지정카드)이외의 법인회원. alert 메인 이동  | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");														
								}
							}
							else
							{
								reResult = "08";
								System.out.print("## GolfLoginLogInsProc | 비회원 | 법인회원 | 회원분류가 6(지정카드)이외의 법인회원. alert 메인 이동  | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");														
							}
							
						}						
					
					//법인테이블에 존재하지 않음
					}else{
						System.out.print("## GolfLoginLogInsProc | 비회원 | 골프회원이 아닌 개인회원 가입처리 시작 | ID : "+userEtt.getAccount()+" \n");
												
						//TM회원인지 체크 
						pstmt = con.prepareStatement(getMemTmQuery());
						pstmt.setString(1, userEtt.getSocid());	
						pstmt.setString(2, userEtt.getSocid());
						rs2 = pstmt.executeQuery();
						
						if(rs2.next()){						
							//1.티엠등록 -> 이미 결제한 상태이므로 동의만 있는 페이지로 돌린다.
							reResult = "01";
							System.out.print("## GolfLoginLogInsProc | 비회원 | TM회원 | TM 가입으로 보내기 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							
						}else{	
														
							// 1순위 IBK APT프리미엄 카드
							if("Y".equals(golfCardYn) && ("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo)) )
							{											
								reResult = "09";
								System.out.print("## GolfLoginLogInsProc | 비회원 | IBK APT프리미엄 카드회원 | IBK APT프리미엄 카드가입으로 보내기 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
							}
							// 모든 스마트 등급은 VIP카드 소지 회원 유로멤버쉽 가입유도페이지 생략 - 최종욱 차장 요청
							else if("Y".equals(vipCardYn) && offerResult  ){
								
								reResult = "15" ;
								System.out.print("## GolfLoginLogInsProc | 비회원 개인 |Smart등급  & vip카드 소유  : "+userEtt.getAccount()+" | offerResult  : " + offerResult  +" | reResult : "+reResult+" \n");									
							}
							// 2순위 탑골프 카드
							else if("Y".equals(topGolfCardYn))
							{								
								System.out.print("## GolfLoginLogInsProc | 비회원 탑골프카드 소지회원 처리 시작 \n");									
								reResult = "786";																						
								System.out.print("## GolfLoginLogInsProc | 비회원 | 탑골프카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							
							// 3순위 농협NH카드
							else if("Y".equals(golfCardNhYn))
							{
								reResult = "10";
								System.out.print("## GolfLoginLogInsProc | 비회원 | 농협NH카드 카드회원 | 농협NH카드 카드가입으로 보내기 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
							}	
							// 4순위 경남은행카드	
							else if("Y".equals(golfCardYn) && "394033".equals(strCardJoinNo) )
							{									
								reResult = "09";
								System.out.print("## GolfLoginLogInsProc | 비회원 | 골프카드회원 | 골프카드가입으로 보내기 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
														
							}	
							// 5순위 리치카드
							else if("Y".equals(richCardYn))
							{								
								System.out.print("## GolfLoginLogInsProc | 비회원 리치카드 소지회원 처리 시작 \n");									
								reResult = "785";																						
								System.out.print("## GolfLoginLogInsProc | 비회원 | 리치카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							// 6순위 VIP카드 			
							else if("Y".equals(vipCardYn))
							{
								System.out.print("## GolfLoginLogInsProc | 비회원 | VIP카드 소지회원 처리 시작 \n");									
								reResult = "787";																						
								System.out.print("## GolfLoginLogInsProc | 비회원 | VIP카드 가입 페이지 이동 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}														
																				
							else
							{
								if("Y".equals(golfCardYn))
								{								
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | 비회원 | 골프카드회원 | 골프카드가입으로 보내기 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
								}
								else
								{
									// I-pin 관련 추가 - 주민등록번호가 없으면 주민등록번호 입력페이지로 넘긴다.

									sql = this.getUserInfoQuery(member_clss);  	// 회원등급번호 1:개인 / 5:법인
						            userInfoPstmt = con.prepareStatement(sql);
						            userInfoPstmt.setString(1, userEtt.getAccount() );
						            userInfoRs = userInfoPstmt.executeQuery();	
						            
									if(userInfoRs.next()){
										jumin_no = userInfoRs.getString("SOCID");	// 이메일
									}
									
									if(userInfoRs != null) userInfoRs.close();
						            if(userInfoPstmt != null) userInfoPstmt.close();
						            
						            if(GolfUtil.empty(jumin_no) || jumin_no.equals("")){
										reResult = "11";
										System.out.print("## GolfLoginLogInsProc | 비회원 | 주민등록번호 입력으로 : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
						            }else{
						            	reResult = "02";
										System.out.print("## GolfLoginLogInsProc | 비회원 | 일반회원 | 일반가입으로 보내기 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
						            }
								}
							}
							
							if("".equals(reResult)) reResult = "02";
							System.out.print("## GolfLoginLogInsProc | 비회원 가입 회원 | 가입분류처리결과 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
														
						}					
					}					
					
				}
				
							
				if(!(member_clss.equals("1") || member_clss.equals("4")  || member_clss.equals("5") )){
					reResult = "05";
				}
				
				System.out.print("## GolfLoginLogInsProc  | ID : "+userEtt.getAccount()+" | MEMBER_CLSS : "+member_clss+" | reResult : "+reResult+" \n");			
				
				/////////////////////////////////////////////////////////////////////////////////
				
				// 기업골드 회원
				if(reResult.equals("02")){
					
					//기업골드 회원
					isIbkGold = execute_isIbkGold(con, userEtt.getSocid());
					if(isIbkGold>0){
						reResult = "06";
					}
					
				}				
				
				if(reResult.equals("02")){	// 02말고 법인에 해당되는..?
					
					//TM 오퍼 월회원 등급은 가입확인 페이지로 이동
					boolean isResult = monthProc.execute_newJoinMemYN(con, userEtt.getSocid());
					if(isResult){
						reResult = "15" ;
					}					
					
				}
				
				/////////////////////////////////////////////////////////////////////////////////
				//  goLogin 이 True인 경우 로그인 처리 등급등 세션처리
				String join_chnl = "";		// 가입경로
				if(goLogin){
					System.out.print("## GolfLoginLogInsProc | goLogin | 1-1.로그인에 따른 정보 세션에 넣기 시작  | ID : "+userEtt.getAccount()+" \n");	
					
					System.out.print("## GolfLoginLogInsProc | 등급 가져오기 ================================ \n");
					pstmt = con.prepareStatement(getGrdQuery());
					pstmt.setString(1, userEtt.getAccount());	
					pstmt.setString(2, userEtt.getAccount());	
					rs_grd = pstmt.executeQuery();
					
					if(rs_grd != null){
						if(rs_grd.next()){
							intMemGrade = rs_grd.getInt("GRD_COMM");
							memGrade = rs_grd.getString("GRD_NM");
							intMemberGrade = rs_grd.getInt("GRD_MEM");
							intCardGrade = rs_grd.getInt("GRD_CARD");
							join_chnl = rs_grd.getString("JOIN_CHNL");
						}
					}

					System.out.print("## GolfLoginLogInsProc | 데이타 정합성 일원화 ================================ \n");
					pstmt = con.prepareStatement(getMemSelQuery());
					idx = 0;
					pstmt.setString(++idx, userEtt.getAccount());
					pstmt.setString(++idx, userEtt.getAccount());
					rs_memInfo = pstmt.executeQuery();
					
					String upd_name = "N";
					String upd_mobile = "N";
					String upd_phone = "N";
					String upd_email = "N";
					String upd_zipcode = "N";
					String upd_addClss = "N";
					String upd_zipaddr = "N";
					String upd_detailaddr = "N";
					String upd_grade = "N";
					
					if(rs_memInfo != null){
						
						if(rs_memInfo.next()){
							
							if(!GolfUtil.empty(rs_memInfo.getString("HG_NM")) && !GolfUtil.empty(userEtt.getName())){ 
								if(!userEtt.getName().equals(rs_memInfo.getString("HG_NM"))) upd_name = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("MOBILE")) && !GolfUtil.empty(userEtt.getMobile())){
								if(!userEtt.getMobile().equals(rs_memInfo.getString("MOBILE"))) upd_mobile = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("PHONE")) && !GolfUtil.empty(userEtt.getPhone())){
								if(!userEtt.getPhone().equals(rs_memInfo.getString("PHONE"))) upd_phone = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("EMAIL")) && !GolfUtil.empty(userEtt.getEmail1())){
								if(!userEtt.getEmail1().equals(rs_memInfo.getString("EMAIL"))) upd_email = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("ZIP_CODE")) && !GolfUtil.empty(userEtt.getZipcode())){
								if(!userEtt.getZipcode().equals(rs_memInfo.getString("ZIP_CODE"))) upd_zipcode = "Y"; 
							}
							
							if(!GolfUtil.empty(rs_memInfo.getString("NW_OLD_ADDR_CLSS")) && !GolfUtil.empty(userEtt.getNwOldAddrClss())){
								if(!userEtt.getNwOldAddrClss().equals(rs_memInfo.getString("NW_OLD_ADDR_CLSS"))) upd_addClss = "Y"; 
							}
														
							if (  userEtt.getNwOldAddrClss() != null ){
								if ( userEtt.getNwOldAddrClss().equals("1")){
								
									if(!GolfUtil.empty(rs_memInfo.getString("ZIPADDR")) && !GolfUtil.empty(userEtt.getZipaddr())){
										if(!userEtt.getZipaddr().equals(rs_memInfo.getString("ZIPADDR"))) upd_zipaddr = "Y"; 
									}
									if(!GolfUtil.empty(rs_memInfo.getString("DETAILADDR")) && !GolfUtil.empty(userEtt.getDetailaddr())){
										if(!userEtt.getDetailaddr().equals(rs_memInfo.getString("DETAILADDR"))) upd_detailaddr = "Y"; 
									}								
									
								}else if ( userEtt.getNwOldAddrClss().equals("2")){
									
									if(!GolfUtil.empty(rs_memInfo.getString("ZIPADDR")) && !GolfUtil.empty(userEtt.getDongOvrNewAddr())){
										if(!userEtt.getDongOvrNewAddr().equals(rs_memInfo.getString("ZIPADDR"))) upd_zipaddr = "Y"; 
									}
									if(!GolfUtil.empty(rs_memInfo.getString("DETAILADDR")) && !GolfUtil.empty(userEtt.getDongBlwNewAddr())){
										if(!userEtt.getDongBlwNewAddr().equals(rs_memInfo.getString("DETAILADDR"))) upd_detailaddr = "Y"; 
									}								
									
								}
							}
							
							if(!GolfUtil.empty(rs_memInfo.getString("CDHD_CTGO_SEQ_NO")) && !GolfUtil.empty(rs_memInfo.getString("TOP_CDHD_CTGO_SEQ_NO"))){
								if(!rs_memInfo.getString("TOP_CDHD_CTGO_SEQ_NO").equals(rs_memInfo.getString("CDHD_CTGO_SEQ_NO"))) upd_grade = "Y";
							}
						
						}
						
					} 			
					
					pstmt = con.prepareStatement(getMemUpdQuery(intMemGrade, upd_name, upd_mobile, upd_phone, upd_email, upd_zipcode, upd_zipaddr, upd_detailaddr, upd_addClss, upd_grade));
					idx = 0;
					if(upd_name.equals("Y"))		pstmt.setString(++idx, userEtt.getName());	
					if(upd_mobile.equals("Y"))		pstmt.setString(++idx, userEtt.getMobile());
					if(upd_phone.equals("Y"))		pstmt.setString(++idx, userEtt.getPhone());
					if(upd_email.equals("Y"))		pstmt.setString(++idx, userEtt.getEmail1());
					if(upd_zipcode.equals("Y"))		pstmt.setString(++idx, userEtt.getZipcode());
					
					if (  userEtt.getNwOldAddrClss() != null ){
						if ( userEtt.getNwOldAddrClss().equals("1")){
							if(upd_zipaddr.equals("Y"))		pstmt.setString(++idx, userEtt.getZipaddr());
							if(upd_detailaddr.equals("Y"))	pstmt.setString(++idx, userEtt.getDetailaddr());
						}else if ( userEtt.getNwOldAddrClss().equals("2")){
							if(upd_zipaddr.equals("Y"))		pstmt.setString(++idx, userEtt.getDongOvrNewAddr());
							if(upd_detailaddr.equals("Y"))	pstmt.setString(++idx, userEtt.getDongBlwNewAddr());
							
						}
					}
					
					if(upd_addClss.equals("Y"))		pstmt.setString(++idx, userEtt.getNwOldAddrClss());
					if(upd_grade.equals("Y"))		pstmt.setString(++idx, rs_memInfo.getString("TOP_CDHD_CTGO_SEQ_NO"));
					pstmt.setString(++idx, userEtt.getAccount());
					pstmt.executeUpdate();
					
//					debug("name : " + userEtt.getName() + " / 핸폰 : " + userEtt.getMobile() + " / 전화 : " + userEtt.getPhone() 
//							+ " / 이메일 : " + userEtt.getEmail1() + " / 우편번호 : " + userEtt.getZipcode() + " / 주소 : " + userEtt.getZipaddr() 
//							+ " / 상세주소 : " + userEtt.getDetailaddr() + " / intMemGrade : " + intMemGrade
//							+ " / upd_name : " + upd_name + " / upd_mobile : " + upd_mobile + " / upd_phone : " + upd_phone + " / upd_email : " + upd_email
//							 + " / upd_zipcode : " + upd_zipcode + " / upd_zipaddr : " + upd_zipaddr + " / upd_detailaddr : " + upd_detailaddr + " / upd_grade : " + upd_grade
//							);  


					//최근접속일자는 데이터 정합성과 함께
//					System.out.print("## GolfLoginLogInsProc | 최근접속일자 업데이트 ================================ \n");
//					pstmt = con.prepareStatement(getUpdAccessQuery());
//		        	pstmt.setString(1, userEtt.getAccount() ); 
//					pstmt.executeUpdate();		

					System.out.print("## GolfLoginLogInsProc | 사이버머니 가져오기 ================================ \n");
					pstmt = con.prepareStatement(getCyberMoneyQuery());
					pstmt.setString(1, userEtt.getAccount());	
					rs3 = pstmt.executeQuery();
					
					if(rs3 != null){
						while(rs3.next()){
							userEtt.setCyberMoney((int)rs3.getInt("TOT_AMT"));
						}
					}
				}
				
				userEtt.setIntMemGrade(intMemGrade);		//공통등급
				userEtt.setIntMemberGrade(intMemberGrade);	//멤버쉽등급처리
				userEtt.setIntCardGrade(intCardGrade);		//카드등급처리
				userEtt.setMemGrade(memGrade);				//등급명
				userEtt.setEmail1(memEmail);
				userEtt.setMobile(memMobile);
				
				debug("## GolfLoginLogInsProc | intMemGrade : "+userEtt.getIntMemGrade() + " / intMemberGrade : "+userEtt.getIntMemberGrade()
						+" / intCardGrade : "+userEtt.getIntCardGrade() + " / memGrade : "+userEtt.getMemGrade() + " / reResult : "+reResult);

				result.addString("RESULT", reResult);
				result.addString("end_date", end_date);
				result.addString("join_chnl", join_chnl);
								
			} 
							
			
		} catch(Exception e){
			// 트랜젝션 상태일때는 롤백
			try {
				if (!con.getAutoCommit()) {
					con.rollback();
				}
			} catch (Exception ex) {}
			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, strMESSAGE_KEY, null );
			msgEtt.addEvent( actnKey + ".do", "/golf/img/common/btn/btn_confirmation.gif");
			throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close(); } catch( Exception ignored){}
			try { if(rs2  != null) rs2.close(); } catch( Exception ignored){}
			try { if(rs3  != null) rs3.close(); } catch( Exception ignored){}
			try { if(rs4  != null) rs4.close(); } catch( Exception ignored){}
			try { if(rs_grd  != null) rs_grd.close(); } catch( Exception ignored){}
			try { if(rs_membership  != null) rs_membership.close(); } catch( Exception ignored){}
			try { if(rs_end_date  != null) rs_end_date.close(); } catch( Exception ignored){}
			try { if(pstmt != null) pstmt.close(); } catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		
		return result;
			
		
	}
	public String memCk(Connection con, String intMemGrade) throws DbTaoException  {
		String strResult = "N";
		
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {		
			
			debug("## memCk | intMemGrade : "+intMemGrade);
			
			sql = this.getMemberCkFreeQuery(); 
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();	
           
            while (rs.next())	{
            	
            	String ckNum = rs.getString("CDHD_CTGO_SEQ_NO").replaceAll("0", "");
            	//debug("## memCk | 유효등급여부 CDHD_CTGO_SEQ_NO : "+rs.getString("CDHD_CTGO_SEQ_NO")+"==> ckNum : "+ckNum);
            	
            	
            	if(intMemGrade.equals(ckNum))
            	{
            		strResult = "Y";
            	}
            	
            }
                            
            //유료연회비 카드 추가 12 (8) : NH티타늄 , 13 (9) : NH플래티늄 , 14 () : NH패밀리, 20 (15) : APT 프리미엄, 19 (14) : 경남은행 Family카드, 21 (16): 탑골프, 22 (17): 리치카드, 전북시그니처 29
            if("12".equals(intMemGrade) || "13".equals(intMemGrade) || "14".equals(intMemGrade) 
            		|| "20".equals(intMemGrade) || "19".equals(intMemGrade) || "21".equals(intMemGrade) || "22".equals(intMemGrade) 
                        || "29".equals(intMemGrade) )
            {
            	strResult = "Y";
            }  
            
			
            debug("## memCk | strResult : "+strResult);
			
		
		} catch(Exception e) {
						
            //MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            //throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}            
		}
		return strResult;
	}
	/** ***********************************************************************
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMemberCkFreeQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT A		\n");
		sql.append("\t  WHERE A.CDHD_SQ1_CTGO = '0002' AND ANL_FEE > 0			\n");
		return sql.toString();
	}
	/** 
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */ 
	public int execute_card(Connection con, String memId, String cardYn, String bnNo, String cardGb) throws TaoException {

		String title				= "카드 등록처리";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;		
		
		try {

			String cardRegDate 	= "";	//카드등록날짜
			String toDate 		= DateUtil.currdate("yyyyMMdd");		//오늘날짜
			String tolDate 		= DateUtil.currdate("yyyyMMddHHmmss");		//오늘날짜
			String fromDate		= "";
			String toChgDate	= "";
			String memGrade		= "";	// 회원대표등급
			String bnNo2		= "";
			
			debug("## golfCardLog | execute_card | memId : "+memId);
			debug("## golfCardLog | execute_card | cardYn : "+cardYn);
			debug("## golfCardLog | execute_card | bnNo : "+bnNo);
			debug("## golfCardLog | execute_card | cardGb : "+cardGb);

			if("Y".equals(cardYn))
			{
				System.out.print("## golfCardLog : 카드 있음 \n");
				
				//VIP는 제외 나머지는 자동카드등급부여
				if(!"vip".equals(cardGb))
				{
					// 해당 카드가 등록되어 있는지 확인 getGrdDateQuery
					pstmt = con.prepareStatement(getGrdDateQuery());
					pstmt.setString(1, memId);	
					pstmt.setString(2, bnNo);	
					rs = pstmt.executeQuery();
					if(rs != null && rs.next())
					{
						
						debug("## execute_card | 해당 카드 등급 이미 있음");
						// 카드등록날짜 조회 
						cardRegDate = rs.getString("REG_ATON").substring(0,8);									
						fromDate	= DateUtil.dateAdd('M', 12, cardRegDate, "yyyyMMdd");

						//카드회원의 카드등록날짜가 만료된 경우 자동연장 처리
						//if( Float.valueOf(toDate).floatValue() > Float.valueOf(fromDate).floatValue() )
						if( Integer.parseInt(toDate) > Integer.parseInt(fromDate) )
						{	
							System.out.print("## golfCardLog : 카드등록날짜 자동연장 처리 | fromDate : "+fromDate+" | toDate : "+toDate+"\n");
							toChgDate	= DateUtil.dateAdd('M', 12, tolDate, "yyyyMMddHHmmss");
							
							pstmt = con.prepareStatement(getCardDateUpdQuery());
							pstmt.setString(1, toChgDate);
							pstmt.setString(2, memId);
							pstmt.executeUpdate();
						}
						
					}
					else
					{
						debug("## execute_card | 해당 카드 등급 없어 카드등급 넣기");
						pstmt = con.prepareStatement(getMemTopGolfQuery());
						pstmt.setString(1, memId ); 
						rs = pstmt.executeQuery();
						if(rs.next()){
							
							String memGrd = rs.getString("CDHD_CTGO_SEQ_NO"); //회원현재등급
							
							debug("## 골프 회원임 ID : "+memId+" | memGrd : "+memGrd);
							
							// 등록되어 있지 않다면 카드 인서트 해준다.
							/**SEQ_NO 가져오기**************************************************************/
							sql = this.getNextValQuery(); 
				            pstmt = con.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long max_seq_no = 0L;
							if(rs.next()){
								max_seq_no = rs.getLong("SEQ_NO");
							}
				            
				            /**Insert************************************************************************/
				            sql = this.getInsertGradeQuery();
							pstmt = con.prepareStatement(sql);
							
				        	pstmt.setLong(1, max_seq_no ); 
				        	pstmt.setString(2, memId ); 
				        	pstmt.setString(3, bnNo );
				        	
							pstmt.executeUpdate();
							
							// 현재 자기등급보다 높은 등급이 있는지 알아본다.getGrdChgQuery => 바꿔준다.
							pstmt = con.prepareStatement(getGrdChgQuery());
							pstmt.setString(1, bnNo);	
							pstmt.setString(2, memId);	
							rs = pstmt.executeQuery();
							if(rs != null)
							{
								if(rs.next())
								{
									if("Y".equals(rs.getString("CHG_YN")))
									{
										
										if("topGolf".equals(cardGb) || "rich".equals(cardGb) 
                                                                                  || "jb".equals(cardGb))
										{
											pstmt = con.prepareStatement(getUpdGradeDateQuery());
										}
										else
										{
											pstmt = con.prepareStatement(getUpdGradeQuery());
										}
										
										pstmt.setString(1, bnNo);	
										pstmt.setString(2, memId);	
										pstmt.executeQuery();
									}
								}
							}
						}
						else
						{
							debug("## 골프 회원이 아님");
						}
					}
					
				}

				if(bnNo.equals("12")){
					bnNo2 = "13";
				}else{
					bnNo2 = "12";
				}

				pstmt = con.prepareStatement(getGrdDateQuery());
				pstmt.setString(1, memId);	
				pstmt.setString(2, bnNo2);	
				rs = pstmt.executeQuery();
				
				if(rs != null && rs.next()){

					pstmt = con.prepareStatement(getGrdDelQuery());
					pstmt.setString(1, rs.getString("CDHD_GRD_SEQ_NO"));	
					pstmt.executeUpdate();
				}
				
			}
			else
			{
				System.out.print("## golfCardLog : 카드 없음 \n");
				
				//VIP는 제외 나머지는 자동카드등급부여
				if(!"vip".equals(cardGb))
				{
					pstmt = con.prepareStatement(getChkGradeQuery(cardGb));
					pstmt.setString(1, memId);	
					rs = pstmt.executeQuery();
					
					if(rs != null)
					{
						if(rs.next())
						{
							memGrade = rs.getString("CDHD_CTGO_SEQ_NO");
							// 삭제	
							pstmt = con.prepareStatement(getDelGrdQuery(cardGb));
							pstmt.setString(1, memId);	
							pstmt.executeQuery();
							
							// 등급이 하나도 없으면 탈퇴처리 getCntGrdQuery -> 20100310 화이트로 돌린다.
							pstmt = con.prepareStatement(getCntGrdQuery());
							pstmt.setString(1, memId);	
							rs = pstmt.executeQuery();
							
							if(rs != null)
							{
								if(rs.next())
								{
									if(rs.getInt("CNT_GRD")==0)
									{
										// 삭제처리
//										pstmt = con.prepareStatement(getClssmUpdQuery());	
//										pstmt.setString(1, memId);
//										pstmt.executeUpdate();	
										
										// 화이트 회원처리 - 멤버테이블
										pstmt = con.prepareStatement(getWhiteUpdateQuery());	
										pstmt.setString(1, memId);
										pstmt.executeUpdate();	

										// 화이트 회원처리 - 등급테이블
										sql = this.getNextValQuery(); 
							            pstmt = con.prepareStatement(sql);
							            rs = pstmt.executeQuery();			
										long max_seq_no = 0L;
										if(rs.next()){
											max_seq_no = rs.getLong("SEQ_NO");
										}
										if(rs != null) rs.close();
							            if(pstmt != null) pstmt.close();
							            
							            sql = this.getInsertGradeQuery();
										pstmt = con.prepareStatement(sql);
										
							        	pstmt.setLong(1, max_seq_no ); 
							        	pstmt.setString(2, memId ); 
							        	pstmt.setString(3, "8" );
							        	
										pstmt.executeUpdate();
										
									}
									else
									{
										// 등급이 남아 있으며, 대표등급이 카드일 경우 대표등급을 변경해준다.									
										if( ( ( "ibk".equals(cardGb) || "vip".equals(cardGb) ) 
												&& ( "9".equals(memGrade) || "10".equals(memGrade) || "19".equals(memGrade) )
											) 
											|| 
											( "nh".equals(cardGb) 
											  && ("12".equals(memGrade) || "13".equals(memGrade) || "14".equals(memGrade))
											)
										){
											
											pstmt = con.prepareStatement(getTopGradeQuery());
											pstmt.setString(1, memId);	
											rs = pstmt.executeQuery();
											if(rs != null)
											{
												if(rs.next())
												{
													pstmt = con.prepareStatement(getUpdGradeQuery());
													pstmt.setString(1, rs.getString("CDHD_CTGO_SEQ_NO"));
													pstmt.setString(2, memId);
													pstmt.executeUpdate();	
												}
											}
												
										}
									}
								}
							}
							
						}
					}
					
				}
				
				
			}

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}	

	// 기업은행 골드 회원인지 알아본다.
	public int execute_isIbkGold(Connection con, String socId) throws TaoException {

		String title				= "기업은행 골드 회원인지 알아본다.";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		ResultSet rs_again 			= null;
		ResultSet rs_used 			= null;
		int result					= 0;
		
		try {

			sql = getIbkGoldQuery();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				result = 1;
			}else{
				
				sql = getUsedIbkGoldQuery();			// 이미 적용된 내역이 있는가?
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, socId);	
				rs_again = pstmt.executeQuery();
				if(rs_again != null && rs_again.next()){
					
					sql = getUsedIbkGoldMemQuery();		// 나의 회원정보에 적용내용이 있는가?
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, socId);	
					pstmt.setString(2, socId);	
					rs_used = pstmt.executeQuery();
					if(!rs_used.next()){				// 이미 적용되었지만 나의 회원정보에는 적용된 내역이 없을때 다시 자격 획득
						result = 1;
					}
				}
				
			}


			if(rs != null) rs.close();
			if(rs_again != null) rs_again.close();
			if(rs_used != null) rs_again.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(rs_again  != null) rs_again.close();  } catch (Exception ignored) {}
            try { if(rs_used  != null) rs_used.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}

	// 멤버십 등급을 가지고 있는지 알아본다.
	public int execute_isMembership(Connection con, String userId) throws TaoException {

		String title				= "멤버십 등급을 가지고 있는지 알아본다.";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		
		try {
			
			sql = getMemberShipQuery();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);	
			rs = pstmt.executeQuery();
			if(rs != null){
				while(rs.next()){
					result = rs.getInt("CDHD_CTGO_SEQ_NO");
				}
			}


			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}
	

	// 기업은행 골드 회원으로 업데이트한다.
	public int execute_updIbkGold(Connection con, String socId, String memId) throws TaoException {

		String title				= "기업은행 골드 회원인지 알아본다.";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		
		int idx = 0;
		int resultExecute = 0;
							
		
		try {

			String joinChnl				= "";	// 골프회원테이블 가입경로구분 코드
			String cdhd_ctgo_seq_no		= "";	// 회원분류일련번호 = 대표등급
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
			GregorianCalendar cal = new GregorianCalendar(); 
	        Date stdate = cal.getTime();
	        String strStDate = fmt.format(stdate);	// 유료회원기간 시작일
	        
	        cal.add(cal.MONTH, 2);
	        Date edDate = cal.getTime();
	        String strEdDate = fmt.format(edDate);	// 유료회원기간 종료일
	        
			sql = getIbkGoldInfoQuery();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){

				joinChnl 	= rs.getString("RCRU_PL_CLSS");
				cdhd_ctgo_seq_no 	= "18";

				// 회원테이블 업데이트 - 대표등급, 가입채널
				DbTaoDataSet dataSet = new DbTaoDataSet(title);
				dataSet.setString("moneyType", "13");
				dataSet.setString("joinChnl", joinChnl);
				dataSet.setString("cdhd_ctgo_seq_no", cdhd_ctgo_seq_no);
				dataSet.setString("strStDate", strStDate);
				dataSet.setString("strEdDate", strEdDate);
				dataSet.setString("memId", memId);
				dataSet.setString("socId", socId);
				
				// 회원 테이블 업데이트
				resultExecute = execute_updMem(con, dataSet);
				//debug("회원 테이블 업데이트 결과 :: resultExecute : " + resultExecute);
		        
				// 가입 후 이벤트 테이블 업데이트
		        if(resultExecute>0){
		            sql = this.getUpdIbkGoldEndQuery();
					pstmt = con.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, memId );
		        	pstmt.setString(++idx, socId );
		        					        	
		        	resultExecute = pstmt.executeUpdate();
					//debug("가입 후 이벤트 테이블 업데이트 :: resultExecute : " + resultExecute);
		        }
			}

			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return resultExecute;
		
	}	

	// 유료멤버십을 가지고 있는, 기업은행 골드 회원 => 유료회원기간 2달 연장
	public String execute_updPeriodIbkGold(Connection con, String socId, String memId) throws TaoException {

		String title				= "유료멤버십을 가지고 있는, 기업은행 골드 회원 => 유료회원기간 2달 연장";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		
		int resultExecute = 0;
		String end_date = "";
		
		try {

			con.setAutoCommit(false);			

			// 2달 연장
			pstmt = con.prepareStatement(getIbkMemUpdQuery());
			pstmt.setString(1, memId);	
			resultExecute = pstmt.executeUpdate();
			
			if(resultExecute>0){

				// 기업골프 회원 테이블 가입완료로 업데이트
				pstmt = con.prepareStatement(getUpdIbkGoldEndQuery());
	        	pstmt.setString(1, memId );
	        	pstmt.setString(2, socId );
	        	resultExecute = pstmt.executeUpdate();
	        	
			}
			

			if(resultExecute>0){			
				con.commit();
				
				// 종료일 가져오기
				pstmt = con.prepareStatement(getMemEndQuery());
				pstmt.setString(1, memId);	
				rs = pstmt.executeQuery();
				if(rs.next()){
					end_date = rs.getString("END_DATE");		        	
				}
			}else{
				con.rollback();
			}


			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return end_date;
		
	}

	// 골프라운지 회원 업데이트
	public int execute_updMem(Connection con, TaoDataSet data) throws TaoException {

		String title				= "회원 업데이트";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		int idx						= 0;
		
		try {

			sql = this.getInsGrdHistoryQuery();
			pstmt = con.prepareStatement(sql);
			idx = 0;
        	pstmt.setString(++idx, data.getString("memId") );
        	result = pstmt.executeUpdate();
        	
        	if(result>0){
				sql = this.getUpdMemIbkGoldQuery();
				pstmt = con.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
	        	pstmt.setString(++idx, data.getString("joinChnl") );
	        	pstmt.setString(++idx, data.getString("strStDate") );
	        	pstmt.setString(++idx, data.getString("strEdDate") );
	        	pstmt.setString(++idx, data.getString("memId") );
	        	result = pstmt.executeUpdate();

				sql = this.getUpdGrdQuery();
				pstmt = con.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
	        	pstmt.setString(++idx, data.getString("memId") );
	        	result = pstmt.executeUpdate();
        	}

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}
	

	// 유료회원기간 연장 - TM
	public int execute_exPeriod(Connection con, String socId, String memId) throws TaoException {

		String title				= "유료회원기간 연장";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		int idx						= 0;
		
		try {
			String tm_golf_cdhd_grd_clss = "";		// 골프회원 등급 구분코드 1:골드, 2:블루, 3:챔피온
			String golf_cdhd_grd_clss = "";			// 업데이트할 회원 등급
			
			// TM 테이블 결제 내역 가져오기
			sql = this.getExPeriodQuery();
			pstmt = con.prepareStatement(sql);
			idx = 0;
        	pstmt.setString(++idx, socId );
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){

				// 현재 멤버십등급 등급번호가져오기
				int isMembershiop 	= execute_isMembership(con, memId);	// 멤버십 회원 여부
				
				// 유료회원기간 업데이트 
				pstmt = con.prepareStatement(getAcrgUpdQuery());
				pstmt.setString(1, memId);
				result = pstmt.executeUpdate();
				
				if(result>0){
				
					// 업데이트 등급 알아보기
					tm_golf_cdhd_grd_clss = rs.getString("GOLF_CDHD_GRD_CLSS");
					if(tm_golf_cdhd_grd_clss.equals("1")){
						golf_cdhd_grd_clss = "7";
					}else if(tm_golf_cdhd_grd_clss.equals("2")){
						golf_cdhd_grd_clss = "6";
					}else if(tm_golf_cdhd_grd_clss.equals("3")){
						golf_cdhd_grd_clss = "5";
					}
					
					// 변경등급과 현재 등급이 같이 않다면 등급을 업데이트 해준다.
					if(isMembershiop!=Integer.parseInt(golf_cdhd_grd_clss)){
	
						// 등급 히스토리 테이블에 인서트해준다.
						pstmt = con.prepareStatement(getInsGrdHistoryQuery());
						idx = 0;
			        	pstmt.setString(++idx, memId );
			        	result = pstmt.executeUpdate();
						
			        	if(result>0){
							// 등급 업데이트 - getGrdUpdTMQuery
							pstmt = con.prepareStatement(getGrdUpdTMQuery());
							idx = 0;
							pstmt.setString(++idx, golf_cdhd_grd_clss);
							pstmt.setString(++idx, "유료회원기간 연장 TM");
							pstmt.setString(++idx, isMembershiop+"");
							pstmt.setString(++idx, memId);
							result = pstmt.executeUpdate();		
							
							if(result>0){
							// 현재 자기등급보다 높은 등급이 있는지 알아본다.getGrdChgQuery => 바꿔준다.
								pstmt = con.prepareStatement(getGrdChgQuery());
								idx = 0;
								pstmt.setString(++idx, golf_cdhd_grd_clss);	
								pstmt.setString(++idx, memId);	
								rs = pstmt.executeQuery();
								if(rs != null)
								{
									if(rs.next())
									{
										if("Y".equals(rs.getString("CHG_YN")))
										{
											// 변경한 등급이 기존 등급들보다 높다면 대표 등급을 업데이트해준다.
											pstmt = con.prepareStatement(getUpdGradeQuery());
											idx = 0;
											pstmt.setString(++idx, golf_cdhd_grd_clss);	
											pstmt.setString(++idx, memId);	
											result = pstmt.executeUpdate();
										}
									}
								}
							}
			        	}
						
					}

					if(result>0){
						// TM 테이블 업데이트 getUpdTmQuery
						pstmt = con.prepareStatement(getUpdTmQuery());
						idx = 0;
						pstmt.setString(++idx, memId);	
						pstmt.setString(++idx, socId);	
						pstmt.setString(++idx, tm_golf_cdhd_grd_clss);	
						result = pstmt.executeUpdate();
					}
				}
				
			}
					

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}	
	/**
	 * 카드등록날짜 연장 업데이트
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemTopGolfQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD  	\n");
		sql.append("\t	WHERE CDHD_ID=?		\n");

		return sql.toString();
		
	}
	
	/**
	 * 카드회원에서 탈퇴처리
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getClssmUpdQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD 	\n");
		sql.append("\t	SET SECE_YN = 'Y' , SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  	\n");
		sql.append("\t	WHERE CDHD_ID=?		\n");

		return sql.toString();
		
	}
	/**
	 * 카드등록날짜 연장 업데이트
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCardDateUpdQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT 	\n");
		sql.append("\t	SET REG_ATON = ?	\n");
		sql.append("\t	WHERE CDHD_ID=?		\n");

		return sql.toString();
		
	}
	
	/**
	 * 등록 여부
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T1.CDHD_ID, T1.ACRG_CDHD_END_DATE, SUBSTR(T1.SECE_ATON,1,8) AS SECE_ATON	\n");
		sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1),'YYYYMMDD') AS SECE_ADD_MONTH	\n");
		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDD') AS NOW_DATE, T1.JUMIN_NO, T2.MEMBER_CLSS	\n");
		sql.append("\t	, CASE WHEN ACRG_CDHD_END_DATE>SUBSTR(T1.SECE_ATON,1,8) THEN 'Y' ELSE 'N' END AS CHARGE_MEM	\n");
		sql.append("\t	, CASE WHEN ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1)>SYSDATE THEN 'N' ELSE 'Y' END AS JOIN_RE	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
		return sql.toString();		
	}
	
	/**
	 * 개인회원 MemClss 등록 여부
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountMemClssQuery() {
	  
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T2.MEMBER_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
		return sql.toString();		
	}
	
	/**
	 * 개인회원 MemClss 등록 여부
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountMemOnlyClssQuery() {
	  
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T2.MEMBER_CLSS	\n");
		sql.append("\t	FROM BCDBA.UCUSRINFO T2 	\n");
		sql.append("\t	WHERE T2.ACCOUNT=?		\n");
		return sql.toString();		
	}
	
	
	
	/**
	 * sso id 로 순수 골프회원 테이블(TBGGOLFCDHD) 존재 유무 체크
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountCkQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T1.CDHD_ID, T1.ACRG_CDHD_END_DATE, SUBSTR(T1.SECE_ATON,1,8) AS SECE_ATON	\n");
		sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1),'YYYYMMDD') AS SECE_ADD_MONTH	\n");
		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDD') AS NOW_DATE, T1.JUMIN_NO	\n");
		sql.append("\t	, CASE WHEN ACRG_CDHD_END_DATE>SUBSTR(T1.SECE_ATON,1,8) THEN 'Y' ELSE 'N' END AS CHARGE_MEM	\n");
		sql.append("\t	, CASE WHEN ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1)>SYSDATE THEN 'N' ELSE 'Y' END AS JOIN_RE	\n");
		sql.append("\t	, T2.VRTL_JUMIN_NO, T2.SOCID, T1.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
		return sql.toString();		
	}
	
	
	
	/**
	 * 티엠으로 등록했는지 검색
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind 
	 * @return
	 */
	private String getMemTmQuery() {
	  
		StringBuffer sql = new StringBuffer();
		//쿼리 수정 2009.09.08 //쿼리 수정 2009.12.10
		sql.append("\n");
		sql.append("\t	SELECT ROWNUM RNUM, MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
		sql.append("\t	    FROM BCDBA.TBLUGTMCSTMR	\n");
		
		sql.append("\t	  	WHERE ( (TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND WK_DATE>=TO_CHAR(SYSDATE-365,'YYYYMMDD')) 	\n");
		sql.append("\t	   			 OR  (TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND RCRU_PL_CLSS = '4200') )	\n");//KT올레클럽
		
		sql.append("\t	    ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
		sql.append("\t	) WHERE ROWNUM=1	\n");
		return sql.toString();

		
	}
	
	/**
	 * 법인원장 테이블 조회 존재 유무 |  | 2009.10.29 | 권영만
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemEntPcYnQuery() {
	  
		StringBuffer sql = new StringBuffer();
		sql.append("\n").append("	SELECT  CARD_NO  FROM BCDBA.TBENTPCDHD WHERE CARD_NO = ?   ");
		return sql.toString();
		
	}
	/**
	 * 법인회원 테이블 조회 존재 유무 |  | 2009.10.29 | 권영만
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemCoYnQuery() {
	  
		StringBuffer sql = new StringBuffer();		
		sql.append("\n").append("	SELECT  A.ACCOUNT, B.MEM_CLSS, A.BUZ_NO   FROM BCDBA.TBENTPUSER A INNER JOIN BCDBA.TBENTPMEM B ON A.MEM_ID = B.MEM_ID JOIN  BCDBA.UCUSRINFO C ON C.ACCOUNT=A.ACCOUNT  WHERE A.ACCOUNT = ? AND B.MEM_STAT = '2' AND B.SEC_DATE IS NULL  ");
		return sql.toString();
		
	}
	
	
	
	/**
	 * 기업회원인지 검색
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemComQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n").append("		SELECT MEMBER_CLSS FROM BCDBA.UCUSRINFO WHERE ACCOUNT = ? 	");


		return sql.toString();
		
	}
	/**
	 * 멤버쉽회원인지 검색
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemCheckQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n").append("	SELECT T1.CDHD_CTGO_SEQ_NO CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO CDHD_SQ2_CTGO, T3.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM	");
		sql.append("\n").append("	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	");
		sql.append("\n").append("	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	");
		sql.append("\n").append("	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	");
		sql.append("\n").append("	WHERE CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	");

		return sql.toString();
		
	}
	
	
	
	/**
	 * 회원권한 가져오기
	 */
	private String getMemTypeQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T4.GOLF_CMMN_CODE_NM AS GOLF_CMMN_CODE_NM	\n");
		sql.append("\t		, CAST(T3.CDHD_SQ2_CTGO AS INT) AS CDHD_SQ2_CTGO	\n");
		sql.append("\t		, EMAIL1, MOBILE , T2.REG_ATON	\n");
		sql.append("\t		FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t		JOIN BCDBA.TBGCMMNCODE T4 ON T4.GOLF_CMMN_CODE=T3.CDHD_SQ2_CTGO AND GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t		JOIN BCDBA.UCUSRINFO T5 ON T5.ACCOUNT=T1.CDHD_ID	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?	\n");

		return sql.toString();
		
	}
	
	/**
	 * 회원권한 가져오기 ( 법인골프회원테이블로)
	 */
	private String getMemTypeCoQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T4.GOLF_CMMN_CODE_NM AS GOLF_CMMN_CODE_NM														\n");
		sql.append("\t		, CAST(T3.CDHD_SQ2_CTGO AS INT) AS CDHD_SQ2_CTGO													\n");
		sql.append("\t		, T5.USER_EMAIL AS EMAIL1, T5.USER_MOB_NO AS MOBILE , T2.REG_ATON									\n");
		sql.append("\t		FROM BCDBA.TBGGOLFCDHD T1																			\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID											\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO						\n");
		sql.append("\t		JOIN BCDBA.TBGCMMNCODE T4 ON T4.GOLF_CMMN_CODE=T3.CDHD_SQ2_CTGO AND GOLF_CMMN_CLSS='0005'			\n");
		sql.append("\t		JOIN BCDBA.TBENTPUSER T5 ON T5.ACCOUNT=T1.CDHD_ID							\n");
		sql.append("\t	WHERE T1.CDHD_ID=?																						\n");

		return sql.toString();
		
	}
	
	
	
	/**
	 * 사이버머니 가져오기
	 */
	private String getCyberMoneyQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		return sql.toString();
		
	}
	

    /** ***********************************************************************
     * 같은 등급이 등록되어 있는지 확인     
     ************************************************************************ */
     private String getChkGradeQuery(String cardGb){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
        sql.append("SELECT CDHD_GRD_SEQ_NO, CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? \n");
        if("ibk".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('9', '10', '19', '20') \n");
        }
        else if("nh".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('12', '13', '14') \n");
        }
        else if("vip".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('7') \n");
        }
        else if("topGolf".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('21') \n");
        }
        else if("rich".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('22') \n");
        }
        else if("jb".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('29') \n");
        }
        
 		return sql.toString();
     }
 	

     /** ***********************************************************************
      * 해당 등급 삭제    
      ************************************************************************ */
      private String getDelGrdQuery(String cardGb){
          StringBuffer sql = new StringBuffer();
  		sql.append("\n");
          sql.append("DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?  \n");
          if("ibk".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('9', '10', '19', '20') \n");
          }
          else if("nh".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('12', '13', '14') \n");
          }
          else if("vip".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('7') \n");
          }
          else if("topGolf".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('21') \n");
          }
          else if("rich".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('22') \n");
          }
          else if("jb".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('29') \n");
          }
  		return sql.toString();
      }
   	

      /** ***********************************************************************
       * 등급 몇개 있는지 알아보기    
       ************************************************************************ */
       private String getCntGrdQuery(){
           StringBuffer sql = new StringBuffer();
   		sql.append("\n");
           sql.append("SELECT COUNT(*) CNT_GRD FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? \n");
   		return sql.toString();
       }

	
 	/** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다. = 골프회원등급관리    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(CDHD_GRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
		return sql.toString();
    }
       
    /** ***********************************************************************
    * 골프회원등급관리 인서트 - TBGGOLFCDHDGRDMGMT    
    ************************************************************************ */
    private String getInsertGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (							\n");
		sql.append("\t  		CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
		sql.append("\t  		) VALUES (												\n");
		sql.append("\t  		?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')			\n");
		sql.append("\t  		)														\n");
        return sql.toString();
    }	
    
    /** ***********************************************************************
     * 골프회원등급명 가져오기    
     ************************************************************************ */
     private String getGradeQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  SELECT T1.CDHD_SQ2_CTGO AS GRADE_NO, T2.GOLF_CMMN_CODE_NM AS GRADE_NAME	\n");
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT T1	\n");
 		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T2 ON T1.CDHD_SQ2_CTGO=T2.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0005'	\n");
 		sql.append("\t  WHERE T1.CDHD_CTGO_SEQ_NO = ?	\n");
         return sql.toString();
     }
     
     /** ***********************************************************************
      * 골프투어멤버스 확인    
      ************************************************************************ */
      private String getChkTourQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t  SELECT T1.ACRG_CDHD_JONN_DATE, T1.ACRG_CDHD_END_DATE, T1.SECE_YN, T1.SECE_ATON	\n");
  		sql.append("\t  , T3.CDHD_CTGO_SEQ_NO, T3.CDHD_SQ1_CTGO, T3.CDHD_SQ2_CTGO	\n");
  		sql.append("\t  , (CASE WHEN T1.ACRG_CDHD_END_DATE<TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END) ACRG_ABLE	\n");
  		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T1	\n");
  		sql.append("\t  LEFT JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
  		sql.append("\t  LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO AND T3.CDHD_SQ1_CTGO='0002'	\n");
  		sql.append("\t  WHERE T1.CDHD_ID=?	\n");
        return sql.toString();
      }
      
      /** ***********************************************************************
      * 골프회원정보에 업데이트   
      ************************************************************************ */
      private String getReInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
  		sql.append("\t	, ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')			\n");
  		sql.append("\t	, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')		\n");
  		sql.append("\t	, SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
  		sql.append("\t	, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
  		sql.append("\t	, EMAIL_RECP_YN='Y', SMS_RECP_YN='Y'		\n");
  		sql.append("\t	WHERE CDHD_ID=?		\n");
          	
        return sql.toString();
      }
      
      /** ***********************************************************************
      * 골프회원정보 테이블 유료회원기간 업데이트    
      ************************************************************************ */
      private String getAcrgUpdQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
  		sql.append("\t	ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')	\n");
  		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
  		sql.append("\t	WHERE CDHD_ID=?	\n");
          	
        return sql.toString();
      }
      
      /** ***********************************************************************
      * 골프회원등급관리 멤버십 등급 삭제    
      ************************************************************************ */
      private String getMemGradeDelQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT T1 WHERE CDHD_ID=? 	\n");
  		sql.append("\t	AND (SELECT CDHD_SQ1_CTGO FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=T1.CDHD_CTGO_SEQ_NO)='0002'	\n");
          	
        return sql.toString();
      }
      
      /** ***********************************************************************
      * 골프회원정보에 인서트 - TBGGOLFCDHD    
      ************************************************************************ */
      private String getInsertMemQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT										\n");
		sql.append("\t  		,  MEMBER_CLSS																\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");	// 20091214 추가
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')	\n");
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', '3000'						\n");
		sql.append("\t  		, 0, 0, 1																	\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
		return sql.toString();
      }         
      
      /** ***********************************************************************
      * 최근접속일자 업데이트    
      ************************************************************************ */
      private String getUpdAccessQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET LASTACCESS = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
  		sql.append("\t	WHERE CDHD_ID = ?	\n");
          return sql.toString();
      }       
      
      /** ***********************************************************************
       * 대표등급 업데이트    
       ************************************************************************ */
       private String getUpdGradeDateQuery(){
         StringBuffer sql = new StringBuffer();
   		sql.append("	\n");
   		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
   		sql.append("\t	SET ACRG_CDHD_JONN_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') , ACRG_CDHD_END_DATE = TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD') , CDHD_CTGO_SEQ_NO = ?	\n");
   		sql.append("\t	WHERE CDHD_ID = ?	\n");
           return sql.toString();
       }     
      /** ***********************************************************************
      * 대표등급 업데이트    
      ************************************************************************ */
      private String getUpdGradeQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET CDHD_CTGO_SEQ_NO = ?	\n");
  		sql.append("\t	WHERE CDHD_ID = ?	\n");
          return sql.toString();
      }
      
      /** ***********************************************************************
      * 대표등급 업데이트 - 투어회원용
      ************************************************************************ */
      private String getUpdGradeTourQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET CDHD_CTGO_SEQ_NO = ?, JOIN_CHNL='3000'	\n");
  		sql.append("\t	WHERE CDHD_ID = ?	\n");
          return sql.toString();
      }

   	/** ***********************************************************************
  	* 회원정보 가져오기    strMemClss // 회원등급번호 1:개인 / 5:법인
  	************************************************************************ */
  	private String getUserInfoQuery(String strMemClss){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		
  		if("5".equals(strMemClss)){

  			sql.append("\t  SELECT USER_EMAIL EMAIL, '' ZIPCODE, '' ZIPADDR, '' DETAILADDR	\n");
  			sql.append("\t  , USER_MOB_NO MOBILE, USER_TEL_NO PHONE, USER_JUMIN_NO SOCID	\n");
  			sql.append("\t  FROM BCDBA.TBENTPUSER	\n");
  			sql.append("\t  WHERE ACCOUNT=?	\n");
  			
  		}else{			  		
  			
  			sql.append("\t  SELECT EMAIL1 EMAIL, ZIPCODE, ZIPADDR, DETAILADDR, MOBILE, PHONE, SOCID	\n");
  			sql.append("\t  FROM BCDBA.UCUSRINFO	\n");
  			sql.append("\t  WHERE ACCOUNT = ?	\n");
  		}
  		
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 해당 등급이 등록되어 있는지 확인, 등록일자 가져오기
  	************************************************************************ */
  	private String getGrdDateQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT T_GRD.REG_ATON, T_GRD.CDHD_CTGO_SEQ_NO, T_GRD.CDHD_GRD_SEQ_NO	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T_CODE ON T_CODE.GOLF_CMMN_CODE=T_CTGO.CDHD_SQ2_CTGO AND T_CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t  WHERE T_GRD.CDHD_ID = ? AND T_GRD.CDHD_CTGO_SEQ_NO = ?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 해당 등급이 등록되어 있는지 확인, 등록일자 가져오기 => 삭제
  	************************************************************************ */
  	private String getGrdDelQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  DELETE BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_GRD_SEQ_NO=?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 대표등급 변경여부 출력
  	************************************************************************ */
  	private String getGrdChgQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT (CASE WHEN T_CTGO.SORT_SEQ>(SELECT SORT_SEQ FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=?) THEN 'Y' ELSE 'N' END) CHG_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 등급가져오기
  	************************************************************************ */
  	private String getGrdQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT T_CTGO.CDHD_SQ2_CTGO GRD_COMM, T_CODE.GOLF_CMMN_CODE_NM GRD_NM, T_CDHD.JOIN_CHNL	\n");
		sql.append("\t  	, (SELECT T_CTGO.CDHD_SQ2_CTGO	\n");
		sql.append("\t  	FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  	WHERE T_GRD.CDHD_ID=T_CDHD.CDHD_ID AND T_CTGO.CDHD_SQ1_CTGO='0002') GRD_MEM	\n");
		sql.append("\t  	, (SELECT CDHD_SQ2_CTGO FROM (	\n");
		sql.append("\t  	SELECT ROWNUM RNUM, T_CTGO.CDHD_SQ2_CTGO, T_CTGO.SORT_SEQ	\n");
		sql.append("\t  	FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  	WHERE T_GRD.CDHD_ID=? AND T_CTGO.CDHD_SQ1_CTGO<>'0002'	\n");
		sql.append("\t      ORDER BY T_CTGO.SORT_SEQ)	\n");
		sql.append("\t      WHERE RNUM=1) GRD_CARD	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T_CODE ON T_CODE.GOLF_CMMN_CLSS='0005' AND T_CODE.GOLF_CMMN_CODE=T_CTGO.CDHD_SQ2_CTGO	\n");
		sql.append("\t  WHERE T_CDHD.CDHD_ID=?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 가지고 있는 등급중 가장 높은 등급 가져오기
  	************************************************************************ */
  	private String getTopGradeQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  FROM (	\n");
		sql.append("\t      SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t      WHERE GRD.CDHD_ID=?	\n");
		sql.append("\t      ORDER BY CTGO.SORT_SEQ	\n");
		sql.append("\t  ) WHERE ROWNUM=1	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 무료블랙회원인지 알아본다.
  	************************************************************************ */
  	private String getFreeBlackQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT CDHD_ID	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  WHERE CDHD_ID=? AND JOIN_CHNL='3000' AND CDHD_CTGO_SEQ_NO='11' AND ACRG_CDHD_END_DATE>TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* 유료회원종료일자를 어제로 바꿔준다.
  	************************************************************************ */
  	private String getUpdEndDateQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE-1,'YYYYMMDD')	\n");
		sql.append("\t  WHERE CDHD_ID=? AND JOIN_CHNL='3000' AND CDHD_CTGO_SEQ_NO='11' AND ACRG_CDHD_END_DATE>TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
  		return sql.toString();
  	}


    /** ***********************************************************************
    * 회원 업데이트 - 화이트 회원으로
    ************************************************************************ */
    private String getWhiteUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE='',	\n");
 		sql.append("\t		ACRG_CDHD_END_DATE='', 	\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO = '8'	\n");
 		sql.append("\t		WHERE CDHD_ID=?	\n");
        return sql.toString();
    }    


    /** ***********************************************************************
    * 기업골드 이벤트 회원인지 알아본다.
    ************************************************************************ */
    private String getIbkGoldQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT * FROM  BCDBA.TBACRGCDHDLODNTBL	\n");
 		sql.append("\t	WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND PROC_RSLT_CLSS<>'01'	\n");
 		sql.append("\t	AND CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND JUMIN_NO = ?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 기업골드 이벤트 회원인지 알아본다. - 이미사용중 상태로 변경되었지만 혜택을 보지 못한사람들 검색
    ************************************************************************ */
    private String getIbkGoldAgainQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT *	\n");
 		sql.append("\t	FROM BCDBA.TBACRGCDHDLODNTBL IBK	\n");
 		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD MEM ON IBK.PROC_RSLT_CTNT=MEM.CDHD_ID	\n");
 		sql.append("\t	WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND PROC_RSLT_CLSS='01'	\n");
 		sql.append("\t	AND CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND MEM.CDHD_CTGO_SEQ_NO<>'18' AND MONTHS_BETWEEN(ACRG_CDHD_END_DATE, ACRG_CDHD_JONN_DATE) IN (12, 3)	\n");
 		sql.append("\t	AND MEM.JUMIN_NO=?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 기업골드 이벤트 사용한 회원인지 알아본다. - 기업골드회원 테이블을 통해서
    ************************************************************************ */
    private String getUsedIbkGoldQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT *	\n");
 		sql.append("\t	FROM BCDBA.TBACRGCDHDLODNTBL	\n");
 		sql.append("\t	WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND PROC_RSLT_CLSS='01'	\n");
 		sql.append("\t	AND CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND JUMIN_NO=?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 기업골드 이벤트 사용한 회원인지 알아본다. - 회원 테이블을 통해서
    ************************************************************************ */
    private String getUsedIbkGoldMemQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=? AND CDHD_CTGO_SEQ_NO='18'	\n");
 		sql.append("\t	UNION ALL	\n");
 		sql.append("\t	SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=?	\n");
 		sql.append("\t	AND CDHD_CTGO_SEQ_NO<>'18' AND MONTHS_BETWEEN(ACRG_CDHD_END_DATE, ACRG_CDHD_JONN_DATE) NOT IN (14, 5)	\n");
        return sql.toString();
    }
        
    /** ***********************************************************************
    * 기업골드 회원정보 알아본다.
    ************************************************************************ */
    private String getIbkGoldInfoQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT TM_IBK.JUMIN_NO, TM_IBK.RCRU_PL_CLSS	\n");
 		sql.append("\t	FROM  BCDBA.TBACRGCDHDLODNTBL TM_IBK	\n");
 		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON TM_IBK.JUMIN_NO = TB_INFO.SOCID	\n");
 		sql.append("\t	WHERE TM_IBK.SITE_CLSS='02' AND TM_IBK.RCRU_PL_CLSS='4003'	\n");
 		sql.append("\t	AND TM_IBK.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  TM_IBK.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND TM_IBK.JUMIN_NO = ?	\n");
        return sql.toString();
    }
    
 	/** ***********************************************************************
 	* 등급 히스토리 테이블 인서트    
 	************************************************************************ */
 	private String getInsGrdHistoryQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
 		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID	\n");
 		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002'	\n");
 		return sql.toString();
 		
 		
 	}   

    /** ***********************************************************************
    * 기업골드회원 골프회원 테이블 업데이트    
    ************************************************************************ */
    private String getUpdMemIbkGoldQuery(){
       StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD 	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, JOIN_CHNL=?, ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		sql.append("\t  WHERE CDHD_ID=? 	\n");
       return sql.toString();
    }
   

 	/** ***********************************************************************
 	* 등급 히스토리 테이블 인서트    
 	************************************************************************ */
 	private String getUpdGrdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
 		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  WHERE CDHD_GRD_SEQ_NO=(	\n");
 		sql.append("\t      SELECT GRD.CDHD_GRD_SEQ_NO	\n");
 		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t      WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002'	\n");
 		sql.append("\t  )	\n");
 		return sql.toString();
 	} 	
 	
    /** ***********************************************************************
    * 기업골드회원 완료 후 업데이트    
    ************************************************************************ */
    private String getUpdIbkGoldEndQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND JUMIN_NO=?	\n");
        return sql.toString();
    }
     
     /** ***********************************************************************
      * 가입이력 히스토리 인서트 
      ************************************************************************ */
      private String getIbkMemUpdQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("	\n");
        sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
        sql.append("\t	SET ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_END_DATE),2),'YYYYMMDD')	\n");
        sql.append("\t	WHERE CDHD_ID=?	\n");
        return sql.toString();
      }      
     
     
     /** ***********************************************************************
     * 유료회원 종료 기간 가져오기
     ************************************************************************ */
     private String getMemEndQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	SELECT TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY/MM/DD') END_DATE FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
       return sql.toString();
     }                 
      
     /** ***********************************************************************
     * 멤버십 등급이 있는지 알아본다.
     ************************************************************************ */
     private String getMemberShipQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	SELECT CTG.CDHD_CTGO_SEQ_NO	\n");
       sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
       sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTG ON GRD.CDHD_CTGO_SEQ_NO=CTG.CDHD_CTGO_SEQ_NO	\n");
       sql.append("\t	WHERE CDHD_ID=? AND CDHD_SQ1_CTGO='0002'	\n");
       return sql.toString();
     }
     
     /** ***********************************************************************
     * 유료회원 기간 연장용 tm 있는지 확인
     ************************************************************************ */
     private String getExPeriodQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	SELECT ROWNUM RNUM, MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
       sql.append("\t	FROM (	\n");
       sql.append("\t		SELECT MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
       sql.append("\t		FROM BCDBA.TBLUGTMCSTMR	\n");
       sql.append("\t		WHERE TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND CONC_DATE>TO_CHAR(SYSDATE-365,'YYYYMMDD')	\n");
       sql.append("\t		ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
       sql.append("\t	) WHERE ROWNUM=1	\n");
       return sql.toString();
     }
     
     /** ***********************************************************************
     * 골프회원등급관리 테이블 업데이트 - TM 유료회원기간 연장용
     ************************************************************************ */
     private String getGrdUpdTMQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET	\n");
       sql.append("\t	CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT=?	\n");
       sql.append("\t	WHERE CDHD_CTGO_SEQ_NO=? AND CDHD_ID=?	\n");
       return sql.toString();
     }
     
     /** ***********************************************************************
     * 라운지 TM 결과 테이블 상태값 업데이트    
     ************************************************************************ */
     private String getUpdTmQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t  UPDATE BCDBA.TBLUGTMCSTMR SET 	\n");
 		sql.append("\t      TB_RSLT_CLSS='00', SQ2_TCALL_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), REJ_RSON=? 	\n");
 		sql.append("\t  WHERE TB_RSLT_CLSS='01' AND JUMIN_NO=? 	\n");
 		sql.append("\t	AND RND_CD_CLSS='2' AND GOLF_CDHD_GRD_CLSS=? 	\n");
        return sql.toString();
     }
     
     /** ***********************************************************************
     * 회원테이블 정합성 업데이트
     ************************************************************************ */
     private String getMemUpdQuery(int intMemGrade, String upd_name, String upd_mobile, String upd_phone, 
    		 String upd_email, String upd_zipcode, String upd_zipaddr, String upd_detailaddr, String upd_addClss, String upd_grade){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET LASTACCESS = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");

 		sql.append("\t	");
  		if(upd_name.equals("Y")) 		sql.append(" , HG_NM=? ");
  		if(upd_mobile.equals("Y")) 		sql.append(" , MOBILE=? ");
  		if(upd_phone.equals("Y")) 		sql.append(" , PHONE=? ");
  		if(upd_email.equals("Y")) 		sql.append(" , EMAIL=? ");
  		if(upd_zipcode.equals("Y")) 	sql.append(" , ZIP_CODE=? ");
  		if(upd_zipaddr.equals("Y")) 	sql.append(" , ZIPADDR=? ");
  		if(upd_detailaddr.equals("Y"))	sql.append(" , DETAILADDR=? ");
  		if(upd_addClss.equals("Y")) 	sql.append(" , NW_OLD_ADDR_CLSS=? ");
  		if(upd_grade.equals("Y")) 		sql.append(" , CDHD_CTGO_SEQ_NO=? ");
  		
 		if(intMemGrade==4 && !"Y".equals(upd_grade)){
 		sql.append("\t		, ACRG_CDHD_JONN_DATE='', ACRG_CDHD_END_DATE=''	\n");
 		} 
 		
 		sql.append("\t	    WHERE CDHD_ID=?	\n");
        return sql.toString();
     }     
     
     /** ***********************************************************************
     * 회원테이블 정보가져오기
     ************************************************************************ */
     private String getMemSelQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT HG_NM, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, NVL(ZIPADDR, ' ')ZIPADDR, NVL(DETAILADDR, ' ')DETAILADDR, NVL(NW_OLD_ADDR_CLSS, ' ') NW_OLD_ADDR_CLSS	\n");
 		sql.append("\t	, (SELECT CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	        FROM (SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	            FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t	            JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	            WHERE CDHD_ID=?	\n");
 		sql.append("\t	            ORDER BY SORT_SEQ	\n");
 		sql.append("\t	    ) WHERE ROWNUM=1) TOP_CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
 		sql.append("\t	WHERE CDHD_ID=?	\n");
        return sql.toString();
     }          
}

