/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBusPeopleInqDaoProc
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 이벤트->골프장버스운행이벤트->신청관리 리스트
*   적용범위  : Golf
*   작성일자  : 2009-09-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;


/** ****************************************************************************
 *  Golf
 * @author	(주)미디어포스 
 * @version 1.0
 **************************************************************************** */
public class GolfAdmBusPeopleInqDaoProc extends DbTaoProc {
	
	/**
	 * Proc 실행. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt			= null;
		ResultSet rs 					= null;
		String title					= dataSet.getString("TITLE");
		String actnKey 					= null;
		DbTaoResult result				= new DbTaoResult(title);  

		try {

			// 회원통합테이블 관련 수정사항 진행
			actnKey 					= GolfUtil.sqlInjectionFilter(dataSet.getString("actnKey"));	
			long page_no 				= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 				= dataSet.getLong("page_size")==0L?20L:dataSet.getLong("page_size");		
			int pidx 					= 0;
			String green_nm				= GolfUtil.sqlInjectionFilter(dataSet.getString("green_nm"));            //예약골프장명
			String golf_cmmn_code		= GolfUtil.sqlInjectionFilter(dataSet.getString("golf_cmmn_code"));      //예약코드
			String grade				= GolfUtil.sqlInjectionFilter(dataSet.getString("grade"));               //회원등급 한글
			String sch_reg_aton_st		= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_reg_aton_st"));     //조회 신청 시작일 
			String sch_reg_aton_ed		= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_reg_aton_ed")); 	 //조회 신청 종료일 
			String sch_pu_date_st		= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_pu_date_st"));   	 //조회 부킹 시작일 
			String sch_pu_date_ed		= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_pu_date_ed"));   	 //조회 부킹 종료일 
			String sch_type				= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_type"));            //이름,ID조회 여부     
			String search_word			= GolfUtil.sqlInjectionFilter(dataSet.getString("search_word"));         //조회 명     
			String sch_chng_aton_st		= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_chng_aton_st"));    //조회 취소일자 시작일              
			String sch_chng_aton_ed		= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_chng_aton_ed"));    //조회 취소일자 종료일   
						
			sch_pu_date_st = sch_pu_date_st.replaceAll("\\.","");
			sch_pu_date_ed = sch_pu_date_ed.replaceAll("\\.","");
			
			debug("sch_pu_date_st:"+sch_pu_date_st);
			debug("sch_pu_date_ed:"+sch_pu_date_ed);
			
			
			pstmt = con.prepareStatement(getSelectQuery(dataSet));		
			pidx = 0;
		
			pstmt.setLong(++pidx, page_size);
			pstmt.setLong(++pidx, page_size);
			pstmt.setLong(++pidx, page_no);
			pstmt.setLong(++pidx, page_size);
			pstmt.setLong(++pidx, page_no);
		
			if(!"".equals(green_nm)){
				pstmt.setString(++pidx,green_nm);
			}

			if(!"".equals(golf_cmmn_code)){
				pstmt.setString(++pidx,golf_cmmn_code);
			}

			if(!"".equals(grade)){
				pstmt.setString(++pidx,grade);
			}

			if(!"".equals(sch_reg_aton_st) && !"".equals(sch_reg_aton_ed)){
				pstmt.setString(++pidx,sch_reg_aton_st.replaceAll("-",""));
				pstmt.setString(++pidx,sch_reg_aton_ed.replaceAll("-",""));
			}

			if(!"".equals(sch_pu_date_st) && !"".equals(sch_pu_date_ed)){
				pstmt.setString(++pidx,sch_pu_date_st.replaceAll("-",""));
				pstmt.setString(++pidx,sch_pu_date_ed.replaceAll("-",""));
			}

			if(!"".equals(sch_chng_aton_st) && !"".equals(sch_chng_aton_ed)){
				pstmt.setString(++pidx,sch_chng_aton_st.replaceAll("-",""));
				pstmt.setString(++pidx,sch_chng_aton_ed.replaceAll("-",""));
			}
			
			if("ID".equals(sch_type)){
				pstmt.setString(++pidx,search_word);
			}else if("NAME".equals(sch_type)){
				pstmt.setString(++pidx,search_word);
			}

			if("admBusPeopleList".equals(actnKey)){
				pstmt.setLong(++pidx, page_no);
			}			
						
			
			
			
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				//GolfUtil.toTaoResultBoard(result, rs, serial);
				GolfUtil.toTaoResult(result, rs);
				result.addString("RESULT", "00");
			} else {
				result.addString("RESULT", "01");
			}		
			
		} catch(Throwable t){
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "SYSTEM_ERROR", null );
			msgEtt.addEvent( actnKey + ".do", "bt_ok.gif");
			throw new DbTaoException(msgEtt,t);
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return result;
	}
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.     
    ************************************************************************ */
	
