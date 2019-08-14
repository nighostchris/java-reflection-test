package pa1;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ReflectionTest {
	static JUnitCore junitCore;
	
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
		ArrayList<Class<?>> classes = getClasses("pa1");
		Class<?> c = classes.get(12);
		// String result = classToJSON(c);
		// System.out.println(setField("health", "archer", 10));
		// System.out.println(invokeMethod("moveDelta", "archer", 0, 0));
		// String temp = aEqual(1, "locationX.getInt(archer)");
		String temp = setField("health", "archer", "i") + "\n";
		String temp2 = invokeMethod("heal", "archer") + "\n";
		String temp3 = aEqual("i + 3", getField("health", "archer"));
		
		System.out.println(forLoop("i", 0, 7, temp, temp2, temp3));
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
		JSONObject json = new JSONObject();
		JSONArray conJSON = new JSONArray();
		JSONObject fieldJSON = new JSONObject();
		JSONObject methodJSON = new JSONObject();

		Constructor<?>[] con = cla.getDeclaredConstructors();
		Field[] fld = cla.getDeclaredFields();
		Method[] method = cla.getDeclaredMethods();
		
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
		}

		for (Method m : method) {
			ArrayList<String> dummy = new ArrayList<String>();

			Parameter[] para = m.getParameters();
			for (Parameter p : para) {
				String temp = p.toString();
				dummy.add(temp.substring(0, temp.length() - 5));
			}
			methodJSON.put(m.getName(), dummy);
		}
		
		json.put("name", cla.getName());
		json.put("constructor", conJSON);
		json.put("field", fieldJSON);
		json.put("method", methodJSON);
		return json.toString();
	}
	
	public static <T> String setField(String fieldName, String objectName, T value) {
		return fieldName + ".set(" + objectName + ", " + value + ");";
	}
	
	public static <T> String getField(String fieldName, String objectName) {
		return fieldName + ".get(" + objectName + ");";
	}
	
	public static <T> String invokeMethod(String methodName, String objectName, T... value) {
		String result = methodName + ".invoke(" + objectName;
		for (T t : value) {
			result += ", " + t;
		}
		result += ");";
		return result; 
	}
	
	public static <T> String aEqual(T value, String secondValue) {
		return "assertEquals(" + value + ", " + secondValue + ");";
	}
	
	public static <T> String aTrue(String stmt) {
		return "assertTrue(" + stmt + ");";
	}
	
	public static <T> String aFalse(String stmt) {
		return "assertFalse(" + stmt + ");";
	}
	
	public static <T> String forLoop(String name, int bot, int top, String... stmt) {
		String template = "for (int " + name + " = " + bot + "; " + name + " <= " + top + "; "
				+ name + "++) {";
		for (String s : stmt) {
			template += "\n\t" + s;
		}
		
		return template + "\n}";
	}
}
