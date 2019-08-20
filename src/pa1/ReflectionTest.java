package pa1;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ReflectionTest {
	static JUnitCore junitCore;
	static String testFile;
	static Map<String, JSONArray> map = new HashMap<String, JSONArray>();
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		/*
		System.out.println("Running JUnit Test.");
		junitCore = new JUnitCore();
		junitCore.addListener(new CustomExecutionListener());
		Result result = junitCore.run(PlayerTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println("Successful: " + result.wasSuccessful() + "ran" + result.getRunCount() + "tests");
		*/
		
		System.out.println(directoryReport());
		testFile = "package pa1;\r\n\r\n" + 
			"import static org.junit.Assert.*;\r\n\r\n" + 
			"import org.junit.After;\r\n" + 
			"import org.junit.AfterClass;\r\n" + 
			"import org.junit.Before;\r\n" + 
			"import org.junit.BeforeClass;\r\n" + 
			"import org.junit.Test;\r\n" + 
			"import org.junit.Rule;\r\n" + 
			"import org.junit.rules.Timeout;\r\n\r\n" + 
			"import java.lang.reflect.Constructor;\r\n" + 
			"import java.lang.reflect.Field;\r\n" + 
			"import java.lang.reflect.Method;\r\n\r\n" + 
			"public class UnitTest \r\n" + 
			"{\r\n\t@Rule\r\n\tpublic Timeout globalTimeout = Timeout.seconds(3);\n\n";
		// Class<?> c = classes.get(8);
		// System.out.println(classToJSON(c));
		
		// System.out.println(testFile);
		// System.out.println(setField("health", "archer", 10));
		// System.out.println(invokeMethod("moveDelta", "archer", 0, 0));
		// String temp = aEqual(1, "locationX.getInt(archer)");
		// String temp = setField("health", "archer", "i");
		// String temp2 = invokeMethod("heal", "archer");
		// String temp3 = aEqual("i + 3", getField("health", "archer"));
		
		// System.out.println(forLoop("i", 0, 7, temp, temp2, temp3));
		// System.out.println(genTestCase());
	}
	
	public static final ArrayList<Class<?>> getClasses(String packageName) {
		String path = packageName.replaceAll("\\.", File.separator);
		ArrayList<Class<?>> classes = new ArrayList<>();
		String[] classPathEntries = System.getProperty("java.class.path").split(
				System.getProperty("path.separator"));
		String name;
		for (String classpathEntry : classPathEntries) {
	        if (classpathEntry.endsWith(".jar")) {
	            File jar = new File(classpathEntry);
	            try {
	                JarInputStream is = new JarInputStream(new FileInputStream(jar));
	                JarEntry entry;
	                while((entry = is.getNextJarEntry()) != null) {
	                    name = entry.getName();
	                    if (name.endsWith(".class")) {
	                        if (name.contains(path) && name.endsWith(".class")) {
	                            String classPath = name.substring(0, entry.getName().length() - 6);
	                            classPath = classPath.replaceAll("[\\|/]", ".");
	                            classes.add(Class.forName(classPath));
	                        }
	                    }
	                }
	            } catch (Exception ex) {
	            }
	        } else {
	            try {
	                File base = new File(classpathEntry + File.separatorChar + path);
	                for (File file : base.listFiles()) {
	                    name = file.getName();
	                    if (name.endsWith(".class")) {
	                        name = name.substring(0, name.length() - 6);
	                        classes.add(Class.forName(packageName + "." + name));
	                    }
	                }
	            } catch (Exception ex) {
	            }
	        }
	    }

	    return classes;
	}
	
	public static String classToJSON(Class<?> cla) {
		String className = cla.getName();
		String rules = "";
		String beforeClass = "\t@BeforeClass\r\n\tpublic static void setUpBeforeClass() throws Exception\r\n\t{";
		JSONObject json = new JSONObject();
		JSONArray conJSON = new JSONArray();
		JSONObject fieldJSON = new JSONObject();
		JSONObject methodJSON = new JSONObject();

		Class<?> parent = cla.getSuperclass();
		Constructor<?>[] con = cla.getDeclaredConstructors();
		Field[] fld = cla.getDeclaredFields();
		Method[] method = cla.getDeclaredMethods();

		if (parent != null && parent.getName() != "java.lang.Object") {
			String parentName = parent.getName();
			if (map.containsKey(parentName)) {
				JSONArray temp = map.get(parentName);
				temp.put(className);
				map.put(parentName, temp);
			} else {
				map.put(parentName, new JSONArray(Arrays.asList(className)));
			}			
		}
		
		for (Constructor<?> c : con) {
			ArrayList<String> dummy = new ArrayList<String>();

			Parameter[] para =  c.getParameters();
			for (Parameter p : para) {
				String temp = p.toString();
				dummy.add(temp.substring(0, temp.length() - 5));
			}
			conJSON.put(dummy);
		}

		for (Field f : fld) {
			fieldJSON.put(f.getName(), f.getType().getName());
			rules += "\tstatic Field " + f.getName() + ";\n";
			beforeClass += "\n\t\t" + f.getName() + " = " + className + ".class.getDeclaredField(\"" + f.getName() + "\""
				+ ");\n\t\t" + f.getName() + ".setAccessible(true);";
		}

		rules += "\n";
		
		for (Method m : method) {
			ArrayList<String> dummy = new ArrayList<String>();

			Parameter[] para = m.getParameters();
			for (Parameter p : para) {
				String temp = p.toString();
				dummy.add(temp.substring(0, temp.length() - 5));
			}
			methodJSON.put(m.getName(), dummy);
			rules += "\n\tstatic Method " + m.getName() + ";";
			beforeClass += "\n\t\t" + m.getName() + " = " + className + ".class.getDeclaredMethod(\"" + m.getName() + "\"";
			for (String d : dummy) {
				beforeClass += ", " + d + ".class";
			}
			beforeClass += ");\n\t\t" + m.getName() + ".setAccessible(true);";
		}
		
		rules += "\n\n";
		beforeClass += "\n\t}";
		testFile += rules + beforeClass + "\n}";
		
		json.put("Constructor", conJSON);
		json.put("Field", fieldJSON);
		json.put("Method", methodJSON);
		return json.toString();
	}
	
	public static String directoryReport() {
		JSONObject result = new JSONObject();
		JSONObject srcJSON = new JSONObject();
		JSONObject dependency = new JSONObject();
		ArrayList<Class<?>> classes = getClasses("pa1");
		String r = "";
		
		for (Class<?> c : classes) {
			//srcJSON.put(c.getName(), classToJSON(c));
			r += classToJSON(c);
			System.out.println(r.length());
		}
		
		// System.out.println(srcJSON);
		System.out.println(r);
		
		for (Map.Entry<String, JSONArray> entry : map.entrySet()) {
		    dependency.put(entry.getKey(), entry.getValue());
		}
		
		System.out.println(dependency);
		result.put("Dependency", dependency);
		result.put("Class", srcJSON);
		return result.toString();
	}
	
	public static <T> String setField(String fieldName, String objectName, T value) {
		return fieldName + ".set(" + objectName + ", " + value + ")";
	}
	
	public static <T> String getField(String fieldName, String objectName) {
		return fieldName + ".get(" + objectName + ")";
	}
	
	public static <T> String invokeMethod(String methodName, String objectName, T... value) {
		String result = methodName + ".invoke(" + objectName;
		for (T t : value) {
			result += ", " + t;
		}
		result += ")";
		return result; 
	}
	
	public static <T> String aEqual(T value, String secondValue) {
		return "assertEquals(" + value + ", " + secondValue + ")";
	}
	
	public static <T> String aTrue(String stmt) {
		return "assertTrue(" + stmt + ")";
	}
	
	public static <T> String aFalse(String stmt) {
		return "assertFalse(" + stmt + ")";
	}
	
	public static <T> String forLoop(String name, int bot, int top, String... stmt) {
		String template = "for (int " + name + " = " + bot + "; " + name + " <= " + top + "; "
				+ name + "++) {";
		for (String s : stmt) {
			template += "\n\t" + s + ";";
		}
		
		return template + "\n}";
	}
	
	public static String genTestCase() {
		String result = "";
		Scanner sc = new Scanner(System.in);
		boolean dnd = true;
		System.out.print("Input name of test case: ");
		String name = sc.next();
		result += "@Test\r\npublic void " + name + "() throws Exception\r\n{\r\n";
		
		while (dnd) {
			System.out.println("Action? ");
			int choice = sc.nextInt();
			switch (choice) {
				case 1:
					System.out.println("setfield");
					System.out.print("Input field name, object name, value: ");
					String fName = sc.next();
					String oName = sc.next();
					String v = sc.next();
					String sField = setField(fName, oName, v);
					result += "\t" + sField + ";";
					break;
				case 2:
					System.out.println("getfield");
					System.out.print("Input field name, object name: ");
					String fName1 = sc.next();
					String oName1 = sc.next();
					String gField = getField(fName1, oName1);
					result += "\t" + gField + ";";
					break;
				case 8:
					dnd = false;
					break;
				default:
					break;
			}
		}
		
		result += "\r\n}";
		sc.close();
		return result;
	}
}
