import static com.googlecode.javacpp.Loader.sizeof;
import be.ac.ulg.montefiore.run.jadti.*;
import be.ac.ulg.montefiore.run.jadti.io.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.text.DecimalFormat;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import static com.googlecode.javacv.cpp.opencv_objdetect.*;



//This helps in recognition and post processing.
class lines{
	public static int count=0;
	
	public charac[] charsinLine;
	public void addnew(int xx,int yy)
	{
		charsinLine[count]=new charac();
		charsinLine[count].x=xx;
		charsinLine[count].y=yy;
		charsinLine[count].tex="";
		count++;
	}
	
	public void remove(int k)
	{
		
		for(int i=k;i<count-1;i++)
		{
			if(charsinLine[i+1].tex.indexOf("noise")<0){
			charsinLine[i].x=charsinLine[i+1].x;
			charsinLine[i].y=charsinLine[i+1].y;
			
			charsinLine[i].tex=charsinLine[i+1].tex;
			}
			else this.remove(i+1);
		}
		
		count--;
	}
	
	public lines()
	{
		charsinLine=new charac[1000];
	}
}
class charac
{
	public int linenum=0;
	public int x=0;
	public int y=0;
	public String tex;
	public charac(){
		tex=new String();
	}
}
public class test {

	public static lines l1=new lines();
	public static lines l2=new lines();
	/**
	 * @param args
	 */

	public Double[] humomDoc(IplImage img) {
		Double[] Momentfeature = new Double[18];
		CvRect rect = bBox(img);
		cvSetImageROI(img, rect);
		IplImage interBox = cvCreateImage(cvGetSize(img), img.depth(),
				img.nChannels());
		cvCopy(img, interBox, null);
		cvResetImageROI(img);
		
		IplImage skel = new skeleton().mainsub(interBox);
		cvNot(skel, skel);
		int onpix = 0;
		int offpix = 0;
		CvScalar pixValues = new CvScalar();
		for (int i = 0; i < interBox.width(); i++) {
			for (int j = 0; j < interBox.height(); j++) {
				pixValues = cvGet2D(interBox, j, i);
				if (pixValues.blue() == 0 && pixValues.green() == 0
						&& pixValues.red() == 0) {
					onpix++;
				} else {
					offpix++;
				}
			}
		}

		double donpix = onpix;
		double doffpix = offpix;
		double widht = interBox.width();
		double height = interBox.height();
		double pixratio = onpix / doffpix;

		double sizeration = widht / height;
		CvMoments moments = new CvMoments();
		// CvHuMoments humoments=new CvHuMoments();
		// First calculate object moments
		cvMoments(skel, moments, 0);
		// Now calculate hu moments
		// cvGetHuMoments(moments, humoments);

		Momentfeature[0] = moments.m00();
		Momentfeature[1] = moments.m01();
		Momentfeature[2] = moments.m02();
		Momentfeature[3] = moments.m03();
		Momentfeature[4] = moments.m10();
		Momentfeature[5] = moments.m11();
		Momentfeature[6] = moments.m20();
		Momentfeature[7] = moments.m21();
		Momentfeature[8] = moments.m30();
		Momentfeature[9] = moments.mu02();
		Momentfeature[10] = moments.mu03();
		Momentfeature[11] = moments.mu11();
		Momentfeature[12] = moments.mu12();
		Momentfeature[13] = moments.mu20();
		Momentfeature[14] = moments.mu21();
		Momentfeature[15] = moments.mu30();
		Momentfeature[16] = pixratio;
		Momentfeature[17] = sizeration;

		// for(int i=0;i<22;i++)System.out.println(Momentfeature[i].intValue());
		return Momentfeature;
		// 11,12,16,23

	}

