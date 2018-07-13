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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="/media/xiake/000B6A010000E4E0/lineageOS";
		//String path="/home/xiake/文档";
		statisticDir(new File(path));
		List<Map.Entry<String, Integer>> list=sortData();
		System.out.println("扫描结果：\n");
		//绘制markdown表格
		System.out.println("| 排名 | 单词 | 出现频率 |");
		System.out.println("| ------------- |:-------------:| --------:|");
		for(int i=0;i<100;i++) {
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
            	if(full.endsWith(".c")||full.endsWith(".cpp")||full.endsWith(".java")) {
            		statisticFile(full);
            	}
            }
            else{
            	System.out.println("扫描："+f.getAbsolutePath());
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
	 * 从大到小排列
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
		for(int i=0;i<len;i++) {
			char ch=sentence.charAt(i);
			
			if(!scan&&Character.isLetter(ch)) {
				start=i;
				scan=true;
				
			}
			 if((scan&&!Character.isLetter(ch))||(scan&&i==len-1)) {
				end=(i==len-1)?len:i;
				if(end-start==1) {
					//不要一个字母的单词
					scan=false;
					continue;
				}
				word=sentence.substring(start, end);
				word=word.toLowerCase();
				if(table.containsKey(word)) {
					int newVal=table.get(word)+1;
					table.put(word, newVal);
				}
				else {
					table.put(word, 1);
				}
				scan=false;
			}
		}

		
	}
}
