/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : ������ Ǯ���� �����ͺ��̽� Ŀ�ؼ�(����DB)�� �����´�.
* ���� :
* ���� :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.sql.Connection;
import java.util.Properties;

import com.bccard.waf.dao.DbConnectionFactory;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoConnectionFactory;
import com.bccard.waf.tao.TaoException;

/******************************************************************************
* �����ͺ��̽��� ����.
* @author ���뱹
* @version 2007.01.04
******************************************************************************/
public class DbTaoLogPurcConnectionFactory extends TaoConnectionFactory {

    /** ***********************************************************************
    * �����ͺ��̽��� ������ �����Ѵ�.
    * @param con Connection
    * @return DbTaoConnection
    ************************************************************************ */
	public TaoConnection getConnection(Properties prop) throws TaoException {
        try {
   		DbConnectionFactory factory = DbConnectionFactory.createFactory("com.bccard.golf.common.DbTaoLogConnectionFactory");
	    	Connection con = factory.getConnection(null);
            DbTaoConnection taoCon = new DbTaoConnection(con);
            return taoCon;
		} catch(Throwable t) {
			throw new DbTaoException("Tao Purc Connection Fail.",t);
		}
    }

}

