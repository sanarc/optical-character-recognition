import be.ac.ulg.montefiore.run.jadti.*;
import be.ac.ulg.montefiore.run.jadti.io.*;
import java.io.*;
import java.util.*;


//this is where we are facing problem now.
//The moments are not being built in a tree and  appropriate decision are being taken.
public class tree_builder {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileFormatException 
	 */
	public static String mainsub(int count,boolean photo,boolean scan,String[] ss) throws FileFormatException, IOException {
		
		

		 String dbFileName = "screen.db.txt";
		  String test = "test.db.txt";
		 
		 ItemSet learningSet = null;
		 ItemSet testset = null;
		 if(photo)dbFileName="photo.db.txt";
		 else if(scan)dbFileName="scan.db.txt";
			try {
				
				
			    learningSet = ItemSetReader.read(new FileReader(dbFileName));
			    System.out.println("File opened successfully"+"\n");
			    testset = ItemSetReader.read(new FileReader(test));
			    System.out.println("File opened successfully"+"\n");
			}
			catch(FileNotFoundException e) {
			    System.err.println("File not found : " + dbFileName + ".");
			    System.err.println("This file is included in the source " +
					       "distribution of OCR.  You can find it at OCR/resources");
			    System.exit(-1);
			}
			
			AttributeSet attributeSet = learningSet.attributeSet();
			
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
		
			
			
			
			AttributeSet testAttributes = new AttributeSet(testAttributesVector);
			SymbolicAttribute goalAttribute =(SymbolicAttribute) learningSet.attributeSet().findByName("charName");

			DecisionTree tree = buildTree(learningSet, testAttributes,
						      goalAttribute);
			
			//space code
			File chc1 = new File("spaceindex.txt");
			 BufferedReader input =  new BufferedReader(new FileReader(chc1));
			String strLine=null;
			int countch=0,cnt=0,x=0;
			int []arr=new int[1000];
			try {
				while( (strLine = input.readLine())!=null){
					 countch=Integer.parseInt(strLine);	
					 arr[cnt]=countch;
					 System.out.print(" arrr is-->  "+arr[cnt]+"\n");
					 cnt++;
				}
				 
				
			
				
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
			
			//space code

			
			//printDot(tree);
			String text=new String();
			//int x=1;
			//int temp=0;
			for(int i=0;i<count;i++){
				///temp=Integer.parseInt(ss[x]);
				if(i==arr[x])
				{
					x++;
					text+=" ";
				}
			text+=printGuess(testset.item(i), tree);
			}
			File f1 = new File("spaceindex.txt");
			File f2 = new File("charactercount.txt");
			  @SuppressWarnings("unused")
			boolean success1 = f1.delete();
			  @SuppressWarnings("unused")
			boolean success2 = f2.delete();

		
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


/*
* Prints a dot file content depicting a tree.
*/
static private void printDot(DecisionTree tree) {
System.out.println((new DecisionTreeToDot(tree)).produce());
}


/*
* Prints an item's guessed goal attribute value.
*/
static private String printGuess(Item item, DecisionTree tree) {
AttributeSet itemAttributes = tree.getAttributeSet();
SymbolicAttribute goalAttribute = tree.getGoalAttribute();

KnownSymbolicValue guessedGoalAttributeValue = 
tree.guessGoalAttribute(item);

String s = goalAttribute.valueToString(guessedGoalAttributeValue);

return s;

}

}
