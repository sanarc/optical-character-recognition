//attributes.java author Vivek R
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;



public class attributes {
	
	
	public IplImage binarize(IplImage img)
	{
		
		/*	The function Threshold applies fixed-level thresholding to grayscale image. The
			result is either a grayscale image or a bi-level image. The former variant is typically
			used to remove noise from the image, while the latter one is used to represent a
			grayscale image as composition of connected components and after that build contours
			on the components via the function FindContours. Figure 10-4 illustrates meanings of
			different threshold types
		*/
		
		//cvThreshold( IplImage* src, IplImage* dst, float thresh, float maxvalue,CvThreshType type);
		//This sets the threshold value and maxvalue and the type desired.<we consider here binary>
		//	CV_THRESH_BINARY = (val > thresh maxvalue:0)
		cvThreshold(img, img, 128, 255, CV_THRESH_BINARY);
		
		//Returns the binarized image
		return img;
	}
	
	
	//array function
	//This method in this class calculates the X and Y axis possible.
	public int[] findX(IplImage img)
	{
		int[] x=new int[2];
		
		//CvMat is a matrix function which has rows and cloumns initialized
		CvMat data = new CvMat();
		CvScalar val=cvRealScalar(0);
		int minfound=0;
		for (int i=0; i< img.width(); i++){
			cvGetCol(img, data,i);
			val= cvSum(data);
			if(val.val(0) < (double)( img.height())*255){
				x[1]=i;
				if(minfound==0){
					x[0]=i;
					minfound=1;
				}
				
			}
		}
		
		return x;
	}
	
	
	//this method in this class
	public int[] findY(IplImage img)
	{
		int[] y=new int[2];
		CvMat data = new CvMat();
		CvScalar val=cvRealScalar(0);
		int minfound=0;
		for(int i=0;i<img.height();i++)
		{
			cvGetRow(img, data,i);
			val= cvSum(data);
			if(val.val(0) < (double)( img.width())*255){
				y[1]=i;
				if(minfound==0){
					y[0]=i;
					minfound=1;
				}
				
			}
		}
		
		return y;
	}
	
	//Draws simple, thick or filled rectangle.
	/*	
	 * The function Rectangle draws a rectangle with two opposite corners pt1 and pt2. If
		the parameter thickness is positive or zero, the outline of the rectangle is drawn with
		that thickness, otherwise a filled rectangle is drawn.
	*/
	
	public CvRect bBox(IplImage img)///not skeleton image
	{
		int[] y=findY(img);
		int[] x=findX(img);
		
		//cvRectangle( IplImage* img, CvPoint pt1, CvPoint pt2, int color, int thickness );
		CvRect rect=new CvRect(x[0],y[0],x[1]-x[0],y[1]-y[0]);
		//cvRectangle(img,cvPoint(rect.x(),rect.y()),cvPoint(rect.x()+rect.width(), rect.y()+rect.height()),cvScalar(0, 0, 255, 0),1, 8, 0);
		
		return rect;
	}
	
