package russosoftware.src;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

public class Window extends JFrame
{
	private static final long serialVersionUID = -8317348312113372603L;
	
	public static final String polyHTMLURL = "http://dl.dropbox.com/u/38453115/WritingPolynomials.html";
	public static final String zeroHTMLURL = "http://dl.dropbox.com/u/38453115/FindingZeroes.html";
	public static final String projInfoHTMLURL = "http://dl.dropbox.com/u/38453115/ProjectInfo.html";
	
	public volatile boolean isShutdownRequested = false;
	
	public Window(int width, int height, String windowTitle)
	{
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		ImageIcon findingZeroesImage = getImageIcon("res/animatedSynthDiv.gif");
		ImageIcon writingPolynomials = getImageIcon("res/poly.png");
		ImageIcon projInfoImg = getImageIcon("res/info.png");
		
		String writPolyHTML;
		try 
		{
			writPolyHTML = getHTML("res/WritingPolynomials.html");
		} 
		catch (IOException e) 
		{
			writPolyHTML = attemptHTMLBackupRetrieval(polyHTMLURL); 
		}
		
		String findZeroHTML;
		try 
		{
			findZeroHTML = getHTML("res/FindingZeroes.html");
		} 
		catch (IOException e) 
		{
			findZeroHTML = attemptHTMLBackupRetrieval(zeroHTMLURL);
		}
		
		String projInfoHTML;
		try 
		{
			projInfoHTML = getHTML("res/ProjectInfo.html");
		} 
		catch (IOException e) 
		{
			projInfoHTML = attemptHTMLBackupRetrieval(projInfoHTMLURL);
		}
		
		System.out.println(findZeroHTML);
		
		JPanel panel1 = createPanel(writPolyHTML);
		JPanel panel2 = createPanel(findZeroHTML);
		JPanel panel3 = createPanel(projInfoHTML);
		tabbedPane.addTab("Writing Polynomials", writingPolynomials, panel1, "Explains Writing Polynomials");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("Finding Zeros of Polynomials", findingZeroesImage, panel2, "Explains how to find Zeros");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.addTab("Project Information", projInfoImg, panel3, "Detailed Information about the Project");
		
		add(tabbedPane);
		
		this.setMinimumSize(new Dimension(width, height));
		this.setTitle(windowTitle);
		this.setVisible(true);
		JOptionPane.showMessageDialog(this, "The two topics written above, \"Writing Polynomials\" and \"Finding Zeroes of Polynomials\", are tabs. Select the tab for whichever topic you would like to view.");
	}

	private String attemptHTMLBackupRetrieval(String htmlAddress) 
	{
		StringBuilder htmlCode = new StringBuilder();
		try 
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(htmlAddress).openConnection();
			connection.connect();
			if(connection.getResponseCode() == 200)
			{
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				while(bis.available() > 0)
				{
					htmlCode.append((char)bis.read());
				}
			}
			else
				htmlCode.append("<html><h1>Unable to Retrieve HTML</h1><p>View Backup version of this " + formHTMLLink(htmlAddress, "Here") + "</html>");
			
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlCode.toString();
	}

	private String formHTMLLink(String string, String textToDisp) 
	{
		String htmlLink = "<a href=\"" + string + "\" target=\"_blank\">" + textToDisp + "</a>";
		return htmlLink;
	}

	private String getHTML(String location) throws IOException 
	{
		StringBuilder buildableString = new StringBuilder();
		BufferedInputStream htmlStream = new BufferedInputStream(getClass().getResourceAsStream(location));
		while(htmlStream.available() > 0)
		{
			buildableString.append((char)htmlStream.read());
		}
		
		return buildableString.toString();
	}

	private JPanel createPanel(String htmlString) 
	{
		JPanel panel = new JPanel(false);
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		JEditorPane label = new JEditorPane();
		label.addHyperlinkListener(new OpenURLAction());
		label.setEditable(false);
		label.setContentType("text/html");
		label.setEditorKit(htmlKit);
		label.setText(htmlString);
		label.setAlignmentX(JEditorPane.CENTER_ALIGNMENT);
		panel.setLayout(new GridLayout(1, 1));
		JScrollPane pane = new JScrollPane(label);
		panel.add(pane);
		return panel;
	}

	private ImageIcon getImageIcon(String filePath) 
	{
		URL imagePath = this.getClass().getResource(filePath);
		if(imagePath != null)
			return new ImageIcon(imagePath);
		
		System.err.println("Couldn't find file: " + filePath);
		return null;
		
	}
	
	private class OpenURLAction implements HyperlinkListener
	{
		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) 
		{
			if(e.getEventType() == EventType.ACTIVATED)
			{
				if(e.getURL() != null)
				{
					if(Desktop.isDesktopSupported())
						try 
						{
							Desktop.getDesktop().browse(e.getURL().toURI());
						} 
						catch (IOException e1) 
						{
							e1.printStackTrace();
						} 
						catch (URISyntaxException e1) 
						{
							e1.printStackTrace();
						}
					else
						System.err.println("URL Navigation Not Supported");
				}
				else
					System.err.println("The URL is null!");
			}
		}
	}
	
	public static void main(String args[])
	{
		Window w = new Window(1500, 600, "Marking Period 3 Math Project");
	}
}
