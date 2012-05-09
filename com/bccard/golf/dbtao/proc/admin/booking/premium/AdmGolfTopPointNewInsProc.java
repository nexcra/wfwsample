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
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;


import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;
/** ****************************************************************************
 * 포인트관리 추가 수행 프로세스
 * @author 김상범
 * @version 2010.12.29
 **************************************************************************** */
public class AdmGolfTopPointNewInsProc extends AbstractProc   {
		public static final String TITLE = "포인트관리 등록";

		/** ***********************************************************************
		* Proc 실행.
		* @param con Connection
		* @param dataSet 조회조건정보
		************************************************************************ */
		public DbTaoResult execute(WaContext context, TaoDataSet dataSet) throws BaseException {
			String messageKey = "DbTaoFail_100";
			PreparedStatement pstmt = null;
			DbTaoResult result = null;
			Connection con = null;
			debug("***********************************************************************************");
			debug(" Action  AdmGolfTopPointNewInsProc.java 등록 execute");
			debug("***********************************************************************************");

			try{
					//조회 조건 Validation 
					String roundDate	= dataSet.getString("roundDate");
					String memId		= dataSet.getString("memId");
					String pointClss	= dataSet.getString("pointClss");
					String pointDetlCd	= dataSet.getString("pointDetlCd");
					String pointMemo	= dataSet.getString("pointMemo");
					String regIp		= dataSet.getString("regIp");
					String regAdminNo	= dataSet.getString("regAdminNo");
					String regAdminNm	= dataSet.getString("regAdminNm");

					if ( "".equals(memId) || "".equals(roundDate) ){
							messageKey = "DbTao_Noti_001";
							throw new Exception(messageKey);
					}

					con = context.getDbConnection("default", null);
					con.setAutoCommit(false);
					String sql = this.getSelectQuery();
					pstmt = con.prepareStatement(sql);
		
					int pidx = 0;
					pstmt.setLong(++pidx, Long.parseLong(memId));				//회원번호
					pstmt.setLong(++pidx, Long.parseLong(memId));				//memId
					pstmt.setString(++pidx, StrUtil.isNull(pointClss,""));		//포인트구분
					pstmt.setString(++pidx, StrUtil.isNull(pointDetlCd,""));	//포인트세부코드
					pstmt.setString(++pidx, StrUtil.isNull(pointMemo,""));		//포인트내용
					pstmt.setString(++pidx, StrUtil.isNull(roundDate,""));		//라운드일자(년월일)
					pstmt.setString(++pidx, StrUtil.isNull(regAdminNm,""));		//처리관리자명
					pstmt.setString(++pidx, StrUtil.isNull(regIp,""));			//처리IP
					pstmt.setLong(++pidx, Long.parseLong(regAdminNo));			//처리관리자번호
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

		sql.append("\n insert into BCDBA.TBGFPOINT ");
		sql.append("\n (MEMID, SEQ_NO, POINT_CLSS, POINT_DETL_CD, POINT_MEMO, ROUND_DATE, ");
		sql.append("\n REG_DATE, REG_ADMIN_NM, REG_IP, REG_ADMIN_NO) ");
		sql.append("\n values (?, ");
		sql.append("\n (select lpad(nvl(max(SEQ_NO),'0')+1,10,'0') from BCDBA.TBGFPOINT where MEMID = ?), ");
		sql.append("\n ?, ?, ?, ?, ");
		sql.append("\n to_char(sysdate,'YYYYMMDDHH24MISS'), ?, ?, ?) ");
		debug("***********************************************************************************");
		debug(" Action  AdmGolfTopPointNewInsProc.java  SQL execute");
		debug(sql.toString());
		debug("***********************************************************************************");

		return sql.toString();
		}

}
