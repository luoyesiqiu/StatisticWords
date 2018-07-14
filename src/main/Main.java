package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
	private static Map<String, Integer> table=new TreeMap<String, Integer>();
	//扫描的目录
	private static final String SEARCH_PATH="K:/code/c";
	//扫描何种类型的文件
	private static String filter="(.*\\.c|.*\\.h)$";
	//大小写是否敏感
	private static boolean sensitive=true;
	//打印前几名，0为全打印
	private static final int RANK=100;
	//扫描时的状态
	private enum State{
		UpperCase,
		LowerCase,
		Other
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File file=new File(SEARCH_PATH);
		if(!file.exists()){
			
			System.out.printf("目录'%s'不存在!",SEARCH_PATH);
			return;
		}
		statisticDir(file);
		List<Map.Entry<String, Integer>> list=sortData();
		System.out.println("扫描结果：\n");
		//绘制markdown表格
		System.out.println("| 排名 | 单词 | 出现频率 |");
		System.out.println("| ------------- |:-------------:| --------:|");
		int rank=(RANK==0?list.size():RANK);
		for(int i=0;i<rank;i++) {
			Map.Entry<String, Integer> entry=list.get(i);
			System.out.println("| "+(i+1)+" | "+entry.getKey()+" | "+entry.getValue()+" |");
		}
	}
	
	/**
	 * 统计一个目录
	 * @param dir
	 */
	private static void statisticDir(File dir) {
		if(dir.isFile()) {
            return;
        }
        File[] fs=dir.listFiles();
        if(fs==null) {
        	return;
        }
        for (File f:fs)
        {
            if (f.isFile())
            {
            	String full=f.getAbsolutePath();
            	if(full.matches(filter)) {
            		statisticFile(full);
            	}
            }
            else{
            	System.out.println("扫描："+f.getAbsolutePath());//想快点可以把这行注释掉
            	statisticDir(f);
            }

        }

	}
	
	/**
	 * 统计一个文件
	 * @param file
	 */
	private static void statisticFile(String file) {
		String sentence=readFile(file).toString();
		statisticWordsBySentence(sentence);
	}
	/**
	 * 读取文件
	 * @param file
	 * @return
	 */
	private static StringBuilder readFile(String file) {
		BufferedReader bufferedReader=null;
		StringBuilder stringBuilder=new StringBuilder();
		try {
			bufferedReader=new BufferedReader(new FileReader(file));
			String line=null;
			while ((line=bufferedReader.readLine())!=null) {
				
				stringBuilder.append(line+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if(bufferedReader!=null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stringBuilder;
	}
	/**
	 * 从大到小排序
	 * @return
	 */
	private static List<Map.Entry<String, Integer>> sortData() {
		List<Map.Entry<String, Integer>> entryArrayList = new ArrayList<>(table.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<String, Integer>>() {
        	@Override
        	public int compare(Map.Entry<String, Integer> v1, Map.Entry<String, Integer> v2) {
        		// TODO Auto-generated method stub
        		return v2.getValue()-v1.getValue();
        	}
		});
        
		return entryArrayList;
	}
	
	/**
	 * 从句子统计单词
	 * @param sentence
	 * @return
	 */
	private static void statisticWordsBySentence(String sentence){
		int start=0;
		int end=0;
		String word=null;
		boolean scan=false;
		
		int len=sentence.length();
		State state=State.Other;
		for(int i=0;i<len;i++) {
			char ch=sentence.charAt(i);
			
			//小写字母
			if(Character.isLowerCase(ch)) {
				if(!scan){
					start=i;
					scan=true;
				}
				//根据前一个状态判断
				if(state==State.Other){
					state=State.LowerCase;
					scan=true;
				}
				else if(state==State.LowerCase||state==State.UpperCase){
					if(i==len-1){
						end=len;
						if(end-start==1) {
							//不要一个字母的单词
							scan=false;
							continue;
						}
						word=sentence.substring(start, end);
						if(!sensitive){
							word=word.toLowerCase();
						}
						if(table.containsKey(word)) {
							int newVal=table.get(word)+1;
							table.put(word, newVal);
						}
						else {
							table.put(word, 1);
						}
					}
				}
				
			}
			//大写字母
			else if(Character.isUpperCase(ch)) {
				if(!scan){
					start=i;
					scan=true;
					state=State.UpperCase;
					continue;
				}
				if(state==State.LowerCase||state==State.UpperCase||state==State.Other){
					end=(i==len-1)?len:i;
					if(end-start==1) {
						//不要一个字母的单词
						scan=false;
						continue;
					}
					word=sentence.substring(start, end);
					if(!sensitive){
						word=word.toLowerCase();
					}
					if(table.containsKey(word)) {
						int newVal=table.get(word)+1;
						table.put(word, newVal);
					}
					else {
						table.put(word, 1);
					}
					state=State.UpperCase;
					scan=true;
					start=i;
				}
				
				
			}
			//其他
			else{
				if(!scan){
					scan=false;
					continue;
				}
				if(state!=State.Other){
					end=(i==len-1)?len:i;
					if(end-start==1) {
						//不要一个字母的单词
						scan=false;
						continue;
					}
					word=sentence.substring(start, end);
					if(!sensitive){
						word=word.toLowerCase();
					}
					if(table.containsKey(word)) {
						int newVal=table.get(word)+1;
						table.put(word, newVal);
					}
					else {
						table.put(word, 1);
					}
					state=State.Other;
					scan=false;
				}
			}
			
		}
	}
}
