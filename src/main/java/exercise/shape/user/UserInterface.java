package exercise.shape.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;

import exercise.shape.InjectObjects.service.IService;
import exercise.shape.InjectObjects.service.ShapeServiceResponse;
import exercise.shape.assist.Point;
import exercise.shape.context.DefaultContext;
import exercise.shape.context.IContext;
import exercise.shape.def.IShape;

public class UserInterface {

	IContext context;
	IService service;
	boolean shutdown;
	Set<String> shapeNames;
	final String HELP_COMMAND = "help";
	final String EXIT_COMMAND = "exit";
	final String LOAD_COMMAND = "load";
	final String FIND_COMMAND = "find";
	final String DELETE_COMMAND = "delete";
	final String DEFAULT_LOAD_FILE = "ShapeDefinitions.txt";
	boolean isDefaultFileIsLoaded;

	public static void main(String[] args) {
		UserInterface userInterface = new UserInterface();
		userInterface.start();
	}

	public void start() {
		context = new DefaultContext("exercise.shape");
		service = context.getInjectObject(IService.class);
		ShapeServiceResponse response = service.getAvailableShapes();
		if (!response.isSuccess()) {
			print("Error!Please contact the administrator.");
		}
		shapeNames = (Set<String>) response.getObject();

		BufferedReader br;
		shutdown = false;
		isDefaultFileIsLoaded = false;
		print("Shape service started.");
		while (!shutdown) {
			System.out.print("==>");
			br = new BufferedReader(new InputStreamReader(System.in));
			try {
				dispatch(br.readLine());
			} catch (IOException e) {
				print("Error!Please contact the administrator.");
			}
		}
	}

	private String dispatch(String inputLine) {
		inputLine = inputLine.toLowerCase().trim();
		if (StringUtils.isBlank(inputLine)) {
			return "";
		}
		// help
		if (inputLine.equalsIgnoreCase(HELP_COMMAND)) {
			return helpHandler(null);
		}

		// help <shape name>
		if (inputLine.toLowerCase().startsWith(HELP_COMMAND + " ")) {
			String params = inputLine.substring(HELP_COMMAND.length() + 1)
					.trim();
			return helpHandler(params);
		}

		int firstSpaceIndex = inputLine.indexOf(" ");
		String inputCmd;
		double[] doubleParams = new double[0];
		if (firstSpaceIndex != -1) {
			inputCmd = inputLine.substring(0, firstSpaceIndex).trim();
		} else {
			inputCmd = inputLine;
		}

		// exit
		if (inputCmd.equalsIgnoreCase(EXIT_COMMAND)) {
			return exit();

		}

		// load file
		if (inputCmd.equalsIgnoreCase(LOAD_COMMAND)) {
			String inputParams = inputLine.substring(inputCmd.length()).trim();
			return loadFileHandler(inputParams);
		}

		// find <shape name>
		if (inputCmd.equalsIgnoreCase(FIND_COMMAND)) {
			String inputParams = inputLine.substring(inputCmd.length()).trim();
			return findShapeHandler(inputParams);
		}

		// delete <shape name>
		if (inputCmd.equalsIgnoreCase(DELETE_COMMAND)) {
			String inputParams = inputLine.substring(inputCmd.length()).trim();
			return deleteShapeHandler(inputParams);
		}

		// shape creation
		if (shapeNames.contains(inputCmd)) {
			String inputParams = inputLine.substring(inputCmd.length());
			if (!StringUtils.isBlank(inputParams)) {
				inputParams = inputParams.trim();
				String[] params = StringUtils.splitByWholeSeparator(
						inputParams, " ");
				doubleParams = new double[params.length];
				for (int i = 0; i < params.length; i++) {
					String s = params[i];
					if (isNumeric(s)) {
						doubleParams[i] = Double.parseDouble(s);
					} else {
						StringBuilder builder = new StringBuilder();
						builder.append("Not valid parameter:" + s
								+ ", must be a number.\n");
						builder.append("Enter 'help " + inputCmd
								+ "' to see more.\n");
						print(builder.toString());
						return builder.toString();
					}
				}
			}
			return createShapeHandler(inputCmd, doubleParams);
		}

		String norecg = "Command not recognized, please enter 'help' for instructions.";
		// find shapes contains the point
		String[] inputs = StringUtils.splitByWholeSeparator(inputLine, " ");
		if (inputs.length == 2) {
			double[] pParams = new double[inputs.length];
			for (int i = 0; i < inputs.length; i++) {
				String s = inputs[i];
				if (isNumeric(s)) {
					pParams[i] = Double.parseDouble(s);
				} else {
					print(norecg);
					return norecg;
				}
			}
			return findContainedShapesByPointHandler(pParams);
		}

		print(norecg);
		return norecg;
	}

