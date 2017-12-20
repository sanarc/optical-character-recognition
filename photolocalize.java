import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;


import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import static com.googlecode.javacv.cpp.opencv_objdetect.*;




//This class helps in seperating the image into text and nontext in it.
//Later it considers the text one and calculates the pixel
public class photolocalize {

	/**
	 * @param args
	 */
	

	
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
	
	public  double[] splitconto(double[] a,int cnt,double avg)
	{
		double[] avgs=new double[2];
		
		double lowavg=0;
		int lowcnt=0;
		int highcnt=0;
		double highavg=0;
		for(int i=0;i<cnt;i++)
		{
			if(a[i]>avg)
			{
				highcnt++;
				highavg+=a[i];
			}
			
			if(a[i]<avg)
			{
				lowcnt++;
				lowavg+=a[i];
			}
		}
		lowavg/=lowcnt;
		highavg/=highcnt;
		if(highcnt>cnt/3)highavg/=1.3;else highavg/=1.9;
		if(lowcnt>cnt/3)lowavg/=1.9;else lowavg/=1.3;
		avgs[0]=lowavg;
		avgs[1]=highavg;
		
		
		 return avgs;
		 
	}
	public  double[] sort(double[] a,int cnt)
	{
		 int i, j;
		 double t=0;
		  for(i = 0; i < cnt; i++){
		  for(j = 1; j < (cnt-i); j++){
		  if(a[j-1] > a[j]){
		  t = a[j-1];
		  a[j-1]=a[j];
		  a[j]=t;
		  }
		  }
		  }
		return a;
	}
	
	public  IplImage findContur(IplImage image)
	{
		 IplImage grayImage = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);
		 cvThreshold(image, image, 150, 255, CV_THRESH_BINARY);
	     cvCvtColor(image, grayImage, CV_BGR2GRAY);
	     cvThreshold(grayImage, grayImage, 150, 255, CV_THRESH_BINARY);
	     cvSmooth( grayImage, grayImage, CV_BLUR, 3, 1 ,0,0);	       
		 IplImage img2=cvCreateImage(cvSize(grayImage.width(),grayImage.height()),grayImage.depth(), 1 );
		 cvMorphologyEx(grayImage,grayImage,img2,cvCreateStructuringElementEx(21,5,12,4,CV_SHAPE_RECT,null),CV_MOP_BLACKHAT,3);
		 cvThreshold(grayImage, grayImage, 110, 255, CV_THRESH_BINARY);
		
				//grayImage=new skeleton().mainsub(grayImage);
				 cvShowImage("text", grayImage);
				cvWaitKey();
			
		    //cvCanny(grayImage, grayImage, 10, 10*3, 3);
	        CvMemStorage mem;
	        CvSeq contours = new CvSeq();
	        CvSeq ptr = new CvSeq();
	      
	        mem = cvCreateMemStorage(0);
	        cvZero(image);
	        cvFindContours(grayImage, mem, contours, sizeof(CvContour.class) , CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

	       
	        int contourCnt=0;
	        double avg=0;
	        int cntcont=0;
	        for (ptr = contours; ptr != null; ptr = ptr.h_next())cntcont++;
	        double[] areaPerimArray=new double[cntcont];
	        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
	        	        	
	        	if(ptr.total()>8){
	        	double area=Math.abs(cvContourArea(ptr, CV_WHOLE_SEQ, 1));
		        double perim=ptr.total();
		       	double areaPerim=area/perim;
		    	areaPerimArray[contourCnt]=areaPerim;
		    	//System.out.println(areaPerim);
		    	avg+=areaPerim;;
	           	contourCnt++;
	        	}
	        }
	     
	        System.out.println(contourCnt);
	           
	        avg/=contourCnt;
	        areaPerimArray=sort(areaPerimArray,contourCnt);
	       
	    
	       
	        double[] avgs=new double[2];
	        avgs=splitconto(areaPerimArray,contourCnt,avg);
	        //avgs[0]/=2;
	      //  avgs[1]/=2;
	        double lowavg=avg-0.5*avg;
	        double hihgavg=0.5*avg+avg;
	        System.out.println(avgs[0]+" "+avgs[1]+" "+lowavg+" "+hihgavg);
	        int charcount=0;
	        
	        IplImage finall=cvCreateImage(cvGetSize(grayImage), 8, 1);
	        cvZero(finall);
	        
	        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {    
	        	double area=Math.abs(cvContourArea(ptr, CV_WHOLE_SEQ, 1));
	        	 double perim=ptr.total();
		       	double areaPerim=area/perim;
		       	areaPerim=areaPerim;
		       	if(areaPerim>=avgs[0]  && areaPerim<=avgs[1]){
		       		cvZero(grayImage);
		       		cvDrawContours(grayImage, ptr, CV_RGB(255,255,255), CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
		       	 cvNot(grayImage, grayImage);
		       	 CvRect rect=bBox(grayImage);
		       	 if(!((rect.width()/rect.height()>2)|| (rect.height()/rect.width()>2) )){
		       	cvDrawContours(finall, ptr, CV_RGB(255,255,255), CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
		       	charcount++;
		    
		       	 }
		       	}
	                     
	        }
	        
	       cvNot(finall,finall);
	      cvSaveImage("imag2.jpg", finall);
	      cvShowImage("final", finall);
	      cvWaitKey();
	      return finall;
	}
	
	public  IplImage harrdetect(IplImage img)
	{

		CvPoint pt1 = new CvPoint(), pt2 = new CvPoint();
		int scale = 1;
		IplImage image=img;
		IplImage imagecpy=image.clone();

		IplImage finall= cvCreateImage( cvGetSize(image),8,3);
		cvZero(finall);
		CvMemStorage storage = CvMemStorage.create();
		CvMemStorage storage1 = CvMemStorage.create();
		 CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad("WholeWord.xml"));
        
              
		CvSeq faces = cvHaarDetectObjects(image, classifier, storage, 1.2, 0,CV_HAAR_FEATURE_MAX);      
		for (int i = 0; i < (faces !=null? faces.total() : 0); i++) {
			 CvRect r = new CvRect(cvGetSeqElem(faces, i));
		     pt1.x(r.x()*scale);
		     pt2.x( (r.x()+r.width())*scale);
		     pt1.y( r.y()*scale);
		     pt2.y ( (r.y()+r.height())*scale);		     
		  cvSetImageROI(image, r);	
		  cvAdd(image, image, image, null);
		  cvResetImageROI(image);
		
			
		}
	for(int i=0;i<image.height();i++){
		for(int j=0;j<image.width();j++){
			CvScalar x=new CvScalar(cvGet2D(image, i, j));
			if(x.blue()==255 && x.green()==255 && x.red()==255){
				x=cvGet2D(imagecpy, i, j);
				cvSet2D(finall, i, j, x);
			}
		}
	}
	
	return finall;
	}


}
