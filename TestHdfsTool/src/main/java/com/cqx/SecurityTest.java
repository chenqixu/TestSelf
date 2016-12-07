package com.cqx;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.security.JniBasedUnixGroupsMapping;

public class SecurityTest {
	public static void main(String[] args) {
		org.apache.hadoop.security.JniBasedUnixGroupsMapping test1 = null;
		test1 = new JniBasedUnixGroupsMapping();
		List<String> result = null;
		try {
			result = test1.getGroups("hadoop");
			for(String str : result){
				System.out.println(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
