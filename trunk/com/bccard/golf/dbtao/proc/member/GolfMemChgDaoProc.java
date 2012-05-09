/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeRsViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 결과 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.member;

import java.sql.*;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;

import com.bccard.golf.common.ResultException;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;

import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.jolt.JtProcess;

import com.bccard.golf.common.SSOdbprotectorWrap;
import com.bccard.golf.jolt.jtproc.UHL017_Ind_RetProc;
import com.bccard.golf.action.member.MemberChangeConEtt;
/*************************************************************************
* 이벤트별 당첨자 전체 조회
* @version 2005.11.09
* @author 정준영
**************************************************************************/
public class GolfMemChgDaoProc extends AbstractProc {
	//private final String TSN_UJW001  = "UJW001_CHK";		    //개인회원인증
	private final String TSN_UHL017  = "UHL017_Ind_Ret";		//개인회원인증
	private final String MHL0010I0100 = "MHL0010I0100";		//개인회원인증 

	/*************************************************************************
	* 이벤트별 당첨자 전체 조회
	* @version 2005.11.09
	* @author 정준영
	**************************************************************************/
	private ResultException getResultException(String msgKey, String[] args, Throwable t) {
		ResultException exception;
		if ( args == null ) {
			if ( t == null ) {
				exception = new ResultException(msgKey);
			} else {
				exception = new ResultException(msgKey,t);
			}
		} else {
			if ( t == null ) {
				exception = new ResultException(msgKey,args);
			} else {
				exception = new ResultException(msgKey,args,t);
			}
		}
		return exception;
	}



	/*********************************************************************
	* 정회원전환 Jolt 통신
	***********************************************************************/
	public void approval(WaContext context, HttpServletRequest request, MemberChangeConEtt ett) throws BaseException {

		String joltServiceXAName = "BSXINPT";
		String joltFmlTrCode = "MHL0170I0100";
		
		String socid =  ett.getSocid();
		String fml_arg1 = socid;
		String fml_arg2 = ett.getCardNo();
		String fml_arg3 = ett.getCardPass();
		String fml_arg4 = ett.getTerm();
		String fml_arg5 = "";
		String fml_arg6 = ett.getCvc();

		try {

			// 전문 셋팅값 설정
			JoltInput entity = new JoltInput(joltServiceXAName);
			entity.setServiceName(joltServiceXAName);
			entity.setString("fml_trcode", joltFmlTrCode);
			entity.setString("fml_arg1", fml_arg1);
			entity.setString("fml_arg2", fml_arg2 );
			entity.setString("fml_arg3", fml_arg3 );
			entity.setString("fml_arg4", fml_arg4 );
			entity.setString("fml_arg6", fml_arg6 );
			debug(" UHL017_Ind_RetProc entity= ["+entity+"]");			

			// 정회원전환 Jolt 설정
			JtTransactionProc pgProc = null;
     		pgProc = (JtTransactionProc)context.getProc("UHL017_Ind_Ret");
     		debug("GOLF_JAE START:" + fml_arg1 + "|" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5  );
			info("GOLF_JAE START:" + fml_arg1 + "|" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5  );

			// 전문 결과 조회
			Properties properties = new Properties();
			properties.setProperty("RETURN_CODE", "fml_ret1");
			properties.setProperty("SOC_ID", socid);
			
			JoltOutput output = pgProc.execute(context, request, entity, properties);
			debug("=======approval============output : " + output);

			String errorCode = pgProc.getErrorCode(entity, output);
			debug("=======approval============errorCode : " + errorCode);

//			if ( !"01".equals(errorCode) ) {
//				throw getResultException("UHL017_Ind_Ret_" + errorCode, new String[]{}, null);
//			} else {
//				//con.commit();
//			}

			ett.setMemberName( output.getString("fml_ret3") );

		} catch(ResultException re) {
			throw re;
		} catch(Throwable t) {
			throw getResultException("MemberChangeProc_1100",new String[]{},t);
		} finally {

		}

	}

