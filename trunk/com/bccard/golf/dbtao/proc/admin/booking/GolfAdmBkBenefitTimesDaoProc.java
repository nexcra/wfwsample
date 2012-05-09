/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBkBenefitTimesDaoProc
*   작성자    : (주)미디어포스 천선정
*   내용      : 관리자 부킹 혜택관리 
*   적용범위  : golf
*   작성일자  : 2009-12-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfAdmBkBenefitTimesDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPermissionDaoProc 프로세스 생성자  
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBkBenefitTimesDaoProc() {}	

	/** 
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	//주중일반부킹
	public DbTaoResult getNmWkdBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		String memb_id = data.getString("CDHD_ID");
		
		DbTaoResult result =  new DbTaoResult(title);
		String sql = ""; 
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		ResultSet rs4 = null;		
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					
		PreparedStatement pstmt4 = null;		
		
		int idx = 0;
 
		try {
			conn = context.getDbConnection("default", null);
			
			int intMemberGrade =0;	// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급
			int intBkGrade = 0;									// 차감등급
			
			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드

			int gen_WKD_BOKG_NUM = 0;			// 주중부킹 가능 횟수
			String gen_BOKG_LIMT_YN = "";		// 일반부킹 제한 여부
			int gen_WED_Y = 0;					// 주중부킹 예약 횟수
			int gen_WED_N = 0;					// 주중부킹 취소 횟수
			int cy_MONEY = 0;					// 사이버머니
			int gen_WKD_BOKG = 0;				// 주중 부킹 가능 출력 횟수
			int mem_gen_WKD_BOKG = 0;			// 멤버십 회원 주중 부킹 가능 출력 횟수
			int card_gen_WKD_BOKG = 0;			// 카드 회원 주중 부킹 가능 출력 횟수
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시

			//멤버십 회원등급 
			//01-1. 회원 정보 MEMBER 버젼
			sql = this.getMemGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			
			rs1 = pstmt1.executeQuery();
			//카드 회원등급
			if(rs1 != null){
				
			}			
			
			// 멤버십 회원등급을 가져온다.	  
			if(intMemberGrade>0){
				
				sql = this.getMemGradeQuery();
				pstmt2 = conn.prepareStatement(sql);
				pstmt2.setString(1, memb_id);
				rs2 = pstmt2.executeQuery();
				if(rs2 != null) {
					while(rs2.next())  {
						cdhd_grd_seq_no = rs2.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs2.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs2.getInt("CDHD_SQ2_CTGO");
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						debug("일반부킹 " + cdhd_sq2_ctgo);
						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt3 = conn.prepareStatement(sql);
						pstmt3.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
							pstmt3.setString(2, cdhd_ctgo_seq_no);
						}
						rs3 = pstmt3.executeQuery();		
						if(rs3 != null) {
							while(rs3.next())  {
								st_date = rs3.getString("ST_DATE");
								ed_date = rs3.getString("ED_DATE");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getNmWkdBenefitQuery("mem");
						pstmt4 = conn.prepareStatement(sql);

						idx = 0;
						pstmt4.setString(++idx, memb_id);
						pstmt4.setInt(++idx, cdhd_sq2_ctgo);
						pstmt4.setString(++idx, st_date);
						pstmt4.setString(++idx, ed_date);
						pstmt4.setString(++idx, memb_id);
						pstmt4.setInt(++idx, cdhd_sq2_ctgo);
						pstmt4.setString(++idx, st_date);
						pstmt4.setString(++idx, ed_date);
						pstmt4.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						 
						rs4 = pstmt4.executeQuery();						
									
						if(rs4 != null) {
							while(rs4.next())  {
								gen_WKD_BOKG_NUM = rs4.getInt("GEN_WKD_BOKG_NUM");
								gen_BOKG_LIMT_YN = rs4.getString("GEN_BOKG_LIMT_YN");
								gen_WED_Y = rs4.getInt("GEN_WED_Y");
								gen_WED_N = rs4.getInt("GEN_WED_N");
								debug("MEM : gen_BOKG_LIMT_YN : " + gen_BOKG_LIMT_YN + " / gen_WKD_BOKG_NUM : " + gen_WKD_BOKG_NUM + " / gen_WED_Y : " + gen_WED_Y + " / gen_WED_N : " + gen_WED_N);
								
								if(gen_BOKG_LIMT_YN.equals("A")){	// 무제한 /  N : 혜택없음
									mem_gen_WKD_BOKG = 1;
								}else{	//제한
									mem_gen_WKD_BOKG = gen_WKD_BOKG_NUM-gen_WED_Y+gen_WED_N;
								}
								gen_WKD_BOKG = gen_WKD_BOKG + mem_gen_WKD_BOKG;
								intBkGrade = cdhd_sq2_ctgo;
							}
						}
					}
				}				
			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();			 
			if(rs4 != null) rs4.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();		
			if(pstmt4 != null) pstmt4.close();
			
			debug("MEM : gen_WKD_BOKG : " + gen_WKD_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			

			// 카드 회원등급을 가져온다.
			if(intCardGrade>0){
				
				sql = this.getCardGradeQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				if(rs1 != null) {
					while(rs1.next())  {
						cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo == 7 || cdhd_sq2_ctgo==12)){
							pstmt2.setString(2, cdhd_ctgo_seq_no);
						}
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
							}
						}
						debug("CARD : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getNmWkdBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setString(++idx, memb_id);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();
									
						if(rs3 != null) {
							while(rs3.next())  {
								
								// 혜택기간 가져오기

								gen_WKD_BOKG_NUM = rs3.getInt("GEN_WKD_BOKG_NUM");
								gen_BOKG_LIMT_YN = rs3.getString("GEN_BOKG_LIMT_YN");
								gen_WED_Y = rs3.getInt("GEN_WED_Y");
								gen_WED_N = rs3.getInt("GEN_WED_N");
								debug("CARD : gen_BOKG_LIMT_YN : " + gen_BOKG_LIMT_YN + " / gen_WKD_BOKG_NUM : " + gen_WKD_BOKG_NUM + " / gen_WED_Y : " + gen_WED_Y + " / gen_WED_N : " + gen_WED_N);
								
								if(gen_BOKG_LIMT_YN.equals("A")){	// 무제한 /  N : 혜택없음
									card_gen_WKD_BOKG = 1;
								}else{	//제한
									card_gen_WKD_BOKG = gen_WKD_BOKG_NUM-gen_WED_Y+gen_WED_N;
								}
								gen_WKD_BOKG = gen_WKD_BOKG + card_gen_WKD_BOKG;
								if(card_gen_WKD_BOKG>0){	// 카드 등급으로 차감 가능 하다면 카드 등급으로 차감해준다.
									intBkGrade = cdhd_sq2_ctgo;
								}
							}
						}
						
					}
				}				

			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();
			
			debug("CARD : gen_WKD_BOKG : " + gen_WKD_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			
			if(gen_BOKG_LIMT_YN.equals("A")){
				cy_MONEY = 0;
			}else{
				// 사이버머니 가져오기
				sql = this.getCyMoneyQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				if(rs1 != null) {
					while(rs1.next())  {
						cy_MONEY = rs1.getInt("CY_MONEY");
					}
				}
			}
			debug("total : gen_WKD_BOKG : " + gen_WKD_BOKG + " / cy_MONEY : " + cy_MONEY + " / intBkGrade : " + intBkGrade);	

			result.addInt("GEN_WKD_BOKG" 			,gen_WKD_BOKG);
			result.addInt("CY_MONEY" 				,cy_MONEY);
			result.addInt("intBkGrade" 				,intBkGrade);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}			
			try { if(rs4 != null) rs4.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}			
			try { if(pstmt4 != null) pstmt4.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}

		}

		return result;
	}	


	//주말 일반부킹
	public DbTaoResult getNmWkeBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		ResultSet rs4 = null;		
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					
		PreparedStatement pstmt4 = null;		

		try {
			conn = context.getDbConnection("default", null);
			int intMemberGrade = 0;	// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급
			int intBkGrade = 0;									// 차감등급
			String memb_id = "";				// 회원 아이디
			
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				 intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				 intCardGrade = userEtt.getIntCardGrade();		// 카드 등급
				 memb_id = userEtt.getAccount();				// 회원 아이디
			}
			
			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드

			int gen_WKE_BOKG_NUM = 0;			// 주중부킹 가능 횟수
			String gen_BOKG_LIMT_YN = "";		// 일반부킹 제한 여부
			int gen_WKE_Y = 0;					// 주중부킹 예약 횟수
			int gen_WKE_N = 0;					// 주중부킹 취소 횟수
			int cy_MONEY = 0;					// 사이버머니
			int gen_WKE_BOKG = 0;				// 주중 부킹 가능 출력 횟수
			int mem_gen_WKE_BOKG = 0;			// 멤버십 회원 주중 부킹 가능 출력 횟수
			int card_gen_WKE_BOKG = 0;			// 카드 회원 주중 부킹 가능 출력 횟수
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시

			// 멤버십 회원등급을 가져온다.	
			if(intMemberGrade>0){
				sql = this.getMemGradeQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				
				if(rs1 != null) {
					while(rs1.next())  {
						cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
							pstmt2.setString(2, cdhd_ctgo_seq_no);
						}
						rs2 = pstmt2.executeQuery();		

						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getNmWkeBenefitQuery("mem");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setString(++idx, memb_id);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
				        
						rs3 = pstmt3.executeQuery();						
									
						if(rs3 != null) {
							while(rs3.next())  {
								gen_WKE_BOKG_NUM = rs3.getInt("GEN_WKE_BOKG_NUM");
								gen_BOKG_LIMT_YN = rs3.getString("GEN_BOKG_LIMT_YN");
								gen_WKE_Y = rs3.getInt("GEN_WKE_Y");
								gen_WKE_N = rs3.getInt("GEN_WKE_N");
								debug("MEM : gen_BOKG_LIMT_YN : " + gen_BOKG_LIMT_YN + " / gen_WKD_BOKG_NUM : " + gen_WKE_BOKG_NUM + " / gen_WED_Y : " + gen_WKE_Y + " / gen_WED_N : " + gen_WKE_N);
								
								if(gen_BOKG_LIMT_YN.equals("A")){	// 무제한 /  N : 혜택없음
									mem_gen_WKE_BOKG = 1;
								}else{	//제한
									mem_gen_WKE_BOKG = gen_WKE_BOKG_NUM-gen_WKE_Y+gen_WKE_N;
								}
								gen_WKE_BOKG = gen_WKE_BOKG + mem_gen_WKE_BOKG;
								intBkGrade = cdhd_sq2_ctgo;
							}
						}
					}
				}				
			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();
			debug("MEM : gen_WKE_BOKG : " + gen_WKE_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			

			// 카드 회원등급을 가져온다.
			if(intCardGrade>0){
				
				sql = this.getCardGradeQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				
				if(rs1 != null) {
					while(rs1.next())  {
						cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
							pstmt2.setString(2, cdhd_ctgo_seq_no);
						}
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
							}
						}
						debug("CARD : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getNmWkeBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setString(++idx, memb_id);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();
									
						if(rs3 != null) {
							while(rs3.next())  {
								
								// 혜택기간 가져오기

								gen_WKE_BOKG_NUM = rs3.getInt("GEN_WKE_BOKG_NUM");
								gen_BOKG_LIMT_YN = rs3.getString("GEN_BOKG_LIMT_YN");
								gen_WKE_Y = rs3.getInt("GEN_WKE_Y");
								gen_WKE_N = rs3.getInt("GEN_WKE_N");
								debug("CARD : gen_BOKG_LIMT_YN : " + gen_BOKG_LIMT_YN + " / gen_WKE_BOKG_NUM : " + gen_WKE_BOKG_NUM + " / gen_WKE_Y : " + gen_WKE_Y + " / gen_WKE_N : " + gen_WKE_N);
								
								if(gen_BOKG_LIMT_YN.equals("A")){	// 무제한 /  N : 혜택없음
									card_gen_WKE_BOKG = 1;
								}else{	//제한
									card_gen_WKE_BOKG = gen_WKE_BOKG_NUM-gen_WKE_Y+gen_WKE_N;
								}
								gen_WKE_BOKG = gen_WKE_BOKG + card_gen_WKE_BOKG;
								if(card_gen_WKE_BOKG>0){	// 카드 등급으로 차감 가능 하다면 카드 등급으로 차감해준다.
									intBkGrade = cdhd_sq2_ctgo;
								}
							}
						}
						
					}
				}
				
			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();
			
			debug("CARD : gen_WKD_BOKG : " + gen_WKE_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			
			if(gen_BOKG_LIMT_YN.equals("A")){
				cy_MONEY = 0;
			}else{
				// 사이버머니 가져오기
				sql = this.getCyMoneyQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				if(rs1 != null) {
					while(rs1.next())  {
						cy_MONEY = rs1.getInt("CY_MONEY");
					}
				}
			}
			debug("total : gen_WKD_BOKG : " + gen_WKE_BOKG + " / cy_MONEY : " + cy_MONEY + " / intBkGrade : " + intBkGrade);	

			result.addInt("GEN_WKE_BOKG" 			,gen_WKE_BOKG);
			result.addInt("CY_MONEY" 				,cy_MONEY);
			result.addInt("intBkGrade" 				,intBkGrade);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}			
			try { if(rs4 != null) rs4.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}			
			try { if(pstmt4 != null) pstmt4.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}

		}

		return result;
	}	

	//관리자 HOME > 부킹 > 파3부킹 > 예약관리
	public DbTaoResult getParBenefit(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		String memb_id = data.getString("CDHD_ID");
		
		debug("PROC >>>>>>>>>>  CDHD_ID :"+memb_id);		

		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		String strMemGradeTxt = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		ResultSet rs4 = null;		
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					
		PreparedStatement pstmt4 = null;
		
		try {
			conn = context.getDbConnection("default", null);
		
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String st_date_month = "";			// 부킹 혜택 시작 일시(월제한)
			String ed_date_month = "";			// 부킹 혜택 종료 일시(월제한)
			
			String bk_yn = "";
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			int par_3_bokg_yr_able = 0;
			int par_3_bokg_yr_done = 0;
			int mem_par_3_bokg_yr_able = 0;
			int mem_par_3_bokg_yr_done = 0;
			int card_par_3_bokg_yr_able = 0;
			int card_par_3_bokg_yr_done = 0; 
			
			//멤버십 회원등급 GET
			sql = this.getMemGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1,memb_id);
			
			rs1 = pstmt1.executeQuery();
			
			if(rs1 != null){
				while(rs1.next()){
					
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}

					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
	
					sql = this.getParBenefitQuery("mem");
					pstmt3 = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();						
								
						if(rs3 != null) {
							while(rs3.next())  {
								bk_yn = rs3.getString("PAR_3_BOKG_LIMT_YN");
								bk_yr_able_num = rs3.getInt("PAR_3_BOKG_YR_ABLE_NUM");
								bk_mo_able_num = rs3.getInt("PAR_3_BOKG_MO_ABLE_NUM");
								bk_yr_done = rs3.getInt("PAR_3_BOKG_YR_DONE");
								bk_mo_done = rs3.getInt("PAR_3_BOKG_MO_DONE");
								debug("MEM : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
								
								if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
									mem_par_3_bokg_yr_able = 0;
								}else{	//제한
									mem_par_3_bokg_yr_able = 0;
									mem_par_3_bokg_yr_done = 0;
									mem_par_3_bokg_yr_able = mem_par_3_bokg_yr_able + bk_yr_able_num;
									mem_par_3_bokg_yr_done = mem_par_3_bokg_yr_done + bk_yr_done;
									
								}
							}
						}
						
						par_3_bokg_yr_able = par_3_bokg_yr_able + mem_par_3_bokg_yr_able; 
						par_3_bokg_yr_done = par_3_bokg_yr_done + mem_par_3_bokg_yr_done;
						
					}
				}
						
			 if(rs1 != null) rs1.close();
			 if(rs2 != null) rs2.close();
			 if(rs3 != null) rs3.close();			 
			 if(pstmt1 != null) pstmt1.close();
			 if(pstmt2 != null) pstmt2.close();
			 if(pstmt3 != null) pstmt3.close();			 			

			debug("MEM : par_3_bokg_yr_able : " + par_3_bokg_yr_able + " / par_3_bokg_yr_done : " + par_3_bokg_yr_done);	

			// 카드 회원등급을 가져온다.
			sql = this.getCardGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
						
						//회원 등급명 GET
						if(!"".equals(strMemGradeTxt)) {
							strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
						}else{
							strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
						}
						
						cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
							pstmt2.setString(2, cdhd_ctgo_seq_no);
						}
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
						
						sql = this.getParBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();
									
						if(rs3 != null) {
							while(rs3.next())  {
								bk_yn = rs3.getString("PAR_3_BOKG_LIMT_YN");
								bk_yr_able_num = rs3.getInt("PAR_3_BOKG_YR_ABLE_NUM");
								bk_mo_able_num = rs3.getInt("PAR_3_BOKG_MO_ABLE_NUM");
								bk_yr_done = rs3.getInt("PAR_3_BOKG_YR_DONE");
								bk_mo_done = rs3.getInt("PAR_3_BOKG_MO_DONE");
								debug("CARD : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
								
								if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
									card_par_3_bokg_yr_able = 0;
								}else{	//제한
									card_par_3_bokg_yr_able = 0;
									card_par_3_bokg_yr_done = 0;
									card_par_3_bokg_yr_able = card_par_3_bokg_yr_able + bk_yr_able_num;
									card_par_3_bokg_yr_done = card_par_3_bokg_yr_done + bk_yr_done;
															
								}
								
							}
						}
						
						par_3_bokg_yr_able = par_3_bokg_yr_able + card_par_3_bokg_yr_able;
						par_3_bokg_yr_done = par_3_bokg_yr_done + card_par_3_bokg_yr_done;
						
					}
				}		
			
						
			debug("CARD : par_3_bokg_yr_able : " + par_3_bokg_yr_able + " / par_3_bokg_yr_done : " + par_3_bokg_yr_done);	
			
			result.addInt("PAR_3_YR_ABLE"			,par_3_bokg_yr_able);	
			result.addInt("PAR_3_YR_DONE" 			,par_3_bokg_yr_done);
			result.addString("MEMGRADE" 			,strMemGradeTxt);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}			
			try { if(rs4 != null) rs4.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}			
			try { if(pstmt4 != null) pstmt4.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}

		}

		return result;
	}	
	
	//부킹 라운지 > VIP 부킹이벤트 
	//관리자 HOME > 부킹 > VIP부킹 > 예약관리
	public DbTaoResult getPreBkBenefit(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		String memb_id = data.getString("CDHD_ID");			// 회원 아이디

		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					
		
		try {
			conn = context.getDbConnection("default", null);
			
			
			int cy_MONEY = 0;
			int intMemberGrade = 0;	// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급
			int intBkGrade = 0;									// 차감등급
 
			String strMemGradeTxt = "";
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String st_date_month = "";			// 부킹 혜택 시작 일시(월제한)
			String ed_date_month = "";			// 부킹 혜택 종료 일시(월제한)
			
			String bk_yn = "";

			int bk_yr_done = 0;
			int bk_mo_done = 0;
			int mem_pre_bokg_yr = 0;
			int mem_pre_bokg_mo = 0;
			int card_pre_bokg_yr = 0;
			int card_pre_bokg_mo = 0;
			int pre_bokg_yr_done = 0;
			int pre_bokg_mo_done = 0;

			
			// 멤버십 회원등급을 가져온다.	
			
			sql = this.getMemGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}
					
					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){					
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
	
					sql = this.getPreBkBenefitQuery("mem");
					pstmt3 = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();			 			
								
					if(rs3 != null) {
						while(rs3.next())  {
							if(!bk_yn.equals("Y")){
								bk_yn = rs3.getString("PMI_BOKG_APO_YN");
							}
							
							bk_yr_done = rs3.getInt("PMI_BOKG_YR_DONE");
							bk_mo_done = rs3.getInt("PMI_BOKG_MO_DONE");
							
							debug("MEM : bk_yn : " + bk_yn + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
							
							if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
								mem_pre_bokg_yr = 0;
								mem_pre_bokg_mo = 0;
							}else{	//제한
								mem_pre_bokg_yr = bk_yr_done;
								mem_pre_bokg_mo = bk_mo_done;
							}
						}
					}
					pre_bokg_yr_done = pre_bokg_yr_done + mem_pre_bokg_yr;
					pre_bokg_mo_done = pre_bokg_mo_done + mem_pre_bokg_mo;
				}
			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();
			
			debug("MEM : pre_bokg_yr_done : " + pre_bokg_yr_done + " / pre_bokg_mo_done : " + pre_bokg_mo_done);	

			// 카드 회원등급을 가져온다.
			
			sql = this.getCardGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}
					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	

					sql = this.getPreBkBenefitQuery("card");
					pstmt3 = conn.prepareStatement(sql);
					
					idx = 0; 
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();
								
					if(rs3 != null) {
						while(rs3.next())  {
							if(!bk_yn.equals("Y")){
								bk_yn = rs3.getString("PMI_BOKG_APO_YN");
							}
							bk_yr_done = rs3.getInt("PMI_BOKG_YR_DONE");
							bk_mo_done = rs3.getInt("PMI_BOKG_MO_DONE");
							
							debug("CARD : bk_yn : " + bk_yn + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
							
							if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
								card_pre_bokg_yr = 0;
								card_pre_bokg_mo = 0;
							}else{	//제한
								card_pre_bokg_yr = bk_yr_done;
								card_pre_bokg_mo = bk_mo_done;
							}
						}
					}
					pre_bokg_yr_done = pre_bokg_yr_done + card_pre_bokg_yr;
					pre_bokg_mo_done = pre_bokg_mo_done + card_pre_bokg_mo;
					
				}
			}
			
			debug("CARD : pre_bokg_yr_done : " + pre_bokg_yr_done + " / pre_bokg_mo_done : " + pre_bokg_mo_done);	

			debug("total : pre_bokg_yr_done : " + pre_bokg_yr_done + " / pre_bokg_mo_done : " + pre_bokg_mo_done);	
			
			result.addString("PERMISSION"	,bk_yn);
			result.addInt("YR_DONE" 		,pre_bokg_yr_done);
			result.addInt("MO_DONE"			,pre_bokg_mo_done);
			result.addString("MEMGRADE" 	,strMemGradeTxt);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}

		}

		return result;
	}	

	//Sky72  드림듄스 
	//관리자 HOME > 부킹 > sky72 드림듄스 > 예약관리 (현재는 사용중지, 차후 사용시 년/월 개수 제대로 가져오는지 확인 필)
	public DbTaoResult getSkyBenefit(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		String memb_id = data.getString("CDHD_ID");

		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					

		try {
			conn = context.getDbConnection("default", null);			

			String strMemGradeTxt = "";
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String st_date_month = "";			// 부킹 혜택 시작 일시(월제한)
			String ed_date_month = "";			// 부킹 혜택 종료 일시(월제한)
			
			String bk_yn = "";
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			
			int drds_bokg_able = 0;
			int drds_bokg_done = 0;
			int mem_drds_bokg_able = 0;
			int mem_drds_bokg_done = 0;
			int card_drds_bokg_able = 0;
			int card_drds_bokg_done = 0;
 

			// 멤버십 회원등급을 가져온다.
			sql = this.getMemGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}
					
					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
	
					sql = this.getSkyBenefitQuery("mem");
					pstmt3 = conn.prepareStatement(sql);

					idx = 0;
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();						
								
					if(rs3 != null) {
						while(rs3.next())  {
							bk_yn = rs3.getString("DRDS_BOKG_LIMT_YN");
							bk_yr_able_num = rs3.getInt("DRDS_BOKG_YR_ABLE_NUM");
							bk_mo_able_num = rs3.getInt("DRDS_BOKG_MO_ABLE_NUM");
							bk_yr_done = rs3.getInt("DRDS_BOKG_YR_DONE");
							bk_mo_done = rs3.getInt("DRDS_BOKG_MO_DONE");
							debug("MEM : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
							
							if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
								mem_drds_bokg_able = 0;
							}else{	//제한
								mem_drds_bokg_able = 0;
								mem_drds_bokg_done = 0;
								mem_drds_bokg_able = mem_drds_bokg_able + bk_yr_able_num;
								mem_drds_bokg_done = mem_drds_bokg_done + bk_yr_done;
							}
						}
					}
					
					drds_bokg_able = drds_bokg_able + mem_drds_bokg_able;
					drds_bokg_done = drds_bokg_done + mem_drds_bokg_done;

				}
			}

			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();


			debug("MEM : drds_bokg_able : " + drds_bokg_able + " / drds_bokg_done : " + drds_bokg_done);	

			// 카드 회원등급을 가져온다.
			
			sql = this.getCardGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}
					
					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
					
					sql = this.getSkyBenefitQuery("card");
					pstmt3 = conn.prepareStatement(sql);

					idx = 0;
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();
								
					if(rs3 != null) {
						while(rs3.next())  {
							bk_yn = rs3.getString("DRDS_BOKG_LIMT_YN");
							bk_yr_able_num = rs3.getInt("DRDS_BOKG_YR_ABLE_NUM");
							bk_mo_able_num = rs3.getInt("DRDS_BOKG_MO_ABLE_NUM");
							bk_yr_done = rs3.getInt("DRDS_BOKG_YR_DONE");
							bk_mo_done = rs3.getInt("DRDS_BOKG_MO_DONE");
							debug("CARD : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
							
							if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
								card_drds_bokg_able = 0;
							}else{	//제한
								card_drds_bokg_able = 0;
								card_drds_bokg_done = 0;
								card_drds_bokg_able = card_drds_bokg_able + bk_yr_able_num;
								card_drds_bokg_done = card_drds_bokg_done + bk_yr_done;
							}
							
						}
					}
					
					drds_bokg_able = drds_bokg_able + card_drds_bokg_able;					
					drds_bokg_done = drds_bokg_done + card_drds_bokg_done;
										
				}
			}

			debug("CARD : drds_bokg_able : " + drds_bokg_able + " / drds_bokg_done : " + drds_bokg_done );	

			debug("total : drds_bokg_able : " + drds_bokg_able + " / drds_bokg_done : " + drds_bokg_done);	

			result.addInt("DRDS_BOKG_ABLE" 			,drds_bokg_able);
			result.addInt("DRDS_BOKG_DONE" 			,drds_bokg_done);
			result.addString("MEMGRADE" 			,strMemGradeTxt);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}		
			
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
			
		}

		return result;
	}	
	
	//연습장라운지 > 드라이빙레인지 할인 > sky72드림골프레인지
	//관리자 HOME > 연습장 > 드림 골프레인지 > 신청(sky72) 상세보기
	public DbTaoResult getDrivingSkyBenefit(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		String memb_id = data.getString("CDHD_ID");

		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					
		
		try {
			conn = context.getDbConnection("default", null);
					
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			
			String strMemGradeTxt = "";
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String st_date_month = "";			// 부킹 혜택 시작 일시(월제한)
			String ed_date_month = "";			// 부킹 혜택 종료 일시(월제한)
			
			String bk_yn = "";
			String bk_apo_yn = "";
			String cupn_prn_num = "0";
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			
			int drgf_bokg_able = 0;
			int drgf_bokg_done = 0;
			int mem_drgf_bokg_able = 0;
			int mem_drgf_bokg_done = 0;
			int card_drgf_bokg_able = 0;
			int card_drgf_bokg_done = 0;

			// 멤버십 회원등급을 가져온다.	
			sql = this.getMemGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}
					
					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
	
					sql = this.getDrivingSkyBenefitQuery("mem");
					pstmt3 = conn.prepareStatement(sql);

					idx = 0;
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();						
								
					if(rs3 != null) {
						while(rs3.next())  {
							bk_yn = rs3.getString("DRGF_LIMT_YN");
							bk_apo_yn = rs3.getString("DRGF_APO_YN");
							bk_yr_able_num = rs3.getInt("DRGF_YR_ABLE_NUM");
							bk_mo_able_num = rs3.getInt("DRGF_MO_ABLE_NUM");
							bk_yr_done = rs3.getInt("DRGF_BOKG_YR_DONE");
							bk_mo_done = rs3.getInt("DRGF_BOKG_YR_DONE");
							cupn_prn_num = rs3.getString("CUPN_PRN_NUM");
							debug("MEM : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
							
							if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
								mem_drgf_bokg_able = 0;
							}else{	//제한
								mem_drgf_bokg_able = 0;
								mem_drgf_bokg_done = 0;
								mem_drgf_bokg_able = mem_drgf_bokg_able + bk_yr_able_num;
								mem_drgf_bokg_done = mem_drgf_bokg_done + bk_yr_done;
							}
						}
					}
					drgf_bokg_able = drgf_bokg_able + mem_drgf_bokg_able;
					drgf_bokg_done = drgf_bokg_done + mem_drgf_bokg_done;
				}
			}			

			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();
			
			debug("MEM : drgf_bokg_able : " + drgf_bokg_able + " / drgf_bokg_done : " + drgf_bokg_done );	

			// 카드 회원등급을 가져온다.
			sql = this.getCardGradeQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					
					//회원 등급명 GET
					if(!"".equals(strMemGradeTxt)) {
						strMemGradeTxt = strMemGradeTxt +  "/" + rs1.getString("GOLF_CMMN_CODE_NM");
					}else{
						strMemGradeTxt = rs1.getString("GOLF_CMMN_CODE_NM");
					}
					cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
					cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
					cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
					debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
					debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
					debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

					sql = this.getDateQuery(cdhd_sq2_ctgo);
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, memb_id);
					if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
						pstmt2.setString(2, cdhd_ctgo_seq_no);
					}
					rs2 = pstmt2.executeQuery();		
					if(rs2 != null) {
						while(rs2.next())  {
							st_date = rs2.getString("ST_DATE");
							ed_date = rs2.getString("ED_DATE");
							st_date_month = rs2.getString("ST_DATE_MONTH");
							ed_date_month = rs2.getString("ED_DATE_MONTH");
						}
					}
					debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
					
					sql = this.getDrivingSkyBenefitQuery("card");
					pstmt3 = conn.prepareStatement(sql);

					idx = 0;
					pstmt3.setString(++idx, memb_id); 
					pstmt3.setString(++idx, st_date);
					pstmt3.setString(++idx, ed_date);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, memb_id);
					pstmt3.setString(++idx, st_date_month);
					pstmt3.setString(++idx, ed_date_month);
					pstmt3.setInt(++idx, cdhd_sq2_ctgo);
					pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					
					rs3 = pstmt3.executeQuery();
								
					if(rs3 != null) {
						while(rs3.next())  {
							bk_yn = rs3.getString("DRGF_LIMT_YN");
							bk_apo_yn = rs3.getString("DRGF_APO_YN");
							bk_yr_able_num = rs3.getInt("DRGF_YR_ABLE_NUM");
							bk_mo_able_num = rs3.getInt("DRGF_MO_ABLE_NUM");
							bk_yr_done = rs3.getInt("DRGF_BOKG_YR_DONE");
							bk_mo_done = rs3.getInt("DRGF_BOKG_MO_DONE");
							cupn_prn_num = rs3.getString("CUPN_PRN_NUM");
							debug("CARD : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
							
							if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
								card_drgf_bokg_able = 0;
							}else{	//제한
								card_drgf_bokg_able = 0;
								card_drgf_bokg_done = 0;
								card_drgf_bokg_able = card_drgf_bokg_able + bk_yr_able_num;
								card_drgf_bokg_done = card_drgf_bokg_done + bk_yr_done;
							}
						}
					}
					drgf_bokg_able = drgf_bokg_able + card_drgf_bokg_able;
					drgf_bokg_done = drgf_bokg_done + card_drgf_bokg_done;
				}
			}		
			
			debug("CARD : drgf_bokg_able : " + drgf_bokg_able + " / drgf_bokg_done : " + drgf_bokg_done);	

			debug("total : drgf_bokg_able : " + drgf_bokg_able + " / drgf_bokg_done : " + drgf_bokg_done);	
			
			result.addInt("DRGF_YR_ABLE" 			,drgf_bokg_able);
			result.addInt("DRGF_YR_DONE" 			,drgf_bokg_done);
			result.addString("MEMGRADE" 			,strMemGradeTxt);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}

		}

		return result;
	}
	
	
	//골프마니아 > 편리한 리무진서비스 > 연 횟수
	public DbTaoResult getVipLmsBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");

		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					

		try {
			conn = context.getDbConnection("default", null);
			
			int cy_MONEY = 0;
			int intMemberGrade = 0;	// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급
			int intBkGrade = 0;									// 차감등급
			String memb_id = "";				// 회원 아이디
			
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				 intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				 intCardGrade = userEtt.getIntCardGrade();		// 카드 등급
				 memb_id = userEtt.getAccount();				// 회원 아이디
			}
			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String st_date_month = "";			// 부킹 혜택 시작 일시(월제한)
			String ed_date_month = "";			// 부킹 혜택 종료 일시(월제한)
			
			String bk_yn = "";

			///////////////////////////////////////////
			int lms_yr_able_num = 0;
			int lms_mo_able_num = 0;
			int lms_yr_done = 0;
			int lms_mo_done = 0;
			
			int mem_lms_yr = 0;
			int mem_lms_mo = 0;
			int card_lms_yr = 0;
			int card_lms_mo = 0;
			
			int lms_yr = 0;
			int lms_mo = 0;

			// 멤버십 회원등급을 가져온다.	
			if(intMemberGrade>0){
				sql = this.getMemGradeQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				if(rs1 != null) {
					while(rs1.next())  {
						cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
							pstmt2.setString(2, cdhd_ctgo_seq_no);
						}
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
		
						sql = this.getVipLmsBenefitQuery("mem");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();						
									
						if(rs3 != null) {
							while(rs3.next())  {
								bk_yn = rs3.getString("LMS_VIP_LIMT_YN");
								lms_yr_able_num = rs3.getInt("LMS_VIP_YR_ABLE_NUM");
								lms_yr_done = rs3.getInt("LMS_YR_DONE");
								lms_mo_done = rs3.getInt("LMS_MO_DONE");
								
								if(bk_yn.equals("N")){	// LMS_VIP_LIMT_YN ( Y : 제한 / N : 혜택없음)
									mem_lms_yr = 0;
									mem_lms_mo = 0;
								}else{	//제한
									mem_lms_yr = lms_yr_able_num - lms_yr_done;
									mem_lms_mo = lms_mo_able_num - lms_mo_done;
								}
								lms_yr = lms_yr + mem_lms_yr;
								lms_mo = lms_mo + mem_lms_mo;
								intBkGrade = cdhd_sq2_ctgo;
							}
						}
					}
				}

			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();
				

			// 카드 회원등급을 가져온다.
			if(intCardGrade>0){
				sql = this.getCardGradeQuery();
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, memb_id);
				rs1 = pstmt1.executeQuery();
				if(rs1 != null) {
					while(rs1.next())  {
						cdhd_grd_seq_no = rs1.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs1.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo);
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						if(!(cdhd_sq2_ctgo==1 || cdhd_sq2_ctgo==2 || cdhd_sq2_ctgo==3 || cdhd_sq2_ctgo==4 || cdhd_sq2_ctgo==7 || cdhd_sq2_ctgo==12)){
							pstmt2.setString(2, cdhd_ctgo_seq_no);
						}
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
						
						sql = this.getVipLmsBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id); 
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();
									
						if(rs3 != null) {
							while(rs3.next())  {
								bk_yn = rs3.getString("LMS_VIP_LIMT_YN");
								lms_yr_able_num = rs3.getInt("LMS_VIP_YR_ABLE_NUM");
								lms_yr_done = rs3.getInt("LMS_YR_DONE");
								lms_mo_done = rs3.getInt("LMS_MO_DONE");
								
								if(bk_yn.equals("N")){	// LMS_VIP_LIMT_YN ( Y : 제한 / N : 혜택없음)
									mem_lms_yr = 0;
									mem_lms_mo = 0;
								}else{	//제한
									mem_lms_yr = lms_yr_able_num - lms_yr_done;
									mem_lms_mo = lms_mo_able_num - lms_mo_done;
								}
								
								if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
									card_lms_yr = 0;
									card_lms_mo = 0;
								}else{	//제한
									card_lms_yr = lms_yr_able_num-mem_lms_yr;
									card_lms_mo = lms_mo_able_num-mem_lms_mo;
								}
								lms_yr = lms_yr + mem_lms_yr;
								lms_mo = lms_mo + mem_lms_mo;
								intBkGrade = cdhd_sq2_ctgo;
							}
						}
						
					}
				}

			}
			
			if(rs1 != null) rs1.close();
			if(rs2 != null) rs2.close();
			if(rs3 != null) rs3.close();
			if(pstmt1 != null) pstmt1.close();
			if(pstmt2 != null) pstmt2.close();
			if(pstmt3 != null) pstmt3.close();				
			
			// 사이버머니 가져오기
			sql = this.getCyMoneyQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					cy_MONEY = rs1.getInt("CY_MONEY");
				}
			}
			debug(">>>> vip 리무진 할인서비스 > lms_vip_limit_yn : "+bk_yn +" / lsm_year_able : "+lms_yr + " / lms_mo_able : "+lms_mo + " / cy_Money : "+cy_MONEY + " / intBkGrade : "+intBkGrade);	
			
			result.addString("LMS_VIP_LIMT_YN" 		,bk_yn);
			result.addInt("LMS_YR" 					,lms_yr);
			result.addInt("LMS_MO" 					,lms_mo);
			result.addInt("LMS_YR_ABLE" 			,lms_yr_able_num);
			result.addInt("CY_MONEY" 				,cy_MONEY);
			result.addInt("intBkGrade" 				,intBkGrade);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}

		}

		return result;
	}	
	
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 멤버십 회원 
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t  SELECT T1.CDHD_GRD_SEQ_NO, T1.CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO, T3.GOLF_CMMN_CODE_NM	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO = T3.GOLF_CMMN_CODE	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO='0002'	AND T3.GOLF_CMMN_CLSS = '0005' \n");
		
		return sql.toString();
    }   
	    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 카드 회원
    ************************************************************************ */
    private String getCardGradeQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t  SELECT T1.CDHD_GRD_SEQ_NO, T1.CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO , T3.GOLF_CMMN_CODE_NM	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO = T3.GOLF_CMMN_CODE	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO!='0002'	AND T3.GOLF_CMMN_CLSS = '0005'	\n");
		
		return sql.toString();
    }	
	    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 혜택기간 출력
    ************************************************************************ */
    private String getDateQuery(int intMemGrade){
    	
        StringBuffer sql = new StringBuffer();
        
        //월시작일(ST_DATE_MONTH)은 신규가입시에는 가입일자(변경일자)로, 그외는 매월의 01일로 시작 
        if(intMemGrade==1 || intMemGrade==2 || intMemGrade==3 || intMemGrade==7 || intMemGrade==12){	// 멤버십 유료회원
        
			sql.append("\t      SELECT CDHD_ID, JONN_ATON	\n");
			sql.append("\t      ,     ACRG_CDHD_JONN_DATE ST_DATE	 --부킹혜택시작일 	\n");
			sql.append("\t      ,     ACRG_CDHD_END_DATE   ED_DATE    -- 부킹혜택종료일 	\n");
			
			sql.append("\t      , CASE WHEN SUBSTR(ACRG_CDHD_JONN_DATE,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
			sql.append("\t            THEN ACRG_CDHD_JONN_DATE	\n");
			sql.append("\t            ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01' 	\n");
			sql.append("\t            END ST_DATE_MONTH  --부킹혜택시작월	\n");
						
			sql.append("\t      ,    TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH --부킹혜택종료월 	\n");
			sql.append("\t      FROM BCDBA.TBGGOLFCDHD		\n");
			sql.append("\t      WHERE CDHD_ID=?		\n");
		
        }else if(intMemGrade==4){	// 멤버십 무료회원
        
			sql.append("\t    SELECT CDHD_ID, JONN_ATON	\n");
			sql.append("\t     ,    SUBSTR(JONN_ATON,1,8) ST_DATE	 --부킹혜택시작일 	\n");
			sql.append("\t     ,    TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8))+365,'YYYYMMDD')   ED_DATE    -- 부킹혜택종료일 	\n");
			
			sql.append("\t     , CASE WHEN SUBSTR(JONN_ATON,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
			sql.append("\t         THEN SUBSTR(JONN_ATON,1,8)	\n");
			sql.append("\t        ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01' 	\n");
			sql.append("\t        END ST_DATE_MONTH 	--부킹혜택시작월		\n");						
			
			sql.append("\t     ,    TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH --부킹혜택종료월 	\n");
			sql.append("\t     FROM BCDBA.TBGGOLFCDHD		\n");
			sql.append("\t     WHERE CDHD_ID= ? 		\n");			

        }else{	// 카드회원
        
			sql.append("\t  SELECT	CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH, ED_DATE_MONTH	\n");
			sql.append("\t  FROM (	\n");
			sql.append("\t  	SELECT CDHD_ID, REG_ATON AS JONN_ATON, SUBSTR(REG_ATON,1,8) ST_DATE	  --부킹혜택시작일	\n");
			sql.append("\t  	, TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8))+365,'YYYYMMDD') ED_DATE	   -- 부킹혜택종료일 		\n");
			sql.append("\t  	, CASE WHEN SUBSTR(REG_ATON,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
			sql.append("\t  	THEN SUBSTR(REG_ATON,1,8)	\n");
			sql.append("\t  	ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01'	\n");
			sql.append("\t  	END ST_DATE_MONTH	-- 부킹혜택시작월 	\n");
			sql.append("\t  	, TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH	 -- 부킹혜택종료월	\n");
			sql.append("\t  	FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
			sql.append("\t  	WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?	\n");
			sql.append("\t  )	\n");			

        }
		
		return sql.toString();
    }		
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 사이버머니
    ************************************************************************ */
    private String getCyMoneyQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) CY_MONEY	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	WHERE CDHD_ID=?	\n");
		
		return sql.toString();
    }	
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 일반부킹 - 주중
    ************************************************************************ */
    private String getNmWkdBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\t  SELECT GEN_BOKG_LIMT_YN, GEN_WKD_BOKG_NUM	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0006' AND PGRS_YN='Y' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y' 	\n");
		
		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?) AS GEN_WED_Y	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0006' AND PGRS_YN='N' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y' 	\n");
		sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		sql.append("\t	AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?) AS GEN_WED_N	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
		
		return sql.toString();
    }	
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 일반부킹 - 주말
    ************************************************************************ */
    private String getNmWkeBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\t  SELECT GEN_BOKG_LIMT_YN, GEN_WKE_BOKG_NUM	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0007' AND PGRS_YN='Y' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y' 	\n");
		
		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?) AS GEN_WKE_Y	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0007' AND PGRS_YN='N' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y' 	\n");
		sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		sql.append("\t	AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?) AS GEN_WKE_N	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
		
		return sql.toString();
    }
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 이벤트라운지 VIP부킹이벤트
    ************************************************************************ */
    private String getPreBkBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();
		

        sql.append("\t	SELECT PMI_BOKG_APO_YN							\n");
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'M')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

 		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		
 		sql.append("\t	) AS PMI_BOKG_YR_DONE	\n");
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'M')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

 		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		
 		sql.append("\t	) AS PMI_BOKG_MO_DONE	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT	\n");
 		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
	    
	    
		return sql.toString();
    }
	/** ***********************************************************************
     * Query를 생성하여 리턴한다. - 파3
     ************************************************************************ */
     private String getParBenefitQuery(String gubun){
         StringBuffer sql = new StringBuffer();
 		

 		sql.append("\t	SELECT PAR_3_BOKG_LIMT_YN, PAR_3_BOKG_YR_ABLE_NUM, PAR_3_BOKG_MO_ABLE_NUM	\n");
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

 		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		
 		sql.append("\t	) AS PAR_3_BOKG_YR_DONE	\n");
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

 		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		
 		sql.append("\t	) AS PAR_3_BOKG_MO_DONE	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT	\n");
 		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
 	    
 		return sql.toString();
     }
              
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 스카이 72
    ************************************************************************ */
    private String getSkyBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();
                

		sql.append("\t	SELECT DRDS_BOKG_LIMT_YN, DRDS_BOKG_YR_ABLE_NUM, DRDS_BOKG_MO_ABLE_NUM	\n");
		
		sql.append("\t      , (SELECT COUNT(*)\n");
		sql.append("\t      FROM BCDBA.TBGRSVTMGMT T1\n");
		sql.append("\t      JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO\n");
		sql.append("\t      JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO\n");
		sql.append("\t      LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO\n");
		sql.append("\t      WHERE T1.CDHD_ID=?\n");
		sql.append("\t      AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'S')\n");
		sql.append("\t      AND T1.RSVT_YN='Y'\n");
		sql.append("\t		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t      AND T4.GOLF_SVC_RSVT_NO IS NULL) AS DRDS_BOKG_YR_DONE\n");
		
		//-- 스카이 72 부킹 월갯수
		sql.append("\t      , (SELECT COUNT(*)\n");
		sql.append("\t      FROM BCDBA.TBGRSVTMGMT T1\n");
		sql.append("\t      JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO\n");
		sql.append("\t      JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO\n");
		sql.append("\t      LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO\n");
		sql.append("\t      WHERE T1.CDHD_ID=?\n");
		sql.append("\t      AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'S')\n");
		sql.append("\t      AND T1.RSVT_YN='Y'\n");
		sql.append("\t		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t      AND T4.GOLF_SVC_RSVT_NO IS NULL) AS DRDS_BOKG_MO_DONE\n");
		
		sql.append("\t      FROM BCDBA.TBGGOLFCDHDBNFTMGMT\n");
		sql.append("\t      WHERE CDHD_SQ2_CTGO=?\n");
	    
		return sql.toString();
    }
	/** ***********************************************************************
     * Query를 생성하여 리턴한다. - 연습장>드라이빙레인지>sky72드림골프레인지
     ************************************************************************ */
     private String getDrivingSkyBenefitQuery(String gubun){
         StringBuffer sql = new StringBuffer();
                 

 		sql.append("\t	SELECT DRGF_LIMT_YN,DRGF_YR_ABLE_NUM,DRGF_MO_ABLE_NUM,DRGF_APO_YN,CUPN_PRN_NUM	\n");
 		
 		sql.append("\t      , (SELECT COUNT(*)\n");
 		sql.append("\t      FROM BCDBA.TBGRSVTMGMT T1\n");
 		sql.append("\t      JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO\n");
 		sql.append("\t      JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO\n");
 		sql.append("\t      LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO\n");
 		sql.append("\t      WHERE T1.CDHD_ID=?\n");
 		sql.append("\t      AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'D')\n");
 		sql.append("\t      AND T1.RSVT_YN='Y'\n");
 		sql.append("\t		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

 		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND  (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}
 		
 		sql.append("\t      AND T4.GOLF_SVC_RSVT_NO IS NULL) AS DRGF_BOKG_YR_DONE				\n");
 		
 		//-- 스카이 72 부킹 월갯수
 		sql.append("\t      , (SELECT COUNT(*)\n");
 		sql.append("\t      FROM BCDBA.TBGRSVTMGMT T1\n");
 		sql.append("\t      JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO\n");
 		sql.append("\t      JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO\n");
 		sql.append("\t      LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO\n");
 		sql.append("\t      WHERE T1.CDHD_ID=?\n");
 		sql.append("\t      AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'D')\n");
 		sql.append("\t      AND T1.RSVT_YN='Y'\n");
 		sql.append("\t		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

 		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND  (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}
 		
 		sql.append("\t      AND T4.GOLF_SVC_RSVT_NO IS NULL) AS DRGF_BOKG_MO_DONE	\n");
 		
 		sql.append("\t      FROM BCDBA.TBGGOLFCDHDBNFTMGMT						\n");
 		sql.append("\t      WHERE CDHD_SQ2_CTGO=?								\n");
 	    
 		return sql.toString();
     } 
     /** ***********************************************************************
      * Query를 생성하여 리턴한다. - 연습장>드라이빙레인지>sky72드림골프레인지
      ************************************************************************ */
      private String getVipLmsBenefitQuery(String gubun){
          StringBuffer sql = new StringBuffer();
                  

  		sql.append("\t	SELECT LMS_VIP_LIMT_YN,LMS_VIP_YR_ABLE_NUM	\n");
  		//--리무진신청 년
  		sql.append("\t      , (SELECT COUNT(*)			\n");
  		sql.append("\t      FROM BCDBA.TBGAPLCMGMT T1	\n");
  		sql.append("\t      WHERE T1.CDHD_ID=?			\n");
  		sql.append("\t      AND T1.GOLF_SVC_APLC_CLSS='0002'\n");
  		sql.append("\t		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? \n");

  		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
  			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?))AS LMS_YR_DONE	\n");
  		}else{
  			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?)AS LMS_YR_DONE \n");
  		}
  		
  		//--리무진 신청 월
  		sql.append("\t      , (SELECT COUNT(*)\n");
  		sql.append("\t      FROM BCDBA.TBGAPLCMGMT T1	\n");
  		sql.append("\t      WHERE T1.CDHD_ID=?			\n");
  		sql.append("\t      AND T1.GOLF_SVC_APLC_CLSS='0002'\n");
  		sql.append("\t		AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? \n");

  		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
  			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?))AS LMS_MO_DONE	\n");
  		}else{
  			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?)AS LMS_MO_DONE \n");
  		}
  		sql.append("\t      FROM BCDBA.TBGGOLFCDHDBNFTMGMT\n"); 
  		sql.append("\t      WHERE CDHD_SQ2_CTGO=?\n");
  	    
  		return sql.toString();
      }

}
