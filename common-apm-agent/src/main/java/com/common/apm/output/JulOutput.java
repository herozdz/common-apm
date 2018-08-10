package com.common.apm.output;

import com.common.apm.IOutput;
import com.common.apm.common.Logs;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-3
 * Time: 下午5:28
 * To change this template use File | Settings | File Templates.
 */
public class JulOutput implements IOutput {
    private static Logger logger = Logger.getLogger(JulOutput.class.getName());

    @Override
    public boolean out(Object value,String logPath) {
        Logs.info(value.toString(),logPath);
        return true;
    }
}
