/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthUserInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : Golf 운영자 관리 입력 Proc
*   적용범위  : Golf
*   작성일자  : 2009-05-06 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.*;
import javax.servlet.http.*;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.GolfBbsConEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.initech.dbprotector.CipherClient; //DB 암호화 관련
import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.JoinConEtt;

public class GolfAdmAuthUserInsDaoProc extends AbstractProc {

	public static final String TITLE = "Golf 운영자 관리 입력";
	/** *****************************************************************
	 * GolfPointMainInsDaoProc 프로세스 생성자
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmAuthUserInsDaoProc() { 

	}
	
	public int execute(WaContext context,  TaoDataSet data) throws DbTaoException  {
		String returnCode = "0";

		GolfBbsConEtt tEtt = new GolfBbsConEtt();

		int result = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		StringBuffer sqlstr = new StringBuffer();
		DbTaoResult taoResult = null;

		try{
			String id_check_yn = "00";
			// id_check_yn : 00 이면 중복되는 아이디임
			
			GolfAdmLoginlnqDaoProc proc = (GolfAdmLoginlnqDaoProc)context.getProc("GolfAdmLoginlnqDaoProc");
			taoResult = (DbTaoResult)proc.execute(context, data);	// 관리자 조회
			
			if(taoResult != null && taoResult.isNext() ) {
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"등록된 아이디가 있습니다.");
				//throw new DbTaoException(msgEtt);
				taoResult.next();
				//debug("RESULT : "+taoResult.getString("RESULT"));
				id_check_yn = taoResult.getString("RESULT");				
				result = -1;
			} else {
				id_check_yn = "01";
			}
			
			if("01".equals(id_check_yn))
			{
				JoinConEtt cEtt = new JoinConEtt();
				// 해시 비밀번호 설정
				/*
				String p_passwd		= data.getString("passwd").trim(); 		//비밀번호
				cEtt.setLogin_passwd(p_passwd.trim()); 				
				String hashpass = cEtt.getLogin_passwd();
				
				byte[] encData = CipherClient.encrypt(CipherClient.MASTERKEY1,hashpass.getBytes());
				*/

				
				int i=0;
				sqlstr.append("insert into BCDBA.TBGMGRINFO							\n");
				sqlstr.append("\t (MGR_ID, PASWD, HG_NM, FIRM_NM, RSV_NM			\n");
				sqlstr.append("\t , ZP, ADDR, DTL_ADDR, EMAIL						\n");
				sqlstr.append("\t , DDD_NO, TEL_HNO, TEL_SNO						\n");
				sqlstr.append("\t , FAX_DDD_NO, FAX_TEL_HNO, FAX_TEL_SNO			\n");
				sqlstr.append("\t , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO	 			\n");
				sqlstr.append("\t , MEMO_CTNT, REG_DATE, INDV_INFO_RPES_NM, JUMIN_NO)	\n");
				sqlstr.append("\t  VALUES											\n");
				sqlstr.append("\t (?, ?, ?, ?, ?									\n");
				sqlstr.append("\t , ?, ?, ?, ?										\n");
				sqlstr.append("\t , ?, ?, ?											\n");
				sqlstr.append("\t , ?, ?, ?											\n");
				sqlstr.append("\t , ?, ?, ?											\n");
				sqlstr.append("\t , ?, to_char(sysdate,'yyyymmdd'), ?, ?)				\n");
				//sqlstr.append("\t ?,to_char(sysdate,'yyyymmdd'),to_char(sysdate,'hh24miss'))");

				//debug("===== 쿼리 : " + sqlstr.toString());

				con = context.getDbConnection("default", null);
				con.setAutoCommit(false); 

				pstmt = con.prepareStatement(sqlstr.toString());
				pstmt.setString(++i, data.getString("account").trim());		// 아이디
				pstmt.setString(++i, data.getString("passwd").trim());		// 암호				
				pstmt.setString(++i, data.getString("name").trim());		// 성명
				pstmt.setString(++i, data.getString("com_nm").trim());		// 업체명
				pstmt.setString(++i, data.getString("prs_nm").trim());		// 대표자명
				
				pstmt.setString(++i, data.getString("zipcode1")+data.getString("zipcode2"));		// 우편번호
				pstmt.setString(++i, data.getString("zipaddr").trim());		// 주소1
				pstmt.setString(++i, data.getString("detailaddr").trim());	// 주소2
				pstmt.setString(++i, data.getString("email").trim());		// 이메일
				
				pstmt.setString(++i, data.getString("tel1").trim());		// 전화1
				pstmt.setString(++i, data.getString("tel2").trim());		// 전화2
				pstmt.setString(++i, data.getString("tel3").trim());		// 전화3
				
				pstmt.setString(++i, data.getString("fax1").trim());		// 팩스1
				pstmt.setString(++i, data.getString("fax2").trim());		// 팩스2
				pstmt.setString(++i, data.getString("fax3").trim());		// 팩스3
				
				pstmt.setString(++i, data.getString("hp_tel_no1").trim());	// 핸드폰1
				pstmt.setString(++i, data.getString("hp_tel_no2").trim());	// 핸드폰2
				pstmt.setString(++i, data.getString("hp_tel_no3").trim());	// 핸드폰3
				
				pstmt.setString(++i, data.getString("memo").trim());		// 메모내용
				pstmt.setString(++i, data.getString("INDV_INFO_RPES_NM").trim());		// 메모내용
				pstmt.setString(++i, data.getString("jumin_no").trim());		// 메모내용
				//pstmt.setBytes(++i, encData);								// 암호	
				
				//쿼리 실행
				result = pstmt.executeUpdate();
	
				if(result > 0) {
					con.commit();
					debug("===== INSERT 성공 | ");
				} else {
					con.rollback();
					debug("===== INSERT 실패 | ");
				}
			}
		}catch(Exception e){
			try	{
				con.rollback();
			}catch (Exception c){}
			debug("===== INSERT 실패 | " + e + " ===========");
			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new DbTaoException(msgEtt,e);
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(con != null) try{ con.close(); }catch(Exception ex1){}
		}
		return result; 
	}
}
