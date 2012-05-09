/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoungTMDetailProc
*   �ۼ���    : ���񽺰����� ������
*   ����      : ������ TMȸ�� ��ȸ
*   �������  : golf
*   �ۼ�����  : 2009-07-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext; 
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;
import com.bccard.waf.common.BcUtil;

/******************************************************************************
 * Golf
 * @author	
 * @version	1.0 
 ******************************************************************************/
public class GolfLoungTMDetailProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfLoungTMDetailProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfLoungTMDetailProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		ResultSet rs2 = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;

		DbTaoResult result =  null;

		try {
			
			result = new DbTaoResult(title);	
			conn = context.getDbConnection("default", null);
				 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("jumin_no"));
			pstmt.setString(++idx, data.getString("work_date"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				String sece_yn = null;
				String state = null;
				String grade = null;
				String grade_nm = null;
				String join_chnl = null;
				String join_nm = null;
				String amt = null;
				String auth_date_fmt  = null;
				String cncl_date_fmt  = null;
				String auth_date  = null;
				String tm_join_chnl = null;
				String tm_join_nm = null;
				String auth_clss = "";
				String auth_clss_nm = "";
				String web_secy_yn ="";
				String web_state ="";

				String cupn_no = "";

				if(rs.next())  {

					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("HG_NM" 				,rs.getString("HG_NM") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("JUMIN_NO_CRY",			BcUtil.getCryptJuminNo(rs.getString("JUMIN_NO"),true) );

					result.addString("EMAIL_ID" 			,rs.getString("EMAIL_ID") );
					join_chnl =rs.getString("JOIN_CHNL");
					result.addString("JOIN_CHNL" 			,join_chnl );
					if (join_chnl==null || "".equals(join_chnl)) join_nm ="����ȸ�� �ڷ����";
					else if ("0001".equals(join_chnl)) join_nm ="�Ϲ�ȸ��";
					else if ("0002".equals(join_chnl)) join_nm ="TM���";
					else if ("0003".equals(join_chnl)) join_nm ="TM���";
					else if ("0004".equals(join_chnl)) join_nm ="TMè�ǿ�";
					else if ("0005".equals(join_chnl)) join_nm ="TM��";
					result.addString("JOIN_CHNL_NM" 			,join_nm );


					tm_join_chnl =rs.getString("TM_JOIN_CHNL");
					result.addString("TM_JOIN_CHNL" 			,tm_join_chnl );
					if (tm_join_chnl==null || "".equals(tm_join_chnl)) tm_join_nm ="";
					else if ("03".equals(tm_join_chnl)) tm_join_nm ="H&C��Ʈ��ũ";
					else if ("11".equals(tm_join_chnl)) tm_join_nm ="TCK";
					else if ("10".equals(tm_join_chnl)) tm_join_nm ="CIC�ڸ���";
				
					result.addString("TM_JOIN_CHNL_NM" 			,tm_join_nm );
					
					
					result.addString("RECP_DATE" 			,rs.getString("RECP_DATE") );
					result.addString("TB_RSLT_CLSS" 		,rs.getString("TB_RSLT_CLSS") );
					result.addString("REJ_RSON" 			,rs.getString("REJ_RSON") );
					result.addString("GOLF_CDHD_GRD_CLSS" 	,rs.getString("GOLF_CDHD_GRD_CLSS") );
					result.addString("AUTH_NO" 				,rs.getString("AUTH_NO") );
					result.addString("AUTH_TIME" 			,rs.getString("AUTH_TIME") );
					amt = rs.getString("AUTH_AMT");
					if (amt==null || "".equals(amt)) amt = "���αݾ׾���";
					else  amt= GolfUtil.comma(amt) ;
					result.addString("AUTH_AMT" 			,amt );
					result.addString("RND_CD_CLSS" 			,rs.getString("RND_CD_CLSS") );

					auth_clss = rs.getString("TM_AUTH_CLSS");	
					if ( auth_clss==null || "".equals(auth_clss) ) auth_clss_nm ="";
					else if ( "1".equals(auth_clss) )  auth_clss_nm ="ī�����";
					else if ( "2".equals(auth_clss) )  auth_clss_nm ="���հ���";
					else if ( "3".equals(auth_clss) )  auth_clss_nm ="����Ʈ";

					result.addString("AUTH_CLSS",					auth_clss);
					result.addString("AUTH_CLSS_NM",				auth_clss_nm );

					result.addString("CARD_NO" 				,rs.getString("CARD_NO") );
					result.addString("FMT_CARD_NO" 			,rs.getString("FMT_CARD_NO") );
					result.addString("VALD_LIM" 			,rs.getString("VALD_LIM") );


					result.addString("CNCL_DATE" 			,rs.getString("CNCL_DATE") );
					result.addString("CNCL_TIME"			,rs.getString("CNCL_TIME") );
					result.addString("AUTH_DATE" 			,rs.getString("AUTH_DATE") );
					result.addString("MB_CDHD_NO" 			,rs.getString("MB_CDHD_NO") );
					auth_date_fmt =rs.getString("AUTH_DATE_FMT");
					if (auth_date_fmt==null || "".equals(auth_date_fmt)) auth_date_fmt = "���γ�������";
					result.addString("AUTH_DATE_FMT" 			,auth_date_fmt );
					cncl_date_fmt =rs.getString("CNCL_DATE_FMT");
					if (cncl_date_fmt==null || "".equals(cncl_date_fmt)) cncl_date_fmt = "��ҽ��γ�������";
					result.addString("CNCL_DATE_FMT" 			,cncl_date_fmt );
					
					grade = rs.getString("CDHD_CTGO_SEQ_NO");
					grade_nm = rs.getString("CDHD_CTGO_SEQ_NO_NM");
					if (grade==null || "".equals(grade)) grade_nm ="����ȸ�� �ڷ����";

					result.addString("CDHD_CTGO_SEQ_NO" 	,grade );
					result.addString("CDHD_CTGO_SEQ_NO_NM" 	, grade_nm );
					result.addString("GOLF_CDHD_GRD_CLSS_NM" 	,rs.getString("GOLF_CDHD_GRD_CLSS_NM") );
					
				
					sece_yn=rs.getString("SECE_YN");
					result.addString("SECE_YN" 	,sece_yn );
					if ( sece_yn==null || "".equals(sece_yn) ) state ="TM ����ȸ�� �ƴ�";
					else if ( "Y".equals(sece_yn) )  state ="TM����";
					else if ( "N".equals(sece_yn) )  state ="TM������"; 
					else  state ="";

					result.addString("STATE",					state);

					web_secy_yn= rs.getString("WEB_STATE");
					if ( web_secy_yn==null || "".equals(web_secy_yn) ) web_state ="�� ����ȸ�� �ƴ� OR  ������Ʈ �̰���";
					else if ( "N".equals(web_secy_yn) )  web_state ="����";
					else if ( "Y".equals(web_secy_yn) )  web_state ="Żȸ"; 
					else  web_state ="";

					result.addString("WEB_STATE",					web_state);



					result.addString("JONN_ATON" 				,rs.getString("JONN_ATON") );
					result.addString("ACRG_CDHD_JONN_DATE" 		,rs.getString("ACRG_CDHD_JONN_DATE") );
					result.addString("ACRG_CDHD_END_DATE" 		,rs.getString("ACRG_CDHD_END_DATE") );

					try
					{
						//����ǰ ��ȸ ----------------------------------------------------------			
						int idxx = 0;
						pstmt2 = conn.prepareStatement(this.getSelectCupnQuery());
						pstmt2.setString(++idxx, "119");
						pstmt2.setString(++idxx, data.getString("jumin_no"));
						
						rs2 = pstmt2.executeQuery();
						
						if(rs2 != null)
						{
							cupn_no = "";
							while(rs2.next())  
							{
								cupn_no += rs2.getString("CUPN_NO") + "/";
							}
						}

					}
					catch (Throwable t) {
						throw new BaseException(t);
					} finally {
						try { if(rs2 != null) {rs2.close();} else{} } catch (Exception ignored) {}
						try { if(pstmt2 != null) {pstmt2.close();} else{} } catch (Exception ignored) {}
					}
					
					result.addString("CUPN_NO",	cupn_no);




					result.addString("RESULT", "00"); //������
				}
			} 
			if(result.size() < 1) {
				result.addString("RESULT", "01");
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.     
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("SELECT B.CDHD_ID, A.HG_NM,A.JUMIN_NO, A.EMAIL_ID, A.JOIN_CHNL TM_JOIN_CHNL,B.JOIN_CHNL,A.RECP_DATE,A.TB_RSLT_CLSS ,A.REJ_RSON , A.CARD_NO FMT_CARD_NO, A.VALD_LIM,A.AUTH_CLSS TM_AUTH_CLSS, \n");
		sql.append("		A.GOLF_CDHD_GRD_CLSS,  C.AUTH_DATE, C.AUTH_NO, C.AUTH_TIME, C.CARD_NO, C.AUTH_AMT, C.RND_CD_CLSS ,  \n");
		sql.append("		TO_DATE(C.AUTH_DATE || C.AUTH_TIME ,'yyyy/MM/dd hh24:mi:ss') AUTH_DATE_FMT,  \n");
		sql.append("		C.AUTH_CLSS, C.CNCL_DATE, C.CNCL_TIME   ,C.MB_CDHD_NO,   \n");
		sql.append("		CASE WHEN A.GOLF_CDHD_GRD_CLSS ='1' THEN '���' WHEN A.GOLF_CDHD_GRD_CLSS ='2' THEN '���' WHEN A.GOLF_CDHD_GRD_CLSS ='3' THEN 'è�ǿ�' ELSE '��' END GOLF_CDHD_GRD_CLSS_NM, \n");
		sql.append("		TO_DATE(C.CNCL_DATE || C.CNCL_TIME ,'yyyy/MM/dd hh24:mi:ss') CNCL_DATE_FMT,  \n");
		sql.append("       B.CDHD_CTGO_SEQ_NO    , \n");
		sql.append("       (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0052' AND GOLF_CMMN_CODE =B.CDHD_CTGO_SEQ_NO ) CDHD_CTGO_SEQ_NO_NM, \n");
		sql.append("       TO_DATE(B.JONN_ATON,'yyyy/MM/dd hh24:mi:ss') JONN_ATON,B.ACRG_CDHD_JONN_DATE,B.ACRG_CDHD_END_DATE , \n");
		sql.append("       CASE WHEN A.TB_RSLT_CLSS IN ('01','00') THEN 'Y'  ELSE 'N' END SECE_YN , CASE WHEN A.TB_RSLT_CLSS ='00' THEN NVL(B.SECE_YN,'N')  ELSE '' END    WEB_STATE  \n");
		sql.append("FROM   BCDBA.TBLUGTMCSTMR  A   ,  BCDBA.TBGGOLFCDHD   B , BCDBA.TBGLUGANLFEECTNT C   \n");
		sql.append("WHERE  A.RND_CD_CLSS='2'     \n");
		sql.append("AND A.JUMIN_NO =?   \n");
		sql.append("AND  A.RECP_DATE = ? \n");
		sql.append("AND A.JUMIN_NO = B.JUMIN_NO (+) \n");
		sql.append("AND A.JUMIN_NO = C.JUMIN_NO (+) \n");
		sql.append("AND A.RECP_DATE = C.AUTH_DATE (+) \n");

		
		return sql.toString();
    }


	/** ***********************************************************************
    * ����ǰ ���� ����     
    ************************************************************************ */
    private String getSelectCupnQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append(" SELECT  CUPN_NO   FROM  BCDBA.TBEVNTLOTPWIN  WHERE SITE_CLSS='10' AND EVNT_NO= ? AND JUMIN_NO = ? ");

		
		return sql.toString();
    }

}
