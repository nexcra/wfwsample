/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthPassUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 비밀번호 수정 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-06 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.initech.dbprotector.CipherClient; //DB 암호화 관련
import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.JoinConEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthPassUpdDaoProc extends AbstractProc {
	
	public static final String TITLE = "관리자  비밀번호  수정 처리";
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result = null;
		Connection con = null;

		
		try{
			JoinConEtt cEtt = new JoinConEtt();
			
			// WHERE절 조회 조건
			String p_passwd		= dataSet.getString("p_passwd"); 		//비밀번호
			String p_idx		= dataSet.getString("p_idx"); 
			
			
			
			
			//UPDATE 
			StringBuffer sql = new StringBuffer();	
			sql.append("\n").append(" update TBGMGRINFO		 set ");
			//sql.append("\n").append(" HASH_PASWD = ? , ");	
			sql.append("\n").append(" PASWD = ? ");			
			sql.append("\n").append(" where MGR_ID = ? ");			
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
		
			int spidx = 0;

			/*//실서버 사용시 적용
			cEtt.setLogin_passwd(p_passwd.trim()); 			
			String hashpass = cEtt.getLogin_passwd();
			byte[] encData = CipherClient.encrypt(CipherClient.MASTERKEY1,hashpass.getBytes());
			pstmt.setBytes(++spidx, encData);
			*/
			
			
			pstmt.setString(++spidx, p_passwd);
			pstmt.setString(++spidx, p_idx);			
									
			int res = pstmt.executeUpdate();
			
			result = new DbTaoResult(TITLE);
			
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
		}catch ( Exception e ) {
			
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	
}
