import static com.googlecode.javacv.cpp.opencv_core.cvRealScalar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;


public class StorageCompareExecute 
{
	static int countcounter = 0;

	/*public static void main(String[] args) 
	{
		//getConnection();
		//showimage();
		
	}*/
	
	
	
	static Connection getConnection()
	{
		Connection conn = null;
		//class and conn are two seperate so it nedds seperate execptions but just try using generic
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/naveen","root","root");
			
			//JOptionPane.showMessageDialog(null,"Connection Done");
			System.out.println("connection done");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public void findPixelSumValues(String image) throws Exception
	{
		IplImage imagge=cvLoadImage(image);
		CvScalar row = new CvScalar();
		CvScalar column = new CvScalar();
		
		
	
		int i;
		CvMat data = new CvMat();
		CvScalar val=cvRealScalar(0);
		
		//cvScalar valval1
		
		CvScalar val2 = cvRealScalar(0);
		
		//getConnection();
		
		for (i=0; i< imagge.width() ;  i++)
		{
			cvGetCol(imagge, data, i);
			val= cvSum(data);
			
		}
		
		row = val;
		System.out.println("######Row value:"+row.getVal(0));
		System.out.println("######Row value:"+row.getVal(1));
		System.out.println("######Row value:"+row.getVal(2));
		
		
		
		for (i=0;i<imagge.height();i++)
		{
			cvGetRow(imagge, data, i);
			val2 = cvSum(data);
		}
		column = val2;
		System.out.println("******Column size:"+column.getVal(0));
		System.out.println("******Column size:"+column.getVal(1));
		System.out.println("******Column size:"+column.getVal(2));
		
		//getConnection();
		System.out.println("This was character:"+countcounter);
		//insertTable(row,column,countcounter);
		//searchcharacter(row, column);
		countcounter++;
		return;
	}
	
	static void insertTable(CvScalar row,CvScalar column,int count)
	{
		
		//instance of the connection so as to connect to database
		Connection conn;
		//this will get connected with db
		conn = getConnection();
		//executing query we need statement
		Statement st;
		//writing the query
		try 
		{
			st = conn.createStatement();
			st.execute("insert into ocr values ('"+row.getVal(0)+"','"+row.getVal(1)+"','"+row.getVal(2)+"','"+column.getVal(0)+"','"+column.getVal(1)+"','"+column.getVal(2)+"','"+count+"')");
			st.close(); 
			conn.close();
			
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void searchcharacter(CvScalar r,CvScalar c)
	{
		Connection conn;
		conn = getConnection();
		Statement st;
		ResultSet rs;

		//writing the query
		try 
		{
			
			
			st = conn.createStatement();
			System.out.println("The character is:");
			rs = st.executeQuery("select charactername from charname where rowval1 = '"+r.getVal(0)+"';");
			//String name = rs;
			//rs = st.executeQuery("select charactername from charname;");
			int cols = rs.getMetaData().getColumnCount();
			int ro = 0,co = 0;
			while(rs.next())
			{
				//same like array and it starts from 1
				for (int i = 1;i <= cols ; i++)
				{
					System.out.print(rs.getObject(i)+"\t");
					co++;
				}
				ro++; co=0;
				System.out.println();
			}

			st.close(); 
			conn.close();
			
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
