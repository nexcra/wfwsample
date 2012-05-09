/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : DbTao 처리시 결과를 반환하기 위한 클래스
* 수정 :
* 내용 :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;


/******************************************************************************
 * DbTao 처리시 결과를 반환하기 위한 클래스.
 * @author 조용국(ykcho@e4net.net)
 * @version 2007.01.04
 *****************************************************************************/
public class DbTaoResult implements TaoResult,Serializable {
    /** 데이터 저장소        */  private Map map;
    /** 데이터 인덱스        */  private int index;
    /** 마지막 데이터 인덱스 */  private int cnt;
    /** 메시지 제목          */  private final String TITLE;

    /** ***********************************************************************
    * DbTao 처리시 결과를 반환하기 위한 클래스.
    ************************************************************************ */
    public DbTaoResult(String title) {
        this.map = new HashMap();
        this.index = 0;
        this.cnt = 0;
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
     * 최대 size 리셋.
     ***************************************************************** */
    private void resetCount() {
        for(Iterator it = this.map.keySet().iterator(); it.hasNext(); ) {
            ArrayList list = (ArrayList) this.map.get( it.next() );
            int size = list.size();
            if ( size > this.cnt ) {
                this.cnt = size;
            }
        }
    }

    /**************************************************************************
    * Object 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addObject(String field, Object value) throws TaoException {
        ArrayList list = (ArrayList)this.map.get(field.trim());
        if ( list == null ) {
            list = new ArrayList();
            this.map.put(field.trim(),list);
        }
        list.add(value);
        resetCount();
    }

    /**************************************************************************
    * String 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addString(String field, String value) throws TaoException {
        if ( value == null ) {
            addObject(field,"");
        /**
        } else if ( "null".equalsIgnoreCase(value) ) {
            addObject(field,"");
        */
        } else {
            addObject(field,value);
        }
    }

    /**************************************************************************
    * int 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addInt(String field, int value) throws TaoException {
        addObject(field,new Integer(value));
    }

    /**************************************************************************
    * long 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addLong(String field, long value) throws TaoException {
        addObject(field,new Long(value));
    }

    /**************************************************************************
    * short 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addShort(String field, short value) throws TaoException {
        addObject(field,new Short(value));
    }

    /**************************************************************************
    * float 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addFloat(String field, float value) throws TaoException {
        addObject(field,new Float(value));
    }

    /**************************************************************************
    * double 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addDouble(String field, double value) throws TaoException {
        addObject(field,new Double(value));
    }

    /**************************************************************************
    * boolean 데이터 추가.
    * @ param field 필드명
    * @ param value 입력데이터
    **************************************************************************/
    public void addBoolean(String field, boolean value) throws TaoException {
        addObject(field,new Boolean(value));
    }

    /** *****************************************************************
     * 첫 레코드로 이동.
     ***************************************************************** */
    public void first() throws TaoException {
        this.index = 0;
    }

    /** *****************************************************************
     * 마지막 레코드로 이동.
     ***************************************************************** */
    public void last() throws TaoException {
        if ( this.cnt > 0 ) {
            this.index = this.cnt;
        }
    }

    /** *****************************************************************
     * 다음 레코드로 이동.
     ***************************************************************** */
    public void next() throws TaoException {
        if ( this.cnt > 0 && this.index < this.cnt ) {
            this.index++;
        } else {
            throw errorHandler("next fail!",null);
        }
    }

    /** *****************************************************************
     * 이전 레코드로 이동.
     ***************************************************************** */
    public void previous() throws TaoException {
        if ( this.index > 0 ) {
            this.index--;
        } else {
            throw errorHandler("previous fail!",null);
        }
    }

    /** *****************************************************************
     * 처음 레코드인지 체크.
     * @return boolean
     ***************************************************************** */
    public boolean isFirst() throws TaoException {
        return ( this.index == 0 );
    }

    /** *****************************************************************
     * 마지막 레코드인지 체크.
     * @return boolean
     ***************************************************************** */
    public boolean isLast() throws TaoException {
        return ( this.index == this.cnt );
    }

    /** *****************************************************************
     * 다음 레코드가 있는지 체크.
     * @return boolean
     ***************************************************************** */
    public boolean isNext() throws TaoException {
        return ( this.cnt > 0 && this.index < this.cnt );
    }

    /** *****************************************************************
     * 이전 레코드가 있는지 체크.
     * @return boolean
     ***************************************************************** */
    public boolean isPrevious() throws TaoException {
        return ( this.index > 0 );
    }

    /** *****************************************************************
     * 필드가 있는지 체크.
     * @return boolean
     ***************************************************************** */
    public boolean containsKey(String field) {
        return ( this.map.containsKey(field) );
    }

    /** *****************************************************************
     * 레코드 수량.
     * @return int
     ***************************************************************** */
    public int size() throws TaoException {
        return this.cnt;
    }

    /** *****************************************************************
     * Object 반환.
     * @param field 필드명
     * @return Object
     ***************************************************************** */
    public Object getObject(String field) throws TaoException {
        if ( this.index == 0 ) throw errorHandler("please run TaoResult.next()",null);
        try {
            ArrayList list = (ArrayList) this.map.get(field);
            if ( this.index <= list.size() ) {
                return list.get(this.index-1);
            } else {
                return list.get(list.size()-1);
            }
        } catch ( Throwable t ) {
            throw errorHandler("field \"" + field + "\" is not found!",t);
        }
    }

    /** *****************************************************************
     * String 반환.
     * @param field 필드명
     * @return String
     ***************************************************************** */
    public String getString(String field) throws TaoException {
        Object value = getObject(field);
        try {
            return (String) value;
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not String!",t);
        }
    }

    /** *****************************************************************
     * int 반환.
     * @param field 필드명
     * @return int
     ***************************************************************** */
    public int getInt(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.intValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not int!",t);
        }
    }

    /** *****************************************************************
     * long 반환.
     * @param field 필드명
     * @return long
     ***************************************************************** */
    public long getLong(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.longValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not long!",t);
        }
    }

    /** *****************************************************************
     * short 반환.
     * @param field 필드명
     * @return short
     ***************************************************************** */
    public short getShort(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.shortValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not short!",t);
        }
    }

    /** *****************************************************************
     * float 반환.
     * @param field 필드명
     * @return float
     ***************************************************************** */
    public float getFloat(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.floatValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not float!",t);
        }
    }

    /** *****************************************************************
     * double 반환.
     * @param field 필드명
     * @return double
     ***************************************************************** */
    public double getDouble(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Number num = (Number) value;
            return num.doubleValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not double!",t);
        }
    }

    /** *****************************************************************
     * boolean 반환.
     * @param field 필드명
     * @return boolean
     ***************************************************************** */
    public boolean getBoolean(String field) throws TaoException {
        Object value = getObject(field);
        try {
            Boolean num = (Boolean) value;
            return num.booleanValue();
        } catch( Throwable t) {
            throw errorHandler("field \"" + field + "\" is not boolean!",t);
        }
    }

    /** *****************************************************************
     * 필드에 들어있는 Object 반환(레코드수에 상관없이).
     * @param field 필드명
     * @return Object
     ***************************************************************** */
    public Object getField(String field) throws TaoException {
        return this.map.get(field);
    }

    /** *****************************************************************
     * 필드명 Set 리턴.
     * @return 필드명 Set
     ***************************************************************** */
    public Set getFieldSet() throws TaoException {
        return this.map.keySet();
    }

}
