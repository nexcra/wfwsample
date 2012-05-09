/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : DbTaoProc ��ü���� ��� Pool�� �����ϴ� Ŭ����
* ���� : 
* ���� : 
******************************************************************************/
package com.bccard.golf.dbtao;
 
import java.util.Map;
import java.util.HashMap;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * DbTao ó���� ���� ���� Ŭ����.
 * @author ���뱹(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
public abstract class DbTaoProcPool {
    /** ���μ����� ��Ƴ��� ��Ű�� */ 
	public static final String PACKAGE = "com.bccard.golf.dbtao.proc";

    /** �ν��Ͻ� ���� Map          */ 
	static Map map = new HashMap();
 
    /** ***********************************************************************
    * Pool �ʱ�ȭ.
    ************************************************************************ */
    public static void reset() {
        map = new HashMap();
    }

    /** ***********************************************************************
    * Proc ����.
    * @param clazz Ŭ������(��Ű��������)
    * @return DbTaoProc
    ************************************************************************ */
    public static DbTaoProc getProc(String clazz) throws DbTaoException {
        DbTaoProc proc = null;
        if ( map.containsKey(clazz) ) {
            try {
                proc = (DbTaoProc) map.get(clazz);
            } catch(Throwable t) {
                proc = null;
            }
        }
        if ( proc == null ) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) classLoader = DbTaoProcPool.class.getClassLoader();
                Class mclazz = classLoader.loadClass( PACKAGE + "." + clazz );
                Object object = mclazz.newInstance();
                proc = (DbTaoProc) object;       
         } catch(Throwable t) {
           		
                MsgEtt msgEtt = new MsgEtt();
                msgEtt.setType( MsgEtt.TYPE_ERROR );
                msgEtt.setTitle("����Ͻ�����");
                msgEtt.setMessage(clazz + " �� ã�� �� �����ϴ�.");
                throw new DbTaoException(msgEtt,t);
            }
        }
        return proc;
    }

}

