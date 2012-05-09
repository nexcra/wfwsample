/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopPenaltyDaoProc
*   작성자    : 김상범   
*   내용      : 관리자 > 부킹 > TOP골프카드부킹전용 > 패널티관리 리스트조회 
*   적용범위  : golf
*   작성일자  : 2010-11-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항  
*
***************************************************************************************************/
   
package com.bccard.golf.dbtao.proc.admin.booking.premium;
                
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;


public class GolfadmTopPenaltyListDaoProc extends   AbstractProc {
	
	/** *****************************************************************
	 * GolfadmTopPenaltyDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmTopPenaltyListDaoProc() { super();}	 
	

		/**
		* Proc 실행.
		* @param Connection con
		* @param TaoDataSet dataSet
		* @return TaoResult
		*/
		public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
			debug("  GolfadmTopPenaltyListDaoProc start:" );
			String messageKey = "CommonProc_0000";
			String title = data.getString("TITLE");
			ResultSet rs = null;
			Connection conn = null;
			PreparedStatement pstmt = null;
			DbTaoResult  result =  new DbTaoResult(title);

			long affiGreenSeqNo    =0L;
			String pointDetlCd     ="";
			try {
					conn = context.getDbConnection("default", null);
					      debug(" lproc -----------------------------"); 
					String roundDateFrom    = data.getString("roundDateFrom");
					String roundDateTo   	= data.getString("roundDateTo");      
					String setFrom		= data.getString("setFrom"); 	 
					String setTo	        = data.getString("setTo"); 	
		    	affiGreenSeqNo     = data.getLong("affiGreenSeqNo"); 
		      debug("try affiGreenSeqNo ["+affiGreenSeqNo+"]");
					pointDetlCd	= data.getString("pointDetlCd"); 	
					long pageNo	        = data.getLong("pageNo"); 	
					long recordCnt          = data.getLong("recordCnt");
					debug("lporc try recordCnt ["+recordCnt+"]");

					if (pageNo <= 0) pageNo = 1L;
					
					//조회 ----------------------------------------------------------
					result = new DbTaoResult(title); 
					String sql = this.getSelectQuery(data);
					// 입력값 (INPUT)         
					int idx = 0;
					pstmt = conn.prepareStatement(sql.toString());

					pstmt.setString(++idx, roundDateFrom);
					pstmt.setString(++idx, roundDateTo);
					pstmt.setString(++idx, setFrom);
					pstmt.setString(++idx, setTo);
					if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++idx, pointDetlCd);
					if(affiGreenSeqNo!=0L) pstmt.setLong(++idx, affiGreenSeqNo);
					pstmt.setString(++idx, roundDateFrom);
					pstmt.setString(++idx, roundDateTo);
					pstmt.setString(++idx, setFrom);
					pstmt.setString(++idx, setTo);
					if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++idx, pointDetlCd);
					rs = pstmt.executeQuery();
      
					if(rs != null) {			 
							debug(" lproc List  if ----------------------rs----------------");      
						while(rs.next())  {	
							debug("  while ------------------------rs----------------");      
							result.addString("memId", rs.getString("memid"));
							debug("  while -----memId-"+rs.getString("memid"));  
							result.addString("seqNo", rs.getString("seq_no"));
							debug("  while -----seq_no-"+rs.getString("seq_no"));  
							result.addLong("pointClss", rs.getLong("point_clss"));
							debug("  while -----point_clss-"+rs.getString("point_clss"));  
							result.addString("pointDetlCd", rs.getString("point_detl_cd"));
							debug("  while -----point_detl_cd-"+rs.getString("point_detl_cd"));  
							result.addString("pointMemo", rs.getString("point_memo"));
							debug("  while -----point_memo-"+rs.getString("point_memo"));  
							result.addLong("affiGreenSeqNo", rs.getLong("AFFI_GREEN_SEQ_NO"));
							debug("  while affiGreenSeqNo ["+rs.getLong("AFFI_GREEN_SEQ_NO")+"]");
							result.addString("roundDate", rs.getString("round_date"));
							debug("  while -----round_date-"+rs.getString("round_date"));  	
							result.addString("roundDateFmt", this.getDateTimeFmt(rs.getString("round_date"), "yyyyMMdd", "yyyy.MM.dd"));
							debug("  while -----point_memo-"+rs.getString("round_date"));  	
							result.addString("breachCont", rs.getString("breach_cont"));
							debug("  while -----breach_cont-"+rs.getString("breach_cont"));  	
							result.addString("penaltyApplyClss", rs.getString("penalty_apply_clss"));
							debug("  while -----penalty_apply_clss-"+rs.getString("penalty_apply_clss"));  	
							result.addString("setDateFrom", rs.getString("set_from"));
							debug("  while -----setDateFrom-"+rs.getString("set_from"));  
							result.addString("setDateFromFmt", this.getDateTimeFmt(rs.getString("set_from"), "yyyyMMdd", "yyyy.MM.dd"));
							debug("  while -----setDateFromFmt-"+rs.getString("set_from"));  
							result.addString("setDateTo", rs.getString("set_to"));
							debug("  while -----setDateTo-"+rs.getString("set_to"));  
							result.addString("setDateToFmt", this.getDateTimeFmt(rs.getString("set_to"), "yyyyMMdd", "yyyy.MM.dd"));
							debug("  while -----setDateToFmt-"+rs.getString("set_to"));  
							result.addString("penaltyResnCd", rs.getString("penalty_resn_cd"));
							debug("  while -----penaltyResnCd-"+rs.getString("penalty_resn_cd"));  
							result.addString("name", rs.getString("name"));
							debug("  while -----name-"+rs.getString("name"));  
							//   result.addLong("pageNo", pageNo);
							result.addString("greenNm", rs.getString("green_nm"));
							debug("while green_nm ["+rs.getString("green_nm"));
							result.addLong("recordCnt", rs.getLong("record_cnt"));
							debug("  while -----record_cnt-"+rs.getString("record_cnt"));  

							result.addString("RESULT", "00"); //정상결과*/
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

		/** ***********************************************************************
		* Query를 생성하여 리턴한다.    
		************************************************************************ */
	
		private String getSelectQuery(TaoDataSet dataSet) throws Exception{
				long affiGreenSeqNo     = dataSet.getLong("affiGreenSeqNo");
				String pointDetlCd      = dataSet.getString("pointDetlCd");
				StringBuffer sql = new StringBuffer();
								sql.append("\n");
								sql.append("\n");
								sql.append("    SELECT x.*, y.record_cnt").append("\n");
								sql.append("    FROM (").append("\n");
								sql.append("            SELECT a.memid, a.seq_no, a.point_clss, a.point_detl_cd, a.point_memo, c.affi_green_seq_no, a.round_date, ").append("\n");
								sql.append("              a.breach_cont, a.penalty_apply_clss, a.set_from, a.set_to, a.penalty_resn_cd, b.name, c.GREEN_NM ").append("\n");
								sql.append("            FROM bcdba.tbgfpoint a, bcdba.ucusrinfo b, bcdba.TBGAFFIGREEN c ").append("\n");
								sql.append("            WHERE a.round_date >= ? ").append("\n");
								sql.append("            AND a.round_date <= ? ").append("\n");
								sql.append("            AND (a.set_from >= ? OR a.set_to <= ?) ").append("\n");
								if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
												sql.append("            AND a.point_detl_cd = ? ").append("\n");
								}
								sql.append("            AND a.point_clss = '60' ").append("\n");
								sql.append("            AND c.affi_firm_clss = '1000' ").append("\n");
								sql.append("            AND a.memid = b.memid(+) ").append("\n");
								if(affiGreenSeqNo!=0L){
												sql.append("            AND c.affi_green_seq_no = ? ").append("\n");
								}
								sql.append("            AND a.green_no = c.affi_green_seq_no ").append("\n");
								sql.append("            ORDER BY a.round_date DESC ").append("\n");
								sql.append("    ) x, (").append("\n");
								sql.append("            SELECT COUNT(*) record_cnt").append("\n");
								sql.append("            FROM bcdba.tbgfpoint a ").append("\n");
								sql.append("            WHERE a.round_date >= ? ").append("\n");
								sql.append("            AND a.round_date <= ? ").append("\n");
								sql.append("            AND (a.set_from >= ? OR a.set_to <= ?) ").append("\n"); 
								if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
												sql.append("            AND a.point_detl_cd = ? ").append("\n");
								}
								sql.append("            AND a.point_clss = '60' ").append("\n");
								sql.append("    ) y").append("\n");
						return sql.toString();
		}
		/** ***********************************************************************
		* 날짜 데이터 포맷 변환.
		* @param src String
		* @param sourcePattern String
		* @param objectPattern String
		************************************************************************ */
		protected String getDateTimeFmt(String src, String sourcePattern, String objectPattern ) {
			if ( src == null ) return "";
			try {
					return DateUtil.format(src,sourcePattern,objectPattern);
			} catch (Throwable t) {
			return "";
			}
		}

}
