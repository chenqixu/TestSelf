package com.bussiness.bi.bigdata.parser.perl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import com.bussiness.bi.bigdata.parser.perl.bean.ContentNode;
import com.bussiness.bi.bigdata.parser.perl.bean.INode;

/**
 * <pre>
 * 1. read perl
 * 2. get sub
 * 3. parser sql in sub
 * </pre>
 * */
public class PerlParser {

	private static MyLogger log = MyLoggerFactory.getLogger(PerlParser.class);
	public static final String Return_And_Line = "\r\n";
	public static final String Well_Number = "#";
	public static final String Print_KeyWord = "print";
	public static final String Left_Brace = "{";
	public static final String Right_Brace = "}";
	private String file_name;
	private Map<String, PerlSub> ps;
	private String currentSubName = null;

	/**
	 * 处理规则接口
	 * */
	interface Deal {
		public String run(String tmp);
	}

	/**
	 * 处理左右空格
	 * 处理#
	 * 处理print
	 * */
	class DealSpaceAndWellAndPrint implements Deal {
		@Override
		public String run(String tmp) {
			tmp = tmp.trim();
			if(tmp.startsWith(Well_Number))return null;
			if(isFindStr(tmp, Print_KeyWord))return null;
			return tmp;
		}
	}

	/**
	 * 处理sub
	 * 处理{}
	 * */
	class DealSubAndBrace implements Deal {
		@Override
		public String run(String tmp) {
			if(tmp.toLowerCase().contains(PerlSub.KeyWord)){
				currentSubName = PerlSub.getSubName(tmp);
				ps.put(currentSubName, new PerlSub());
			}
			Integer[] leftbrace_arr = findStr(tmp, Left_Brace);
			Integer[] rightbrace_arr = findStr(tmp, Right_Brace);
			if(currentSubName!=null){
				log.debug(Arrays.toString(leftbrace_arr)+"|"+Arrays.toString(rightbrace_arr));
				ps.get(currentSubName).increaseLeftBraceArr(leftbrace_arr);
				ps.get(currentSubName).increateRigthBraceArr(rightbrace_arr);
				if(!ps.get(currentSubName).isEnd()){
					ps.get(currentSubName).appendContent(tmp);
					ps.get(currentSubName).appendContent(Return_And_Line);
				}
			}
			return tmp;
		}
	}

	/**
	 * 按指定关键字进行删除
	 * */
	class DealDelKeyWord implements Deal {
		private String keyword;
		public DealDelKeyWord(String keyword){
			this.keyword = keyword;
		}
		@Override
		public String run(String tmp) {
			if(isFindStr(tmp, keyword))return null;
			return tmp;
		}
	}

	/**
	 * 按指定关键字进行保留
	 * */
	class DealSaveKeyWord implements Deal {
		private String keyword;
		public DealSaveKeyWord(String keyword){
			this.keyword = keyword;
		}
		@Override
		public String run(String tmp) {
			if(!isFindStr(tmp, keyword))return null;
			return tmp;
		}
	}

	/**
	 * 保留关键字前面内容
	 * */
	class DealRetainKeywordBeforeContent implements Deal {
		private String keyword;
		public DealRetainKeywordBeforeContent(String keyword){
			this.keyword = keyword;
		}
		@Override
		public String run(String tmp) {
			return retainKeywordBeforeContent(tmp, keyword);
		}
	}

	/**
	 * 构造
	 * */
	public PerlParser(String file_name){
		this.file_name = file_name;
		this.ps = new HashMap<String, PerlSub>();
	}

	/**
	 * 读字符串
	 * */
	private List<String> readStr(String str){
		String findstr = str;
		List<String> list = new ArrayList<String>();
		int index = -1;
		do {
			index = findstr.indexOf(Return_And_Line);
			if(index<0)break;
			String currentstr = findstr.substring(0, index);
			findstr = findstr.substring(index+1);
			list.add(currentstr);
		} while (index>=0);
		return list;
	}

	/**
	 * 读文件
	 * 处理前后空格和#和print
	 * 处理sub和{}
	 * */
	private String read(String file_name){
		List<Deal> deallist = new ArrayList<Deal>();
		deallist.add(new DealSpaceAndWellAndPrint());
		deallist.add(new DealSubAndBrace());
		return read(file_name, deallist);
	}

