/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2010.12.08 [김상범]
* 내용 : 회원정보 목록 조회 프로세스
* 수정 : 
* 내용 : 해당조건의 회원정보 목록을 조회한다..
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.common.StrUtil;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;		// 강선영 추가
import com.bccard.waf.core.WaContext;					// 강선영 추가

		/** ****************************************************************************
		* 회원정보 목록 조회 수행 프로세스.
		* @author  김상범
		* @version 2010.12.08
		**************************************************************************** */
public class GolfadmTopPenaltyMemInqProc extends AbstractProc  {
		public static final String TITLE = "페널티대상 회원조회";

		/** ***********************************************************************
		* Proc 실행.
		* @param con Connection
		* @param dataSet 조회조건정보
		************************************************************************ */
		public DbTaoResult execute(WaContext context, TaoDataSet dataSet) throws BaseException { // 강선영 추가
                debug("=====================================================================");
                debug("=====================================================================");
                debug("=============               GolfadmTopPenaltyMemInqProc   ===========");
                debug("=====================================================================");
                debug("=====================================================================");
                debug("=====================================================================");
				String messageKey = "CommonProc_0000";
				PreparedStatement pstmt = null;
				ResultSet rset = null;
				DbTaoResult result = null;
				Connection con = null;
				try{
						//조회 조건 Validation
						String name			= dataSet.getString("name");
						String account	= dataSet.getString("account");
						long pageNo			= dataSet.getLong("pageNo");
						long recordsInPage = dataSet.getLong("recordsInPage");
						
						if (pageNo <= 0) pageNo = 1L;
						long startR = (pageNo-1L) * recordsInPage + 1L;
						long endR = pageNo * recordsInPage;

						con = context.getDbConnection("default", null);

						result 			= new DbTaoResult(TITLE);
						String sql	= this.getSelectQuery(dataSet);
						pstmt 			= con.prepareStatement(sql);
						
						int pidx = 0;            
						if(name!=null && !"".equals(name.trim())) pstmt.setString(++pidx, name);
						if(account!=null && !"".equals(account.trim())) pstmt.setString(++pidx, account);
						if(name!=null && !"".equals(name.trim())) pstmt.setString(++pidx, name);
						if(account!=null && !"".equals(account.trim())) pstmt.setString(++pidx, account);
						pstmt.setLong(++pidx, endR);
						pstmt.setLong(++pidx, startR);
						pstmt.setLong(++pidx, endR);
						rset = pstmt.executeQuery();

						boolean existsData = false;
						while(rset.next()){
								if(!existsData){
								result.addString("result", "00");
								}
								result.addString("name", rset.getString("name"));
								result.addString("account", rset.getString("account"));
								result.addLong("memId", rset.getLong("memid"));
								result.addString("phone", rset.getString("phone"));
								result.addString("mobile", rset.getString("mobile"));
								result.addLong("pageNo", pageNo);
								result.addLong("recordCnt", rset.getLong("record_cnt"));
								existsData = true;
								con.commit();
						}
						if(!existsData){
							result.addString("result","01");
						}
								
				}catch(Exception e){
						try{ con.rollback(); }catch(Exception ee){} //추가함
						MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, messageKey, null );
						throw new DbTaoException(msgEtt,e); // 계속 여기서 걸림
				}finally{
					try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
					try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
					try{ if(con != null) con.close();  }catch( Exception ignored){}  // 강선영 추가
				}
				return result;
		}

		/** ***********************************************************************
		* Query를 생성하여 리턴한다.    
		************************************************************************ */
		private String getSelectQuery(TaoDataSet dataSet) throws Exception{
		String name				= dataSet.getString("name");
		String account		= dataSet.getString("account");
		StringBuffer sql 	= new StringBuffer();
		sql.append("\n");
		sql.append("\n");
		sql.append("SELECT *").append("\n");
		sql.append("FROM (").append("\n");
		sql.append("	SELECT x.*, y.record_cnt, ROWNUM row_num").append("\n");
		sql.append("	FROM (").append("\n");
		sql.append("		SELECT a.name, a.account, a.memid, a.phone, a.mobile ").append("\n");
		sql.append("		FROM bcdba.ucusrinfo a, (select memid from bcdba.tbgfbooking group by memid) b ").append("\n");
		sql.append("		WHERE a.memid is not null ").append("\n");
		sql.append("		AND a.memid = b.memid ").append("\n");
		if(name!=null && !"".equals(name.trim())){
			sql.append("		AND a.name = ? ").append("\n");
		}
		if(account!=null && !"".equals(account.trim())){
			sql.append("		AND a.account = ? ").append("\n");
		}
		sql.append("	) x, (").append("\n");
		sql.append("		SELECT COUNT(*) record_cnt").append("\n");
		sql.append("		FROM bcdba.ucusrinfo a, (select memid from bcdba.tbgfbooking group by memid) b ").append("\n");
		sql.append("		WHERE a.memid is not null ").append("\n");
		sql.append("		AND a.memid = b.memid ").append("\n");
		if(name!=null && !"".equals(name.trim())){
			sql.append("		AND a.name = ? ").append("\n");
		}
		if(account!=null && !"".equals(account.trim())){
			sql.append("		AND a.account = ? ").append("\n");
		}
		sql.append("	) y").append("\n");
		sql.append("	WHERE ROWNUM <= ?").append("\n");
		sql.append(")").append("\n");
		sql.append("WHERE row_num >= ?").append("\n");
		sql.append("AND row_num <= ?").append("\n");
		return sql.toString();
		}
}
