import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import be.ac.ulg.montefiore.run.jadti.FileFormatException;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;



//The input image which is appropriately validated is sent to this class for further process of conversion
public class apprunner {

	public static String path1=null;
	
	//OpenCV is designed to be used together with Intel® Image Processing Library (IPL)
	//and extends the latter functionality toward image and pattern analysis. Therefore,
	//OpenCV shares the same image format (IplImage) with IPL.
	
	//IPL reference manual gives detailed information
	//about the format, but, for completeness, it is also briefly described here.
	/*
	 * IPLImage structure
	 typedef struct _IplImage {
	 int nSize; // size of iplImage struct 
	 int ID; // image header version 
	 int nChannels;
	 int alphaChannel;
	 int depth; //pixel depth in bits 
	 char colorModel[4];
	 char channelSeq[4];
	 int dataOrder;
	 int origin;
	 int align; //4- or 8-byte align 
	 int width;
	 int height;
	 struct _IplROI *roi; //pointer to ROI if any
	 struct _IplImage *maskROI; //pointer to mask ROI if any
	 void *imageId; //use of the application
	 struct _IplTileInfo *tileInfo; //contains information on tiling
	 int imageSize; // useful size in bytes 
	 char *imageData; //pointer to aligned image 
	 int widthStep; //size of aligned line in bytes 
	 int BorderMode[4]; //the top, bottom, left,and right border mode 
	 int BorderConst[4]; //constants for the top, bottom,left, and right border
	 char *imageDataOrigin; //ptr to full, nonaligned image
	 } IplImage;

	 */
	
	
	//This method describes whether the image is black or white
	//IPLImage is an API which tells about the image size,pixel depth,color model and various references wrt the image.
	//Returns color  and ratio of each pixel
	
	public boolean isbw(IplImage img)
	{
		boolean color=false;
		
		//Calculate the total no. of pixels in the image which latter helps in easy processing
		int totpix=img.width()*img.height();
		
		//calculates the pixel values
		CvScalar pixValues=new CvScalar();
		
		//initially a variable is declared and set to 0.later,on scanning each and every pixel it is counted 
		int blackWhitePix=0;
		
		//This loops checks/scans the each and every pixel and keeps track of the count
		for(int i=0;i<img.width();i++)
		{
			for(int j=0;j<img.height();j++)
			{
				pixValues=cvGet2D(img, j, i);
				if((pixValues.blue()>250 && pixValues.green()>250 && pixValues.red()>250)||
				(pixValues.blue()<30 && pixValues.green()<30 && pixValues.red()<30)	){
					blackWhitePix++;
				}
				
			}
		}
		if((double)blackWhitePix/(double)totpix*100>60)
		{
			color=true;
		}
		System.out.println("color ratio:blact to white"+(double)blackWhitePix/(double)totpix*100);
		return color;
	}

	//returns the path of the image
	public String path(String s,boolean status,boolean photo,boolean scan)
	{
		path1=s;
		return mainsub(status,photo,scan);
	}
	
	
	/**
	 * @param args
	 * This method is used to build a database where all important things are kept/recorded.
	 */
	public  String mainsub(boolean status,boolean photo,boolean scan) {
		// TODO Auto-generated method stub
		  File testdb=new File("test.db.txt");
		  if(testdb.exists())testdb.delete();
		String[] filenames ;
		String[] ss;
		String tempfilenames=new String();
		String spacesequence=new String();
		
		//cvLoadImage is a function where it loads the image by taking the path as an argument to IPLImage to get the info of the image.
		//not much to think on it since it considers the checkbox selected so that is not much considered in our project so thinking of dropping it.
		 IplImage image =cvLoadImage(path1, 1);
		 boolean b;
		 b=isbw(image);
		 if(b&&photo)
		 {JFrame frame= new JFrame();
			int x;
			x= JOptionPane.showConfirmDialog(frame, "You have selected an image that seems to be a black and white, but selected option is photograph. Would you like to change ? ");
			if(x==JOptionPane.YES_OPTION)
			{
				photo=false;
				JOptionPane.showMessageDialog(frame, "Option changed to Non-Photograph");
			}
		 }
		 
		 
		 if(!b&&!photo)
		 {JFrame frame= new JFrame();
			int x;
			//x= JOptionPane.showConfirmDialog(frame, "You have selected an image that seems to be a Photograph, but option is not selected as Photograph. Would you like to change ? ");
			x= JOptionPane.showConfirmDialog(frame, "You have selected an image that seems to be a Photograph.Would you like to change ? ");
			if(x==JOptionPane.YES_OPTION)
			{
				photo=true;
				JOptionPane.showMessageDialog(frame, "Option changed to Photograph");
			}
		 }

		 
		 if(photo)
		 {
			 try {
				 String pText=new String();
				 pText=new test().submain(image);
				 if(testdb.exists())testdb.delete();
				return pText;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 ///////////
		 //keeps track of character count ????
		 File f;
		  f=new File("charactercount.txt");
		  if(!f.exists()){
		  try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  }
		

		 ///////////
		 //this is sent to skew class
		 	skew skewobj=new skew();
		 	
		 	//image is skewed and count is kept.
		 	//This makes the image to the desired angle that helps in processing of the image.
		 	//This helps to recognize characters even if the image is in different angle.
		 	int charcount=0;
		 	if(status)
		 	{
		 		IplImage temp=image.clone();
				double angle=skewobj.skewness(temp);
				image=skewobj.correctskew(angle, image);
				cvShowImage("testskew", image);
				cvWaitKey();
		 	}
			
			try {
				
				//control flows to boundingbox where lines and characters are counted.<skewed image is sent> 
				tempfilenames=new boundingBox().findLines(image);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			spacesequence=boundingBox.spaceSeq;
			//System.out.println(spacesequence);
			
			
			//each character is seperated and detected.
			ss=spacesequence.split(",");
			System.out.println("spaces "+ss.length);
			filenames=tempfilenames.split(",");
		    //System.out.println("Number of chars are "+filenames.length);
		    charcount=filenames.length;
		    String text=new String();
		    new humomentsDoc().mainsub(filenames);
		    try {
		    	
		    	new tree_builder();
				//the count is stored in tree which helps latter in postprocessing.
				text=tree_builder.mainsub(charcount-1,photo,scan,ss);
				
			} catch (FileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     
		  
		//returns text which is built latter.
		return text;
	}

}
