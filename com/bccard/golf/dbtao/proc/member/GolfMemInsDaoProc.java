/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfMemInsDaoProc extends AbstractProc {

	public static final String TITLE = "회원가입처리";

	public GolfMemInsDaoProc() {}
	




	/** ***********************************************************************
	* 쿠폰정보 update    
	*********************************************************************** */
	public int couponUpExecute(WaContext context, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			
			String site_clss			= data.getString("SITE_CLSS");	
			String evnt_no				= data.getString("CODE_EVNT_NO").trim();
			String code					= data.getString("CODE_NO");	//쿠폰번호
			
			String cupn_type				= data.getString("CUPN_TYPE");//쿠폰구분
			
			//dataSet.setString("CUPN_TYPE", cupn_type);//쿠폰구분 
			
			String use_yn				= "N";
		

			if ( "1".equals(cupn_type)) use_yn= "Y"; //할인쿠폰은 사용여부를 항시 N으로 ..
			else if ( "N".equals(cupn_type)) use_yn= "N";
			
			conn = context.getDbConnection("default", null);	
			String sql = this.getCouponUpQuery();
			pstmt = conn.prepareStatement(sql);
			
			idx = 0;

			pstmt.setString(++idx, use_yn ); 
			pstmt.setString(++idx, site_clss ); 
			pstmt.setString(++idx, evnt_no ); 
			pstmt.setString(++idx, code );
			
			result = pstmt.executeUpdate();

            if(pstmt != null) pstmt.close();

		} catch(Exception e) {
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;

	}


	/** ***********************************************************************
	* 알아보기    
	*********************************************************************** */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		int insJoinChnlHistoryResult = 0;		// 회원가입경로 히스토리 인서트 처리결과
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		ResultSet rs4 = null;
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String payType 				= data.getString("payType").trim();		// 1:카드 2:카드+포인트
			String moneyType 			= data.getString("moneyType").trim();	// 1:챔피온(200,000) 2:블루(50,000) 3:골드(25,000)
			if(GolfUtil.empty(moneyType)){moneyType = "4";}
			//String memType 				= data.getString("memType").trim();		// 회원구분 - 정회원 : 1 비회원:2
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			if(GolfUtil.empty(socId)){
				socId = userEtt.getVrtlJuminNo();
			}
			String insType				= data.getString("insType").trim();		// 유입경로 - TM : 1 일반 : ""
			String payWay				= data.getString("payWay").trim();		// yr:연회비, mn:연회비
			String sttl_amt				= data.getString("STTL_AMT").trim();	// 결제금액
//			String paySort				= data.getString("paySort").trim();		// 회원업그레이드 구분 all:전액결제, half:반액결제
			String code					= data.getString("CODE_NO");			//제휴처코드
			String join_chnl			= data.getString("JOIN_CHNL");	
			if(GolfUtil.empty(join_chnl)){join_chnl="0001";}
			
			String vipCardYn			= data.getString("vipCardYn");	
			if("Y".equals(vipCardYn)){join_chnl="3003";} 

			String memEmail				= "";	// 이메일
			String memZipCode			= "";	// 우편번호
			String memZipAddr			= "";	// 주소
			String memDetailAddr		= "";	// 상세주소
			String memMobile			= "";	// 핸드폰번호
			String memPhone				= "";	// 전화번호
			
			String strMemClss			= userEtt.getStrMemChkNum();		// 회원Clss  2009.10.30 추가 .getStrMemChkNum()
			
			System.out.print("## GolfMemInsDaoProc | memId : "+memId+" | strMemClss : "+strMemClss+"\n");
			if(!"5".equals(strMemClss)) strMemClss ="1";
			
			/*
			//유효한 제휴처코드인지 체크
			DbTaoResult codeCheck = this.codeExecute(context, data, request);
			debug("===================codeCheck : " + codeCheck);
			if (codeCheck != null && codeCheck.isNext()) {
				codeCheck.first();
				codeCheck.next();
				debug("===================memGrade : " + codeCheck.getString("RESULT"));
				if(codeCheck.getString("RESULT").equals("00")){
					code = (String) codeCheck.getString("CUPN_NO");	
				} else {
					code ="";
				}
			} else {
				code ="";
			}*/
			
			String cdhd_SQ1_CTGO		= "0002";				// cdhd_SQ1_CTGO -> 0001:골프카드고객 0002:멤버쉽고객
			String cdhd_SQ2_CTGO		= GolfUtil.lpad(moneyType+"", 4, "0");	// cdhd_SQ2_CTGO -> 0001:챔피온 0002:블루 0003:골드 0004:일반
			
			String cdhd_CTGO_SEQ_NO = "";
			String sece_yn = "";
			
			debug("GolfMemInsDaoProc / payType : " + payType + " / moneyType : " + moneyType + " / insType : " + insType);
								
			
			if(!"".equals(cdhd_SQ2_CTGO) && cdhd_SQ2_CTGO != null)
			{						
			
				sql = this.getMemberLevelQuery(); 
	            pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, cdhd_SQ1_CTGO );
	        	pstmt.setString(2, cdhd_SQ2_CTGO );
	            rs = pstmt.executeQuery();	
				if(rs.next()){
					cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            if(!"".equals(cdhd_CTGO_SEQ_NO) && cdhd_CTGO_SEQ_NO != null)
	            {
	            	  //debug("===GolfMemInsDaoProc=======01. 이미 등록된 회원인지 알아본다. (등록되어 있는 아이디가 있는지 검색)======");
					sql = this.getMemberedCheckQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, socId );
		        	pstmt.setString(2, memId );
		            rs = pstmt.executeQuery();	
					if(!rs.next()){
						// 신규회원

						// 2009.12.14 추가 : 개인 정보를 가져와서 넣어준다.
						sql = this.getUserInfoQuery(strMemClss);  	// 회원등급번호 1:개인 / 5:법인
			            userInfoPstmt = conn.prepareStatement(sql);
			            userInfoPstmt.setString(1, memId );
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
						
						debug("===GolfMemInsDaoProc=======00. 새로운 회원============================");
						debug("===GolfMemInsDaoProc=======140. 골프회원정보에 인서트 - TBGGOLFCDHD ");	            
			            sql = this.getInsertMemQuery(moneyType);
						pstmt = conn.prepareStatement(sql);
						
			        	pstmt.setString(++idx, memId ); 
			        	pstmt.setString(++idx, memNm );
			        	pstmt.setString(++idx, socId );
						pstmt.setString(++idx, join_chnl );
						
						pstmt.setString(++idx, code ); //제휴처코드 입력시 삽입
						pstmt.setString(++idx, strMemClss ); 	// 회원등급번호 1:개인 / 5:법인   2009.10.30 추가
						
						pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
						pstmt.setString(++idx, memMobile );
						pstmt.setString(++idx, memPhone );
						pstmt.setString(++idx, memEmail );
						pstmt.setString(++idx, memZipCode );
						pstmt.setString(++idx, memZipAddr );
						pstmt.setString(++idx, memDetailAddr );
						result = pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
			  
						debug("===GolfMemInsDaoProc=======152. 골프회원등급관리 인서트 - TBGGOLFCDHDGRDMGMT");
			            // 2009-07-29 같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
						sql = this.getChkGradeQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, memId ); 
			        	pstmt.setString(2, cdhd_CTGO_SEQ_NO );
			            rs = pstmt.executeQuery();	
						if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0")){
			            
				            /**SEQ_NO 가져오기**************************************************************/
							sql = this.getNextValQuery(); 
				            pstmt = conn.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long max_seq_no = 0L;
							if(rs.next()){
								max_seq_no = rs.getLong("SEQ_NO");
							}
							if(rs != null) rs.close();
				            if(pstmt != null) pstmt.close();
				            
				            /**Insert************************************************************************/
			
				            sql = this.getInsertGradeQuery();
							pstmt = conn.prepareStatement(sql);
							
							idx = 0;
				        	pstmt.setLong(++idx, max_seq_no ); 
				        	pstmt.setString(++idx, memId ); 
				        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				        	
							result = pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();
				            
				            // 월결제 일경우 
				            if("mn".equals(payWay)){
					            mnInsExecute(context, data, request, "ins", "");
				            }
				            
				            
						}
						if(rs != null) rs.close();
			            if(pstmt != null) pstmt.close();
		            
					

						// 신규회원 처리 종료
					}else{
						// 등록된 아이디가 있는경우 처리 시작

						sece_yn = rs.getString("SECE_YN");
						
						if(sece_yn.equals("Y")){
							// 현재 탈퇴 -> 재가입 처리 시작
							// 2009.12.14 추가 : 개인 정보를 가져와서 넣어준다.
							sql = this.getUserInfoQuery(strMemClss); 
				            userInfoPstmt = conn.prepareStatement(sql);
				            userInfoPstmt.setString(1, memId );
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
				            
				            // 20100409 가입경로 히스토리 인서트
				            insJoinChnlHistoryResult = insJoinChnlHistoryExecute(context, data, request, conn);
				            
				            sql = this.getReInsertMemQuery(moneyType);
							pstmt = conn.prepareStatement(sql);	
							pstmt.setString(++idx, join_chnl );
				        	pstmt.setString(++idx, code );

							pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
							pstmt.setString(++idx, memMobile );
							pstmt.setString(++idx, memPhone );
							pstmt.setString(++idx, memEmail );
							pstmt.setString(++idx, memZipCode );
							pstmt.setString(++idx, memZipAddr );
							pstmt.setString(++idx, memDetailAddr );
							
							pstmt.setString(++idx, memId ); 
							
				        	
							result = pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();

							//debug("===GolfMemInsDaoProc=======재가입 회원 : 등급테이블 - 회원분류일련번호 업데이트");
				            sql = this.getMemberGradeUpdateQuery();
							pstmt = conn.prepareStatement(sql);
							
							idx = 0;
				        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				        	pstmt.setString(++idx, memId ); 
				        	
							result = pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();

				            // 월결제 일경우 
				            if("mn".equals(payWay)){
					            mnInsExecute(context, data, request, "ins", "");
				            }
				            
				            // 현재 탈퇴 -> 재가입 처리 종료
						}else{
							// 탈퇴가 아닌회원 처리 시작 -> 업그레이드, 유료회원전화, 재가입
							sql = this.getMeCheckQuery(); 
				            pstmt = conn.prepareStatement(sql);
				        	pstmt.setString(1, memId );
				            rs4 = pstmt.executeQuery();	
				            int intchks = 0;
				            
				            
				            if(!rs4.next()){
				            	// 현재 아이디가 등록되어 있지 않은 경우 처리 시작(카드 회원일 경우?)
				            	
				            	intchks = Integer.parseInt(rs4.getString("CDHD_CTGO_SEQ_NO")); //9,10:골프카드회원
				            	if(intchks > 8){
				            		
				            		// 2009-07-29 같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
				        			sql = this.getChkGradeQuery(); 
				                    pstmt = conn.prepareStatement(sql);
				                	pstmt.setString(1, memId ); 
				                	pstmt.setString(2, cdhd_CTGO_SEQ_NO );
				                    rs = pstmt.executeQuery();	
				        			if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0"))
				        			{
				        				// 등급추가
				        	            /**SEQ_NO 가져오기**************************************************************/
				        				sql = this.getNextValQuery(); 
				        	            pstmt = conn.prepareStatement(sql);
				        	            rs = pstmt.executeQuery();			
				        				long max_seq_no = 0L;
				        				if(rs.next()){
				        					max_seq_no = rs.getLong("SEQ_NO");
				        				}
				        				if(rs != null) rs.close();
				        	            if(pstmt != null) pstmt.close();
				        	            
				        	            /**Insert************************************************************************/            
				        	            sql = this.getInsertGradeQuery();
				        				pstmt = conn.prepareStatement(sql);			
				        				idx = 0;
				        	        	pstmt.setLong(++idx, max_seq_no ); 
				        	        	pstmt.setString(++idx, memId ); 
				        	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				        	        	
				        				result = pstmt.executeUpdate();
				        	            if(pstmt != null) pstmt.close();
				        	            
				                    
				        			}
					            	// 현재 아이디가 등록되어 있는 경우 처리 종료(카드 회원일 경우?)
				            	}else{
					            	// 현재 아이디가 등록되어 있지만 카드회원이 아닌경우  처리 시작(카드 회원일 경우?)
				            				            		
				            		// 현재 멤버십 등급이 있는지 확인
				            		sql = this.getMembershipGradeQuery();
				            		pstmt = conn.prepareStatement(sql);
				            		pstmt.setString(1, memId);
				            		rs = pstmt.executeQuery();
				            		
				            		if(rs.next()){
				                    	int grade_seq = rs.getInt("GRADE_SEQ");
				                    	
				                    	if(grade_seq > 0){
				                    		//debug("===GolfMemInsDaoProc======= 히스토리 인서트");
				        					/**SEQ_NO 가져오기**************************************************************/
				        					sql = this.getMaxHistoryQuery(); 
				        		            pstmt = conn.prepareStatement(sql);
				        		            rs = pstmt.executeQuery();			
				        					long max_seq_no = 0L;
				        					if(rs.next()){
				        						max_seq_no = rs.getLong("MAX_SEQ_NO");
				        					}
				        					if(rs != null) rs.close();
				        		            if(pstmt != null) pstmt.close();
				        		            
				        		            /**Insert************************************************************************/
				        		            sql = this.setHistoryQuery();
				        					pstmt = conn.prepareStatement(sql);
				        					
				        					idx = 0;
				        					pstmt.setLong(++idx, max_seq_no );	// 히스토리 테이블 일련번호
				        					pstmt.setLong(++idx, grade_seq );	// 골프회원등급관리 테이블 일련번호
				        					
				        					int result2 = pstmt.executeUpdate();
				        					
				        					if(result2 > 0){
				        		            
				        						//debug("===GolfMemInsDaoProc======= 등급 업데이트");
				        	        			idx = 0;
				        	        			sql = this.updGradeQuery();
				        	        			pstmt = conn.prepareStatement(sql);
				        	        			
				        	        			pstmt.setString(++idx,  cdhd_CTGO_SEQ_NO);
				        	        			pstmt.setInt(++idx,  grade_seq);
				        	        			
				        	        			result = pstmt.executeUpdate();
				        	        			
				        	        			if(result > 0){

				        	        				// 멤버 테이블 멤버십 유료회원 유료회원 기간, 업데이트
				        		            		if(cdhd_CTGO_SEQ_NO.equals("5") || cdhd_CTGO_SEQ_NO.equals("6") || cdhd_CTGO_SEQ_NO.equals("7") || cdhd_CTGO_SEQ_NO.equals("11") || cdhd_CTGO_SEQ_NO.equals("17")){
				        			            				        			            		
				        			            		// 회원테이블 가입경로 히스토리 저장
				        		            			insJoinChnlHistoryResult = insJoinChnlHistoryExecute(context, data, request, conn);
				        				
				        					            // 회원테이블 업데이트 / cdhd_CTGO_SEQ_NO=17 : e-champ 회원은 유료기간을 3개월로 한다.
				        								int iindex = 0;
				        			            		sql = this.getMemberUpdateQuery(code, cdhd_CTGO_SEQ_NO);
				        								pstmt = conn.prepareStatement(sql);
				        								// 특별한 가입경로가 있을 경우 업데이트 해준다.
				        								if (!"".equals(code)) {
				        									pstmt.setString(++iindex, code );
				        								}
			        									pstmt.setString(++iindex, join_chnl );
				        								pstmt.setString(++iindex, cdhd_CTGO_SEQ_NO );
				        								pstmt.setString(++iindex, socId );
				        								pstmt.setString(++iindex, memId );		        								
				        								pstmt.executeUpdate();
				        					            if(pstmt != null) pstmt.close();
				        					            
				        					            if("mn".equals(payWay)){
				        					            	// 월결제 일경우 신청테이블 월결제 내역 저장하기
				        						            mnInsExecute(context, data, request, "ins", "");
				        					            }else{
				        					            	// 월결제가 아닐경우, 신청테이블에 진행중인 월결제 내역이 있을 경우 종료 시킨다 => 월결제 회원이 연결제 회원으로 재가입할 경우
				        						            mnDelExecute(context, data, request, conn);
				        					            }
				        		            		}
				        	        			}
				        					}
				                    		
				                    	}
				            		}
				            		// 현재 아이디가 등록되어 있지만 카드회원이 아닌경우  처리 종료(카드 회원일 경우?)	
				            	}
				            	// 현재 아이디가 등록되어 있지 않은 경우 처리 종료(카드 회원일 경우?)
				            }else{
			            		// 현재 아이디가 등록되어 있는경우 처리 시작 (재가입)
			            		
			            		// 현재 멤버십 등급이 있는지 확인
			            		sql = this.getMembershipGradeQuery();
			            		pstmt = conn.prepareStatement(sql);
			            		pstmt.setString(1, memId);
			            		rs = pstmt.executeQuery();
			            		
			            		if(rs.next()){
			            			// 멤버십 등급이 있는경우 처리 시작
			                    	int grade_seq = rs.getInt("GRADE_SEQ");
			                    	if(grade_seq > 0){
			                    		
			                    		// 등급변경전 히스토리 인서트
			        					/**SEQ_NO 가져오기**************************************************************/
			        					sql = this.getMaxHistoryQuery(); 
			        		            pstmt = conn.prepareStatement(sql);
			        		            rs = pstmt.executeQuery();			
			        					long max_seq_no = 0L;
			        					if(rs.next()){
			        						max_seq_no = rs.getLong("MAX_SEQ_NO");
			        					}
			        					if(rs != null) rs.close();
			        		            if(pstmt != null) pstmt.close();
			        		            
			        		            /**Insert************************************************************************/
			        		            sql = this.setHistoryQuery();
			        					pstmt = conn.prepareStatement(sql);
			        					
			        					idx = 0;
			        					pstmt.setLong(++idx, max_seq_no );	// 히스토리 테이블 일련번호
			        					pstmt.setLong(++idx, grade_seq );	// 골프회원등급관리 테이블 일련번호
			        					
			        					int result2 = pstmt.executeUpdate();
			        					if(result2 > 0){     		            
			        						
			        						// 등급 관리테이블 등급 업데이트
			        	        			idx = 0;
			        	        			sql = this.updGradeQuery();
			        	        			pstmt = conn.prepareStatement(sql);
			        	        			
			        	        			pstmt.setString(++idx,  cdhd_CTGO_SEQ_NO);
			        	        			pstmt.setInt(++idx,  grade_seq);
			        	        			result = pstmt.executeUpdate();
			        	        			
			        	        			if(result > 0){
			        	        				
			        	        				// 유료회원만 
			        	        				if(cdhd_CTGO_SEQ_NO.equals("5") || cdhd_CTGO_SEQ_NO.equals("6") || cdhd_CTGO_SEQ_NO.equals("7") || cdhd_CTGO_SEQ_NO.equals("11") || cdhd_CTGO_SEQ_NO.equals("17")){
			        			            		
			        			            		insJoinChnlHistoryResult = insJoinChnlHistoryExecute(context, data, request, conn);
			        				
			        					            // 회원테이블 업데이트
			        			            		sql = this.getMemberUpdateQuery(code, cdhd_CTGO_SEQ_NO);
			        								pstmt = conn.prepareStatement(sql);

			        								int iindex = 0;
			        								if (!"".equals(code)) {
			        									pstmt.setString(++iindex, code );
			        								}
		        									pstmt.setString(++iindex, join_chnl );
			        								pstmt.setString(++iindex, cdhd_CTGO_SEQ_NO );
			        								pstmt.setString(++iindex, socId );
			        								pstmt.setString(++iindex, memId );	        				        	
			        								result = pstmt.executeUpdate();
			        					            if(pstmt != null) pstmt.close();

			        					            // 월결제 일경우 
			        					            if("mn".equals(payWay)){
			        					            	mnInsExecute(context, data, request, "ins", "");
			        					            }else{
			        					            	// 월결제가 아닐경우, 신청테이블에 진행중인 월결제 내역이 있을 경우 종료 시킨다 => 월결제 회원이 연결제 회원으로 재가입할 경우
			        						            mnDelExecute(context, data, request, conn);
			        					            }
			        		            		}
			        	        			}
			        					}
			                    		
			                    	}

			            			// 멤버십 등급이 있는경우 처리 종료
			            		}else{
			            			// 멤버십 등급이 없는 경우 처리 시작 (카드 회원 등급 업데이트)
		    						sql = this.getMemberShipUpdateQuery();
		    						pstmt = conn.prepareStatement(sql);
		    			        	pstmt.setString(1, cdhd_CTGO_SEQ_NO );
		    			        	pstmt.setString(2, memId );
		    			        	
		    						result = pstmt.executeUpdate();
				            		
				            		// 2009-07-29 같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
				        			sql = this.getChkGradeQuery(); 
				                    pstmt = conn.prepareStatement(sql);
				                	pstmt.setString(1, memId ); 
				                	pstmt.setString(2, cdhd_CTGO_SEQ_NO );
				                    rs = pstmt.executeQuery();	
				        			if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0"))
				        			{
				        				// 등급추가
				        	            /**SEQ_NO 가져오기**************************************************************/
				        				sql = this.getNextValQuery(); 
				        	            pstmt = conn.prepareStatement(sql);
				        	            rs = pstmt.executeQuery();			
				        				long max_seq_no = 0L;
				        				if(rs.next()){
				        					max_seq_no = rs.getLong("SEQ_NO");
				        				}
				        				if(rs != null) rs.close();
				        	            if(pstmt != null) pstmt.close();
				        	            
				        	            /**Insert************************************************************************/            
				        	            sql = this.getInsertGradeQuery();
				        				pstmt = conn.prepareStatement(sql);			
				        				idx = 0;
				        	        	pstmt.setLong(++idx, max_seq_no ); 
				        	        	pstmt.setString(++idx, memId ); 
				        	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				        	        	
				        				result = pstmt.executeUpdate();
				        	            if(pstmt != null) pstmt.close();
				        			}

			            			// 멤버십 등급이 없는 경우 처리 시작 (카드 회원 등급 업데이트)
			            		}
			            		// 현재 아이디가 등록되어 있는경우 처리 종료(재가입)
			            	}
							
				            if(pstmt != null) pstmt.close();
				            
							// 탈퇴가 아닌회원 처리 시작 -> 업그레이드, 유료회원전화, 재가입
						}
						if(rs != null) rs.close();
						if(rs4 != null) rs4.close();
			            if(pstmt != null) pstmt.close();
							
			            // 등록된 아이디가 있는경우 처리 종료
					}
	        
	            
	            
	            }
	            
			}
			
			
				
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
						
			
			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(userInfoRs != null) userInfoRs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** ***********************************************************************
	* 알아보기    
	*********************************************************************** */
	public int cardJoinExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String moneyType = "4";
			String memId				= userEtt.getAccount();
						
			String cdhd_SQ1_CTGO		= "0002";				// cdhd_SQ1_CTGO -> 0001:골프카드고객 0002:멤버쉽고객
			String cdhd_SQ2_CTGO		= GolfUtil.lpad(moneyType+"", 4, "0");	// cdhd_SQ2_CTGO -> 0001:VIP 0002:골드 0003:우량 0004:일반
			
			String cdhd_CTGO_SEQ_NO = "";
																	            
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");		// White : 8
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            
         // 2009-07-29 같은 아이디 같은 레벨은 다시 등록되지 않도록 막는다.
			sql = this.getChkGradeQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, memId ); 
        	pstmt.setString(2, cdhd_CTGO_SEQ_NO );
            rs = pstmt.executeQuery();	
			if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0"))
			{
            
            
	            //등급추가
	            /**SEQ_NO 가져오기**************************************************************/
				sql = this.getNextValQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				long max_seq_no = 0L;
				if(rs.next()){
					max_seq_no = rs.getLong("SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            /**Insert************************************************************************/            
	            sql = this.getInsertGradeQuery();
				pstmt = conn.prepareStatement(sql);			
				idx = 0;
	        	pstmt.setLong(++idx, max_seq_no ); 
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
	        	
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
            
			}else
			{
				result = 8;
			}
					
				
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
			
			debug("===cardJoinExecute=======가입 완료 ===============");
			
			
			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		
		
		return result;
	}
	

	/** ***********************************************************************
	* 등급 알아보기    
	*********************************************************************** */
	public DbTaoResult reJoinExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		//DbTaoResult result = null;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		String title = "";

		DbTaoResult result =  new DbTaoResult(title);
				
		try {
	
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			String memId = "";

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt!=null){
				memId				= userEtt.getAccount();
			}
			String charge_mem = "";
			String join_re = "";
			
			sql = this.getReJoinViewQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, memId );
            rs = pstmt.executeQuery();	
			if(rs != null) {
				while(rs.next())  {

					charge_mem = rs.getString("CHARGE_MEM");
					join_re = rs.getString("JOIN_RE");

					if(charge_mem.equals("Y") && join_re.equals("N")){
						debug("=============>GolfLoginLogInsProc --->  한달이내에 탈퇴한 유료회원일 경우 재가입불가	 => 03");
						result.addString("RESULT", "Y");
					}else{
						result.addString("RESULT", "N");
					}

					result.addString("memGrade", rs.getString("GOLF_CMMN_CODE_NM"));
					result.addInt("intMemGrade", rs.getInt("GOLF_CMMN_CODE"));
					result.addString("join_date", rs.getString("JOIN_DATE"));
					result.addString("upd_pay", rs.getString("UPD_PAY"));
					result.addString("join_chnl", rs.getString("JOIN_CHNL")); 
				}
			}
			
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

	return result;
	}

	/** ***********************************************************************
	* 등급 알아보기     
	*********************************************************************** */
	public DbTaoResult gradeExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			String moneyType = data.getString("moneyType");
			moneyType = GolfUtil.lpad(moneyType,4,"0");
		
			String sql = this.getMemGradeQuery(); 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, moneyType);
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("memGrade" 	,rs.getString("MEM_GRADE") );
					result.addInt("intMemGrade" 	,rs.getInt("INT_MEM_GRADE") );
					result.addInt("anl_fee" 		,rs.getInt("ANL_FEE") );
					result.addString("RESULT", "00"); //정상결과
					
					debug("MEM_GRADE : " + rs.getString("MEM_GRADE"));
					debug("INT_MEM_GRADE : " + rs.getInt("INT_MEM_GRADE"));
				}
			}else{
				result.addString("RESULT", "01");	
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	
	
	/** ***********************************************************************
	* 제휴코드 등록  알아보기    
	************************************************************************ */
	public DbTaoResult codeExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			int intMemGrade = userEtt.getIntMemGrade();
			
			String code = data.getString("CODE").trim();
			String formtarget = data.getString("formtarget");	// 재가입 : Coupon / 업그레이드 팝업 : upgradePop / 연장하기 : Extend
			
			debug("intMemGrade : " + intMemGrade + " / code : " + code + " / formtarget : " + formtarget);
			
			if(intMemGrade!=13){
				debug("aaa");
				if(code.equals("EVENTECHAMP201007") || code.equals("EVENTLETTER08")){
					debug("bbb");
					if(formtarget.equals("upgradePop") || formtarget.equals("Extend")){
						debug("ccc");
						
					}
				}
				
			}
			
			if(intMemGrade!=13 && (code.equals("EVENTECHAMP201007") || code.equals("EVENTLETTER08")) && ((formtarget.equals("upgradePop") || formtarget.equals("Extend")))){
				
				// e-champ 멤버십 연회비 10%할인 쿠폰은 IBK기업은행 Gold 회원만 사용가능

				result.addString("RESULT", "05"); //사용여부인것
				result.addString("RESULT_MESSAGE", "유효한 번호가 아닙니다"); //유효기간 초과
				
			}else{

				if ( !"".equals(code))
				{
					conn = context.getDbConnection("default", null);
				
					String sql = this.getMemCodeQuery(); 
					pstmt = conn.prepareStatement(sql);
					int idx = 0;
					pstmt.setString(++idx, data.getString("SITE_CLSS").trim());
					pstmt.setString(++idx, data.getString("EVNT_NO").trim());
					pstmt.setString(++idx, data.getString("EVNT_NO2"));
					pstmt.setString(++idx, code);
	
					rs = pstmt.executeQuery();			
					
					int i = 0; 
					if(rs != null) {
						while(rs.next())  {
							++i ;
							result.addString("CUPN_CTNT" 			,rs.getString("CUPN_CTNT") );
							result.addString("CUPN_NO" 				,rs.getString("CUPN_NO") );
							result.addInt("CUPN_AMT" 				,rs.getInt("CUPN_AMT") );
							result.addString("EVNT_NO" 				, rs.getString("EVNT_NO") );
							result.addString("CUPN_AMT_FMT" 		,GolfUtil.comma(rs.getString("CUPN_AMT")) );
							result.addString("CUPN_CLSS" 			, rs.getString("CUPN_CLSS") ); //01: 할인율 02: 할인금액 03:무료
							result.addString("CUPN_PYM_YN" 			, rs.getString("CUPN_PYM_YN") );
							result.addInt("CUPN_USE_NUM" 			, rs.getInt("CUPN_USE_NUM") );
							result.addString("CUPN_VALD_STRT_DATE" 	, rs.getString("CUPN_VALD_STRT_DATE") );
							result.addString("CUPN_VALD_END_DATE" 	, rs.getString("CUPN_VALD_END_DATE") );
							result.addString("TODATE" 				, rs.getString("TODATE") );
							result.addString("CUPN_TYPE" 			, rs.getString("CUPN_TYPE") );
							result.addString("PMGDS_PYM_YN" 		, rs.getString("PMGDS_PYM_YN") );
		
					
							debug("CUPN_CTNT : " + rs.getString("CUPN_CTNT"));
							debug("CUPN_AMT : " + rs.getInt("CUPN_AMT"));
							/* 사용여부 판단 */
							// 사용여부가 N:미지급 인것
							if (  "N".equals(rs.getString("CUPN_PYM_YN")) )
							{
								// 유효기간 체크
								if (Double.parseDouble(rs.getString("TODATE")) >= Double.parseDouble(rs.getString("CUPN_VALD_STRT_DATE")) &&
									Double.parseDouble(rs.getString("TODATE")) <= Double.parseDouble(rs.getString("CUPN_VALD_END_DATE")) )
								{
									
									//사용횟수 체크
									if (rs.getInt("CUPN_USE_NUM") <= 0 )
									{
										result.addString("RESULT", "04"); //사용횟수 초과
										result.addString("RESULT_MESSAGE", "이미 사용한 번호 입니다"); //유효기간 초과
									} else {
										result.addString("RESULT", "00"); //정상결과
									}
	
	
								} else {
									result.addString("RESULT", "03"); //유효기간 초과
									result.addString("RESULT_MESSAGE", "유효한 기간이 아닙니다"); //유효기간 초과
								}
							} else {
								result.addString("RESULT", "05"); //사용여부인것
								result.addString("RESULT_MESSAGE", "유효한 번호가 아닙니다"); //유효기간 초과
							}
							/* 사용여부 판단 */
							
						}
						if (i == 0) result.addString("RESULT", "01");	
						
					}else{
						result.addString("RESULT", "02");	
					}
				} else {
					result.addString("RESULT", "02");	
				}
			}
			

			//debug("RESULT : " + result.getString("RESULT")); 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	


	/** ***********************************************************************
	* 월결제 저장하기    
	*********************************************************************** */
	public int mnInsExecute(WaContext context, TaoDataSet data, HttpServletRequest request, String memSort, String paySort) throws BaseException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		
		// memSort => ins:신규등록, upd:업데이트 / paySort => all:전액, half:반액

		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String sql = "";
			int idx = 0;

			String memId				= userEtt.getAccount();
			String sttl_amt				= data.getString("STTL_AMT").trim();		// 결제금액

            sql = this.getPayMonthQuery(memSort, paySort);
			pstmt = conn.prepareStatement(sql);
			idx = 0;
			
			if("ins".equals(memSort)){
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, sttl_amt );
			}else{
	        	pstmt.setString(++idx, sttl_amt );
	        	pstmt.setString(++idx, memId ); 
			}
		
			
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			

			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}

		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	

	/** ***********************************************************************
	* 월결제 종료하기    
	*********************************************************************** */
	public int mnDelExecute(WaContext context, TaoDataSet data, HttpServletRequest request, Connection conn) throws BaseException {

		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int result =  0;
		

		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			String socId = userEtt.getSocid();
			int idx = 0;
			String pgrs_yn = "N";		// 진행상태 - 종료
			String aplc_seq_no = "";	// 신청테이블 일련번호
			String tb_rslt_clss = "03";	// TM 테이블 진행상태 
			
			
			// 신청테이블에 진행중인 월결제 내역이 있는지 검색한다.
			pstmt = conn.prepareStatement(getSelMonthMemQuery());	
        	pstmt.setString(1, socId );
			rs = pstmt.executeQuery();			
			
			if(rs != null && rs.next()) {
				
				aplc_seq_no = rs.getString("APLC_SEQ_NO");

				// 신청테이블 상태 변경
				pstmt = conn.prepareStatement(getUpdQuery());
				idx = 0;
				pstmt.setString(++idx, pgrs_yn );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
				
				// TM 테이블 상태 변경 
				pstmt = conn.prepareStatement(getTmUpdQuery());
				idx = 0;
				pstmt.setString(++idx, tb_rslt_clss );
				pstmt.setString(++idx, socId );
				result += pstmt.executeUpdate();
			}
			
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			

		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	

	/** ***********************************************************************
	* 아이핀 회원 등록하기    
	*********************************************************************** */
	public int ipinExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		

		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String sql = "";
			int idx = 0;

			String memId				= userEtt.getAccount();
			String jumin_no				= data.getString("jumin_no");

            sql = this.getMemberedCheckQuery();
			pstmt = conn.prepareStatement(sql);	
        	pstmt.setString(1, memId );
        	pstmt.setString(2, jumin_no );		

			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				// 업데이트
	            sql = this.getJuminUpdateQuery();
				pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(++idx, jumin_no ); 
	        	pstmt.setString(++idx, memId );
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
			}

			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}

		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	

	/** ***********************************************************************
	* 가입경로 히스토리 인서트
	*********************************************************************** */
	public int insJoinChnlHistoryExecute(WaContext context, TaoDataSet data, HttpServletRequest request, Connection conn) throws BaseException {

		PreparedStatement pstmt = null;
		int result =  0;
		
		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			String sql = "";
			String memId = userEtt.getAccount();
			int idx = 0;

            sql = this.getJoinChnlHistoryInsQuery();
			pstmt = conn.prepareStatement(sql);	
	        pstmt.setString(++idx, memId ); 
			result = pstmt.executeUpdate();
			//debug("가입경로 히스토리 인서트 result : " + result);


		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	
	
 	/** ***********************************************************************
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD		\n");
		sql.append("\t  WHERE JUMIN_NO=? AND CDHD_ID=?				\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 현재등록된 아이디인지 알아보기    
	************************************************************************ */
	private String getMeCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT T2.CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD T1 JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t  WHERE T1.CDHD_ID=?		\n");
		sql.append("\t  ORDER BY T2.CDHD_CTGO_SEQ_NO DESC				\n");
		return sql.toString();
	}

   	/** ***********************************************************************
    * 회원 분류 정보 가져오기 - TBGGOLFCDHDCTGOMGMT    
    ************************************************************************ */
    private String getMemberLevelQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("\n");
      sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM					\n");
      sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
      sql.append("\t    WHERE CDHD_SQ1_CTGO=? AND CDHD_SQ2_CTGO=?	\n");
      return sql.toString();
    }
              
    /** ***********************************************************************
    * 골프회원정보에 인서트 - TBGGOLFCDHD    
    ************************************************************************ */
    private String getInsertMemQuery(String moneyType){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT										\n");
		sql.append("\t  		, AFFI_FIRM_NM, MEMBER_CLSS													\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");
		
		if(moneyType.equals("1") || moneyType.equals("2") || moneyType.equals("3") || moneyType.equals("7") || moneyType.equals("11")){
			sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')	\n");
		}else if(moneyType.equals("12")){
			sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD')	\n");
		}else{
			sql.append("\t  		, '', ''																\n");
		}
		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', ?							\n");
		sql.append("\t  		, 0, 0																		\n");
		sql.append("\t  		, ?	,?																		\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
    * 골프회원정보에 업데이트 - TBGGOLFCDHD => 재가입    
    ************************************************************************ */
    private String getReInsertMemQuery(String moneyType){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t          UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");

		if(moneyType.equals("1") || moneyType.equals("2") || moneyType.equals("3") || moneyType.equals("7") || moneyType.equals("11")){
			sql.append("\t          , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')			\n");
			sql.append("\t          , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')		\n");
		}else if(moneyType.equals("12")){
			sql.append("\t          , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')			\n");
			sql.append("\t          , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD')		\n");
		}else{
			sql.append("\t          , ACRG_CDHD_JONN_DATE=''			\n");
			sql.append("\t          , ACRG_CDHD_END_DATE=''		\n");
		}
		
		sql.append("\t          , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
		sql.append("\t          , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
		sql.append("\t          , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y'		\n");
		sql.append("\t          , JOIN_CHNL= ? , AFFI_FIRM_NM= ?		\n");
		sql.append("\t          , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
		sql.append("\t          , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		
		sql.append("\t          WHERE CDHD_ID=?		\n");
        	
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
    * 같은 등급이 등록되어 있는지 확인    
    ************************************************************************ */
    private String getChkGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
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
    * 회원 업데이트 - 유료회원으로    
    ************************************************************************ */
    private String getMemberUpdateQuery(String code, String grd_seq){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET												\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')							\n");
 		
 		if(grd_seq.equals("17")){
 			sql.append("\t		, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD')			\n");
 		}else{
 			sql.append("\t		, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')			\n");
 		}
		
		if (!"".equals(code)) {
			sql.append("\t      , AFFI_FIRM_NM= ?		\n");
		}
		
 		sql.append("\t		, JOIN_CHNL= ?, CDHD_CTGO_SEQ_NO = ?	\n");
 		sql.append("\t		WHERE JUMIN_NO=? AND CDHD_ID=?	\n");
        return sql.toString();
    }
       
    /** ***********************************************************************
    * 회원 등급관리 업데이트 - 유료회원으로    
    ************************************************************************ */
    private String getMemberGradeUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET					\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO=?								\n");
 		sql.append("\t		WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_SQ1_CTGO='0002')	\n");
        return sql.toString();
    }
       
    /** ***********************************************************************
    * 탈퇴한지 한달이 안된 유료회원 파악하기+ 회원등급, 등급명 가져오기 
    ************************************************************************ */
    private String getReJoinViewQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T1.CDHD_ID, ACRG_CDHD_END_DATE, SUBSTR(SECE_ATON,1,8) AS SECE_ATON	\n");
		sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(SECE_ATON,1,8)),1),'YYYYMMDD') AS SECE_ADD_MONTH	\n");
		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDD') AS NOW_DATE	\n");
		sql.append("\t	, CASE WHEN ACRG_CDHD_END_DATE>SUBSTR(SECE_ATON,1,8) THEN 'Y' ELSE 'N' END AS CHARGE_MEM	\n");
		sql.append("\t	, CASE WHEN ADD_MONTHS(TO_DATE(SUBSTR(SECE_ATON,1,8)),1)>SYSDATE THEN 'N' ELSE 'Y' END AS JOIN_RE	\n");
		sql.append("\t	, T4.GOLF_CMMN_CODE_NM, T4.GOLF_CMMN_CODE	\n");
		sql.append("\t	, CASE T1.JONN_ATON WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS JOIN_DATE	\n");
		sql.append("\t	, CASE WHEN to_date(T1.ACRG_CDHD_JONN_DATE,'YYYYMMDD')+30 >=SYSDATE THEN 'half' ELSE 'all' END UPD_PAY	\n");
		sql.append("\t	, JOIN_CHNL	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T4 ON T4.GOLF_CMMN_CODE=T3.CDHD_SQ2_CTGO AND T4.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T3.CDHD_SQ1_CTGO='0002'	\n");
        return sql.toString();
    }
       
    /** ***********************************************************************
    * 회원등급 가져오기 
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT GOLF_CMMN_CODE_NM MEM_GRADE, GOLF_CMMN_CODE INT_MEM_GRADE, ANL_FEE	\n");
		sql.append("\t	FROM BCDBA.TBGCMMNCODE CODE	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND USE_CLSS='Y'	\n");
		sql.append("\t	WHERE GOLF_CMMN_CLSS='0005' AND GOLF_CMMN_CODE=?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 쿠폰번호 가져오기 
    ************************************************************************ */
    private String getMemCodeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT EVNT_NO,CUPN_AMT,CUPN_CTNT,CUPN_NO,CUPN_CLSS,NVL(CUPN_PYM_YN,'0') CUPN_PYM_YN ,CUPN_USE_NUM,CUPN_VALD_STRT_DATE,CUPN_VALD_END_DATE, TO_CHAR(SYSDATE,'YYYYMMDD') TODATE ,NVL(CUPN_TYPE,'N') CUPN_TYPE , NVL(PMGDS_PYM_YN,'Y') PMGDS_PYM_YN 	\n");
		sql.append("\t	FROM BCDBA.TBEVNTUNIFCUPNINFO 	\n");
		sql.append("\t	WHERE  SITE_CLSS=?  AND  EVNT_NO IN (?,?)  AND CUPN_NO = UPPER(?) 	\n");
		sql.append("\t	ORDER BY CUPN_VALD_STRT_DATE, CUPN_NO 	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * 쿠폰번호 Updaet (사용횟수 차감)
    ************************************************************************ */
    private String getCouponUpQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET  CUPN_PYM_YN= ?, CUPN_USE_NUM = CUPN_USE_NUM-1	\n");
		sql.append("\t	WHERE SITE_CLSS = ? AND EVNT_NO = ? AND  CUPN_NO = UPPER(?) 	\n");
        return sql.toString();

    }

	 
	/** ***********************************************************************
	* 해당회원이 멤버십 등급을 가지고 있는지 검색 (멤버십만)
	************************************************************************ */
	private String getMembershipGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T2.CDHD_CTGO_SEQ_NO GRADE_NO, T1.CDHD_GRD_SEQ_NO GRADE_SEQ	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t 	WHERE T1.CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 히스토리 테이블 일련번호 최대값 가져오기 
	************************************************************************ */
	private String getMaxHistoryQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGCDHDGRDCHNGHST 	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* 히스토리 테이블에 저장한다.
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();
								
		sql.append("\n	");
		sql.append("\n	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	");
		sql.append("\n	SELECT ?, A.CDHD_CTGO_SEQ_NO, A.CDHD_ID, A.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS')	");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHDGRDMGMT A , BCDBA.TBGGOLFCDHD B	");
		sql.append("\n	WHERE A.CDHD_GRD_SEQ_NO= ?	");
		sql.append("\n	AND A.CDHD_ID = B.CDHD_ID	");
		
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* 등급을 업데이트 한다.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t	WHERE CDHD_GRD_SEQ_NO=?	\n");
				
		return sql.toString();
	}

 	/** ***********************************************************************
	* 회원정보 가져오기    strMemClss // 회원등급번호 1:개인 / 5:법인
	************************************************************************ */
	private String getUserInfoQuery(String strMemClss){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("1".equals(strMemClss)){
			
			sql.append("\t  SELECT EMAIL1 EMAIL, ZIPCODE, ZIPADDR, DETAILADDR, MOBILE, PHONE	\n");
			sql.append("\t  FROM BCDBA.UCUSRINFO	\n");
			sql.append("\t  WHERE ACCOUNT = ?	\n");
			
		}else{					

			sql.append("\t  SELECT CMEM.USER_EMAIL EMAIL, CMEM.USER_MOB_NO MOBILE, CMEM.USER_TEL_NO PHONE	\n");
			sql.append("\t  , NMEM.ZIPCODE, NMEM.ZIPADDR, NMEM.DETAILADDR	\n");
			sql.append("\t  FROM BCDBA.TBENTPUSER CMEM	\n");
			sql.append("\t  LEFT JOIN BCDBA.UCUSRINFO NMEM ON CMEM.ACCOUNT=NMEM.ACCOUNT	\n");
			sql.append("\t  WHERE CMEM.ACCOUNT=?	\n");
			
		}
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* 월결제 등록하기
	************************************************************************ */
	private String getPayMonthQuery(String memSort, String paySort){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("ins".equals(memSort)){
			
			sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_LESN_RSVT_NO	\n");
			sql.append("\t  , GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT)	\n");
			sql.append("\t  (SELECT MAX(APLC_SEQ_NO)+1, 1, '1001', 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
			sql.append("\t  , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),?	\n");
			sql.append("\t	FROM BCDBA.TBGAPLCMGMT)	\n");	
			
		}else{
			if("all".equals(paySort)){
				
				sql.append("\t  UPDATE BCDBA.TBGAPLCMGMT SET 	\n");
				sql.append("\t  GOLF_LESN_RSVT_NO = 1, PU_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
				sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?	\n");
				sql.append("\t  WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
				
			}else{
				
				sql.append("\t  UPDATE BCDBA.TBGAPLCMGMT SET 	\n");
				sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?	\n");
				sql.append("\t  WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
				
			}
		}
		
		return sql.toString();
	}
    
    /** ***********************************************************************
     * 월결제 진행여부 변경
     ************************************************************************ */
 	private String getSelMonthMemQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	SELECT APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND JUMIN_NO=?	\n");
 		return sql.toString();
 	}
        
    /** ***********************************************************************
     * 월결제 진행여부 변경
     ************************************************************************ */
 	private String getUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN=? WHERE APLC_SEQ_NO=?	\n");
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * TM 상태 변경
     ************************************************************************ */
 	private String getTmUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS=? WHERE RND_CD_CLSS='2' AND RCRU_PL_CLSS='5000' AND JUMIN_NO=?	\n");
 		return sql.toString();
 	}
    
    /** ***********************************************************************
    * 회원 업데이트 - 유료회원으로
    ************************************************************************ */
    private String getMemberShipUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'),	\n");
 		sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD'), 	\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO = ?	\n");
 		sql.append("\t		WHERE CDHD_ID=?	\n");
        return sql.toString();
    }  
    
    /** ***********************************************************************
     * 회원 업데이트 - 주민등록번호 변경
     ************************************************************************ */
     private String getJuminUpdateQuery(){
    	 StringBuffer sql = new StringBuffer();
  		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET JUMIN_NO=? WHERE CDHD_ID=?	\n");
        return sql.toString();
     }   
     
     
    /** ***********************************************************************
    * 회원 업데이트 - 주민등록번호 변경
    ************************************************************************ */
    private String getJoinChnlHistoryInsQuery(){
    	StringBuffer sql = new StringBuffer();
    	sql.append("	\n");
 		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t	SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST), JOIN_CHNL, CDHD_ID, '0'	\n");
 		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ACRG_CDHD_JONN_DATE , ACRG_CDHD_END_DATE , JOIN_CHNL FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
   		
 		
        return sql.toString();
    }   
 

}
