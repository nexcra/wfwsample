/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkCheckPreListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 프리미엄 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.check;

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
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfBkCheckAllViewDaoProc extends AbstractProc {
	
	public GolfBkCheckAllViewDaoProc() {}	

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
		String sql = "";
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			int intMemberGrade = 0;		// 멤버십 등급
			int intCardGrade = 0;		// 카드 등급 
			String memb_id = "";		// 회원 아이디
			String memb_nm = "";		// 회원 이름
			 
			// 00. 세션 정보 체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				intMemberGrade = userEtt.getIntMemberGrade();	// 멤버십 등급
				intCardGrade = userEtt.getIntCardGrade();		// 카드 등급 
				memb_id = userEtt.getAccount();				// 회원 아이디
				memb_nm = userEtt.getName();					// 회원 이름
			}

			
			String cdhd_grd_seq_no = "";		// 회원등급일련번호
			String cdhd_ctgo_seq_no = "";		// 회원분류일련번호
			int cdhd_sq2_ctgo = 0;				// 회원2차분류코드
			String mem_state = "N";				// 부킹 가능 여부
			
			String st_date = "";				// 부킹 혜택 시작 일시
			String ed_date = "";				// 부킹 혜택 종료 일시
						
			String evt_YN = "";
			String pre_YN = "";
			String sky_YN = "";
			String par_YN= "";
			String gen_YN= "";
			String evt_TXT = "";
			String pre_TXT = "";
			String sky_TXT = "";
			String par_TXT= "";
			String gen_TXT= "";

			int evt_ABLE = 0;
			int event_USE = 0;
			int pre_USE = 0;
			int sky_ABLE= 0;
			int sky_USE= 0;
			int par_ABLE= 0;
			int par_USE= 0;
			int wkd_ABLE= 0;
			int wke_ABLE= 0;
			int wkd_USE= 0;
			int wkd_DEL= 0;
			int wke_USE= 0;
			int wke_DEL= 0;
			int tot_BK = 0;
			int pmi_wkd_bokg_num = 0;
			int pmi_wke_bokg_num = 0;

			// 멤버십 등급 내역 가져오기
			if(intMemberGrade>0)
			{
				sql = this.getMemGradeQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, memb_id);
				rs = pstmt.executeQuery();
				if(rs != null) {
					while(rs.next())  {
						cdhd_grd_seq_no = rs.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs.getInt("CDHD_SQ2_CTGO");
						debug("MEM : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("MEM : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("MEM : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo, "mem");
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, memb_id);
						rs = pstmt.executeQuery();		
						if(rs != null) {
							while(rs.next())  {
								st_date = rs.getString("ST_DATE");
								ed_date = rs.getString("ED_DATE");
								mem_state = rs.getString("MEM_STATE");
							}
						}
						debug("MEM : st_date : " + st_date + " / ed_date : " + ed_date);	
						

						sql = this.getSelectQuery("mem");
						pstmt = conn.prepareStatement(sql);

						idx = 0;
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						
						rs = pstmt.executeQuery();						
									
						if(rs != null) {
							while(rs.next()) {
								evt_YN = rs.getString("EVT_YN");
								pre_YN = rs.getString("PRE_YN");
								sky_YN = rs.getString("SKY_YN");
								par_YN= rs.getString("PAR_YN");
								gen_YN= rs.getString("GEN_YN");

								evt_ABLE = rs.getInt("EVT_ABLE");
								event_USE = rs.getInt("EVENT_USE");
								pre_USE = rs.getInt("PRE_USE");
								pmi_wkd_bokg_num = rs.getInt("PMI_WKD_BOKG_NUM");
								pmi_wke_bokg_num = rs.getInt("PMI_WKE_BOKG_NUM");
								sky_ABLE= rs.getInt("SKY_ABLE");
								sky_USE= rs.getInt("SKY_USE");
								par_ABLE= rs.getInt("PAR_ABLE");
								par_USE= rs.getInt("PAR_USE");
								wkd_ABLE= rs.getInt("WKD_ABLE");
								wke_ABLE= rs.getInt("WKE_ABLE");
								wkd_USE= rs.getInt("WKD_USE");
								wkd_DEL= rs.getInt("WKD_DEL");
								wke_USE= rs.getInt("WKE_USE");
								wke_DEL= rs.getInt("WKE_DEL");
								tot_BK = pre_USE+sky_USE+par_USE+(wkd_USE-wkd_DEL)+(wke_USE-wke_DEL);

								if("N".equals(mem_state)){	
									gen_TXT = "유료회원 기간 종료";
									pre_TXT = "유료회원 기간 종료";
									evt_TXT = "유료회원 기간 종료";
									sky_TXT = "유료회원 기간 종료";
									par_TXT = "유료회원 기간 종료";
								}else{
									if(gen_YN.equals("A")){
										gen_TXT = (wkd_USE-wkd_DEL)+(wke_USE-wke_DEL) + " / " + "무제한";
									}else{
										gen_TXT = (wkd_USE-wkd_DEL)+(wke_USE-wke_DEL) + " / " + (wkd_ABLE+wke_ABLE);
									}
									
									if(pre_YN.equals("Y")){
										//pre_TXT = pre_USE + " / " + (pmi_wkd_bokg_num+pmi_wke_bokg_num);
										pre_TXT = pre_USE + " / 무제한";
									}else if(pre_YN.equals("N")){
										pre_TXT = "접근불가";
									}
									
									if(evt_YN.equals("Y")){
										evt_TXT = event_USE + " / " + evt_ABLE;
									}else if(evt_YN.equals("N")){
										evt_TXT = "접근불가";
									}
									
									if(sky_YN.equals("Y")){
										sky_TXT = sky_USE + " / " + sky_ABLE;
									}else if(sky_YN.equals("N")){
										sky_TXT = "혜택없음";
									}
									
									if(par_YN.equals("Y")){
										par_TXT = par_USE + " / " + par_ABLE;
									}else if(par_YN.equals("N")){
										par_TXT = "혜택없음";
									} 
								}
							}
						}
					}
				}
				
			}
			// 카드 등급 내역 가져오기(더해준다)
			if(intCardGrade>0)
			{
				sql = this.getCardGradeQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, memb_id);
				rs = pstmt.executeQuery();
				if(rs != null) {
					while(rs.next())  {
						cdhd_grd_seq_no = rs.getString("CDHD_GRD_SEQ_NO");
						cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
						cdhd_sq2_ctgo = rs.getInt("CDHD_SQ2_CTGO");
						debug("CARD : cdhd_grd_seq_no : 회원등급 일련번호 : " + cdhd_grd_seq_no);	
						debug("CARD : cdhd_ctgo_seq_no : 회원분류 일련번호 : " + cdhd_ctgo_seq_no);
						debug("CARD : cdhd_sq2_ctgo : 회원2차분류코드(등급) : " + cdhd_sq2_ctgo);

						sql = this.getDateQuery(cdhd_sq2_ctgo, "card");
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, memb_id);
						pstmt.setString(2, cdhd_ctgo_seq_no);	// 카드회원일경우만 추가한다.
						rs = pstmt.executeQuery();		
						if(rs != null) {
							while(rs.next())  {
								st_date = rs.getString("ST_DATE");
								ed_date = rs.getString("ED_DATE");
							}
						}
						debug("CARD : st_date : " + st_date + " / ed_date : " + ed_date);	
						

						sql = this.getSelectQuery("mem");
						pstmt = conn.prepareStatement(sql);

						idx = 0;
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, memb_id);
						pstmt.setInt(++idx, cdhd_sq2_ctgo);
						pstmt.setString(++idx, st_date);
						pstmt.setString(++idx, ed_date);
						pstmt.setString(++idx, GolfUtil.lpad(cdhd_sq2_ctgo+"", 4, "0"));
						
						rs = pstmt.executeQuery();						
									
						if(rs != null) {
							while(rs.next()) {
								if(!evt_YN.equals("Y")) evt_YN = rs.getString("EVT_YN");
								if(!pre_YN.equals("Y")) pre_YN = rs.getString("PRE_YN");
								if(!sky_YN.equals("Y")) sky_YN = rs.getString("SKY_YN");
								if(!par_YN.equals("Y")) par_YN= rs.getString("PAR_YN");
								if(!gen_YN.equals("Y")) gen_YN= rs.getString("GEN_YN");

								evt_ABLE = evt_ABLE + rs.getInt("EVT_ABLE");
								event_USE = event_USE + rs.getInt("EVENT_USE");
								pre_USE = pre_USE + rs.getInt("PRE_USE");
								pmi_wkd_bokg_num = pmi_wkd_bokg_num + rs.getInt("PMI_WKD_BOKG_NUM");
								pmi_wke_bokg_num = pmi_wke_bokg_num + rs.getInt("PMI_WKE_BOKG_NUM");
								sky_ABLE= sky_ABLE + rs.getInt("SKY_ABLE");
								sky_USE= sky_USE + rs.getInt("SKY_USE");
								par_ABLE= par_ABLE + rs.getInt("PAR_ABLE");
								par_USE= par_USE + rs.getInt("PAR_USE");
								wkd_ABLE= wkd_ABLE + rs.getInt("WKD_ABLE");
								wke_ABLE= wke_ABLE + rs.getInt("WKE_ABLE");
								wkd_USE= wkd_USE + rs.getInt("WKD_USE");
								wkd_DEL= wkd_DEL + rs.getInt("WKD_DEL");
								wke_USE= wke_USE + rs.getInt("WKE_USE");
								wke_DEL= wke_DEL + rs.getInt("WKE_DEL");
								tot_BK =  pre_USE+sky_USE+par_USE+(wkd_USE-wkd_DEL)+(wke_USE-wke_DEL);

								if(gen_YN.equals("A")){ 
									gen_TXT = (wkd_USE-wkd_DEL)+(wke_USE-wke_DEL) + " / " + "무제한";
								}else{
									gen_TXT = (wkd_USE-wkd_DEL)+(wke_USE-wke_DEL) + " / " + (wkd_ABLE+wke_ABLE);
								}
								
								if(pre_YN.equals("Y")){
									pre_TXT = pre_USE + " / " + (pmi_wkd_bokg_num+pmi_wke_bokg_num);
								}else if(pre_YN.equals("N")){
									pre_TXT = "접근불가";
								}
								
								if(evt_YN.equals("Y")){
									evt_TXT = event_USE + " / " + evt_ABLE;
								}else if(evt_YN.equals("N")){
									evt_TXT = "접근불가";
								}
								
								if(sky_YN.equals("Y")){
									sky_TXT = sky_USE + " / " + sky_ABLE;
								}else if(sky_YN.equals("N")){
									sky_TXT = "혜택없음";
								}
								
								if(par_YN.equals("Y")){
									par_TXT = par_USE + " / " + par_ABLE;
								}else if(par_YN.equals("N")){
									par_TXT = "혜택없음";
								}
							}
						}
					}
				}
			}
			// 결과 출력하기	

			result.addString("EVT_TXT" 			,evt_TXT);	
			result.addString("PRE_TXT" 			,pre_TXT);	
			result.addString("SKY_TXT" 			,sky_TXT);	
			result.addString("PAR_TXT" 			,par_TXT);	
			result.addString("GEN_TXT" 			,gen_TXT);		
			result.addInt("TOT_BK" 				,tot_BK);
			result.addString("memb_id" 			,memb_id);	
			result.addString("memb_nm" 			,memb_nm);	
			
			result.addString("RESULT", "00"); //정상결과
			
			 
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
    * Query를 생성하여 리턴한다. - 멤버십 회원
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t  SELECT T1.CDHD_GRD_SEQ_NO, T1.CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO='0002'	\n");
		
		return sql.toString();
    }   
	    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 카드 회원
    ************************************************************************ */
    private String getCardGradeQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t  SELECT T1.CDHD_GRD_SEQ_NO, T1.CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO!='0002'	\n");
		
		return sql.toString();
    }	
	    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다. - 혜택기간 출력
    ************************************************************************ */
    private String getDateQuery(int intMemGrade, String memGbun){
        StringBuffer sql = new StringBuffer();
        
        if("mem".equals(memGbun)){	// 멤버십 회원
        	if(intMemGrade==4){	// 무료회원기간
        		
    			//2009.10.16 수정  기준 : 부킹혜택기간을 월단위로 한다.
    			sql.append("\t    SELECT CDHD_ID, JONN_ATON, 'Y' MEM_STATE	\n");
    			sql.append("\t     ,    SUBSTR(JONN_ATON,1,8) ST_DATE 	\n");
    			sql.append("\t     ,    TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8))+365,'YYYYMMDD')   ED_DATE 	\n");
    			sql.append("\t     ,    TO_CHAR(SYSDATE,'YYYYMM') ||'01' ST_DATE_MONTH	\n");
    			sql.append("\t     ,    TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH 	\n");
    			sql.append("\t     FROM BCDBA.TBGGOLFCDHD		\n");
    			sql.append("\t     WHERE CDHD_ID= ? 		\n");

    			/*2009.10.16 수정  기준 : 부킹혜택기간을 가입일자 기준으로 + 1달 임 
        		sql.append("\t  SELECT CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH	\n");
    			sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(ST_DATE_MONTH),1)-1,'YYYYMMDD') ED_DATE_MONTH	\n");
    			sql.append("\t	FROM (	\n");
    			sql.append("\t	    SELECT CDHD_ID, JONN_ATON, ST_DATE, TO_CHAR(TO_DATE(ST_DATE)+364,'YYYYMMDD') ED_DATE	\n");
    			sql.append("\t	    , CASE WHEN TO_CHAR(TO_DATE(ST_DATE),'DD')>TO_CHAR(SYSDATE,'DD')	\n");
    			sql.append("\t	        THEN TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM')	\n");
    			sql.append("\t	        ELSE TO_CHAR(SYSDATE,'YYYYMM')	\n");
    			sql.append("\t	        END||TO_CHAR(TO_DATE(ST_DATE),'DD') ST_DATE_MONTH	\n");
    			sql.append("\t	    FROM (	\n");
    			sql.append("\t	        SELECT CDHD_ID, JONN_ATON	\n");
    			sql.append("\t	        , TO_CHAR(SYSDATE, 'MMDD')	\n");
    			sql.append("\t	        , SUBSTR(JONN_ATON,5,4)	\n");
    			sql.append("\t	        , CASE WHEN SUBSTR(JONN_ATON,5,4)>=TO_CHAR(SYSDATE, 'MMDD')	\n");
    			sql.append("\t	            THEN TO_CHAR(SYSDATE-365,'YYYY')	\n");
    			sql.append("\t	            ELSE TO_CHAR(SYSDATE,'YYYY')	\n");
    			sql.append("\t	        END||SUBSTR(JONN_ATON,5,4) ST_DATE	\n");
    			sql.append("\t	        FROM BCDBA.TBGGOLFCDHD	\n");
    			sql.append("\t	        WHERE CDHD_ID=?	\n");
    			sql.append("\t	    )	\n");
    			sql.append("\t	)	\n");
    			*/
        		
        	}else{	// 유료회원기간
        		
        		sql.append("\t   SELECT CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH, ED_DATE_MONTH	\n");
        		sql.append("\t   , CASE WHEN ED_DATE<TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END MEM_STATE	\n");
        		sql.append("\t   FROM (	\n");
    			sql.append("\t   	SELECT CDHD_ID, JONN_ATON	\n");
    			sql.append("\t		, CASE WHEN ACRG_CDHD_JONN_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE)-365,'YYYYMMDD')	\n");
    			sql.append("\t		    ELSE ACRG_CDHD_JONN_DATE END ST_DATE	\n");
    			sql.append("\t		, CASE WHEN ACRG_CDHD_JONN_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE)-365,'YYYYMMDD')	\n");
    			sql.append("\t		    ELSE ACRG_CDHD_END_DATE END ED_DATE	\n");
    			sql.append("\t		, TO_CHAR(SYSDATE,'YYYYMM') ||'01' ST_DATE_MONTH	\n");
    			sql.append("\t		, TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH	\n");
    			sql.append("\t		FROM BCDBA.TBGGOLFCDHD	\n");
    			sql.append("\t		WHERE CDHD_ID=?	\n");
    			sql.append("\t   )	\n");
    			
    			/*2009.10.16 수정  기준 : 부킹혜택기간을 유료회원가입일자 기준으로 + 30일 임 (7월31일,8월31일 가입자 오류 남)
        		sql.append("\t  SELECT CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH	\n");
    			sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(ST_DATE_MONTH),1)-1,'YYYYMMDD') ED_DATE_MONTH	\n");
    			sql.append("\t	FROM (	\n");
    			sql.append("\t	    SELECT CDHD_ID, JONN_ATON, ST_DATE, TO_CHAR(TO_DATE(ST_DATE)+364,'YYYYMMDD') ED_DATE	\n");
    			sql.append("\t	    , CASE WHEN TO_CHAR(TO_DATE(ST_DATE),'DD')>TO_CHAR(SYSDATE,'DD')	\n");
    			sql.append("\t	        THEN TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM')	\n");
    			sql.append("\t	        ELSE TO_CHAR(SYSDATE,'YYYYMM')	\n");
    			sql.append("\t	        END||TO_CHAR(TO_DATE(ST_DATE),'DD') ST_DATE_MONTH	\n");
    			sql.append("\t	    FROM (	\n");
    			sql.append("\t	        SELECT CDHD_ID, JONN_ATON, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE	\n");
    			sql.append("\t	        , CASE WHEN ACRG_CDHD_END_DATE<TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
    			sql.append("\t	        THEN 	\n");
    			sql.append("\t	            CASE WHEN SUBSTR(JONN_ATON,5,4)>=TO_CHAR(SYSDATE, 'MMDD')	\n");
    			sql.append("\t	                THEN TO_CHAR(SYSDATE-365,'YYYY')	\n");
    			sql.append("\t	                ELSE TO_CHAR(SYSDATE,'YYYY')	\n");
    			sql.append("\t	            END||SUBSTR(JONN_ATON,5,4)	\n");
    			sql.append("\t	        ELSE ACRG_CDHD_JONN_DATE	\n");
    			sql.append("\t	        END ST_DATE	\n");
    			sql.append("\t	        FROM BCDBA.TBGGOLFCDHD	\n");
    			sql.append("\t	        WHERE CDHD_ID=?	\n");
    			sql.append("\t	    )	\n");
    			sql.append("\t	)	\n");
    			*/
        	}
        }else{	// 카드회원

			//2009.10.16 수정  기준 : 부킹혜택기간을 월단위로 한다.
			sql.append("\t SELECT	CDHD_ID, JONN_ATON, ST_DATE, ED_DATE \n");
			sql.append("\t        , TO_CHAR(SYSDATE,'YYYYMM') ||'01' ST_DATE_MONTH 	\n");
			sql.append("\t 	   , TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE,'YYYYMM') ||'01'),1)-1,'YYYYMMDD') ED_DATE_MONTH  \n");
			sql.append("\t FROM (	\n");
			sql.append("\t     SELECT CDHD_ID, REG_ATON AS JONN_ATON, SUBSTR(REG_ATON,1,8) ST_DATE \n");
			sql.append("\t          , TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8))+365,'YYYYMMDD') ED_DATE \n");
			sql.append("\t     FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
			sql.append("\t     WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?	\n");
			sql.append("\t )\n");

			/*2009.10.16 수정  기준 : 부킹혜택기간을 등급가입일자 기준으로 + 1달 임 
    		sql.append("\t  SELECT CDHD_ID, JONN_ATON, ST_DATE, ED_DATE, ST_DATE_MONTH	\n");
			sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(ST_DATE_MONTH),1)-1,'YYYYMMDD') ED_DATE_MONTH	\n");
			sql.append("\t	FROM (	\n");
			sql.append("\t	    SELECT CDHD_ID, JONN_ATON, ST_DATE, TO_CHAR(TO_DATE(ST_DATE)+364,'YYYYMMDD') ED_DATE	\n");
			sql.append("\t	    , CASE WHEN TO_CHAR(TO_DATE(ST_DATE),'DD')>TO_CHAR(SYSDATE,'DD')	\n");
			sql.append("\t	        THEN TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM')	\n");
			sql.append("\t	        ELSE TO_CHAR(SYSDATE,'YYYYMM')	\n");
			sql.append("\t	        END||TO_CHAR(TO_DATE(ST_DATE),'DD') ST_DATE_MONTH	\n");
			sql.append("\t	    FROM(	\n");
			sql.append("\t	        SELECT CDHD_ID, REG_ATON AS JONN_ATON, SUBSTR(REG_ATON,1,8) ST_DATE	\n");
			sql.append("\t	        , TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8))+364,'YYYYMMDD') ED_DATE	\n");
			sql.append("\t	        FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
			sql.append("\t	        WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?	\n");
			sql.append("\t	    )	\n");
			sql.append("\t	)	\n");
			*/	
        }
        
		return sql.toString();
    }		
        
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String gubun){
        StringBuffer sql = new StringBuffer(); 
        
		sql.append("\t  SELECT PMI_WKD_BOKG_NUM, PMI_WKE_BOKG_NUM, PMI_EVNT_APO_YN AS EVT_YN, (TO_NUMBER(PMI_EVNT_NUM)*12) AS EVT_ABLE, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE GOLF_SVC_APLC_CLSS='9001' AND CDHD_ID=? and NVL(NUM_DDUC_YN,'Y')='Y' and 	PGRS_YN='B' \n");
		
		if(gubun.equals("mem")){ // 기존에 값이 없는것들은 회원으로 구분한다.
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	) AS EVENT_USE, PMI_BOKG_APO_YN AS PRE_YN, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGAFFIGREEN T4 ON T1.AFFI_GREEN_SEQ_NO=T4.AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=? AND RSVT_YN='Y' and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='M'	\n");

		
		
		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(T1.REG_ATON,1,8)>=? AND SUBSTR(T1.REG_ATON,1,8)<=?	\n");
		sql.append("\t	) AS PRE_USE, DRDS_BOKG_LIMT_YN AS SKY_YN, DRDS_BOKG_YR_ABLE_NUM AS SKY_ABLE, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=? AND RSVT_YN='Y' AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='S' and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

		
		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	    AND T4.GOLF_SVC_RSVT_NO IS NULL	\n");
		sql.append("\t	) AS SKY_USE, PAR_3_BOKG_LIMT_YN AS PAR_YN, PAR_3_BOKG_YR_ABLE_NUM AS PAR_ABLE, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=? AND T1.RSVT_YN='Y'AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='P' and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");

		
		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	    AND T2.GOLF_SVC_RSVT_NO IS NULL	\n");
		sql.append("\t	) AS PAR_USE, GEN_BOKG_LIMT_YN AS GEN_YN, GEN_WKD_BOKG_NUM AS WKD_ABLE, GEN_WKE_BOKG_NUM AS WKE_ABLE, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0006' AND PGRS_YN='Y' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y'	\n");

		
		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	) AS WKD_USE, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0006' AND PGRS_YN='N' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		

		
		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	) AS WKD_DEL, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0007' AND PGRS_YN='Y' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y'	\n");

		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}
		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	) AS WKE_USE, (	\n");
		sql.append("\t	    SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='0007' AND PGRS_YN='N' AND CSLT_YN='N' and NVL(NUM_DDUC_YN,'Y')='Y'	\n");

		if(gubun.equals("mem")){
			sql.append("\t	AND (RSVT_CDHD_GRD_SEQ_NO IS NULL OR RSVT_CDHD_GRD_SEQ_NO=?)	\n");
		}else{
			sql.append("\t	AND RSVT_CDHD_GRD_SEQ_NO=?	\n");
		}

		
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=? AND SUBSTR(REG_ATON,1,8)<=?	\n");
		sql.append("\t	) AS WKE_DEL FROM BCDBA.TBGGOLFCDHDBNFTMGMT T_BNF	\n");
		sql.append("\t	WHERE TO_NUMBER(CDHD_SQ2_CTGO)=TO_NUMBER(?)	\n");
		
		return sql.toString();
    }
}
