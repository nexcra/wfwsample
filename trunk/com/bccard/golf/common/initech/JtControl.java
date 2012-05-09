package com.bccard.golf.common.initech;


import com.bccard.waf.common.BaseException;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.jolt.JoltOutput;

public interface JtControl
{

    public abstract JoltOutput getJtOutput();

    public abstract void commit()
        throws BaseException;

    public abstract void rollback()
        throws BaseException;

    public abstract void close()
        throws TaoException;

    
//    {
//        throw new Error("Unresolved compilation problem: \n\tThe declared package \"com.bccard.common\" does not match the expected package \"common\"\n");
//    }
}
