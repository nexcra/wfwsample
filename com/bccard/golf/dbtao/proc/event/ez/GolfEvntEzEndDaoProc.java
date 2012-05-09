/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntKvpDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 월례회 > 등록처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.ez;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import java.net.InetAddress;
import com.bccard.golf.common.AppConfig;
/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0  
 ******************************************************************************/
public class GolfEvntEzEndDaoProc extends AbstractProc {
	
	public GolfEvntEzEndDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int updEvntFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int bcMemCnt = 0;	// BC 멤버 테이블 갯수

		try {
			conn = context.getDbConnection("default", null);

			// BC 회원 여부
			String aspOrderNum 	= data.getString("aspOrderNum");	// 주문번호 (제휴사측) -> 골프라운지 주문번호
			
			pstmt = conn.prepareStatement(getBcMemCnt());
			pstmt.setString(1, aspOrderNum);
			rs = pstmt.executeQuery(); 
			while (rs.next())	{		
				bcMemCnt	= rs.getInt("CNT");
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return bcMemCnt;
	}

	


	/** ***********************************************************************
    * 이벤트 정보 가져오기
    ************************************************************************ */
    private String getBcMemCnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append(" \n");
		sql.append("\t	SELECT COUNT(*) CNT \n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT APL \n");
		sql.append("\t	JOIN BCDBA.UCUSRINFO BCM ON APL.JUMIN_NO=BCM.SOCID \n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='0012' AND APLC_SEQ_NO=? \n");
		return sql.toString();
    }
}
