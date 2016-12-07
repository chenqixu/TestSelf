package com.newland.bi.bigdata.ant;

public class CompilerXML {
	
	/**
	 * <b>默认执行target</b>
	 * <br><b>清理</b> clean
	 * <br><b>过滤</b> filter_project
	 * <br><b>编译,打包</b> compile_XX
	 * <br><b>增量拷贝新增lib</b> copy_lib_XX
	 * <br><b>清理过滤项目时生成的临时文件</b> clean_list_project
	 * */
	public String targetAll(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 默认执行target -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<target name=\"all\">");
		tmp.append(ConstantsAnt.tab);
		// 清理编译目标路径
		tmp.append("<!-- 清理编译目标路径 -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<antcall target=\"clean\" />");
		tmp.append(ConstantsAnt.tab);
		// 根据检出清单过滤编译项目
		tmp.append("<!-- filter project in history list -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<antcall target=\"filter_project\" />");
		tmp.append(ConstantsAnt.tab);
		// 全量编译,打包
		tmp.append("<!-- 全量编译,打包 -->");
		tmp.append(ConstantsAnt.tab);
		// jar工程
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<antcall target=\"compile_"+ConstantsAnt.getProgramelistInstance().get(i)+"\" />");
			tmp.append(ConstantsAnt.tab);
		}
		// war工程
		for(int i=0;i<ConstantsAnt.getWebProgramelistInstance().size();i++){
			tmp.append("<antcall target=\"compile_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"\" />");
			tmp.append(ConstantsAnt.tab);
		}		
		// 增量拷贝新增lib
		tmp.append("<!-- 增量拷贝新增lib -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<antcall target=\"copy_lib_"+ConstantsAnt.getProgramelistInstance().get(i)+"\" />");
			tmp.append(ConstantsAnt.tab);
		}
		// 压缩jar和lib到一个文件
		tmp.append("<!-- 压缩jar和lib到一个文件 -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){		
			tmp.append("<antcall target=\"zip_jar_lib_"+ConstantsAnt.getProgramelistInstance().get(i)+"\"  />");
			tmp.append(ConstantsAnt.tab);
		}
		// 清理过滤项目时生成的临时文件
		tmp.append("<!-- 清理过滤项目时生成的临时文件 -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<antcall target=\"clean_list_project\" />");
		tmp.append(ConstantsAnt.tab);
		tmp.append("</target>");
		tmp.append(ConstantsAnt.tab);
		return tmp.toString();
	}
	
	/**
	 * 清理编译目标路径
	 * */
	public String targetClean(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 清理编译目标路径 -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<target name=\"clean\">");
		tmp.append(ConstantsAnt.tab);
		// 删除全量编译目标路径
		tmp.append("<!-- 删除全量编译目标路径 -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<delete dir=\"${edcbigdatadir_dist}\" />");
		tmp.append(ConstantsAnt.tab);
		// 删除编译生成的全量或增量包
		tmp.append("<!-- 删除编译生成的全量或增量包 -->");
		tmp.append(ConstantsAnt.tab);
		// 删除jar工程的zip包(含lib和jar)
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<delete file=\"${output.dir}/${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_zip}\" />");
			tmp.append(ConstantsAnt.tab);
		}
		// 删除web工程的war包
		for(int i=0;i<ConstantsAnt.getWebProgramelistInstance().size();i++){
			tmp.append("<delete file=\"${output.dir}/${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_war}\" />");
			tmp.append(ConstantsAnt.tab);
		}
		// 创建全量编译目标路径,编译路径,拷贝lib路径,jar打包路径
		tmp.append("<!-- 创建全量编译目标路径,编译路径,拷贝lib路径,jar打包路径 -->");
		tmp.append(ConstantsAnt.tab);
		// 编译路径
		tmp.append("<!-- 编译路径 -->");
		tmp.append(ConstantsAnt.tab);
		// jar工程
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${compile_all}\" />");
			tmp.append(ConstantsAnt.tab);
		}
		// war工程
		tmp.append("<!-- web工程 -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getWebProgramelistInstance().size();i++){
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}/${war.config.dir}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}/${war.classes.dir}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}/${lib.dir}\" />");
			tmp.append(ConstantsAnt.tab);
		}		
		// 拷贝lib路径
		tmp.append("<!-- 拷贝lib路径 -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${lib.dir}\" />");
			tmp.append(ConstantsAnt.tab);
		}
		// jar打包路径
		tmp.append("<!-- jar打包路径 -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<mkdir dir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${jar.dir}\" />");
			tmp.append(ConstantsAnt.tab);
		}
		tmp.append("</target>");
		tmp.append(ConstantsAnt.tab);
		return tmp.toString();
	}
	
	/**
	 * 通过运行shell脚本过滤历史清单中的项目，生成项目名临时文件
	 * */
	public String targetFilter_project(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 通过运行shell脚本过滤历史清单中的项目，生成项目名临时文件 -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<target name=\"filter_project\">");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<exec executable=\"./filterProjectEdcbigdata.sh\">");
		tmp.append(ConstantsAnt.tab);
		tmp.append("</exec>");
		tmp.append(ConstantsAnt.tab);
		tmp.append("</target>");
		tmp.append(ConstantsAnt.tab);
		return tmp.toString();
	}
	
	/**
	 * 清理过滤项目时生成的临时文件
	 * */
	public String targetClean_list_project(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 清理过滤项目时生成的临时文件 -->");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<target name=\"clean_list_project\">");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<delete>");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<fileset dir=\"./\">");
		tmp.append(ConstantsAnt.tab);
		tmp.append("<include name=\"*.lst_project\"/>");
		tmp.append(ConstantsAnt.tab);
		tmp.append("</fileset>");
		tmp.append(ConstantsAnt.tab);
		tmp.append("</delete>");
		tmp.append(ConstantsAnt.tab);
		tmp.append("</target>");
		tmp.append(ConstantsAnt.tab);
		return tmp.toString();
	}
	
	/**
	 * 通过临时文件是否存在来判断项目是否需要编译
	 * */
	public String conditionExistProject(){
		StringBuffer tmp = new StringBuffer();
		// 通过临时文件是否存在来判断项目是否需要编译
		tmp.append("<!-- 通过临时文件是否存在来判断项目是否需要编译 -->");
		tmp.append(ConstantsAnt.tab);
		// jar工程
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<condition property=\"exist."+ConstantsAnt.getProgramelistInstance().get(i)+".project\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<and>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<available file=\"edc-bigdata-"+ConstantsAnt.getProgramelistInstance().get(i)+".lst_project\" filepath=\"./\"/>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</and>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</condition>");
			tmp.append(ConstantsAnt.tab);
		}
		// war工程
		for(int i=0;i<ConstantsAnt.getWebProgramelistInstance().size();i++){
			tmp.append("<condition property=\"exist."+ConstantsAnt.getWebProgramelistInstance().get(i)+".project\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<and>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<available file=\""+ConstantsAnt.getWebProgramelistInstance().get(i)+".lst_project\" filepath=\"./\"/>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</and>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</condition>");
			tmp.append(ConstantsAnt.tab);
		}
		return tmp.toString();
	}
	
	/**
	 * 判断是否有新增lib
	 * */
	public String conditionExistLib(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 判断是否有新增lib -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<condition property=\"exist."+ConstantsAnt.getProgramelistInstance().get(i)+".lib\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<and>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<available file=\"${cvsworkdir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_path}/${lib.dir}\" type=\"dir\"/>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</and>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</condition>");
			tmp.append(ConstantsAnt.tab);
		}
		return tmp.toString();
	}
	
	/**
	 * 全量编译,打包
	 * */
	public String targetCompile(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 全量编译,打包 -->");
		tmp.append(ConstantsAnt.tab);
		// jar工程
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<target name=\"compile_"+ConstantsAnt.getProgramelistInstance().get(i)+"\" if=\"exist."+ConstantsAnt.getProgramelistInstance().get(i)+".project\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- lib依赖全量 -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<path id=\"classpathdef\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<fileset dir=\"${cvsworkdir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${lib.dir}\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<include name=\"**/*.jar\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</fileset>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</path>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- javac编译到目标路径compile -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<javac classpathref=\"classpathdef\" srcdir=\"${cvsworkdir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${src.dir}\"");
			tmp.append(" destdir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${compile_all}\"");
			String jdk = ConstantsAnt.getJdkByName(ConstantsAnt.getProgramelistInstance().get(i));
			if(jdk!=null && !jdk.toLowerCase().equals("null") && jdk.trim().length()>0){
			}else{
				jdk =  "${jdk.path}";
			}
			tmp.append(" fork=\"true\" debug=\"true\" executable=\""+jdk+"\" memorymaximumsize=\"512M\" includeantruntime=\"on\"");
			tmp.append(" encoding=\"${build.encode}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- 打包成jar到目标路径jar -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<jar destfile=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${jar.dir}/${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_jar}\"");
			tmp.append(" basedir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${compile_all}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</target>");
			tmp.append(ConstantsAnt.tab);
		}
		// war工程
		for(int i=0;i<ConstantsAnt.getWebProgramelistInstance().size();i++){
			tmp.append("<target name=\"compile_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"\" if=\"exist."+ConstantsAnt.getWebProgramelistInstance().get(i)+".project\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- lib依赖全量 -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<path id=\"classpathdef\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<fileset dir=\"${cvsworkdir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${lib.dir}\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<include name=\"**/*.jar\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</fileset>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</path>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- javac编译到目标路径classes -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<javac classpathref=\"classpathdef\" srcdir=\"${cvsworkdir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${src.dir}\" ");
			tmp.append(" destdir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}/${war.classes.dir}\" ");
			String jdk = ConstantsAnt.getJdkByName(ConstantsAnt.getProgramelistInstance().get(i));
			if(jdk!=null && !jdk.toLowerCase().equals("null") && jdk.trim().length()>0){
			}else{
				jdk =  "${jdk.path}";
			}
			tmp.append(" fork=\"true\" debug=\"true\" executable=\""+jdk+"\" memorymaximumsize=\"512M\" includeantruntime=\"on\" encoding=\"${build.encode}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- copy configure files -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<copy todir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}/${war.classes.dir}\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<fileset dir=\"${cvsworkdir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${resources.dir}\" includes=\"**/*.*\"/>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</copy>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- 把所有lib文件copy到编译目录 -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<copy todir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webinf.dir}/${lib.dir}\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<fileset dir=\"${cvsworkdir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${lib.dir}\" includes=\"**/*.jar\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</copy>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- 把所有web文件copy到编译目录 -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<copy todir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<fileset dir=\"${cvsworkdir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}/${war.webrot.dir}\" includes=\"**/*.*\"/>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</copy>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- 打包成war到目标路径jar -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<jar destfile=\"${output.dir}/${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_war}\" basedir=\"${edcbigdatadir_"+ConstantsAnt.getWebProgramelistInstance().get(i)+"_all}\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</target>");
			tmp.append(ConstantsAnt.tab);
		}
		return tmp.toString();
	}

	/**
	 * 判断是否有增量lib,有的话拷贝增量lib到lib路径
	 * */
	public String targetCopy_lib(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 判断是否有增量lib,有的话拷贝增量lib到lib路径 -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<target name=\"copy_lib_"+ConstantsAnt.getProgramelistInstance().get(i)+"\" if=\"exist."+ConstantsAnt.getProgramelistInstance().get(i)+".lib\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<!-- 拷贝增量lib到目标路径lib -->");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<copy todir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}/${lib.dir}\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<fileset dir=\"${cvsworkdir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_path}/${lib.dir}\" includes=\"**/*.jar\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</copy>");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</target>");
			tmp.append(ConstantsAnt.tab);
		}
		return tmp.toString();
	}
	
	/**
	 * 压缩jar和lib
	 * */
	public String targetZip_jar_lib(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("<!-- 压缩jar和lib -->");
		tmp.append(ConstantsAnt.tab);
		for(int i=0;i<ConstantsAnt.getProgramelistInstance().size();i++){
			tmp.append("<target name=\"zip_jar_lib_"+ConstantsAnt.getProgramelistInstance().get(i)+"\" if=\"exist."+ConstantsAnt.getProgramelistInstance().get(i)+".project\">");
			tmp.append(ConstantsAnt.tab);
			tmp.append("<zip destfile=\"${output.dir}/${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_zip}\"");
			tmp.append(" basedir=\"${edcbigdatadir_"+ConstantsAnt.getProgramelistInstance().get(i)+"_all}\" excludes=\"compile/\" />");
			tmp.append(ConstantsAnt.tab);
			tmp.append("</target>");
			tmp.append(ConstantsAnt.tab);
		}
		return tmp.toString();
	}
	
	public static void main(String[] args) {
		CompilerXML cx = new CompilerXML();
		StringBuffer all = new StringBuffer();
		all.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		all.append(ConstantsAnt.tab);
		all.append("<project name=\"edc-bigdata\" basedir=\".\" default=\"all\">");
		all.append(ConstantsAnt.tab);
		all.append("<!-- 配置参数据信息 -->");
		all.append(ConstantsAnt.tab);
		all.append("<property file=\"edc-bigdata.properties\"/>");
		all.append(ConstantsAnt.tab);
		all.append(cx.targetAll());
		all.append(cx.targetClean());
		all.append(cx.targetFilter_project());
		all.append(cx.targetClean_list_project());
		all.append(cx.conditionExistProject());
		all.append(cx.conditionExistLib());
		all.append(cx.targetCompile());
		all.append(cx.targetCopy_lib());
		all.append(cx.targetZip_jar_lib());
		all.append("</project>");
		System.out.println(all.toString());
	}
}
