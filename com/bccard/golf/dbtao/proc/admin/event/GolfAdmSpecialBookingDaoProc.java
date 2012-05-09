/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmSpecialBookingDaoProc
*   작성자    : 이포넷 은장선
*   내용      : 관리자 > 이벤트->VIP부킹이벤트->명문골프장부킹 
*   적용범위  : golf
*   작성일자  : 2009-09-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	이포넷 은장선
 * @version	1.0
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* golfloung		20100305	임은혜	검색수정
 ******************************************************************************/
public class GolfAdmSpecialBookingDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmSpecialBookingDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmSpecialBookingDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			// 회원통합테이블 관련 수정사항 진행
			//조회 ----------------------------------------------------------
			

			String sql = this.getDetailSQL();   
			  
			String aplc_seq_no	= data.getString("aplc_seq_no");      //예약번호
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, aplc_seq_no);		
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy/MM/dd"));	
					result.addString("GOLF_CMMN_CODE",rs.getString("GOLF_CMMN_CODE"));
					result.addString("CO_NM",rs.getString("CO_NM"));
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("DDD_NO",rs.getString("DDD_NO"));
					result.addString("TEL_HNO",rs.getString("TEL_HNO"));
					result.addString("TEL_SNO",rs.getString("TEL_SNO"));
					result.addString("EMAIL",rs.getString("EMAIL"));
					result.addString("RIDG_PERS_NUM",rs.getString("RIDG_PERS_NUM"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy/MM/dd"));
					result.addString("DPRT_PL_INFO",rs.getString("DPRT_PL_INFO"));	
					result.addString("PU_TIME",rs.getString("PU_TIME"));
					result.addString("ESTM_ITM_CLSS",rs.getString("ESTM_ITM_CLSS"));
					result.addString("APPR_OPION",rs.getString("APPR_OPION"));
					
					String teof_time = rs.getString("TEOF_TIME");
					teof_time = teof_time.substring(0,2) + "시대";
					result.addString("TEOF_TIME",teof_time);
					
					String pu_date = rs.getString("PU_DATE");
					if(pu_date != null){
						pu_date = DateUtil.format(pu_date,"yyyyMMdd","yyyy-MM-dd");
					}
					result.addString("PU_DATE",pu_date);	
					
					String memGrade = this.getMemGradeNm(context, data, rs.getString("CDHD_ID"));
					
					if(!"".equals(memGrade) && memGrade != null){
						result.addString("GRADE", memGrade);
					}else{
						result.addString("GRADE","");
					}
				
				}
			}

			if(result.size() < 1) {
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

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			

			String sql = this.getSelectQuery(data);   

			long page_no			= data.getLong("PAGE_NO");               //페이지번호
			long record_size		= data.getLong("RECORD_SIZE");           //페이지당 출력될 갯수

			String sch_yn			= data.getString("sch_yn");            	 //검색여부
			String green_nm			= data.getString("green_nm");            //예약골프장명
			String golf_cmmn_code	= data.getString("golf_cmmn_code");      //예약코드
			String grade			= data.getString("grade");               //회원등급 한글
			String sch_reg_aton_st	= data.getString("sch_reg_aton_st");     //조회 신청 시작일 
			String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 	 //조회 신청 종료일 
			String sch_type			= data.getString("sch_type");            //이름,ID조회 여부     
			String search_word		= data.getString("search_word");         //조회 명     
			String sch_chng_aton_st	= data.getString("sch_chng_aton_st");    //조회 취소일자 시작일              
			String sch_chng_aton_ed	= data.getString("sch_chng_aton_ed");    //조회 취소일자 종료일   
			String doyn             = data.getString("doyn");				 //처리여부      
			String actnKey          = data.getString("actnKey");				 //액션키
			String cmmn_code_nm		= "";
//			debug("green_nm>>>>>>>22222>>>>>>>>>>>>>>>>>>>>>>>" + green_nm);
//			debug("sch_yn>>>>>>>>>>2222>>>>>>>>>>>>>>>>>>>>" + sch_yn); 
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);

			if(sch_yn.equals("Y")){
				
				if(!green_nm.equals("")){
					pstmt.setString(++idx,green_nm);
				}
	
				if(!golf_cmmn_code.equals("")){
					pstmt.setString(++idx,golf_cmmn_code);
				}
	
				if(!grade.equals("")){
					pstmt.setString(++idx,grade);
				}
				
				if(!sch_reg_aton_st.equals("") && !sch_reg_aton_ed.equals("")){
						pstmt.setString(++idx,sch_reg_aton_st.replaceAll("-",""));
						pstmt.setString(++idx,sch_reg_aton_ed.replaceAll("-",""));
				}

				if(!search_word.equals("")){
					if(sch_type.equals("ID")){
						pstmt.setString(++idx,search_word);
					}else if(sch_type.equals("NAME")){
						pstmt.setString(++idx,search_word);
					}else{
						pstmt.setString(++idx,search_word);
						pstmt.setString(++idx,search_word);
					}
				}

			}
			
			if(!sch_chng_aton_st.equals("") && !sch_chng_aton_ed.equals("")){
				pstmt.setString(++idx,sch_chng_aton_st.replaceAll("-",""));
				pstmt.setString(++idx,sch_chng_aton_ed.replaceAll("-",""));
			}
			
			if(!doyn.equals("")){
				pstmt.setString(++idx,doyn);
			}

			if(actnKey.equals("admSpecialBookingCanList") || actnKey.equals("admSpecialBookingList")){
				pstmt.setLong(++idx, page_no);
			}			
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("GOLF_CMMN_CODE",rs.getString("GOLF_CMMN_CODE"));
					//result.addString("GOLF_CMMN_CODE_NM",rs.getString("GOLF_CMMN_CODE_NM"));
					
					cmmn_code_nm = rs.getString("GOLF_CMMN_CODE_NM");
