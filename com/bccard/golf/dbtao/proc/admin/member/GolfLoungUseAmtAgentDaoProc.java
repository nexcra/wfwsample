/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoungUseAmtAgentDaoProc
*   �ۼ���    : �强��
*   ����      : ��������� �̿� ���� ó��
*   �������  : golf
*   ��������  : WAS���� Agent ������� ���� 19�� �۾�. ������� 31������ ����
*   �ۼ�����  : 2011-01-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
***************************************************************************************************/
  

package com.bccard.golf.dbtao.proc.admin.member;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.bccard.golf.common.AppConfig;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;

public class GolfLoungUseAmtAgentDaoProc extends AbstractProc {
	
	public static final String TITLE = "��������� �̿� ���� ó��"; 
    private static String file_naming = "BWX450M_R010.";

    /*******************************************************************
	 * GolfLoungUseAmtAgentDaoProc ��������� �̿� ���� ó�� ������Ʈ  
	 ******************************************************************/
	public GolfLoungUseAmtAgentDaoProc() {}	

	public boolean execute(WaContext context) throws BaseException {

		String fileDirectory = null;
		String movedFileDirectory = null;

		boolean isNormal = false;
		
		try {
			fileDirectory = AppConfig.getAppProperty("TOPGOLFCARD_FILE_PATH" );
			movedFileDirectory = AppConfig.getAppProperty("TOPGOLFCARD_MOVED_FILE_PATH" );
			debug("fileDirectory : " + fileDirectory);
			debug("movedFileDirectory : " + movedFileDirectory);

		} catch(Exception e) {
			error("fileDirectory, movedFileDirectory : " + e.getMessage());
			isNormal = false;
			return isNormal;
		}

		int count = 0;       
        Connection conn = null;
        PreparedStatement pstmtUseAmt   = null;
        DataInputStream   d_in    = null;
        InputStreamReader reader  = null;
        BufferedReader bufReader  = null;
        
        String filename = "";
        
        try {
        	
            count = 0;
            
	        File directory = new File(fileDirectory);
	        File[] files   = directory.listFiles();            
            
            if ( files == null || files.length == 0) {
            	error( "<<<< " + fileDirectory + " ���丮�� ó���� ������ �����ϴ�. >>>>");
    			isNormal = false;
    			return isNormal;
            }
            
            for ( int x = 0 ; x < files.length ; x++ ) {
            	
                File file = files[x];
                filename = file.getName();
                
                if ( file.isDirectory() == true ) {
                    continue;
                }
                
                if ( filename.length() < 23 && !file_naming.equals(filename.substring(0, 13)) ) {
                    continue;
                }
                
                String use_yyyymm = filename.substring(13,19); // yyyymm
                info( "<<<< start BatchUseAmt file : " + filename + " >>>>");
                
                if(!file.canRead() || !file.exists() || !file.isFile()) {
                    error(filename + "�� ���� �� ���ų� �������� �ʽ��ϴ�.");
                    continue ;
                }
                
                d_in        = new DataInputStream(new FileInputStream(file));
                reader        = new InputStreamReader(d_in);
                bufReader    = new BufferedReader(reader);
                
				conn = context.getDbConnection("default", null);
				conn.setAutoCommit(false);

                String line_string  = null;
                String sqlUseAmt = insertUseAmtQuery();
                
                while ( (line_string = bufReader.readLine()) != null ) {
                	
                    int idx = 1;
                    int index = 0 ;    
                    
    				pstmtUseAmt   = conn.prepareStatement(sqlUseAmt.toString());
                   // debug("sqlUseAmt : " + sqlUseAmt); 
                    
                    StringTokenizer st      = new StringTokenizer(line_string,"|");
                    
                    pstmtUseAmt.setString(idx++, use_yyyymm);
                    
                    String temp             = st.nextToken();
                    pstmtUseAmt.setString(idx++, temp.trim());
                    pstmtUseAmt.setString(idx++, temp.trim().substring(4,6));    // ȸ�����ȣ = ī���ȣ�� 5~6��°��
                    
                    index++;
                    
                    while (st.hasMoreTokens()) {
                        temp        = st.nextToken();
                        index++;
                        if ( index == 3 ) {                                     // ȸ����ȸ����ȣ
                        	pstmtUseAmt.setString(idx++, temp.trim());
                        	pstmtUseAmt.setString(idx++, temp.trim().substring(2,3));    // ȸ������ = ȸ����ȸ����ȣ�� 3��°��
                        } else {
                        	pstmtUseAmt.setString(idx++, temp.trim());
                        }
                    }
                    
                    pstmtUseAmt.executeUpdate();
                    if ( pstmtUseAmt != null )  pstmtUseAmt.close();
                    
                    count++;
                    
                }                
          
                info( "<<<< count = " + count + " >>>>");
                info( "<<<< end BatchUseAmt file : " + filename + " >>>>");
                
                insertHistory(conn, "20", filename, count, "1", "������ �ε� ����");
                
                conn.commit();
                bufReader.close();
                
                file.renameTo(new File(movedFileDirectory + File.separator + filename));
                
                count = 0;
                
            }
            
        } catch ( Exception e ) {
        	
            error(filename + " : line [" + (count+1) + "] " + e.getMessage() );
            e.printStackTrace();
        	
            try { if ( conn != null ) conn.rollback(); } catch ( Exception ignored) {}
            try { insertHistory(conn, "20", filename, 0, "9", "line [" + (count+1) + "]" + e.getMessage()); } catch ( Exception ignored) {}             
            try { if ( conn != null ) conn.commit(); } catch ( Exception ignored) {} 
            
        } finally {
        	
            try { if ( pstmtUseAmt != null )  pstmtUseAmt.close(); }    catch (SQLException ignored) {}
            try { if ( conn != null ) conn.close(); conn = null; } catch ( SQLException ignored) {}
            if(reader != null) {try {reader.close();} catch (Exception e) {error("reader.close() : " + e.getMessage());}}
            if(bufReader != null) {try {bufReader.close();} catch (Exception e) {error("bufReader.close() : " + e.getMessage());}}
            
        }
	        
		return isNormal;
 
	}	

	
	/*************************************************************************
	 * 7. �����ͷε����� ����
	 *************************************************************************/
    private int insertHistory(Connection con, String data_clss, String file_nm, int proc_cnt, String proc_stat, String proc_memo) throws Exception {
    	
        PreparedStatement pstmtHistory = null;
        int result              = 0;

        try {
        	
            String proc_date = "" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            
     		String sqlHistory = insertDataLoadHistoryQuery();    		
    		//debug("sqlHistory : " + sqlHistory);
    		
    		int idx = 1;

    		pstmtHistory   = con.prepareStatement(sqlHistory.toString());
    		pstmtHistory.setString(idx++, data_clss);
    		pstmtHistory.setString(idx++, file_nm);
    		pstmtHistory.setString(idx++, proc_date);
    		pstmtHistory.setString(idx++, proc_stat);
    		pstmtHistory.setInt   (idx++, proc_cnt);
    		pstmtHistory.setString(idx++, proc_memo.length() > 120 ? proc_memo.substring(0, 120) : proc_memo);
            
            result = pstmtHistory.executeUpdate();
            
        } catch (Exception e) {
            error("insertHistory() Error : " + e.getMessage() );
            throw e;
        } finally {
            try { if ( pstmtHistory != null )  pstmtHistory.close(); }    catch (SQLException ignored) {}
        }
        
        return result;
        
    }
    
	
	/*************************************************************************
	 * 1. ī������� ���� SQL 
	 *************************************************************************/
	private String insertUseAmtQuery(){
		
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" \n");
        sqlBuffer.append(" \t INSERT INTO BCDBA.TBGFUSEAMT (USE_YYYYMM,	CARD_NO, MB_NO, FST_REG_DATE, MB_CDHG_NO, MEMBER_CLSS, BK_BR_NO,");
        sqlBuffer.append(" BK_BR_NM, MEM_SOC_ID, NAME, CNTRY_CRDT_AMT, CNTRY_CASH_AMT, OVSEA_CRDT_AMT, OVSEA_CASH_AMT, ZIPCODE, ZIPADDR,");
        sqlBuffer.append(" DETLADDR, COMP_NM, BRAN_NM, TEL_NO, MOBILE_NO, FINAL_TROUBLE_CD, REG_DATE) \n");
        sqlBuffer.append(" \t VALUES    ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");                

		return sqlBuffer.toString();	
	
	}
 
	/*************************************************************************
	 * 2. �����ͷε����� ���� SQL 
	 *************************************************************************/
	private String insertDataLoadHistoryQuery(){
		
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" \n");
        sqlBuffer.append(" \t INSERT INTO BCDBA.TBGFDATLDHST (DATA_CLSS, FILE_NM, PROC_DATE, PROC_STAT, PROC_CNT, PROC_MEMO )\n");
        sqlBuffer.append(" \t VALUES    ( ?,?,?,?,?,?) \n");
        
		return sqlBuffer.toString();
	
	}

	
	
} 
