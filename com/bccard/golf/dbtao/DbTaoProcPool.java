/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : DbTaoProc 객체들을 담는 Pool을 제어하는 클래스
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.dbtao;
 
import java.util.Map;
import java.util.HashMap;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * DbTao 처리를 위한 슈퍼 클래스.
 * @author 조용국(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
public abstract class DbTaoProcPool {
    /** 프로세스를 모아놓은 패키지 */ 
	public static final String PACKAGE = "com.bccard.golf.dbtao.proc";

    /** 인스턴스 저장 Map          */ 
	static Map map = new HashMap();
 
    /** ***********************************************************************
    * Pool 초기화.
    ************************************************************************ */
    public static void reset() {
        map = new HashMap();
    }

    /** ***********************************************************************
    * Proc 실행.
    * @param clazz 클래스명(패키지제외한)
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
                msgEtt.setTitle("비즈니스로직");
                msgEtt.setMessage(clazz + " 를 찾을 수 없습니다.");
                throw new DbTaoException(msgEtt,t);
            }
        }
        return proc;
    }

}

