import static com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

//This class is not much of concern in our project but plays a major role in placing/rotating the image to the exact correct position..
//Which helps us in/helps itself to recognize the characters.
public class skew {
	public double Round(double Rval, int Rpl) {
		double p = Math.pow(10, Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (float) tmp / p;
	}
	
	public double skewness(IplImage image)
	{
		IplImage gray = cvCreateImage(cvSize(image.width(),image.height()), IPL_DEPTH_8U, 1);
		if(image.nChannels()==3) cvCvtColor(image, gray, CV_BGR2GRAY);
		else gray=image;
		cvNot(gray, gray);
		CvSize size=new CvSize(gray);
		 CvMemStorage storage = cvCreateMemStorage(0);
	        CvSeq lines=new CvSeq();
	        CvSeq lines1=new CvSeq();
	      //  cvCanny(gray, gray, 50, 200, 3);
		lines=cvHoughLines2(gray, storage,CV_HOUGH_PROBABILISTIC, 1, Math.PI/180, 31,15, 11);
		//lines1=cvHoughLines2(gray, storage,CV_HOUGH_PROBABILISTIC, 1, Math.PI/180, 33,0, 10);
	   // lines=cvHoughLines2(gray, storage,CV_HOUGH_PROBABILISTIC, 1, Math.PI/180, 10,0, 10);
		  double angle = 0;
		  int nb_lines = lines.total();
		  //System.out.print(nb_lines);
		  for (int i = 0; i <nb_lines; i++)
		    {
			 
			  Pointer line = cvGetSeqElem(lines, i);
             CvPoint pt1  = new CvPoint(line).position(0);
             CvPoint pt2  = new CvPoint(line).position(1);
             angle += Math.atan2(pt2.y() - pt1.y(), pt2.x() - pt1.x());
             cvLine(image, pt1, pt2, CV_RGB(255, 0, 0), 1, CV_AA, 0); 		 
		    }
		  int nb_lines1=lines1.total();
		  for (int i = 0; i <nb_lines1; i++)
		    {
			 
			  Pointer line = cvGetSeqElem(lines1, i);
           CvPoint pt1  = new CvPoint(line).position(0);
           CvPoint pt2  = new CvPoint(line).position(1);
           angle += Math.atan2(pt2.y() - pt1.y(), pt2.x() - pt1.x());
          			 
		    }
		 angle/=(nb_lines+nb_lines1)*22/7 ;
			angle*=180;
			
			cvShowImage("skew", image);
			cvWaitKey();
			
			  System.out.println("skew angle"+angle);
			  angle=(int)Math.round(angle);
			  System.out.println("skew angle"+angle);
		  return angle;
	};
	
	public IplImage correctskew(double angle,IplImage image)
	{
		
		IplImage gray = cvCreateImage(cvSize(image.width(),image.height()), IPL_DEPTH_8U, 1);
		if(image.nChannels()==3) cvCvtColor(image, gray, CV_BGR2GRAY);
		else gray=image;
		 cvNot(gray, gray);
		
		 CvMat img=new CvMat(gray);
		
		 skewboundingBox bb=new skewboundingBox();
		 CvRect aux=new CvRect();
		 aux=bb.findBB(gray);
		 
		 IplImage rotatedImage = cvCreateImage(cvSize(gray.width(),gray.height()), IPL_DEPTH_8U,gray.nChannels());
		 cvSet(rotatedImage, cvScalar(255, 255, 255, 0));
		 
		// cvZero(rotatedImage);
		 CvPoint2D32f center = new CvPoint2D32f();
		 center.x(aux.x()+aux.width()/2);
		 center.y(aux.y()+aux.height()/2);
		 CvMat mapMatrix = cvCreateMat( 2, 3, CV_32FC1 );
		 cv2DRotationMatrix(center, angle, 1, mapMatrix);
		 cvWarpAffine(img, rotatedImage, mapMatrix, CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS, cvScalarAll(0));
		 cvNot(rotatedImage, rotatedImage);
		 
		 return rotatedImage;
	};

}