	public int[] findX(IplImage img) {
		int[] x = new int[2];
		CvMat data = new CvMat();
		CvScalar val = cvRealScalar(0);
		int minfound = 0;
		for (int i = 0; i < img.width(); i++) {
			cvGetCol(img, data, i);
			val = cvSum(data);
			if (val.val(0) < (double) (img.height()) * 255) {
				x[1] = i;
				if (minfound == 0) {
					x[0] = i;
					minfound = 1;
				}

			}
		}

		return x;
	}

	public int[] findY(IplImage img) {
		int[] y = new int[2];
		CvMat data = new CvMat();
		CvScalar val = cvRealScalar(0);
		int minfound = 0;
		for (int i = 0; i < img.height(); i++) {
			cvGetRow(img, data, i);
			val = cvSum(data);
			if (val.val(0) < (double) (img.width()) * 255) {
				y[1] = i;
				if (minfound == 0) {
					y[0] = i;
					minfound = 1;
				}

			}
		}

		return y;
	}

	public CvRect bBox(IplImage img)// /not skeleton image
	{
		int[] y = findY(img);
		int[] x = findX(img);
		CvRect rect = new CvRect(x[0], y[0], x[1] - x[0], y[1] - y[0]);
		
		l1.addnew(x[0], y[0]);
		
		return rect;
	}

	public boolean isblack(IplImage img) {
		boolean color = false;
		int totpix = img.width() * img.height();
		CvScalar pixValues = new CvScalar();
		int blackWhitePix = 0;
		for (int i = 0; i < img.width(); i++) {
			for (int j = 0; j < img.height(); j++) {
				pixValues = cvGet2D(img, j, i);
				if ((pixValues.blue() < 30)) {
					blackWhitePix++;
				}

			}
		}

		double ratio = (double) blackWhitePix / (double) totpix;
		if (ratio * 100 < 0.9) {
			color = true;
		}
		return color;
	}

