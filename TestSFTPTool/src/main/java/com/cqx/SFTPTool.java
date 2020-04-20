package com.cqx;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.jcraft.jsch.ChannelSftp;

public class SFTPTool {
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		// -u username
		Option option = new Option("u", "username", true, "username");
		option.setRequired(true);
		options.addOption(option);
		// -p password
		option = new Option("p", "password", true, "password");
		option.setRequired(true);
		options.addOption(option);
		// -h host
		option = new Option("h", "host", true, "host");
		option.setRequired(true);
		options.addOption(option);
		// -l localpath
		option = new Option("l", "localpath", true, "localpath");
		option.setRequired(true);
		options.addOption(option);
		// -r remotepath
		option = new Option("r", "remotepath", true, "remotepath");
		option.setRequired(true);
		options.addOption(option);
		// -f filename
		option = new Option("f", "filename", true, "filename");
		option.setRequired(true);
		options.addOption(option);
		// parser
		CommandLineParser parser = new GnuParser();
		CommandLine commandLine = parser.parse(options, args);
		// getOptionValue
		String u = commandLine.getOptionValue('u');
		String p = commandLine.getOptionValue('p');
		String h = commandLine.getOptionValue('h');
		String l = commandLine.getOptionValue('l');
		String r = commandLine.getOptionValue('r');
		String f = commandLine.getOptionValue('f');

		SFtpUtils sFtpUtil = new SFtpUtils();
		ChannelSftp chSftp = sFtpUtil.getChannel(u, h, 22, p);
		if (chSftp != null) {
			sFtpUtil.dowload(chSftp, l, r, f);
		}
		// 关闭sftp连接
		sFtpUtil.closeSftpConnection();
	}
}
