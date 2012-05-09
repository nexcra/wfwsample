/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmSLessonRegDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 BC Golf 이벤트 
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;


public class GolfAdmSLessonRegDaoProc extends AbstractProc {
	
public static final String TITLE = "게시판 등록 처리";
	
	/** ***********************************************************************
	* Proc 실행. 
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet, Map paramMap) throws DbTaoException  {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int result = 0;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		
		debug("==========================  dao 1 ==============================");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no		= (String)userEtt.getMemId(); 			
			}
			
			String evnt_NM = dataSet.getString("EVNT_NM");
            String evnt_ST = dataSet.getString("EVNT_ST");
            String evnt_EN = dataSet.getString("EVNT_EN");
            String les_ST = dataSet.getString("LES_ST");
            String les_EN = dataSet.getString("LES_EN");
            String les_PAY_NOR = dataSet.getString("LES_PAY_NOR");
            String les_PAY_DC = dataSet.getString("LES_PAY_DC");
            String img_NM = dataSet.getString("IMG_NM");
            String mo_PE = dataSet.getString("MO_PE");
            String ctnt = dataSet.getString("CTNT");
            String disp_YN = dataSet.getString("DISP_YN");
            String evnt_BNF = dataSet.getString("EVNT_BNF");
            String affi_FIRM = dataSet.getString("AFFI_FIRM");
            String evnt_CLSS = "0003";
          
			
			long maxValue = this.selectArticleNo(context);
			con.setAutoCommit(false);
			
			String sql = this.getSelectQuery("");
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
							
			 pstmt.setLong(++pidx, maxValue);
	            pstmt.setString(++pidx, evnt_CLSS);
	            pstmt.setString(++pidx, evnt_NM);
	            pstmt.setString(++pidx, evnt_ST);
	            pstmt.setString(++pidx, evnt_EN);
	            pstmt.setString(++pidx, disp_YN);
	            pstmt.setString(++pidx, mo_PE);
	            pstmt.setString(++pidx, les_ST);
	            pstmt.setString(++pidx, les_EN);
	            pstmt.setString(++pidx, les_PAY_NOR);
	            pstmt.setString(++pidx, les_PAY_DC);
	            pstmt.setString(++pidx, evnt_BNF);
	            pstmt.setString(++pidx, affi_FIRM);
	            pstmt.setString(++pidx, img_NM);

			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			

            /**clob 처리*********************************************************************/
			if (ctnt.length() > 0){
			
				sql = this.getSelectForUpdateQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(1, maxValue);
				rs = pstmt.executeQuery();
	
//				if(rs.next()) {
//					java.sql.Clob clob = rs.getClob("CTNT");
//					writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//					reader = new CharArrayReader(ctnt.toCharArray());
//					
//					char[] buffer = new char[1024];
//					int read = 0;
//					while ((read = reader.read(buffer,0,1024)) != -1) {
//						writer.write(buffer,0,read);
//					}
//					writer.flush();
//				}
				if (rs  != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
		

			 if(result > 0){
                con.commit();
                paramMap.put("RESULTOK", "00");
	         } else{
                con.rollback();
                paramMap.put("RESULTOK", "01");
	         }
			
			//debug("==== GolfAdmBoardComRegDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComRegDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardComRegDaoProc ERROR ===");
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


		
		sql.append("\n	INSERT INTO BCDBA.TBGEVNTMGMT(   											");
        sql.append("\n		EVNT_SEQ_NO,															");
        sql.append("\n		EVNT_CLSS,																");
        sql.append("\n		EVNT_NM,																");													
        sql.append("\n		EVNT_STRT_DATE,															");
        sql.append("\n		EVNT_END_DATE,															");
        sql.append("\n		CTNT,																	");
        sql.append("\n		BLTN_YN,																");
        sql.append("\n		RCRU_PE_ORG_NUM,														");
        sql.append("\n		LESN_STRT_DATE,															");
        sql.append("\n		LESN_END_DATE,															");
        sql.append("\n		LESN_NORM_COST,															");
        sql.append("\n		LESN_DC_COST,															");
        sql.append("\n		EVNT_BNFT_EXPL,															");
        sql.append("\n		AFFI_FIRM_EXPL,															");
        sql.append("\n		REG_ATON,																");
        sql.append("\n		INQR_NUM,																");
        sql.append("\n		IMG_FILE_PATH															");
        sql.append("\n	)VALUES(																	");
        sql.append("\n		?,?,?,?,?,EMPTY_CLOB(),													");
        sql.append("\n		?, ?, ?, ?, ?, 															");
        sql.append("\n		?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),0, ?	");
        sql.append("\n	)																			");
		return sql.toString();
	}
	/** ***********************************************************************
	 * 게시판번호 가져오기
	************************************************************************ */
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "SELECT NVL(MAX(EVNT_SEQ_NO),'0')+1 AS EVNT_SEQ_NO FROM BCDBA.TBGEVNTMGMT";
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
        sql.append("\n 	SELECT CTNT FROM BCDBA.TBGEVNTMGMT						");
        sql.append("\n 	WHERE EVNT_SEQ_NO = ? 									");
        sql.append("\n	 FOR UPDATE 											");
		return sql.toString();
    }


}