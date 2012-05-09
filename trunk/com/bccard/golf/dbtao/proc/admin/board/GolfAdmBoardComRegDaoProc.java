/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComRegDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 게시판 등록 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;

import java.net.InetAddress;
import com.bccard.golf.common.AppConfig;
/** ****************************************************************************
 * Media4th / Golf
 * @author 
 * @version 2009-03-31 
 **************************************************************************** */
public class GolfAdmBoardComRegDaoProc extends AbstractProc {

	public static final String TITLE = "게시판 등록 처리";
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보 
	************************************************************************ */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException  {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int result = 0;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no		= (String)userEtt.getMemId(); 			
			}

			String bbrd_CLSS				= dataSet.getString("BBRD_CLSS");
			String golf_BOKG_FAQ_CLSS		= dataSet.getString("GOLF_BOKG_FAQ_CLSS");
			String titl						= dataSet.getString("TITL");
			String ctnt						= dataSet.getString("CTNT");
			String eps_YN					= dataSet.getString("EPS_YN");
			String annx_FILE_NM				= dataSet.getString("ANNX_FILE_NM");
			String reg_MGR_ID				= admin_no;
			String reg_IP_ADDR				= request.getRemoteAddr();
			String golf_clm_clss			= dataSet.getString("golf_clm_clss");
			
			int res = 0;	
			long maxValue = this.selectArticleNo(context);
			con.setAutoCommit(false);
			
			String sql = this.getSelectQuery("");
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
							
			pstmt.setLong(++pidx, maxValue);
			pstmt.setString(++pidx, bbrd_CLSS);
			pstmt.setString(++pidx, golf_BOKG_FAQ_CLSS);
			pstmt.setString(++pidx, titl);
			pstmt.setString(++pidx, eps_YN);
			pstmt.setString(++pidx, annx_FILE_NM);
			pstmt.setString(++pidx, reg_MGR_ID);
			pstmt.setString(++pidx, reg_IP_ADDR);
			pstmt.setString(++pidx, golf_clm_clss);

			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();


			String serverip = InetAddress.getLocalHost().getHostAddress();	// 서버아이피
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");	   // 개발기 ip 정보
			
            /**clob 처리*********************************************************************/
			if (ctnt.length() > 0){
			
				sql = this.getSelectForUpdateQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(1, maxValue);
				rs = pstmt.executeQuery();
	
				if(rs.next()) {
					java.sql.Clob clob = rs.getClob("CTNT");
					
					if(serverip.equals(devip)){
						writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
					}else{
//						writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
					}
					reader = new CharArrayReader(ctnt.toCharArray());
					
					char[] buffer = new char[1024];
					int read = 0;
					while ((read = reader.read(buffer,0,1024)) != -1) {
						writer.write(buffer,0,read);
					}
					writer.flush();
				}
				if (rs  != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
		

			
			if(result > 0) {
				con.commit();
			} else {
				con.rollback();
			}

		}catch ( Exception e ) {
			
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
				
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	INSERT INTO BCDBA.TBGBBRD				");
		sql.append("\n	(BBRD_SEQ_NO,							");	// sequence (1)
		sql.append("\n	BBRD_CLSS,								");	// 게시판고유일련번호
		sql.append("\n	GOLF_BOKG_FAQ_CLSS,						");	// 분류코드 : 부킹FAQ에서사용하는분류	0001:프리미엄부킹 0002:파3부킹 0003:제주그린피할인 0004:Sky72드림듄스 0005:주중그린피할인
		sql.append("\n	TITL,									");	// 제목
		sql.append("\n	CTNT,									");	// 내용
		
		sql.append("\n	EPS_YN,									");	// 노출여부 : Y: 노출 N: 비노출
		sql.append("\n	ANNX_FILE_NM,							");	// 첨부파일
		sql.append("\n	REG_MGR_ID,								");	// 등록관리자일련번호
		sql.append("\n	REG_ATON,								");	// 등록일시
		sql.append("\n	REG_IP_ADDR,							");	// 등록IP주소
		sql.append("\n	INQR_NUM, GOLF_CLM_CLSS					");	// 조회수
		sql.append("\n	)										");	
		
								
		sql.append("\n	VALUES(?,?,?,?,EMPTY_CLOB(),								");
		sql.append("\n	?, ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDD'), ?,0, ?	");
		sql.append("\n	)										");

		return sql.toString();
	}
	/** ***********************************************************************
	 * 게시판번호 가져오기
	************************************************************************ */
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select nvl(max(BBRD_SEQ_NO),'0')+1 as BBRD_SEQ_NO from BCDBA.TBGBBRD";
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}	
			
        } catch (Throwable t) {          // SQLException 시 예외 처리 : 쿼리에 문제가 있을때 발생
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
            try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return pidx;

	}

    
	/** ***********************************************************************
    * CLOB Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectForUpdateQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CTNT FROM BCDBA.TBGBBRD \n");
        sql.append("WHERE BBRD_SEQ_NO = ? \n");
        sql.append("FOR UPDATE \n");
		return sql.toString();
    }
	
}
