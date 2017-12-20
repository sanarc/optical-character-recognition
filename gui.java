import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import java.io.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;




//This is the main frame class which includes smaller parts of the main GUI look//
public class gui extends JFrame {
	public String text=new String();
	private JPanel contentPane;
	private JTextField t1;
	public static String tp;
	public static String segmover="false";
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	/**
	 * Launch the application. At this point the program launches and provide GUI for input and output
	 */
	
	//This function is to validate whether the input image is valid one.It considers .jpg,.bmp,.png as image files.
	//If the user inputs any other file with a different extension other than the described it rejects it.
	public boolean isImage(String filename)
	{
		boolean value=false;
		
			if(filename.indexOf(".jpg")>=0 ||
				filename.indexOf(".JPG")>=0 ||
				filename.indexOf(".bmp")>=0 ||
				filename.indexOf(".BMP")>=0 ||
				filename.indexOf(".png")>=0 ||
				filename.indexOf(".PNG")>=0 
				)value=true;
		return value;
	}
	
	
	//This is the main function where objects are created and executed.
	//UI manager is used to change the apperance of the GUI.
	//this also needs to have the appropriate UI JAR file to be loaded else it wont show the appropriate desired look.
	//this application uses QUAQUA which is like IOS GUI.
	//UI manager is similar to mobile profile where it selects a valid profile and it outputs the same looks described in that profile.
	//If this quaqua is not appropriately loaded and gives error just comment the UImanager line to carry out the normal execution.
	public static void main(String[] args) {
		//Exception is used to validate whether it recognizes the UI and loads.
		try {
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			//This is similar to thread where it runs and executes the UI
			//i.e.,the default will be running as usual but this method provides the virtual look that the described UI is being executed.
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
					gui frame = new gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * This method describes the appropriate measurement and dimensions of the GUI.
	 */
	public gui() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Defaultly exit button is provided on the frame
		setBounds(100, 100, 616, 534);
		contentPane = new JPanel();
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		/**
		 * String n = "\u0C92" ;
		 * string n = Character.UnicodeBlock.KANNADA.("\u0c92") ;
		 * JLabel lblInputFile = new JLabel("MLae");
		 * lblInputFile.setFont(new Font("Nudi 07 e", Font.PLAIN, 24));
		 * This small snippet shows how exactly we can change the font and font style
		 * As discovered by me,Java i mean eclipse supports kannada as a language default one
		 * Inorder to use it properly we can select the unicodes for each letter.
		 * The use of unicode block function idk but "MLae" in NUDI font describes some kannada letter and the same is shown during execution.
		 * Now we just need to know for what keystroke which letter is the output!!! :P		
		 */
		JLabel lblInputFile = new JLabel("Input File");
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblInputFile.setBounds(43, 120, 74, 19);
		contentPane.add(lblInputFile);
		
		t1 = new JTextField();
		//.setFont(new Font("Nudi 01 e",Font.PLAIN,20));
		t1.setBounds(139, 113, 211, 33);
		contentPane.add(t1);
		t1.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBrowse.addActionListener(new ActionListener() {
		
			//This method validates the input generally.No complex logic is being described.
			//Here in this method it describes the various error messages and operations done on clicking the appropriate button.
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser chooser = new JFileChooser();
		        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        
		        //Validation of file input extension
		        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images","JPG","png","PNG", "jpg", "bmp","BMP");
				chooser.setFileFilter(filter);
                 
				
				//Browse window provided to choose the appropriate file by finding it in appropriate directories
		        String filename=new String();
		        String ftp=new String();
		        int returnVal = chooser.showOpenDialog(null);
		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		        	filename=chooser.getSelectedFile().getAbsolutePath();
		        	ftp=filename;
		        	System.out.println("Browse at"+filename);
		            t1.setText(chooser.getSelectedFile().getAbsolutePath());
		        }
                
		        
		        //describes the error condition on choosing the inappropriate image file or its not present
		        File file=new File(filename);
		         if(!isImage(filename))
		         {
		        	 JFrame frame=new JFrame();
		        	 JOptionPane.showMessageDialog(frame, "Choosen file is not an Image file!\nChoose file with extension '.jpg' or '.bmp' or '.png'");
		        	   t1.setText(""); 
		         }
		         else if(!file.exists()){
		        	 JFrame frame=new JFrame();
		        	   JOptionPane.showMessageDialog(frame, "Choosen file does not exists !");
		        	   t1.setText("");
		           }
				
				
				
			}
		});
		btnBrowse.setBounds(378, 112, 89, 34);
		contentPane.add(btnBrowse);
		
		
		//Describes the Main label on the window
		JLabel lblOpticalCharacterRecognition = new JLabel("Optical Character Recognition");
		lblOpticalCharacterRecognition.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 19));
		lblOpticalCharacterRecognition.setBounds(137, 14, 357, 33);
		contentPane.add(lblOpticalCharacterRecognition);
		
		
		//These are the checkbox provided to describe what input file is.
		//Since we are not dealing with these its better to inactive these!!! ??? -<ask>
		final JCheckBox chckbxDeskew = new JCheckBox("De-Skew");
		chckbxDeskew.setBounds(127, 158, 97, 23);
		contentPane.add(chckbxDeskew);
		
		final JCheckBox chckbxPhotograph = new JCheckBox("Photograph");
		buttonGroup.add(chckbxPhotograph);
		chckbxPhotograph.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				
			}
		});
		chckbxPhotograph.setBounds(241, 158, 97, 23);
		contentPane.add(chckbxPhotograph);
		
		final JCheckBox chckbxScannedFile = new JCheckBox("Scanned File");
		buttonGroup.add(chckbxScannedFile);
		chckbxScannedFile.setBounds(360, 158, 119, 23);
		contentPane.add(chckbxScannedFile);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(137, 254, 330, 169);
		contentPane.add(scrollPane);
		
		final JTextArea t2 = new JTextArea();
		t2.setEditable(false);
		
		scrollPane.setViewportView(t2);
		t2.setLineWrap(true);
		
		
		//This is the key component of this class.
		//This executes the internal OCR methods to get the desired output.
		JButton btnRunOcr = new JButton("Run OCR");
		btnRunOcr.setFont(new Font("Tahoma", Font.PLAIN, 14));
		

		//Actions/events to be overviwed again before the procedure of conversion starts
		btnRunOcr.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				String s=t1.getText();
				
				//Checks and displays whether the input field is blank or not
				
				if(!(s.isEmpty()))
				{
					
					 if(!isImage(s))
			         {
			        	 JFrame frame=new JFrame();
			        	 JOptionPane.showMessageDialog(frame, "Choosen file is not an Image file!\nChoose file with extension '.jpg' or '.bmp' or '.png'");
			        	   t1.setText(""); 
			         }
					 else
					 {
						   File filec=new File(s);
						 
						  if(!filec.exists()){
				        	 JFrame frame=new JFrame();
				        	   JOptionPane.showMessageDialog(frame, "Choosen file does not exists !");
				        	   t1.setText("");
				           }
						  
						  
						  //if all satisfies then it is sent to the process of ocr.
						  else
						  {
						 segmover="true";
				
				String s1;
				s1=t1.getText();
				System.out.println(s1);
				boolean status=chckbxDeskew.isSelected();
				boolean photo=chckbxPhotograph.isSelected();
				boolean scan=chckbxScannedFile.isSelected();
				
				//This apprunner is an important class where the input image is passed on to it and the process starts
				//i.e., the conversion/process of OCR starts.
				apprunner a=new apprunner();
				text=a.path(s1,status,photo,scan);
				t2.setText(text);
				//System.out.println(text);
					 }	
					 }
				}
				else
				{
					 JFrame frame=new JFrame();
					JOptionPane.showMessageDialog(frame, "Choose a Image File");
				}
				
			}
		});
		
		btnRunOcr.setBounds(191, 203, 211, 40);
		contentPane.add(btnRunOcr);
		contentPane.setEnabled(false);
		
		
		//This describes the output text field.
		//This will show the desired output 
		//This text field will provide deired text from input image file once the entire process gets over.
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		//lblOutput.setFont(new Font("Nudi 01 e",Font.PLAIN,20));
		lblOutput.setBounds(59, 256, 58, 17);
		contentPane.add(lblOutput);
		
		
		//Provides an option to save the output inorder to digitize.
		JButton btnNewButton = new JButton("Save");
		
		//This describes and provides the directories to the user where he can save this output.
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (!(t2.getText().equals("")))
				{
					 	
				
				JFileChooser chooser = new JFileChooser();
				
		        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		     
			File dir= new File("E:\\");
			chooser.setCurrentDirectory(dir);
			
				
		        String filename=new String();
		        int returnVal = chooser.showSaveDialog(null);
		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		           filename=chooser.getSelectedFile().getAbsolutePath();
		           System.out.println("Save at"+filename);
		           
		        }
		       
		        //Execption on whether successful in saving or not
		        try {
		        	 File savefile=new File(filename);
					FileWriter wr=new FileWriter(savefile);
					wr.write(t2.getText());
					wr.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}
				
				//if the process is still being carried out then it symbolizes that the process is still being processed
				//so it displays the error message
				else
				{
					JFrame frame=new JFrame();
					JOptionPane.showMessageDialog(frame, "Wait!! Till the process completes");
		
				}
			}
		});
		btnNewButton.setBounds(135, 435, 153, 33);
		contentPane.add(btnNewButton);
		
		
		//this button is provided which symbolizes exit and terminate the application
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setBounds(300, 434, 167, 33);
		contentPane.add(btnExit);
		
		 int min = 0;
		 int max = 100;
		
		 Font font = new Font("Nudi 01 e", Font.BOLD, 20);
		 t2.setFont(font);
		 t2.setForeground(Color.BLUE);

		 
		String p=t2.getText();
	
	
	}
}
