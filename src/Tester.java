import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tester{
private World world;

	private static void printUsage() {
		System.out.println("Two arguments needed.");
		System.out.println("Usage: Tester <TESTCASE-FILE-NAME> <STUDENT-NAME>");
	}
	
	public static void main(String[] args) throws Throwable {			
		if (args.length == 0){
			printUsage();
			return;
		}
		
		String testCases = "resources/"+args[0];
		File file = new File(testCases);
		
		if (!file.exists()){
			System.out.println("File " + testCases + " does not exists");
			return;
		}
		
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			try{
				String line;
				while((line = in.readLine())!=null){
					if (line.equals("")){
						continue;
					}
					Scanner scanner = new Scanner(line);
					
					int repetitions = scanner.nextInt();
					
					String fileName = scanner.next();					
					
					int scoreLimit = scanner.nextInt();
					
					scanner.close();
					
					for(int i = 1; i <= repetitions; i++){
						Tester s = new Tester();
						
						s.world = new World(0);
						s.world.loadWorld("resources/"+fileName);

						World.Result result = s.world.run();
						writeLog(args[1], fileName, i, scoreLimit, s.world, result);
						
						System.out.println(i+"/"+repetitions+" of " + line);
					}
					System.out.println();
				}
								
			}finally{
				in.close();
			}		
		}catch (IOException e){
			System.out.println("Error during test occured");			
			e.printStackTrace();
		}		
	}
	
	private static void writeLog(String logName, String mapName, int repetition,
			int scoreLimit, World world, World.Result result) {
		
		File file = new File("resuls.csv");
		boolean alreadyExist = file.exists();
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));							
			try {
				if (!alreadyExist){
					// write a header of the table
					out.write("Solver"+","+"Map file name"+","+"Run number"+","+"Map height"+","+"Map width"+","+
							  "Score limit"+","+	
							  "Final state"+","+"Score");
					out.newLine();
				}					
				
				out.write(logName+","+mapName+","+repetition+","+world.getHeight()+","+world.getWidth()+","+
						  +scoreLimit+","+result.getState()+","+
						  result.getScore());
				out.newLine();
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();							
			System.out.println("Exception occured during LOG creation :(");
		}
		
		if(World.State.DEAD.equals(result.getState())){
			File dir = new File(logName);
			dir.mkdir();			
			WorldRenderer renderer = new WorldRenderer(world);
			try {
				renderer.saveImage(new File(logName + "/" + mapName + "-" + repetition + ".png"));
			} catch (IOException ex) {
				Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}