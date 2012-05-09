/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : 웹로직 풀에서 데이터베이스 커넥션을 가져온다.
* 수정 :
* 내용 : 
******************************************************************************/
package com.bccard.golf.dbtao;

import java.sql.Connection;
import java.util.Properties;

import com.bccard.waf.dao.DbConnectionFactory;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoConnectionFactory;
import com.bccard.waf.tao.TaoException;

/******************************************************************************
* 데이터베이스에 연동.
* @author 조용국
* @version 2007.01.04
******************************************************************************/
public class DbTaoLogConnectionFactory extends TaoConnectionFactory {

    /** ***********************************************************************
    * 메시지를 통한 예외.
    * @param s 메시지
    ************************************************************************ */
	public TaoConnection getConnection(Properties prop) throws TaoException {
        try {
        	DbConnectionFactory factory = DbConnectionFactory.createFactory("com.bccard.golf.factory.GolfDbConnectionFactory");
	    	Connection con = factory.getConnection(null);
            DbTaoConnection taoCon = new DbTaoConnection(con);
            return taoCon;
		} catch(Throwable t) {
			
			throw new DbTaoException("Tao Connection Fail.",t);
		}
    }

}

