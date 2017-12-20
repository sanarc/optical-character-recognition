import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;



//???? similar to curve and bounding box and part of attributes
//It calculates again some features called momentfeature and calculates the X and Y and writes in db and tree is accessed??
public class humomentsDoc {

	/**
	 * @param args
	 */
	
	public IplImage binarize(IplImage img)
	{
		cvThreshold(img, img, 128, 255, CV_THRESH_BINARY);
		return img;
	}
	public double Round(double Rval, int Rpl) {
		double p = Math.pow(10, Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (float) tmp / p;
	}
	public int[] findX(IplImage img)
	{
		int[] x=new int[2];
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
	
	public CvRect bBox(IplImage img)///not skeleton image
	{
		int[] y=findY(img);
		int[] x=findX(img);
		CvRect rect=new CvRect(x[0],y[0],x[1]-x[0],y[1]-y[0]);
		//cvRectangle(img,cvPoint(rect.x(),rect.y()),cvPoint(rect.x()+rect.width(), rect.y()+rect.height()),cvScalar(0, 0, 255, 0),1, 8, 0);
		
		return rect;
	}
	
	public Double[] humomDoc(IplImage img)
	{
		Double[] Momentfeature=new Double[18];
		CvRect rect=bBox(img);
		cvSetImageROI(img, rect);
		IplImage interBox = cvCreateImage(cvGetSize(img),img.depth(),img.nChannels());
		cvCopy(img, interBox, null);
		cvResetImageROI(img);
		
		IplImage skel=new skeleton().mainsub(interBox);
		cvNot(skel, skel);
		int onpix=0;
		int offpix=0;
		CvScalar pixValues=new CvScalar();
		for(int i=0;i<interBox.width();i++){
			for(int j=0;j<interBox.height();j++){
				pixValues=cvGet2D(interBox, j, i);
				if(pixValues.blue()==0 && pixValues.green()==0 && pixValues.red()==0)
				{
					onpix++;
				}
				else {
					offpix++;
				}
			}
		}
		
		
		double donpix=onpix;
		double doffpix=offpix;
		double widht=interBox.width();
		double height=interBox.height();
		double pixratio=onpix/doffpix;
		
		double sizeration=widht/height;
		CvMoments moments=new CvMoments();
		//CvHuMoments humoments=new CvHuMoments();
		//First calculate object moments
		 cvMoments(skel, moments, 0);
		 //Now calculate hu moments
		//cvGetHuMoments(moments, humoments);
		
		 Momentfeature[0]=moments.m00();
		 Momentfeature[1]=moments.m01();
		 Momentfeature[2]=moments.m02();
		 Momentfeature[3]=moments.m03();
		 Momentfeature[4]=moments.m10();
		 Momentfeature[5]=moments.m11();
		 Momentfeature[6]=moments.m20();
		 Momentfeature[7]=moments.m21();
		 Momentfeature[8]=moments.m30();
		 Momentfeature[9]=moments.mu02();
		 Momentfeature[10]=moments.mu03();
		 Momentfeature[11]=moments.mu11();
		 Momentfeature[12]=moments.mu12();
		 Momentfeature[13]=moments.mu20();
		 Momentfeature[14]=moments.mu21();
		 Momentfeature[15]=moments.mu30();
		 Momentfeature[16]=pixratio;
		 Momentfeature[17]=sizeration;
		 
		
		//for(int i=0;i<22;i++)System.out.println(Momentfeature[i].intValue());
		 return Momentfeature;
		 //11,12,16,23
		 
	}
	
	public  void mainsub(String[] filenames) {
	
		DataInputStream d = new DataInputStream(System.in);
		File xmlfile  = null;
		 RandomAccessFile access =null;
		try {
			 xmlfile = new File("test.db.txt");
			 access = new RandomAccessFile(xmlfile, "rw");
		
			 
			 access.writeBytes("word-database\nobject name charName symbolic mom1 numerical mom2 numerical mom3 numerical mom4 numerical mom5 numerical mom6 numerical mom7 numerical mom8 numerical mom9 numerical mom10 numerical mom11 numerical mom12 numerical mom13 numerical mom14 numerical mom15 numerical mom16 numerical pixratio numerical sizeratio numerical\n");
			 
			 // 
			
			//access.seek(xmlfile.length());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
			
			 
			  //Read File Line By Line
			  for(int fileitem=1;fileitem<filenames.length;fileitem++)
			  {
				  String strLine=filenames[fileitem];
					Double[] feature=new Double[18];
					String charName = null;
				IplImage inputChar=cvLoadImage(strLine);
				//inputChar=new attributes().binarize(inputChar);
				feature=new humomentsDoc().humomDoc(inputChar);
				//System.out.println("Enter the char name");
				//cvShowImage("char",inputChar );
				//cvWaitKey();
				try{
					//charName=d.readLine();
					charName="uk";
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				try {
					access.writeBytes("char"+charName+" "+charName+" ");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int j=0;j<18;j++)
				{
					try {
						String temp;
						int fet=feature[j].intValue();
						if(j==16){
							DecimalFormat myformat=new DecimalFormat("0.00000");
							String temp1=myformat.format(feature[j]) ;
							temp=temp1+" ";
						}
						else if(j==17)  temp=feature[j]+"\n";
						else   temp=fet+" ";
						access.writeBytes(temp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
			  }
		
		try {
			access.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
