/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : AdmGolfTopPointLstInqProc
*   작성자    : 김상범   
*   내용      : 관리자 > 부킹 > TOP골프카드부킹전용 > 포인트관리 리스트조회 
*   적용범위  : golf
*   작성일자  : 2010-12-29  
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


public class AdmGolfTopPointLstInqProc extends   AbstractProc {
		public static final String TITLE = "포인트관리 목록조회";
	/** *****************************************************************
	 * GolfadmTopPenaltyDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public AdmGolfTopPointLstInqProc() { super();}	 
	
		/**
		* Proc 실행.
		* @param Connection con
		* @param TaoDataSet dataSet
		* @return TaoResult
		*/
		public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException {
			debug("  AdmGolfTopPointLstInqProc start:" );
			String messageKey = "CommonProc_0000";
			String title = dataSet.getString("TITLE");
			ResultSet rset = null;
			Connection conn = null;
			PreparedStatement pstmt = null;
			DbTaoResult  result =  new DbTaoResult(title);

			try {
			conn = context.getDbConnection("default", null);
			//조회 조건 Validation
			String roundDateFrom = dataSet.getString("roundDateFrom");
			String roundDateTo = dataSet.getString("roundDateTo");
			String pointDetlCd = dataSet.getString("pointDetlCd");
			long pageNo = dataSet.getLong("pageNo");
			long recordsInPage = dataSet.getLong("recordsInPage");

			if ("".equals(roundDateFrom)){
					messageKey = "BookingProc_0001";
					throw new Exception(messageKey);
			}
			if ("".equals(roundDateTo)){
					messageKey = "BookingProc_0002";
					throw new Exception(messageKey);
			}

			if (pageNo <= 0) pageNo = 1L;
			long startR = (pageNo-1L) * recordsInPage + 1L;
			long endR = pageNo * recordsInPage;
 
			result = new DbTaoResult(TITLE);
			String sql = this.getSelectQuery(dataSet);
			pstmt = conn.prepareStatement(sql);
			
			int pidx = 0;            
			pstmt.setString(++pidx, roundDateFrom);
			pstmt.setString(++pidx, roundDateTo);
			if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++pidx, pointDetlCd);
			pstmt.setString(++pidx, roundDateFrom);
			pstmt.setString(++pidx, roundDateTo);
			if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++pidx, pointDetlCd);
			pstmt.setLong(++pidx, endR);
			pstmt.setLong(++pidx, startR);
			pstmt.setLong(++pidx, endR);
			rset = pstmt.executeQuery();
			
			boolean existsData = false;
			while(rset.next()){
					if(!existsData){
						result.addString("result", "00");
					}
				debug(" lproc List  if ----------------------rs----------------");
				result.addString("memId", rset.getString("memid"));
				debug("  while -----memId-"+rset.getString("memid"));
				result.addString("seqNo", rset.getString("seq_no"));
				debug("  while -----seqNo-"+rset.getString("seq_no"));
				result.addLong("pointClss", rset.getLong("point_clss"));
				debug("  while -----pointClss-"+rset.getLong("point_clss"));
				result.addString("pointDetlCd", rset.getString("point_detl_cd"));
				debug("  while -----pointDetlCd-"+rset.getString("point_detl_cd"));
				result.addString("pointMemo", rset.getString("point_memo"));
				debug("  while -----pointMemo-"+rset.getString("point_memo"));
				result.addString("roundDate", rset.getString("round_date"));
				debug("  while -----roundDate-"+rset.getString("round_date"));
				result.addString("roundDateFmt", this.getDateTimeFmt(rset.getString("round_date"), "yyyyMMdd", "yyyy.MM.dd"));
//				debug("  while ****** -----roundDateFmt ----*****"+rset.getString("roundDateFmt"));
				result.addString("name", rset.getString("name"));
				debug("  while -----name-"+rset.getString("name"));
				result.addLong("pageNo", pageNo);
			//	debug("  while -----pageNo-"+rset.getLong("pageNo"));
				result.addLong("recordCnt", rset.getLong("record_cnt"));
			//	debug("  while -----recordCnt-"+rset.getLong("record_cnt"));
				debug("while end --------------------");
				existsData = true;
			}
            
				if(!existsData){
						result.addString("result","01");
				}
			} catch (Throwable t) {
					throw new BaseException(t);
			}finally{
						try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
						try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
						try{ if(conn  != null) conn.close();  } catch (Exception ignored) {}
			}
			return result;
		}

		/** ***********************************************************************
		* Query를 생성하여 리턴한다.    
		************************************************************************ */
		private String getSelectQuery(TaoDataSet dataSet) throws Exception{
		String pointDetlCd		= dataSet.getString("pointDetlCd");
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\n");
		sql.append("SELECT *").append("\n");
		sql.append("FROM (").append("\n");
		sql.append("	SELECT x.*, y.record_cnt, ROWNUM row_num").append("\n");
		sql.append("	FROM (").append("\n");
		sql.append("		SELECT a.memid, a.seq_no, a.point_clss, a.point_detl_cd, ").append("\n");
		sql.append("		  a.point_memo, a.round_date, b.name ").append("\n");
		sql.append("		FROM bcdba.tbgfpoint a, bcdba.ucusrinfo b ").append("\n");
		sql.append("		WHERE a.round_date >= ? ").append("\n");
		sql.append("		AND a.round_date <= ? ").append("\n");
		if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
			sql.append("		AND a.point_detl_cd = ? ").append("\n");
		}
		sql.append("		AND a.point_clss = '50' ").append("\n");
		sql.append("		AND a.memid = b.memid(+) ").append("\n");
		sql.append("		ORDER BY a.round_date DESC ").append("\n");
		sql.append("	) x, (").append("\n");
		sql.append("		SELECT COUNT(*) record_cnt").append("\n");
		sql.append("		FROM bcdba.tbgfpoint a ").append("\n");
		sql.append("		WHERE a.round_date >= ? ").append("\n");
		sql.append("		AND a.round_date <= ? ").append("\n");
		if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
			sql.append("		AND a.point_detl_cd = ? ").append("\n");
		}
		sql.append("		AND a.point_clss = '50' ").append("\n");
		sql.append("	) y").append("\n");
		sql.append("	WHERE ROWNUM <= ?").append("\n");
		sql.append(")").append("\n");
		sql.append("WHERE row_num >= ?").append("\n");
		sql.append("AND row_num <= ?").append("\n");
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
