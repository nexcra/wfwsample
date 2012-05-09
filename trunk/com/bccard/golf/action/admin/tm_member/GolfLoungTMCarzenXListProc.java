/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoungTMListProc
*   작성자    : 서비스개발팀 강선영
*   내용      : TM회원 조회 Proc 
*   적용범위  : Golf
*   작성일자  : 2009-07-17  
************************** 수정이력 ****************************************************************
*    일자       작성자      변경사항
* 2011.03.30    이경희	   TM채널 추가 (리딩아이, 월앤비전)
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
 
import com.bccard.waf.common.BcUtil;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
/** ****************************************************************************
 * Media4th 
 * @author
 * @version 2009-05-06
 **************************************************************************** */
public class GolfLoungTMCarzenXListProc extends AbstractProc {
	public static final String TITLE = "TM회원 조회 > 엑셀";
	/** *****************************************************************
	 * GolfLoungTMListProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	
	/** ***********************************************************************
	* Proc 실행.
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data 
	* @return DbTaoResult	result 
	************************************************************************ */
	public TaoResult execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		DbTaoResult result = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {				
			
			//debug("GolfLoungTMlistProc >> 쿼리 시작 >> ");
			
			con = context.getDbConnection("default", null);
			result = new DbTaoResult(TITLE);
			
			String start_date		= data.getString("start_date");	// 
			String end_date			= data.getString("end_date");	//
			String jumin_no			= data.getString("jumin_no");	//
			String hg_nm			= data.getString("hg_nm");	//
			String tb_rslt_clss		= data.getString("tb_rslt_clss");	//
			String page				= data.getString("page");	//
			String acpt_chnl_clss	= data.getString("acpt_chnl_clss");
			String st_gb			= data.getString("st_gb");
			String sch_type			= data.getString("sch_type");
			String sch_text			= data.getString("sch_text");
			String sch_scal			= data.getString("sch_scal");
			String sch_auth			= data.getString("sch_auth");
			String sch_cupn			= data.getString("sch_cupn");
			long ret =0;

			StringBuffer sql = new StringBuffer();	
			
			

			sql.append("SELECT	*  \n");  
			sql.append("FROM (SELECT  ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, A.MB_CDHD_NO,A.HG_NM, A.EMAIL_ID, A.STTL_FAIL_RSON_CTNT,  \n");
			sql.append("				A.ACPT_CHNL_CLSS,A.TB_RSLT_CLSS, A.REJ_RSON, A.JUMIN_NO, A.GOLF_CDHD_GRD_CLSS,  \n");
			sql.append("				CASE WHEN A.GOLF_CDHD_GRD_CLSS ='1' THEN '골드' WHEN A.GOLF_CDHD_GRD_CLSS ='2' THEN '블루' WHEN A.GOLF_CDHD_GRD_CLSS ='3' THEN '챔피온'   ELSE '블랙' END GOLF_CDHD_GRD_CLSS_NM,  \n");

			sql.append("              DECODE(A.JOIN_CHNL,'03','H&C','10','CIC코리아','11','TCK','12','리딩아이','13','윌앤비전',(SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0050' AND A.JOIN_CHNL = GOLF_CMMN_CODE)) JOIN_CHNL_NM, \n");
			sql.append("              rcru_pl_clss, (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND A.rcru_pl_clss = GOLF_CMMN_CODE) rcru_pl_clss_NM,  \n");    

			sql.append("              COUNT(*) OVER() AS TOT_CNT, A.RECP_DATE ,A.SQ1_TCALL_DATE, A.JOIN_CHNL, A.AUTH_CLSS, A.CARD_NO, \n");  
			sql.append("              '-'    SECE_YN ,A.SQ3_TCALL_DATE, SUBSTR(A.HP_DDD_NO,2,3) HP_DDD_NO, A.HP_TEL_HNO, A.HP_TEL_SNO  \n");    
			sql.append("              , CASE SCAL_LCAL_CLSS WHEN '1' THEN '실속' WHEN '2' THEN '기존' WHEN '3' THEN 'VIP' END CAZEN_GUBUN   \n"); 
			sql.append("              , CASE A.AUTH_CLSS WHEN '1' THEN '카드' WHEN '2' THEN '복합' WHEN '3' THEN '포인트' END AUTH_TXT   \n");        
			sql.append("              , B.CUPN_NO	\n");    
			sql.append("        FROM  BCDBA.TBLUGTMCSTMR A \n");
			sql.append("        LEFT JOIN BCDBA.TBEVNTLOTPWIN B ON A.JUMIN_NO=B.JUMIN_NO AND SITE_CLSS='10' AND EVNT_NO='119' \n");
			sql.append("       WHERE  A.RND_CD_CLSS='2'  \n");
			
			if ( "2".equals(st_gb)           ) 
			{
				sql.append("         AND  A.SQ3_TCALL_DATE BETWEEN ? AND  ? 	 \n");
			} else {
				sql.append("         AND  A.RECP_DATE BETWEEN ? AND  ? 	 \n");
			}
			
			if (   !(jumin_no==null || "".equals(jumin_no)) )
			{
				sql.append("         AND  A.JUMIN_NO LIKE ? \n");
			}
			if (!(hg_nm==null || "".equals(hg_nm)))
			{
				sql.append("         AND  A.HG_NM LIKE ? \n");
			}
			if(!GolfUtil.empty(sch_text) && sch_type.equals("mobile")){
				sql.append("         AND  (A.HP_DDD_NO LIKE ? OR A.HP_TEL_HNO LIKE ? OR A.HP_TEL_SNO LIKE ?) \n");
			}
			//System.out.println("tb_rslt_clss====================>"+tb_rslt_clss + "===============>page:"+data.getLong("page_no"));
			
			
			if ( "99".equals(tb_rslt_clss) ) {
				sql.append("         AND  A.TB_RSLT_CLSS NOT IN ('01','00') \n");
			
			} else if ( "98".equals(tb_rslt_clss) ) {
				sql.append("         AND  A.TB_RSLT_CLSS = '98' \n");
			} else if ( !"00".equals(tb_rslt_clss) ) {
				sql.append("         AND  A.TB_RSLT_CLSS  IN ('01','00') \n");
			} 
			//sql.append("         AND  A.ACPT_CHNL_CLSS = ? \n");

			
			// 카젠 TM 구분
			if(!GolfUtil.empty(sch_scal)){
				sql.append("         AND  A.SCAL_LCAL_CLSS = ? \n");
			}
			
			// 결제구분
			if(!GolfUtil.empty(sch_auth)){
				sql.append("         AND  A.AUTH_CLSS = ? \n");
			}
			
			// 쿠폰지급여부
			if(!GolfUtil.empty(sch_cupn)){
				if(sch_cupn.equals("Y")){
					sql.append("         AND  B.CUPN_NO IS NOT NULL	\n");
				}else if(sch_cupn.equals("N")){
					sql.append("         AND  B.CUPN_NO IS NULL \n");
				}
			}
			

			sql.append("          AND A.RCRU_PL_CLSS='0103'  \n"); 
			sql.append("    ORDER BY  RNUM \n");
			sql.append(")         \n");
    //sql.append("WHERE PAGE = ? \n");

			
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 

			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setString(++idx, start_date);
			pstmt.setString(++idx, end_date);
			
			if (!(jumin_no==null || "".equals(jumin_no)))
			{
				pstmt.setString(++idx, "%"+jumin_no+"%");
			}
			if (!(hg_nm==null || "".equals(hg_nm)))
			{
				pstmt.setString(++idx, "%"+hg_nm+"%");
			}
			if(!GolfUtil.empty(sch_text) && sch_type.equals("mobile")){
				pstmt.setString(++idx, "%"+sch_text+"%");
				pstmt.setString(++idx, "%"+sch_text+"%");
				pstmt.setString(++idx, "%"+sch_text+"%");
			}
			if(!GolfUtil.empty(sch_scal)){
				pstmt.setString(++idx, sch_scal);
			}
			if(!GolfUtil.empty(sch_auth)){
				pstmt.setString(++idx, sch_auth);
			}
			
			  
			//pstmt.setString(++idx, acpt_chnl_clss);			
			//pstmt.setLong(++idx, data.getLong("page_no"));

			
			rs = pstmt.executeQuery();
			
			if(rs != null)
			{
				String sece_yn = "";
				String state = "";
				String join_chnl = "";
				String join_chnl_nm = "";
				String auth_clss = "";
				String auth_clss_nm = "";
				String str_jumin_no = "";
				String hp = "";
				String txt_tb_rslt_clss = "";
				String txt_jumin_no = "";
				
				while(rs.next())  
				{
					
					
					result.addString("RNUM",					rs.getString("RNUM"));
					result.addString("CURR_PAGE"				,rs.getString("PAGE") );

					result.addString("MB_CDHD_NO",				rs.getString("MB_CDHD_NO"));
					result.addString("HG_NM",					rs.getString("HG_NM"));
					result.addString("EMAIL_ID",				rs.getString("EMAIL_ID"));
					result.addString("REJ_RSON",				rs.getString("REJ_RSON"));

					result.addString("GOLF_CDHD_GRD_CLSS",		rs.getString("GOLF_CDHD_GRD_CLSS"));
					result.addString("GOLF_CDHD_GRD_CLSS_NM",	rs.getString("GOLF_CDHD_GRD_CLSS_NM"));
					result.addString("TOT_CNT",					rs.getString("TOT_CNT"));
					result.addString("RECP_DATE",				rs.getString("RECP_DATE"));
					result.addString("SQ1_TCALL_DATE",			rs.getString("SQ1_TCALL_DATE"));
					
					result.addString("JOIN_CHNL",			rs.getString("JOIN_CHNL")	);
					result.addString("JOIN_CHNL_NM",		rs.getString("JOIN_CHNL_NM")		   );
					
					result.addString("RCRU_PL_CLSS",		rs.getString("RCRU_PL_CLSS")	);
					result.addString("RCRU_PL_CLSS_NM",		rs.getString("RCRU_PL_CLSS_NM")		   );
					result.addString("ACPT_CHNL_CLSS",		rs.getString("ACPT_CHNL_CLSS")	); 
					result.addString("SQ3_TCALL_DATE",		rs.getString("SQ3_TCALL_DATE")	); 
					
					result.addString("STTL_FAIL_RSON_CTNT",		rs.getString("STTL_FAIL_RSON_CTNT")	); 
					
					
					
					
			
					auth_clss = rs.getString("AUTH_CLSS");	
					if ( auth_clss==null || "".equals(auth_clss) ) auth_clss_nm ="";
					else if ( "1".equals(auth_clss) )  auth_clss_nm ="카드승인";
					else if ( "2".equals(auth_clss) )  auth_clss_nm ="복합결제";
					else if ( "3".equals(auth_clss) )  auth_clss_nm ="포인트";
					else if ( "4".equals(auth_clss) )  auth_clss_nm ="제휴사정산";

					result.addString("AUTH_CLSS",					auth_clss);
					result.addString("AUTH_CLSS_NM",				auth_clss_nm );
					result.addString("CARD_NO",					rs.getString("CARD_NO"));
					
					
					sece_yn=rs.getString("SECE_YN");
					if ( sece_yn==null || "".equals(sece_yn) ) state ="-";
					else if ( "N".equals(sece_yn) )  state ="정상";
					else if ( "Y".equals(sece_yn) )  state ="탈회";
					else  state ="";
					result.addString("SECE_YN",					rs.getString("SECE_YN"));
					result.addString("STATE",					state);
					

					if(GolfUtil.empty(rs.getString("TB_RSLT_CLSS"))){
						txt_tb_rslt_clss = "";
					}else{
						if(rs.getString("TB_RSLT_CLSS").equals("01")){
							txt_tb_rslt_clss = "가입대기";
						}else if(rs.getString("TB_RSLT_CLSS").equals("00")){
							txt_tb_rslt_clss = "가입완료";
						}else if(rs.getString("TB_RSLT_CLSS").equals("03")){
							txt_tb_rslt_clss = "가입실패";
						}else{
							txt_tb_rslt_clss = "오류";
						}
					}
					result.addString("TB_RSLT_CLSS",			txt_tb_rslt_clss);
					
					result.addString("JUMIN_NO",				rs.getString("JUMIN_NO")); 
					if(GolfUtil.empty(rs.getString("JUMIN_NO"))){
						txt_jumin_no = "";
					}else{
						txt_jumin_no = rs.getString("JUMIN_NO").substring(0, 6);
					}
					result.addString("TXT_JUMIN_NO",			txt_jumin_no); 

					hp = rs.getString("HP_DDD_NO")+"-"+rs.getString("HP_TEL_HNO")+"-"+rs.getString("HP_TEL_SNO");
					result.addString("HP",						hp);
					result.addString("CAZEN_GUBUN",				rs.getString("CAZEN_GUBUN"));
					result.addString("AUTH_TXT",				rs.getString("AUTH_TXT"));
					result.addString("CUPN_NO",					rs.getString("CUPN_NO"));

					ret++;	
				}
			}
												 
		
			if(ret == 0){
				result.addString("RESULT","01");
				
			} else {
				result.addString("RESULT","00");	
			}
						

        } catch(Exception e) {
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
}
