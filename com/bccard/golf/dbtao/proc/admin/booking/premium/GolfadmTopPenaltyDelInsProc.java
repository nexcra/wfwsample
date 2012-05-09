/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2010.12.07 [김상범]
* 내용 : 페널트관리 삭제 프로세스
* 수정 : 
* 내용 : 페널트관리 정보를 삭제한다.
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.common.StrUtil;

import com.bccard.waf.action.AbstractProc; 
import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.BaseException;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;
/** ****************************************************************************
 * 페널트관리 추가 수행 프로세스
 * @author  김상범
 * @version 2010.12.07
 **************************************************************************** */
public class GolfadmTopPenaltyDelInsProc extends AbstractProc  {
		public static final String TITLE = "페널트관리 삭제";

		/** ***********************************************************************
		* Proc 실행.
		* @param con Connection
		* @param dataSet 조회조건정보
		************************************************************************ */
		public DbTaoResult  execute(WaContext context, TaoDataSet dataSet) throws BaseException {
				String messageKey = "DbTaoFail_100";
				PreparedStatement pstmt = null;
				DbTaoResult result = null;
				Connection con = null;
				
			try{
			//조회 조건 Validation 
				String seqNo		= dataSet.getString("seqNo");
				String memId		= dataSet.getString("memId");

				if ( "".equals(seqNo) || "".equals(memId) ){
				    messageKey = "DbTao_Noti_001";
				    throw new Exception(messageKey);
				}
				
				con = context.getDbConnection("default", null);
				con.setAutoCommit(false);
				String sql = this.getSelectQuery();
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				pstmt.setLong(++pidx, Long.parseLong(memId));				//회원번호
				pstmt.setLong(++pidx, Long.parseLong(seqNo));				//일련번호
				int rset = pstmt.executeUpdate();

				if(rset != 1){  
					messageKey = "DbTao_Noti_201";
						throw new Exception(messageKey);
				}

				result = new DbTaoResult(TITLE);
				result.addString("result","00");
				con.commit();

			}catch(Exception e){
			try{ con.rollback(); }catch(Exception ee){}
						MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, messageKey, null );
						throw new DbTaoException(msgEtt,e);
			}finally{
			try{ if(pstmt != null) pstmt.close();  con.setAutoCommit(true); }catch( Exception ignored){}
			try{ if(con != null) con.close();  }catch( Exception ignored){}
			}
			return result;
		}

		/** ***********************************************************************
		* Query를 생성하여 리턴한다.    
		************************************************************************ */
		private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n delete from BCDBA.TBGFPOINT ");
		sql.append("\n where MEMID=? and SEQ_NO=? ");
			return sql.toString();
		}

}
