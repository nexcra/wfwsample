/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2010.12.08 [김상범]
* 내용 : 페널트관리 수정 프로세스
* 수정 : 
* 내용 : 페널트관리 정보를 수정한다.
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection; 								// 강선영 추가
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;	// 강선영 추가
import com.bccard.waf.core.WaContext;				// 강선영 추가

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;
/** ****************************************************************************
 * 페널트관리 수정 수행 프로세스
 * @author  김상범
 * @version 2010.12.08
 **************************************************************************** */

public class GolfadmTopPenaltyUpdInsProc extends  AbstractProc {	// 강선영 추가

	public static final String TITLE = "페널트관리 수정";

		/** ***********************************************************************
		* Proc 실행.
		* @param con Connection
		* @param dataSet 조회조건정보
		************************************************************************ */

		public DbTaoResult execute(WaContext context, TaoDataSet dataSet) throws BaseException { // 강선영 추가

			String messageKey = "DbTaoFail_100";
			PreparedStatement pstmt = null;
			Connection con = null;

			DbTaoResult result = null;

			try{
					debug("*********************************************************************** ");
					debug("* GolfadmTopPenaltyUpdInsProc 실행.                                     ");
					debug("* @param con Connection                                                 ");
					debug("* @param dataSet 조회조건정보                                           ");
					debug("*********************************************************************** ");
					//조회 조건 Validation 
					String memId		= dataSet.getString("memId");			//회원번호
					String seqNo		= dataSet.getString("seqNo");			//일련번호
					String pointClss	= dataSet.getString("pointClss");		//패널티구분
					String pointDetlCd      = dataSet.getString("pointDetlCd");		//패널티종류
					String pointMemo	= dataSet.getString("pointMemo");		//위약내용
					long   affiGreenSeqNo	= dataSet.getLong("affiGreenSeqNo");		//골프장번호
					String greenNM		= dataSet.getString("greenNM");		        //골프장명
					debug("lproc UPDINS  greenNM["+greenNM);
					String roundDate	= dataSet.getString("roundDate");		//위약일자
					String breachCont	= dataSet.getString("breachCont");		//적용내용
					String penaltyApplyClss	= dataSet.getString("penaltyApplyClss");	//페널티적용구분
					String setFrom		= dataSet.getString("setFrom");			//적용시작일자
					String setTo		= dataSet.getString("setTo");			//적용종료일자
					String penaltyResnCd	= dataSet.getString("penaltyResnCd");   	//페널티사유코드
					String regAdminNm	= dataSet.getString("regAdminNm");             // 등록자성명
					String regIp		= dataSet.getString("regIp");	                // 등록자IP
					String regAdminNo	= dataSet.getString("regAdminNo");              // 등록자사번
					if ( "".equals(seqNo) || "".equals(memId) ){
							messageKey = "DbTao_Noti_001";
							throw new Exception(messageKey);
					}

					con = context.getDbConnection("default", null);

					con.setAutoCommit(false);
					String sql = this.getSelectQuery();
					pstmt = con.prepareStatement(sql);
					int pidx = 0;
					pstmt.setString(++pidx, StrUtil.isNull(pointClss,""));		//패널티구분
					pstmt.setString(++pidx, StrUtil.isNull(pointDetlCd,""));	//패널티종류
					pstmt.setString(++pidx, StrUtil.isNull(pointMemo,""));		//위약내용
					if(affiGreenSeqNo!=0L) pstmt.setLong(++pidx, affiGreenSeqNo);	//골프장번호 
					//pstmt.setString(++pidx, StrUtil.isNull(greenNM,""));		//골프장명
					//debug("lproc UPDINS pstmt greenNM["+greenNM);   // 강선영 추가
					pstmt.setString(++pidx, StrUtil.isNull(roundDate,""));		//위약일자
					pstmt.setString(++pidx, StrUtil.isNull(breachCont,""));		//적용내용
					pstmt.setString(++pidx, StrUtil.isNull(penaltyApplyClss,""));	//페널티적용구분
					pstmt.setString(++pidx, StrUtil.isNull(setFrom,""));		//적용시작일자
					pstmt.setString(++pidx, StrUtil.isNull(setTo,""));		//적용종료일자
					pstmt.setString(++pidx, StrUtil.isNull(penaltyResnCd,""));	//페널티사유코드
					pstmt.setString(++pidx, StrUtil.isNull(regAdminNm,""));		//처리관리자명
					pstmt.setString(++pidx, StrUtil.isNull(regIp,""));		//처리IP
					pstmt.setLong(++pidx, Long.parseLong(regAdminNo));		//처리관리자번호
					pstmt.setLong(++pidx, Long.parseLong(memId));			//회원번호
					pstmt.setLong(++pidx, Long.parseLong(seqNo));			//일련번호
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
				try{ if(con != null) con.close();  }catch( Exception ignored){}  // 강선영 추가
			}
				return result;
		}

			/** ***********************************************************************
			* Query를 생성하여 리턴한다.    
			************************************************************************ */
			private String getSelectQuery() throws Exception{

			StringBuffer sql = new StringBuffer();
			
			sql.append("\n update BCDBA.TBGFPOINT set ");
			sql.append("\n POINT_CLSS=?, POINT_DETL_CD=?, POINT_MEMO=?, GREEN_NO=?,  ROUND_DATE=?, ");
			sql.append("\n BREACH_CONT=?, PENALTY_APPLY_CLSS=?, SET_FROM=?, SET_TO=?, PENALTY_RESN_CD=?, ");
			sql.append("\n REG_DATE=to_char(sysdate,'YYYYMMDDHH24MISS'), ");
			sql.append("\n REG_ADMIN_NM=?, REG_IP=?, REG_ADMIN_NO=? ");
			sql.append("\n where MEMID=? and SEQ_NO=? ");
			return sql.toString();
			}
}
