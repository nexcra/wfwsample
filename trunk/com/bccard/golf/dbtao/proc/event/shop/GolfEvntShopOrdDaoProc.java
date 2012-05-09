/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntShopOrdDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntShopOrdDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);

			String gds_code			= data.getString("gds_code");			// 상품코드
			String sgl_lst_itm_code	= data.getString("sgl_lst_itm_code");	// 옵션
			String qty				= data.getString("qty");				// 수량
			String realPayAmt		= data.getString("realPayAmt");			// 결제금액
			
			// 고객정보 - 구매자
			String juminno1			= data.getString("juminno1");	
			String juminno2			= data.getString("juminno2");	
			String juminno			= juminno1+juminno2;
			String userNm			= data.getString("userNm");	
			String userId			= data.getString("userId");	
			String zip_code1		= data.getString("zip_code1");	
			String zip_code2		= data.getString("zip_code2");	
			String zipaddr			= data.getString("zipaddr");	
			String detailaddr		= data.getString("detailaddr");	
			String addr_clss		= data.getString("addr_clss");
			String addr				= zip_code1+"-"+zip_code2+" "+zipaddr+" "+detailaddr;
			String mobile1			= data.getString("mobile1");	
			String mobile2			= data.getString("mobile2");	
			String mobile3			= data.getString("mobile3");	
			String hdlv_msg_ctnt	= data.getString("hdlv_msg_ctnt");	
			
			// 고객정보 - 수령자
			String dlv_userNm		= data.getString("dlv_userNm");	
			String dlv_zip_code1	= data.getString("dlv_zip_code1");	
			String dlv_zip_code2	= data.getString("dlv_zip_code2");	
			String dlv_zip_code		= dlv_zip_code1+dlv_zip_code2;
			String dlv_zipaddr		= data.getString("dlv_zipaddr");	
			String dlv_detailaddr	= data.getString("dlv_detailaddr");	
			String dlv_addr_clss	= data.getString("dlv_addr_clss");
			String dlv_mobile1		= data.getString("dlv_mobile1");
			String dlv_mobile2		= data.getString("dlv_mobile2");	
			String dlv_mobile3		= data.getString("dlv_mobile3");	
			
			if(GolfUtil.empty(dlv_userNm)) 		dlv_userNm = userNm;
			if(GolfUtil.empty(dlv_zip_code)) 	dlv_zip_code = zip_code1+zip_code2;
			if(GolfUtil.empty(dlv_zipaddr)) 	dlv_zipaddr = zipaddr;
			if(GolfUtil.empty(dlv_detailaddr)) 	dlv_detailaddr = detailaddr;
			if(GolfUtil.empty(dlv_addr_clss)) 	dlv_addr_clss = addr_clss;
			if(GolfUtil.empty(dlv_mobile1)) 	dlv_mobile1 = mobile1;
			if(GolfUtil.empty(dlv_mobile2)) 	dlv_mobile2 = mobile2;
			if(GolfUtil.empty(dlv_mobile3)) 	dlv_mobile3 = mobile3;
			
			// 결제정보
			String order_no 		= data.getString("ORDER_NO");

			String sql = this.getSelectQuery();
			
			// 입력값 (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, order_no);
			pstmt.setString(idx++, userId);
			pstmt.setString(idx++, juminno);
			pstmt.setString(idx++, qty);
			pstmt.setString(idx++, realPayAmt);
			pstmt.setString(idx++, sgl_lst_itm_code);
			pstmt.setString(idx++, realPayAmt);
			pstmt.setString(idx++, hdlv_msg_ctnt);
			pstmt.setString(idx++, addr);
			pstmt.setString(idx++, userNm);
			pstmt.setString(idx++, dlv_mobile1);
			pstmt.setString(idx++, dlv_mobile2);
			pstmt.setString(idx++, dlv_mobile3);
			pstmt.setString(idx++, dlv_zip_code);
			pstmt.setString(idx++, dlv_zipaddr);
			pstmt.setString(idx++, dlv_detailaddr);
			pstmt.setString(idx++, mobile1);
			pstmt.setString(idx++, mobile2);
			pstmt.setString(idx++, mobile3);
			pstmt.setString(idx++, dlv_userNm);
            pstmt.setString(idx++, gds_code);
			pstmt.setString(idx++, addr_clss);
			pstmt.setString(idx++, dlv_addr_clss);
			result = pstmt.executeUpdate();
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	// 결제 성공했을 경우 주문내역 업데이트
	public int execute_upd(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);

			String order_no			= data.getString("ORDER_NO");			// 주문번호
			String sql = this.getUpdQuery();   
			
			// 입력값 (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, order_no);
			result = pstmt.executeUpdate();

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/** ***********************************************************************
    * 주문정보 insert   
    ************************************************************************ */
	
    private String getSelectQuery(){
    	
        StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGLUGODRCTNT (	\n");
		sql.append("\t	ODR_NO, CDHD_ID, JUMIN_NO, ODR_ATON, ODR_DTL_CLSS, ODR_STAT_CLSS, ODR_CLSS, GDS_CLSS, ACPT_QTY, ODR_AMT, DC_RT, STTL_AMT, HDLV_MSG_CTNT, CONG_MSG_CTNT	\n");
		sql.append("\t	, HG_NM, RECEIVER_TEL1, RECEIVER_TEL2, RECEIVER_TEL3, DLV_PL_ZP, DLV_PL_DONG_OVR_ADDR, DLV_PL_DONG_BLW_ADDR, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO	\n");
		sql.append("\t	, FNL_USE_ATON, EMAIL_ID, DLV_YN, GDS_CODE, STL_CARD_NO, NW_OLD_ADDR_CLSS, ODRPN_NW_OLD_ADDR_CLSS	\n");
		sql.append("\t	) VALUES (	\n");
		sql.append("\t	?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), '20', '10', '00', '10', ?, ?, ?, ?, ?, ?	\n");
		sql.append("\t	, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	\n");
		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, 'N', ?, '', ?, ?	\n");
		sql.append("\t	)	\n");
		
		return sql.toString();
		
    }
    
	/** ***********************************************************************
     * 주문정보 update
     ************************************************************************ */
 	
     private String getUpdQuery(){
         StringBuffer sql = new StringBuffer();		

  		sql.append("\n	UPDATE BCDBA.TBGLUGODRCTNT 	\n");
 		sql.append("\t	SET ODR_DTL_CLSS = '10', ODR_STAT_CLSS = '20'	\n");
 		sql.append("\t	WHERE ODR_NO=?	\n");
 		
 		return sql.toString();
     }
}
