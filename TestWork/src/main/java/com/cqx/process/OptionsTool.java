package com.cqx.process;

import com.cqx.bean.CmdBean;
import org.apache.commons.cli.*;

/**
 * 解析参数
 *
 * @author chenqixu
 */
public class OptionsTool {

    private CmdBean cmdBean;

    private OptionsTool() {
    }

    public static OptionsTool newbuilder() {
        return new OptionsTool();
    }

    public CmdBean getCmdBean() {
        return cmdBean;
    }

    public OptionsTool parser(String[] args) throws ParseException {
        Options options = new Options();
        Option option;
        //-t type
        option = new Option("t", "type", true, "type");
        option.setRequired(true);
        options.addOption(option);
        //-u username
        option = new Option("u", "username", true, "username");
        option.setRequired(true);
        options.addOption(option);
        //-p password
        option = new Option("p", "password", true, "password");
        option.setRequired(true);
        options.addOption(option);
        //-d dns
        option = new Option("d", "dns", true, "dns");
        option.setRequired(true);
        options.addOption(option);
        //-l loglevel
        option = new Option("l", "loglevel", true, "loglevel");
        option.setRequired(false);
        options.addOption(option);
        //parser
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(options, args);
        //getOptionValue
        cmdBean = CmdBean.newbuilder()
                .setType(commandLine.getOptionValue('t'))
                .setUsername(commandLine.getOptionValue('u'))
                .setPassword(commandLine.getOptionValue('p'))
                .setDns(commandLine.getOptionValue('d'));
        if (commandLine.getOptionValue('l') != null) cmdBean.setLoglevel(commandLine.getOptionValue('l'));
        return this;
    }
}
