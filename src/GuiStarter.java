import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

class GuiStarter extends Frame implements Present{
	
	private static final long serialVersionUID = 1L;	
	public World world;
	private WorldRenderer renderer;	
	public JPanel area;
	Button b;	
	
	private static void printUsage(){
		System.out.println("Two arguments are needed.");
		System.out.println("Usage: GuiStarter <MAP-FILENAME> <WAIT-TIME>");
	}

	public static void main(String[] args) throws Throwable {
		if (args.length < 2){
			printUsage();
			return;
		}
		
		GuiStarter s = new GuiStarter();
		
		String fileName = args[0];
		long wait = Long.parseLong(args[1]);	
		s.world = new World(wait);
		s.world.loadWorld("resources/"+fileName);

		s.world.presenter = s;
		s.renderer = new WorldRenderer(s.world);
		s.init();
		
	    s.setVisible(true);    	  
	}

	public void redraw(){
		area.repaint();		
	}

	@SuppressWarnings("serial")
	public void init(){
		  setSize((world.net.length+1)*20-9,(world.net[0].length+3)*20);
		  repaint();
	      Panel p;
	      p = new Panel();
	      p.setSize((world.net.length+1)*20,(world.net[0].length+1)*20+30);
	      p.setLayout(new BorderLayout());
	      b = new Button("Run");
	      p.add(BorderLayout.NORTH,b);
	      b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				b.setEnabled(false);				
				Thread t = new Thread(new Runnable() {

					public void run() {
						World.Result result = world.run();
						if (World.State.EXCEPTION.equals(result.getState())) {
							System.exit(1);
						}else if (World.State.DEAD.equals(result.getState())) {
							try {
								renderer.saveImage(new File("screen.png"));
								System.out.println("screen successfully created");
							} catch (IOException ex) {
								Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
							}	
						}												
					}					
				});
				
				t.start();				
			}		    	  
	      });

			area = new JPanel(){
				public void paint(Graphics g) {
					renderer.render(g);
				}
			};
	      
	      p.add(BorderLayout.CENTER,area);
	      add( p);
	      addWindowListener(new WindowAdapter(){
	    	 public void windowClosing(WindowEvent arg0) {
	    		System.exit(0);	    	
	    	} 
	      });
	      repaint();
	}

}
