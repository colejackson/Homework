import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PhotoAlbum extends ArrayList<BufferedImage>
{
	private static final long serialVersionUID = 1L;

	File splashFile;
	
	int position;
	
	public PhotoAlbum()
	{
		super();
		
		splashFile = new File("resources/images/splash.png");
		
		this.position = -1;
		
		BufferedImage splash = null;
		
		try
		{
			splash = ImageIO.read(splashFile);
		}
		catch (Exception e)
		{
			System.err.println("Splash Files Not Found");
		}
		
		this.add(splash);
	}
	
	public PhotoAlbum(String path)
	{
		File dir = new File(path);
		
		splashFile = new File("/resources/images/splash.jpg");
		
		if(!dir.isDirectory())
		{
			System.err.println("Album Constructor Failure: Not a Directory");
			return;
		}
		
		@SuppressWarnings("unused")
		BufferedImage image = null;
		
		for (File imageFile : dir.listFiles())
		{			
			try
			{
				this.add(image = ImageIO.read(imageFile));
			}
			catch(Exception e)
			{
				System.err.println("Not an Image Type File: " + imageFile.getName());
			}
		}
		
		if(this.size() > 0)
			position = 0;
	}
	
	public BufferedImage display()
	{
		// If there is not user image loaded then return the splash screen.
		if(position < 0)
			return this.get(0);
		
		// Otherwise output the position image.
		return this.get(position);
	}
	
	@Override
	public BufferedImage remove(int index)
	{
		if(this.size() == 1)
		{
			position = -1;
		}
		
		return this.remove(index);
	}
	
	public int next()
	{
		System.out.println(position);
		
		if(position < 0)
		{
			return -1;
		}
		else if(position < (this.size() - 1))
		{
			position++;
			return position;
		}
		else if(position == (this.size() - 1))
		{
			position = 0;
			return position;
		}
		
		return -100;
	}
	
	public int previous()
	{
		System.out.println(position);
		
		if(position < 0)
		{
			return -1;
		}
		else if(position > 0)
		{
			position--;
			return position;
		}
		else if(position == 0)
		{
			position = (this.size() - 1);
			return position;
		}
		
		return -100;
	}	
}
