package com.cqx.common.option;

import com.cqx.common.option.CmdOpImpl.CmdIOp;
import org.apache.commons.cli.*;

import java.lang.reflect.Method;

/**
 * 命令参数工具类
 *
 * @author chenqixu
 */
public class OptionsTool {
    public <T> T parser(String[] args, Class<? extends T> cls) throws Exception {
        Options options = new Options();
        Option option;
        T obj = null;
        CmdOpImpl cmdOp = cls.getAnnotation(CmdOpImpl.class);
        if (cmdOp != null) {
            obj = cls.newInstance();
            for (Method m : cls.getDeclaredMethods()) {
                CmdIOp cmdIOp = m.getAnnotation(CmdIOp.class);
                if (cmdIOp != null) {
                    option = new Option(cmdIOp.opt(), cmdIOp.longOpt(), cmdIOp.hasArg(), cmdIOp.description());
                    option.setRequired(cmdIOp.required());
                    options.addOption(option);
                }
            }
            //parser
            CommandLineParser parser = new GnuParser();
            CommandLine commandLine = parser.parse(options, args);
            for (Method m : cls.getDeclaredMethods()) {
                CmdIOp cmdIOp = m.getAnnotation(CmdIOp.class);
                if (cmdIOp != null) {
                    if (cmdIOp.required()) {
                        m.invoke(obj, commandLine.getOptionValue(cmdIOp.opt()));
                    } else {
                        if (commandLine.getOptionValue(cmdIOp.opt()) != null) {
                            m.invoke(obj, (Object) commandLine.getOptionValue(cmdIOp.opt()));
                        }
                    }
                }
            }
        }
        return obj;
    }
}
