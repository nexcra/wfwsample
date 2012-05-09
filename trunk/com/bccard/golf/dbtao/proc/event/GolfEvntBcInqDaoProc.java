/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBcInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : BC Golf 이벤트 상세보기
*   적용범위  : golf
*   작성일자  : 2009-06-05
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0 
 ******************************************************************************/
public class GolfEvntBcInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBcInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBcInqDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------		
			String urlReal = AppConfig.getAppProperty("URL_REAL");
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			//pstmt.setString(++idx, urlReal);
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			rs = pstmt.executeQuery();
			String ctnt = "";

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("SEQ_NO" 		,rs.getLong("EVNT_SEQ_NO") );		
					result.addString("EVNT_CLSS" 	,rs.getString("EVNT_CLSS") );
					result.addString("EVNT_NM" 		,rs.getString("EVNT_NM") );
					result.addString("EVNT_FROM" 	,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_TO" 		,rs.getString("EVNT_END_DATE") );					
					result.addString("TITL" 		,rs.getString("TITL") );
					result.addString("DISP_YN"		,rs.getString("BLTN_YN"));
					result.addLong("INQR_NUM" 		,rs.getLong("INQR_NUM") );		
					result.addString("IMG_NM"		,rs.getString("IMG_FILE_PATH"));
					result.addString("REG_ATON"		,rs.getString("REG_ATON"));
					//result.addString("CTNT"		,rs.getString("CTNT"));
					//debug("===CTNT => " + rs.getString("CTNT"));

					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					ctnt = bufferSt.toString();
					ctnt = GolfUtil.replace(ctnt, urlReal, "");
					result.addString("CTNT", ctnt);
					debug("ctnt : " + ctnt);
					
					/*
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CTNT", this.getCMSHtml(bufferSt.toString()));
					*/
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/**
	 * 조회수 업데이트 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int readCntUpd(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			sql = this.getUpdateQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			

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
        
		sql.append("\n SELECT");
		sql.append("\n 	EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM,");
		sql.append("\n 	TO_CHAR(TO_DATE(EVNT_STRT_DATE,'YYYYMMDD'),'YYYY-MM-DD') EVNT_STRT_DATE, TO_CHAR(TO_DATE(EVNT_END_DATE,'YYYYMMDD'),'YYYY-MM-DD') EVNT_END_DATE, ");
		sql.append("\n 	TITL, CTNT, BLTN_YN, INQR_NUM, IMG_FILE_PATH, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON ");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGEVNTMGMT");
		sql.append("\n WHERE EVNT_SEQ_NO = ?	");	
		sql.append("\n AND EVNT_CLSS = '0001'	");		

		return sql.toString();
    }

    /*************************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGEVNTMGMT SET	\n");
		sql.append("\t  INQR_NUM=INQR_NUM+1	\n");
		sql.append("\t WHERE EVNT_SEQ_NO=?	\n");
        return sql.toString();
    }

    /*************************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
    /*
     private String getCMSHtml(String cmsUrl) throws IOException {

        URL url;//URL 주소 객체        
 		String cmsCtnt = "";
 		try {
 			url = new URL(cmsUrl); //url에 쿼리 포함
 		} catch (MalformedURLException e) {
 			e.printStackTrace();
 		}

 		if (!cmsUrl.equals("")) {
             //URL객체를 생성하고 해당 URL로 접속한다..
             url = new URL(cmsUrl);
             URLConnection connection = url.openConnection();

             //내용을 읽어오기위한 InputStream객체를 생성한다..
             InputStream is = connection.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);
             
             //내용을 읽어서 화면에 출력한다..
             String buf = null;
             while(true){
                 buf = br.readLine();
                 if(buf == null) break;

                 cmsCtnt = cmsCtnt + buf;
             }            

 		}
 		
 		return cmsCtnt;
     }
     */
}
