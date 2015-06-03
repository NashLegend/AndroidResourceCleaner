import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Main {

	public static void main(String[] args) {
		if (args.length > 0) {
			unusedCleaner(args[0]);
		}
	}

	static ArrayList<TypeSource> currents = new ArrayList<>();
	static String encoding = "utf-8";

	static boolean LastIsPath = false;

	public static void unusedCleaner(String filePath) {
		ArrayList<TypeSource> files = new ArrayList<>();
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (!parseType(lineTxt)) {
						String trim = lineTxt.trim();
						if (new File(trim).exists()) {
							for (Iterator<TypeSource> iterator = currents
									.iterator(); iterator.hasNext();) {
								TypeSource typeSource = (TypeSource) iterator
										.next().clone();
								typeSource.path = trim;
								typeSource.xmlTag = typeSource.getXmlTag();
								files.add(typeSource);
							}
							LastIsPath = true;
						} else {
							continue;
						}
					}
				}
				read.close();
			} else {
				System.out.println("noFile");
			}
		} catch (Exception e) {
			System.out.println("Failed");
			e.printStackTrace();
		}

		for (Iterator<TypeSource> iterator = files.iterator(); iterator
				.hasNext();) {
			TypeSource typeSource = (TypeSource) iterator.next();
			System.out.println(typeSource);
			typeSource.cleanSelf();
		}
		System.out.println("done");
	}

	public static boolean parseType(String lineTxt) {
		String reg = "((drawable)|(anim)|(layout)|(dimen)|(string)|(attr)|(style)|(styleable)|(color)|(id))\\s*:\\s*(\\S+)";
		Matcher matcher = Pattern.compile(reg).matcher(lineTxt);
		if (matcher.find()) {
			if (LastIsPath) {
				currents.clear();
			}
			LastIsPath = false;
			TypeSource typeSource = new TypeSource();
			typeSource.type = matcher.group(1);
			typeSource.name = matcher.group(matcher.groupCount());
			currents.add(typeSource);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static void deleteNodeByName(String path, String tag, String name) {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(path));
			Element element = document.getRootElement();
			List<Element> list = element.elements("dimen");
			for (int i = 0; i < list.size(); i++) {
				Element ele = list.get(i);
				String tName = ele.attributeValue("name");
				if (tName != null && tName.length() > 0) {
					if (name.equals(ele.attributeValue("name"))) {
						element.remove(ele);
						break;
					}
				}
			}
			OutputFormat format = new OutputFormat("", false);//
			XMLWriter xmlWriter = new XMLWriter(new FileWriter(path), format);
			xmlWriter.write(document);
			xmlWriter.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	static class TypeSource {
		String type = "";// 类型
		String name = "";// xml中的name属性
		String xmlTag = "";// xml的tag名
		String path = "";// 属于哪个文件

		public String getXmlTag() {
			if ("styleable".equals(type)) {
				return "declare-styleable";
			} else {
				return type;
			}
		}

		@Override
		public String toString() {
			return type + " | " + name + " | " + xmlTag + " | " + path;
		}

		/**
		 * 一个一个的单独删，要啥效率啊
		 */
		public void cleanSelf() {
			try {
				if (type == null) {
					return;
				}
				if (type.equals("drawable") || type.equals("layout") || type.equals("anim")) {
					new File(path).delete();
				} else if (type.equals("id") || type.equals("")) {
					// do nothing
				} else {
					deleteNodeByName(path, xmlTag, name);
				}
			} catch (Exception e) {

			}
		}

		public TypeSource clone() {
			TypeSource ts = new TypeSource();
			ts.type = type;
			ts.name = name;
			ts.xmlTag = xmlTag;
			ts.path = path;
			return ts;
		}
	}
}
