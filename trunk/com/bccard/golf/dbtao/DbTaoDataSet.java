/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 :  2004.08.02 [조용국(ykcho@e4net.net)]
* 내용 : DbTao 처리시 조건을 넘겨주기 위한 클래스
* 적용범위  : welco
* 작성일자  : 2007.01.04
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;


/******************************************************************************
 * DbTao 처리시 조건을 넘겨주기 위한 클래스.
 * @author 조용국
* 작성일자  : 2007.01.04
 **************************************************************************** */
public class DbTaoDataSet implements TaoDataSet,Serializable {
    /** 데이터 저장소 */ private Map map;
    /** 메시지 제목   */ private final String TITLE;

    /** ***********************************************************************
    * DbTao 처리시 조건을 넘겨주기 위한 클래스.
    ************************************************************************ */
    public DbTaoDataSet(String title) {
        this.map = new HashMap();
        this.TITLE = title;
    }

    /** ***********************************************************************
    * 예외를 발생하기 위한 메서드.
    * @ param msg 예외메시지
    * @ param t 예외가 발생한 원본 Throwable
    ************************************************************************ */
    protected DbTaoException errorHandler(String msg,Throwable t) {
        MsgEtt msgEtt = new MsgEtt();
        msgEtt.setType( MsgEtt.TYPE_ERROR );
        msgEtt.setTitle(this.TITLE);
        msgEtt.setMessage( msg );
        if ( t == null ) {
            return new DbTaoException(msgEtt);
        } else {
            return new DbTaoException(msgEtt,t);
        }
    }

    /** *****************************************************************
     * 필드명 Set 리턴.
     * @return 필드명 Set
     ***************************************************************** */
    public Set getFieldSet() throws TaoException {
        return this.map.keySet();
    }

    /** *****************************************************************
     * Object 반환.
     * @return Object
     ***************************************************************** */
    public Object getObject(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            return this.map.get(field);
        } else {
            return null;
        }
    }

    /** *****************************************************************
     * String 반환.
     * @return String
     ***************************************************************** */
    public String getString(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                return (String) this.map.get(field);
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not String!",t);
            }
        } else {
            return null;
        }
    }

    /** *****************************************************************
     * int 반환.
     * @return int
     ***************************************************************** */
    public int getInt(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.intValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not int!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * long 반환.
     * @return long
     ***************************************************************** */
    public long getLong(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.longValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not long!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * short 반환.
     * @return short
     ***************************************************************** */
    public short getShort(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.shortValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not short!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * float 반환.
     * @return float
     ***************************************************************** */
    public float getFloat(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.floatValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not float!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * double 반환.
     * @return double
     ***************************************************************** */
    public double getDouble(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Number value = (Number)this.map.get(field);
                return value.doubleValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not double!",t);
            }
        } else {
            return 0;
        }
    }

    /** *****************************************************************
     * boolean 반환.
     * @return boolean
     ***************************************************************** */
    public boolean getBoolean(String field) throws TaoException {
        if ( this.map.containsKey(field) ) {
            try {
                Boolean value = (Boolean)this.map.get(field);
                return value.booleanValue();
            } catch (Throwable t) {
                throw errorHandler("field \"" + field + "\" is not boolean!",t);
            }
        } else {
            return false;
        }
    }

    /** *****************************************************************
     * Object 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setObject(String field, Object value) throws TaoException {
        this.map.put(field,value);
    }

    /** *****************************************************************
     * Object 입력.
     * @param field 필드명
     * @param ins_list 필드값
     ***************************************************************** */
    public void setString(String field, ArrayList ins_list) throws TaoException {
        this.map.put(field,ins_list);
    }
    /** *****************************************************************
     * Object 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setString(String field, String value) throws TaoException {
        this.map.put(field,value);
    }

    /** *****************************************************************
     * int 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setInt(String field,int value) throws TaoException {
        this.map.put(field, new Integer(value) );
    }

    /** *****************************************************************
     * long 입력.
     * @param field 필드명
     * @param del_list 필드값
     ***************************************************************** */
    public void setLong(String field, long del_list) throws TaoException {
        this.map.put(field, new Long(del_list) );
    }

    /** *****************************************************************
     * short 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setShort(String field, short value) throws TaoException {
        this.map.put(field, new Short(value) );
    }

    /** *****************************************************************
     * float 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setFloat(String field, float value) throws TaoException {
        this.map.put(field, new Float(value) );
    }

    /** *****************************************************************
     * double 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setDouble(String field, double value) throws TaoException {
        this.map.put(field, new Double(value) );
    }

    /** *****************************************************************
     * boolean 입력.
     * @param field 필드명
     * @param value 필드값
     ***************************************************************** */
    public void setBoolean(String field, boolean value) throws TaoException {
        this.map.put(field, new Boolean(value) );
    }
}
