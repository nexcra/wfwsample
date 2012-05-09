/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMojibProc
*   작성자    : E4NET 은장선
*   내용      : 메인 비지니스 로직
*   적용범위  : Golf
*   작성일자  : 2009-09-03  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.security.cryptography.Base64Encoder;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
 
/** *****************************************************************
 * GolfAdmMojibProc 프로세스 생성자
 * @param N/A
 ***************************************************************** */
public class GolfAdmMojibEzUpdProc extends AbstractProc {
		
	//private static final String TITLE = "이지웰 수정";		
	
	/** *****************************************************************
	 * GolfAdmMojibProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMojibEzUpdProc() {}
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	
	/*수정*/
	public DbTaoResult getPay(WaContext context, TaoDataSet dataSet) throws BaseException {		

		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		//int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);
		ResultSet rs = null;
			
		try{
			con = context.getDbConnection("default", null);	

			String jumin_no		= dataSet.getString("jumin_no");

			String aspOrderNum 	= "";	// 주문번호 (제휴사측) -> 골프라운지 주문번호
			String orderNum 	= "";	// 주문번호 (이지웰측)
			String cspCd		= "golfrounge";	// 제휴사(CP) 업체코드 
			String command		= "102";	// 명령어 종류
			
			String enc_aspOrderNum = "";
			String enc_orderNum = "";
			String enc_cspCd = "";
			String enc_command = "";
			
			   
			String 	sStrSql = this.getPaySql();
			pstmt = con.prepareStatement(sStrSql);
			pstmt.setString(1, jumin_no);
			rs = pstmt.executeQuery(); 
			if (rs.next())	{		
				aspOrderNum	= rs.getString("AUTH_NO");
				orderNum	= rs.getString("CARD_NO");
			}

			if(!GolfUtil.empty(aspOrderNum))	enc_aspOrderNum	= new String(Base64Encoder.encode(aspOrderNum.getBytes()));
			if(!GolfUtil.empty(orderNum))		enc_orderNum 	= new String(Base64Encoder.encode(orderNum.getBytes()));
			if(!GolfUtil.empty(cspCd))			enc_cspCd 		= new String(Base64Encoder.encode(cspCd.getBytes()));
			if(!GolfUtil.empty(command))		enc_command 	= new String(Base64Encoder.encode(command.getBytes()));
			
			
			result.addString("dec_aspOrderNum"	, aspOrderNum);
			result.addString("dec_orderNum"		, orderNum);
			result.addString("dec_cspCd"		, cspCd);
			result.addString("dec_command"		, command);
			
			result.addString("aspOrderNum"		, enc_aspOrderNum);
			result.addString("orderNum"			, enc_orderNum);
			result.addString("cspCd"			, enc_cspCd);
			result.addString("command"			, enc_command);
			
//			debug("aspOrderNum : " + enc_aspOrderNum + " / orderNum : " + enc_orderNum + " / cspCd : " + enc_cspCd + " / command : " + enc_command);
//			debug("aspOrderNum : " + aspOrderNum + " / orderNum : " + orderNum + " / cspCd : " + cspCd + " / command : " + command);
			
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {	
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}




	/* 결제테이블 내역 가져오기 */
	public String getPaySql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT AUTH_NO, CARD_NO FROM  BCDBA.TBGLUGANLFEECTNT WHERE JUMIN_NO=? AND MB_CDHD_NO='ezwel'	");
		return sb.toString();
	}


}


