import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


//This class is again one more important which decides the character area.
//This class has the main important job of localizing and segmenting is what we know so far.
public class boundingBox {
	
	static int countchars=0;
	String filenames=new String();
	static String spaceSeq=new String();
	
	public IplImage joinImage(IplImage prev,IplImage curr)
	{
		CvSize newSize=new CvSize(prev.width()+curr.width(), prev.height());
		IplImage joinedImage=cvCreateImage(newSize, 8, 1);
		cvSetImageROI(joinedImage, cvRect(0, 0, prev.width(), prev.height()));
		cvCopy(prev, joinedImage);
		cvResetImageROI(joinedImage);
		cvSetImageROI(joinedImage, cvRect(prev.width(), 0, curr.width(), curr.height()));
		cvCopy(curr, joinedImage);
		cvResetImageROI(joinedImage);
	//	cvShowImage("joined", joinedImage);
	//	cvWaitKey();
		return joinedImage;
	}
	
	
	//This method is used to identify the characters from the file we wrote all the features into in the attribute class.
	public void findChars(String imgName, String num) throws FileNotFoundException
	{
		
		// space code
			
		
			
				 int countch=0;
				 String strLine=null;
				  
					//Number of characters in the image are calculated.
					File chc1 = new File("charactercount.txt");
					 BufferedReader input =  new BufferedReader(new FileReader(chc1));
					 try {
						 strLine = input.readLine();
						 if(strLine==""||strLine==null){
								countch=1;
							}
						 else countch=Integer.parseInt(strLine);
					System.out.print("from file countch is-->  "+countch+"\n");
				  
					 } catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							input.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				  
						
						File chc = new File("charactercount.txt");
						File sp = new File("spaceindex.txt");
						FileWriter fooWriter1 = null,fooWriter2=null;
						
						try {
							fooWriter1 = new FileWriter(chc, false);
							fooWriter2 = new FileWriter(sp, true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // true to append
						
						
						
				  //space code
		
		
		
		int i;
		IplImage img=cvLoadImage(imgName);
		IplImage reSizeLine=new IplImage();
		int minFound=0;
		int spacecount=0;
		if(img.height()<55) 
		{
			int width=(int) ((55.0/img.height())*img.width());
			if(width<1000)width=1000;
			System.out.println("----Width is -> "+width);
			Runtime r=Runtime.getRuntime();
			Process p=null;
			imgName=imgName.substring(2, imgName.length());
			//imgName="F:\\demoocrNoGUI\\"+imgName;
			System.out.println(imgName);
			
			//Imagemagick is called to resize the image which helps in segmenting it.
			try {
				String s="ImageMagick-6.7.9-Q8\\convert.exe convert "+imgName+" -resize "+width+"x55  output.png";
				p=r.exec(s);
				p.waitFor();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reSizeLine=cvLoadImage("output.png");
			
		}
		else
		{
			System.out.println("no resize");
			reSizeLine=img;
		}
	
		//cvEqualizeHist(reSizeLine, reSizeLine);
		cvSmooth( reSizeLine, reSizeLine, CV_GAUSSIAN, 3, 3, 0, 0 );
		cvThreshold(reSizeLine, reSizeLine,155,255,CV_THRESH_BINARY);
		
		cvSmooth( reSizeLine, reSizeLine, CV_BLUR, 2, 2, 0, 0 );
		
	
		CvMat data = new CvMat();
		CvRect aux=new CvRect();
		Integer xmin=new Integer(0);
		Integer xmax =new Integer(0);
		int prevmin=0;		
		CvScalar val=cvRealScalar(0);
		Integer count=1;
	
		
		//For each col sum, if sum > 0 then we find the min
		//then continue to end to search the max, if sum> 0 then is new max
		//space code
		
				int tr=1,x=0,counta = 0,indx=0,spcount=0;
				int []arr=new int[200];
				
				//space code

		for (i=0; i< reSizeLine.width(); i++)
		{
		cvGetCol(reSizeLine, data,i);
		val= cvSum(data);
		//System.out.println("value is :"+val);
		
		x=i;

		int pixel=(int) Math.abs(val.val(0)/255-reSizeLine.height());
		//System.out.println(val.val(0)/255-reSizeLine.height());
		//if(val.val(0) < (double)( reSizeLine.height())*255){
		if(pixel>=2){
		xmax= i;
		if(minFound==0){
		xmin= i;
		minFound= 1;
		}
					
		}
		else if(minFound==1 )
		{
			//space code
			while(tr==1){
				cvGetCol(reSizeLine, data,++x);
				val= cvSum(data);

				 pixel=(int) Math.abs(val.val(0)/255-reSizeLine.height());
			if(pixel<2)
			{
				counta++;
			 if(counta>20){         
				 arr[indx]=countch;
				 spcount++;
				 tr=0;
				 counta=0;
				 //countch++;
				 indx++;
				 System.out.println("\nspaceindex "+countch);
			 }
			}
			else{tr=0;}
			
			}
			//space code

			
			prevmin=xmax;
			minFound=0;
			
		
			aux=cvRect(xmin-1, 0, xmax-xmin+3, reSizeLine.height());			
			cvSetImageROI(reSizeLine, aux);
			IplImage chars = cvCreateImage(cvGetSize(reSizeLine),reSizeLine.depth(),reSizeLine.nChannels());
			cvCopy(reSizeLine, chars, null);
			cvSaveImage("line"+num+"/char"+count.toString()+".jpg", chars);
			
			cvResetImageROI(reSizeLine);
			String imgName1="line"+num+"\\char"+count.toString()+".jpg";
			
			//adding here
			StorageCompareExecute n = new StorageCompareExecute();
			try {
				n.findPixelSumValues(imgName1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			filenames+=","+imgName1;
			countchars++;
			count++;
			
			countch++;
			
			
			
			
		}
		else if(pixel <2){
			
			spacecount++;
			if(spacecount>28 && minFound==0 && countchars>0)
			{
				//System.out.println("here");
				spacecount=0;
				if(spaceSeq.indexOf(","+countchars)<0){
				spaceSeq+=","+countchars;
			}
			}
		}
		tr=1;

		}
		
		//space code
		
				for(int m=0;m<spcount;m++){
					try {
						strLine=Integer.toString(arr[m]);
						fooWriter2.append(strLine+"\n");
						System.out.println("\nwriting spaceindex to file "+strLine+"\n");
						//fooWriter.write(x);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}         // false to overwrite
					
				}
			/*
				try {
					strLine=Integer.toString(countch);
					fooWriter2.append("\ncount of characters"+strLine+"\n");
					System.out.println("\nwriting count of ch to spindex "+strLine+"\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				*/
				try {
					
					fooWriter2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// end of index
				
				/// writing character count
				strLine=Integer.toString(countch);
				try {
					fooWriter1.write(strLine);
					System.out.println("\nwriting count to file "+countch);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fooWriter1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/// end of character count
		
	//	spacesequnece+=","+countchars;
				
		return;
	}


	

//This method actually calculates the number of lines in the image and outputs them seperately which later each character and its
	//corresponding character count displayed.
	public String findLines(IplImage img) throws FileNotFoundException {
		
		IplImage clone=new IplImage();
		clone=img.clone();
		//cvEqualizeHist(img, img);
		 cvThreshold(img, img,145,255,CV_THRESH_BINARY);
		CvRect aux = new CvRect();
	
		Integer ymin = new Integer(0);
		Integer ymax = new Integer(0);
		int i;
		String strDirectoy = "line";
		Integer count = 1;
		int minFound = 0;
		CvMat data = new CvMat();
		CvScalar val = cvRealScalar(0);		
		
	
		for (i = 0; i < img.height(); i++) {
			cvGetRow(img, data, i);
			val = cvSum(data);
			
			// if(val.val(0) > maxVal.val(0)){
			if (val.val(0) < (double) img.width() * 255) {
				ymax = i;
				
				if (minFound == 0) {
					
					ymin = i;
					minFound = 1;
				}
			} else if (minFound == 1) {
				
				minFound = 0;
				strDirectoy = "line" + count.toString();
				aux = cvRect(0, ymin-2, img.width(), ymax - ymin+3);
			
				cvSetImageROI(clone, aux);
				IplImage line = cvCreateImage(cvGetSize(clone), clone.depth(),
						clone.nChannels());
				cvCopy(clone, line, null);
				new File(strDirectoy).mkdir();
				
			
				String lineName="./line" + count.toString() + ".jpg";
				cvSaveImage(lineName, line);
				
				 findChars(lineName,count.toString());
				cvResetImageROI(clone);
				count++;

			}
		}
		// System.out.println(max+min);
			
		return filenames;

	}
}