	/*********************************************************************
	* 정회원 전환 디비 수정
	***********************************************************************/
	public void executMemberChange(WaContext context, MemberChangeConEtt ett) throws BaseException {

		StringBuffer sql = new StringBuffer();
		sql.append("\n SELECT ACCOUNT,SITE_CLSS,MEMID,SOCID,NAME FROM BCDBA.UCUSRINFO " );
		sql.append("\n WHERE SOCID = ? AND MEMBER_CLSS = '4' AND SITE_CLSS = '2' ");

		Connection con = null;
		try {
			con = context.getDbConnection("default",null);
		} catch (Throwable t) {
			throw getResultException("MemberChangeProc_9004",new String[]{"데이터베이스 연결 실패"},t);
		}
		PreparedStatement pstmt1 = null;
		ResultSet rset1 = null;
		boolean isResult = false;

		String result = "";

		try {

			memberCheckSocid(con, ett); // 주민번호 중복 체크
			pstmt1 = con.prepareStatement( sql.toString() );
			pstmt1.setString(1, ett.getSocid() );
			rset1 = pstmt1.executeQuery();

			if( rset1.next() ) {

				ett.setAccount ( rset1.getString("ACCOUNT"));
				ett.setSiteClss( rset1.getString("SITE_CLSS"));
				ett.setMemid   ( rset1.getString("MEMID") );
				ett.setSocid   ( rset1.getString("SOCID") );
				ett.setName    ( rset1.getString("NAME"));
				
				debug("========executMemberChange======== ett.getName() : " + ett.getName());
				debug("========executMemberChange======== ett.getMemberName() : " + ett.getMemberName());

				// 회원가입된 이름과 카드등록된 이름과 같은가 ?
				if ( ett.getName().trim().equals(ett.getMemberName().trim()) ) {

					result = updateMember(con,ett);

					if ("1".equals(result)) {
						isResult = true;
					} else if ("2".equals(result)) {
						throw getResultException("MemberChangeProc_9004",new String[]{"회원 전환 처리중 데이터베이스 오류"}, null);
					}

				} else {
					throw getResultException("MemberChangeProc_1005",new String[]{},null);  // 등록이름이 서로 다를때
				}
			}

			if ( ! isResult ) {
				String accountType = getAccountType(con,ett);
				debug("========executMemberChange======== accountType : " + accountType);
				if ( "00".equals( accountType) ) {
					throw getResultException("MemberChangeProc_10010",new String[]{},null); // 등록회원 부재
				} else if ( "01".equals( accountType) ) {
					throw getResultException("MemberChangeProc_10020",new String[]{},null); // 카드회원만 등록
				} else if ( "10".equals( accountType) ) {
					throw getResultException("MemberChangeProc_10030",new String[]{},null); // 공통회원으로 등록
				} else if ( "11".equals( accountType) ) {
					throw getResultException("MemberChangeProc_10040",new String[]{},null); // 카드, 공통회원으로 등록
				} else {
					throw getResultException("MemberChangeProc_9004",new String[]{"알수 없는 오류"},null); // 기타
				}
			}

		} catch(SQLException se) {
			throw getResultException("MemberChangeProc_9004",new String[]{"회원 정보 조회중 데이터베이스 오류"},se);
		} finally {
			try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
			try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
			try { if ( con    != null ) con.close();	} catch ( Throwable ignored) {}
		}

		throw getResultException("MemberChangeProc_1000", new String[]{},null);    // 성공
	}

	//	memberCheckSocid 	: 주민번호 중복체크
	//	luckyBcExit			: 럭키BC탈회 실행
	/*************************************************************************
	* 회원가입관련
	* @version 2005.11.09
	* @author 정준영
	**************************************************************************/
	protected void memberCheckSocid(Connection con, MemberChangeConEtt ett) throws BaseException {
		boolean rtnVal = false;
		String selectsql = "\n SELECT COUNT(*) AS CNT FROM BCDBA.UCUSRINFO WHERE SOCID = ?  ";

		PreparedStatement pstmt1 = null;
		ResultSet rset1 = null;
		try {
			pstmt1 = con.prepareStatement( selectsql );
			pstmt1.setString(1, ett.getSocid() );
			rset1 = pstmt1.executeQuery();
			if( rset1.next() ) {
				if ( rset1.getInt("CNT") > 1 ) rtnVal = true;
			}
		} catch(SQLException se) {
			throw getResultException("MemberChangeProc_9004",new String[]{"주민등록번호 중복 확인중 데이터베이스 오류"},se);
		} finally {
			try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
			try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
		}
		if ( rtnVal == true ) throw getResultException("MemberChangeProc_1002",new String[]{},null);        // 주민번호 중복
	}



