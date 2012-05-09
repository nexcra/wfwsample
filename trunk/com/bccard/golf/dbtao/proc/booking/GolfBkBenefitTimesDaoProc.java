/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreGrMapViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 지도보기 처리
*   적용범위  : golf
*   작성일자  : 2009-06-03 
************************** 수정이력 ****************************************************************
*    일자   작성자   변경사항
*20110426   이경희  드라이빙레인지 카드소유 개수 별로 모두 체크하도록 수정
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking;

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
public class GolfBkBenefitTimesDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPermissionDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfBkBenefitTimesDaoProc() {}	

	/** 
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	//주중일반부킹
	public DbTaoResult getNmWkdBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			
			int intMemberGrade =0;	// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급
			int intBkGrade = 0;									// 차감등급
			String memb_id = "";				// 회원 아이디
			
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				 intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				 intCardGrade = userEtt.getIntCardGrade();	
				 memb_id = userEtt.getAccount();				// 회원 아이디
			}
			
			
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
			String mem_state = "N";				// 부킹 가능 여부

			debug("## getNmWkdBenefit | memb_id : "+memb_id+" | intMemberGrade : "+intMemberGrade);
			
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

						debug("일반부킹 " + cdhd_sq2_ctgo);
						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								mem_state = rs2.getString("MEM_STATE");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date);	

						
						sql = this.getNmWkdBenefitQuery("mem");
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
								gen_WKD_BOKG_NUM = rs3.getInt("GEN_WKD_BOKG_NUM");
								gen_BOKG_LIMT_YN = rs3.getString("GEN_BOKG_LIMT_YN");
								gen_WED_Y = rs3.getInt("GEN_WED_Y");
								gen_WED_N = rs3.getInt("GEN_WED_N");
								debug("MEM : gen_BOKG_LIMT_YN : " + gen_BOKG_LIMT_YN + " / gen_WKD_BOKG_NUM : " + gen_WKD_BOKG_NUM + " / gen_WED_Y : " + gen_WED_Y + " / gen_WED_N : " + gen_WED_N);
								
								if("N".equals(mem_state)){
									mem_gen_WKD_BOKG = 0;
								}else{
									if(gen_BOKG_LIMT_YN.equals("A")){	// 무제한 /  N : 혜택없음
										mem_gen_WKD_BOKG = 1;
									}else{	//제한
										mem_gen_WKD_BOKG = gen_WKD_BOKG_NUM-gen_WED_Y+gen_WED_N;
									}
								}
								gen_WKD_BOKG = gen_WKD_BOKG + mem_gen_WKD_BOKG;
								intBkGrade = cdhd_sq2_ctgo;
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
				
			}
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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);
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

				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();
				if(rs3 != null) rs3.close();
				if(pstmt1 != null) pstmt1.close();
				if(pstmt2 != null) pstmt2.close();
				if(pstmt3 != null) pstmt3.close();				
				
			}
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
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}			
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}	
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
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;		

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
			String mem_state = "N";				// 부킹 가능 여부
			
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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();		

						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								mem_state = rs2.getString("MEM_STATE");
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

								if("N".equals(mem_state)){
									mem_gen_WKE_BOKG = 0;
								}else{
									if(gen_BOKG_LIMT_YN.equals("A")){	// 무제한 /  N : 혜택없음
										mem_gen_WKE_BOKG = 1;
									}else{	//제한
										mem_gen_WKE_BOKG = gen_WKE_BOKG_NUM-gen_WKE_Y+gen_WKE_N;
									}
								}
								gen_WKE_BOKG = gen_WKE_BOKG + mem_gen_WKE_BOKG;
								intBkGrade = cdhd_sq2_ctgo;
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

			}
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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);
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

				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();
				if(rs3 != null) rs3.close();
				if(pstmt1 != null) pstmt1.close();
				if(pstmt2 != null) pstmt2.close();
				if(pstmt3 != null) pstmt3.close();				
				
			}
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
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}			
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}	
		}

		return result;
	}	

	//주중프리미엄부킹 => 연횟수 제한일 경우 => 현재 사용하지 않는다.
	public DbTaoResult getPmiWkdBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			
			int intMemberGrade =0;	// 멤버십 등급
			int intCardGrade = 0;	// 카드 등급
			int intBkGrade = 0;		// 차감등급
			String memb_id = "";	// 회원 아이디
			
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				 intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				 intCardGrade = userEtt.getIntCardGrade();		// 카드 등급
				 memb_id = userEtt.getAccount();				// 회원 아이디
			}
			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq1_ctgo = 0;				// 회원1차분류코드
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드

			int pmi_WKD_BOKG_NUM = 0;			// VIP주중부킹 가능 횟수
			int pmi_WKD_DONE = 0;				// VIP주중부킹 예약 횟수
			int cy_MONEY = 0;					// 사이버머니
			int pmi_WKD_BOKG = 0;				// VIP주중 부킹 가능 출력 횟수
			int mem_pmi_WKD_BOKG = 0;			// 멤버십 회원 VIP주중 부킹 가능 출력 횟수
			int card_pmi_WKD_BOKG = 0;			// 카드 회원 VIP주중 부킹 가능 출력 횟수
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String mem_state = "N";				// 부킹 가능 여부

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
						cdhd_sq1_ctgo = rs1.getInt("CDHD_SQ1_CTGO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq1_ctgo : 회원1차분류코드(1:카드/2:멤버십) : " + cdhd_sq1_ctgo);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);
						
						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();	
						
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								mem_state = rs2.getString("MEM_STATE");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getPmiWkdBenefitQuery("mem");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						 
						rs3 = pstmt3.executeQuery();						
									
						if(rs3 != null) {
							while(rs3.next())  {
								if("N".equals(mem_state)){
									mem_pmi_WKD_BOKG = 0;
								}else{
									pmi_WKD_BOKG_NUM = rs3.getInt("PMI_WKD_BOKG_NUM");
									pmi_WKD_DONE = rs3.getInt("PMI_WKD_DONE");
									debug("MEM : pmi_WKD_BOKG_NUM : " + pmi_WKD_BOKG_NUM + " / pmi_WKD_DONE : " + pmi_WKD_DONE);
									mem_pmi_WKD_BOKG = pmi_WKD_BOKG_NUM - pmi_WKD_DONE;
								}
								
								pmi_WKD_BOKG = pmi_WKD_BOKG + mem_pmi_WKD_BOKG;
								intBkGrade = cdhd_sq2_ctgo;
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
				
			}
			
			debug("MEM : pmi_WKD_BOKG : " + pmi_WKD_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
							}
						}
						debug("CARD : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getPmiWkdBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();
									
						if(rs3 != null) {
							while(rs3.next())  {
								
								// 혜택기간 가져오기

								pmi_WKD_BOKG_NUM = rs3.getInt("PMI_WKD_BOKG_NUM");
								pmi_WKD_DONE = rs3.getInt("PMI_WKD_DONE");
								debug("CARD : pmi_WKD_BOKG_NUM : " + pmi_WKD_BOKG_NUM + " / pmi_WKD_DONE : " + pmi_WKD_DONE);
								
								card_pmi_WKD_BOKG = pmi_WKD_BOKG_NUM-pmi_WKD_DONE;
								pmi_WKD_BOKG = pmi_WKD_BOKG + card_pmi_WKD_BOKG;
								
								if(card_pmi_WKD_BOKG>0){	// 카드 등급으로 차감 가능 하다면 카드 등급으로 차감해준다.
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
				
			}
			debug("CARD : pmi_WKD_BOKG : " + pmi_WKD_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			
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
				
			debug("total : pmi_WKD_BOKG : " + pmi_WKD_BOKG + " / cy_MONEY : " + cy_MONEY + " / intBkGrade : " + intBkGrade);	

			result.addInt("PMI_WKD_BOKG" 			,pmi_WKD_BOKG);
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


	//주말 VIP부킹 => 연횟수 제한일 경우 => 현재 사용하지 않는다.
	public DbTaoResult getPmiWkeBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			String mem_state = "N";				// 부킹 가능 여부

			int pmi_WKE_BOKG_NUM = 0;			// 주중부킹 가능 횟수
			int pmi_WKE_DONE = 0;					// 주중부킹 예약 횟수
			int cy_MONEY = 0;					// 사이버머니
			int pmi_WKE_BOKG = 0;				// 주중 부킹 가능 출력 횟수
			int mem_pmi_WKE_BOKG = 0;			// 멤버십 회원 주중 부킹 가능 출력 횟수
			int card_pmi_WKE_BOKG = 0;			// 카드 회원 주중 부킹 가능 출력 횟수
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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();		

						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								mem_state = rs2.getString("MEM_STATE");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getPmiWkeBenefitQuery("mem");
						pstmt3 = conn.prepareStatement(sql);

						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
				        
						rs3 = pstmt3.executeQuery();						
									
						if(rs3 != null) {
							while(rs3.next())  {
								if("N".equals(mem_state)){
									mem_pmi_WKE_BOKG = 0;
								}else{
									pmi_WKE_BOKG_NUM = rs3.getInt("PMI_WKE_BOKG_NUM");
									pmi_WKE_DONE = rs3.getInt("PMI_WKE_DONE");
									debug("MEM : pmi_WKE_BOKG_NUM : " + pmi_WKE_BOKG_NUM + " / pmi_WKE_DONE : " + pmi_WKE_DONE);
									mem_pmi_WKE_BOKG = pmi_WKE_BOKG_NUM-pmi_WKE_DONE;
								}
								
								pmi_WKE_BOKG = pmi_WKE_BOKG + mem_pmi_WKE_BOKG;
								intBkGrade = cdhd_sq2_ctgo;
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

			}
			debug("MEM : pmi_WKE_BOKG : " + pmi_WKE_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
							}
						}
						debug("CARD : st_date : " + st_date + " / ed_date : " + ed_date);	
		
						sql = this.getPmiWkeBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs3 = pstmt3.executeQuery();
									
						if(rs3 != null) {
							while(rs3.next())  {
								
								// 혜택기간 가져오기

								pmi_WKE_BOKG_NUM = rs3.getInt("PMI_WKE_BOKG_NUM");
								pmi_WKE_DONE = rs3.getInt("PMI_WKE_DONE");
								debug("CARD : pmi_WKE_BOKG_NUM : " + pmi_WKE_BOKG_NUM + " / pmi_WKE_DONE : " + pmi_WKE_DONE);
								
								card_pmi_WKE_BOKG = pmi_WKE_BOKG_NUM-pmi_WKE_DONE;
								pmi_WKE_BOKG = pmi_WKE_BOKG + card_pmi_WKE_BOKG;
								if(card_pmi_WKE_BOKG>0){	// 카드 등급으로 차감 가능 하다면 카드 등급으로 차감해준다.
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

			}
			debug("CARD : pmi_WKD_BOKG : " + pmi_WKE_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			
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
			debug("total : pmi_WKD_BOKG : " + pmi_WKE_BOKG + " / cy_MONEY : " + cy_MONEY + " / intBkGrade : " + intBkGrade);	

			result.addInt("PMI_WKE_BOKG" 			,pmi_WKE_BOKG);
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
	
	//주중프리미엄부킹 => 일주일에 몇번하도록 변경한다.
	public DbTaoResult getPmiWkdBenefitWeek(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			
			int intMemberGrade =0;	// 멤버십 등급
			int intCardGrade = 0;	// 카드 등급
			int intBkGrade = 0;		// 차감등급
			String memb_id = "";	// 회원 아이디
			
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				 intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				 intCardGrade = userEtt.getIntCardGrade();		// 카드 등급
				 memb_id = userEtt.getAccount();				// 회원 아이디
			}
			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq1_ctgo = 0;				// 회원1차분류코드
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			String mem_state = "N";				// 부킹 가능 여부

			int pmi_WKD_BOKG_NUM = 0;			// VIP주중부킹 가능 횟수
			int pmi_WKD_DONE = 0;				// VIP주중부킹 예약 횟수
			int cy_MONEY = 0;					// 사이버머니
			int pmi_WKD_BOKG = 0;				// VIP주중 부킹 가능 출력 횟수
			int mem_pmi_WKD_BOKG = 0;			// 멤버십 회원 VIP주중 부킹 가능 출력 횟수
			int card_pmi_WKD_BOKG = 0;			// 카드 회원 VIP주중 부킹 가능 출력 횟수
			
			String bkps_date_real = data.getString("bkps_date_real");	//부킹일자

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
						cdhd_sq1_ctgo = rs1.getInt("CDHD_SQ1_CTGO");
						cdhd_sq2_ctgo = rs1.getInt("CDHD_SQ2_CTGO");
						mem_state = rs1.getString("MEM_STATE");
						
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq1_ctgo : 회원1차분류코드(1:카드/2:멤버십) : " + cdhd_sq1_ctgo);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);
						

						if("N".equals(mem_state)){	
							pmi_WKD_BOKG = 0;
							intBkGrade = cdhd_sq2_ctgo;							
						}else{
							sql = this.getPmiWkdBenefitWeekQuery("mem");
							pstmt2 = conn.prepareStatement(sql);
							idx = 0;
							pstmt2.setString(++idx, memb_id);
							pstmt2.setInt(++idx, cdhd_sq2_ctgo);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
							 
							rs2 = pstmt2.executeQuery();						
										
							if(rs2 != null) {
								while(rs2.next())  {
									pmi_WKD_BOKG_NUM = rs2.getInt("PMI_WKD_BOKG_NUM");
									pmi_WKD_DONE = rs2.getInt("PMI_WKD_DONE");
									debug("MEM : pmi_WKD_BOKG_NUM : " + pmi_WKD_BOKG_NUM + " / pmi_WKD_DONE : " + pmi_WKD_DONE);
									mem_pmi_WKD_BOKG = pmi_WKD_BOKG_NUM - pmi_WKD_DONE;
									
									pmi_WKD_BOKG = pmi_WKD_BOKG + mem_pmi_WKD_BOKG;
									intBkGrade = cdhd_sq2_ctgo;
								}
							}
						}
					}
				}				

				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();				
				if(pstmt1 != null) pstmt1.close();
				if(pstmt2 != null) pstmt2.close();
				
			}
			debug("MEM : pmi_WKD_BOKG : " + pmi_WKD_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			

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
		
						sql = this.getPmiWkdBenefitWeekQuery("card");
						pstmt2 = conn.prepareStatement(sql);

						idx = 0;
						pstmt2.setString(++idx, memb_id);
						pstmt2.setInt(++idx, cdhd_sq2_ctgo);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs2 = pstmt2.executeQuery();
									
						if(rs2 != null) {
							while(rs2.next())  {
								
								// 혜택기간 가져오기

								pmi_WKD_BOKG_NUM = rs2.getInt("PMI_WKD_BOKG_NUM");
								pmi_WKD_DONE = rs2.getInt("PMI_WKD_DONE");
								debug("CARD : pmi_WKD_BOKG_NUM : " + pmi_WKD_BOKG_NUM + " / pmi_WKD_DONE : " + pmi_WKD_DONE);
								
								card_pmi_WKD_BOKG = pmi_WKD_BOKG_NUM-pmi_WKD_DONE;
								pmi_WKD_BOKG = pmi_WKD_BOKG + card_pmi_WKD_BOKG;
								
								if(card_pmi_WKD_BOKG>0){	// 카드 등급으로 차감 가능 하다면 카드 등급으로 차감해준다.
									intBkGrade = cdhd_sq2_ctgo;
								}
							}
						}
						
					}
				}

				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();				
				if(pstmt1 != null) pstmt1.close();
				if(pstmt2 != null) pstmt2.close();								
				
			}
			
			debug("CARD : pmi_WKD_BOKG : " + pmi_WKD_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			
			// 사이버머니 가져오기
			sql = this.getCyMoneyQuery();
			pstmt3 = conn.prepareStatement(sql);
			pstmt3.setString(1, memb_id);
			rs3 = pstmt3.executeQuery();
			
			if(rs3 != null) {
				while(rs3.next())  {
					cy_MONEY = rs3.getInt("CY_MONEY");
				}
			}
				
			debug("total : pmi_WKD_BOKG : " + pmi_WKD_BOKG + " / cy_MONEY : " + cy_MONEY + " / intBkGrade : " + intBkGrade);	

			result.addInt("PMI_WKD_BOKG" 			,pmi_WKD_BOKG);
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


	//주말 VIP부킹 => 일주일에 몇번하도록 변경한다.
	public DbTaoResult getPmiWkeBenefitWeek(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			String mem_state = "N";				// 부킹 가능 여부

			int pmi_WKE_BOKG_NUM = 0;			// 주중부킹 가능 횟수
			int pmi_WKE_DONE = 0;					// 주중부킹 예약 횟수
			int cy_MONEY = 0;					// 사이버머니
			int pmi_WKE_BOKG = 0;				// 주중 부킹 가능 출력 횟수
			int mem_pmi_WKE_BOKG = 0;			// 멤버십 회원 주중 부킹 가능 출력 횟수
			int card_pmi_WKE_BOKG = 0;			// 카드 회원 주중 부킹 가능 출력 횟수
			String bkps_date_real = data.getString("bkps_date_real");	//부킹일자
			int yoil_num = data.getInt("yoil_num");	//부킹요일

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
						mem_state = rs1.getString("MEM_STATE");
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						if("N".equals(mem_state)){	
							pmi_WKE_BOKG = 0;
							intBkGrade = cdhd_sq2_ctgo;
						}else{
							sql = this.getPmiWkeBenefitWeekQuery("mem", yoil_num);
							pstmt2 = conn.prepareStatement(sql);
	
							idx = 0;
							pstmt2.setString(++idx, memb_id);
							pstmt2.setInt(++idx, cdhd_sq2_ctgo);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, bkps_date_real);
							pstmt2.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
					        
							rs2 = pstmt2.executeQuery();						
										
							if(rs2 != null) {
								while(rs2.next())  {
									pmi_WKE_BOKG_NUM = rs2.getInt("PMI_WKE_BOKG_NUM");
									pmi_WKE_DONE = rs2.getInt("PMI_WKE_DONE");
									debug("MEM : pmi_WKE_BOKG_NUM : " + pmi_WKE_BOKG_NUM + " / pmi_WKE_DONE : " + pmi_WKE_DONE);
									
									mem_pmi_WKE_BOKG = pmi_WKE_BOKG_NUM-pmi_WKE_DONE;
									pmi_WKE_BOKG = pmi_WKE_BOKG + mem_pmi_WKE_BOKG;
									intBkGrade = cdhd_sq2_ctgo;
								}
							}
						}
					}
				}

				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();				
				if(pstmt1 != null) pstmt1.close();
				if(pstmt2 != null) pstmt2.close();								
				
			}
			debug("MEM : pmi_WKE_BOKG : " + pmi_WKE_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			

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
		
						sql = this.getPmiWkeBenefitWeekQuery("card", yoil_num);
						pstmt2 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt2.setString(++idx, memb_id);
						pstmt2.setInt(++idx, cdhd_sq2_ctgo);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, bkps_date_real);
						pstmt2.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs2 = pstmt2.executeQuery();
									
						if(rs2 != null) {
							
							while(rs2.next())  {
								
								// 혜택기간 가져오기
								pmi_WKE_BOKG_NUM = rs2.getInt("PMI_WKE_BOKG_NUM");
								pmi_WKE_DONE = rs2.getInt("PMI_WKE_DONE");
								debug("CARD : pmi_WKE_BOKG_NUM : " + pmi_WKE_BOKG_NUM + " / pmi_WKE_DONE : " + pmi_WKE_DONE);
								
								card_pmi_WKE_BOKG = pmi_WKE_BOKG_NUM-pmi_WKE_DONE;
								pmi_WKE_BOKG = pmi_WKE_BOKG + card_pmi_WKE_BOKG;
								if(card_pmi_WKE_BOKG>0){	// 카드 등급으로 차감 가능 하다면 카드 등급으로 차감해준다.
									intBkGrade = cdhd_sq2_ctgo;
								}
							}
						}
						
					}
				}
			}
			debug("CARD : pmi_WKD_BOKG : " + pmi_WKE_BOKG + " / cdhd_sq2_ctgo : " + cdhd_sq2_ctgo);	
			
			// 사이버머니 가져오기
			sql = this.getCyMoneyQuery();
			pstmt3 = conn.prepareStatement(sql);
			pstmt3.setString(1, memb_id);
			rs3 = pstmt3.executeQuery();
			if(rs3 != null) {
				while(rs3.next())  {
					cy_MONEY = rs3.getInt("CY_MONEY");
				}
			}
			debug("total : pmi_WKD_BOKG : " + pmi_WKE_BOKG + " / cy_MONEY : " + cy_MONEY + " / intBkGrade : " + intBkGrade);	

			result.addInt("PMI_WKE_BOKG" 			,pmi_WKE_BOKG);
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
	
	//파3
	public DbTaoResult getParBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		
		Connection conn = null;		
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;				

		try {
			conn = context.getDbConnection("default", null);
			
			int intMemberGrade = 0;		// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급
			int intBkMemGrade = 0;			// 차감등급
			int intBkCardGrade = 0;			// 차감등급
			int cy_MONEY = 0;			// 사이버머니 - 현재는 사용하지 않는다.
			String memb_id = "";		// 회원 아이디
			String affi_green_seq_no = data.getString("AFFI_GREEN_SEQ_NO");		// 골프장 아이디
			
			
			debug("@@@@@@@@@@@@@@@@@@@@@@@@@affi_green_seq_no :" +affi_green_seq_no );
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
			String mem_state = "N";				// 부킹 가능 여부
			
			String bk_yn = "";
			
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			
			int bk_yr_able_num_green = 0;
			int bk_mo_able_num_green = 0;
			int bk_yr_done_green = 0;
			int bk_mo_done_green = 0;
			
			int par_3_bokg_yr = 0;
			int par_3_bokg_mo = 0;
			int mem_par_3_bokg_yr = 0;
			int mem_par_3_bokg_mo = 0;
			int card_par_3_bokg_yr = 0;
			int card_par_3_bokg_mo = 0;
			
			int par_3_bokg_yr_green = 0;
			int par_3_bokg_mo_green = 0;
			int mem_par_3_bokg_yr_green = 0;
			int mem_par_3_bokg_mo_green = 0;
			int card_par_3_bokg_yr_green = 0;
			int card_par_3_bokg_mo_green = 0;
			
			int mem_temp_yr = 0;
			int mem_temp_mo = 0;
			int card_temp_yr = 0;
			int card_temp_mo = 0;
			
			debug("@@@@@@@@@intMemberGrade : "+intMemberGrade);
			debug("@@@@@@@@@intCardGrade : "+intCardGrade);
					
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
//						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
//						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
//						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
								mem_state = rs2.getString("MEM_STATE");
							}
						}
//						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
		
						sql = this.getParBenefitQuery("mem");
						pstmt3 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						pstmt3.setString(++idx, affi_green_seq_no);
						
						pstmt3.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						pstmt3.setString(++idx, affi_green_seq_no);
						
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);

						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, affi_green_seq_no);						
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, affi_green_seq_no);						
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

								bk_yr_able_num_green = rs3.getInt("PAR_3_BOKG_YR_ABLE_NUM_GREEN");
								bk_mo_able_num_green = rs3.getInt("PAR_3_BOKG_MO_ABLE_NUM_GREEN");
								bk_yr_done_green = rs3.getInt("PAR_3_BOKG_YR_DONE_GREEN");
								bk_mo_done_green = rs3.getInt("PAR_3_BOKG_MO_DONE_GREEN");
								
								debug("MEM : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num 
										+ " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done
										+ " / bk_yr_able_num_green : " + bk_yr_able_num_green + " / bk_mo_able_num_green : " + bk_mo_able_num_green
										+ " / bk_yr_done_green : " + bk_yr_done_green + " / bk_mo_done_green : " + bk_mo_done_green
										);

								if("N".equals(mem_state)){
									mem_par_3_bokg_yr = 0;
									mem_par_3_bokg_mo = 0;
									mem_par_3_bokg_yr_green = 0;
									mem_par_3_bokg_mo_green = 0;
								}else{
									if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
										mem_par_3_bokg_yr = 0;
										mem_par_3_bokg_mo = 0;
										mem_par_3_bokg_yr_green = 0;
										mem_par_3_bokg_mo_green = 0;
									}else{	//제한
										mem_par_3_bokg_yr = bk_yr_able_num-bk_yr_done;
										mem_par_3_bokg_mo = bk_mo_able_num-bk_mo_done;
										
										if(bk_yr_able_num_green > 0){				////골프장별로 연횟수 제한이 있음
											mem_par_3_bokg_yr_green = bk_yr_able_num_green-bk_yr_done_green;		//6-2 골프장별제한횟수-사용횟수
											mem_temp_yr = mem_par_3_bokg_yr_green;		//넘겨줄 구분값				//남은 횟수
										}
										else{											//골프장별로 연횟수 제한이 없음
											mem_par_3_bokg_yr_green = mem_par_3_bokg_yr;//총횟수에서 사용 횟수를 뺌
											mem_temp_yr = mem_par_3_bokg_yr_green;
										}
										if(bk_mo_able_num_green > 0 ){		////골프장별로 월횟수 제한이 있음
											mem_par_3_bokg_mo_green = bk_mo_able_num_green-bk_mo_done_green;		//6-2 골프장별제한횟수-사용횟수
											mem_temp_mo = mem_par_3_bokg_mo_green;		//넘겨줄 구분값				//남은 횟수
										}
										else{											//골프장별로 월횟수 제한이 없음
											mem_par_3_bokg_mo_green = mem_par_3_bokg_mo;//총횟수에서 사용 횟수를 뺌
											mem_temp_mo = mem_par_3_bokg_mo_green;
										}
										
									}
								} 
								par_3_bokg_yr = par_3_bokg_yr + mem_par_3_bokg_yr;
								par_3_bokg_mo = par_3_bokg_mo + mem_par_3_bokg_mo;
								par_3_bokg_yr_green = par_3_bokg_yr_green + mem_par_3_bokg_yr_green;
								par_3_bokg_mo_green = par_3_bokg_mo_green + mem_par_3_bokg_mo_green;
								intBkMemGrade = cdhd_sq2_ctgo; 
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
				
			}
			
			debug("MEM : par_3_bokg_yr : " + par_3_bokg_yr + " / par_3_bokg_mo : " + par_3_bokg_mo + " / intBkGrade : " + intBkMemGrade
					 + " / par_3_bokg_yr_green : " + par_3_bokg_yr_green + " / par_3_bokg_mo_green : " + par_3_bokg_mo_green		
			);	

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
//						debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
//						debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
//						debug("CARD : cdhd_sq2_ctgo : 회원2차 분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
							}
						}
//						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	
						
						sql = this.getParBenefitQuery("card");
						pstmt3 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, affi_green_seq_no);
						
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						pstmt3.setString(++idx, affi_green_seq_no);
						
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date);
						pstmt3.setString(++idx, ed_date);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);

						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, affi_green_seq_no);						
						pstmt3.setString(++idx, st_date_month);
						pstmt3.setString(++idx, ed_date_month);
						pstmt3.setInt(++idx, cdhd_sq2_ctgo);
						
						pstmt3.setString(++idx, memb_id);
						pstmt3.setString(++idx, affi_green_seq_no);						
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

								bk_yr_able_num_green = rs3.getInt("PAR_3_BOKG_YR_ABLE_NUM_GREEN");
								bk_mo_able_num_green = rs3.getInt("PAR_3_BOKG_MO_ABLE_NUM_GREEN");
								bk_yr_done_green = rs3.getInt("PAR_3_BOKG_YR_DONE_GREEN");
								bk_mo_done_green = rs3.getInt("PAR_3_BOKG_MO_DONE_GREEN");
								
								debug("CARD : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num 
										+ " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done
										+ " / bk_yr_able_num_green : " + bk_yr_able_num_green + " / bk_mo_able_num_green : " + bk_mo_able_num_green
										+ " / bk_yr_done_green : " + bk_yr_done_green + " / bk_mo_done_green : " + bk_mo_done_green
								);
								
								
								if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
									card_par_3_bokg_yr = 0;
									card_par_3_bokg_mo = 0;
									card_par_3_bokg_yr_green = 0;
									card_par_3_bokg_mo_green = 0;
								}else{	//제한
									card_par_3_bokg_yr = bk_yr_able_num-bk_yr_done;
									card_par_3_bokg_mo = bk_mo_able_num-bk_mo_done;
									if(bk_yr_able_num_green > 0){				////골프장별로 연횟수 제한이 있음
										card_par_3_bokg_yr_green = bk_yr_able_num_green-bk_yr_done_green;		//6-2 골프장별제한횟수-사용횟수
										card_temp_yr = card_par_3_bokg_yr_green;		//넘겨줄 구분값				//남은 횟수
									}
									else{											//골프장별로 연횟수 제한이 없음
										card_par_3_bokg_yr_green = card_par_3_bokg_yr;//총횟수에서 사용 횟수를 뺌
										card_temp_yr = card_par_3_bokg_yr_green;
									}
									if(bk_mo_able_num_green > 0 ){		////골프장별로 월횟수 제한이 있음
										card_par_3_bokg_mo_green = bk_mo_able_num_green-bk_mo_done_green;		//6-2 골프장별제한횟수-사용횟수
										card_temp_mo = card_par_3_bokg_mo_green;		//넘겨줄 구분값				//남은 횟수
									}
									else{											//골프장별로 월횟수 제한이 없음
										card_par_3_bokg_mo_green = card_par_3_bokg_mo;//총횟수에서 사용 횟수를 뺌
										card_temp_mo = card_par_3_bokg_mo_green;
									}
								}
								
								par_3_bokg_yr = par_3_bokg_yr + card_par_3_bokg_yr;
								par_3_bokg_mo = par_3_bokg_mo + card_par_3_bokg_mo;
								par_3_bokg_yr_green = par_3_bokg_yr_green + card_par_3_bokg_yr_green;
								par_3_bokg_mo_green = par_3_bokg_mo_green + card_par_3_bokg_mo_green;
								intBkCardGrade = cdhd_sq2_ctgo; 

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
				
			}
			
			/*if(bk_yr_able_num_green==0){
				par_3_bokg_yr_green = 1;
			}
			
			if(bk_mo_able_num_green==0){
				par_3_bokg_mo_green = 1;
			}*/
			
			debug("total : par_3_bokg_yr : " + par_3_bokg_yr + " / par_3_bokg_mo : " + par_3_bokg_mo + " / intBkMemGrade : " + intBkMemGrade + " / cy_MONEY : " + cy_MONEY
					+ " / par_3_bokg_yr_green : " + par_3_bokg_yr_green + " / par_3_bokg_mo_green : " + par_3_bokg_mo_green	
			);	

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
			result.addInt("PAR_3_BOKG_YR" 			,par_3_bokg_yr);
			result.addInt("PAR_3_BOKG_MO" 			,par_3_bokg_mo);
			result.addInt("PAR_3_BOKG_YR_GREEN" 	,par_3_bokg_yr_green);
			result.addInt("PAR_3_BOKG_MO_GREEN" 	,par_3_bokg_mo_green);
			result.addInt("CY_MONEY" 				,cy_MONEY);
			
			if(mem_temp_yr >0){ 
				if(mem_temp_mo >0 ) 	result.addInt("intBkGrade" 				,intBkMemGrade);
				else result.addInt("intBkGrade" 				,intBkCardGrade);
			}	
			else result.addInt("intBkGrade" 				,intBkCardGrade);	
			
			rs1.close(); pstmt1.close();   
			 
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
	
	//이벤트 라운지 > VIP 부킹이벤트
	public DbTaoResult getPreBkEvntBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt5 = null;

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
			String mem_state = "N";				// 부킹 가능 여부
			
			String bk_yn = "";
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			int pre_evnt_bokg_yr = 0;
			int pre_evnt_bokg_mo = 0;
			int mem_pre_evnt_bokg_yr = 0;
			int mem_pre_evnt_bokg_mo = 0;
			int card_pre_evnt_bokg_yr = 0;
			int card_pre_evnt_bokg_mo = 0;
			int tot_mo_able_num = 0;
			int tot_mo_done_num = 0;
			String blockData = "NO";			// 사용자 블럭 기간 조회
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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();	
						
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
								mem_state = rs2.getString("MEM_STATE");
							}
						}
						
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date + " / st_date_month : " + st_date_month + " / ed_date_month : " + ed_date_month);	

						sql = this.getBlockDateQuery();
						pstmt3 = conn.prepareStatement(sql);
						pstmt3.setString(1, memb_id);

						rs3 = pstmt3.executeQuery();		
						
						if(rs3 != null) {
							while(rs3.next())  {
								if(!"0".equals(blockData)){
									blockData = "LOCK";
								}
							}
						}
						debug("MEM : blockData : " + blockData);

						sql = this.getPreBkEvntBenefitQuery("mem");
						pstmt4 = conn.prepareStatement(sql);
						
						idx = 0;
						pstmt4.setString(++idx, memb_id);
						pstmt4.setString(++idx, st_date);
						pstmt4.setString(++idx, ed_date);
						pstmt4.setInt(++idx, cdhd_sq2_ctgo);
						pstmt4.setString(++idx, memb_id);
						pstmt4.setString(++idx, st_date_month);
						pstmt4.setString(++idx, ed_date_month);
						pstmt4.setInt(++idx, cdhd_sq2_ctgo);
						pstmt4.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs4 = pstmt4.executeQuery();			 			
									
						if(rs4 != null) {
							while(rs4.next())  {
								bk_yn = rs4.getString("PMI_EVNT_APO_YN");
								bk_mo_able_num = rs4.getInt("PMI_EVNT_NUM");
								bk_yr_done = rs4.getInt("PMI_GEN_YR_NUM");
								bk_mo_done = rs4.getInt("PMI_GEN_MO_NUM");
								debug("MEM : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);

								if("N".equals(mem_state)){
									mem_pre_evnt_bokg_yr = 0;
									mem_pre_evnt_bokg_mo = 0;
									bk_mo_able_num = 0;
								}else{
									if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
										mem_pre_evnt_bokg_yr = 0;
										mem_pre_evnt_bokg_mo = 0;
										bk_mo_able_num = 0;
									}else{	//제한
										mem_pre_evnt_bokg_yr = bk_yr_able_num-bk_yr_done;
										mem_pre_evnt_bokg_mo = bk_mo_able_num-bk_mo_done;
									}
								}
								
								pre_evnt_bokg_yr = pre_evnt_bokg_yr + mem_pre_evnt_bokg_yr;
								pre_evnt_bokg_mo = pre_evnt_bokg_mo + mem_pre_evnt_bokg_mo;
								tot_mo_able_num = tot_mo_able_num + bk_mo_able_num;
								tot_mo_done_num = tot_mo_done_num + bk_mo_done;
								intBkGrade = cdhd_sq2_ctgo;
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
			
			}
			
			debug("MEM : pre_evnt_bokg_yr : " + pre_evnt_bokg_yr + " / pre_evnt_bokg_mo : " + pre_evnt_bokg_mo + " / intBkGrade : " + intBkGrade);	

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
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

						sql = this.getBlockDateQuery();
						pstmt3 = conn.prepareStatement(sql);
						pstmt3.setString(1, memb_id);

						rs3 = pstmt3.executeQuery();		
						
						if(rs3 != null) {
							while(rs3.next())  {
								if(!"0".equals(blockData)){
									blockData = "LOCK";
								}
							}
						}
						debug("MEM : blockData : " + blockData);
						
						sql = this.getPreBkEvntBenefitQuery("card");
						pstmt4 = conn.prepareStatement(sql);
						
						idx = 0; 
						pstmt4.setString(++idx, memb_id);
						pstmt4.setString(++idx, st_date);
						pstmt4.setString(++idx, ed_date);
						pstmt4.setInt(++idx, cdhd_sq2_ctgo);
						pstmt4.setString(++idx, memb_id);
						pstmt4.setString(++idx, st_date_month);
						pstmt4.setString(++idx, ed_date_month);
						pstmt4.setInt(++idx, cdhd_sq2_ctgo);
						pstmt4.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs4 = pstmt4.executeQuery();
									
						if(rs4 != null) {
							while(rs4.next())  {
					
								bk_yn = rs4.getString("PMI_EVNT_APO_YN");
								bk_mo_able_num = rs4.getInt("PMI_EVNT_NUM");
								bk_yr_done = rs4.getInt("PMI_GEN_YR_NUM");
								bk_mo_done = rs4.getInt("PMI_GEN_MO_NUM");
								
								debug("CARD : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
								
								if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
									card_pre_evnt_bokg_yr = 0;
									card_pre_evnt_bokg_mo = 0;
									bk_mo_able_num = 0;
								}else{	//제한
									card_pre_evnt_bokg_yr = bk_yr_able_num-bk_yr_done;
									card_pre_evnt_bokg_mo = bk_mo_able_num-bk_mo_done;
								}
								pre_evnt_bokg_yr = pre_evnt_bokg_yr + card_pre_evnt_bokg_yr;
								pre_evnt_bokg_mo = pre_evnt_bokg_mo + card_pre_evnt_bokg_mo;
								tot_mo_able_num = tot_mo_able_num+bk_mo_able_num;
								tot_mo_done_num = tot_mo_done_num + bk_mo_done;
								intBkGrade = cdhd_sq2_ctgo;
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
				
			}
			debug("CARD : pre_evnt_bokg_yr : " + pre_evnt_bokg_yr + " / pre_evnt_bokg_mo : " + pre_evnt_bokg_mo + " / intBkGrade : " + intBkGrade);	
			
			// 사이버머니 가져오기 
			sql = this.getCyMoneyQuery();
			pstmt5 = conn.prepareStatement(sql);
			pstmt5.setString(1, memb_id);
			rs5 = pstmt5.executeQuery();
			if(rs5 != null) {
				while(rs5.next())  {
					cy_MONEY = rs5.getInt("CY_MONEY");
				}
			}
			debug("total : pre_evnt_bokg_yr : " + pre_evnt_bokg_yr + " / pre_evnt_bokg_mo : " + pre_evnt_bokg_mo + " / intBkGrade : " + intBkGrade + " / cy_MONEY : " + cy_MONEY);	
			
			//총 사용할 수 있는 건수 :월
			result.addInt("PRE_EVNT_PMI_NUM"		,tot_mo_able_num);
			//result.addInt("PRE_EVNT_BOKG_YR" 		,pre_evnt_bokg_yr);
			//남은 건수 : 월
			result.addInt("PRE_EVNT_BOKG_MO" 		,pre_evnt_bokg_mo);
			//이미사용건수 : 월
			result.addInt("PRE_EVNT_BOKG_DONE"		,tot_mo_done_num);
			result.addString("blockDate" 			,blockData);
			
			result.addInt("CY_MONEY" 				,cy_MONEY);
			result.addInt("intBkGrade" 				,intBkGrade);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {

			try { if(rs1 != null) rs1.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(rs3 != null) rs3.close(); } catch (Exception ignored) {}			
			try { if(rs4 != null) rs4.close(); } catch (Exception ignored) {}
			try { if(rs5 != null) rs5.close(); } catch (Exception ignored) {}
			try { if(pstmt1 != null) pstmt1.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}			
			try { if(pstmt4 != null) pstmt4.close(); } catch (Exception ignored) {}
			try { if(pstmt5 != null) pstmt5.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}			

		}

		return result;
	}	

	//이벤트 라운지 > VIP 부킹이벤트 (20091230)
	public DbTaoResult getPreBkEvntBenefit2(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			
			int cnt_pmi_evnt_num = 0;
			int cnt_pmi_gen_mo_num = 0;
			int cy_MONEY = 0;
			int intMemberGrade = 0;		// 멤버십 등급
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
			
			int pre_evnt_bokg_yr = 0;
			int pre_evnt_bokg_mo = 0;

			String blockData = "NO";			// 사용자 블럭 기간 조회
			
			// 총 사용 가능 횟수 가져오기
			sql = this.getUseTotalQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					cnt_pmi_evnt_num = rs1.getInt("CNT_PMI_EVNT_NUM");
					debug("============ cnt_pmi_evnt_num : " + cnt_pmi_evnt_num);
				}
			}

			// 이번달 신청한 총 갯수
			sql = this.getPreBkEvntBenefitQuery2();
			pstmt2 = conn.prepareStatement(sql);
			
			idx = 0;
			pstmt2.setString(++idx, memb_id);
			
			rs2 = pstmt2.executeQuery();			 			
						
			if(rs2 != null) {
				while(rs2.next())  {
					cnt_pmi_gen_mo_num = rs2.getInt("CNT_PMI_GEN_MO_NUM");
					debug("=========== cnt_pmi_gen_mo_num : " + cnt_pmi_gen_mo_num);
				}
			}

			sql = this.getBlockDateQuery();
			pstmt3 = conn.prepareStatement(sql);
			pstmt3.setString(1, memb_id);

			rs3 = pstmt3.executeQuery();		
			
			if(rs3 != null) {
				while(rs3.next())  {
					if(!"0".equals(blockData)){
						blockData = "LOCK";
					}
				}
			}
			debug("======================== blockData : " + blockData);
			
			// 사이버머니 가져오기 
			sql = this.getCyMoneyQuery();
			pstmt4 = conn.prepareStatement(sql);
			pstmt4.setString(1, memb_id);
			rs4 = pstmt4.executeQuery();
			if(rs4 != null) {
				while(rs4.next())  {
					cy_MONEY = rs4.getInt("CY_MONEY");
				}
			}
			debug("total : pre_evnt_bokg_yr : " + pre_evnt_bokg_yr + " / pre_evnt_bokg_mo : " + pre_evnt_bokg_mo + " / intBkGrade : " + intBkGrade + " / cy_MONEY : " + cy_MONEY);	
			
			pre_evnt_bokg_mo = cnt_pmi_evnt_num - cnt_pmi_gen_mo_num;
			//총 사용할 수 있는 건수 :월
			result.addInt("PRE_EVNT_PMI_NUM"		,cnt_pmi_evnt_num);
			//result.addInt("PRE_EVNT_BOKG_YR" 		,pre_evnt_bokg_yr);
			//남은 건수 : 월
			result.addInt("PRE_EVNT_BOKG_MO" 		,pre_evnt_bokg_mo);
			//이미사용건수 : 월
			result.addInt("PRE_EVNT_BOKG_DONE"		,cnt_pmi_gen_mo_num);
			result.addString("blockDate" 			,blockData);
			
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
	
	//Sky72  드림듄스
	public DbTaoResult getSkyBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;
		
		Connection conn = null;		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;					
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt5 = null;		

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
			String mem_state = "N";				// 부킹 가능 여부
			
			String bk_yn = "";
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			
			int drds_bokg_yr = 0;
			int drds_bokg_mo = 0;
			int mem_drds_bokg_yr = 0;
			int mem_drds_bokg_mo = 0;
			int card_drds_bokg_yr = 0;
			int card_drds_bokg_mo = 0;

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
								mem_state = rs2.getString("MEM_STATE");
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

								if("N".equals(mem_state)){
									mem_drds_bokg_yr = 0;
									mem_drds_bokg_mo = 0;
								}else{
									if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)
										mem_drds_bokg_yr = 0;
										mem_drds_bokg_mo = 0;
									}else{	//제한
										mem_drds_bokg_yr = bk_yr_able_num-bk_yr_done;
										mem_drds_bokg_mo = bk_mo_able_num-bk_mo_done;
									}
								}
								drds_bokg_yr = drds_bokg_yr + mem_drds_bokg_yr;
								drds_bokg_mo = drds_bokg_mo + mem_drds_bokg_mo;
								intBkGrade = cdhd_sq2_ctgo;
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
				
			}
			
			debug("MEM : drds_bokg_yr : " + drds_bokg_yr + " / drds_bokg_mo : " + drds_bokg_mo + " / intBkGrade : " + intBkGrade);	

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
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
									card_drds_bokg_yr = 0;
									card_drds_bokg_mo = 0;
								}else{	//제한
									card_drds_bokg_yr = bk_yr_able_num-bk_yr_done;
									card_drds_bokg_mo = bk_mo_able_num-bk_mo_done;
								}
								drds_bokg_yr = drds_bokg_yr + card_drds_bokg_yr;
								drds_bokg_mo = drds_bokg_mo + card_drds_bokg_mo;
								intBkGrade = cdhd_sq2_ctgo;
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
				
			}
			debug("CARD : drds_bokg_yr : " + drds_bokg_yr + " / drds_bokg_mo : " + drds_bokg_mo + " / intBkGrade : " + intBkGrade);	
			
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
			debug("total : drds_bokg_yr : " + drds_bokg_yr + " / drds_bokg_mo : " + drds_bokg_mo + " / intBkGrade : " + intBkGrade + " / cy_MONEY : " + cy_MONEY);	

			result.addInt("DRDS_BOKG_YR" 			,drds_bokg_yr);
			result.addInt("DRDS_BOKG_MO" 			,drds_bokg_mo);
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
	
	
	//연습장라운지 > 드라이빙레인지 할인 > sky72드림골프레인지
	public DbTaoResult getDrivingSkyBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			int intMemberGrade = 0; 	//멤버십등급
			int intCardGrade = 0;		//카드 등급
			int intBkGrade = 0;	  		//멤버쉽차감등급
			int intBkCardGrade = 0;		//카드차감등급
			int cy_MONEY = 0;
			String memb_id = ""; //회원아이디
			
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			if(userEtt != null){
				intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				intCardGrade = userEtt.getIntCardGrade();		// 카드 등급
				memb_id = userEtt.getAccount();				// 회원 아이디
			}	
			
			//intMemberGrade = 1;
			//intCardGrade = 1;
			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
			String st_date_month = "";			// 부킹 혜택 시작 일시(월제한)
			String ed_date_month = "";			// 부킹 혜택 종료 일시(월제한)
			String mem_state = "N";				// 부킹 가능 여부
			
			String bk_yn = "";
			String bk_apo_yn = "";
			String cupn_prn_num = "0";
			int bk_yr_able_num = 0;
			int bk_mo_able_num = 0;
			int bk_yr_done = 0;
			int bk_mo_done = 0;
			
			int drgf_bokg_yr = 0;
			int drgf_bokg_mo = 0;
			int mem_drgf_bokg_yr = 0;
			int mem_drgf_bokg_mo = 0;
			int card_drgf_bokg_yr = 0;
			int card_drgf_bokg_mo = 0;

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();
						
						if(rs2 != null) {
							
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
								mem_state = rs2.getString("MEM_STATE");
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
								bk_mo_done = rs3.getInt("DRGF_BOKG_MO_DONE");
								cupn_prn_num = rs3.getString("CUPN_PRN_NUM");
								debug("MEM : bk_yn : " + bk_yn + " / bk_yr_able_num : " + bk_yr_able_num + " / bk_mo_able_num : " + bk_mo_able_num + " / bk_yr_done : " + bk_yr_done + " / bk_mo_done : " + bk_mo_done);
								debug (" @@---------01 :mem_state:"+mem_state);
								if("N".equals(mem_state)){debug (" @@---------02 :mem_state:"+mem_state);
									mem_drgf_bokg_yr = 0;
									mem_drgf_bokg_mo = 0;
								}else{
									debug (" @@---------03 :bk_yn:"+bk_yn);
									if(bk_yn.equals("N")){	// PAR_BK_LMT_YN ( Y : 제한 / N : 혜택없음)							
										mem_drgf_bokg_yr = 0;
										mem_drgf_bokg_mo = 0;
									}else{	//제한
										
										debug (" @@--------1 : " + bk_yr_able_num + ", " + bk_yr_done);
										mem_drgf_bokg_yr = bk_yr_able_num-bk_yr_done;
										debug (" @@--------11 : " + mem_drgf_bokg_yr);
										debug (" ");
										debug (" @@--------2 : " + bk_mo_able_num + ", " + bk_mo_done);
										mem_drgf_bokg_mo = bk_mo_able_num-bk_mo_done;
										debug (" @@--------22 : " + mem_drgf_bokg_mo);
									}
								}
								drgf_bokg_yr = drgf_bokg_yr + mem_drgf_bokg_yr;
								drgf_bokg_mo = drgf_bokg_mo + mem_drgf_bokg_mo;
								intBkGrade = cdhd_sq2_ctgo;
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
				
			}
			
			debug("MEM : drds_bokg_yr : " + drgf_bokg_yr + " / drds_bokg_mo : " + drgf_bokg_mo + " / intBkGrade : " + intBkGrade);	

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
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
								
								if(bk_yn.equals("N")){	// DRGF_LIMT_YN ( Y : 제한 / N : 혜택없음)
									card_drgf_bokg_yr = 0;
									card_drgf_bokg_mo = 0;
								}else{	//제한
									card_drgf_bokg_yr = bk_yr_able_num-bk_yr_done;
									card_drgf_bokg_mo = bk_mo_able_num-bk_mo_done;
								}
								drgf_bokg_yr = drgf_bokg_yr + card_drgf_bokg_yr;
								drgf_bokg_mo = drgf_bokg_mo + card_drgf_bokg_mo;
								intBkCardGrade = cdhd_sq2_ctgo;
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
				
			}
			
			debug("CARD : drgf_bokg_yr : " + drgf_bokg_yr + " / drgf_bokg_mo : " + drgf_bokg_mo + " / intBkCardGrade : " + intBkCardGrade);	
			
			// 사이버머니 가져오기
			sql = this.getCyMoneyQuery();
			pstmt4 = conn.prepareStatement(sql);
			pstmt4.setString(1, memb_id);
			rs4 = pstmt4.executeQuery();
			
			if(rs4 != null) {
				
				while(rs4.next())  {
					cy_MONEY = rs4.getInt("CY_MONEY");
				}
			}
			
			debug("total : drgf_bokg_yr : " + drgf_bokg_yr + " / drgf_bokg_mo : " + drgf_bokg_mo + " / intBkGrade : " + intBkGrade + " / intBkCardGrade : " + intBkCardGrade + " / cy_MONEY : " + cy_MONEY);	
			
			result.addString("DRGF_LMT_YN" 			,bk_yn);
			result.addString("DRGF_APO_YN" 			,bk_apo_yn);
			result.addString("CUPN_PRN_NUM" 		,cupn_prn_num);
			result.addInt("DRGF_BOKG_YR" 			,drgf_bokg_yr);
			result.addInt("DRGF_BOKG_MO" 			,drgf_bokg_mo);
			result.addInt("CY_MONEY" 				,cy_MONEY);		
			
			if(drgf_bokg_yr >0){  
				if(drgf_bokg_mo >0 ) 	result.addInt("intBkGrade", intBkGrade);
				else result.addInt("intBkGrade", intBkCardGrade);
			}	
			else {
				result.addInt("intBkGrade", intBkCardGrade);
			}
			
			if(rs4 != null) rs4.close();
			if(pstmt4 != null) pstmt4.close();			
			 
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
			String mem_state = "N";				// 부킹 가능 여부
			
			String bk_yn = "";
			String bk_apo_yn = "";
			String cupn_prn_num = "0";

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						rs2 = pstmt2.executeQuery();		
						if(rs2 != null) {
							while(rs2.next())  {
								st_date = rs2.getString("ST_DATE");
								ed_date = rs2.getString("ED_DATE");
								st_date_month = rs2.getString("ST_DATE_MONTH");
								ed_date_month = rs2.getString("ED_DATE_MONTH");
								mem_state = rs2.getString("MEM_STATE");
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

								if("N".equals(mem_state)){
									mem_lms_yr = 0;
									mem_lms_mo = 0;
								}else{
									if(bk_yn.equals("N")){	// LMS_VIP_LIMT_YN ( Y : 제한 / N : 혜택없음)
										mem_lms_yr = 0;
										mem_lms_mo = 0;
									}else{	//제한
										mem_lms_yr = lms_yr_able_num - lms_yr_done;
										mem_lms_mo = lms_mo_able_num - lms_mo_done;
									}
								}
								lms_yr = lms_yr + mem_lms_yr;
								lms_mo = lms_mo + mem_lms_mo;
								intBkGrade = cdhd_sq2_ctgo;
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
			}
			

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

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt2 = conn.prepareStatement(sql);
						pstmt2.setString(1, memb_id);
						pstmt2.setString(2, cdhd_ctgo_seq_no);	// 카드회원만 추가
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
				
				if(rs1 != null) rs1.close();
				if(rs2 != null) rs2.close();
				if(rs3 != null) rs3.close();			
				if(pstmt1 != null) pstmt1.close();
				if(pstmt2 != null) pstmt2.close();
				if(pstmt3 != null) pstmt3.close();	
				
			}
				
			
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
	

	//이벤트 라운지 > VIP 부킹이벤트
	public DbTaoResult getAdmPreBkEvntBenefit(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
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
			
			int cnt_pmi_evnt_num = 0;
			int cnt_pmi_gen_mo_num = 0;
			int cy_MONEY = 0;
			//int intMemberGrade = Integer.parseInt(data.getString("intMemGrade"));	// 멤버십 등급
			//int intCardGrade = 0;		// 카드 등급
			int intBkGrade = 0;									// 차감등급
			String memb_id = data.getString("userId");				// 회원 아이디

			int pre_evnt_bokg_yr = 0;
			int pre_evnt_bokg_mo = 0;

			String blockData = "NO";			// 사용자 블럭 기간 조회
			
			// 총 사용 가능 횟수 가져오기
			sql = this.getUseTotalQuery();
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, memb_id);
			rs1 = pstmt1.executeQuery();
			if(rs1 != null) {
				while(rs1.next())  {
					cnt_pmi_evnt_num = rs1.getInt("CNT_PMI_EVNT_NUM");
					debug("============ cnt_pmi_evnt_num : " + cnt_pmi_evnt_num);
				}
			}

			// 이번달 신청한 총 갯수
			sql = this.getPreBkEvntBenefitQuery2();
			pstmt2 = conn.prepareStatement(sql);
			
			idx = 0;
			pstmt2.setString(++idx, memb_id);
			
			rs2 = pstmt2.executeQuery();			 			
						
			if(rs2 != null) {
				while(rs2.next())  {
					cnt_pmi_gen_mo_num = rs2.getInt("CNT_PMI_GEN_MO_NUM");
					debug("=========== cnt_pmi_gen_mo_num : " + cnt_pmi_gen_mo_num);
				}
			}

			sql = this.getBlockDateQuery();
			pstmt3 = conn.prepareStatement(sql);
			pstmt3.setString(1, memb_id);

			rs3 = pstmt3.executeQuery();		
			
			if(rs3 != null) {
				while(rs3.next())  {
					if(!"0".equals(blockData)){
						blockData = "LOCK";
					}
				}
			}
			debug("======================== blockData : " + blockData);
			
			// 사이버머니 가져오기 
			sql = this.getCyMoneyQuery();
			pstmt4 = conn.prepareStatement(sql);
			pstmt4.setString(1, memb_id);
			rs4 = pstmt4.executeQuery();
			if(rs4 != null) {
				while(rs4.next())  {
					cy_MONEY = rs4.getInt("CY_MONEY");
				}
			}
			debug("total : pre_evnt_bokg_yr : " + pre_evnt_bokg_yr + " / pre_evnt_bokg_mo : " + pre_evnt_bokg_mo + " / intBkGrade : " + intBkGrade + " / cy_MONEY : " + cy_MONEY);	
			
			pre_evnt_bokg_mo = cnt_pmi_evnt_num - cnt_pmi_gen_mo_num;
			//총 사용할 수 있는 건수 :월
			result.addInt("PRE_EVNT_PMI_NUM"		,cnt_pmi_evnt_num);
			//result.addInt("PRE_EVNT_BOKG_YR" 		,pre_evnt_bokg_yr);
			//남은 건수 : 월
			result.addInt("PRE_EVNT_BOKG_MO" 		,pre_evnt_bokg_mo);
			//이미사용건수 : 월
			result.addInt("PRE_EVNT_BOKG_DONE"		,cnt_pmi_gen_mo_num);
			result.addString("blockDate" 			,blockData);
			
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

	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 총 사용 가능 횟수 
    ************************************************************************ */
    private String getUseTotalQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT NVL(SUM(PMI_EVNT_NUM), 0) as CNT_PMI_EVNT_NUM								\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1													\n");
		sql.append("\t	inner JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	inner join BCDBA.TBGGOLFCDHDBNFTMGMT T3 on T3.CDHD_SQ2_CTGO=T2.CDHD_SQ2_CTGO		\n");
		sql.append("\t	inner join BCDBA.TBGGOLFCDHD T4 on T4.CDHD_ID=T1.CDHD_ID							\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T3.PMI_EVNT_APO_YN='Y'										\n");
		//sql.append("\t	WHERE T1.CDHD_ID='simijoa81' AND T3.PMI_EVNT_APO_YN='Y' AND T2.CDHD_SQ1_CTGO!='0002'	\n");
		sql.append("\t		and (																			\n");
		// 멤버쉽 회원 정보일 경우 회원테이블의 ACRG_CDHD_END_DATE 날짜와 비교 (ACRG_CDHD_END_DATE 이컬러메 날짜가 있다는건 멤버쉽회원 정보라는것)
		sql.append("\t			T4.CDHD_CTGO_SEQ_NO=T1.CDHD_CTGO_SEQ_NO and T4.ACRG_CDHD_END_DATE>=TO_CHAR(SYSDATE,'YYYYMM')	\n");
		sql.append("\t				or					\n");
		// 카드회원 정보일 경우 회원등급관리(TBGGOLFCDHDGRDMGMT)의 등록일자+365일 로 비교
		sql.append("\t			T4.CDHD_CTGO_SEQ_NO<>T1.CDHD_CTGO_SEQ_NO and TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON,1,8))+365,'YYYYMMDD')>=TO_CHAR(SYSDATE,'YYYYMM')		\n");
		sql.append("\t		)																				\n");
		return sql.toString();
    }   
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 멤버십 회원 
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT CDHD_GRD_SEQ_NO, CDHD_CTGO_SEQ_NO, CDHD_SQ2_CTGO, CDHD_SQ1_CTGO	\n");
		sql.append("\t	, CASE WHEN ED_DATE<TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END MEM_STATE	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT T1.CDHD_GRD_SEQ_NO, T1.CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO, T2.CDHD_SQ1_CTGO	\n");
		sql.append("\t	        , CASE WHEN ACRG_CDHD_JONN_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE)-365,'YYYYMMDD')	\n");
		sql.append("\t	        ELSE ACRG_CDHD_JONN_DATE END ST_DATE	\n");
		sql.append("\t	        , CASE WHEN ACRG_CDHD_JONN_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE)-365,'YYYYMMDD')	\n");
		sql.append("\t	    ELSE ACRG_CDHD_END_DATE END ED_DATE	\n");
		sql.append("\t	    FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD T3 ON T1.CDHD_ID=T3.CDHD_ID	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	)	\n");
		
		return sql.toString();
    }   
	    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 카드 회원
    ************************************************************************ */
    private String getCardGradeQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT T1.CDHD_GRD_SEQ_NO, T1.CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO!='0002'	\n");
		
		return sql.toString();
    }	
	    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 혜택기간 출력
    ************************************************************************ */
 /*   private String getDateQuery(int intMemGrade, String memGbun){
        StringBuffer sql = new StringBuffer();
        
        sql.append("	\n");
		
        //월시작일(ST_DATE_MONTH)은 신규가입시에는 가입일자(변경일자)로, 그외는 매월의 01일로 시작
        if("mem".equals(memGbun)){	// 멤버십 회원
        	if(intMemGrade==4){	// 무료회원기간
    			
         		sql.append("\t	SELECT CDHD_ID, JONN_ATON, 'Y' MEM_STATE	\n");
    			sql.append("\t     ,    SUBSTR(JONN_ATON,1,8) ST_DATE 	\n");
    			sql.append("\t     ,    TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8))+365,'YYYYMMDD')   ED_DATE 	\n");
    			
    			sql.append("\t		,CASE WHEN SUBSTR(JONN_ATON,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");    			
    			sql.append("\t			THEN SUBSTR(JONN_ATON,1,8) 	\n");
    			sql.append("\t			ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01' 	\n");
    			sql.append("\t			END ST_DATE_MONTH	\n");        			

    			sql.append("\t     ,    TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH 	\n");
    			sql.append("\t     FROM BCDBA.TBGGOLFCDHD		\n");
    			sql.append("\t     WHERE CDHD_ID= ? 		\n");    			
        		
        	}else{	// 유료회원기간        		
    			
        		sql.append("\t	SELECT CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH, ED_DATE_MONTH	\n");
        		sql.append("\t   , CASE WHEN ED_DATE<TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END MEM_STATE	\n");
        		sql.append("\t   FROM (	\n");
    			sql.append("\t   	SELECT CDHD_ID, JONN_ATON	\n");    			
    			sql.append("\t		    ,ACRG_CDHD_JONN_DATE ST_DATE	\n");    			
    			sql.append("\t		    ,ACRG_CDHD_END_DATE ED_DATE	\n");
    			
    			sql.append("\t		,CASE WHEN SUBSTR(ACRG_CDHD_JONN_DATE,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
    			sql.append("\t			THEN ACRG_CDHD_JONN_DATE 	\n");
    			sql.append("\t			ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01' 	\n");
    			sql.append("\t			END ST_DATE_MONTH	\n");
    			
    			sql.append("\t		, TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH	\n");
    			sql.append("\t		FROM BCDBA.TBGGOLFCDHD	\n");
    			sql.append("\t		WHERE CDHD_ID=?	\n");
    			sql.append("\t   )	\n");      			

        	}
        }else{	// 카드회원

        	sql.append("\t	SELECT  CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, 'Y' MEM_STATE, ST_DATE_MONTH, ED_DATE_MONTH \n");
            sql.append("\t	FROM ( \n");
            sql.append("\t		SELECT CDHD_ID, REG_ATON AS JONN_ATON, SUBSTR(REG_ATON,1,8) ST_DATE	 \n");
            sql.append("\t	 	, TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8))+365,'YYYYMMDD') ED_DATE	\n");
            sql.append("\t	 	, CASE WHEN SUBSTR(REG_ATON,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
            sql.append("\t	 			THEN SUBSTR(REG_ATON,1,8)	\n");
            sql.append("\t	 			ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01'	\n");
            sql.append("\t	 			END ST_DATE_MONTH\n");
            sql.append("\t	 	, TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH\n");
            sql.append("\t	 	FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
			sql.append("\t      WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?	\n");
			sql.append("\t 	)\n");

        }
        
		return sql.toString();
    }
    */
    
    private String getDateQuery(int intMemGrade, String memGbun){
        StringBuffer sql = new StringBuffer();
        
        sql.append("	\n");
		
        //월시작일(ST_DATE_MONTH)은 신규가입시에는 가입일자(변경일자)로, 그외는 매월의 01일로 시작
        if("mem".equals(memGbun)){	// 멤버십 회원
        	if(intMemGrade==4){	// 무료회원기간
    			
         		sql.append("\t	SELECT CDHD_ID, JONN_ATON, 'Y' MEM_STATE	\n");
    			sql.append("\t     ,    SUBSTR(JONN_ATON,1,8) ST_DATE 	\n");
    			sql.append("\t     ,    TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8))+365,'YYYYMMDD')   ED_DATE 	\n");
    			
    			sql.append("\t		,CASE WHEN SUBSTR(JONN_ATON,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");    			
    			sql.append("\t			THEN SUBSTR(JONN_ATON,1,8) 	\n");
    			sql.append("\t			ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01' 	\n");
    			sql.append("\t			END ST_DATE_MONTH	\n");        			

    			sql.append("\t     ,    TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH 	\n");
    			sql.append("\t     FROM BCDBA.TBGGOLFCDHD		\n");
    			sql.append("\t     WHERE CDHD_ID= ? 		\n");    			
        		
        	}else{	// 유료회원기간        		
    			
        		sql.append("\t	SELECT CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH, ED_DATE_MONTH	\n");
        		sql.append("\t   , CASE WHEN ED_DATE<TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END MEM_STATE	\n");
        		sql.append("\t   FROM (	\n");
    			sql.append("\t   	SELECT CDHD_ID, JONN_ATON	\n");
    			sql.append("\t		, CASE WHEN ACRG_CDHD_JONN_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE)-365,'YYYYMMDD')	\n");
    			sql.append("\t		    ELSE ACRG_CDHD_JONN_DATE END ST_DATE	\n");
    			sql.append("\t		, CASE WHEN ACRG_CDHD_JONN_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE)-365,'YYYYMMDD')	\n");
    			sql.append("\t		    ELSE ACRG_CDHD_END_DATE END ED_DATE	\n");
    			
    			sql.append("\t		,CASE WHEN SUBSTR(ACRG_CDHD_JONN_DATE,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
    			sql.append("\t			THEN ACRG_CDHD_JONN_DATE 	\n");
    			sql.append("\t			ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01' 	\n");
    			sql.append("\t			END ST_DATE_MONTH	\n");
    			
    			sql.append("\t		, TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH	\n");
    			sql.append("\t		FROM BCDBA.TBGGOLFCDHD	\n");
    			sql.append("\t		WHERE CDHD_ID=?	\n");
    			sql.append("\t   )	\n");      			

        	}
        }else{	// 카드회원

        	sql.append("\t	SELECT  CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, 'Y' MEM_STATE, ST_DATE_MONTH, ED_DATE_MONTH \n");
            sql.append("\t	FROM ( \n");
            sql.append("\t		SELECT CDHD_ID, REG_ATON AS JONN_ATON, SUBSTR(REG_ATON,1,8) ST_DATE	 \n");
            sql.append("\t	 	, TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8))+365,'YYYYMMDD') ED_DATE	\n");
            sql.append("\t	 	, CASE WHEN SUBSTR(REG_ATON,1,6)=TO_CHAR(SYSDATE,'YYYYMM')	\n");
            sql.append("\t	 			THEN SUBSTR(REG_ATON,1,8)	\n");
            sql.append("\t	 			ELSE TO_CHAR(SYSDATE,'YYYYMM') ||'01'	\n");
            sql.append("\t	 			END ST_DATE_MONTH\n");
            sql.append("\t	 	, TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH\n");
            sql.append("\t	 	FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
			sql.append("\t      WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?	\n");
			sql.append("\t 	)\n");

        }
        
		return sql.toString();
    }	    
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 사이버머니
    ************************************************************************ */
    private String getCyMoneyQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) CY_MONEY	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	WHERE CDHD_ID=?	\n");
		
		return sql.toString();
    }	
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - VIP부킹 - 주중 
    ************************************************************************ */
    private String getPmiWkdBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT PMI_WKD_BOKG_NUM	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE SUBSTR(T1.GOLF_SVC_RSVT_NO, 5, 1)='M' AND T1.CDHD_ID=? AND T1.RSVT_YN='Y'	\n");
		sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)>=? AND SUBSTR(T1.REG_ATON,1,8)<=?	\n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	AND TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE),'D') NOT IN (7,1)) AS PMI_WKD_DONE	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
		
		return sql.toString();
    }	
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - VIP부킹 - 주말
    ************************************************************************ */
    private String getPmiWkeBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT PMI_WKE_BOKG_NUM	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE SUBSTR(T1.GOLF_SVC_RSVT_NO, 5, 1)='M' AND T1.CDHD_ID=? AND T1.RSVT_YN='Y'	\n");
		sql.append("\t	AND SUBSTR(T1.REG_ATON,1,8)>=? AND SUBSTR(T1.REG_ATON,1,8)<=?	\n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	AND TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE),'D') IN (7,1)) AS PMI_WKE_DONE	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
		
		return sql.toString();
    }
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - VIP부킹 - 주중 
    ************************************************************************ */
    private String getPmiWkdBenefitWeekQuery(String gubun){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT PMI_WKD_BOKG_NUM	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE SUBSTR(T1.GOLF_SVC_RSVT_NO, 5, 1)='M' AND T1.CDHD_ID=? AND T1.RSVT_YN='Y'	\n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}

		sql.append("\t	AND T3.BOKG_ABLE_DATE>=?-TO_CHAR(TO_DATE(?), 'D')+2	\n");
		sql.append("\t	AND T3.BOKG_ABLE_DATE<=?-TO_CHAR(TO_DATE(?), 'D')+6	\n");
		sql.append("\t	) AS PMI_WKD_DONE	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
		
		return sql.toString();
    }	
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - VIP부킹 - 주말
    ************************************************************************ */
    private String getPmiWkeBenefitWeekQuery(String gubun, int yoil_num){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT PMI_WKE_BOKG_NUM	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE SUBSTR(T1.GOLF_SVC_RSVT_NO, 5, 1)='M' AND T1.CDHD_ID=? AND T1.RSVT_YN='Y'	\n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		if(yoil_num==7){
			sql.append("\t	AND T3.BOKG_ABLE_DATE>=?-TO_CHAR(TO_DATE(?), 'D')+7	\n");
			sql.append("\t	AND T3.BOKG_ABLE_DATE<=?-TO_CHAR(TO_DATE(?), 'D')+8	\n");
		}else{
			sql.append("\t	AND T3.BOKG_ABLE_DATE>=?-TO_CHAR(TO_DATE(?), 'D')	\n");
			sql.append("\t	AND T3.BOKG_ABLE_DATE<=?-TO_CHAR(TO_DATE(?), 'D')+1	\n");
		}
		sql.append("\t	) AS PMI_WKE_DONE	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
		
		return sql.toString();
    }
	
    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 일반부킹 - 주중
    ************************************************************************ */
    private String getNmWkdBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT GEN_BOKG_LIMT_YN, GEN_WKD_BOKG_NUM	\n");
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

        sql.append("	\n");
		sql.append("\t	SELECT GEN_BOKG_LIMT_YN, GEN_WKE_BOKG_NUM	\n");
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
    private String getPreBkEvntBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
		sql.append("\t	SELECT PMI_EVNT_APO_YN,PMI_EVNT_NUM						\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT T1			\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_APLC_CLSS='9001'		\n");
		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=?  AND (T1.PGRS_YN='B' OR T1.PGRS_YN='E') AND NVL(NUM_DDUC_YN,'Y')='Y'  \n");

		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?						\n");
		}
		sql.append("\t	) AS PMI_GEN_YR_NUM										\n");
		
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT T1			\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_APLC_CLSS='9001'		\n");
		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=?  AND (T1.PGRS_YN='B' OR T1.PGRS_YN='E') AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		
		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?						\n");
		}
		sql.append("\t	) AS PMI_GEN_MO_NUM										\n");
		
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT							\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=? 									\n");
		sql.append("\n");
	    
		return sql.toString();
    }

	/** ***********************************************************************
     * Query를 생성하여 리턴한다. - 이벤트라운지 VIP부킹이벤트
     ************************************************************************ */
     private String getPreBkEvntBenefitQuery2(){
         StringBuffer sql = new StringBuffer();

         sql.append("	\n");
 		sql.append("\t	SELECT COUNT(*) as CNT_PMI_GEN_MO_NUM	\n");
 		sql.append("\t	FROM BCDBA.TBGAPLCMGMT T1			\n");
 		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
 		sql.append("\t	AND T1.GOLF_SVC_APLC_CLSS='9001'  \n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYYMMDD')>=TO_CHAR(SYSDATE,'YYYYMM') ||'01'	\n");
		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYYMMDD')<=TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD')	\n");
 		sql.append("\t	AND (T1.PGRS_YN='B' OR T1.PGRS_YN='E')		\n");
 		sql.append("\t	AND NVL(NUM_DDUC_YN, 'Y')='Y'	\n");
 		sql.append("\n");
 	    
 		return sql.toString();
     }
     
	/** ***********************************************************************
     * Query를 생성하여 리턴한다. - 파3
     ************************************************************************ */
     private String getParBenefitQuery(String gubun){
         StringBuffer sql = new StringBuffer();
 		
        sql.append("	\n");
 		sql.append("\t	SELECT PAR_3_BOKG_LIMT_YN, PAR_3_BOKG_YR_ABLE_NUM, PAR_3_BOKG_MO_ABLE_NUM	\n");
 		sql.append("\t	, (SELECT YR_BOKG_ABLE_NUM FROM BCDBA.TBGCDHDGRDBNFT WHERE CDHD_SQ2_CTGO=? AND BOKG_KND='0001' AND AFFI_GREEN_SEQ_NO=?) PAR_3_BOKG_YR_ABLE_NUM_GREEN	\n");
 		sql.append("\t	, (SELECT MO_BOKG_ABLE_NUM FROM BCDBA.TBGCDHDGRDBNFT WHERE CDHD_SQ2_CTGO=? AND BOKG_KND='0001' AND AFFI_GREEN_SEQ_NO=?) PAR_3_BOKG_MO_ABLE_NUM_GREEN	\n");
 		
// 연간 부킹 횟수
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
 		if(gubun.equals("mem")){ 
 			// 기존에 값이 없는것들은 회원으로 구분한다.
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		sql.append("\t	) AS PAR_3_BOKG_YR_DONE	\n");
 		
// 월간 부킹 횟수
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
 		if(gubun.equals("mem")){ 
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		sql.append("\t	) AS PAR_3_BOKG_MO_DONE	\n");
 		

// 골프장별 - 연간 부킹 횟수
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL AND T1.AFFI_GREEN_SEQ_NO=?	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
 		if(gubun.equals("mem")){ 
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		sql.append("\t	) AS PAR_3_BOKG_YR_DONE_GREEN	\n");
 		
 		
// 골프장별 - 월간 부킹 횟수
 		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
 		sql.append("\t	WHERE T1.CDHD_ID=? AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')	\n");
 		sql.append("\t	AND T1.RSVT_YN='Y' AND T2.GOLF_SVC_RSVT_NO IS NULL AND T1.AFFI_GREEN_SEQ_NO=?	\n");
 		sql.append("\t	AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')>=? AND TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYYMMDD')<=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
 		if(gubun.equals("mem")){ 
 			sql.append("\t	AND (T1.RSVT_CDHD_GRD_SEQ_NO IS NULL OR T1.RSVT_CDHD_GRD_SEQ_NO=?)	\n");
 		}else{
 			sql.append("\t	AND T1.RSVT_CDHD_GRD_SEQ_NO=?	\n");
 		}
 		sql.append("\t	) AS PAR_3_BOKG_MO_DONE_GREEN	\n");
 		
 		
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHDBNFTMGMT	\n");
 		sql.append("\t	WHERE CDHD_SQ2_CTGO=?	\n");
 	    
 		return sql.toString();
     }
              
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 스카이 72
    ************************************************************************ */
    private String getSkyBenefitQuery(String gubun){
        StringBuffer sql = new StringBuffer();

        sql.append("	\n");
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

         sql.append("	\n");
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

        sql.append("	\n");
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
      
  	/** ***********************************************************************
       * Query를 생성하여 리턴한다. - 혜택기간 출력
       ************************************************************************ */
       private String getBlockDateQuery(){
           StringBuffer sql = new StringBuffer();
           
   			//2009.10.08 수정  기준 : 부킹혜택기간을 월단위로 한다.
           sql.append("	\n");
   			sql.append("\t	SELECT CDHD_ID, JONN_ATON	\n");
   			sql.append("\t      ,     ACRG_CDHD_JONN_DATE ST_DATE 	\n"); 
   			sql.append("\t      ,     ACRG_CDHD_END_DATE   ED_DATE 	\n");
   			sql.append("\t      ,    TO_CHAR(SYSDATE,'YYYYMM') ||'01' ST_DATE_MONTH 	\n");
   			sql.append("\t      ,    TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH 	\n");
   			sql.append("\t      FROM BCDBA.TBGGOLFCDHD		\n");
   			sql.append("\t      WHERE CDHD_ID=? and BOKG_LIMT_YN='Y' and TO_CHAR(TO_DATE(BOKG_LIMT_FIXN_STRT_DATE, 'YYYYMMDD'), 'YYYYMMDD')<= TO_CHAR(SYSDATE, 'YYYYMMDD')		\n");
   			sql.append("\t      and TO_CHAR(TO_DATE(BOKG_LIMT_FIXN_END_DATE, 'YYYYMMDD'), 'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')		\n");
   		
   		return sql.toString();
       }
}
