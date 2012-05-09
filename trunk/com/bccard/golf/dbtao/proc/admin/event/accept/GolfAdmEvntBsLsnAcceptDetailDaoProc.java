/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntBsLsnAcceptDetailDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 당첨자관리 상세
*   적용범위	: golf
*   작성일자	: 2009-07-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.accept;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBsLsnAcceptDetailDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 당첨자관리 상세";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet 
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rset = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result = null;
		String sql = "";
		String ctnt = "";  
		String useid = "";   
	
		try {

			// 회원통합테이블 관련 수정사항 진행
			// 01.data get
			String p_idx 				= data.getString("p_idx");
			String mode 				= data.getString("mode");
			String evnt_seq 			= data.getString("search_evnt");
			String evnt_clss 			= data.getString("evnt_clss");
			String golf_svc_aplc_clss 	= data.getString("golf_svc_aplc_clss");
			
			
			// 02.connection 연결
			conn = context.getDbConnection("default", null);
			result =  new DbTaoResult(TITLE);
			
			//기본변수들 할당
			int pidx = 0;
			int rs = 0;
			boolean eof = false;
			
			if("RegFormInq".equals(mode)){
				pidx = 0;
				sql = this.getSelectRegFormQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, p_idx);
				
				rset = pstmt.executeQuery();
				
				while(rset.next()){
					if(!eof) result.addString("RESULT", "00");
					
					//본문 내용 CLOB처리
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rset.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1)  
							bufferSt.append(buffer,0,byteRead);  
						reader.close();
					}
					ctnt = bufferSt.toString();
					
					result.addString("evnt_seq_no", rset.getString("EVNT_SEQ_NO"));
					result.addString("titl", 		rset.getString("TITL"));
					result.addString("ctnt", 		ctnt);
					result.addString("bltn_yn", 	rset.getString("BLTN_YN"));
					result.addString("inqr_num",	rset.getString("INQR_NUM"));
					eof = true;
					
					
					//조회수 증가
					pidx = 0;
					sql = this.getInqrPlusQuery();
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(++pidx, p_idx);
					
					rs = pstmt.executeUpdate();
					
					
				}
				if(!eof) result.addString("RESULT","01");
				
				
			}else if("PrzWinList".equals(mode)){
				pidx = 0;
				sql = this.getSelectPrzWinQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, golf_svc_aplc_clss);
				pstmt.setString(++pidx, evnt_seq);	
				rset = pstmt.executeQuery();
				
				while(rset.next()){
					if(!eof) result.addString("RESULT", "00");
					
					useid = rset.getString("CDHD_ID");
					if(useid.length() > 3){
						useid = "***"+useid.substring(3);
					}else{
						useid = "***";
					}
					
					result.addString("useid", 	useid);
					result.addString("usename", rset.getString("USENAME"));
					eof = true;
					
				}
				
				if(!eof) result.addString("RESULT", "01");
			
			}

			
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rset != null) rset.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectRegFormQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT													");
		sql.append("\n 		  SEQ_NO											");
		sql.append("\n 		  ,TITL												");
		sql.append("\n 		  ,EVNT_SEQ_NO										");
		sql.append("\n 		  ,CTNT												");
		sql.append("\n 		  ,BLTN_YN											");
		sql.append("\n 		  ,INQR_NUM											");
		sql.append("\n FROM   BCDBA.TBGEVNTPRZPEMGMT 							");
		sql.append("\n WHERE  SEQ_NO = ?										");
		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */     
	private String getInqrPlusQuery() throws Exception{ 
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n UPDATE BCDBA.TBGEVNTPRZPEMGMT SET						");
		sql.append("\n INQR_NUM = INQR_NUM+1									");
		sql.append("\n WHERE SEQ_NO = ?											");
		
		return sql.toString();		
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectPrzWinQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT 													");
		sql.append("\n 		APLC_SEQ_NO											");
		sql.append("\n 		,CDHD_ID											");
		sql.append("\n 		,(SELECT HG_NM FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T1.CDHD_ID)as USENAME ");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT T1								");
		sql.append("\n WHERE  GOLF_SVC_APLC_CLSS = ? AND PRZ_WIN_YN = 'Y'		");
		sql.append("\n 		AND  LESN_SEQ_NO  = ?								");
		return sql.toString();		
	}
}
