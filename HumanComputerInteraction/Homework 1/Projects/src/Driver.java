import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Driver {

	public static void main(String[] args) 
	{
		final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
		final JFrame frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize((int)(screenSize.getWidth() * .8), (int)(screenSize.getHeight() * .8));
		frame.setMinimumSize(new Dimension((int)(screenSize.getWidth() * .4), (int)(screenSize.getHeight() * .4)));
		frame.setTitle("PhotoViewer 2.0");
		frame.setLocation((int)(screenSize.getWidth() * .1), (int)(screenSize.getHeight() * .1));
		
		com.apple.eawt.FullScreenUtilities.setWindowCanFullScreen(frame, true);
		
		final PhotoPanel splashPanel = new PhotoPanel();
		
		final JMenuBar menuBar = new JMenuBar();
		
		final JMenu fileMenu = new JMenu("File");
		final JMenu displayMenu = new JMenu("Display");
		
		final JMenuItem openItem = new JMenuItem("Open");
		openItem.setIcon(new ImageIcon("resources/icons/open.png"));
		final JMenuItem closeItem = new JMenuItem("Exit");
		closeItem.setIcon(new ImageIcon("resources/icons/close.png"));
		
		final JMenuItem fullscreenItem = new JMenuItem("Fullscreen");
		fullscreenItem.setIcon(new ImageIcon("resources/icons/fullscreen.png"));
		final JMenuItem nextItem = new JMenuItem("Next Image");
		nextItem.setIcon(new ImageIcon("resources/icons/small right.png"));
		final JMenuItem prevItem = new JMenuItem("Prev Image");
		prevItem.setIcon(new ImageIcon("resources/icons/small left.png"));
		
		fileMenu.add(openItem);
		fileMenu.add(closeItem);
		
		displayMenu.add(fullscreenItem);
		displayMenu.add(nextItem);
		displayMenu.add(prevItem);
		
		prevItem.setEnabled(false);
		nextItem.setEnabled(false);
		
		menuBar.add(fileMenu);
		menuBar.add(displayMenu);
		
		frame.setJMenuBar(menuBar);
		
		closeItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JOptionPane.showMessageDialog( null, "Are you sure you want to close?" );
                System.exit(0);
			}
		});
		
		openItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int returnVal = chooser.showOpenDialog(frame);
				
				File dir = null;
				if (returnVal == JFileChooser.APPROVE_OPTION) 
				{
		            dir = chooser.getSelectedFile();
				}
				
				PhotoPanel pan = new PhotoPanel(dir.getAbsolutePath());
				
				frame.getContentPane().removeAll();
				
				pan.addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent e) 
					{	
						if(e.getPoint().getY() >= (pan.getHeight() * .9) && ((pan.getWidth() * .4) <= e.getPoint().getX()) && ((pan.getWidth() * .6) >= e.getPoint().getX()))
						{
							pan.play();
							return;				
						}
						
						if(e.getPoint().getX() >= (pan.getWidth() * .9))
						{
							pan.next();
							return;
						}
						
						if(e.getPoint().getX() <= (pan.getWidth() * .1))
						{
							pan.previous();
							return;
						}
						
						if(e.getButton() == MouseEvent.BUTTON1)
							pan.next();
						
						if(e.getButton() == MouseEvent.BUTTON3)
							pan.previous();
					}

					@Override
					public void mousePressed(MouseEvent e) {}

					@Override
					public void mouseReleased(MouseEvent e) {}

					@Override
					public void mouseEntered(MouseEvent e) {}

					@Override
					public void mouseExited(MouseEvent e) {}
				});
				
				pan.addKeyListener(new KeyListener() 
				{
                    @Override
                    public void keyTyped(KeyEvent e) {}

					@Override
					public void keyPressed(KeyEvent e) 
					{
						if(e.getKeyCode() == KeyEvent.VK_RIGHT)
							pan.next();
						if(e.getKeyCode() == KeyEvent.VK_LEFT)
							pan.previous();
					}

					@Override
					public void keyReleased(KeyEvent e) {}
                    
				});
				
				pan.setFocusable(true);
				pan.requestFocusInWindow();
				
				pan.validate();
				pan.repaint();
				frame.getContentPane().add(pan);
				frame.revalidate();
				
				nextItem.setEnabled(true);
				prevItem.setEnabled(true);
			}
		});
		
		fullscreenItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				com.apple.eawt.Application.getApplication().requestToggleFullScreen(frame);
			}
		});
		
		nextItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				((PhotoPanel) frame.getContentPane().getComponent(0)).next();
			}
		});
		
		prevItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				((PhotoPanel) frame.getContentPane().getComponent(0)).previous();
			}
		});
		
		splashPanel.setFocusable(true);
		splashPanel.requestFocusInWindow();
		splashPanel.validate();
		splashPanel.repaint();
		frame.getContentPane().add(splashPanel);
		frame.revalidate();
		
		frame.setVisible(true);
	}
	
	private static final class PhotoPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		PhotoAlbum album;
		
		boolean isSplash = false;
		
		boolean isPlaying = false;
		Timer t = null;
		
		@Override
	    protected void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			
			int panelHeight = this.getHeight();
			int panelWidth = this.getWidth();
			
			BufferedImage unsized = album.display();
			
			int scaledWidth = unsized.getWidth();
			int scaledHeight = unsized.getHeight();
			int xpos = (int)(panelWidth * .1);
			int ypos = (int)(panelHeight * .1);
			
			if(unsized.getWidth() > (int)(panelWidth * .8) && unsized.getHeight() > (int)(panelHeight * .8))
			{
				scaledHeight = (int)(panelHeight * .8);
				scaledWidth = (int)(panelWidth * .8);
			}
			else if(unsized.getWidth() > (int)(panelWidth * .8))
			{
				scaledWidth = (int)(panelWidth * .8);
				ypos = (int)((panelHeight - unsized.getHeight()) / 2);
			}
			else if(unsized.getHeight() > (int)(panelHeight * .8))
			{
				scaledHeight = (int)(panelHeight * .8);
				xpos = (int)((panelWidth - unsized.getWidth()) / 2);
			}
			else
			{
				scaledHeight = unsized.getHeight();
				scaledWidth = unsized.getWidth();
				ypos = (int)((panelHeight - unsized.getHeight()) / 2);
				xpos = (int)((panelWidth - unsized.getWidth()) / 2);
			}
	        
	        g.drawImage(unsized, xpos, ypos, scaledWidth, scaledHeight, null);
	        
	        if(!isSplash)
	        {
		        ImageIcon left = new ImageIcon("resources/icons/large left.png");
		        ImageIcon right = new ImageIcon("resources/icons/large right.png");
		        ImageIcon play = new ImageIcon("resources/icons/large play.png");
		        
		        g.drawImage(left.getImage(), 0, ((panelHeight - left.getIconHeight()) / 2), null);
		        g.drawImage(right.getImage(), (panelWidth - right.getIconWidth()), ((panelHeight - left.getIconHeight()) / 2), null);
		        g.drawImage(play.getImage(), ((panelWidth - play.getIconWidth()) / 2), (panelHeight - play.getIconHeight()), null);
	        }
       
	    }
		
		public void play() 
		{	
			if(isPlaying)
			{
				t.stop();
				t = null;
				isPlaying = false;
				return;
			}
			
			ActionListener taskPerformed = new ActionListener() 
			{
				public void actionPerformed(ActionEvent e)
				{
					next();
				}
			};
			
			t = new Timer(5000, taskPerformed);
			t.start();
			isPlaying = true;
		}

		public PhotoPanel()
		{
			super();
			album = new PhotoAlbum();
			isSplash = true;
		}
		
		public PhotoPanel(String path)
		{
			super();
			album = new PhotoAlbum(path);
			isSplash = false;
		}
		
		protected void next()
		{
			if(album.next() < 0)
				return;
			
			this.repaint();
		}
		
		protected void previous()
		{
			if(album.previous() < 0)
				return;
			
			this.repaint();
		}
	}

}