	private String findContainedShapesByPointHandler(double[] params) {
		Point point = new Point(params[0], params[1]);
		ShapeServiceResponse response = service.findShapeContainsPoint(point);
		StringBuilder builder = new StringBuilder();
		if (response.isSuccess()) {
			List<IShape> shapes = (List<IShape>) response.getObject();
			builder.append("==>The following shapes contains the point "
					+ point.toString() + ":\n");
			if (shapes.size() > 0) {
				double totalArea = 0;
				for (IShape shape : shapes) {
					double shapeArea = shape.getArea();
					totalArea = totalArea + shapeArea;
					builder.append(shape.toString());
					builder.append("\n");
					builder.append("Area:" + shapeArea + "\n");
					builder.append("\n");
				}
				builder.append("Total Area:" + totalArea + "\n");
				System.out.println(builder.toString());
				return builder.toString();
			}
		}

		print("No shape contain the point " + point.toString());
		return "No shape contain the point " + point.toString();
	}

	private String createShapeHandler(String shapeName, double[] params) {
		ShapeServiceResponse response = service.saveNewShape(shapeName, params);
		if (response.isSuccess()) {
			print(response.getObject().toString());
			return response.getObject().toString();
		} else {
			print(response.getMessage());
			return response.getMessage();
		}
	}

	private String helpHandler(String shapeName) {
		IService service = context.getInjectObject(IService.class);
		StringBuilder inst = new StringBuilder();
		ShapeServiceResponse response;

		// all instructions
		if (shapeName == null || shapeName.equals("")) {
			response = service.getBuidShapeInstructions();
			inst.append("1. Follow the formats below to create new shape:\n");
			if (response.isSuccess()) {
				inst.append(response.getObject());
			} else {
				inst.append(response.getMessage());
			}
			inst.append("\n");
			inst.append("Or you can input: help <shape name> to show the only instruction of a shape.\n ");
			inst.append("   For example: help circle\n");
			inst.append("\n");
			inst.append("2. Use 'find <shape name>' to find the created shape\n");
			inst.append("   Use 'delete <shape name>' to delete the created shape\n");
			inst.append("3. To find all shapes contain a point, input a pair of number to indicate the point\n");
			inst.append("   For example: \n");
			inst.append("    1 2 \n");
			inst.append("   means to find all shapes contains the point (1,2) \n");
			inst.append("4. Use 'load <filePath>', you can load shape definition files to create shapes in a batch\n");
			inst.append("   if <filePath> is not entered, system will load default file: 'ShapeDefinitions.txt', this file can be load once\n");
			inst.append("   For example: \n");
			inst.append("    load def.txt \n");
			inst.append("5. Use 'exit' to exit.");
		} else {
			// single instructions for one shape
			response = service.getBuidShapeInstructions(shapeName);
			if (response.isSuccess()) {
				inst.append(response.getObject());
			} else {
				inst.append(response.getMessage());
			}
		}
		System.out.println(inst.toString());
		return inst.toString();
	}

	private String findShapeHandler(String param) {
		if (StringUtils.isBlank(param)) {
			String s = "Find command must have one shape name.";
			System.out.println(s);
			return s;
		}
		IService service = context.getInjectObject(IService.class);
		StringBuilder inst = new StringBuilder();
		ShapeServiceResponse response = service.findShape(param);
		if (response.isSuccess()) {
			inst.append("Find shape:" + response.getObject().toString());
		} else {
			inst.append(response.getMessage());
		}
		print(inst.toString());
		return inst.toString();

	}

	private String deleteShapeHandler(String param) {
		if (StringUtils.isBlank(param)) {
			String s = "Delete command must have one shape name.";
			System.out.println(s);
			return s;
		}
		IService service = context.getInjectObject(IService.class);
		StringBuilder inst = new StringBuilder();
		ShapeServiceResponse response = service.deleteShape(param);
		if (response.isSuccess()) {
			inst.append("Delete shape:" + response.getObject().toString());
		} else {
			inst.append(response.getMessage());
		}
		print(inst.toString());
		return inst.toString();

	}

	private String loadFileHandler(String filename) {
		StringBuilder rt = new StringBuilder();
		String s;
		if (StringUtils.isBlank(filename)) {
			if (!isDefaultFileIsLoaded) {
				s = "Will load default definition file: ShapeDefinitions.txt";
				print(s);
				filename = DEFAULT_LOAD_FILE;
				rt.append(s);
			} else {
				s = "Default definition file has been loaded.";
				print(s);
				rt.append(s);
				return rt.toString();
			}
		}
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					new File(filename))));
			String line;
			while ((line = br.readLine()) != null) {
				dispatch(line);
			}
			isDefaultFileIsLoaded = true;
			s = "File:" + filename + " loaded";
			print(s);
			rt.append(s);
		} catch (FileNotFoundException e) {
			s = "file:" + filename + " not exist.";
			print(s);
			rt.append(s);
		} catch (IOException e) {
			s = "Error when read file:" + filename + ".";
			print(s);
			rt.append(s);
		}
		return rt.toString();
	}

	private String exit() {
		stop();
		String s = "Shape service stoped.";
		print(s);
		return s;
	}

	public void stop() {
		shutdown = true;
	}

	private void print(Object obj) {
		System.out.println("==> " + obj.toString());
	}

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+[.\\d]*$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

}
