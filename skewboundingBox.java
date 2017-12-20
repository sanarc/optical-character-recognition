import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.io.File;




//This is another which i not much of concern but this class in defining a bound of the image where characters are written in some different angle
public class skewboundingBox {


	 public Integer[] findX(IplImage imgSrc,Integer min, Integer max){
			int i;
			int minFound=0;
			CvMat data = new CvMat();
			Integer[] temp = new Integer[2];
	
			CvScalar val=cvRealScalar(0);
			//For each col sum, if sum < width*255 then we find the min
			//then continue to end to search the max, if sum< width*255 then is new max
			
			for (i=0; i< imgSrc.width(); i++){
			cvGetCol(imgSrc, data,i);
			val= cvSum(data);
			//if(val.val(0) > maxVal.val(0)){
			if(val.val(0) >0){
			max= i;
			if(minFound==0){
			min= i;
			minFound= 1;
			}
			}
			else if(val.val(0) <0 && minFound==1)
			{
			  break;
			}
			}
			temp[0]=min;
			temp[1]=max;
			return temp;
					
			};
			
			
		 public Integer[] findY(IplImage imgSrc,Integer min, Integer max){
			int i;
			int minFound=0;
			CvMat data=new CvMat();
			Integer[] temp = new Integer[2];
		//	CvScalar maxVal=cvRealScalar(imgSrc.width() * 255);
			CvScalar val=cvRealScalar(0);
			//For each col sum, if sum < width*255 then we find the min
			//then continue to end to search the max, if sum< width*255 then is new max
			for (i=0; i< imgSrc.height(); i++){
			cvGetRow(imgSrc, data, i);
			val= cvSum(data);
			//if(val.val(0) > maxVal.val(0)){
			if(val.val(0) >0){
			max=i;
			if(minFound==0){
			min= i;
			minFound= 1;
			}
			}
			//else if(val.val(0) <=0 && minFound==1)
			//{
			 // break;
			//}
			}
			//System.out.println(max+min);
			temp[0]=min;
			temp[1]=max;
			return temp;
			};
			
			public CvRect findBB(IplImage imgSrc){
				CvRect aux=new CvRect();
				Integer xmin=new Integer(0);
				Integer xmax =new Integer(0);
				Integer ymin=new Integer(0);
				Integer ymax=new Integer(0);
				Integer[] temp = new Integer[2];
				temp=findX(imgSrc, xmin, xmax);
				xmin=temp[0];
				xmax=temp[1];
				temp=findY(imgSrc, ymin, ymax);
				ymin=temp[0];
				ymax=temp[1];
				System.out.println(ymax+ymin);
				aux=cvRect(xmin, ymin, xmax-xmin, ymax-ymin);

				//printf("BB: %d,%d - %d,%d\n", aux.x, aux.y, aux.width, aux.height);

				return aux;

				};
				
			public void findChars(IplImage img, String num)
			{
				int i;
				int minFound=0;
				CvMat data = new CvMat();
				CvRect aux=new CvRect();
				Integer xmin=new Integer(0);
				Integer xmax =new Integer(0);
				Integer ymin=new Integer(0);
				Integer ymax=new Integer(0);			
				CvScalar val=cvRealScalar(0);
				Integer count=1;
				Integer[] temp = new Integer[2];
				//For each col sum, if sum > 0 then we find the min
				//then continue to end to search the max, if sum> 0 then is new max
				temp=findY(img, ymin, ymax);
				ymin=temp[0];
				ymax=temp[1];
				for (i=0; i< img.width(); i++){
				cvGetCol(img, data,i);
				val= cvSum(data);
				//if(val.val(0) > maxVal.val(0)){
				if(val.val(0) >0){
				xmax= i;
				if(minFound==0){
				xmin= i;
				minFound= 1;
				}
				}
				else if(val.val(0) <=0 && minFound==1)
				{
					minFound=0;
					aux=cvRect(xmin, ymin, xmax-xmin, ymax-ymin);
					cvRectangle(img,cvPoint(aux.x(),aux.y()),cvPoint(aux.x()+aux.width(), aux.y()+aux.height()),cvScalar(0, 0, 255, 0),1, 8, 0);
					cvSetImageROI(img, aux);
					IplImage chars = cvCreateImage(cvGetSize(img),img.depth(),img.nChannels());
					cvCopy(img, chars, null);
					//IplImage destination = cvCreateImage( cvSize(chars.width()*4 , chars.height()*4), chars.depth(), chars.nChannels() );

							  //use cvResize to resize source to a destination image
							//  cvResize(chars, destination);

							  // save image with a name supplied with a second argument
							        

					cvSaveImage("line"+num+"/char"+count.toString()+".jpg", chars);
					count++;
					cvResetImageROI(img);
					//break;
					
				}
				}
				return;
			}
				
			public IplImage findLines(IplImage img )
			{
				CvRect aux=new CvRect();
				Integer xmin=new Integer(0);
				Integer xmax =new Integer(0);
				Integer ymin=new Integer(0);
				Integer ymax=new Integer(0);
				int i;
				String strDirectoy="line";
				Integer count=1;
				int minFound=0;
				CvMat data=new CvMat();				
				CvScalar val=cvRealScalar(0);
				Integer[] temp = new Integer[2];
				temp=findX(img, xmin, xmax);
				xmin=temp[0];
				xmax=temp[1];
				//For each col sum, if sum < width*255 then we find the min
				//then continue to end to search the max, if sum< width*255 then is new max
				for (i=0; i< img.height(); i++){
				cvGetRow(img, data, i);
				val= cvSum(data);
				//if(val.val(0) > maxVal.val(0)){
				if(val.val(0) >0){
				ymax=i;
				if(minFound==0){
				ymin= i;
				minFound= 1;
				}
				}
				else if(val.val(0) <=0 && minFound==1)
				{
					minFound=0;
					strDirectoy="line"+count.toString();
					aux=cvRect(xmin, ymin, xmax-xmin, ymax-ymin);
					
					cvSetImageROI(img, aux);
					IplImage line = cvCreateImage(cvGetSize(img),img.depth(),img.nChannels());
					cvCopy(img, line, null);
					new File(strDirectoy).mkdir();
					findChars(line,count.toString());
					cvSaveImage("./line"+count.toString()+".jpg", line);
					cvResetImageROI(img);
					count++;
					cvRectangle(img,cvPoint(aux.x(),aux.y()),cvPoint(aux.x()+aux.width(), aux.y()+aux.height()),cvScalar(0, 0, 255, 0),1, 8, 0);
					
				}
				}
				//System.out.println(max+min);
				
				return img;
				
			}
	
}