	/**
	 * 读文件，按规则列表进行处理
	 * */
	private String read(String file_name, List<Deal> deallist){
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file_name), "GBK"));
			String tmp;
			currentSubName = null;
			while((tmp=br.readLine())!=null){
				for(Deal deal : deallist){
					if(tmp!=null)	 tmp = deal.run(tmp);
					else break;
				}
				if(tmp!=null){
					sb.append(tmp);
					sb.append(Return_And_Line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( br!=null )
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return sb.toString();
	}

	/**
	 * 找到所有关键字，写入数组
	 * */
	private Integer[] findStr(String str, String keyword){
		List<Integer> list = new ArrayList<Integer>();
		String findstr = str;
		int result_index = -1;
		int last_index = 0;
		while(true){
			if(result_index>=0)last_index++;
			result_index = findstr.indexOf(keyword, last_index);
			last_index = result_index;
			if(result_index<0){
				break;
			}else{
				list.add(result_index);
			}
		}
		return list.toArray(new Integer[0]);
	}

	/**
	 * 根据给定的关键字找到第一次出现的整行
	 * */
	private String findAssignmentStatementByStr(String str, String keyword){
		String findstr = str;
		int index = -1;
		int endindex = -1;
		int startindex = -1;
		index = findstr.indexOf(keyword);
		endindex = findstr.indexOf(Return_And_Line, index);
		startindex = findstr.lastIndexOf(Return_And_Line, endindex-2);
		if(index>=0 && endindex>=0 && startindex>=0){
			return findstr.substring(startindex+2, endindex).trim();
		}
		return null;
	}

	private String findAssignmentStatementByStrStopBySql(String str, String keyword, String type){
		String findstr = str;
		int index = -1;
		int endindex = -1;
		int startindex = -1;
		String _tmp = "";
		boolean findtag = false;
		while(true){
			index = findstr.indexOf(keyword);
			endindex = findstr.indexOf(Return_And_Line, index);
			startindex = findstr.lastIndexOf(Return_And_Line, endindex-2);
			if(index>=0 && endindex>=0 && startindex>=0){
				_tmp = findstr.substring(startindex+2, endindex).trim();
				//找关键字，找到就退出
				if(type.equals("SQL")){
					if(findInsertInto(_tmp)){//找values (或values(
						findtag = true;
						break;
					}
				}else{
					if(findEqualTo(_tmp, keyword)){//keyword应该在等号的右边
						findtag = true;
						break;
					}
				}
				findstr = findstr.substring(endindex);
			}else{
				break;
			}
		}
		log.info("[代码]"+_tmp);
		return findtag==true?_tmp:null;
	}

	/**
	 * 查找values (
	 * 查找values(
	 * */
	private boolean findInsertInto(String str){
		String findstr = str;
		int startindex = findstr.lastIndexOf("values (");
		if(startindex<0)startindex = findstr.lastIndexOf("values(");
		return startindex>=0;
	}

	/**
	 * keyword应该在等号的右边
	 * */
	private boolean findEqualTo(String str, String keyword){
		String findstr = str;
		//先查找keyword位置
		int startindex = findstr.lastIndexOf(keyword);
		//向前查找是否有等号
		int equalindex = findstr.lastIndexOf("=", startindex);
		return equalindex>=0;
	}

	/**
	 * 查找指定关键字
	 * */
	private boolean isFindStr(String str, String keyword){
		return str.indexOf(keyword)>=0;
	}

	/**
	 * 保留关键字前面内容
	 * */
	private String retainKeywordBeforeContent(String str, String keyword){
		String findstr = str;
		int startindex = findstr.lastIndexOf(keyword);
		return findstr.substring(0, startindex);
	}

	/**
	 * 去重
	 * */
	private String distinctStr(String str){
		Set<String> dis = new HashSet<String>();
		for(String s : readStr(str)){
			dis.add(s);
		}
		StringBuffer newstr = new StringBuffer();
		Iterator<String> it = dis.iterator();
		while(it.hasNext()){
			newstr.append(it.next());
			newstr.append(Return_And_Line);
		}
		return newstr.toString();
	}

	/**
	 * 先按SQL方式找，找不到，就按变量方式找，然后继续按SQL方式找
	 * */
	private String findAssignmentStatementByStrList(String str, String keywordlist){
		//可能是=，又是变量，可能就是SQL，已经是结尾
		//先按SQL找，找不到，就按变量找
		String findresult = findAssignmentStatementByStrStopBySql(str, keywordlist, "SQL");
		if(findresult==null){
			//查找等号
			findresult = findAssignmentStatementByStrStopBySql(str, keywordlist, "");
			//找到变量那一行，转换成变量，继续找SQL
			if(findresult!=null){
				findresult = getDefine(findresult);
				findresult = findAssignmentStatementByStrList(str, findresult);
			}
		}
		return findresult;
	}

	/**
	 * 根据给定的某行查找=前面的变量
	 * */
	private String getDefine(String str){
		String findstr = str;
		int index = findstr.indexOf("=");
		String  tmp = findstr.substring(0, index);
		tmp = tmp.replace("my ", "");
		tmp = tmp.replace("$", "");
		tmp = tmp.trim();
		log.info("[变量]"+tmp);
		return tmp;
	}

	public void start(){
		log.debug(read(this.file_name));
		read(this.file_name);
		String mainstr = "";
		String cfgaigstr = "";
		for(Map.Entry<String, PerlSub> sp : ps.entrySet()){
			if(sp.getKey().equals("main")){
//				log.info(sp.getKey()+"|"+sp.getValue().getLeftBrace()+"|"+sp.getValue().getRigthBrace());
//				log.info(sp.getValue().getContent());
//				parserTree(sp.getValue().getContent());
//				log.info(Arrays.toString(findStr(sp.getValue().getContent(), "$aFieldArray[0]")));
				mainstr = sp.getValue().getContent();
			}
			if(sp.getKey().equals("cfgAIG")){
				cfgaigstr = sp.getValue().getContent();
			}
		}
		//根据关键字找赋值语句，前面的变量又变成关键字找赋值语句，直到找到ENDSQL为止
		List<String> keylist = new ArrayList<String>();
		for(int i=0;i<18;i++)
			keylist.add("$aFieldArray["+i+"]");
		for(String key : keylist){
			String list = findAssignmentStatementByStrList(cfgaigstr,
					getDefine(findAssignmentStatementByStr(mainstr, key)));
			log.info("[key]"+key+"[查找结果]"+list);
		}
	}

	/**
	 * 根据关键字进行逐行删除
	 * */
	public void deleteByKeyWord(String keyword){
		List<Deal> deallist = new ArrayList<Deal>();
		deallist.add(new DealSpaceAndWellAndPrint());
		deallist.add(new DealDelKeyWord(keyword));
		log.info(read(file_name, deallist));
	}
	/**
	 * 仅保留关键字 keyword1
	 * */
	public void statisticsSql(String keyword){
		List<Deal> deallist = new ArrayList<Deal>();
		deallist.add(new DealSpaceAndWellAndPrint());
		deallist.add(new DealSaveKeyWord(keyword));
		log.info(read(file_name, deallist));
	}

	/**
	 * 仅保留关键字 keyword1
	 * 保留关键字前面内容 keyword2
	 * */
	public void statisticsSqlAndSave(String keyword1, String keyword2){
		List<Deal> deallist = new ArrayList<Deal>();
		deallist.add(new DealSpaceAndWellAndPrint());
		deallist.add(new DealSaveKeyWord(keyword1));
		deallist.add(new DealRetainKeywordBeforeContent(keyword2));
		log.info(distinctStr(read(file_name, deallist)));
	}

	public void printINodeListTree(List<INode> inode, String tip){
		for(INode id : inode){
			log.info(tip+id.getContent().getContent());
			if(id.hasChild()){
				printINodeListTree(id.getChildList(), tip+"--");
			}
		}
	}

	public void printINodeTree(INode inode, String tip){
		log.info(tip+inode.getContent().getContent());
		if(inode.hasChild()){
			printINodeListTree(inode.getChildList(), tip+"--");
		}
	}

	public void parserTree(String str){
		log.info("[str]"+str);
		Integer[] leftarr = findStr(str, PerlParser.Left_Brace);
		Integer[] rightarr = findStr(str, PerlParser.Right_Brace);
		int first_left = -1;
		int next_post = -1;
		INode firstINode = new INode();//顶层
		firstINode.setContent(new ContentNode("==first=="));
		INode currentINode = null;
		INode previosINode = null;
		boolean superflag = false;
		if(leftarr.length>0){
			//找到第一个左括号和第二个左括号之间的内容，增加
			for(int i=0;i<leftarr.length;i++){
				first_left = i;
				if(leftarr.length>1 && first_left<leftarr.length-1){
					next_post = leftarr[i+1];
				}else{
					next_post = rightarr[i];
				}
				String findcontent = str.substring(leftarr[first_left]+1, next_post);
				log.debug("[findcontent]"+findcontent);
				//这里需要再对前面的对象进行补充
				//找到一个右括号，就关闭一个
				Integer[] findcontentrightarr = findStr(findcontent, PerlParser.Right_Brace);
				//找到右括号，需要关闭处理
				if(findcontentrightarr.length>0){
					int find_first_post = 0;
					int find_next_post = -1;
					for(int j=0;j<findcontentrightarr.length;j++){
						find_next_post = findcontentrightarr[j];
						if(j>0){
							find_first_post = findcontentrightarr[j-1]+1;
						}
						String rightfindcontent = findcontent.substring(find_first_post, find_next_post);
						log.debug(rightfindcontent);
						//首先得知道后面有没右括号
						//有的话，第一个需要新建，后面需要往插入前面子节点（切换当前节点为上一个节点，然后插入当前节点）
						if(j==0){
							//需要处理同级有其他节点的事情，加list，应该是父亲有多个孩子
							if(superflag){
								superflag = false;
								previosINode = currentINode.getParent();
								//在父亲下加子节点
								currentINode = new INode();
								ContentNode cn = new ContentNode(rightfindcontent);
								log.debug("[wwX new current]"+rightfindcontent);
								currentINode.setContent(cn);
								currentINode.setParent(previosINode);
								previosINode.addChild(currentINode);
							}else{
								previosINode = currentINode;
								currentINode = new INode();
								ContentNode cn = new ContentNode(rightfindcontent);
								log.debug("[ww new current]"+rightfindcontent);
								currentINode.setContent(cn);
								if(previosINode!=null){
									previosINode.addChild(currentINode);
									currentINode.setParent(previosINode);
								}
							}
						}else{
							previosINode.getContent().add(rightfindcontent);
						}
						//没有右括号，这里不需要处理
					}
					//补充末尾不是右括号的，所以条件还要一个，末尾是右括号的这里不处理
					//插入上个节点
					if(find_next_post<findcontent.length() &&
							!findcontent.trim().endsWith(PerlParser.Right_Brace)){
						String rightfindcontent = findcontent.substring(find_next_post+1);
						log.debug(rightfindcontent);
						int backParentCnt = 0;
						if(findcontentrightarr.length>1)backParentCnt=findcontentrightarr.length-1;
						currentINode = previosINode;
						previosINode = currentINode.getParent();
						for(int x=0;x<backParentCnt;x++){
							currentINode = currentINode.getParent();
							previosINode = currentINode.getParent();
						}
						currentINode.getContent().add(rightfindcontent);
					}
					if(find_next_post<findcontent.length() &&
							findcontent.trim().endsWith(PerlParser.Right_Brace)){
						log.debug("last----[currten]"+currentINode.getContent().getContent());
						log.debug("last----[previos]"+previosINode.getContent().getContent());
						//因为是上一周期最后的子节点，所以后续需要新增的子节点，在上一个父节点上增加
						superflag = true;
					}
				}
				else{
					//需要处理同级有其他节点的事情，加list，应该是父亲有多个孩子
					if(superflag){
						superflag = false;
						previosINode = currentINode.getParent();
						//在父亲下加子节点
						currentINode = new INode();
						ContentNode cn = new ContentNode(findcontent);
						log.debug("[wX new current]"+findcontent);
						currentINode.setContent(cn);
						currentINode.setParent(previosINode);
						previosINode.addChild(currentINode);
					}
					else{
						//同级没有其他节点，只要新增
						previosINode = currentINode;
						currentINode = new INode();
						ContentNode cn = new ContentNode(findcontent);
						log.debug("[w new current]"+findcontent);
						currentINode.setContent(cn);
						if(i==0){
							previosINode = firstINode;
						}
						if(previosINode!=null){
							previosINode.addChild(currentINode);
							currentINode.setParent(previosINode);
						}
					}
				}
			}
			printINodeTree(firstINode, "");
		}
	}

	public static void main(String[] args) {
		String parserfile;
//		parserfile = "j:\\Work\\CVS\\BI\\program_script\\bass\\perl_program\\other\\tool_cfg_etl.pl";
//		parserfile = "d:\\tmp\\perl\\tool_cfg_syn.pl";
//		parserfile = "d:\\tmp\\perl\\tool_cfg_syn.log";
		parserfile = "d:\\tmp\\perl\\xdload.txt";
		PerlParser pp = new PerlParser(parserfile);
//		pp.start();
//		pp.deleteByKeyWord(" rows ");
//		pp.statisticsSqlAndSave("insert into", "(");
		pp.statisticsSql("delete");

//		PerlSub ps = new PerlSub();
//		Integer[] a = new Integer[1];
//		System.out.println(a.length);
//		ps.increaseLeftBraceArr(a);
//		System.out.println(ps.getLeftBrace());

//		String str = "a{be{ce{dxxxx}e}f{x}{y{ttt}z}}";
//		str = "a{be{c}f}";
//		str = "a{b{c}1{d}2{e}3{f}4}";
//		pp.parserTree(str);
	}
}