//					debug(">>>>>>>>>>>>> cmmn_code_nm :" + cmmn_code_nm);
					if("예약신청".equals(cmmn_code_nm) || "대기중".equals(cmmn_code_nm) || "부킹대기".equals(cmmn_code_nm)) {
						cmmn_code_nm = "<font color='#9ACD32'>" + cmmn_code_nm + "</font>";		// 연두색
					} else if("대기취소".equals(cmmn_code_nm) || "부킹취소".equals(cmmn_code_nm)) {
						cmmn_code_nm = "<font color='red'>" + cmmn_code_nm + "</font>";		// 빨강색
					} else if("부킹확정".equals(cmmn_code_nm)) {
						cmmn_code_nm = "<font color='blue'>" + cmmn_code_nm + "</font>";		// 파랑색
					}
//					debug(">>>>>>>>>>>>> cmmn_code_nm2 :" + cmmn_code_nm);
					
					result.addString("GOLF_CMMN_CODE_NM",cmmn_code_nm);
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));					
					result.addString("DPRT_PL_INFO",rs.getString("DPRT_PL_INFO"));					
					result.addString("PU_TIME",rs.getString("PU_TIME"));
					result.addString("CO_NM",rs.getString("CO_NM"));
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));
					result.addString("GRADE",rs.getString("GRADE"));
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy-MM-dd"));
					result.addString("RNUM",rs.getString("RNUM"));
					result.addInt("PAGE",rs.getInt("PAGE"));
					result.addInt("TOT_CNT",rs.getInt("TOT_CNT"));
					result.addString("LIST_NO",rs.getString("LIST_NO"));
					result.addString("RIDG_PERS_NUM",rs.getString("RIDG_PERS_NUM"));
					result.addString("CSLT_YN",rs.getString("CSLT_YN"));					

					String teof_time = rs.getString("TEOF_TIME");
					teof_time = teof_time.substring(0,2) + "시대";
					result.addString("TEOF_TIME",teof_time);	
					
					String pu_date = rs.getString("PU_DATE");
					if(pu_date != null){
						pu_date = DateUtil.format(pu_date,"yyyyMMdd","yyyy-MM-dd");
					}
					result.addString("PU_DATE",pu_date);	

					String chng_aton = rs.getString("CHNG_ATON");
					if(chng_aton != null){
						chng_aton = DateUtil.format(chng_aton,"yyyyMMdd","yyyy-MM-dd");
					}
					result.addString("CHNG_ATON",chng_aton);	
										
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
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


	
	/* Proc 실행. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult setFinalCncl(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			

			String sql = this.getFinalCnclSQL();   			
			
			String aplc_seq_no	= data.getString("aplc_seq_no");      //예약번호         			
			String cslt_yn      = data.getString("cslt_yn");
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(++idx, cslt_yn);	
			pstmt.setString(++idx, aplc_seq_no);			
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	


	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult setCancel(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			

			String sql = this.getCancelSQL(data);   
			
			String cncl_param	= data.getString("cncl_param");       //변경 예약 코드명
			String aplc_seq_no	= data.getString("aplc_seq_no");      //예약번호         
			String cdhd_id		= data.getString("cdhd_id");          //ID
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, cncl_param);
			pstmt.setString(++idx, aplc_seq_no);
			pstmt.setString(++idx, cdhd_id);			
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult setUpdateEvnt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			

			String sql = this.getUpdateEvntSQL(data);   
			
			String cdhd_id			= data.getString("cdhd_id");              //ID
			String golf_cmmn_codes	= data.getString("golf_cmmn_codes");      //예약상태코드
			String aplc_seq_no		= data.getString("aplc_seq_no");   	      //예약번호
			String green_nms	= data.getString("green_nms");                //신청골프장명
			String dprt_pl_info	= data.getString("dprt_pl_info");             //예약골프장명
			String pu_date		= data.getString("pu_date");                  //부킹일자
			String pu_time		= data.getString("pu_time");                  //부킹시간
			String hp_ddd_no	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String co_nm		= data.getString("co_nm");
			String teof_date	= data.getString("teof_date");
			String teof_time		= data.getString("teof_time");
			String[] pudarry = pu_date.split("-");
			debug(">>>>>>>>>>>>>>>>>>pu_date :" + pu_date);
			pu_date = pu_date.replaceAll("-","");
			debug(">>>>>>>>>>>>>>>>>>pu_date2 :" + pu_date);
			debug(">>>>>>>>>>>>>>>>>>pu_time :" + pu_time);
			debug(">>>>>>>>>>>>>>>>>>pu_date.length() :" + pu_date.length());
			//debug(">>>>>>>>>>>>>>>>>>pudarry[0] :" + pudarry[0]);
			//debug(">>>>>>>>>>>>>>>>>>pudarry[1] :" + pudarry[1]);
			//debug(">>>>>>>>>>>>>>>>>>pudarry[2] :" + pudarry[2]);
			//debug(">>>>>>>>>>>>>>>>>>pu_date.substring(6,2) :" + pu_date.substring(6,2));


			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, green_nms);
			pstmt.setString(++idx, dprt_pl_info);
			pstmt.setString(++idx, golf_cmmn_codes);
			pstmt.setString(++idx, pu_date);
			pstmt.setString(++idx, pu_time);
			pstmt.setString(++idx, aplc_seq_no);			
			pstmt.setString(++idx, cdhd_id);
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
				
				if(("W".equals(golf_cmmn_codes) || "B".equals(golf_cmmn_codes)) && !"".equals(co_nm) && !"".equals(hp_ddd_no)) {
					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", co_nm);
					smsMap.put("sPhone1", hp_ddd_no);
					smsMap.put("sPhone2", hp_tel_hno);
					smsMap.put("sPhone3", hp_tel_sno);
					
					//debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					//- 예약 신청시 : [Golf Loun.G]000님,00월00일00시대 경기남부권 골프장 신청 하였습니다
					//- 부킹대기시 : [Golf Loun.G]000님,00월00일00:00에 신라로 부킹예정이니 확정하여 주십시요
					//- 부킹확정시 : [Golf Loun.G]000님,00월00일00:00에 신라로 부킹확정되었습니다
	
					String smsClss = "641";
					String message = "";
					
					//pu_date = pu_date.substring(8,2) + "시대";
					
					//if("R".equals(golf_cmmn_codes)) {		// 예약 신청시
						//message = "[Golf Loun.G]"+co_nm+"님," + teof_date.substring(4,2) + "월"+teof_date.substring(6,2)+"일"+teof_time+" " + green_nms + " 골프장 신청 하였습니다";
					//} else if("W".equals(golf_cmmn_codes)) {	// 부킹대기시
					if("W".equals(golf_cmmn_codes)) {	// 부킹대기시
						message = "[Golf Loun.G]"+co_nm+"님," + pudarry[1] + "월"+pudarry[2]+"일"+pu_time+"시에 "+dprt_pl_info+"으로 부킹예정이니 확정하여 주십시요";						
					} else if("B".equals(golf_cmmn_codes)) {	// 부킹확정시
						message = "[Golf Loun.G]"+co_nm+"님," + pudarry[1] + "월"+pudarry[2]+"일"+pu_time+"시에 "+dprt_pl_info+"으로 부킹확정 되었습니다";
					}
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}
				
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}


	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult setUpdateUsr(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			

			String sql = this.getUpdateUsrSQL();   
			
			String estm_itm_clss	= data.getString("estm_itm_clss");       //회원평가 등급
			String cdhd_id	= data.getString("cdhd_id");                     //아이디
			String appr_opion		= data.getString("appr_opion");          //회원평가 글
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, estm_itm_clss);
			pstmt.setString(++idx, appr_opion);
			pstmt.setString(++idx, cdhd_id);			
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	public String getMemGradeNm(WaContext context, TaoDataSet data, String cdhd_id) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String mem_Grade = "";
		String apo_yn = "N";

		try {
			
			conn = context.getDbConnection("default", null);
			String sql = this.getGradeSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id);
			
			rs = pstmt.executeQuery();
			boolean eof = false;
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				if(!"".equals(mem_Grade)){
					mem_Grade = mem_Grade + " / "+rs.getString("GOLF_CMMN_CODE_NM");
				}else{
					mem_Grade = rs.getString("GOLF_CMMN_CODE_NM");
				}
				
				
				eof = true;
			}
			
						
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return mem_Grade;
		
	}
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getSelectQuery(TaoDataSet data) throws BaseException{
        StringBuffer sql = new StringBuffer();

		String sch_yn			= data.getString("sch_yn");  
		String sch_date			= data.getString("sch_date");  
		String green_nm			= data.getString("green_nm");               
		String golf_cmmn_code	= data.getString("golf_cmmn_code");   
		String grade			= data.getString("grade");                     
		String sch_reg_aton_st	= data.getString("sch_reg_aton_st"); 
		String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 
		String sch_type			= data.getString("sch_type");               
		String search_word		= data.getString("search_word");   
		String actnKey          = data.getString("actnKey");
		String sch_chng_aton_st	= data.getString("sch_chng_aton_st");               
		String sch_chng_aton_ed	= data.getString("sch_chng_aton_ed");   
		String doyn             = data.getString("doyn");	 
		
		
		sql.append("\n     SELECT E.*                                                                                                 ");
		sql.append("\n       FROM (SELECT D.*,ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE,MAX(RN) OVER() TOT_CNT,                           ");
		sql.append("\n                    (MAX(RN) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO                              ");
		sql.append("\n               FROM (SELECT ROWNUM RN,A.APLC_SEQ_NO,B.GOLF_CMMN_CODE,B.GOLF_CMMN_CODE_NM,A.PU_TIME,           ");
		sql.append("\n                            A.GREEN_NM,A.TEOF_TIME,A.DPRT_PL_INFO,A.PU_DATE,A.CO_NM,A.CDHD_ID,                ");
		//sql.append("\n                            DECODE(C.CDHD_CTGO_SEQ_NO,'16','골프투어멤버스','13','NH플래티늄','11','블랙','10','나의알파플래티늄','8','화이트','7','골드','6','블루','5','챔피온') GRADE, ");
		sql.append("\n                            CODE.GOLF_CMMN_CODE_NM AS GRADE,	");
		sql.append("\n                            A.HP_DDD_NO,A.HP_TEL_HNO,A.HP_TEL_SNO,A.RIDG_PERS_NUM,A.TEOF_DATE,                ");
		sql.append("\n                            SUBSTR(A.MEMO_EXPL,1,20) MEMO_EXPL,SUBSTR(A.REG_ATON,1,8) REG_ATON,               ");
		sql.append("\n                            A.CHNG_ATON,DECODE(A.CSLT_YN,NULL,'미처리','N','미처리','Y','처리') CSLT_YN        ");		
		//sql.append("\n                       	FROM BCDBA.TBGAPLCMGMT A,  BCDBA.TBGGOLFCDHDGRDMGMT C,                                 ");
		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT A,								                                 ");
/*
		sql.append("\n                            ( select CDHD_ID, MAX(CDHD_CTGO_SEQ_NO) as CDHD_CTGO_SEQ_NO     	                ");
		sql.append("\n                            	from BCDBA.TBGGOLFCDHDGRDMGMT                                   			    ");
		sql.append("\n                            	group by CDHD_ID ) C,                                       					");
		*/
		sql.append("\n                            (SELECT  GOLF_CMMN_CODE_NM,GOLF_CMMN_CODE                                         ");
		sql.append("\n                               FROM BCDBA.TBGCMMNCODE                                                         ");
		sql.append("\n                              WHERE GOLF_CMMN_CLSS='0053' AND USE_YN ='Y'                                     ");
		sql.append("\n                              ORDER BY SORT_SEQ) B,                                                            ");
		sql.append("\n                              BCDBA.TBGGOLFCDHD CDHD, BCDBA.TBGGOLFCDHDCTGOMGMT CTGO, BCDBA.TBGCMMNCODE CODE	");
		sql.append("\n                      WHERE A.GOLF_SVC_APLC_CLSS = '9001'                                                     ");
		sql.append("\n                        AND A.PGRS_YN = B.GOLF_CMMN_CODE                                                      ");
		//sql.append("\n                        AND C.CDHD_ID = A.CDHD_ID                                                             ");
		sql.append("\n                        AND A.CDHD_ID = CDHD.CDHD_ID	");
		sql.append("\n                        AND CDHD.CDHD_CTGO_SEQ_NO = CTGO.CDHD_CTGO_SEQ_NO	");
		sql.append("\n                        AND CODE.GOLF_CMMN_CODE = CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	");
		sql.append("\n                                                                                         ");
		sql.append("\n                                                                                         ");
		if(actnKey.equals("admSpecialBookingCanList") || actnKey.equals("admEvntMMoonCanExcel")){
			sql.append("\n                        AND A.PGRS_YN IN ('E','L','C')                                                        ");
		}
		
		if(sch_yn.equals("Y")){
			if(!green_nm.equals("")){
				sql.append("\n                        AND A.GREEN_NM = ?                                                                ");
			}
	
			if(!golf_cmmn_code.equals("")){
				sql.append("\n                        AND B.GOLF_CMMN_CODE = ?                                                          ");
			}
	
			if(!grade.equals("")){
				sql.append("\n                        AND C.CDHD_CTGO_SEQ_NO = ?                                                        ");
			}
			
			if(!sch_reg_aton_st.equals("") && !sch_reg_aton_ed.equals("")){
				if(sch_date.equals("join_date")){
					sql.append("\n                        AND A.REG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// 신청일자 
				}else if(sch_date.equals("del_date")){
					sql.append("\n                        AND A.CHNG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// 취소일자
				}else{
					sql.append("\n                        AND A.PU_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// 부킹일자
				}
			}
			
			if(!search_word.equals("")){
				if(sch_type.equals("ID")){
					sql.append("\n                        AND A.CDHD_ID LIKE '%'||?||'%'                                                    ");
				}else if(sch_type.equals("NAME")){
					sql.append("\n                        AND A.CO_NM LIKE '%'||?||'%'                                                      ");
				}else{
					sql.append("\n                        AND (A.CDHD_ID LIKE '%'||?||'%' OR A.CO_NM LIKE '%'||?||'%')                      ");
				}
			}
		}
		
		if(!sch_chng_aton_st.equals("") && !sch_chng_aton_ed.equals("")){
			sql.append("\n                        AND A.CHNG_ATON BETWEEN ? AND ?                                                     ");
		}
		
		if(!doyn.equals("")){
			sql.append("\n                        AND NVL(A.CSLT_YN,'N') = ?                                                        ");
		}		

		sql.append("\n                ORDER BY A.APLC_SEQ_NO DESC          ) D                                                     ");
		sql.append("\n                                                                                                            ");
		sql.append("\n             ) E                                                                                             ");
		if(actnKey.equals("admSpecialBookingCanList") || actnKey.equals("admSpecialBookingList")){
			sql.append("\n      WHERE PAGE = ?                                                                                          ");
		}

		return sql.toString();
    } 

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getDetailSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();

		sql.append("\n     SELECT SUBSTR(A.REG_ATON,1,8) REG_ATON,B.GOLF_CMMN_CODE,A.CO_NM,A.CDHD_ID,A.PU_TIME,A.PU_DATE,	 ");
		//sql.append("\n            NVL(DECODE(C.CDHD_CTGO_SEQ_NO,'8','화이트','7','골드','6','블루','5','챔피온'), C.CDHD_CTGO_SEQ_NO) GRADE,			 ");
		
		sql.append("\n            A.HP_DDD_NO,A.HP_TEL_HNO,A.HP_TEL_SNO,A.DDD_NO,A.TEL_HNO,A.TEL_SNO,A.EMAIL,			     ");
		sql.append("\n            A.RIDG_PERS_NUM,A.MEMO_EXPL,A.GREEN_NM,A.TEOF_DATE,A.TEOF_TIME,A.DPRT_PL_INFO,             ");
		sql.append("\n            NVL(D.ESTM_ITM_CLSS,'B') ESTM_ITM_CLSS,D.APPR_OPION                                        ");		
		sql.append("\n       FROM BCDBA.TBGAPLCMGMT A,                                          						  	 ");
		//sql.append("\n				BCDBA.TBGGOLFCDHDGRDMGMT C,																 ");
		sql.append("\n            (SELECT  GOLF_CMMN_CODE_NM,GOLF_CMMN_CODE                                                  ");
		sql.append("\n               FROM BCDBA.TBGCMMNCODE                                                                  ");
		sql.append("\n              WHERE GOLF_CMMN_CLSS='0053' AND USE_YN ='Y'                                              ");
		sql.append("\n              ORDER BY SORT_SEQ) B,BCDBA.TBGGOLFCDHD D                                                 ");
		sql.append("\n      WHERE A.GOLF_SVC_APLC_CLSS = '9001'                                                              ");
		sql.append("\n        AND A.APLC_SEQ_NO = ?                                                                          ");
		sql.append("\n        AND A.PGRS_YN = B.GOLF_CMMN_CODE   AND A.CDHD_ID = D.CDHD_ID          ");
		//sql.append("\n        AND A.CDHD_ID = C.CDHD_ID         ");
		
        
 
		return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getCancelSQL(TaoDataSet data) throws BaseException{
		StringBuffer sql = new StringBuffer();

		String cd = data.getString("cncl_param");
		
		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT                    ");
		sql.append("\n        SET PGRS_YN = ?                          ");
		if(cd.equals("C") || cd.equals("E") || cd.equals("L")){
			sql.append("\n   ,CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDD')  ");
		}else if(cd.equals("B")){
			sql.append("\n   ,NUM_DDUC_YN = 'Y'                        ");
		}
		sql.append("\n      WHERE APLC_SEQ_NO = ?                      ");
		sql.append("\n        AND GOLF_SVC_APLC_CLSS = '9001'          ");
		sql.append("\n        AND CDHD_ID = ?                          ");

		return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getUpdateUsrSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n     UPDATE BCDBA.TBGGOLFCDHD            ");
		sql.append("\n        SET ESTM_ITM_CLSS = ?,           ");
		sql.append("\n            APPR_OPION = ?               ");
		sql.append("\n      WHERE CDHD_ID = ?                  ");

		return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getUpdateEvntSQL(TaoDataSet data) throws BaseException{
		StringBuffer sql = new StringBuffer();

		String golf_cmmn_codes = data.getString("golf_cmmn_codes");
		
		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT               ");
		sql.append("\n        SET GREEN_NM = ?,                   ");
		sql.append("\n            DPRT_PL_INFO = ?,               ");
		sql.append("\n            PGRS_YN = ?,                    ");
		sql.append("\n            PU_DATE = ?,                    ");
		if(golf_cmmn_codes.equals("B")){
			sql.append("\n            NUM_DDUC_YN = 'Y',                    ");
		}
		sql.append("\n            PU_TIME = ?                     ");
		sql.append("\n      WHERE APLC_SEQ_NO = ?                 ");
		sql.append("\n        AND GOLF_SVC_APLC_CLSS = '9001'     ");
		sql.append("\n        AND CDHD_ID = ?                     ");

		return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getFinalCnclSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT               ");
		sql.append("\n        SET CSLT_YN = ?                     ");		
		sql.append("\n      WHERE APLC_SEQ_NO = ?                 ");
		sql.append("\n        AND GOLF_SVC_APLC_CLSS = '9001'     ");
		
		return sql.toString();
	}

    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getGradeSelectQuery(){
         StringBuffer sql = new StringBuffer();
         
 		sql.append("	SELECT 	T3.GOLF_CMMN_CODE_NM   													\n");
 		sql.append("	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1 					  							\n");
 		sql.append("	 JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO = T2.CDHD_CTGO_SEQ_NO \n");
 		sql.append("	 JOIN BCDBA.TBGCMMNCODE T3 ON T3.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO  			\n");
 		sql.append("	 JOIN BCDBA.TBGGOLFCDHDBNFTMGMT T4 ON T4.CDHD_SQ2_CTGO = T2.CDHD_SQ2_CTGO  		\n");
 		sql.append("	 WHERE T1.CDHD_ID = ?  AND T3.GOLF_CMMN_CLSS='0005'  							\n");
 		
 		
     return sql.toString();
     }

	
}
