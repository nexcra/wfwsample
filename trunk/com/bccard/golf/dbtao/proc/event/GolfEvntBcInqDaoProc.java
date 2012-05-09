/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBcInqDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : BC Golf �̺�Ʈ �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-06-05
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * @author	����Ŀ�´����̼�
 * @version	1.0 
 ******************************************************************************/
public class GolfEvntBcInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBcInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBcInqDaoProc() {}	

	/**
	 * Proc ����.
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
			 
			//��ȸ ----------------------------------------------------------		
			String urlReal = AppConfig.getAppProperty("URL_REAL");
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
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
					
					result.addString("RESULT", "00"); //������
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
	 * ��ȸ�� ������Ʈ ó��
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
    * Query�� �����Ͽ� �����Ѵ�.    
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
    * Query�� �����Ͽ� �����Ѵ�.    
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
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
    /*
     private String getCMSHtml(String cmsUrl) throws IOException {

        URL url;//URL �ּ� ��ü        
 		String cmsCtnt = "";
 		try {
 			url = new URL(cmsUrl); //url�� ���� ����
 		} catch (MalformedURLException e) {
 			e.printStackTrace();
 		}

 		if (!cmsUrl.equals("")) {
             //URL��ü�� �����ϰ� �ش� URL�� �����Ѵ�..
             url = new URL(cmsUrl);
             URLConnection connection = url.openConnection();

             //������ �о�������� InputStream��ü�� �����Ѵ�..
             InputStream is = connection.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);
             
             //������ �о ȭ�鿡 ����Ѵ�..
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
