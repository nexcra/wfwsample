/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBusInsDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 사용자 > 이벤트->골프장버스이벤트->등록 처리
*   적용범위  : Golf
*   작성일자  : 2009-09-30
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  Golf
 * @author	Media4th
 * @version 1.0
 **************************************************************************** */
public class GolfEvntBusInsDaoProc extends DbTaoProc {
	/**
	 * Proc 실행. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt				= null;
		ResultSet rs 						= null;
		String title						= dataSet.getString("title");
		String actnKey 						= null;
		DbTaoResult result					= new DbTaoResult(title);
		
		try {
			actnKey 						= dataSet.getString("actnKey");
			int res 						= 0;
			int pidx 						= 0;
			int cntVal						= 0;
			String pgrs_yn					= "Y";	// W	대기 -  28명 이상 Y	신청 -  28 미만

			
			if(!"".equals(dataSet.getString("teof_date")) && dataSet.getString("teof_date") != null)
			{
				
				long maxValue = this.getMaxQuery(con);

				//해당 날짜에 등록된 갯수 (부킹확정)
				pstmt = con.prepareStatement(getCheckQuery());
				pidx = 0;				
				pstmt.setString(++pidx, dataSet.getString("teof_date"));				
				rs = pstmt.executeQuery();

				if ( rs.next() ) {
					cntVal = rs.getInt("cnt");	// 부킹확정된 갯수
				}

				if ( (28-cntVal) < dataSet.getInt("arrCnt") ) {
					pgrs_yn = "W";				// 대기중
				}
				pstmt = con.prepareStatement(getInsQuery());
				pidx = 0;
				pstmt.setLong(++pidx, maxValue);
				pstmt.setString(++pidx, "9002");
				pstmt.setString(++pidx, pgrs_yn);
				pstmt.setString(++pidx, dataSet.getString("userId"));		// 예약자 아이디
				pstmt.setString(++pidx, dataSet.getString("co_nm"));		// 예약자 이름
				pstmt.setString(++pidx, dataSet.getString("email"));		// 이메일
				pstmt.setString(++pidx, dataSet.getString("hp_ddd_no"));	// 휴대폰
				pstmt.setString(++pidx, dataSet.getString("hp_tel_hno"));	// 휴대폰
				pstmt.setString(++pidx, dataSet.getString("hp_tel_sno"));	// 휴대폰
				pstmt.setString(++pidx, dataSet.getString("teof_date"));	// 신청일자
				pstmt.setString(++pidx, dataSet.getString("green_nm"));		// 골프장 명
				
				pstmt.setString(++pidx, dataSet.getString("golf_mgz_dlv_pl_clss"));		// 골프장 명			
				
				//pstmt.setInt(++pidx, dataSet.getInt("arrCnt"));		// 승차인원수
				pstmt.setString(++pidx, dataSet.getString("trAllValue"));	// 메모 또는 특이사항,요구사항,신청자명단
				res = pstmt.executeUpdate();
				if(res>0) {
					result.addString("RESULT","00");
					result.addString("pgrs_yn", pgrs_yn);		// 결과 팝업창 분기
					con.commit();
				}			
				else {
					result.addString("RESULT","01");
					con.rollback();
				}
				 
			}
			else {
				result.addString("RESULT","01");
			}
			
			
			
			

		} catch(Exception e){
			// 트랜젝션 상태일때는 롤백			
			try { if( !con.getAutoCommit() ){ con.rollback(); } else {} } catch(Throwable ignore) {}			
			
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		return result;
	}	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getCheckQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT NVL(sum(RIDG_PERS_NUM),0) as cnt FROM BCDBA.TBGAPLCMGMT		 					");
		sql.append("\n	WHERE GOLF_SVC_APLC_CLSS = '9002' AND TEOF_DATE = ? AND PGRS_YN='B' 	");	
		return sql.toString();
	}	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getInsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT	(							");
		sql.append("\n		APLC_SEQ_NO ,	 		");
		sql.append("\n		GOLF_SVC_APLC_CLSS ,	");
		sql.append("\n		NUM_DDUC_YN,  			");
		sql.append("\n		PRZ_WIN_YN, 			");
		
		sql.append("\n		PGRS_YN,  				");
		sql.append("\n		CDHD_ID,  				");
		sql.append("\n		CO_NM,  				");
		sql.append("\n		EMAIL,  				");
		sql.append("\n		HP_DDD_NO,  			");
		sql.append("\n		HP_TEL_HNO,				");
		sql.append("\n		HP_TEL_SNO,				");
		sql.append("\n		TEOF_DATE, 				");
		sql.append("\n		TEOF_TIME, 				");
		sql.append("\n		GREEN_NM,  				");
		sql.append("\n		RIDG_PERS_NUM, 			");
		sql.append("\n		MEMO_EXPL, 				");
		sql.append("\n		REG_ATON 				");
		
		sql.append("\n	) VALUES (					");
		sql.append("\n		?, ?, 'N', 'N',  ");
		sql.append("\n		?, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'hh24MISS'), ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDhh24MISS')  ");
		sql.append("\n	)	");
		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private long getMaxQuery(Connection con) throws BaseException {

		PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT";
        long pidx = 0;
        try {        	
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}
        } catch (Throwable t) {          // SQLException 시 예외 처리 : 쿼리에 문제가 있을때 발생
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
            try { if ( rset1  != null ) { rset1.close(); } else { ; }  } catch ( Throwable ignored) {}
            try { if ( pstmt1 != null ) { pstmt1.close(); } else { ; } } catch ( Throwable ignored) {}
            try { if ( con    != null ) { con.close(); } else { ; }    } catch ( Throwable ignored) {}
        }
        return pidx;

	}
}