	//  회원 " update set site_clss = '0' , check_name = '1' where memid = ? "
	/*************************************************************************
	* 회원가입관련
	* @version 2005.11.09
	* @author 정준영
	**************************************************************************/
	protected String updateMember(Connection con, MemberChangeConEtt ett) throws BaseException, SQLException {

		String select_sql = " SELECT COUNT(*) AS CNT FROM BCDBA.UCUSRINFO WHERE ACCOUNT = ? ";

		String update_sql = " UPDATE BCDBA.UCUSRINFO SET SITE_CLSS = '0', CHKNAME ='1', MEMBER_CLSS='1' WHERE ACCOUNT = ? ";

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rset1 = null;

		// 리턴값
		String result = "1";

		try {

			con.setAutoCommit(false);
			int rtnCnt = 0;
			pstmt1 = con.prepareStatement( select_sql );
			pstmt1.setString(1, ett.getAccount() );
			rset1 = pstmt1.executeQuery();
			if( rset1.next() ) {
				rtnCnt = rset1.getInt("CNT");
			}
			if ( rtnCnt > 1 ) {
				throw getResultException("MemberChangeProc_10000",new String[]{},null); // 중복 회원
			}
			if ( rtnCnt <= 1 ) {
				pstmt2 = con.prepareStatement( update_sql );
				pstmt2.setString(1, ett.getAccount() );
				rtnCnt = pstmt2.executeUpdate();
			}
			if ( rtnCnt > 1 ) {
				result = "2";
				throw new SQLException("중복 수정","MemberChangeProc_10000");
			}

			// ssocheck 가 성공인 경우만 실행한다.
			SSOdbprotectorWrap sso = new SSOdbprotectorWrap();

			// 회원이 있는지 확인한다.
			boolean returnFlag = sso.existUser(ett.getAccount());

			// MEM_CLSS 를 '4' --> '1' 로 업데이트 한다.
			boolean ssocheck = sso.changeUserExFieldValue2(ett.getAccount(),"MEM_CLSS", "1");

			if (returnFlag && ssocheck) {
				con.commit();
				result = "1";
			} else {
   				con.rollback();
				result = "2";
			}


		} catch(SQLException se) {
			try { con.rollback(); } catch ( Throwable ignored) {}
			if ( "MemberChangeProc_10000".equals(StrUtil.isNull(se.getSQLState() ,"")) ) {
				throw getResultException("MemberChangeProc_10000",new String[]{},null);
			} else {
				throw getResultException("MemberChangeProc_9004",new String[]{"회원 전환 처리중 데이터베이스 오류"},se);
			}
		} catch(Throwable ignored) {

		} finally {
			try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
			try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
			try { if ( con != null )  con.setAutoCommit(true); } catch ( Throwable ignored) {}
		}

		return result;
	}



	// 회원종류 가져오기
	/*************************************************************************
	* 회원가입관련
	* @version 2005.11.09
	* @author 정준영
	**************************************************************************/
	protected String getAccountType(Connection con, MemberChangeConEtt ett) throws BaseException {
		String rtnVal = "";
		String select = " SELECT COUNT(*) AS CNT FROM BCDBA.UCUSRINFO WHERE SOCID = ? AND SITE_CLSS = ? ";

		PreparedStatement pstmt1 = null;
		ResultSet rset1 = null;
		try {
			int rtnCnt = 0;
			pstmt1 = con.prepareStatement( select );
			pstmt1.setString(1, ett.getSocid() );
			pstmt1.setString(2, "0" );
			rset1 = pstmt1.executeQuery();
			if( rset1.next() ) {
				rtnCnt = rset1.getInt("CNT");
			}
			if ( rtnCnt != 0 ) {
				rtnVal = "1";
			} else {
				rtnVal = "0";
			}

			pstmt1.setString(1, ett.getSocid() );
			pstmt1.setString(2, "1" );
			rset1 = pstmt1.executeQuery();
			if( rset1.next() ) {
				rtnCnt = rset1.getInt("CNT");
			}
			if ( rtnCnt != 0 ) {
				rtnVal += "1";
			} else {
				rtnVal += "0";
			}
		} catch(SQLException se) {
			throw getResultException("MemberChangeProc_9004",new String[]{"회원 등록 상태 확인중 데이터베이스 오류"},se);
		} finally {
			try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
			try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
		}
		return rtnVal;
	}
	
	
	// BC회원인지 체크
	/*************************************************************************
	* BC회원인지 체크
	* @version 2009.12.02
	* @author 권영만
	**************************************************************************/
	public String getBcMemYn(Connection con, String jumin) throws BaseException {
		String rtnVal = "";
		String select = " SELECT COUNT(*) AS CNT FROM UCUSRINFO WHERE SOCID = ? ";

		PreparedStatement pstmt1 = null;
		ResultSet rset1 = null;
		try {
			int rtnCnt = 0;
			pstmt1 = con.prepareStatement( select );
			pstmt1.setString(1, jumin );
			rset1 = pstmt1.executeQuery();
			if( rset1.next() ) {
				rtnCnt = rset1.getInt("CNT");
			}
						
			if ( rtnCnt != 0 ) {
				rtnVal = "Y";
			} else {
				rtnVal = "N";
			}
			System.out.print("## NhLog | getBcMemYn | BC회원인지 체크 | jumin : "+jumin+" | rtnCnt : "+rtnCnt+" | rtnVal : "+rtnVal+"\n");

		} catch(SQLException se) {
			throw getResultException("MemberChangeProc_9004",new String[]{"회원 등록 상태 확인중 데이터베이스 오류"},se);
		} finally {
			try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
			try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
		}
		return rtnVal;
	}
}