	public IplImage findContur(IplImage grayImage) {
		
		DataInputStream d = new DataInputStream(System.in);
		File xmlfile = null;
		
		RandomAccessFile access = null;
	
		try {
			xmlfile = new File("test.db.txt");
			access = new RandomAccessFile(xmlfile, "rw");
			access.writeBytes("word-database\nobject name charName symbolic mom1 numerical mom2 numerical mom3 numerical mom4 numerical mom5 numerical mom6 numerical mom7 numerical mom8 numerical mom9 numerical mom10 numerical mom11 numerical mom12 numerical mom13 numerical mom14 numerical mom15 numerical mom16 numerical pixratio numerical sizeratio numerical\n");
	
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Double[] feature = new Double[18];

		CvMemStorage mem;
		CvSeq contours = new CvSeq();
		CvSeq ptr = new CvSeq();

		mem = cvCreateMemStorage(0);

		cvFindContours(grayImage, mem, contours, sizeof(CvContour.class),
				CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
		double totArea=grayImage.width()*grayImage.height()*0.2;
		IplImage finall = cvCreateImage(cvGetSize(grayImage), 8, 1);
		cvZero(finall);

		for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
			CvRect r=cvBoundingRect(ptr, 1);
			
			if (r.width()>2 && r.height()>2 && ptr.total()>0 && cvContourPerimeter(ptr) > 12 && cvContourArea(ptr, CV_WHOLE_SEQ, 0)<totArea) {
				cvDrawContours(finall, ptr, CV_RGB(255, 255, 255),
						CV_RGB(0, 0, 0), -1, CV_FILLED, 8, cvPoint(0, 0));
			

				cvNot(finall, finall);
				feature = humomDoc(finall);
				String charName = new String();
				try {
					//charName=d.readLine();
					charName = "uk";
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					access.writeBytes("char" + charName + " " + charName + " ");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int j = 0; j < 18; j++) {
					try {
						String temp;
						int fet = feature[j].intValue();
						if (j == 16) {
							DecimalFormat myformat = new DecimalFormat(
									"0.00000");
							String temp1 = myformat.format(feature[j]);
							if(temp1.length()<7)temp1="0.00000";
							temp = temp1 + " ";
						} else if (j == 17)
							temp = feature[j] + "\n";
						else
							temp = fet + " ";
						access.writeBytes(temp);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				cvZero(finall);
			}

		}
		try {
			access.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finall;
	}

	public IplImage preprocess(IplImage img) {
		IplImage grayImage = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
		IplImage edge = cvCreateImage(cvSize(img.width(), img.height()),
				IPL_DEPTH_8U, 1);
		cvCvtColor(img, grayImage, CV_BGR2GRAY);
		cvSmooth(grayImage, grayImage, CV_BLUR, 3, 3, 0, 0);
		IplImage cedge = cvCreateImage(cvSize(img.width(), img.height()),
				IPL_DEPTH_8U, 3);
		cvNot(grayImage, edge);
		cvCanny(grayImage, edge, 185, 185 * 3, 3);
		cvDilate(edge, edge, null, 1);
		cvZero(cedge);
		cvSet(cedge, cvScalar(132, 132, 132, 0));

		cvCopy(img, cedge, edge);
		// cvShowImage("set", cedge);
		// cvWaitKey();

		cvThreshold(grayImage, grayImage, 150, 255, CV_THRESH_BINARY);
		cvSmooth(grayImage, grayImage, CV_BLUR, 3, 1, 0, 0);
		cvThreshold(grayImage, grayImage, 110, 255, CV_THRESH_BINARY);
		if (!isblack(cedge))
			cvNot(grayImage, grayImage);

		cvShowImage("edge", grayImage);
		cvWaitKey();
		
		return grayImage;
	}

	public IplImage harrdetect(IplImage img) {

		CvPoint pt1 = new CvPoint(), pt2 = new CvPoint();
		int scale = 1;
		IplImage image = img;
		IplImage imagecpy = image.clone();

		IplImage finall = cvCreateImage(cvGetSize(image), 8, 3);
		cvZero(finall);
		CvMemStorage storage = CvMemStorage.create();
		CvMemStorage storage1 = CvMemStorage.create();
		CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(
				cvLoad("WholeWord.xml"));

		CvSeq faces = cvHaarDetectObjects(image, classifier, storage, 1.2, 0,
				CV_HAAR_FEATURE_MAX);
		for (int i = 0; i < (faces != null ? faces.total() : 0); i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			pt1.x(r.x() * scale);
			pt2.x((r.x() + r.width()) * scale);
			pt1.y(r.y() * scale);
			pt2.y((r.y() + r.height()) * scale);
			cvSetImageROI(image, r);
			cvAdd(image, image, image, null);
			cvResetImageROI(image);

		}
		int minx = 0, maxx = 0, miny = 0, maxy = 0;
		for (int i = 0; i < image.height(); i++) {

			for (int j = 0; j < image.width(); j++) {
				CvScalar x = new CvScalar(cvGet2D(image, i, j));
				if (x.blue() == 255 && x.green() == 255 && x.red() == 255) {
					x = cvGet2D(imagecpy, i, j);
					cvSet2D(finall, i, j, x);
				}
			}
		}

		return finall;
	}
	
	public String recognize()
	{
		
		double avg=0;
		String text=new String();
		try {
			ItemSet testset = ItemSetReader.read(new FileReader("test.db.txt"));
			ItemSet noiseset= ItemSetReader.read(new FileReader("noise.db.txt"));
			ItemSet learningSet = ItemSetReader.read(new FileReader("photo.db.txt"));
			
			AttributeSet attributeSet1 = learningSet.attributeSet();
			
			Vector testAttributesVector1 = new Vector();
			testAttributesVector1.add(attributeSet1.findByName("mom1"));
			testAttributesVector1.add(attributeSet1.findByName("mom2"));
			testAttributesVector1.add(attributeSet1.findByName("mom3"));
			testAttributesVector1.add(attributeSet1.findByName("mom4"));
			testAttributesVector1.add(attributeSet1.findByName("mom5"));
			testAttributesVector1.add(attributeSet1.findByName("mom6"));
			testAttributesVector1.add(attributeSet1.findByName("mom7"));
			testAttributesVector1.add(attributeSet1.findByName("mom8"));
			testAttributesVector1.add(attributeSet1.findByName("mom9"));
			testAttributesVector1.add(attributeSet1.findByName("mom10"));
			testAttributesVector1.add(attributeSet1.findByName("mom11"));
			testAttributesVector1.add(attributeSet1.findByName("mom12"));
			testAttributesVector1.add(attributeSet1.findByName("mom13"));
			testAttributesVector1.add(attributeSet1.findByName("mom14"));
			testAttributesVector1.add(attributeSet1.findByName("mom15"));
			testAttributesVector1.add(attributeSet1.findByName("mom16"));
			testAttributesVector1.add(attributeSet1.findByName("pixratio"));
			testAttributesVector1.add(attributeSet1.findByName("sizeratio"));
		
			AttributeSet attributeSet = noiseset.attributeSet();			
			Vector testAttributesVector = new Vector();
			testAttributesVector.add(attributeSet.findByName("mom1"));
			testAttributesVector.add(attributeSet.findByName("mom2"));
			testAttributesVector.add(attributeSet.findByName("mom3"));
			testAttributesVector.add(attributeSet.findByName("mom4"));
			testAttributesVector.add(attributeSet.findByName("mom5"));
			testAttributesVector.add(attributeSet.findByName("mom6"));
			testAttributesVector.add(attributeSet.findByName("mom7"));
			testAttributesVector.add(attributeSet.findByName("mom8"));
			testAttributesVector.add(attributeSet.findByName("mom9"));
			testAttributesVector.add(attributeSet.findByName("mom10"));
			testAttributesVector.add(attributeSet.findByName("mom11"));
			testAttributesVector.add(attributeSet.findByName("mom12"));
			testAttributesVector.add(attributeSet.findByName("mom13"));
			testAttributesVector.add(attributeSet.findByName("mom14"));
			testAttributesVector.add(attributeSet.findByName("mom15"));
			testAttributesVector.add(attributeSet.findByName("mom16"));
			testAttributesVector.add(attributeSet.findByName("pixratio"));
			testAttributesVector.add(attributeSet.findByName("sizeratio"));
			
			AttributeSet testAttributes1 = new AttributeSet(testAttributesVector1);
			AttributeSet testAttributes = new AttributeSet(testAttributesVector);
			SymbolicAttribute goalAttribute1 =(SymbolicAttribute) learningSet.attributeSet().findByName("charName");
			SymbolicAttribute goalAttribute =(SymbolicAttribute) noiseset.attributeSet().findByName("charName");

			DecisionTree tree1 = buildTree(learningSet, testAttributes1, goalAttribute1);
			DecisionTree tree = buildTree(noiseset, testAttributes, goalAttribute);
			String temp=new String();
			int l2count=0;
			for(int i=0;i<lines.count;i++)
			{
				
				temp=printGuess(testset.item(i), tree);
				if(temp.indexOf("noise")<0)
				{			
				l2.charsinLine[l2count]=new charac();
					l2.charsinLine[l2count].x=l1.charsinLine[i].x;
					l2.charsinLine[l2count].y=l1.charsinLine[i].y;
					l2.charsinLine[l2count].tex=printGuess(testset.item(i), tree1);
					l2count++;
					
				}
				
				
			}
			
			lines.count=l2count;
			
			for(int i=0;i<lines.count;i++)
			{
				
					avg+=l2.charsinLine[i].y;
				
			}
			avg=avg/lines.count;
			double var=0;
			int tx=0;
			int ty=0;
			
			for(int i=lines.count-2;i>=0;i--)
			{
				for(int j=0;j<=i;j++)  {
					
					 if(l2.charsinLine[j].y>l2.charsinLine[j+1].y) {
						 tx=l2.charsinLine[j].x;
						 ty=l2.charsinLine[j].y;
						 temp=l2.charsinLine[j].tex;
						 
						 l2.charsinLine[j].x=l2.charsinLine[j+1].x;
						 l2.charsinLine[j].y=l2.charsinLine[j+1].y;
						 l2.charsinLine[j].tex=l2.charsinLine[j+1].tex;
						 
						 l2.charsinLine[j+1].x=tx;
						 l2.charsinLine[j+1].y=ty;
						 l2.charsinLine[j+1].tex=temp;
					 
					}
				}
			}
			
			for(int i=0;i<lines.count;i++)
			{
				
				var+=Math.pow(l2.charsinLine[i].y-avg, 2);
				
			}
			
			var=var/lines.count;
			double sd=Math.sqrt(var);
			sd=sd-0.18*sd;
			int linenum=1;
			l2.charsinLine[0].linenum=linenum;
			for(int i=1;i<lines.count;i++)
			{
				l2.charsinLine[i].linenum=linenum;
				
				if(Math.abs(l2.charsinLine[i-1].y-l2.charsinLine[i].y)>=sd)
				{	
				
					linenum++;
					
				}
				
			}
			int tline=0;
			for(int k=1;k<=linenum;k++){
			for(int i=lines.count-2;i>=0;i--)
			{
				for(int j=0;j<=i;j++)  {
					if(l2.charsinLine[j].linenum==k && l2.charsinLine[j+1].linenum==k )
					{
						if(l2.charsinLine[j].x>l2.charsinLine[j+1].x) {
							 tx=l2.charsinLine[j].x;
							 ty=l2.charsinLine[j].y;
							 temp=l2.charsinLine[j].tex;
							 tline=l2.charsinLine[j].linenum;
							 
							 l2.charsinLine[j].x=l2.charsinLine[j+1].x;
							 l2.charsinLine[j].y=l2.charsinLine[j+1].y;
							 l2.charsinLine[j].tex=l2.charsinLine[j+1].tex;
							 l2.charsinLine[j].linenum=l2.charsinLine[j+1].linenum;
							 
							 l2.charsinLine[j+1].x=tx;
							 l2.charsinLine[j+1].y=ty;
							 l2.charsinLine[j+1].tex=temp;
							 l2.charsinLine[j+1].linenum=tline;
						 }
					}
					
					
				}
			}
			}
			for(int j=1;j<=linenum;j++){
				for(int i=0;i<lines.count;i++)
				{
					if(l2.charsinLine[i].linenum==j){
					System.out.print(l2.charsinLine[i].tex);
					String temp2=new String();
					temp2=l2.charsinLine[i].tex;
					if(temp2.indexOf("NUM")>=0)
					{
						temp2=temp2.replace("NUM", "");
					}
					text+=temp2;
					}
				}
				text+="\n";
				System.out.println();
			}
		
			System.out.println("sd "+sd+"linenums "+linenum);
		} catch (FileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return text;
	}
	static private DecisionTree buildTree(ItemSet learningSet, 
			  AttributeSet testAttributes, 
			  SymbolicAttribute goalAttribute) {
DecisionTreeBuilder builder = 
new DecisionTreeBuilder(learningSet, testAttributes,
		    goalAttribute);

return builder.build().decisionTree();
}

	
	static private String printGuess(Item item, DecisionTree tree) {
		AttributeSet itemAttributes = tree.getAttributeSet();
		SymbolicAttribute goalAttribute = tree.getGoalAttribute();

		KnownSymbolicValue guessedGoalAttributeValue = 
		tree.guessGoalAttribute(item);

		String s = goalAttribute.valueToString(guessedGoalAttributeValue);

		return s;

		}
	public  String submain(IplImage image) throws FileNotFoundException {
		String text=new String();
		try{
				test t1=new test();
				IplImage imge = image;
			
				imge = t1.harrdetect(imge);
			
			//	imge=new photolocalize().preprocess(imge);
				t1.findContur(t1.preprocess(imge));
				
			 text= new test().recognize();
			
				
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		lines.count=lines.count=0;
		return text;

}
}


