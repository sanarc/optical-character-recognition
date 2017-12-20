//Curve.java author Vivek R
import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/*
 * 1-circle
 * 2-ellipse
 * 3-line
 * 4-arc
 * */


//Describes the kind of shape and curve.
class descriptor {
	public CvPoint center;
	public double angle;
	public int zone;
	public String shapeName;

	public descriptor() {
		center = new CvPoint();
		angle = 0;
		zone = 0;
		shapeName = new String();
	}
}

class feature {
	descriptor[] shapes;
	int count_contour;
	char charName;

	public feature() {
		count_contour = 0;
	}

	public void addShape(descriptor shape) {
		shapes[count_contour] = new descriptor();
		shapes[count_contour].angle = shape.angle;
		shapes[count_contour].center = shape.center;
		shapes[count_contour].zone = shape.zone;
		shapes[count_contour].shapeName = shape.shapeName;

		count_contour++;
	}

	//This method uses xml file to keep track of all vectors and count and names of chars.
	public boolean writeToFile(String filename) {
		boolean status = false;

		File xmlfile = new File(filename);
		boolean exists = xmlfile.exists();
		if (exists) {
			try {
				RandomAccessFile access = new RandomAccessFile(xmlfile, "rw");
				access.seek(xmlfile.length() - 20);
				access.writeBytes("\n<char>\n\t<char_name>" + charName
						+ "</char_name>\n");
				for (int i = 0; i < count_contour; i++) {
					access.writeBytes("\t<shape>\n");
					access.writeBytes("\t\t<name>" + shapes[i].shapeName
							+ "</name>\n");
					access.writeBytes("\t\t<angle>" + shapes[i].angle
							+ "</angle>\n");
					access.writeBytes("\t\t<center_x>" + shapes[i].center.x()
							+ "</center_x>\n");
					access.writeBytes("\t\t<center_y>" + shapes[i].center.y()
							+ "</center_y>\n");
					access.writeBytes("\t\t<zone>" + shapes[i].zone
							+ "</zone>\n");
					access.writeBytes("\t</shape>\n");
				}
				access.writeBytes("</char>\n</feature_vectors>");
				access.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		//This writes the individual shape name angle and etc.
		else {
			FileWriter fstream;
			try {
				fstream = new FileWriter(filename);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("<feature_vectors>\n<char>\n\t<char_name>" + charName
						+ "</char_name>\n");
				for (int i = 0; i < count_contour; i++) {
					out.write("\t<shape>\n");
					out.write("\t\t<name>" + shapes[i].shapeName + "</name>\n");
					out.write("\t\t<angle>" + shapes[i].angle + "</angle>\n");
					out.write("\t\t<center_x>" + shapes[i].center.x()
							+ "</center_x>\n");
					out.write("\t\t<center_y>" + shapes[i].center.y()
							+ "</center_y>\n");
					out.write("\t\t<zone>" + shapes[i].zone + "</zone>\n");
					out.write("\t</shape>\n");
				}
				out.write("</char>\n</feature_vectors>");
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return status;
	}

	
	//The features extracted and wriiten into file are then loaded into a tree based.
	public feature[] LoadFromXml(String filename) {
		feature[] trainData = null;
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(filename));

			// normalize text representation
			doc.getDocumentElement().normalize();
			// System.out.println ("Root element of the doc is "
			// +doc.getDocumentElement().getNodeName());

			NodeList listofChar = doc.getElementsByTagName("char");
			int totalChars = listofChar.getLength();

			trainData = new feature[totalChars];

			for (int i = 0; i < totalChars; i++) {
				trainData[i] = new feature();

				Node character = listofChar.item(i);
				if (character.getNodeType() == Node.ELEMENT_NODE) {
					Element charElement = (Element) character;

					NodeList charNameList = charElement
							.getElementsByTagName("char_name");
					NodeList charName = charNameList.item(0).getChildNodes();
					// System.out.println("char name"+charName.item(0).getNodeValue().trim());
					trainData[i].charName = charName.item(0).getNodeValue()
							.charAt(0);

					descriptor tempshape = new descriptor();
					NodeList ShapeList = charElement
							.getElementsByTagName("shape");
					int shapecount = ShapeList.getLength();
					// System.out.println("shapecount "+shapecount);
					trainData[i].shapes = new descriptor[shapecount];
					for (int j = 0; j < shapecount; j++) {
						Node shape = ShapeList.item(j);

						Element shapeElement = (Element) shape;
						tempshape.shapeName = shapeElement
								.getElementsByTagName("name").item(0)
								.getChildNodes().item(0).getNodeValue();
						tempshape.angle = Double.parseDouble(shapeElement
								.getElementsByTagName("angle").item(0)
								.getChildNodes().item(0).getNodeValue());
						tempshape.center.x(Integer.parseInt(shapeElement
								.getElementsByTagName("center_x").item(0)
								.getChildNodes().item(0).getNodeValue()));
						tempshape.center.y(Integer.parseInt(shapeElement
								.getElementsByTagName("center_y").item(0)
								.getChildNodes().item(0).getNodeValue()));
						tempshape.zone = Integer.parseInt(shapeElement
								.getElementsByTagName("zone").item(0)
								.getChildNodes().item(0).getNodeValue());
						trainData[i].addShape(tempshape);
					}

				}
			}

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		return trainData;
	}

}

// point class to store the x and y value of a point
// point.count holds the number of points read from file
class point {
	public float x;
	public float y;

	point() {
		x = 0;
		y = 0;
		// count++;
	}

}

class arrayPoint {
	public point[] Pointarry = new point[400];
	int NumOfPoint;

	public arrayPoint(int num) {
		NumOfPoint = num;
		for (int i = 0; i < NumOfPoint; i++) {
			Pointarry[i] = new point();
		}
	}

	public void copyallpoints(arrayPoint p) {
		if (p.NumOfPoint <= NumOfPoint) {
			for (int i = 0; i < p.NumOfPoint; i++) {
				Pointarry[i].x = p.Pointarry[i].x;
				Pointarry[i].y = p.Pointarry[i].y;
			}

			NumOfPoint = p.NumOfPoint;
		} else {
			System.out
					.println("src point count does not match destination point count");
		}
	}

	public void copyPoint(point p) {
		Pointarry[NumOfPoint] = new point();
		Pointarry[NumOfPoint].x = p.x;
		Pointarry[NumOfPoint].y = p.y;
		NumOfPoint++;
	}

	public void deletelast() {
		NumOfPoint--;

	}
}

//This class calculates the very minute details of the character or image.
//Calculaters the types of curves present and calculates the corresponding X And Y and also their sum,product to get the desired.
public class Curve {
	// reads points from console and stores in the

	int degree = 0;

	public float Round(float Rval, int Rpl) {
		float p = (float) Math.pow(10, Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return tmp / p;
	}

	public double angle(CvPoint center, CvPoint p1, CvPoint p2) {
		double theta = 0;
		double a = Math.sqrt(Math.pow(center.x() - p1.x(), 2)
				+ Math.pow(center.y() - p1.y(), 2));
		double b = Math.sqrt(Math.pow(center.x() - p2.x(), 2)
				+ Math.pow(center.y() - p2.y(), 2));
		theta = Math.acos(a / b);
		return theta;
	}

	public int zones(int x, int y, int height, int width) {
		int zone = -1;
		int partwidth = width / 3;
		int partheight = height / 3;
		if (x >= 0 * partwidth && x <= 1 * partwidth && y >= 0 * partheight
				&& y <= 1 * partheight) {
			zone = 1;
		} else if (x >= 1 * partwidth && x <= 2 * partwidth
				&& y >= 0 * partheight && y <= 1 * partheight) {
			zone = 2;
		} else if (x >= 2 * partwidth && x <= 3 * partwidth
				&& y >= 0 * partheight && y <= 1 * partheight) {
			zone = 3;
		} else if (x >= 0 * partwidth && x <= 1 * partwidth
				&& y >= 1 * partheight && y <= 2 * partheight) {
			zone = 4;
		} else if (x >= 1 * partwidth && x <= 2 * partwidth
				&& y >= 1 * partheight && y <= 2 * partheight) {
			zone = 5;
		} else if (x >= 2 * partwidth && x <= 3 * partwidth
				&& y >= 1 * partheight && y <= 2 * partheight) {
			zone = 6;
		} else if (x >= 0 * partwidth && x <= 1 * partwidth
				&& y >= 2 * partheight && y <= 2 * partheight) {
			zone = 7;
		} else if (x >= 1 * partwidth && x <= 2 * partwidth
				&& y >= 2 * partheight && y <= 3 * partheight) {
			zone = 8;
		} else if (x >= 2 * partwidth && x <= 3 * partwidth
				&& y >= 2 * partheight && y <= 3 * partheight) {
			zone = 9;
		}

		return zone;
	}

	public descriptor shape_extractor(CvSeq contur, int width, int height) {
		IplImage res = cvCreateImage(cvSize(width, height), 8, 1);
		IplImage image = cvCreateImage(cvSize(width, height), 8, 3);
		cvZero(res);
		cvZero(image);
		cvDrawContours(res, contur, CV_RGB(255, 255, 255), CV_RGB(0, 0, 0), -1,
				CV_FILLED, 8, cvPoint(0, 0));
		descriptor shape = new descriptor();
		CvBox2D box;
		int count = (int) cvContourPerimeter(contur); // This is number point in
														// contour
		CvPoint center = new CvPoint();
		CvSize size = new CvSize();

		box = new CvBox2D(count);
		box = cvFitEllipse2(contur);
		center.x((int) box.center().x());
		center.y((int) box.center().y());
		// System.out.println("zone"+" "+zones(center.x(),center.y(),height,width));
		size.width((int) (box.size().width() * 0.5));
		size.height((int) (box.size().height() * 0.5));
		box.angle(-box.angle());

		double perimetr = 2
				* Math.PI
				* Math.sqrt((Math.pow(size.width(), 2) + Math.pow(
						size.height(), 2)) / 2);
		System.out.println(count + " " + perimetr);

		if (size.height() >= 6 && size.width() >= 6)// validation of ellipse
		{
			if (Math.abs(count / perimetr * 100 - 100) <= 10) {
				// complete ellipse
				if (size.height() == size.width()) {
					// circle
					// System.out.println("circle "+size.height()+" "+size.width());
					// System.out.println(count+" "+perimetr);
					cvEllipse(image, center, size, box.angle(), 0, 360,
							CV_RGB(255, 0, 0), 1, CV_AA, 0);
					shape.angle = box.angle();
					shape.center = center;
					shape.zone = zones(center.x(), center.y(), height, width);
					shape.shapeName = "circle";

				} else {
					cvEllipse(image, center, size, box.angle(), 0, 360,
							CV_RGB(255, 0, 0), 1, CV_AA, 0);
					shape.angle = box.angle();
					shape.center = center;
					shape.zone = zones(center.x(), center.y(), height, width);
					shape.shapeName = "ellipse";
				}
			}

			else if (Math.abs(count / perimetr * 100 - 100) <= 60) {
				// arc
				cvEllipse(image, center, size, box.angle(), 0, 360,
						CV_RGB(255, 0, 0), 1, CV_AA, 0);
				CvPoint p1 = new CvPoint(contur).position(0);
				CvPoint p2 = new CvPoint(contur).position(contur.total() - 1);
				shape.angle = angle(center, p1, p2);
				CvPoint centemp = new CvPoint(contur)
						.position(contur.total() / 2);
				shape.center = centemp;
				shape.zone = zones(shape.center.x(), shape.center.y(), height,
						width);
				shape.shapeName = "arc";
			}

			else {
				System.out.println("Unknown");
			}
		} else {
			CvMemStorage storage = cvCreateMemStorage(0);
			CvSeq lines = new CvSeq();
			lines = cvHoughLines2(res, storage, CV_HOUGH_PROBABILISTIC, 1,
					Math.PI / 180, 7, 5, 5);
			double angle = 0;

			System.out.println("Lines " + lines.total());
			if(lines.total()>0){
			Pointer line = cvGetSeqElem(lines, 0);
			CvPoint pt1 = new CvPoint(line).position(0);
			CvPoint pt2 = new CvPoint(line).position(1);
			//angle = Math.atan2(pt2.y() - pt1.y(), pt2.x() - pt1.x());
			// angle/=nb_lines*22/7 ;
			angle *= 180;
			CvPoint pt3 = new CvPoint();
			pt3.x((pt1.x() + pt2.x()) / 2);
			pt3.y((pt1.y() + pt2.y()) / 2);
			shape.angle = angle;
			shape.center = pt3;
			shape.zone = zones(pt3.x(), pt3.y(), height, width);
			shape.shapeName = "line";

			System.out.println("line");
			cvLine(image, pt1, pt2, CV_RGB(255, 0, 0), 1, CV_AA, 0);
			}
		}
		cvShowImage("contur", image);
		cvWaitKey();

		return shape;
	}

	public arrayPoint drwgrid(CvSeq contur, int width, int height) {
		IplImage res = cvCreateImage(cvSize(width, height), 8, 1);
		cvZero(res);
		cvDrawContours(res, contur, CV_RGB(255, 255, 255), CV_RGB(0, 0, 0), -1,
				CV_FILLED, 8, cvPoint(0, 0));
		cvNot(res, res);
		cvShowImage("contur", res);
		cvWaitKey();
		arrayPoint p = new arrayPoint(400);
		int count = 0;
		IplImage matrix = cvCreateImage(cvSize(64, 64), 8, 1);
		cvZero(matrix);
		float min = 100;
		for (int i = 0; i < res.width(); i++) {
			for (int j = 0; j < res.height(); j++) {
				CvScalar x = new CvScalar();
				x = cvGet2D(res, j, i);
				if (x.blue() == 0 && x.green() == 0 && x.red() == 0) {

					p.Pointarry[count].x = i;
					p.Pointarry[count].y = j;
					if (p.Pointarry[count].x < min)
						min = p.Pointarry[count].x;
					if (p.Pointarry[count].y < min)
						min = p.Pointarry[count].y;
					// System.out.println(p.Pointarry[count].x+" "+p.Pointarry[count].y);
					count++;
					// break;
				}
			}
		}

		/*
		 * int intmin=(int) (min/10); intmin=intmin*10; //
		 * System.out.println("Minimum is"+intmin+" "+min); for(int
		 * i=0;i<count;i++) { p.Pointarry[i].x=p.Pointarry[i].x-intmin;
		 * p.Pointarry[i].y=p.Pointarry[i].y-intmin;
		 * //System.out.println(p.Pointarry[i].x+" "+p.Pointarry[i].y); }
		 */
		p.NumOfPoint = count;
		// System.out.println(count);

		// cvSaveImage("grid.jpg", matrix);
		cvWaitKey();

		return p;

	}

	public arrayPoint readPointsFromconsole() {
		arrayPoint p = new arrayPoint(25);
		DataInputStream d = new DataInputStream(System.in);
		try {
			System.out.println("Enter the number of points");
			int count = Integer.parseInt(d.readLine());
			p.NumOfPoint = count;
			System.out.println("Enter the poin values");
			for (int i = 0; i < count; i++) {
				p.Pointarry[i] = new point();
				p.Pointarry[i].x = Float.parseFloat(d.readLine());
				p.Pointarry[i].y = Float.parseFloat(d.readLine());
			}

			for (int i = 0; i < count; i++) {
				System.out.println(p.Pointarry[i].x + " " + p.Pointarry[i].y);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return p;

	}

	/*
	 * rotate function is used to rotate the stroke; if slope of is
	 * uncomputable, then rotate is used to make it computable
	 */

	public arrayPoint rotate(int rotateAngle, arrayPoint p) {
		int dis = 0;
		for (int i = 1; i < p.NumOfPoint; i++) {
			dis = 0;
			dis = (int) Math.sqrt(Math.pow(
					(p.Pointarry[0].x - p.Pointarry[i].x), 2)
					+ Math.pow((p.Pointarry[0].y - p.Pointarry[i].y), 2));
			p.Pointarry[i].x = Round(
					(float) ((p.Pointarry[0].x + dis) / (Math.sqrt(1 + Math
							.tan(Math.toRadians(rotateAngle))))), 2);
			p.Pointarry[i].y = Round(
					(float) ((p.Pointarry[0].y + dis) / (Math.sqrt(1 + Math
							.tan(Math.toRadians(rotateAngle))))), 2);

			// System.out.println(p.Pointarry[i].x+" "+p.Pointarry[i].y);
		}
		return p;

	}

	public boolean findslope(arrayPoint p) {
		if (Math.abs(p.Pointarry[0].y - p.Pointarry[p.NumOfPoint / 2].y) > 0
				&& Math.abs(p.Pointarry[0].x - p.Pointarry[p.NumOfPoint / 2].x) > 0
				&& Math.abs(p.Pointarry[p.NumOfPoint / 2].x
						- p.Pointarry[p.NumOfPoint - 1].x) > 0
				&& Math.abs(p.Pointarry[p.NumOfPoint / 2].y
						- p.Pointarry[p.NumOfPoint - 1].y) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public IplImage corners(IplImage image) {
		IplImage imgGrayscale = cvCreateImage(cvGetSize(image), 8, 1);
		IplImage skel = cvCreateImage(cvGetSize(image), 8, 1);
		skeleton ss = new skeleton();
		cvThreshold(skel, skel, 128, 255, CV_THRESH_BINARY);
		skel = ss.mainsub(image);
		imgGrayscale = skel;
		IplImage imgEigen = cvCreateImage(cvGetSize(image), 32, 1);
		IplImage finall = cvCreateImage(cvGetSize(image), 8, 1);

		cvCornerMinEigenVal(imgGrayscale, imgEigen, 4, 3);
		cvConvertScale(imgEigen, finall, CV_32FC1, CV_8SC1);
		//cvErode(skel, skel, null, 1);
		
		CvScalar s = new CvScalar();

		for (int i = 0; i < finall.height(); i++) {
			for (int j = 0; j < finall.width(); j++) {
				s = cvGet2D(finall, i, j);
				if (s.blue() > 1) {

					s.blue(255);
					s.green(255);
					s.red(255);
					cvSet2D(skel, i, j, s);
				}
			}
		}
		//
		
		cvNamedWindow("Original");
		cvShowImage("Original", skel);
		cvWaitKey();

		cvNot(skel, skel);

		CvMemStorage mem;
		CvSeq contours = new CvSeq();

		mem = cvCreateMemStorage(0);

		cvFindContours(skel, mem, contours, sizeof(CvContour.class),
				CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, cvPoint(0, 0));
		int count = 0;
		feature character = new feature();
		character.shapes = new descriptor[10];
		character.charName = 'A';
		for (; contours != null; contours = contours.h_next()) {

			if (contours.total() > 6) {

				// arrayPoint p=drwgrid(ptr, skel.width(), skel.height());
				character.addShape(shape_extractor(contours, skel.width(),
						skel.height()));
				count++;
			}
			// break;
		}
		System.out.print(count);
		character.writeToFile("F:\\sampl.xml");
		return skel;

	}

	public float calcY(float[][] A, float x) {
		float y = 0;

		for (int i = degree - 1; i >= 0; i--) {
			y = (float) (y + (A[i][0] * Math.pow(x, i)));
		}

		return y;
	}

	public float err(arrayPoint p, float[][] A) {
		float err = 0;
		for (int i = 0; i < p.NumOfPoint; i++) {
			// err+=Math.sqrt(Math.pow((p.Pointarry[i].y-calcY(A,
			// p.Pointarry[i].x)), 2));
			System.out.println(p.Pointarry[i].x + " "
					+ calcY(A, p.Pointarry[i].x));
		}
		return err;
	}

	public float sumX(arrayPoint p, int pow) {
		float sum = 0;
		for (int i = 0; i < p.NumOfPoint; i++) {
			sum += Math.pow(p.Pointarry[i].x, pow);
		}
		return sum;
	}

	public float sumXY(arrayPoint p, int pow) {
		float sum = 0;
		for (int i = 0; i < p.NumOfPoint; i++) {
			sum += (Math.pow(p.Pointarry[i].x, pow) * p.Pointarry[i].y);
		}
		return sum;
	}

	public float detrm(float a[][], float k) {
		float s = 1, det = 0;
		float[][] b = new float[25][25];
		int i, j, m, n, c;

		if (k == 1) {
			return (a[0][0]);
		} else {
			det = 0;

			for (c = 0; c < k; c++) {
				m = 0;
				n = 0;

				for (i = 0; i < k; i++) {
					for (j = 0; j < k; j++) {
						b[i][j] = 0;

						if (i != 0 && j != c) {
							b[m][n] = a[i][j];

							if (n < (k - 2))
								n++;
							else {
								n = 0;
								m++;
							}
						}
					}
				}

				det = det + s * (a[0][c] * detrm(b, k - 1));
				s = -1 * s;
			}
		}

		return det;
	}

	public float[][] matrixMul(float a[][], float[] b, int degree) {

		float[][] ansArr = new float[degree][1];
		for (int i = 0; i < degree; i++) {
			for (int j = 0; j < 1; j++) {
				ansArr[i][0] = 0;
			}

		}
		for (int i = 0; i < degree; i++) {
			for (int j = 0; j < 1; j++) {
				for (int k = 0; k < degree; k++) {

					ansArr[i][0] += a[i][k] * b[k];
				}
			}
		}

		return ansArr;
	}

	public float[][] cofact(float num[][], float f) {
		float[][] b = new float[25][25], fac = new float[25][25];
		int p, q, m, n, i, j;

		for (q = 0; q < f; q++) {
			for (p = 0; p < f; p++) {
				m = 0;
				n = 0;

				for (i = 0; i < f; i++) {
					for (j = 0; j < f; j++) {
						b[i][j] = 0;

						if (i != q && j != p) {
							b[m][n] = num[i][j];

							if (n < (f - 2))
								n++;
							else {
								n = 0;
								m++;
							}
						}
					}
				}

				fac[q][p] = (float) (Math.pow(-1, q + p) * detrm(b, f - 1));
			}
		}

		return trans(num, fac, f);
	}

	public float[][] trans(float num[][], float fac[][], float r)

	{
		int i, j = 0;
		float[][] b = new float[25][25], inv = new float[25][25];
		float d;

		for (i = 0; i < r; i++) {
			for (j = 0; j < r; j++) {
				b[i][j] = fac[j][i];
			}
		}

		d = detrm(num, r);
		inv[i][j] = 0;

		for (i = 0; i < r; i++) {
			for (j = 0; j < r; j++) {
				inv[i][j] = b[i][j] / d;
			}
		}

		return inv;
	}

	public float[][] curvefit(arrayPoint p) {

		float[][] A = new float[degree][1];
		float[][] X = new float[degree][degree];
		float[] B = new float[degree];
		for (int i = 0; i < degree; i++) {
			B[i] = new Float(sumXY(p, i));
			for (int j = 0; j < degree; j++) {
				X[i][j] = new Float(sumX(p, i + j));
			}
		}

		float d = detrm(X, degree);
		if (d != 0) {
			X = cofact(X, degree);
			A = matrixMul(X, B, degree);
			for (int i = 0; i < degree; i++) {
				A[i][0] = Round(A[i][0], 2);
				// System.out.println(A[i][0]);

			}
			return A;
		}
		System.out.println("Determent is zero");
		return null;

	}
}