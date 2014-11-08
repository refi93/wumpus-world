
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class WorldRenderer {

	private World world;
	
	private Image[] images = new Image[6];
	private BufferedImage[] imagesVisited = new BufferedImage[6];	

	private Image img_agent;
	private Image[] img_arrow = new Image[4];

	public WorldRenderer(World world) {
		this.world = world;
		
		images[Constants.CLEAN] =  createImage("resources/img/img_clean.jpg");
		imagesVisited[Constants.CLEAN] =  createImage("resources/img/img_clean.jpg");
		images[Constants.GOLD] = createImage("resources/img/img_gold.jpg");
		imagesVisited[Constants.GOLD] = createImage("resources/img/img_gold.jpg");
		images[Constants.WALL] = createImage("resources/img/img_wall.jpg");
		imagesVisited[Constants.WALL] = createImage("resources/img/img_wall.jpg");
		images[Constants.PIT] = createImage("resources/img/img_pit.png");
		imagesVisited[Constants.PIT] = createImage("resources/img/img_pit.png");		
		images[Constants.WUM] = createImage("resources/img/img_wum.jpg");
		imagesVisited[Constants.WUM] = createImage("resources/img/img_wum.jpg");
		images[Constants.WUM+1] = createImage("resources/img/img_wum2.jpg");
		imagesVisited[Constants.WUM+1] = createImage("resources/img/img_wum2.jpg");		
		for (int i = 0; i < img_arrow.length; i++) {
			img_arrow[i] = createImage("resources/img/img_arrow"+i+".png");
		}
		img_agent = createImage("resources/img/img_agent.png");		
		
		
		RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);	
		
		for (int i = 0; i < imagesVisited.length; i++) {
			rescaleOp.filter(imagesVisited[i], imagesVisited[i]);	
		}						
	}

	private BufferedImage createImage(String resource) {
		try {
			return ImageIO.read(new File(resource));
		} catch (IOException ex) {
			Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}
		return null;
	}
	
	public Dimension getImageSize() {
		return new Dimension(world.getWidth() * 20, world.getHeight() * 20);
	}

	public void render(Graphics g) {
		for (int i = 0; i < world.net.length; i++) 
			for (int j = 0; j < world.net[i].length; j++) {
				int tile = world.net[i][j];
				if (tile == Constants.WUM && world.killedWumpus){
					tile++;
				}
												
				g.drawImage(world.visited[i][j]?imagesVisited[tile]:images[tile],20*j,20*i,null);
			}
		
		g.setColor(Color.RED);				
		g.drawRect(world.startX*20, world.startY*20, 20, 20);		

		g.drawImage(img_agent,20*world.agentX,20*world.agentY,null);
		
		if (world.flyingArrow()) {
			g.drawImage(img_arrow[world.agent.getOrientation()],20*world.arrowX,20*world.arrowY,null);
		}
	}
	
	public void saveImage(File file) throws IOException {
		Dimension size = getImageSize();
		BufferedImage bi = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		render(g);
		ImageIO.write(bi, "png", file);
	}

}