    private String getSelectQuery(TaoDataSet dataSet) throws BaseException{
        StringBuffer sql = new StringBuffer();

		String green_nm			= GolfUtil.sqlInjectionFilter(dataSet.getString("green_nm"));               
		String golf_cmmn_code	= GolfUtil.sqlInjectionFilter(dataSet.getString("golf_cmmn_code"));   
		String grade			= GolfUtil.sqlInjectionFilter(dataSet.getString("grade"));                     
		String sch_reg_aton_st	= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_reg_aton_st")); 
		String sch_reg_aton_ed	= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_reg_aton_ed")); 
		String sch_pu_date_st	= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_pu_date_st"));   
		String sch_pu_date_ed	= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_pu_date_ed"));   
		String sch_type			= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_type"));               
		
		String actnKey          = GolfUtil.sqlInjectionFilter(dataSet.getString("actnKey"));
		String sch_chng_aton_st	= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_chng_aton_st"));               
		String sch_chng_aton_ed	= GolfUtil.sqlInjectionFilter(dataSet.getString("sch_chng_aton_ed"));  
		
		
		
		sql.append("\n     SELECT E.*                                                                                                 ");
		sql.append("\n       FROM (SELECT D.*,ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE,MAX(RN) OVER() TOT_CNT,                           ");
		sql.append("\n                    (MAX(RN) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO                              ");
		sql.append("\n               FROM (SELECT ROWNUM RN,A.APLC_SEQ_NO,B.GOLF_CMMN_CODE,B.GOLF_CMMN_CODE_NM,A.PU_TIME,           ");
		sql.append("\n                            A.GREEN_NM,A.TEOF_TIME,A.DPRT_PL_INFO,A.PU_DATE,A.CO_NM,A.CDHD_ID,                ");
		//sql.append("\n                            ( select DECODE(CDHD_CTGO_SEQ_NO,'8','화이트','7','골드','6','블루','5','챔피온') GRADE from BCDBA.TBGGOLFCDHDGRDMGMT where CDHD_ID = A.CDHD_ID ) AS GRADE ,            ");
		//sql.append("\n                            DECODE(C.CDHD_CTGO_SEQ_NO,'8','화이트','7','골드','6','블루','5','챔피온') GRADE, ");
		sql.append("\n                            CODE.GOLF_CMMN_CODE_NM AS GRADE, ");
		sql.append("\n                            A.HP_DDD_NO,A.HP_TEL_HNO,A.HP_TEL_SNO,A.EMAIL,A.RIDG_PERS_NUM,A.TEOF_DATE,                ");
		if("admBusPeopleList".equals(actnKey) ){
		sql.append("\n                            DECODE(SIGN(LENGTHB(A.MEMO_EXPL)-20),1,SUBSTRB(A.MEMO_EXPL,1,20)||'...',A.MEMO_EXPL) as MEMO_EXPL ,     ");
		}
		else{
			sql.append("\n                        A.MEMO_EXPL ,     ");	
		}
		sql.append("\n                            SUBSTR(A.REG_ATON,1,8) REG_ATON,               ");
		sql.append("\n                            A.CHNG_ATON,DECODE(A.CSLT_YN,NULL,'미처리','N','미처리','Y','처리') CSLT_YN        ");		
		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT A,                                  ");
		sql.append("\n                            (SELECT  GOLF_CMMN_CODE_NM,GOLF_CMMN_CODE                                         ");
		sql.append("\n                               FROM BCDBA.TBGCMMNCODE                                                         ");
		sql.append("\n                              WHERE GOLF_CMMN_CLSS='0054' AND USE_YN ='Y'                                     ");
		sql.append("\n                              ORDER BY SORT_SEQ) B                                                            ");
		sql.append("\n                             , BCDBA.TBGGOLFCDHD CDHD, BCDBA.TBGGOLFCDHDCTGOMGMT CTGO, BCDBA.TBGCMMNCODE CODE");
		sql.append("\n                      WHERE A.GOLF_SVC_APLC_CLSS = '9002'                                                     ");
		sql.append("\n                        AND A.PGRS_YN = B.GOLF_CMMN_CODE                                                      ");
		sql.append("\n                        AND A.CDHD_ID = CDHD.CDHD_ID     ");
		sql.append("\n                        AND CDHD.CDHD_CTGO_SEQ_NO = CTGO.CDHD_CTGO_SEQ_NO     ");
		sql.append("\n                        AND CTGO.CDHD_SQ2_CTGO = CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'     ");
		//sql.append("\n                        AND C.CDHD_ID = A.CDHD_ID                                                             ");
		if("admBusPeopleList".equals(actnKey) ){
			sql.append("\n                        AND A.PGRS_YN IN ('Y','W','B','C')                                                        ");
		}
		if(!"".equals(green_nm)){
			sql.append("\n                        AND A.GREEN_NM = ?                                                                ");
		}

		if(!"".equals(golf_cmmn_code)){
			sql.append("\n                        AND B.GOLF_CMMN_CODE = ?                                                          ");
		}

		if(!"".equals(grade)){
			sql.append("\n                        AND C.CDHD_CTGO_SEQ_NO = ?                                                        ");
		}

		
		if(!"".equals(sch_reg_aton_st) && !"".equals(sch_reg_aton_ed)){
			sql.append("\n                        AND A.REG_ATON BETWEEN ? AND ?                                                    ");
		}

		if(!"".equals(sch_pu_date_st) && !"".equals(sch_pu_date_ed)){
			sql.append("\n                        AND A.TEOF_DATE BETWEEN ? AND ?                                                     ");
		}

		if(!"".equals(sch_chng_aton_st) && !"".equals(sch_chng_aton_ed)){
			sql.append("\n                        AND A.CHNG_ATON BETWEEN ? AND ?                                                     ");
		}
		

		if("ID".equals(sch_type)){
			sql.append("\n                        AND A.CDHD_ID LIKE '%'||?||'%'                                                    ");
		}else if("NAME".equals(sch_type)){
			sql.append("\n                        AND A.CO_NM LIKE '%'||?||'%'                                                      ");
		}

		sql.append("\n                ORDER BY A.APLC_SEQ_NO DESC          ) D                                                     ");
		sql.append("\n                                                                                                            ");
		sql.append("\n             ) E                                                                                             ");
		if("admBusPeopleList".equals(actnKey) ){
			sql.append("\n      WHERE PAGE = ?                                                                                          ");
		}

		return sql.toString();
    }

		

}
