package com.bccard.golf.dbtao.proc.admin.event;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;

public class GolfAdmEvntBkJoinExcelDaoProc extends AbstractProc
{

    public GolfAdmEvntBkJoinExcelDaoProc()
    {
    }

    public DbTaoResult execute(WaContext wacontext, HttpServletRequest httpservletrequest, TaoDataSet taodataset)
        throws BaseException
    {
        String s = taodataset.getString("TITLE");
        ResultSet resultset = null;
        Connection connection = null;
        PreparedStatement preparedstatement = null;
        DbTaoResult dbtaoresult = new DbTaoResult(s);
        try
        {
            connection = wacontext.getDbConnection("default", null);
            String s1 = taodataset.getString("SEARCH_SEL");
            String s2 = taodataset.getString("SEARCH_WORD");
            String s3 = taodataset.getString("SGR_NM");
            String s4 = taodataset.getString("SPRIZE_YN");
            String s5 = taodataset.getString("SBKPS_SDATE");
            String s6 = taodataset.getString("SBKPS_EDATE");
            String s7 = taodataset.getString("SEVNT_FROM");
            String s8 = taodataset.getString("SEVNT_TO");
            String s9 = getSelectQuery(s1, s2, s3, s4, s5, s6, s7, s8);
            int i = 0;
            preparedstatement = connection.prepareStatement(s9.toString());
            preparedstatement.setLong(++i, taodataset.getLong("RECORD_SIZE"));
            preparedstatement.setLong(++i, taodataset.getLong("RECORD_SIZE"));
            preparedstatement.setLong(++i, taodataset.getLong("PAGE_NO"));
            preparedstatement.setLong(++i, taodataset.getLong("RECORD_SIZE"));
            preparedstatement.setLong(++i, taodataset.getLong("PAGE_NO"));
            if(!GolfUtil.isNull(s2))
                if(s1.equals("ALL"))
                {
                    preparedstatement.setString(++i, "%" + s2 + "%");
                    preparedstatement.setString(++i, "%" + s2 + "%");
                } else
                {
                    preparedstatement.setString(++i, "%" + s2 + "%");
                }
            if(!GolfUtil.isNull(s3))
                preparedstatement.setString(++i, s3);
            if(!GolfUtil.isNull(s4))
                preparedstatement.setString(++i, s4);
            if(!GolfUtil.isNull(s5) && !GolfUtil.isNull(s6))
            {
                preparedstatement.setString(++i, s5);
                preparedstatement.setString(++i, s6);
            }
            if(!GolfUtil.isNull(s7) && !GolfUtil.isNull(s8))
            {
                preparedstatement.setString(++i, s8);
                preparedstatement.setString(++i, s7);
            }
            resultset = preparedstatement.executeQuery();
            if(resultset != null)
                for(; resultset.next(); dbtaoresult.addString("RESULT", "00"))
                {
                    dbtaoresult.addString("GR_NM", resultset.getString("GREEN_NM"));
                    dbtaoresult.addString("BKPS_DATE", resultset.getString("BOKG_ABLE_DATE"));
                    dbtaoresult.addString("BKPS_TIME", resultset.getString("BOKG_ABLE_TIME"));
                    dbtaoresult.addString("DIPY_BKPS_TIME", resultset.getString("DIPY_BOKG_ABLE_TIME"));
                    dbtaoresult.addString("ING_YN", resultset.getString("ING_YN"));
                    dbtaoresult.addString("HAN_NM", resultset.getString("HG_NM"));
                    dbtaoresult.addString("RECV_NO", resultset.getString("APLC_SEQ_NO"));
                    dbtaoresult.addString("PRIZE_YN", resultset.getString("PRZ_WIN_YN"));
                    dbtaoresult.addString("CSTMR_ID", resultset.getString("CDHD_ID"));
                    dbtaoresult.addString("HP", resultset.getString("HP"));
                    dbtaoresult.addString("REG_ATON", resultset.getString("REG_ATON"));
                    dbtaoresult.addString("TOTAL_CNT", resultset.getString("TOT_CNT"));
                    dbtaoresult.addString("CURR_PAGE", resultset.getString("PAGE"));
                    dbtaoresult.addString("LIST_NO", resultset.getString("LIST_NO"));
                    dbtaoresult.addString("RNUM", resultset.getString("RNUM"));
                }

            if(dbtaoresult.size() < 1)
                dbtaoresult.addString("RESULT", "01");
        }
        catch(Throwable throwable)
        {
            throw new BaseException(throwable);
        }
        finally
        {
            try
            {
                if(resultset != null)
                    resultset.close();
            }
            catch(Exception exception1) { }
            try
            {
                if(preparedstatement != null)
                    preparedstatement.close();
            }
            catch(Exception exception2) { }
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(Exception exception3) { }
        }
        return dbtaoresult;
    }