	//This considers the features of individual characters and stores in an array which later helps in identifying the characters
	public int[] feature_extraction(IplImage img)
	{
		int[] feature=new int[27];
		
		int width=img.width();
		int height=img.height();
		
		
		//new image is created
		IplImage skel=cvCreateImage(cvGetSize(img), 8, 1);
		
		//call to seleton class
		skel=new skeleton().mainsub(skel);
		cvNot(skel, skel);
		CvRect rect=bBox(img);
		
		//image is copied by resizing 
		cvSetImageROI(img, rect);
		
		//interBox is an temp image considered which solves and calculates by putting a bounding box.
		IplImage interBox = cvCreateImage(cvGetSize(img),img.depth(),img.nChannels());
		cvCopy(img, interBox, null);
		cvResetImageROI(img);
		
		
		//These are the variables that describes the various attributes of the new image created.
		int onpix=0;
		int meanHorizontalDistance=0;
		int meanSqrHorDis=0;
		int meanSqrVerDis=0;
		int meanVerticalDistance=0;
		int centerWidth=interBox.width()/2;
		int centerHeight=interBox.height()/2;
		int meanHorPos=0;
		int meanVerPos=0;
		int meanProduct=0;
		int XcorY=0;
		int YcorX=0;
		int sumHorEdge=0;
		int sumVerEdge=0;
		int[] zoneciunt=new int[9];
		int partwidth = width / 3;
		int partheight = height / 3;
		
		
		//These try to put the image in different zones
		//pixvalues holds the info wrt that particular pixel
		CvScalar pixValues=new CvScalar();
		
		//scan the temp image
		for(int i=0;i<interBox.width();i++){
			for(int j=0;j<interBox.height();j++){
				pixValues=cvGet2D(interBox, j, i);
				if(pixValues.blue()==0 && pixValues.green()==0 && pixValues.red()==0)
					{
						onpix++;
						meanHorizontalDistance+=(i-centerWidth);
						meanVerticalDistance+=(j-centerHeight);
						meanSqrHorDis+=Math.pow(i-centerWidth, 2);
						meanSqrVerDis+=Math.pow(j-centerHeight, 2);
						XcorY+=Math.pow(i-centerWidth, 2)*(j-centerHeight);
						YcorX+=Math.pow(j-centerHeight, 2)*(i-centerWidth);
						
						if (i >= 0 * partwidth && i<= 1 * partwidth && j >= 0 * partheight
								&& j <= 1 * partheight) {
							zoneciunt[0]++;
						} else if (i >= 1 * partwidth && i <= 2 * partwidth
								&& j >= 0 * partheight && j <= 1 * partheight) {
							zoneciunt[1]++;
						} else if (i >= 2 * partwidth && i<= 3 * partwidth
								&& j >= 0 * partheight && j <= 1 * partheight) {
							zoneciunt[2]++;
						} else if (i >= 0 * partwidth && i<= 1 * partwidth
								&& j >= 1 * partheight && j <= 2 * partheight) {
							zoneciunt[3]++;
						} else if (i >= 1 * partwidth && i <= 2 * partwidth
								&& j >= 1 * partheight && j <= 2 * partheight) {
							zoneciunt[4]++;
						} else if (i >= 2 * partwidth && i <= 3 * partwidth
								&& j >= 1 * partheight && j <= 2 * partheight) {
							zoneciunt[5]++;
						} else if (i >= 0 * partwidth && i <= 1 * partwidth
								&& j >= 2 * partheight && j <= 2 * partheight) {
							zoneciunt[6]++;
						} else if (i >= 1 * partwidth && i <= 2 * partwidth
								&& j >= 2 * partheight && j <= 3 * partheight) {
							zoneciunt[7]++;
						} else if (i >= 2 * partwidth && i <= 3 * partwidth
								&& j >= 2 * partheight && j <= 3 * partheight) {
							zoneciunt[8]++;
						}
						
					}
			}
		}
		
		int[] data;
		int[] prevData;
		data=new int[skel.height()];
		prevData=new int[skel.height()];
		
		int horEdgeCnt=0;

		for(int i=0;i<skel.width();i++){
			for(int j=0;j<skel.height();j++)
			{
				pixValues=cvGet2D(skel, j, i);
				data[j]=(int)pixValues.blue();
				
				if((prevData[j]-data[j])<0)
				{
					
					horEdgeCnt++;
					sumHorEdge+=(i-centerWidth);
				}
				prevData[j]=data[j];
			}
		
		
		}
		
		data=new int[skel.width()];
		prevData=new int[skel.width()];
		
		int verEdgeCnt=0;

		for(int i=0;i<skel.height();i++){
			for(int j=0;j<skel.width();j++)
			{
				pixValues=cvGet2D(skel, i, j);
				data[j]=(int)pixValues.blue();
				
				if((prevData[j]-data[j])<0)
				{
					sumVerEdge+=(i-centerHeight);
					verEdgeCnt++;
				}
				prevData[j]=data[j];
			}
		
		
		}
		
		meanHorPos=meanHorizontalDistance/interBox.width();
		meanVerPos=meanVerticalDistance/interBox.height();
		meanSqrVerDis=meanSqrVerDis/interBox.height();
		meanSqrHorDis=meanSqrHorDis/interBox.width();
		meanProduct=meanHorizontalDistance*meanVerticalDistance;
		XcorY/=onpix;
		YcorX/=onpix;
		
		feature[0]=centerWidth;
		feature[1]=centerHeight;
		feature[2]=onpix;
		feature[3]=meanHorizontalDistance;
		feature[4]=meanVerticalDistance;
		feature[5]=meanSqrHorDis;
		feature[6]=meanSqrVerDis;
		feature[7]=meanProduct;
		feature[8]=XcorY;
		feature[9]=YcorX;
		feature[10]=horEdgeCnt;
		feature[11]=verEdgeCnt;
		feature[12]=sumHorEdge;
		feature[13]=sumVerEdge;
		feature[14]=meanHorPos;
		feature[15]=meanVerPos;
		feature[16]=interBox.width();
		feature[17]=interBox.height();
		feature[18]=zoneciunt[0];
		feature[19]=zoneciunt[1];
		feature[20]=zoneciunt[2];
		feature[21]=zoneciunt[3];
		feature[22]=zoneciunt[4];
		feature[23]=zoneciunt[5];
		feature[24]=zoneciunt[6];
		feature[25]=zoneciunt[7];
		feature[26]=zoneciunt[8];
				
	/*	System.out.println("Onpix "+onpix);
		System.out.println("meanHorizontalDistance "+meanHorizontalDistance);
		System.out.println("meanVerticalDistance "+meanVerticalDistance);
		System.out.println("meanSqrVerDis "+meanSqrVerDis);
		System.out.println("meanSqrHorDis "+meanSqrHorDis);
		System.out.println("meanProduct "+meanProduct);
		System.out.println("XcorY "+XcorY);
		System.out.println("YcorX "+YcorX);
		System.out.println("horEdgeCnt "+horEdgeCnt);
		System.out.println("verEdgeCnt "+verEdgeCnt);
		System.out.println("sumHorEdge "+sumHorEdge);
		System.out.println("sumVerEdge "+sumVerEdge);
		System.out.println("meanHorPos "+meanHorPos);
		System.out.println("meanVerPos "+meanVerPos);*/
		return feature;
	}
	
