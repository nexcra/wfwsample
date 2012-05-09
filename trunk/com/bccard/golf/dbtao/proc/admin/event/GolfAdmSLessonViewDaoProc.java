package com.bccard.golf.dbtao.proc.admin.event;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

public class GolfAdmSLessonViewDaoProc extends AbstractProc
{
  public DbTaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet data)
    throws BaseException
  {
    String title = data.getString("TITLE");
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    DbTaoResult result = new DbTaoResult(title);
    try
    {
      conn = context.getDbConnection("default", null);

      long EVNT_SEQ_NO = data.getLong("EVNT_SEQ_NO");

      debug("===================//// check : " + EVNT_SEQ_NO);

      StringBuffer sql = new StringBuffer();
      sql.append("\n  SELECT EVNT_NM, TO_CHAR(TO_DATE(EVNT_STRT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_STRT_DATE,\t");
      sql.append("\n  TO_CHAR(TO_DATE(EVNT_END_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_END_DATE, CTNT, BLTN_YN, ");
      sql.append("\n \tRCRU_PE_ORG_NUM, TO_CHAR(TO_DATE(LESN_STRT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') LESN_STRT_DATE,\t");
      sql.append("\n \tTO_CHAR(TO_DATE(LESN_END_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') LESN_END_DATE, LESN_NORM_COST,\t");
      sql.append("\n \tLESN_DC_COST, EVNT_BNFT_EXPL, AFFI_FIRM_EXPL, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON,\t");
      sql.append("\n \tINQR_NUM, IMG_FILE_PATH, TITL\t");
      sql.append("\n \tFROM  BCDBA.TBGEVNTMGMT\t");
      sql.append("\n \tWHERE EVNT_CLSS = '0003' \t");
      sql.append("\n \tAND EVNT_SEQ_NO = ? \t");

      int idx = 0;
      pstmt = conn.prepareStatement(sql.toString());

      idx++; pstmt.setLong(idx, data.getLong("EVNT_SEQ_NO"));
      rs = pstmt.executeQuery();

      if (rs != null)
      {
        while (rs.next()) {
          result.addString("EVNT_NM", rs.getString("EVNT_NM"));
          result.addString("EVNT_ST", rs.getString("EVNT_STRT_DATE"));
          result.addString("EVNT_EN", rs.getString("EVNT_END_DATE"));
          result.addString("DISP_YN", rs.getString("BLTN_YN"));
          result.addLong("PE_NUM", rs.getLong("RCRU_PE_ORG_NUM"));
          result.addString("LESN_ST", rs.getString("LESN_STRT_DATE"));
          result.addString("LESN_EN", rs.getString("LESN_END_DATE"));
          result.addLong("LESN_NORM_COST", rs.getLong("LESN_NORM_COST"));
          result.addLong("LESN_DC_COST", rs.getLong("LESN_DC_COST"));
          result.addString("BNFT_EXPL", rs.getString("EVNT_BNFT_EXPL"));
          result.addString("AFFI_FIRM_EXPL", rs.getString("AFFI_FIRM_EXPL"));
          result.addLong("CNT", rs.getLong("INQR_NUM"));
          result.addString("IMG_FILE_PATH", rs.getString("IMG_FILE_PATH"));
          result.addString("REG_ATON", rs.getString("REG_ATON"));
          result.addLong("SEQ", data.getLong("EVNT_SEQ_NO"));
          Reader reader = null;
          StringBuffer bufferSt = new StringBuffer();
          reader = rs.getCharacterStream("CTNT");
          
          if (reader != null) {
            char[] buffer = new char[1024];
            int byteRead;
            while ((byteRead = reader.read(buffer, 0, 1024)) != -1){
              
              bufferSt.append(buffer, 0, byteRead);
              
            }reader.close();
          }
          result.addString("CTNT", bufferSt.toString());

          result.addString("RESULT", "00");
        }

      }

      if (result.size() < 1)
        result.addString("RESULT", "01");
    }
    catch (Throwable t)
    {
      throw new BaseException(t); } finally {
      try {
        if (rs != null) rs.close();  } catch (Exception localException) {
      }try {
        if (pstmt != null) pstmt.close();  } catch (Exception localException1) {
      }try {
        if (conn != null) conn.close(); 
      } catch (Exception localException2) {
      }
    }
    return result;
  }
}