/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0  
******************************************************************************/
public class GolfMemPresentViewDaoProc extends AbstractProc {

	public static final String TITLE = "회원가입처리";

	public GolfMemPresentViewDaoProc() {}
	
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId = userEtt.getAccount();

			conn = context.getDbConnection("default", null);
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, memId);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("PRE_NAME" 		,rs.getString("PRE_NAME") );
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	public DbTaoResult execute_mem(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			String cpn_code = data.getString("cpn_code");
			String vipCardPayAmt = data.getString("vipCardPayAmt");
			debug("## 회원등급 가져오기 execute_mem | vipCardPayAmt : "+vipCardPayAmt);
			
			conn = context.getDbConnection("default", null);
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectMemQuery(cpn_code);   

			// 입력값 (INPUT)        
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GRD_SEQ" 			,rs.getString("GRD_SEQ") );
					
					if (rs.getString("GRD_SEQ").equals("27")){//27 : Smart1000->월정액1000원
						result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
						result.addString("GRD_NM2" 			, rs.getString("GRD_NM")+"(월정액1,000원)");
					}else {
						result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
					}
					
					result.addString("GRD_SQ2" 			,rs.getString("GRD_SQ2") );
					
					if(!"0".equals(vipCardPayAmt))
					{
						if("Gold".equals(rs.getString("GRD_NM")))
						{
							result.addString("ANL_FEE" 			,GolfUtil.comma("15000") );
						}
						else
						{
							if (rs.getString("GRD_SEQ").equals(AppConfig.getDataCodeProp("0052CODE11"))){//27 : Smart1000->월정액1000원								
								result.addString("ANL_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
							}else{								
								result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
							}
						}
					}else if (rs.getString("GRD_SEQ").equals(AppConfig.getDataCodeProp("0052CODE11"))){//27 : Smart1000->월정액1000원						
						result.addString("ANL_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
					}else{						
						result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
					}													
					
					result.addString("MO_MSHP_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
					result.addString("RESULT", "00"); //정상결과 
				}
			
			}
			
			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	// 회원기간 연장하기에서 회원정보 가져오는 함수
	public DbTaoResult execute_extend(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			// 01. 세션정보체크
			String ctgo_seq = data.getString("ctgo_seq");

			conn = context.getDbConnection("default", null);
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectMemExtendQuery();   

			// 입력값 (INPUT)  
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ctgo_seq);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GRD_SEQ" 			,rs.getString("GRD_SEQ") );
					result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
					result.addString("GRD_SQ2" 			,rs.getString("GRD_SQ2") );
					result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
					result.addString("MO_MSHP_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
					result.addString("RESULT", "00"); //정상결과 
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
			
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */ 
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n");
		sql.append("\n SELECT GOLF_CMMN_CODE_NM AS PRE_NAME 	");
		sql.append("\n FROM BCDBA.TBGCDHDRIKMGMT T1	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE T2 ON T1.GOLF_TMNL_GDS_CODE=T2.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0044'	");
		sql.append("\n WHERE CDHD_ID=?	");
		
		return sql.toString();
    }
			
	/** ***********************************************************************
    * 유료회원제 리스트를 가져온다.   
    * 멤버쉽회원 리스트에 카드회원 추가(개인결제 월회원 Smart1000) - 20110621
    ************************************************************************ */ 
    private String getSelectMemQuery(String cpn_code){
        StringBuffer sql = new StringBuffer();

        sql.append("\n");
		sql.append("\t SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2, EXPL	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
		sql.append("\t JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t WHERE ( CTGO.USE_CLSS='Y' AND ANL_FEE>0 OR CTGO.CDHD_CTGO_SEQ_NO='27')			\n"); //27 : Smart1000->월정액1000원
		
		// e-champ 회원 가입시에만 할인이 적용되는 쿠폰
		if("EVENTECHAMP201007".equals(cpn_code.toString().toUpperCase()) || "EVENTLETTER08".equals(cpn_code.toString().toUpperCase()) || "EVENTTHEBC08".equals(cpn_code.toString().toUpperCase())){
			sql.append("\t AND CTGO.CDHD_CTGO_SEQ_NO='17'	\n");
		}
		
		sql.append("\t ORDER BY CTGO.SORT_SEQ ASC	\n");
		
		return sql.toString();
    }
			
	/** ***********************************************************************
    * 유료회원제 리스트를 가져온다.    
    ************************************************************************ */ 
    private String getSelectMemExtendQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n");
		sql.append("\t SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
		sql.append("\t JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t WHERE CTGO.USE_CLSS='Y' AND CTGO.CDHD_CTGO_SEQ_NO=? 	\n");
		sql.append("\t ORDER BY CTGO.SORT_SEQ ASC	\n");
		
		return sql.toString();
    }
    
}