	//This method writes all the features identified ie its zone,height and other attributes calculated and writes to a text file
	//but the problem is im mot getting how exactly these values are calculated???
	public void mainsub(String[] filenames) {
		
		
		DataInputStream d = new DataInputStream(System.in);
		File xmlfile  = null;
		 RandomAccessFile access =null;
		try {
			 xmlfile = new File("test.db.txt");
			 access = new RandomAccessFile(xmlfile, "rw");
		
			 
			 access.writeBytes("word-database\nobject name charName symbolic centerWidth numerical centerHeight numerical onpix numerical meanHorizontalDistance numerical meanVerticalDistance numerical meanSqrHorDis numerical meanSqrVerDis numerical meanProduct numerical XcorY numerical YcorX numerical horEdgeCnt numerical verEdgeCnt numerical sumHorEdge numerical sumVerEdge numerical meanHorPos numerical meanVerPos numerical width numerical height numerical zone1 numerical zone2 numerical zone3 numerical zone4 numerical zone5 numerical zone6 numerical zone7 numerical zone8 numerical zone9 numerical\n\n\n");
			
			//access.seek(xmlfile.length());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
			  for(int fileitem=1;fileitem<filenames.length;fileitem++)
			  {
				  String strLine=filenames[fileitem];
				  
				 //System.out.println(strLine);
					int[] feature=new int[27];
					String charName = null;
				IplImage inputChar=cvLoadImage(strLine);
				inputChar=new attributes().binarize(inputChar);
				feature=new attributes().feature_extraction(inputChar);
				
				//cvShowImage("char",inputChar );
				//cvWaitKey();
				charName="uk";
				try {
					access.writeBytes("char"+charName+" "+charName+" ");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int j=0;j<27;j++)
				{
					try {
						String temp;
						if(j==26)  temp=feature[j]+"\n";
						else   temp=feature[j]+" ";
						access.writeBytes(temp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				  
				  
			  }
				
			
	
		}
		
}