    private String getSelectQuery(String s, String s1, String s2, String s3, String s4, String s5, String s6, 
            String s7)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("\n SELECT\t*\t");
        stringbuffer.append("\n FROM (SELECT ROWNUM RNUM,\t");
        stringbuffer.append("\n \t\t\tGREEN_NM, BOKG_ABLE_DATE, BOKG_ABLE_TIME, DIPY_BOKG_ABLE_TIME, ING_YN,\t");
        stringbuffer.append("\n \t\t\tHG_NM, \t");
        stringbuffer.append("\n \t\t\tAPLC_SEQ_NO, PRZ_WIN_YN, CDHD_ID, HP, REG_ATON, \t");
        stringbuffer.append("\n \t\t\tCEIL(ROWNUM/?) AS PAGE,\t");
        stringbuffer.append("\n \t\t\tMAX(RNUM) OVER() TOT_CNT,\t");
        stringbuffer.append("\n \t\t\t(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  \t");
        stringbuffer.append("\n \t\t\tFROM (SELECT ROWNUM RNUM,\t");
        stringbuffer.append("\n \t\t\t\tTGE.GREEN_NM, TO_CHAR(TO_DATE(TGE.BOKG_ABLE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') BOKG_ABLE_DATE,   \t");
        stringbuffer.append("\n \t\t\t\tTGE.BOKG_ABLE_TIME, TO_CHAR (TO_DATE (TGE.BOKG_ABLE_TIME, 'HH24MI'), 'HH24:MI') DIPY_BOKG_ABLE_TIME,\t");
        stringbuffer.append("\n \t\t\t\tCASE WHEN TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN TGE.EVNT_STRT_DATE AND TGE.EVNT_END_DATE THEN '\uC9C4\uD589' ELSE '<FONT COLOR=RED>\uB9C8\uAC10</FONT>' END ING_YN,\t");
        stringbuffer.append("\n \t\t\t\tTGU.HG_NM,   \t");
        stringbuffer.append("\n \t\t\t\tTGR.APLC_SEQ_NO, DECODE(NVL(TGR.PRZ_WIN_YN,'N'),'Y','\uB2F9\uCCA8','N','\uBBF8\uB2F9\uCCA8') PRZ_WIN_YN, TGR.CDHD_ID, TGR.DDD_NO||TGR.TEL_HNO||TGR.TEL_SNO HP, TO_CHAR(TO_DATE(TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI:SS') REG_ATON   \t");
        stringbuffer.append("\n \t\t\t\tFROM \t");
        stringbuffer.append("\n \t\t\t\tBCDBA.TBGAPLCMGMT TGR, BCDBA.TBGEVNTMGMT TGE, BCDBA.TBGGOLFCDHD TGU\t");
        stringbuffer.append("\n \t\t\t\tWHERE TGR.LESN_SEQ_NO = TGE.EVNT_SEQ_NO \t");
        stringbuffer.append("\n \t\t\t\tAND TGR.CDHD_ID = TGU.CDHD_ID \t");
        stringbuffer.append("\n \t\t\t\tAND TGR.GOLF_SVC_APLC_CLSS = '0004' \t");
        if(!GolfUtil.isNull(s1))
            if(s.equals("ALL"))
            {
                stringbuffer.append("\n \t\t\tAND (TGU.HG_NM LIKE ?\t");
                stringbuffer.append("\n \t\t\tOR TGR.CDHD_ID LIKE ?)\t");
            } else
            {
                stringbuffer.append("\n \t\t\tAND " + s + " LIKE ?\t");
            }
        if(!GolfUtil.isNull(s2))
            stringbuffer.append("\n \t\t\t\tAND TGE.HG_NM = ?\t");
        if(!GolfUtil.isNull(s3))
            stringbuffer.append("\n \t\t\t\tAND TGR.EVNT_BNFT_EXPL = ?\t");
        if(!GolfUtil.isNull(s4) && !GolfUtil.isNull(s5))
            stringbuffer.append("\n \t\t\t\tAND TGE.BOKG_ABLE_DATE BETWEEN ? AND ?\t");
        if(!GolfUtil.isNull(s6) && !GolfUtil.isNull(s7))
        {
            stringbuffer.append("\n \t\t\t\tAND TGE.EVNT_STRT_DATE <= ?\t");
            stringbuffer.append("\n \t\t\t\tAND TGE.EVNT_END_DATE >=  ?\t");
        }
        stringbuffer.append("\n \t\t\t\tORDER BY TGR.APLC_SEQ_NO DESC\t");
        stringbuffer.append("\n \t\t\t)\t");
        stringbuffer.append("\n \tORDER BY RNUM\t");
        stringbuffer.append("\n \t)\t");
        return stringbuffer.toString();
    }

    public DbTaoResult execute2(WaContext wacontext, HttpServletRequest httpservletrequest, TaoDataSet taodataset)
        throws BaseException
    {
        String s = taodataset.getString("TITLE");
        ResultSet resultset = null;
        Connection connection = null;
        PreparedStatement preparedstatement = null;
        DbTaoResult dbtaoresult = new DbTaoResult(s);
        try
        {
            connection = wacontext.getDbConnection("default", null);
            String s1 = taodataset.getString("SEARCH_SEL");
            String s2 = taodataset.getString("SEARCH_WORD");
            String s3 = taodataset.getString("SGR_NM");
            String s4 = taodataset.getString("SPRIZE_YN");
            String s5 = taodataset.getString("SBKPS_SDATE");
            String s6 = taodataset.getString("SBKPS_EDATE");
            String s7 = taodataset.getString("LESN_SEQ_NO");
            String s8 = getSelectQuery2(s1, s2, s3, s4, s5, s6);
            int i = 0;
            preparedstatement = connection.prepareStatement(s8.toString());
            preparedstatement.setLong(++i, taodataset.getLong("RECORD_SIZE"));
            preparedstatement.setLong(++i, taodataset.getLong("RECORD_SIZE"));
            preparedstatement.setLong(++i, taodataset.getLong("PAGE_NO"));
            preparedstatement.setLong(++i, taodataset.getLong("RECORD_SIZE"));
            preparedstatement.setLong(++i, taodataset.getLong("PAGE_NO"));
            preparedstatement.setString(++i, taodataset.getString("LESN_SEQ_NO"));
            if(!GolfUtil.isNull(s2))
                if(s1.equals("ALL"))
                {
                    preparedstatement.setString(++i, "%" + s2 + "%");
                    preparedstatement.setString(++i, "%" + s2 + "%");
                } else
                {
                    preparedstatement.setString(++i, "%" + s2 + "%");
                }
            if(!GolfUtil.isNull(s3))
                preparedstatement.setString(++i, s3);
            if(!GolfUtil.isNull(s4))
                preparedstatement.setString(++i, s4);
            if(!GolfUtil.isNull(s5) && !GolfUtil.isNull(s6))
            {
                preparedstatement.setString(++i, s5);
                preparedstatement.setString(++i, s6);
            }
            resultset = preparedstatement.executeQuery();
            if(resultset != null)
                for(; resultset.next(); dbtaoresult.addString("RESULT", "00"))
                {
                    dbtaoresult.addString("GREEN_NM", resultset.getString("GREEN_NM"));
                    dbtaoresult.addString("TEOF_TIME", resultset.getString("TEOF_TIME"));
                    dbtaoresult.addString("TEOF_DATE", resultset.getString("TEOF_DATE"));
                    dbtaoresult.addString("PRZ_WIN_YN", resultset.getString("PRZ_WIN_YN"));
                    dbtaoresult.addString("CDHD_ID", resultset.getString("CDHD_ID"));
                    dbtaoresult.addString("CO_NM", resultset.getString("CO_NM"));
                    dbtaoresult.addString("EMAIL", resultset.getString("EMAIL"));
                    dbtaoresult.addString("HP_DDD_NO", resultset.getString("HP_DDD_NO"));
                    dbtaoresult.addString("HP_TEL_HNO", resultset.getString("HP_TEL_HNO"));
                    dbtaoresult.addString("HP_TEL_SNO", resultset.getString("HP_TEL_SNO"));
                    dbtaoresult.addString("MEMO_EXPL", resultset.getString("MEMO_EXPL"));
                    dbtaoresult.addString("REG_ATON", "'" + resultset.getString("REG_ATON") + "'");
                    dbtaoresult.addString("APLC_SEQ_NO", resultset.getString("APLC_SEQ_NO"));
                    dbtaoresult.addString("GRD", resultset.getString("GRD"));
                    dbtaoresult.addString("TOTAL_CNT", resultset.getString("TOT_CNT"));
                    dbtaoresult.addString("CURR_PAGE", resultset.getString("PAGE"));
                    dbtaoresult.addString("LIST_NO", resultset.getString("LIST_NO"));
                    dbtaoresult.addString("RNUM", resultset.getString("RNUM"));
                }

            if(dbtaoresult.size() < 1)
                dbtaoresult.addString("RESULT", "01");
        }
        catch(Throwable throwable)
        {
            throw new BaseException(throwable);
        }
        finally
        {
            try
            {
                if(resultset != null)
                    resultset.close();
            }
            catch(Exception exception1) { }
            try
            {
                if(preparedstatement != null)
                    preparedstatement.close();
            }
            catch(Exception exception2) { }
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(Exception exception3) { }
        }
        return dbtaoresult;
    }

    private String getSelectQuery2(String s, String s1, String s2, String s3, String s4, String s5)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("\n SELECT\t*\t");
        stringbuffer.append("\n FROM (SELECT ROWNUM RNUM, APLC_SEQ_NO,\t");
        stringbuffer.append("\n \t\t\tGREEN_NM, TEOF_TIME, TEOF_DATE, PRZ_WIN_YN, CDHD_ID,\t");
        stringbuffer.append("\n \t\t\tCO_NM, EMAIL, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, MEMO_EXPL, REG_ATON, DECODE(GRD ,'8','\uD654\uC774\uD2B8','7','\uACE8\uB4DC','6','\uBE14\uB8E8','5','\uCC54\uD53C\uC628','9','\uBE14\uB799') GRD , \t");
        stringbuffer.append("\n \t\t\tCEIL(ROWNUM/?) AS PAGE,\t");
        stringbuffer.append("\n \t\t\tMAX(RNUM) OVER() TOT_CNT,\t");
        stringbuffer.append("\n \t\t\t(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  \t");
        stringbuffer.append("\n \t\t\tFROM (SELECT ROWNUM RNUM,\t");
        stringbuffer.append("\n \t\t\t\tAPLC_SEQ_NO, CO_NM, GREEN_NM,  TO_NUMBER(SUBSTR(TEOF_TIME,0,2)) TEOF_TIME , TO_CHAR(TO_DATE(TEOF_DATE,'YYYYMMDD'),'YYYY-MM-DD') TEOF_DATE,   \t");
        stringbuffer.append("\n \t\t\t\tDECODE(NVL(PRZ_WIN_YN,'N'),'Y','\uB2F9\uCCA8','N','\uBBF8\uB2F9\uCCA8') PRZ_WIN_YN,  CDHD_ID, EMAIL ,\t");
        stringbuffer.append("\n \t\t\t\tHP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, MEMO_EXPL, REG_ATON ,\t");
        stringbuffer.append("\n \t\t\t\t(SELECT MIN(CDHD_CTGO_SEQ_NO)  FROM  BCDBA.TBGGOLFCDHDGRDMGMT  WHERE  CDHD_ID = A.CDHD_ID)   GRD\t");
        stringbuffer.append("\n \t\t\t\tFROM \t");
        stringbuffer.append("\n \t\t\t\tBCDBA.TBGAPLCMGMT A\t");
        stringbuffer.append("\n \t\t\t\tWHERE GOLF_SVC_APLC_CLSS = '0004' \t");
        stringbuffer.append("\n \t\t\t\tAND LESN_SEQ_NO = ? \t");
        if(!GolfUtil.isNull(s1))
            if(s.equals("ALL"))
            {
                stringbuffer.append("\n \t\t\tAND (CO_NM LIKE ?\t");
                stringbuffer.append("\n \t\t\tOR CDHD_ID LIKE ? )\t");
            } else
            {
                stringbuffer.append("\n \t\t\tAND " + s + " LIKE ?\t");
            }
        if(!GolfUtil.isNull(s2))
            stringbuffer.append("\n \t\t\t\tAND GREEN_NM = ?\t");
        if(!GolfUtil.isNull(s3))
            stringbuffer.append("\n \t\t\t\tAND PRZ_WIN_YN = ?\t");
        if(!GolfUtil.isNull(s4) && !GolfUtil.isNull(s5))
            stringbuffer.append("\n \t\t\t\tAND TEOF_DATE BETWEEN ? AND ?\t");
        stringbuffer.append("\n \t\t\t\tORDER BY TEOF_DATE DESC\t");
        stringbuffer.append("\n \t\t\t)\t");
        stringbuffer.append("\n \tORDER BY RNUM\t");
        stringbuffer.append("\n \t)\t");
        return stringbuffer.toString();
    }
}