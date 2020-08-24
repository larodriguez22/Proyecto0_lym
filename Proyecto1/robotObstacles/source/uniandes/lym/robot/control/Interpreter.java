package uniandes.lym.robot.control;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;



/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;

	StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");


	public Interpreter()
	{
	}


	/**
	 * Creates a new interpreter for a given world
	 * @param world 
	 */


	public Interpreter(RobotWorld mundo)
	{
		this.world =  (RobotWorldDec) mundo;

	}


	/**
	 * sets a the world
	 * @param world 
	 */

	public void setWorld(RobotWorld m) 
	{
		world = (RobotWorldDec) m;

	}



	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  M:  moves forward
	 *  R:  turns right
	 *  
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 */

	public String process(String input) throws Error
	{   
		int i;
		int n;
		boolean ok = true;
		String newInput=input.replaceAll(" ","");
		newInput= newInput.replaceAll("\n", "");
		newInput= newInput.replaceAll("\t", "");	
		newInput= newInput.replaceAll("[\\[\\]{}]","");
		n= newInput.length();
		System.out.println(newInput);
		if(newInput.contains("ROBOT_R")){
			ROBOT_R(newInput);
		}
		else{
			i  = 0;
			try	    {
				while (i < n &&  ok) {
					switch (newInput.charAt(i)) {

					case 'L': moveinDir("south", 4); output.append("move \n");break;
					case 'M': world.moveForward(1); output.append("move \n");break;
					case 'R': world.turnRight(); output.append("turnRignt \n");break;
					case 'C': world.putChips(1); output.append("putChip \n");break;
					case 'B': world.putBalloons(1); output.append("putBalloon \n");break;
					case  'c': world.pickChips(1); output.append("getChip \n");break;
					case  'b': world.grabBalloons(1); output.append("getBalloon \n");break;
					default: output.append(" Unrecognized command:  "+ input.charAt(i)); ok=false;
					}

					if (ok) {
						if  (i+1 == n)  { output.append("expected ';' ; found end of input; ");  ok = false ;}
						else if (newInput.charAt(i+1) == ';') 
						{
							i= i+2;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								System.err.format("IOException: %s%n", e);
							}

						}
						else {output.append(" Expecting ;  found: "+ input.charAt(i+1)); ok=false;
						}
					}

				}


			}
			catch (Error e ){
				output.append("Error!!!  "+e.getMessage());

			}
		}

		return output.toString();
	}

	public void ROBOT_R(String cadena){
		String arreglo[]=cadena.split("ROBOT_R");
		ArrayList<String> futbol= new ArrayList<>();
		ArrayList<Integer> val= new ArrayList<>();
		String a2[];
		if(arreglo[1].startsWith("VARS"))
		{
			String a1[]=arreglo[1].split("VARS");
			a2=a1[1].split("BEGIN");
			futbol=VARS(a2[0]);
			System.out.println(futbol);
		}
		else{
			a2=arreglo[1].split("BEGIN");
		}
		String covid19[]= a2[1].split(";");
		int i=0;
		while( i<covid19.length && !covid19[i].equals("END")){
			
			if(covid19[i].startsWith("assign("))
			{
				covid19[i]=covid19[i].replace("assign(", "");
				covid19[i]=covid19[i].replace(")", "");
				String arr[]=covid19[i].split(",");
				assign(arr[0], Integer.parseInt(arr[1]), futbol, val);
				
			}
			if(covid19[i].startsWith("move(")){
				covid19[i]=covid19[i].replace("move(", "");
				covid19[i]=covid19[i].replace(")", "");
				if(isNumeric(covid19[i])){
					move( Integer.parseInt(covid19[i]));
				}
				else{
					move( val.get(varval(covid19[i], futbol)));
				}
			}
			if(covid19[i].startsWith("turn(")){

				covid19[i]=covid19[i].replace("turn(", "");
				covid19[i]=covid19[i].replace(")", "");
				System.out.println(covid19[i]);
				turn(covid19[i]);

			}
			if(covid19[i].startsWith("face(")){
				covid19[i]=covid19[i].replace("face(", "");
				covid19[i]=covid19[i].replace(")", "");
				face(covid19[i]);
			}
			if(covid19[i].startsWith("put(")){
				covid19[i]=covid19[i].replace("put(", "");
				covid19[i]=covid19[i].replace(")", "");
				String arr[]=covid19[i].split(",");
				put(Integer.parseInt(arr[0]), arr[1]);

			}
			if(covid19[i].startsWith("pick(")){
				covid19[i]=covid19[i].replace("pick(", "");
				covid19[i]=covid19[i].replace(")", "");
				String arr[]=covid19[i].split(",");
				pick(Integer.parseInt(arr[0]), arr[1]);
			}
			if(covid19[i].startsWith("moveDir(")){
				covid19[i]=covid19[i].replace("moveDir(", "");
				covid19[i]=covid19[i].replace(")", "");
				String arr[]=covid19[i].split(",");
				if(isNumeric(arr[0])){
					moveDir(arr[1], Integer.parseInt(arr[0]));
				}
				else{
					moveDir(arr[1], val.get(varval(arr[0], futbol)));
				}
			}
			if(covid19[i].startsWith("moveInDir(")){
				covid19[i]=covid19[i].replace("moveInDir(", "");
				covid19[i]=covid19[i].replace(")", "");
				String arr[]=covid19[i].split(",");
				if(isNumeric(arr[0])){
					moveinDir(arr[1], Integer.parseInt(arr[0]));
				}
				else{
					moveinDir(arr[1], val.get(varval(arr[0], futbol)));
				}
			}
			if(covid19[i].startsWith("skip(")){
				skip();
			}
			if(covid19[i].startsWith("facing(")){
				covid19[i]=covid19[i].replace("facing(", "");
				covid19[i]=covid19[i].replace(")", "");
				facing(covid19[i]);
			}
			if(covid19[i].startsWith("not(")){
				covid19[i]=covid19[i].replace("not(facing(", "");
				covid19[i]=covid19[i].replace("))", "");
				not(covid19[i]);
			}
			i++;
		}


	}
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        int d = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	public ArrayList<String> VARS(String vars){
		String arreglo[]=vars.split(",");
		ArrayList<String> futbol= new ArrayList<>();
		for(int i=0; i<arreglo.length; i++){
			futbol.add(arreglo[i]);

		}
		return futbol;
	}

	public int varval(String variable,  ArrayList<String> var)
	{
		int k=-1;
		for(int i=0; i<var.size(); i++)
		{
			if(var.get(i).equals(variable)){
				k=i;
			}
		}
		return k;
	}

	public void assign(String name, int n, ArrayList<String> var,  ArrayList<Integer> val)
	{
		val.add(varval(name,var), n);
		System.out.println(var+ "-"+ val);
	}

	public void move(int n)
	{
		world.moveForward(n); output.append("move"+n+"\n");
	}

	public void turn(String uribito)
	{
		uribito=uribito.toLowerCase();
		if(uribito.equals("right"))
		{
			world.turnRight(); output.append("turnRigt \n");
		}
		if(uribito.equals("around"))
		{
			world.turnRight(); world.turnRight(); output.append("around \n");
		}
		if(uribito.equals("left"))
		{
			world.turnRight(); world.turnRight(); world.turnRight(); output.append("left \n");
		}
	}

	public void face(String petro)
	{
		petro=petro.toLowerCase();
		int corona=-1;
		if(petro.equals("north")){
			corona=0;
		}
		if(petro.equals("south")){
			corona=1;
		}
		if(petro.equals("east")){
			corona=2;
		}
		if(petro.equals("west")){
			corona=3;
		}
		while(world.getFacing()!=corona)
		{
			world.turnRight(); 
		}
		output.append("face"+petro+"\n");

	}

	public void put(int val, String x)
	{
		x=x.toLowerCase();
		if(x.equals("chip"))
		{
			world.putChips(val); output.append("put"+x+" "+val+"\n");
		}
		if(x.equals("balloon"))
		{
			world.putBalloons(val); output.append("put"+x+" "+val+"\n");
		}

	}

	public void pick(int val, String x)
	{
		x=x.toLowerCase();
		if(x.equals("chip"))
		{
			world.pickChips(val); output.append("put"+x+" "+val+"\n");
		}
		if(x.equals("balloon"))
		{
			for(int i=0; i<val;i++){
				world.pickupBalloon(); 
			}
			output.append("put"+x+" "+val+"\n");
		}

	}

	public void moveDir(String elTibio, int n)
	{
		elTibio=elTibio.toLowerCase();
		for(int i=0; i<n; i++){
			if(elTibio.equals("left")){
				world.left(); 
			}
			if(elTibio.equals("right")){
				world.right(); 
			}
			if(elTibio.equals("front")){
				world.up(); 
			}
			if(elTibio.equals("back")){
				world.down(); 
			}

		}
		output.append("moveDir"+n+"\n");

	}

	public void moveinDir(String elTibio, int n)
	{
		face(elTibio);
		move(n); output.append("move"+n+"\n");

	}

	public void skip()
	{
		output.append("skip");
	}
	public void facing(String petro)
	{
		boolean joseDaniel=false;
		int corona=-1;
		if(petro.equals("north")){
			corona=0;
		}
		if(petro.equals("south")){
			corona=1;
		}
		if(petro.equals("east")){
			corona=2;
		}
		if(petro.equals("west")){
			corona=3;
		}
		if(corona==world.getFacing())
		{
			joseDaniel=true;
		}
		output.append("Is facing "+petro+" "+joseDaniel);
	}
	
	public void not(String petro){
		boolean joseDaniel=true;
		int corona=-1;
		if(petro.equals("north")){
			corona=0;
		}
		if(petro.equals("south")){
			corona=1;
		}
		if(petro.equals("east")){
			corona=2;
		}
		if(petro.equals("west")){
			corona=3;
		}
		if(corona==world.getFacing())
		{
			joseDaniel=false;
		}
		output.append("Is facing "+petro+" "+joseDaniel);
	}
	














}
